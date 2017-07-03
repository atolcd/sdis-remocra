Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.ComboForce');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Observations', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.observations',

    border: false,
    defaults: {
        anchor: '100%'
    },

    items: [{
        xtype: 'textarea',
        name: 'oldebObservation',
        height: 200,
        id: 'oldebObservation'
    } ]
});