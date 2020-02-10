Ext.define('Sdis.Remocra.features.courriers.Courriers', {
    extend: 'Ext.Component',
    alias: 'widget.crCourriers',

    vueVisualisationCourriers  : null,
    id : "show-visualisationCourrier-"+(++Ext.AbstractComponent.AUTO_ID),

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vueVisualisationCourriers = remocraVue.visualisationCourriers(this.id);
            } else {
                console.log('VisualisationCourrier : remocraVue undefined');
            }
        },
        'beforedestroy': function() {
            this.vueVisualisationCourriers.$el.remove();
            this.vueVisualisationCourriers.$destroy();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});