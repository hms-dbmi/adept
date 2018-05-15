define(["jquery"], function($){
	var synonymousDrugs = 
		[
		 {
			 name : "Ambrisentan",
			 cui : "C1176329",
			 cuis : ["C1176329", "C1949323"],
			 synonyms : ["Ambrisentan","Letairis"]
		 },
		 {
			 name : "Bosentan",
			 cui : "C0252643",
			 cuis : ["C0252643", "C1101331"],
			 synonyms : ["Bosentan","Tracleer"]
		 },
		 {
			 name : "Iloprost",
			 cui : "C0079594",
			 cuis : ["C0079594", "C1541936"],
			 synonyms : ["Iloprost","Ventavis"]
		 },
		 {
			 name : "Treprostinil",
			 cui : "C0079594",
			 cuis : ["C0079594", "C3700977", "C1101501", "C2718336"],
			 synonyms : ["Treprostinil","Orenitram","Remodulin","Tyvaso"]
		 },
		 {
			 name : "Sildenafil",
			 cui : "C0529793",
			 cuis : ["C0529793", "C1614029", "C0663448"],
			 synonyms : ["Sildenafil","Revatio","Viagra"]
		 },
		 {
			 name : "Tadalafil",
			 cui : "C1176316",
			 cuis : ["C1176316", "C2709986", "C0967376"],
			 synonyms : ["Tadalafil","Adcirca","Cialis"]
		 },
		 {
			 name : "Eproprostenol",
			 cui : "C0033567",
			 cuis : ["C0033567", "C0376357","C1174787","C0033567"],
			 synonyms : ["Eproprostenol","Flolan","Veletri","Prostacyclin", "PGI2"]
		 },
		 ];

	return {
		nameForDrugCui : function(drugCui, originalName){
			var synonym = _.find(synonymousDrugs, function(drug){
				return _.contains(drug.cuis, drugCui);
			});
			if(synonym === undefined){
				return originalName;
			}else{
				return synonym.name;				
			}
		},
		mapCandidateDrugtoSynonym : function(candidate){
			var synonym = _.find(synonymousDrugs, function(drug){
				return _.contains(drug.cuis, candidate.drugCui);
			});
			if(synonym === undefined){
				return [candidate.patientId, candidate.drugCui, candidate.eventCui].join("_");
			}else{
				return [candidate.patientId, synonym.cui, candidate.eventCui].join("_");
			}
		},
		mapSynonymClasses : function(){
			_.each(synonymousDrugs, function(drug){
				_.each(drug.cuis, function(cui){
					if(cui!==drug.cui){
						$('span.'+cui).addClass(drug.cui);
						$('span.'+cui).removeClass(cui);
					}
				});
			});
		}
	};

});