Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.VerifHydrauliquePibi', {

    extend: 'Ext.form.FieldSet',
    title: 'Vérification hydraulique',
    alias: 'widget.hydrant.verifhydrauliquepibi',

    layout: {
        type: 'table',
        columns: 2
    },
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 150,
        xtype: 'numberfield',
        hideTrigger: true,
        minValue: 0,
        mouseWheelEnabled: false
    },

    items: [{
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : ''
    }, {
        width: 300,
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : '',
        name : 'dateTerrain',
        value : 'Aucun historique',
        fieldStyle : 'font-weight: bold;'
    }, {
        fieldLabel: 'Débit (1 Bar) m³/h',
        name: 'debit',
        allowDecimals: false
    },{
        width: 250,
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : '',
        name: 'debitNM1'
    },{
        xtype: 'displayfield',
        name: 'debit_msg',
        margin: '0 0 0 160',
        colspan: 2
    },{
        fieldLabel: 'Pression statique (bar)',
        name :'pression'
    },{
        width: 250,
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : '',
        name: 'pressionNM1'
    },{
        xtype: 'displayfield',
        name: 'pression_msg',
        margin: '0 0 0 160',
        colspan: 2
    },{
        fieldLabel: 'Débit max (m³/h)',
        name: 'debitMax',
        allowDecimals: false
    },{
        width: 250,
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : '',
        name: 'debitMaxNM1'
    },{
        xtype: 'displayfield',
        name: 'debitMax_msg',
        margin: '0 0 0 160',
        colspan: 2
    },{
        fieldLabel: 'Pression dynamique (bar)',
        name: 'pressionDyn'
    },{
        width: 250,
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : '',
        name: 'pressionDynNM1'
    },{
        xtype: 'displayfield',
        name: 'pressionDyn_msg',
        margin: '0 0 0 160',
        colspan: 2
    }]
});