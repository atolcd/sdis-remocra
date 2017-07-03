Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.AnomalieOldeb');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Anomalies', {
    extend: 'Ext.container.Container',
    alias: 'widget.oldeb.anomalies',

    deferredRender: false,

    items: [{
        xtype: 'anomalieOldeb',
        title: '',
        border: false,
        itemId: 'oldebAnomalies'

    } ]
});