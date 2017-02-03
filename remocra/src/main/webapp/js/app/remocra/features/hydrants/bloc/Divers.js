Ext.require('Ext.form.FieldSet');
Ext.require('Ext.form.Panel');
Ext.require('Ext.form.field.File');
Ext.require('Sdis.Remocra.widget.LinkButton');

Ext.define('Sdis.Remocra.features.hydrants.bloc.Divers', {
    extend: 'Ext.form.FieldSet',
    title: 'Divers',
    alias: 'widget.hydrant.divers',

    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 200,
        maxWidth: 500
    },

    items: [ {
        fieldLabel: 'Courrier/convention',
        xtype: 'textfield',
        name: 'courrier'
    }, {
        fieldLabel: 'Date de dernière attestation',
        xtype: 'datefield',
        format: 'd/m/Y',
        name: 'dateAttestation'
    } ]
});