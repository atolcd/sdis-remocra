Ext.require('Ext.window.Window');
Ext.define('Sdis.Remocra.features.hydrants.ProlongerIndispo', {
    extend: 'Ext.window.Window',
    alias: 'widget.prolongerIndispo',
    width: 450,
       height: 250,
       title: 'Prolongation d\'une indisponibilité temporaire',
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
            fieldLabel: 'Nouvelle date de fin',
            width: 250,
            margin: '0 20 0 0',
            labelWidth: 150,
            labelAlign: 'left',
            name: 'dateFin',
            format: 'd/m/Y'
            }, {
            xtype: 'timefield',
            name: 'timeFin',
            fieldLabel: 'à',
            width: 100,
            labelWidth: 20,
            minValue: "00:00",
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
            text: 'Confirmer',
            itemId: 'prolongeIndispo'
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
