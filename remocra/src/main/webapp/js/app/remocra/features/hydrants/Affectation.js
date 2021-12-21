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
            xtype: 'fieldcontainer',
            layout: 'vbox',
            items: [{
               xtype:'textfield',
               name:'nom',
               emptyText: 'Nom de la tournée...',
               width: 220,
               margin: '0 0 0 20',
               allowBlank:false
            },{
                xtype: 'combo',
                queryMode: 'local',
                typeAhead: false,
                id: 'comboOrganisme',
                itemId: 'comboOrganisme',
                emptyText: 'Organisme...',
                store: {
                    model: 'Sdis.Remocra.model.Organisme',
                    remoteSort: true,
                    remoteFilter: false,
                    autoLoad: true,
                    pageSize: 999
                },
                pageSize: false, // bizarrerie ExtJS
                displayField: 'nom',
                valueField: 'id',
                disabled: false,
                name: 'organisme',
                width: 220,
                margin: '0 0 0 20',
                allowBlank: false,
                editable: false,
                validator: function (val) {
                    return val.length > 0;
                }
            }]
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
            queryMode: 'remote',
            typeAhead: true,
            store: Ext.create('Ext.data.Store', {
                model: 'Sdis.Remocra.model.Tournee',
                remoteSort: true,
                remoteFilter: true,
                pageSize: 10,
                sorters: ['affectation', 'nom'],
                filters : [{
                    property: 'reserved',
                    value: 'false'
                }]
            }),
            listConfig: {
                getInnerTpl: function() {
                    return '[{affectation.nom}] {nom}';
                }
            },
            pageSize: true, // bizarrerie ExtJS
            displayField: 'nom',
            valueField: 'id',
            allowBlank: false,
            disabled: true,
            name: 'tournee',
            width: 220,
            minChars: 3
        }]
    }],

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

        var organismesCombo = Ext.getCmp('comboOrganisme');

        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../organismes/organismeetenfants'),
            method: 'GET',
            headers: {
                'Accept' : 'application/json,application/xml',
                'Content-Type' : 'application/json'
            },
            scope: this,
            success: function (response) {
                var jsResp = Ext.decode(response.responseText);
                if(jsResp.success){
                    organismesCombo.store.filter(new Ext.util.Filter({
                        filterFn: function(item) {
                            return jsResp.data.indexOf(item.data.id) != -1;
                        }
                    }));
                }
            }
        });
    }
});