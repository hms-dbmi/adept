define(["backbone", "handlebars"], function(BB, HBS){
	var baseView = BB.View.extend({
		tagName: "div",
		className: "base-view",
		renderAfterTemplateCompiled: function(){
			this.$el.html(this.template(this.renderingContext()));
			return this;
		},
		renderingContext : function(){
			return this.model.attributes;
		},
		render: function(){
			this.template = HBS.compile(this.templateText);
			this.render = this.renderAfterTemplateCompiled;
			return this.render();
		}
	});
	return baseView;
});