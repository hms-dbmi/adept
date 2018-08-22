define(['jquery', 'handlebars', 'underscore', 'vis', 'common/synonymMapper'],
		function($, HBS, _, vis, synMapper){
	var eventCuiTemplate = 
		HBS.compile(
		"<span class='{{this}}-arrow'>&#9660;</span><br>");

	var drugCuiTemplate = 
		HBS.compile(
		"<span class='{{this}}-arrow'>&#9650;</span><br>");

	return function(patientNotes, relations, relationCuis, currentDocument, loadDocument){
		var timelineElement = document.getElementById('timeline');
		var sortedNotes = _.sortBy(patientNotes, 'date');
		_.each(sortedNotes, function(note, index, sortedNotes){
			note.drugCuis = _.uniq(note.drugCuis);
			note.candidateIds = _.uniq(note.candidateIds);
		});
		var notes = _.map(sortedNotes, function(note){
			var hasInterestingRelation = false;
			_.each(relations, function(relation){
				if(_.contains(note.candidateIds, relation.candidateId)){
					hasInterestingRelation = true;
				}
			});
			var mappedNote = {
				id: note.noteId,
				title: note.date,
				start: note.date,
				hasInterestingRelation: hasInterestingRelation,
				group: 'History',
				className: hasInterestingRelation ? "has-related-cuis" : "",
				content: 
					_.map(relationCuis.eventCuis, function(cui){
						if(_.contains(note.eventCuis, cui)){
							return eventCuiTemplate(cui);
						}else{
							return "&nbsp;<br>"
						}
					}).concat(
					_.map(relationCuis.drugCuis, function(cui){
						if(_.contains(note.drugCuis, cui)){
							return drugCuiTemplate(cui);
						}else{
							return "&nbsp;<br>"
						}
					})).join("")
			};
			return mappedNote;
		});

		notes.push({id: 'History-Background', type:'background', className: 'history-bg', content:'', start: _.first(sortedNotes).date, end: _.last(sortedNotes).date, group:'History'});

		var CUIs = new vis.DataSet(
				[
				 {
					 id:"History",
					 content: "<span class='legend-spacer'><br></span>" + 
					 _.map(relationCuis.eventCuis, function(cui){
						 return _.find(relations, function(relation){return cui==relation.symptomCui}).symptom;
					 }).concat(
							 _.map(relationCuis.drugCuis, function(cui){
								 return _.find(relations, function(relation){return cui==relation.drugCui}).drug;
							 })).join("<br>"),
							 subgroupOrder: function(a,b){return a.subgroupOrder - b.subgroupOrder}
				 }
				 ]
		);
		var timeline;
		
		var noteToSelect = _.findWhere(notes, {hasInterestingRelation: true}).id;

		var selectedItem;

		var createTimeline = function(loadDocument){
			$(timelineElement).html("");
			timeline = new vis.Timeline(timelineElement, notes, CUIs, {
				stack:false, 
				zoomMax : (1000 * 60 * 60) * 24 /*hours*/ * 365 /*days*/ * 3 /*years*/
			});	
			timeline.on('select', _.debounce(function(properties){
				selectedItem = properties.items[0];
				if(selectedItem !== undefined){
					currentDocument = properties.items[0];
					loadDocument(currentDocument);						
				}
				
			}, 100));
		};

		createTimeline(loadDocument);
		timeline.setSelection(noteToSelect);
		loadDocument(noteToSelect);
		
		setTimeout(function(){timeline.focus(noteToSelect)}, 100);

		timeline.on("rangechanged", _.debounce(function(args){
			var start = args.start;
			var end = args.end;
			var notesInRange = (args.start && args.end) ? _.filter(notes, function(note){
				return new Date(note.start) >= start && new Date(note.end) <= end;
			}) : notes;

			this.setItems(notes);
			this.setSelection(this.selectedItem);
		}.bind(_.extend({selectedItem:noteToSelect}, timeline)), 100));

	};


}
);