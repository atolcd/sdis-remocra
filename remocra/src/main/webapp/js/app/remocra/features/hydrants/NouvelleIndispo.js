Ext.require('Ext.window.Window');
Ext.require('Sdis.Remocra.features.hydrants.GridHydrantIndispos');
Ext.define('Sdis.Remocra.features.hydrants.NouvelleIndispo', {
    extend: 'Ext.window.Window',
    alias: 'widget.nouvelleIndispo',
    width: 550,
       height: 350,
       title: 'Nouvelle indisponibilité temporaire',
       modal: true,
       layout: 'form',
       bodyPadding: 15,
       minButtonWidth: 100,
       buttonAlign: 'center',
       defaults: {
           height: 30
       },

        items: [{
           boxLabel: 'Déclarer une indisponibilité prévisionnelle',
           labelAlign: 'right',
           xtype: 'checkbox',
           name: 'activationImmediate',
           value: true,
           width: 280
        }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        hidden: true,
        itemId: 'containDebPrev',
        items: [{
           xtype: 'datefield',
           fieldLabel: 'Date prévisionnelle de début',
           width: 350,
           margin: '0 20 0 0',
           labelWidth: 200,
           labelAlign: 'left',
           name: 'datePrevDebut',
           value: new Date(),
           format: 'd/m/Y'
        }, {
         xtype: 'timefield',
         name: 'timePrevDebut',
         fieldLabel: 'à',
         width: 100,
         labelWidth: 20,
         minValue: "00:00",
         value: new Date(),
         format: 'H:i',
         increment: 15
        }]
        }, {
         xtype: 'fieldcontainer',
         layout: 'hbox',
         itemId: 'containFinPrev',
         hidden: true,
         items: [{
            xtype: 'datefield',
            fieldLabel: 'Date prévisionnelle de Fin',
            width: 350,
            margin: '0 20 0 0',
            labelWidth: 200,
            labelAlign: 'left',
            name: 'datePrevFin',
            value: new Date(),
            format: 'd/m/Y'
            }, {
            xtype: 'timefield',
            name: 'timePrevFin',
            fieldLabel: 'à',
            width: 100,
            labelWidth: 20,
            minValue: "00:00",
            value: new Date(),
            format: 'H:i',
            increment: 15
            }]
        }, {
         xtype: 'fieldcontainer',
         layout: 'hbox',
         itemId: 'containDebReel',
         items: [{
              xtype: 'datefield',
              fieldLabel: 'Date réelle de Début',
              name: 'dateDebut',
              format: 'd/m/Y',
              width: 350,
              labelWidth: 200,
              labelAlign: 'left',
              value: new Date(),
              margin: '0 20 0 0'
         }, {
         xtype: 'timefield',
         name: 'timeDebut',
         fieldLabel: 'à',
         width: 100,
         labelWidth: 20,
         minValue: "00:00",
         value: new Date(),
         format: 'H:i',
         increment: 15
         }]
       }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            fieldLabel: 'Motif',
            width: 470,
            labelWidth: 200,
            xtype: 'textfield',
            name: 'motif',
            labelAlign: 'left'
        }]

    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
         items: [{
           xtype: 'combo',
           displayField: 'numero',
           valueField: 'id',
           forceSelection: true,
           hideTrigger: true,
           minChars: 1,
           mode: 'remote',
           typeAhead: true,
           store: {model: 'Sdis.Remocra.model.Hydrant', autoLoad: true},
           name: 'numHydrant',
           fieldLabel: 'Points d\'eau concernés',
           width: 400,
           labelWidth: 200,
           margin: '0 20 0 0'
           }, {
           xtype: 'button',
           text: 'Ajouter',
           itemId:'addHydrantIndispo'
         }]
    }, {
            xtype: 'fieldcontainer',
            layout: 'hbox',
            items: [{
        xtype: 'gridHydrantIndispos',
        itemId: 'gridHydrantIndispos',
        width: 195,
        height: 100,
        margin: '0 20 0 205',
        hideHeaders: true
        }, {
           xtype: 'button',
           text: 'Retirer',
           itemId:'deleteHydrantIndispo'
         }        ]
    }],

    initComponent: function() {
        this.buttons = [{
            text: 'Valider',
            itemId: 'validHydrantIndispo'
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
