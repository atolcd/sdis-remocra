Ext.require('Ext.window.Window');
Ext.define('Sdis.Remocra.features.hydrants.LeveIndispo', {
    extend: 'Ext.window.Window',
    alias: 'widget.leveIndispo',
    width: 450,
       height: 250,
       title: 'Fin d\'indisponibilité temporaire',
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
            xtype: 'datefield',
            fieldLabel: 'Date réelle de fin',
            width: 250,
            margin: '0 20 0 0',
            labelWidth: 150,
            labelAlign: 'left',
            name: 'dateFin',
            value: new Date(),
            format: 'd/m/Y'
            }, {
            xtype: 'timefield',
            name: 'timeFin',
            fieldLabel: 'à',
            width: 100,
            labelWidth: 20,
            minValue: "00:00",
            value: new Date(),
            format: 'H:i',
            increment: 15
         }]
    },{
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
            text: 'Valider',
            itemId: 'levIndispo'
            },{
            text: 'Fermer',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});
