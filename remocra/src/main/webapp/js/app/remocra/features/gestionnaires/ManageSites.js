Ext.define('Sdis.Remocra.features.gestionnaires.ManageSites', {
    extend: 'Ext.Component',
    alias: 'widget.crManageSites',

    vueManageSites  : null,
    id : "manageSites",

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vueManageSites = remocraVue.manageSites(this.id);
            }
        },
        'beforedestroy': function() {
            this.vueManageSites.$el.remove();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});