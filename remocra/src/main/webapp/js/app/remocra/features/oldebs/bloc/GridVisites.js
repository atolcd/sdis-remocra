Ext.require('Ext.selection.CheckboxModel');
Ext.require('Sdis.Remocra.model.OldebVisite');
Ext.require('Ext.grid.Panel');

Ext.define('Sdis.Remocra.features.oldebs.bloc.GridVisites', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.oldeb.GridVisites',

    title: 'Visites',
    id: 'gridVisite',
    frame: true,
    viewConfig: {
        markDirty: false,
        allowBlank: false
    },
    height: 150,

    store: {
        autoLoad: false,
        xtype: 'crOldebVisite',
        model: 'Sdis.Remocra.model.OldebVisite'
    },

    columns: [{
        text: "Date",
        sortable: true,
        dataIndex: 'dateVisite',
        renderer: Ext.util.Format.dateRenderer('d/m/Y')
    }, {
        text: "Agent",
        sortable: true,
        dataIndex: 'agent'
    }, {
        text: "Déb parc.",
        sortable: true,
        dataIndex: 'nomDebParcelle'
    }, {
        text: "Déb. accès",
        sortable: true,
        dataIndex: 'nomDebAcces'
    }, {
        text: "Anomalies",
        sortable: true,
        renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
            return record.typeOldebAnomalies().count();
        }
    }, {
        text: "Avis",
        sortable: true,
        dataIndex: 'nomAvis'
    }, {
        text: "Action à mener",
        flex: 1,
        sortable: true,
        dataIndex: 'nomAction'
    } ],
    columnLines: true,

    // inline buttons
    dockedItems: [{
        xtype: 'toolbar',
        items: [{
            itemId: 'addVisite',
            text: 'Nouvelle visite',
            iconCls: 'add'
        }, '-', {
            itemId: 'removeVisite',
            text: 'supprimer',
            iconCls: 'deleteIcon',
            disabled: true
        } ]
    } ],
    selModel: {
        mode: 'SINGLE',
        allowDeselect: false

    }
});