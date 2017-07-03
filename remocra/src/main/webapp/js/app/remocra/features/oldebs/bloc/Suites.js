Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.ComboForce');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Suites', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.suites',

    title: 'Suites',
    layout: 'anchor',
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120
    }
});