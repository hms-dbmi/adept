define(["common/session", "common/searchParser", "underscore", "jquery", "handlebars", "text!common/login.hbs"], 
		function(session, parseQueryString, _, $, HBS, loginTemplate){
	var loginTemplate = HBS.compile(loginTemplate);

	var loginCss = null
	$.get("https://avillachlab.us.webtask.io/connection_details_base64?webtask_no_cache=1&css=true",function(css){
		loginCss = "<style>" + css + "</style";
	});
	
	var login = {
		showLoginPage : function(){			
			var queryObject = parseQueryString();

			var webtaskBaseUrl = "https://avillachlab.us.webtask.io/connection_details_base64/";

			if(typeof queryObject.code === "string"){
				$.ajax({
					url: "/rest/auth", 
					type: 'post',
					data: JSON.stringify({
						code : queryObject.code,
					}),
					contentType: 'application/json',
					success: function(data){
						session.authenticated(data.userId, data.token, data.username, data.permissions);
						history.pushState({}, "", "patientList");
					},
					error: function(data){
						history.pushState({}, "", "login");
					}
				});
			}else{
				$.ajax("https://avillachlab.us.webtask.io/connection_details_base64/?webtask_no_cache=1&client_id=MUPJoktRm8irc1yOqCfbP5IvAONQtK4W", 
						{
					dataType: "text",
						success : function(scriptResponse){
							$('#app').html(loginTemplate({
								buttonScript : scriptResponse,
								clientId : "MUPJoktRm8irc1yOqCfbP5IvAONQtK4W",
								auth0Subdomain : "avillachlab",
								callbackURL : window.location.protocol + "//"+ window.location.hostname + (window.location.port ? ":"+window.location.port : "") +"/login"
							}));
							$('#app').append(loginCss);
						}
				});				
			}
		}
	};
	return login;
});