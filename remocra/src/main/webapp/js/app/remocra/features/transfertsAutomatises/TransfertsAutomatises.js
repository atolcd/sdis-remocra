Ext.define('Sdis.Remocra.features.transfertsAutomatises.TransfertsAutomatises', {
    extend: 'Ext.Component',
    alias: 'widget.crTransfertsAutomatises',

    vueTransfertsAutomatises  : null,
    id : "show-transfertsAutomatises-"+(++Ext.AbstractComponent.AUTO_ID),

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vueTransfertsAutomatises = remocraVue.transfertsAutomatises(this.id);
            } else {
                console.log('transfertsAutomatises : remocraVue undefined');
            }
        },
        'beforedestroy': function() {
            this.vueTransfertsAutomatises.$el.remove();
            this.vueTransfertsAutomatises.$destroy();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});