define(["clinicalHistoryViewer/usermodeFilters", "common/session", "common/searchParser", "backbone", "handlebars", "jquery", "underscore", "common/synonymMapper", "text!clinicalHistoryViewer/candidateQuestions.hbs"], 
		function(usermodeFilters, session, parseQueryString, BB, HBS, $, _, synMapper, candidateQuestions){
	var candidatePanelView = BB.View.extend({
		
		candidateQuestions: HBS.compile(candidateQuestions),
		
		tagName: "div",

		className: "candidate-adverse-reaction-list",

		events: {
			"click .history-validate-confirm":          "validationConfirmed",
			"click .history-adjudicate-confirm":   "adjudicationConfirmed",
			"click .candidate-checkbox": "candidateCheckboxClicked"
		},

		initialize: function(opts) {
			this.patientId = opts.patientId;
			this.drawSelectionCandidates = opts.callback;
			this.relations = opts.relations;
			this.candidateValidations = opts.candidateValidations;
			this.listenTo(this.model, "change", this.render);
		},
		
		selectedRelations : function(relations){
			var selectedRelations = [];
			_.each($('.candidate-checkbox'), function(checkbox){
				if(checkbox.checked === true){
					selectedRelations.push(_.extend(checkbox.dataset, { candidateId : checkbox.id }));
				}
			});
			return selectedRelations;
		},
		updateRelationCheckboxes : function(selectedRelations){
			_.each($('.candidate-checkbox'), function(checkbox){
				if(_.findWhere(selectedRelations, {
					drugCui : checkbox.dataset["drugCui"],
					symptomCui : checkbox.dataset["symptomCui"]
				})){
					checkbox.checked = true;
				};
			});
		},
		
		render: function() {
			$('#accordion').html("");
			_.each(usermodeFilters[session.userMode()](this.relations, this.candidateValidations), function(candidate){
				$('#accordion').append(this.candidateQuestions(_.extend(candidate, {
					userMode: session.userMode(),
					confirmText: session.userMode() == 'adjudicate' ? 'Adjudicate and Lock' : 'Confirm Selection'
				})));
			}.bind(this));

			var patientId = this.patientId;
			_.each(parseQueryString().candidate.split(","), function(candidate){
				var checkboxId = candidate;
				if($('#' + checkboxId)[0]){
					$('#' + checkboxId)[0].checked = true;					
				}
			});

			_.each(this.selectedRelations(), function(relation){
				$('input#' + relation.candidateId).attr('checked', true);
			});	

			$('.triggers-tooltip').tooltip();
		},
		
		updateValidationStatus : function(url, score, candidateId){
			$.ajax({
				url: url,
				type: 'put',
				success: function(data) {
					console.log(candidateId);
					$('#accordion-'+candidateId).collapse();
					var targetCandidate=_.findWhere(this.candidateValidations, {candidateId : this.candidateId, userId : session.userId()});
					
					// this should be different based on adjudication or validation
					
					if(targetCandidate === undefined){
						this.candidateValidations.push({
							candidateId : this.candidateId,
							userId : session.userId(),
							patientId : this.candidateId.split("_")[0],
							score: this.score
						});
					}else{
						targetCandidate.score = this.score;
					}
					this.render();
				}.bind(_.extend(this, {score : score, candidateId : candidateId})),
				error: function(data) {
					console.log("An error has occurred.");
				}
			});

		},
		
		validationConfirmed : function(event){
			var candidateId = event.target.dataset['candidateId'];
			var score = parseInt($('input[name=optradio-'+candidateId+']:checked').val());
			this.updateValidationStatus('/rest/validation/candidate/' + candidateId + '/user/' + session.userId() + "/score/"+ score, score, candidateId);
		},
		
		adjudicationConfirmed : function(event){
			var candidateId = event.target.dataset['candidateId'];
			var score = parseInt($('input[name=optradio-'+candidateId+']:checked').val());
			this.updateValidationStatus('/rest/adjudicateCandidate/' + candidateId + '/user/' + session.userId() + "/score/"+ score, score, candidateId);
			var targetRelation=_.findWhere(this.relations, {candidateId : this.candidateId});
			targetRelation.adjudicated=true;
			targetRelation.adjudicatedScore=score;
			targetRelation.adjudicatorId=session.userId();
			targetRelation.adjudicatorEmail=session.userEmail();
		},
		candidateCheckboxClicked : function(event){
			this.candidateSelectionChange(event);
			this.drawSelectionCandidates();
		},
		candidateSelectionChange : function(event){
			history.pushState({trigger:false}, "", location.pathname + "?candidate=" + _.map(this.selectedRelations(), function(relation){
					return relation.candidateId;
				}).join(","));
		}
	});

	return candidatePanelView;
});