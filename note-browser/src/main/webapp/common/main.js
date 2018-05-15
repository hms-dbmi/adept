require.config({
	baseUrl: "/",
	paths: {
		jquery: 'webjars/jquery/3.2.1/jquery.min',
		underscore: 'webjars/underscorejs/1.8.3/underscore-min',
		backbone: 'webjars/backbonejs/1.3.3/backbone-min',
		bootstrap: 'webjars/bootstrap/3.3.7-1/js/bootstrap.min',
		handlebars: 'webjars/handlebars/4.0.5/handlebars.min',
		vis: 'webjars/vis/4.19.1/dist/vis.min',
		text: 'webjars/requirejs-text/2.0.15/text',
		nav: '/nav/',
		clinicalHistoryViewer: "/clinicalHistoryViewer/",
		patientList: "/patientList/",
		common: "/common/"
	},
	shim: {
		"bootstrap": {
			deps: ["jquery"]
		}
	}
});

require(["backbone", "common/session", "common/router", "underscore", "jquery", "bootstrap"],
		function(Backbone, session, router){
	Backbone.history.start({pushState:true});
});
