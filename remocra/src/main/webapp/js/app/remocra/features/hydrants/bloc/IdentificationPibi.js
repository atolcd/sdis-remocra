Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.IdentificationPibi', {

    extend: 'Ext.form.FieldSet',
    title: 'Identification',
    alias: 'widget.hydrant.identificationpibi',

    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120,
        maxWidth: 400
    },
    
    items: [{
        itemId: 'numeroInterne',
        fieldLabel: 'Numéro interne',
        xtype: 'numberfield',
        name: 'numeroInterne',
        msgTarget: 'under',
        hideTrigger: true,
        allowDecimals : false,
        maxValue: 99999,
        forceSelection: true
    },{
        xtype: 'combo',
        fieldLabel: 'Type',
        name: 'nature',
        store: 'TypeHydrantNaturePibi',
        displayField: 'nom',
        valueField: 'id',
        queryMode: 'local',
        allowBlank: false,
        forceSelection: true
    },{
        xtype: 'combo',
        fieldLabel: 'Diamètre',
        name: 'diametre',
        store: 'TypeHydrantDiametre',
        displayField: 'nom',
        valueField: 'id',
        forceSelection: true,
        queryMode: 'local'
    },{
        xtype: 'textfield',
        fieldLabel: 'Identifiant SCP',
        name : 'numeroSCP'
    }]
});