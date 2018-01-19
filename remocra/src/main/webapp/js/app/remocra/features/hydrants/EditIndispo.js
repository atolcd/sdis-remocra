Ext.require('Ext.window.Window');
Ext.require('Sdis.Remocra.features.hydrants.GridIndispos');
Ext.define('Sdis.Remocra.features.hydrants.EditIndispo', {
    extend: 'Ext.window.Window',
    alias: 'widget.editIndispo',
    width: 550,
       height: 350,
       title: 'Indisponibilités temporaires liées au point d\'eau',
       modal: true,
       layout: 'form',
       bodyPadding: 15,
       minButtonWidth: 100,
       buttonAlign: 'center',
       defaults: {
           height: 30
       },

        items:[ {
         xtype: 'fieldcontainer',
         layout: 'hbox',
         items: [{
            xtype: 'button',
            text: 'Activer',
            margin:'0 10 0 0',
            itemId: 'activIndispo',
            disabled: true
        },{
            xtype: 'button',
            text: 'Lever',
            itemId: 'levIndispo',
            disabled: true
        }]}, {
            xtype: 'fieldcontainer',
            layout: 'hbox',
            items: [{
        xtype: 'gridIndispos',
        itemId: 'gridIndispos',
        width: 500,
        height: 200,
        hideHeaders: true
        }]
    }],

    initComponent: function() {
        this.buttons = [{
            text: 'Fermer',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});
