Ext.require('Sdis.Remocra.features.oldebs.bloc.Entete');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Propriete');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Proprietaires');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Locataire');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Localisation');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Environnement');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Visites');
Ext.require('Sdis.Remocra.features.oldebs.bloc.GridVisites');
Ext.require('Sdis.Remocra.features.oldebs.bloc.DetailsVisites');

Ext.define('Sdis.Remocra.features.oldebs.BaseFiche', {
    extend: 'Ext.window.Window',
    alias: 'widget.oldebsFiche',
    modal: true,
    width: 900,
    layout: 'fit',
    title: 'A REDEFINIR',
    bodyPadding: 10,
    resizable: false,
    tabItems: [],
    listeners: {
        'resize': function(win, width, height, eOpts) {
            win.center();
        }
    },
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
                xtype: 'oldebs.entete',
                padding: 10
            }, {
                xtype: 'tabpanel',
                plain: true,
                flex: 1,
                defaults: {
                    xtype: 'container',
                    padding: 10
                },
                items: this.tabItems
            } ]
        } ];

        this.tbar = [{
            xtype: 'displayfield',
            cls: 'hydrant-msg-error',
            value: 'Un petit message d\'erreur ...',
            name: 'errorMsg',
            hidden: true
        } ];

        this.buttons = [{
            itemId: 'print',
            text: 'Exporter la fiche',
            margin: '0 620 0 0'

        }, {
            text: 'Valider',
            // disabled: true,
            itemId: 'ok'

        }, {
            itemId: 'close',
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.close();
                var grid = Ext.ComponentQuery.query('#obligation')[0];
                grid.getStore().load();
                var map = Ext.ComponentQuery.query('#localisation')[0];
                if (map.isVisible()) {
                    map.refreshZonesLayer();
                    map.workingLayer.removeAllFeatures();

                }

            }

        } ];

        this.callParent(arguments);
    }

});