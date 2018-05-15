require(["Backbone", "Handlebars", "patientList/patientList.hbs"], function(Backbone, HBS, template){
	var patientListView = Backbone.View.extend({
		tagName: "div",
		className: "patient-list",
		events: {
			
		},
		initialize: function(){
			
		},
		render: function(){
			this.$el.html(HBS.compile(template)(this.model));
		}
	});
});