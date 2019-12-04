Ext.require('Ext.window.Window');
Ext.define('Sdis.Remocra.features.crises.ExportCrise', {
    extend: 'Ext.window.Window',
    alias: 'widget.exportCrise',
    title: 'Exporter la crise',
      modal: true,
      autoScroll    : true,
      width: 400,
      height: 400,
      plain: true,

    buttonAlign : 'center',

    initComponent: function() {
        this.buttons = [{
                   text: 'Valider',
                   itemId: 'validExportCrise'
               },{
                   text: 'Annuler',
                   itemId: 'annulExportCrise',
                   scope: this,
                   handler: function() {
                       this.close();
                   }

               }];
        var formPanel = new Ext.form.FormPanel({
               itemId : 'formExport',
               defaults : { labelSeparator : '', allowBlank : true, msgTarget : 'side', width: 350,  margin: '10 10 10 10', labelAlign: 'top' },
              items : this.getItems(),
              border : false
            });

               Ext.apply(this, { items : [ formPanel ] });
               this.callParent(arguments);
    },

    getItems: function(){
      var  items =  [];
       return items;
    }
});
