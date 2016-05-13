Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.IdentificationPena', {

    extend: 'Ext.form.FieldSet',
    title: 'Identification',
    alias: 'widget.hydrant.identificationpena',

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
        fieldLabel: 'Nature',
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'combo',
            name: 'nature',
            store: 'TypeHydrantNaturePena',
            displayField: 'nom',
            valueField: 'id',
            queryMode: 'local',
            allowBlank: false,
            forceSelection: true
        },{
            fieldLabel: 'HBE',
            xtype: 'checkbox',
            name: 'hbe',
            labelWidth: 80,
            flex: 1,
            labelAlign: 'right',
            maxWidth: 420
        }]
    }]
});