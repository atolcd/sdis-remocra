Ext.define('Sdis.Remocra.features.gestionnaires.ManageGestionnaires', {
    extend: 'Ext.Component',
    alias: 'widget.crManageGestionnaires',

    vueManageGestionnaires  : null,
    id : "manageGestionnaires",

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vueManageGestionnaires = remocraVue.manageGestionnaires(this.id);
            } else {
                console.log('manageGestionnaires : remocraVue undefined');
            }
        },
        'beforedestroy': function() {
            this.vueManageGestionnaires.$el.remove();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});