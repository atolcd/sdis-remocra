Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.crises.bloc.Synchronisation');
Ext.require('Sdis.Remocra.features.crises.bloc.Repertoires');
Ext.require('Sdis.Remocra.features.crises.bloc.ServicesCrise');

Ext.define('Sdis.Remocra.features.crises.bloc.Sources', {

    extend: 'Ext.container.Container',
    id: 'sources',
    alias: 'widget.crise.Sources',
    height: 700,
    padding: 10,
    layout: 'vbox',
    defaults: {
        labelAlign: 'right',
        height: 30
    },
    title:'',
         modal: true,
         bodyPadding: 10,
         minButtonWidth: 100,
         buttonAlign: 'center',
          items: [{
              xtype: 'crise.Synchronisation',
              height:145,
              id:'synchro',
              width: 850
          },{
             xtype: 'crise.Repertoires',
             height:145,
             id:'repertoire',
             width: 850
          },{
              xtype: 'crise.ServicesCrise',
              width:850,
              id:'service',
              height:270

          }
      ]
});