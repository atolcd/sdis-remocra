Ext.require('Ext.selection.CheckboxModel');
Ext.require('Sdis.Remocra.model.TypeOldebSuite');
Ext.require('Sdis.Remocra.model.OldebVisiteSuite');
Ext.require('Ext.grid.Panel');

Ext.define('Sdis.Remocra.features.oldebs.bloc.GridSuites', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.oldeb.GridSuites',

    id: 'gridSuites',
    frame: true,
    autoHeight: true,

    store: {
        xtype: 'crOldebVisiteSuite',
        autoLoad: false,
        model: 'Sdis.Remocra.model.OldebVisiteSuite'
    },

    columns: [{
        header: 'Date',
        dataIndex: 'dateSuite',
        flex: 0.5,
        renderer: Ext.util.Format.dateRenderer('d/m/Y'),
        editor: {
            xtype: 'datefield',
            allowBlank: false
        }
    }, {
        header: 'Type de suite',
        dataIndex: 'nomSuite',
        flex: 0.5,
        editor: {
            xtype: 'comboforce',
            displayField: 'nom',
            valueField: 'nom',
            name: 'nomSuite',
            id: 'nomSuite',
            queryMode: 'remote',
            allowBlank: false,
            store: Ext.create('Sdis.Remocra.store.TypeOldebSuite', {
                autoLoad: true
            })
        }
    }, {
        header: 'Observation',
        dataIndex: 'observation',
        flex: 1,
        editor: 'textfield'
    } ],
    selType: 'rowmodel',
    plugins: [Ext.create('Ext.grid.plugin.RowEditing', {
        clicksToEdit: 2
    }) ],
    height: 200,
    // inline buttons
    dockedItems: [{
        xtype: 'toolbar',
        items: [{
            itemId: 'addSuite',
            text: 'Nouvelle suite',
            iconCls: 'add'
        }, '-', {
            itemId: 'removeSuite',
            text: 'supprimer',
            iconCls: 'deleteIcon',
            disabled: true
        } ]
    } ]
});