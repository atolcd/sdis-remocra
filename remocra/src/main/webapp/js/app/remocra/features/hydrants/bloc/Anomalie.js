Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.Anomalie');

Ext.define('Sdis.Remocra.features.hydrants.bloc.Anomalie', {

    extend: 'Ext.container.Container',
    alias: 'widget.hydrant.anomalie',

    layout: 'column',

    items: [{
        columnWidth: 0.50,
        margin: '0 20 0 0',
        xtype: 'container',
        items: [{
            xtype: 'checkbox',
            name: 'allAnomalie',
            boxLabel : 'Afficher toutes les anomalies'
        },{
            id :'hydrantAnomalies',
            xtype: 'anomalie'
        }]
    },{
        columnWidth: 0.45,
        xtype: 'fieldset',
        title: 'Observations',
        layout: 'fit',
        items: [{
            xtype: 'textarea',
            height: 300,
            name: 'observation'
        }]
    }]
});