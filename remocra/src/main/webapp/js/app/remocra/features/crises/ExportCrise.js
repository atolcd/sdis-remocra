Ext.require('Ext.window.Window');
Ext.define('Sdis.Remocra.features.crises.ExportCrise', {
    extend: 'Ext.window.Window',
    alias: 'widget.exportCrise',
    title: 'Exporter la crise',
      width: 300,
      height: 250,
      modal: true,
      plain: true,
      buttonAlign: 'center',
      items: [{
             xtype: 'combo',
             store: new Ext.data.SimpleStore({
                       fields: ['id', 'format' ],
                       data: [['0', 'HTML' ], ['1', 'CSV' ] ]
             }),
             displayField: 'format',
             valueField: 'id',
             forceSelection: true,
             width: 200,
             value: '0',
             minChars: 1,
             mode: 'remote',
             margin: '30 0 10 0',
             typeAhead: true,
             //store: {model: 'Sdis.Remocra.model.Hydrant', autoLoad: true},
             name: 'typeExport',
             fieldLabel: 'Format',
             labelAlign: 'right'
      },{
             boxLabel: 'Exporter des documents',
             labelAlign: 'right',
             margin: '20 0 0 55',
             xtype: 'checkbox',
             name: 'exportDocuments',
             value: 'exportDocuments',
             width: 280
      }
    ],

    initComponent: function() {
        this.buttons = [{
            text: 'Valider',
            itemId: 'validCrise'
        },{
            text: 'Annuler',
            itemId: 'annulCrise',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});
