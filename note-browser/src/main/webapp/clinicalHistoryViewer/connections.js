define(['jquery', 'handlebars'], 
		function($, HBS){
	var connections = {
		setupConnections : function(){
			var canvas = document.getElementById('canvas');
			var ctx = canvas.getContext('2d');

			var DOMURL = window.URL || window.webkitURL || window;

			var createSVG = function($selector){
				var data = '<svg xmlns="http://www.w3.org/2000/svg" width="' + $('#text')[0].width + '"" height="' + $('#text')[0].height + '">' +
				'<foreignObject width="100%" height="100%">' +
				'<div id="text" xmlns="http://www.w3.org/1999/xhtml" style="font-size:12pt">'+
				$selector.innerHTML+
				'</div>'+
				'</foreignObject>' +
				'</svg>'
				;

				var img = new Image();
				var svg = new Blob([data], {type: 'image/svg+xml'});
				var url = DOMURL.createObjectURL(svg);
				return { 
					url : url, 
					img: img
				};	
			}

			var connections = [];

			var connectElements = function($a, $b, drugName, symptomName){
				var aBounds = $a[0].getBoundingClientRect();
				var bBounds = $b[0].getBoundingClientRect();
				var textBounds = $('#text')[0].getBoundingClientRect();
				var canvasTop = $('#canvas').position().top;

				ctx.beginPath();
				ctx.lineWidth = 10;
				var avg = function(a, b){
					return (a + b)/2;
				}
				var angle = Math.atan2(aBounds.right - bBounds.right, aBounds.top-bBounds.top);
				var start = {
						x : avg(aBounds.left,aBounds.right) - textBounds.left + 4,
						y : avg(aBounds.top,aBounds.bottom)  + ( $(window).scrollTop())
				}
				var end = {
						x : avg(bBounds.left,bBounds.right) - textBounds.left + 4,
						y : avg(bBounds.top,bBounds.bottom) + ( $(window).scrollTop())
				}
				connections.push({
					start : start,
					end : end,
					drug : drugName,
					symptom : symptomName
				});
				ctx.moveTo(start.x, start.y - (canvasTop));
				ctx.lineTo(end.x, end.y - (canvasTop));
				ctx.strokeStyle = 'rgba(110,110,110,0.3)';
				ctx.stroke();	
			}


			var registerConnectionHandler = function(){
				$('canvas').off();
				$('canvas').click(function(event){
					var scrollTop = 0;//($(window).scrollTop());
					var distance = function(event, connection){
						var leftOffset = (parseInt($('canvas')[0].style.left));
						var denominator = ((connection.end.y - connection.start.y)^2) + ((connection.end.x - connection.start.x)^2);
						var numerator = ((connection.end.y - connection.start.y) * (event.pageX - leftOffset)) - ((connection.end.x - connection.start.x) * (event.pageY-scrollTop)) + (connection.end.x * connection.start.y) - (connection.end.y * connection.start.x);
						return numerator / denominator;
					}
					var test = function(connection){
						var leftOffset = (parseInt($('canvas')[0].style.left));
						var dist = distance(event, connection);
						var tolerance = 25;
						var startX = Math.min(connection.end.x, connection.start.x);
						var endX = Math.max(connection.end.x, connection.start.x);
						var startY = Math.min(connection.end.y, connection.start.y);
						var endY = Math.max(connection.end.y, connection.start.y);
						var withinX = 
							(event.pageX < (endX + leftOffset)) 
							&& (event.pageX > (startX + leftOffset));
						var withinY = ((event.pageY-scrollTop) < (endY + tolerance)) && (((event.pageY-scrollTop) > (startY - tolerance)));
						return (((dist * dist) < tolerance) && withinX && withinY);
					}
					for(var x = 0;x<connections.length;x++){
						console.log(connections[x].start.x + "," + connections[x].start.y + " " + connections[x].end.x + "," + connections[x].end.y + " --- " + test(connections[x]));
						if(test(connections[x])){
							$('.modal-content').html(
									HBS.compile($('#modal-content-template')[0].innerHTML)({
										drugName : connections[x].drug,
										symptomName : connections[x].symptom
									}));
							$('#annotationModal').modal({
								keyboard: false
							});
						}
					}
				});
			}

			var drawConnections = function(groupList){
				connections = [];
				$('.candidate-checkbox').each(function(index, candidate){
					if(candidate.id != "{{candidateId}}" ){
						var drugCui = candidate.dataset.drugCui;
						var eventCui = candidate.dataset.symptomCui;

						_.each(groupList, function(group){
							if(group!="" && $("."+drugCui+"."+group)[0] != undefined && $("."+eventCui+"."+group)[0] != undefined){
								connectElements($("."+drugCui+"."+group),$("."+eventCui+"."+group), candidate.dataset['drug'], candidate.dataset['symptom']);	  		  					  
							}			  
						});
					}
				});
				registerConnectionHandler();
			};
			return {
				registerConnectionHandler : registerConnectionHandler,
				drawConnections : drawConnections
			}
		}
	};
	return connections;
}
);