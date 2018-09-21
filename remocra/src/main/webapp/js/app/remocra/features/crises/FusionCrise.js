Ext.require('Ext.window.Window');
Ext.require('Sdis.Remocra.features.crises.bloc.GridFusionCrise');
Ext.define('Sdis.Remocra.features.crises.FusionCrise', {
    extend: 'Ext.window.Window',
    alias: 'widget.fusionCrise',
    title: 'Fusionner les crises',
      width: 500,
      height: 400,
      modal: true,
      plain: true,
      buttonAlign: 'center',
      items: [{
        xtype:'displayfield',
        itemId: 'msgFusion',
         margin: '20px 0px 0px 10px'

      },{
             xtype: 'gridFusionCrise',
             itemId:'gridFusionCrise',
             height: 150
      },
      {
          xtype: 'fieldcontainer',
          itemId: 'containFusionCrise',
           fieldLabel: 'Date de fusion',
           labelStyle: 'font-weight:bold;',
           margin: '20px, 0px, 0px, 30px',
          items: [{
             xtype: 'datefield',
             fieldLabel: 'Le * ',
             labelWidth: 30,
             labelAlign: 'left',
             name: 'dateFusionCrise',
             value: new Date(),
             maxValue: new Date(),
             format: 'd/m/Y',
             allowBlank: false
          }, {
               xtype: 'timefield',
               name: 'timeFusionCrise',
               fieldLabel: 'à * ',
               labelWidth: 30,
               minValue: "00:00",
               value: new Date(),
               format: 'H:i',
               increment: 15,
               allowBlank: false,
               maxValue: new Date()
          }]
      }
    ],

    initComponent: function() {
        this.buttons = [{
            text: 'Valider',
            itemId: 'validFusion'
        },{
            text: 'Annuler',
            itemId: 'annulFusion',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});
