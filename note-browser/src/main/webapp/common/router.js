define(["settings/settings","common/searchParser", "nav/topNav", "backbone", "common/session", "common/login", "patientList/patientList", "clinicalHistoryViewer/visualization"], 
        function(settings, searchParser, topNav, Backbone, session, login, patientList, visualization){
    var Router = Backbone.Router.extend({
        routes: {
            "patientList(/)" : "displayPatientList",
            "login(/)" : "login",
            "logout(/)" : "logout",
            "settings(/)" : "displaySettings",
            "patientHistory/:patientId(/)(?*queryString)" : "displayPatientHistory",
            "changeUserMode(?*queryString)" : "changeUserMode",
            
            // This path must be last in the list
            "*path" : "displayPatientList"
        },
       
        initialize: function(){
            topNav.registerRouter(this);
            this.settings = new settings();
            var pushState = history.pushState;
            history.pushState = function(state, title, path) {
            		if(state.trigger){
            			this.router.navigate(path, state);
            		}else{
            			this.router.navigate(path, {trigger: true});
            		}
                return pushState.apply(history, arguments);
            }.bind({router:this});
        },
       
        execute: function(callback, args, name){
            if( ! session.isValid()){
                this.login();
                return false;
            }
            if (callback) {
                callback.apply(this, args);
            }
        },
       
        login : function(){
            topNav.clear();
            login.showLoginPage();
        },

        logout : function(){
            topNav.clear();
            sessionStorage.clear();
            window.location = "/logout";
        },

        displayPatientList : function(){
            topNav.updateTopNav("Patients", session.username);
            patientList.showPatientList(this);            
        },

        displayPatientHistory : function(patientId, queryString){
            topNav.updateTopNav("Patient History", session.username);
            visualization.showPatientHistory(this, patientId, queryString);
        },
        
        displaySettings : function(){
            topNav.updateTopNav("Settings", session.username);
            this.settings.showSettings();
        },

        changeUserMode : function(queryString){
            this.navigate(unescape(searchParser().currentState), {trigger: true});
        }

    });
    return new Router();
});