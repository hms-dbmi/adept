define(["text!clinicalHistoryViewer/cuiStyles.hbs", "text!clinicalHistoryViewer/cuiStylesTimeline.hbs", 'jquery', 'handlebars', 'common/synonymMapper'],
		function(cuiStyles, cuiStylesTimeline, $, HBS, synMapper){
	var gradientFactor = .15;

	var colorPalette = 
		[
		 "rgb(0,110,130)",
		 "rgb(130,20,160)",
		 "rgb(250,120,250)",
		 "rgb(170,10,60)",
		 "rgb(250,120,80)",
		 "rgb(240,240,50)",
		 "rgb(160,250,130)",
		 "rgb(250,230,190)"
		 ];
	var colorPaletteGradient =
		[
		 "rgba(0,110,130,"+gradientFactor+")",
		 "rgba(130,20,160,"+gradientFactor+")",
		 "rgba(250,120,250,"+gradientFactor+")",
		 "rgba(170,10,60,"+gradientFactor+")",
		 "rgba(250,120,80,"+gradientFactor+")",
		 "rgba(240,240,50,"+gradientFactor+")",
		 "rgba(160,250,130,"+gradientFactor+")",
		 "rgba(250,230,190,"+gradientFactor+")"
		 ];

	var cuiStylesTemplate = HBS.compile(cuiStyles);

	return function(relations){
		var cuisInOrder = 
			_.uniq(_.union(_.pluck(relations, 'eventCui'),_.pluck(relations, 'drugCui')));

		var colorMapping = [];

		var x = 0;
		_.each(cuisInOrder, function(cui){
			colorMapping.push({
				cui: cui,
				color: colorPalette[x % colorPalette.length],
				gradientColor: colorPaletteGradient[x++ % colorPaletteGradient.length]
			});
		});

		$("#cui-styles").html(cuiStylesTemplate({
			cuiColorsSolid : colorMapping,
			cuiColors : colorMapping
		}));
	};
});