Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.MCOPibi', {

    extend: 'Ext.form.FieldSet',
    title: 'Elément de MCO',
    alias: 'widget.hydrant.mcopibi',

    items: [{
        xtype: 'container',
        layout: 'hbox',
        defaults: {
            width: 240,
            labelWidth: 70,
            labelAlign: 'right'
        },
        items: [{
            fieldLabel: 'Marque',
            name: 'marque',
            xtype: 'combo',
            store: 'TypeHydrantMarque',
            displayField: 'nom',
            valueField: 'id',
            queryMode: 'local',
            forceSelection: true
        },{
            fieldLabel: 'Modèle',
            name: 'modele',
            xtype: 'combo',
            displayField: 'nom',
            valueField: 'id',
            queryMode: 'local',
            disabled: true,
            forceSelection: true

        },{
            xtype: 'numberfield',
            fieldLabel: 'Année de fabrication',
            labelWidth: 150,
            width: 200,
            name: 'anneeFabrication',
            hideTrigger: true,
            mouseWheelEnabled: false
        }]
    },{
        xtype: 'checkboxfield',
        name: 'choc',
        fieldLabel: 'CHOC',
        width: 240,
        labelWidth: 70,
        labelAlign: 'right'
    }]
});