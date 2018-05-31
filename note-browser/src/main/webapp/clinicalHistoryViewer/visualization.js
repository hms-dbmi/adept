define(["common/session", "common/searchParser", "text!clinicalHistoryViewer/viewer.hbs", "clinicalHistoryViewer/candidatePanel", "common/synonymMapper", "jquery", "underscore", "backbone", "handlebars", "vis", "clinicalHistoryViewer/colors", "clinicalHistoryViewer/timeline", "clinicalHistoryViewer/connections", "bootstrap"],
		function(session, parseQueryString, template, candidatePanel, synMapper, $, _, BB, HBS, vis, cuiColors, timeline, connections){

	var visualizationView = BB.View.extend({
		tagName : "div",

		className: "patient-history-visualization",

		events: {

		},

		initialize: function(opts){
			this.router = opts.router;
			this.patientId = opts.patientId;
			this.queryString = opts.queryString;
		},

		render: function(){

		},

		currentDocument: null,

		candidatePanelView: null,


	});

	var currentDocument = undefined;

	var candidatePanelView;

	var patientHistoryVisualization = {
			showPatientHistory : function(router, patientId, queryString){
				var selectedRelations = [];
				if(queryString){
					var candidateList = parseQueryString().candidate.split(",")

					selectedRelations = _.map(candidateList, function(candidate){
						return {drugCui : candidate.split("_")[0], symptomCui : candidate.split("_")[1]};
					});					
				}

				$("#app").html(template);
				$('.triggers-tooltip').tooltip();

				var relations = [];

				$(".candidate-adverse-drug-reaction").focus(function(event){
					$(this).blur();
				});

				var configuredConnections = null;

				var documentCache = {};

				var loadDocument = function(documentId){
					var renderDocument = function(data){
						scrollTo(0,0);
						var groupIds = _.pluck(data.groups, "groupId");

						$('#text').html(atob(data.markup));

						synMapper.mapSynonymClasses();

						$('canvas')[0].style.left = ($('#text')[0].getBoundingClientRect().left-8);
						$('canvas')[0].style.top = ($('#text')[0].getBoundingClientRect().top);
						$('canvas')[0].width = $('#text')[0].getBoundingClientRect().width+8;
						$('canvas')[0].height = $('#text')[0].getBoundingClientRect().height+8;

						configuredConnections = connections.setupConnections();
						configuredConnections.drawConnections(groupIds);
					}
					if(documentCache[documentId]){
						renderDocument(documentCache[documentId]);
					}else{
						$.ajax({
							url: '/rest/document/' + documentId,
							type: 'GET',
							success: function(data, status, jqXHR){
								renderDocument(data);
								documentCache[documentId] = data;
							}.bind(this),
							error: function(jqXHR, status, error){
								switch(jqXHR.status){
								case 403:
									alert('You have exceeded the number of notes you can view in a short period of time. If this happens too many times you will have to have an admin unlock your account before you can view any more notes.');
									break;
								case 404:
									alert('This note seems to be missing. Please contact an administrator.')
									break;
								default:
									alert('An unknown error has occurred. Please contact an administrator.')
									break;
								}
							}
						});

					}
				}

				window.addEventListener("resize", function(){
					loadDocument(currentDocument);
				});

				window.addEventListener("scroll", function(){
					if(configuredConnections){
						configuredConnections.registerConnectionHandler();						
					}
				});

				var patientNotes;


				$.get("/rest/patientHistory/" + patientId,
						function(data, status, jqXHR){
					var synRelations = {};
					_.each(data.relations, function(relation){
						var synDrugCui = relation.drugCui;
						if(relation.drugCui != synDrugCui){
							relation.drugCui = synDrugCui;
							relation.drug = synMapper.nameForDrugCui(synDrugCui, relation.drug);
						}
						synRelations[relation.drugCui + "_" + relation.eventCui] = relation;
					});
					patientNotes = data.patientNotes;
					var entries = [];
					_.each(_.values(synRelations), function(relation, key){
						relations.push(relation);
					});
					relations = _.sortBy(relations, function(relation){
						return relation.drug;
					});

					var selectedRelations = [];
					var candidateList = parseQueryString().candidate.split(",")
					candidatePanelView = new candidatePanel({
						patientId : patientId,
						relations : relations,
						candidateValidations : data.candidateValidations,
						callback: drawSelectedCandidates
					});
					candidatePanelView.setElement($('#accordion'));
					candidatePanelView.render();

					cuiColors(relations);

					firstNoteWithSelectedCuis = _.find(patientNotes, function(note){
						return _.find(note.candidateIds, candidateList[0]);
					});
					if(!firstNoteWithSelectedCuis){
						console.log("unable to find note for selected candidate in patient history");
						firstNoteWithSelectedCuis = patientNotes[0];
					}
					currentDocument = firstNoteWithSelectedCuis.noteId;

					drawSelectedCandidates();

					loadDocument(currentDocument);
				}.bind({drawSelectedCandidates : drawSelectedCandidates})
				);
				var drawSelectedCandidates = function(){
					_.each(relations, function(relation){
						relation.drug = synMapper.nameForDrugCui(relation.drugCui);
					});
					selectedRelations = candidatePanelView.selectedRelations();

					var relationCuis = {
							drugCuis: _.uniq(_.pluck(selectedRelations, 'drugCui')),
							eventCuis: _.uniq(_.pluck(selectedRelations, 'symptomCui'))
					};

					if(selectedRelations.length > 0){
						timeline(patientNotes, selectedRelations, relationCuis, currentDocument, loadDocument);
					}

				};
			}	
	};
	return patientHistoryVisualization;
}
);
