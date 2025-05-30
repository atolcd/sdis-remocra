Ext.require('Ext.window.Window');
Ext.define('Sdis.Remocra.features.hydrants.LeveIndispo', {
    extend: 'Ext.window.Window',
    alias: 'widget.leveIndispo',
    width: 450,
       height: 200,
       title: 'Confirmation de fin d\'indisponibilité temporaire',
       modal: true,
       layout: 'form',
       bodyPadding: 15,
       minButtonWidth: 100,
       buttonAlign: 'center',
       defaults: {
           height: 30
       },

    items:[{
         xtype: 'fieldcontainer',
         layout: 'hbox',
         items: [{
           xtype: 'label',
           text: 'Points d\'eau concernés :',
           width: 150,
           margin: '0 5 0 0'
         },{
           xtype: 'gridHydrantIndispos',
           itemId:'gridHydrantIndispos',
           width: 215,
          height: 100,
           hideHeaders: true
         }]
    }    ],

    initComponent: function() {
        this.buttons = [{
            text: 'Confirmer',
            itemId: 'levIndispo'
            },{
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});
