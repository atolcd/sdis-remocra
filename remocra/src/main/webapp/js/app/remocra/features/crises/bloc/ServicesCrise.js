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

    items: [ /*{
           xtype : 'combo',
           fieldLabel : 'Service',
           labelWidth: 100,
           width: 700,
           name:'choixServices',
           store : {
           type : 'crOgcService',
           autoLoad: true
           },
           displayField : 'nom',
           valueField : 'id',
           emptyText: 'Veuillez séléctionner un service..',
           forceSelection : true,
           editable : false,
           itemId: 'comboServices'
        },*/{
          xtype: 'crise.Services',
          itemId: 'gridServices'
      }]
});