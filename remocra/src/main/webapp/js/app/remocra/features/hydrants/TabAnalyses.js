Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.store.RequeteModele');
Ext.define('Sdis.Remocra.features.hydrants.TabAnalyses', {
    extend: 'Ext.Panel',
    alias: 'widget.crHydrantsAnalyses',
    itemId: 'analyses',

       defaults: {
            xtype: 'fieldcontainer',
            layout: 'vbox',
            labelWidth: 120
        },

    initComponent: function() {
     var formPanel = new Ext.form.FormPanel({
        itemId : 'formRequetage',
        defaults : { labelSeparator : '', allowBlank : true, msgTarget : 'side', width: 350,  margin: '10 10 10 10', labelAlign: 'top' },
       items : this.getItems(),
       buttons: this.getButtons(),
       buttonAlign : 'center',
       border : false
     });

        Ext.apply(this, { items : [ formPanel ] });
        this.callParent(arguments);
    },

    getButtons: function(){
    return [{ id : 'idExecButton',
           xtype : 'button',
           text : 'Exécuter',
           disabled : true,
           width : 100,
           style: 'margin-top:20px',
           tooltip : 'Exécute la requête sélectionné'

          }];
    },

    getItems: function(){
                      var  items =  [{
                        name: 'requeteModele',
                        id: 'requeteModele',
                        xtype: 'combo',
                        store: {
                          autoLoad: true,
                          type: 'crRequeteModele',
                          filters : {
                                property: 'categorie',
                                value   : 'POINTDEAU'
                          }
                        },
                        displayField: 'libelle',
                        valueField: 'id',
                        fieldLabel: 'Analyses',
                        labelStyle: 'font-weight: bold;',
                        queryMode: 'local',
                        forceSelection: true
                        },{
                        xtype: 'label',
                        name: 'description',
                        id: 'description',
                        text: ''
                        },{
                          tag: 'div',
                          style:'line-height:1px; font-size: 1px;',
                          margin: '10 10 10 10'
                        }];
                       return items;
    }
});