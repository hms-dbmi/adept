define(["text!patientList/patientListContainer.hbs", "common/session", "text!patientList/patientList.hbs","text!patientList/cuiSelectionTile.hbs","text!patientList/candidateSelectionTile.hbs", "common/synonymMapper", "jquery", "underscore", "handlebars"],
		function(patientListContainer, session, patientTemplate, cuiTemplate, candidateTemplate, synMapper, $, _, HBS){
	var patientList = {
			showPatientList : function(router){
				$('#app').html(patientListContainer);

				var params = new URLSearchParams(window.location.search);
				
				var selectedDrugCuis = [];
				var selectedEventCuis = [];
				var selectedCandidates = [];

				if(sessionStorage && sessionStorage.selectedCuis){
					var selections = JSON.parse(sessionStorage.getItem('selectedCuis'));
					selectedDrugCuis = selections.selectedDrugCuis;
					selectedEventCuis = selections.selectedEventCuis;
				}

					
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
					
					var selectedPairs = [];
					_.each(selectedDrugCuis, function(drugCui){
						_.each(selectedEventCuis, function(eventCui){
							selectedPairs.push({drugCui : drugCui, eventCui : eventCui});
						});
					});
					
					var drugEnabledPatients = [];
					var eventEnabledPatients = [];
					var pairEnabledPatients = [];

					if(selectedPairs.length > 0){
						_.each(selectedPairs, function(pair){
							pairEnabledPatients.push($('#patientList .patient-info-block')
									.has('.btn-candidate.drug-'+pair.drugCui+'.event-'+pair.eventCui));
						});

						// Hide all patients if any filters are applied, otherwise show all patients
							$('.patient-info-block').hide();
							$('.btn-candidate').hide();
							$('.candidate-validation-indicator').hide();
						_.each(pairEnabledPatients, function(patient){
							patient.show();
						});
						_.each(selectedPairs, function(pair){
							$('.btn-candidate.drug-'+pair.drugCui+'.event-'+pair.eventCui).show();
							$('.candidate-validation-indicator.drug-'+pair.drugCui+'.event-'+pair.eventCui).show();
						});
					}else{
						if(selectedDrugCuis.length > 0){
							_.each(selectedDrugCuis, function(drugCui){
								drugEnabledPatients.push($('#patientList .patient-info-block').has('.btn-candidate.drug-'+drugCui));
							});
						}
						if(selectedEventCuis.length > 0){
							_.each(selectedEventCuis, function(eventCui){
								eventEnabledPatients.push($('#patientList .patient-info-block').has('.btn-candidate.event-'+eventCui));
							});
						}
						// Hide all patients if any filters are applied, otherwise show all patients
						if(drugEnabledPatients.length > 0 || eventEnabledPatients.length > 0){
							$('.patient-info-block').hide();
							$('.btn-candidate').hide();
							$('.candidate-validation-indicator').hide();
						}else{
							$('.patient-info-block').show();
							$('.btn-candidate').show();
							$('.candidate-validation-indicator').show();
						}
						_.each(_.union(drugEnabledPatients, eventEnabledPatients), function(patient){
							patient.show();
						});
						_.each(selectedDrugCuis, function(drugCui){
							$('.btn-candidate.drug-'+drugCui).show();
							$('.candidate-validation-indicator.drug-'+drugCui).show();
						});
						_.each(selectedEventCuis, function(eventCui){
							$('.btn-candidate.event-'+eventCui).show();
							$('.candidate-validation-indicator.event-'+eventCui).show();
						});
					}
					
					if(session.userMode()==="validate"){
						$('.btn-candidate[data-adjudicated=true]').hide();
						$('.candidate-validation-indicator[data-adjudicated=true]').hide();
					}
				};

				var filterCandidates = function(){
					if(selectedDrugCuis.length > 0 || selectedEventCuis.length > 0){
						$('.candidate-relation-block').hide();
						selectedDrugCuis.forEach(function(drugCui){
							$('.candidate-relation-block')
							.has('.candidate-selection-checkbox.drug-'+drugCui)
							.show();
						});	
						selectedEventCuis.forEach(function(eventCui){
							$('.candidate-relation-block')
							.has('.candidate-selection-checkbox.event-'+eventCui)
							.show();
						});			
					}else{
						$('.candidate-relation-block').show();
					}
					sessionStorage.setItem('selectedCuis', JSON.stringify({
						selectedDrugCuis : selectedDrugCuis,
						selectedEventCuis : selectedEventCuis
					}));
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
					
					$(".link-patient-id").click(function(event){
						var target = event.target.dataset.targetUrl;
						router.navigate(target, {trigger: true});
					});

					filterPatients();
					filterCandidates();
				}.bind(this)
				);
				$.get("/rest/cui/",
						function(data, status, jqXHR){
					var drugTerms = _.uniq(_.map(_.where(data, {"term_class": 1}), function(drug){
						drug.term = synMapper.nameForDrugCui(drug.cui, drug.term);
						return drug;
					}), function(drug){
						return drug.cui;
					});
					var content = HBS.compile(cuiTemplate)({
						termClassName : "Drug",
						terms : _.sortBy(drugTerms, "term")
					})

					$("#drug-filter").html(content);

					var eventTerms = _.uniq(_.map(_.where(data, {"term_class": 2}), function(drug){
						drug.term = synMapper.nameForDrugCui(drug.cui, drug.term);
						return drug;
					}), function(drug){
						return drug.cui;
					});
					var content = HBS.compile(cuiTemplate)({
						termClassName : "Event",
						terms : _.sortBy(eventTerms, "term")
					});

					$("#event-filter").html(content);

					$('#event-autocomplete').keyup(function(event){
						var searchTerm = event.target.value.toLowerCase();
						_.each($('#event-filter .cui-selection-checkbox'), function(checkbox){
							if(checkbox.dataset.cuiTerm.toLowerCase().indexOf(searchTerm) === -1 && !checkbox.checked){
								$(checkbox).parent().hide();
							}else{
								$(checkbox).parent().show();
							}
						});
					});
					
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

					$("#event-filter .cui-selection-checkbox").click(function(event){
						selectedEventCuis = getSelectedCuis("#event-filter");
						filterPatients();
						filterCandidates();
					}.bind(this));
					
					$('#drug-filter-clear-btn').click(function(event){
						$('#drug-filter').find('.cui-selection-checkbox').prop('checked', false);
						selectedDrugCuis = getSelectedCuis("#drug-filter");
						filterPatients();
						filterCandidates();
					});
					$('#event-filter-clear-btn').click(function(event){
						$('#event-filter').find('.cui-selection-checkbox').prop('checked', false);
						selectedEventCuis = getSelectedCuis("#event-filter");
						filterPatients();
						filterCandidates();
					});
					

					_.each(_.union(selectedEventCuis, selectedDrugCuis), function(cui){
						$('#'+cui).prop("checked", true);
					});

				}.bind(this)
				);
				
			}
	};
	return patientList;
});
