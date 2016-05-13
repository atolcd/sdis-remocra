Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.MCOPena', {

    extend: 'Ext.form.FieldSet',
    title: 'Elément de MCO',
    alias: 'widget.hydrant.mcopena',

    layout: 'hbox',
    defaults: {
        width: 240,
        labelWidth: 70,
        labelAlign: 'right'
    },

    items: [{
        xtype: 'numberfield',
        fieldLabel: 'Année de fabrication',
        labelWidth: 150,
        width: 200,
        name: 'anneeFabrication',
        hideTrigger: true,
        mouseWheelEnabled: false
    }]
});