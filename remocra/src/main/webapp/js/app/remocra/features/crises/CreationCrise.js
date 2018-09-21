Ext.require('Ext.window.Window');
Ext.require('Sdis.Remocra.features.crises.bloc.Generalite');
Ext.require('Sdis.Remocra.features.crises.bloc.Sources');
Ext.define('Sdis.Remocra.features.crises.CreationCrise', {
    extend: 'Ext.window.Window',
    alias: 'widget.creationCrise',
    title: 'Créer une nouvelle crise',
      width: 900,
      height: 700,
      modal: true,
      items: [
              {xtype: 'form',
              name: 'ficheCreation',
              border: false,
              layout: {
                  type: 'vbox',
                  align: 'stretch'
              },
            items: [{
            xtype: 'tabpanel',
            plain: true,
            items: [{
                title: 'Généralités',
                xtype: 'crise.Generalite'

            },{
                title: 'Sources d\'informations associées',
                xtype: 'crise.Sources'

            }]
            }]

        } ],

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
