Ext.define('Sdis.Remocra.features.parametre.ManageInfosPeiMobile', {
    extend: 'Ext.Component',
    alias: 'widget.crManageInfosPeiMobile',

    vueManageInfosPeiMobile  : null,
    id : "manageInfosPeiMobile",

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vueManageInfosPeiMobile = remocraVue.manageInfosPeiMobile(this.id);
            }
        },
        'beforedestroy': function() {
            this.vueManageInfosPeiMobile.$el.remove();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});