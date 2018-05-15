define(["handlebars", "jquery", "text!nav/topNav.hbs"], 
		function(HBS, $, topNavHbs){
	var template = HBS.compile(topNavHbs);


	var trueEntries = function(permission){
		if(permission[1]) return true;
	};
	
	var firstColumn = function(permission){
		return permission[0]
	};
	
	return {
		clear: function(){
			$('#header').html("");
		},
		
		updateTopNav : function(currentViewName, currentUser){
			var session = JSON.parse(sessionStorage.getItem("session"));
			var currentUserMode = session.currentUserMode;
			if(!currentUserMode){
				currentUserMode = _.first(_.map(_.filter(_.pairs(session.permissions), trueEntries), firstColumn));
				session.currentUserMode = currentUserMode;
			}
			$('#header').html(template({
				currentUserMode : currentUserMode,
				permissions : _.map(
						_.filter(_.pairs(session.permissions), trueEntries), 
						firstColumn),
				currentViewName : currentViewName,
				currentUser : session.username,
				isAdmin : session.permissions.admin
			}));		
			$('#user-mode-dropdown .dropdown-menu a').click(function(event){
				this.updateUserMode(event.target.dataset["mode"]);
				this.router.navigate("changeUserMode?currentState=" + escape(location.pathname + location.search), {trigger : true});
			}.bind(this));
			$('#user-menu-dropdown .dropdown-menu a').click(function(event){
				this.router.navigate(event.target.dataset["route"], 
						{trigger:true})
			}.bind(this));
			sessionStorage.setItem("session", JSON.stringify(session));
		}, 
		registerRouter : function(router){
			this.router = router;
		},
		updateUserMode : function(userMode){
			var session = JSON.parse(sessionStorage.getItem("session"));
			session.currentUserMode = userMode;
			sessionStorage.setItem("session", JSON.stringify(session));	
			$('#user-mode').text(userMode);
			$('#user-mode').append("<span class=\"caret\">");
		}
	};
});