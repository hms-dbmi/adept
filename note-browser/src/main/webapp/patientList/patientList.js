define(["text!patientList/patientListContainer.hbs", "common/session", "text!patientList/patientList.hbs","text!patientList/cuiSelectionTile.hbs","text!patientList/candidateSelectionTile.hbs", "common/synonymMapper", "jquery", "underscore", "handlebars"],
		function(patientListContainer, session, patientTemplate, cuiTemplate, candidateTemplate, synMapper, $, _, HBS){
	var patientList = {
			showPatientList : function(router){
				$('#app').html(patientListContainer);

				var params = new URLSearchParams(window.location.search);

				var selectedDrugCuis = [];
				var selectedEventCuis = [];
				var selectedCandidates = [];

				var synMapCandidates = function(candidates){
					var postSynData = {};
					_.each(candidates, function(candidate){
						var synCandidate = postSynData[candidate.drugCui];
						candidate.drug = synMapper.nameForDrugCui(candidate.drugCui, candidate.drug);
						if(candidate.patientIds){
							candidate.patientCount = candidate.patientIds.length;
						}
						if(synCandidate === undefined){
							postSynData[candidate.drugCui + "_" + candidate.eventCui] = candidate;
						}else{
							if(synCandidate.patientIds){
								synCandidate.patientIds = _.union(candidate.patientIds, synCandidate.patientIds);
								synCandidate.patientCount = synCandidate.patientIds.length;
							}
						}
					});
					return postSynData;
				}

				var filterPatients = function(){
					var drugEnabledPatients = [];
					var eventEnabledPatients = [];
					var candidateEnabledPatients = [];
					if(selectedCandidates.length > 0){
						_.each(selectedCandidates, function(candidate){
							var cuis = candidate.split('-');
							candidateEnabledPatients.push($('#patientList .patient-info-block').has('.btn-candidate.drug-'+cuis[0]+'.event-'+cuis[1]));
						});
					}
					if(selectedDrugCuis.length > 0){
						_.each(selectedDrugCuis, function(drugCui){
							drugEnabledPatients.push($('#patientList .patient-info-block').has('.btn-candidate.drug-'+drugCui));
						});
					}
					if(drugEnabledPatients.length > 0 || candidateEnabledPatients.length > 0 || eventEnabledPatients.length > 0){
						$('.patient-info-block').hide();
					}else{
						$('.patient-info-block').show();
					}
					if(selectedCandidates.length > 0){
						_.each(candidateEnabledPatients, function(patient){
							patient.show();
						});						
					}else{
						_.each(drugEnabledPatients, function(patient){
							patient.show();
						});						
					}
					if(drugEnabledPatients.length > 0 || candidateEnabledPatients.length > 0 || eventEnabledPatients.length > 0){
						$('.btn-candidate').hide();
						$('.candidate-validation-indicator').hide();
					}else{
						$('.btn-candidate').show();
						$('.candidate-validation-indicator').show();
					}
					if(selectedCandidates.length > 0){
						_.each(selectedCandidates, function(candidateId){
							var cuis = candidateId.split('-');
							$('.btn-candidate.drug-'+cuis[0]+'.event-'+cuis[1]).show();
							$('.btn-candidate.drug-'+cuis[0]+'.event-'+cuis[1]).show();
						});
					}else{
						_.each(selectedDrugCuis, function(drugCui){
							$('.btn-candidate.drug-'+drugCui).show();
							$('.candidate-validation-indicator.drug-'+drugCui).show();
						});
					}
					if(session.userMode()==="validate"){
						$('.btn-candidate[data-adjudicated=true]').hide();
						$('.candidate-validation-indicator[data-adjudicated=true]').hide();
					}
					
				};

				var filterCandidates = function(){
					if(selectedDrugCuis.length > 0){
						$('.candidate-relation-block').hide();
						selectedDrugCuis.forEach(function(drugCui){
							$('.candidate-relation-block')
							.has('.candidate-selection-checkbox.drug-'+drugCui)
							.show();
						});			
					}else{
						$('.candidate-relation-block').show();
					}
				};

				$.get("/rest/patientHistory/",
						function(data, status, jqXHR){
					var patientsWithCandidateRelations = _.filter(data, function(patient){
						return patient.candidateRelations.length > 0;
					});
					_.each(data, function(patient){
						patient.candidateRelations = synMapCandidates(patient.candidateRelations);
					});
					var content = HBS.compile(patientTemplate)(patientsWithCandidateRelations.map(function(patient){
						return {
							patientId : patient.patientId.substring(0,7),
							candidateRelations: patient.candidateRelations
						};
					}));
					
					$("#patientList").html(content);
					// Hide all candidate buttons that have already been adjudicated
					if(session.userMode()==="validate"){
						$('.btn-candidate[data-adjudicated=true]').hide();
						$('.candidate-validation-indicator[data-adjudicated=true]').hide();
					}
					$(".btn-candidate").click(function(event){
						var target = event.target.dataset.targetUrl;
						router.navigate(target, {trigger: true});
					});
				}.bind(this)
				);
				$.get("/rest/cui/",
						function(data, status, jqXHR){
					var terms = _.uniq(_.map(_.where(data, {"term_class": 1}), function(drug){
						drug.term = synMapper.nameForDrugCui(drug.cui, drug.term);
						return drug;
					}), function(drug){
						return drug.cui;
					});
					var content = HBS.compile(cuiTemplate)({
						termClassName : "Drug",
						terms : terms
					});

					$("#drug-filter").html(content);

					var getSelectedCuis = function(wrapperId){
						return _.map(
								_.where(
										$(wrapperId + " .cui-selection-checkbox:checked")
										, {checked: true}), 
										function(checkbox){
									return checkbox.id;
								});
					}

					var getSelectedCandidates = function(wrapperId){
						return _.map(
								_.where(
										$(wrapperId + " .candidate-selection-checkbox:checked")
										, {checked: true}), 
										function(checkbox){
									return checkbox.id;
								});
					}

					$("#drug-filter .cui-selection-checkbox").click(function(event){
						selectedDrugCuis = getSelectedCuis("#drug-filter");
						filterPatients();
						filterCandidates();
					}.bind(this));

					$("#candidate-filter .candidate-selection-checkbox").click(function(event){
						selectedCandidates = getSelectedCandidates("#candidate-filter");
						filterPatients();
						filterCandidates();
					}.bind(this));

				}.bind(this)
				);
				$.get("/rest/cui/candidates/",
						function(data, status, jqXHR){
					var postSynData = synMapCandidates(data);
					var content = HBS.compile(candidateTemplate)
					(_.sortBy(_.values(postSynData), 'drug'));
					$("#candidate-filter").html(content);

				}.bind(this)
				);
			}
	};
	return patientList;
});
