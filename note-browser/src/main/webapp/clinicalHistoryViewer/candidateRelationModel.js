define(['Backbone'], function(BB){
	var candidateRelation = BB.Model.extend({
		
	});
	
	return {
		model : candidateRelation,
		collection : BB.Collection.extend({
			model : candidateRelation
		})
	};
});