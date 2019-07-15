Ext.require('Ext.window.Window');
Ext.define('Sdis.Remocra.features.hydrants.ListerPeiIndispo', {
    extend: 'Ext.window.Window',
    alias: 'widget.listerPeiIndispo',
    width: 250,
       height: 350,
       title: 'Liste des points d\'eau concernés',
       modal: true,
       layout: 'form',
       bodyPadding: 15,
       minButtonWidth: 100,
       buttonAlign: 'center',
       defaults: {
           height: 30
       },

    items:[{
         xtype: 'fieldcontainer',
         layout: 'hbox',
         items: [{
           xtype: 'gridHydrantIndispos',
           itemId:'gridHydrantIndispos',
           width: 215,
          height: 250,
           hideHeaders: true
         }]
    }    ],

    initComponent: function() {
        this.buttons = [{
            text: 'Quitter',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
    }

});
