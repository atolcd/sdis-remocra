Ext.require('Ext.window.Window');
Ext.require('Ext.grid.Panel');
Ext.define('Sdis.Remocra.features.crises.bloc.Parametres', {
    extend: 'Ext.window.Window',
    alias: 'widget.crises.Parametres',

    title: 'Paramètres',
    width: 350,
    modal: true,
    bodyPadding: '10px',
    minButtonWidth: 100,
    buttonAlign: 'center',
      defaults: {
                xtype: 'fieldcontainer',
                layout: 'vbox',
                labelWidth: 120
            },

    initComponent: function() {

       var formPanel = new Ext.form.FormPanel({
            itemId : 'formParametre',
            defaults : { labelSeparator : '', allowBlank : true, msgTarget : 'under', width: 300, labelAlign: 'top' },
           items : this.getItems(),
           buttons: this.getButtons(),
           buttonAlign : 'center',
           border : false
         });
           Ext.apply(this, { items : [ formPanel ] });
                 this.callParent(arguments);
    },

    getButtons: function(){
            return [{text: 'Valider',
            itemId: 'validParameter',
            scope: this,
            handler: this.onOkButton
        }, {
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.fireEvent('cancel');
                this.close();
        }
    } ];
    },
    getItems: function(){
    return [{
        xtype: 'textfield',
        itemId: 'Expression',
        labelAlign: 'top',
        fieldLabel : 'Intervalle d\'exécution du traitement (min)',
        allowBlank : false,
        name: 'Expression'
       }];

    },
    onOkButton: function() {
        this.fireEvent('valid');
    }
});