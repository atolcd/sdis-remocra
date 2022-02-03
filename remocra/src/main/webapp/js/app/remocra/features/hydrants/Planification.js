Ext.define('Sdis.Remocra.features.hydrants.Planification', {
    extend: 'Ext.Component',
    alias: 'widget.planification',

    vueVisualisationCourriers  : null,
    id : "show-planificationDeci-"+(++Ext.AbstractComponent.AUTO_ID),

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vuePlanificationDeci = remocraVue.planificationDeci(this.id, REMOCRA_IGN_KEYS, REMOCRA_INIT_BOUNDS);
            } else {
                console.log('PlanificationDeci : remocraVue undefined');
            }
        },
        'beforedestroy': function() {
            this.vuePlanificationDeci.$el.remove();
            this.vuePlanificationDeci.$destroy();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});