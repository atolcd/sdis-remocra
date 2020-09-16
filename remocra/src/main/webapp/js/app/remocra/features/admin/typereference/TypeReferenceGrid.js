
Ext.ns('Sdis.Remocra.features.admin.typereference');

Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.require('Ext.form.field.Date');
Ext.require('Ext.ux.CheckColumn');
Ext.require('Sdis.Remocra.model.util.Util');

Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.model.Organisme');

Ext.require('Sdis.Remocra.network.TypeReferenceStore');


Ext.define('Sdis.Remocra.features.admin.typereference.TypeReferenceGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crAdminTypeReferenceGrid',
    cls: 'remocra-editable',

    border: false, defaults: {border: false},
    style: 'margin-right:15px',
    
    // "Surchageables"
    uniqueConstraints: ['code'],
    modelType: 'Sdis.Remocra.model.TypeReference',
    // Attendus
    store: null,
    //
    
    statics: {
        // Les colonnes sont définies au niveau de la classe pour être réutilisées ultérieurement
        columns: {
            code: {
                header: 'Code',
                dataIndex: 'code',
                field: 'textfield',
                menuDisabled: true,
                flex: 5,
                editor: {xtype: 'textfield',  allowBlank: false}
            }, nom: {
                header : 'Nom',
                dataIndex : 'nom',
                field : 'textfield',
                menuDisabled : true,
                flex: 5,
                editor: {xtype: 'textfield',  allowBlank: false}
            }, actif: {
                header: 'Actif',
                dataIndex : 'actif',
                xtype: 'checkcolumn',
                menuDisabled: true,
                flex: 1, align: 'center',
                editor: {xtype: 'checkbox', cls: 'x-grid-checkheader-editor'},
                // Pour processEvent, on annule le comportement surchargé par CheckColumn : on ne veut pas d'édition "directe"
                processEvent: Ext.grid.column.Column.prototype.processEvent
                //
            }
        }
    },
    constructor: function(config) {
        config = config || {};
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        Ext.applyIf(config, {
            columns: [
                this.statics().columns.code, this.statics().columns.nom, this.statics().columns.actif
            ]
        });
        this.callParent([config]);
        
        if (this.store.getCount()<1) {
            this.store.load();
        }
    },
    
    plugins: [
           Sdis.Remocra.widget.WidgetFactory.createRoweditingPluginCfg(false)
    ],
    tbar:[{
        text: 'Ajouter',
        itemId: 'addBtn',
        tooltip: 'ajouter un nouvel élément',
        iconCls: 'add',
        handler: function() {
            var gripPanel = this.findParentByType('crAdminTypeReferenceGrid');
            var rowEditing = gripPanel.plugins[0];
            if (rowEditing.editing) {
                rowEditing.cancelEdit();
            }

            var r = Ext.create(gripPanel.modelType);

            gripPanel.store.insert(0, r);
            rowEditing.startEdit(0, 0);
        }
    }, {

          text: 'Gérer les contacts',
          tooltip: 'Gérer les contacts de l\'organisme',
          iconCls: 'contactIcon',
          itemId: 'organismeContact',
          hidden: true,
          handler: function() {
              var gridPanel = this.findParentByType('crAdminTypeReferenceGrid');
              var record = gridPanel.getSelectionModel().getSelection();
                  if (record != null && Ext.isArray(record)) {
                      record = record[0];
                         var d = document.createElement('div');
                         var id = "show-contact-"+(++Ext.AbstractComponent.AUTO_ID);
                         d.id = id;
                         document.body.appendChild(d);
                         var vueContacts = window.remocraVue.buildContacts(d, {
                                   id: record.get('id'), title: 'Organisme', nom: record.get('nom')
                         });

                         vueContacts.$options.bus.$on('closed', Ext.bind(function(data) {
                                 vueContacts.$el.remove();
                                 vueContacts.$destroy();
                         }, this));
                  }


          }
    }]
});
