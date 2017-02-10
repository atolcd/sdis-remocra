Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.Citerne', {

    extend: 'Ext.form.FieldSet',
    title: 'Citerne',
    alias: 'widget.hydrant.citerne',

    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120,
        maxWidth: 400
    },

    items: [{
        xtype: 'combo',
        fieldLabel: 'Positionnement',
        name: 'positionnement',
        store: 'TypeHydrantPositionnement',
        displayField: 'nom',
        valueField: 'id',
        forceSelection: true,
        queryMode: 'local'
    },{
        xtype: 'combo',
        fieldLabel: 'Type',
        name: 'materiau',
        store: 'TypeHydrantMateriau',
        displayField: 'nom',
        valueField: 'id',
        forceSelection: true,
        queryMode: 'local'
    },{
        xtype: 'numberfield',
        fieldLabel: 'Q. d\'appoint (m³/h)',
        name: 'QAppoint',
        hideTrigger: true
    },{
        xtype: 'fieldcontainer',
        fieldLabel: 'PI / PA associé',
        items: [{
            name: 'btnPIAssocie',
            xtype: 'button',
            text: 'Associer un PI / PA'
        },{
            name: 'btnPIOpen',
            xtype: 'button',
            text: 'Ouvrir la fiche du PI / PA associé',
            hidden: true
        }]
    }]
});