define(["text!settings/settings.hbs", "common/session", "jquery", "underscore", "common/baseView"],
		function(settingsTemplate, session, $, _, baseView){
	var settingsView = baseView.extend({
		el: $("#app"),
		initialize: function(){
			this.templateText = settingsTemplate;
		},
		events: {
			"click #add-user-btn" : "addUser",
			"click input.permissionCheckbox" : "permissionsUpdated"
		},
		addUser: function(event){
			var userModel = {
				"authenticationName" : $("#add-user-email").val(),
				"authenticationSource" : "unknown",
				"canValidate" : $("#add-user-canValidate").is(':checked'),
				"canAdjudicate" : $("#add-user-canAdjudicate").is(':checked'),
				"isAdmin" : $("#add-user-isAdmin").is(':checked')
			};
			$.ajax({
				data: JSON.stringify(userModel),
				url: "/rest/user",
				type: 'post',
				dataType: "json",
				contentType: "application/json",
				success: function(data){
					console.log(JSON.stringify(data));
					this.showSettings();
				}.bind(this)
			});
		},
		permissionsUpdated : function(event){
			var dataset = event.target.dataset;
			$.ajax({
				url: "/rest/user/" + dataset.userId + "/" + dataset.permissionName + "/" + event.target.checked,
				type: "PUT",
				success: function(data){
					this.showSettings();
				}.bind(this)
			});
			console.log(event);
		},
		showSettings : function(){
			$.get("/rest/user", function(data){
				this.renderingContext = function(){
					return { users: data };
				};
				this.render();
			}.bind(this));
		}
	});
	return settingsView;
});
