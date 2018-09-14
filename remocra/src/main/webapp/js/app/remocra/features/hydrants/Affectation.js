Ext.require('Ext.window.Window');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');

Ext.define('Sdis.Remocra.features.hydrants.Affectation', {
    extend: 'Ext.window.Window',
    alias: 'widget.affectation',
    width: 500,
    height: 210,
    title: 'Ajouter à la tournée',
    modal: true,
    layout: 'form',
    bodyPadding: 15,
    minButtonWidth: 100,
    buttonAlign: 'center',
    defaults: {
        height: 30
    },
    items: [{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
        xtype: 'radio',
        name: 'choiceTournee',
        boxLabel: 'Nouvelle tournée',
        checked: true,
        inputValue: '1'
       },{
       xtype:'textfield',
       name:'nom',
       emptyText: 'Nom de la tournée...',
       width: 220,
       margin: '0 0 0 20',
       allowBlank:false
      }]
      },{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'radio',
            boxLabel: 'Dernière tournée',
            name: 'choiceTournee',
            inputValue: '2',
            margin: '0 20 0 0',
            disabled: true
        },{
            xtype: 'component',
            html: '&nbsp;',
            margin: '5 0 0 0',
            name: 'lastTournee'
        }]
    },{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'radio',
            boxLabel: 'Tournée existante',
            name: 'choiceTournee',
            inputValue: '3',
            margin: '0 20 0 0'
        },{
            xtype: 'combo',
            mode: 'local',
            store: Ext.create('Ext.data.Store', {
                model: 'Sdis.Remocra.model.Tournee',
                remoteSort: true,
                remoteFilter: true,
                pageSize: 10,
                filters : [{
                    property: 'reserved',
                    value: 'false'
                }]
            }),
            pageSize: true, // bizarrerie ExtJS
            displayField: 'nom',
            valueField: 'id',
            allowBlank: false,
            disabled: true,
            name: 'tournee',
            width: 220
        }]
    }/*,{
        xtype: 'combo',
        mode: 'local',
        store: 'Utilisateur',
        fieldLabel: 'Affecter à',
        displayField: 'prenomNomIdentifiant',
        valueField: 'id',
        allowBlank: false,
        name: 'utilisateur'
    }*/],

    initComponent: function() {
        this.buttons = [{
            text: 'Valider',
            itemId: 'ok'
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