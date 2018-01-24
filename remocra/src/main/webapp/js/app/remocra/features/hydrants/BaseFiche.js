Ext.require('Sdis.Remocra.features.hydrants.bloc.Entete');
Ext.require('Sdis.Remocra.features.hydrants.bloc.Tracabilite');
Ext.require('Sdis.Remocra.features.hydrants.bloc.LocalisationPibi');
Ext.require('Sdis.Remocra.features.hydrants.bloc.LocalisationPena');
Ext.require('Sdis.Remocra.features.hydrants.bloc.IdentificationPibi');
Ext.require('Sdis.Remocra.features.hydrants.bloc.IdentificationPena');
Ext.require('Sdis.Remocra.features.hydrants.bloc.Citerne');
Ext.require('Sdis.Remocra.features.hydrants.bloc.GestionnairePibi');
Ext.require('Sdis.Remocra.features.hydrants.bloc.GestionnairePena');
Ext.require('Sdis.Remocra.features.hydrants.bloc.VerifHydrauliquePibi');
Ext.require('Sdis.Remocra.features.hydrants.bloc.VerifHydrauliquePena');
Ext.require('Sdis.Remocra.features.hydrants.bloc.MCOPibi');
Ext.require('Sdis.Remocra.features.hydrants.bloc.MCOPena');
Ext.require('Sdis.Remocra.features.hydrants.bloc.Divers');
Ext.require('Sdis.Remocra.features.hydrants.bloc.Anomalie');

Ext.define('Sdis.Remocra.features.hydrants.BaseFiche', {
    extend: 'Ext.window.Window',
    alias: 'widget.hydrantFiche',
    modal: true,
    width: 900,
    layout: 'fit',

    cls: 'fichehydrant',

    title: 'A REDEFINIR',
    bodyPadding: 10,
    tabItems: [],

    initComponent: function() {
        this.items = [{
            xtype: 'form',
            name: 'fiche',
            border: false,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'hydrant.entete'
            },{
                xtype: 'tabpanel',
                plain: true,
                flex: 1,
                defaults: {
                    xtype: 'container',
                    padding: 10
                },
                deferredRender: false,
                items: this.tabItems
            }]
        }];

        this.tbar = [{
            xtype: 'displayfield',
            cls: 'hydrant-msg-error',
            value: 'Un petit message d\'erreur ...',
            name: 'errorMsg',
            hidden: true
        }];

        this.buttons = [{
            text: 'Valider',
            // disabled: true,
            itemId: 'ok'
        },{
            itemId: 'close',
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});