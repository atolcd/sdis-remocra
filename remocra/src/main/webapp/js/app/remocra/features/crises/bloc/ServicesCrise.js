Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.crises.bloc.Services');
Ext.require('Sdis.Remocra.store.OgcService');
Ext.define('Sdis.Remocra.features.crises.bloc.ServicesCrise', {

    extend: 'Ext.form.FieldSet',
    title: 'Services cartographiques WMS',
    id: 'servicesCrise',
    alias: 'widget.crise.ServicesCrise',
     height: 120,
    layout: 'vbox',
    defaults: {
        labelAlign: 'right'
    },

    items: [ {
          xtype: 'crise.Services',
          itemId: 'gridServices'
      }]
});