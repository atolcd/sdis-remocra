Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.crises.bloc.CommuneCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.ActivationCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.ClotureCrise');
Ext.define('Sdis.Remocra.features.crises.bloc.Generalite', {

    extend: 'Ext.container.Container',
    id: 'generalites',
    alias: 'widget.crise.Generalite',
    cls: 'fichehydrant',
    height: 700,
    padding: 10,
    layout: 'vbox',
    defaults: {
        labelAlign: 'right',
        height: 30
    },
    title:'',
         modal: true,
         minButtonWidth: 100,
         buttonAlign: 'center',
          items: [{
              xtype: 'displayfield',
              cls: 'hydrant-msg-error',
              value: 'Un petit message d\'erreur ...',
              name: 'errorMsg',
              hidden: true

          },{
             xtype: 'fieldcontainer',
             margin: '40 0 0 0',
             layout: 'hbox',
             items:[{
                   xtype: 'combo',
                   displayField: 'nom',
                   valueField: 'id',
                   forceSelection: true,
                   width: 410,
                   minChars: 1,
                   mode: 'remote',
                   typeAhead: true,
                   store: {
                        autoLoad: true,
                        xtype: 'crTypeCrise',
                        model: 'Sdis.Remocra.model.TypeCrise'
                   },
                   name: 'typeCrise',
                   fieldLabel: 'Type * ',
                   labelAlign: 'right',
                   allowBlank: false
             },{
                   fieldLabel: 'Nom * ',
                   width: 410,
                   xtype: 'textfield',
                   name: 'nomCrise',
                   labelAlign: 'right',
                   allowBlank: false

              }]
          },{
                    fieldLabel: 'Description',
                    xtype: 'textarea',
                    height: 120,
                    width: 820,
                    name: 'descriptionCrise',
                    labelAlign: 'right'
          },{
              xtype: 'fieldcontainer',
              layout: 'hbox',
              items: [{
                   xtype: 'fieldcontainer',
                    layout: 'vbox',
                    items: [{
                       xtype: 'crise.Activation',
                       id: 'criseActivation',
                       width: 300,
                       margin: '30 0 0 10'
                    },{
                      xtype: 'crise.Cloture',
                      id: 'criseCloture',
                      width:300,
                      margin: '15 0 0 10'
                    }]
              },{
                xtype: 'displayfield',
                width: 30,
                margin: '30 0 0 0'

              },{
                 xtype: 'communeCrise',
                 width: 480,
                 margin: '30 0 0 0'

              }

              ]

          }
      ]
});