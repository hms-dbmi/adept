define(["common/synonymMapper", 'underscore'], function(synMapper, _){

	var mapCandidates = function(candidateList){
		return _.map(candidateList, function(candidate){
			candidate.drug = synMapper.nameForDrugCui(candidate.drugCui, candidate.drug);
			return candidate;
		});
	};

	var buildValidations = function(candidate, validationsByCandidate){
		return (candidate.adjudicated ? [{
			candidateId : candidate.id,
			patientId: candidate.patientId,
			score: candidate.adjudicatedScore,
			userId: candidate.adjudicatorId,
			userEmail: candidate.adjudicatorEmail,
			validationIndicatorA: candidate.adjudicatedScore === 1 ? "up" : "down",
					validationIndicatorB: candidate.adjudicatedScore === -1 ? "down" : "up",
							validationOutcome: candidate.adjudicatedScore === 1 ? "confirmed that there was a relationship." : (candidate.adjudicatedScore === 0 ? "confirmed the relationship was inconclusive" : "confirmed there was not a relationship")
		}] : 
			_.map(validationsByCandidate[candidate.candidateId], function(validation){
				return _.extend({}, validation, {
					validationIndicatorA: validation.score === 1 ? "up" : "down",
							validationIndicatorB: validation.score === -1 ? "down" : "up",
									validationOutcome: validation.score === 1 ? "believes there may be a relationship." : (validation.score === 0 ? "believes the relationship was inconclusive" : "believes there was not a relationship")

				});
			})
		);
	};

	return {
		adjudicate : function(candidateList, validations){
			var validationsByCandidate = _.groupBy(validations, 'candidateId');
			var mappedCandidates = mapCandidates(candidateList);
			var sortedCandidates = _.sortBy(mappedCandidates, function(candidate){
				var validations = validationsByCandidate[candidate.id];
				return -1 * (validations ? validations.length : 0) + (candidate.adjudicated ? 1000 : 0);
			});
			return _.map(
					sortedCandidates, 
					function(candidate){
						return _.extend({
							priorityClass : (candidate.adjudicated ? "pink-background" : 
								(validationsByCandidate[candidate.candidateId]  && 
										validationsByCandidate[candidate.candidateId].length > 1) 
										? "yellow-background" : "")
						}, candidate, 
						{ validations : buildValidations(candidate, validationsByCandidate)});
					});
		},
		validate : function(candidateList, validations){
			var validationsByCandidate = _.groupBy(_.filter(validations, function(validation){
				return validation.userId == JSON.parse(sessionStorage.session).userId;
			}), 'candidateId');
			var mappedCandidates = mapCandidates(candidateList);
			var sortedCandidates = _.sortBy(mappedCandidates, function(candidate){
				var validations = validationsByCandidate[candidate.candidateId];
				return 1 * (validations ? validations.length : 0);
			});
			return _.filter(_.map(
					sortedCandidates, 
					function(candidate){
						return _.extend({
							priorityClass : 
								(validationsByCandidate[candidate.candidateId]) 
								? "gray-background" : ""
						}, candidate, { validations : 
							_.map(validationsByCandidate[candidate.candidateId], function(validation){
								return _.extend({}, validation, {
									validationIndicatorA: validation.score === 1 ? "up" : "down",
											validationIndicatorB: validation.score === -1 ? "down" : "up",
													validationOutcome: validation.score === 1 ? "believes there may be a relationship." : (validation.score === 0 ? "believes the relationship was inconclusive" : "believes there was not a relationship")
								});
							})});
					}), 
					function(candidate){
				return !candidate.adjudicated
			});
		},
		admin : function(candidateList, validations){
			var validationsByCandidate = _.groupBy(validations, 'candidateId');
			var mappedCandidates = mapCandidates(candidateList);
			var sortedCandidates = _.sortBy(mappedCandidates, function(candidate){
				var validations = validationsByCandidate[candidate.candidateId];
				return -1 * (validations ? validations.length : 0);
			});
			return _.map(
					sortedCandidates, 
					function(candidate){
						return _.extend({}, candidate, { validations : 
							_.map(validationsByCandidate[candidate.candidateId], function(validation){
								return _.extend({}, validation, {
									validationIndicatorA: validation.score === 1 ? "up" : "down"
										,validationIndicatorB: validation.score === -1 ? "down" : "up"
								});
							})});
					});
		}
	};
});