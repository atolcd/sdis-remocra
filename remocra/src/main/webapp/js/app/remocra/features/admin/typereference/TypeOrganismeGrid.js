Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.store.TypeOrganisme');
Ext.require('Sdis.Remocra.model.TypeOrganisme');

Ext.define('Sdis.Remocra.features.admin.typereference.TypeOrganismeGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminTypeOrganismeGrid',
    
    modelType: 'Sdis.Remocra.model.TypeOrganisme',
    store: null,

    constructor: function(config) {
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                typeRefGridCols.code,
                typeRefGridCols.nom,
                {
                    header: 'Type parent',
                    dataIndex: 'typeOrganismeParent',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.phantom === true && record.dirty === false) {
                            return null;
                        }
                        try{
                            return record.getTypeOrganismeParent().get('nom');
                        }catch(error){
                            return null;
                        }
                    },
                    editor: {
                        xtype: 'combo',itemId: 'typeOrganismeParent',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        forceSelection: true,
                        allowBlank: true,
                        store: {
                            type: 'crTypeOrganisme',
                            autoLoad: true,
                            remoteFilter: false,
                            remoteSort: true,
                            sorters: [{
                                property: 'nom',
                                direction: 'ASC'
                            }],
                            listeners:
                            {
                                load: function(store, records)
                                {
                                    store.insert(0, [{
                                        id: null,
                                        nom: "[Aucune sélection]"
                                    }]);
                                }
                            }
                        },
                        pageSize: true, // bizarrerie ExtJS
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var typeOrganisme = this.editingPlugin.context.record;
                                var newRec = records[0];
                                typeOrganisme.setTypeOrganismeParent(newRec);

                                var record = this.editingPlugin.context.record;
                                var self = this;
                                var oldValue = record.data.typeOrganismeParent;
                                var newValue = combo.value;

                                // Changement de type d'organisme parent
                                if(oldValue !== null && oldValue != newValue){
                                    Ext.Ajax.request({
                                        url: Sdis.Remocra.util.Util.withBaseUrl('../typeorganisme/nbOrganismesAvecParentEtProfilSpecifique/'+record.data.id),
                                        method: 'GET',
                                        scope: this,
                                        callback: function(options, success, response) {
                                            var jsResp = Ext.decode(response.responseText);
                                            if(jsResp.success && parseInt(jsResp.message, 10) > 0){
                                                Ext.MessageBox.show({
                                                    title: 'Avertissement',
                                                    msg: Number(jsResp.message)+" organismes avec ce profil ont déjà un organisme parent configuré. "+
                                                    "Si vous modifiez le type d'organisme parent, les parents de ces organismes seront désaffectés",
                                                    buttons: Ext.Msg.YESNO,
                                                    buttonText: {
                                                        yes: 'Continuer',
                                                        no: 'Annuler'
                                                    },
                                                    fn: function(text, btn){
                                                        if(text == 'no'){ // Annulation
                                                            combo.setRawValue(oldValue);
                                                            self.editingPlugin.cancelEdit();
                                                            self.editingPlugin.context.store.reload();
                                                        }
                                                        else{ // Désaffectation des organismes parents pour les organismes de ce type
                                                            Ext.Ajax.request({
                                                                url: Sdis.Remocra.util.Util.withBaseUrl('../organismes/removeOrganismeParentForSpecificType/'),
                                                                method: 'POST',
                                                                scope: this,
                                                                params: {
                                                                    idTypeOrganisme: record.data.id
                                                                },
                                                                callback: function(options, success, response) {
                                                                    if(newValue === null){
                                                                        record.setTypeOrganismeParent(null);
                                                                    }

                                                                    record.save({
                                                                        callback: function(records, operation, success){
                                                                        combo.clearValue();
                                                                        combo.collapse();
                                                                        self.editingPlugin.context.store.reload();
                                                                       }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    },
                                                    icon: Ext.MessageBox.WARNING
                                                });
                                            }
                                        }
                                    });
                                }
                                else if(record.typeOrganismeParentBelongsToInstance.data.id === null){
                                    record.setTypeOrganismeParent(null);
                                    record.save();
                                    combo.clearValue();
                                    combo.collapse();
                                }
                            }, scope: this
                        }
                    }
                },
                typeRefGridCols.actif
            ]
        });
        // Paging Toolbar (store partagé)
        this.store = Ext.create('Sdis.Remocra.store.TypeOrganisme', {
            remoteFilter: true,
            remoteSort: true,
            autoLoad: true,
            autoSync: true,
            pageSize: 20
        });
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Types d\'organismes {0} - {1} de {2}',
            emptyMsg: 'Aucun types d\'organismes à afficher'
        });

        this.callParent([config]);

        this.editingPlugin.addListener('beforeedit', function(roweditor, e, eOpts){
            var idTypeOrganisme = e.record.get('id');
            var typesCombo = this.editingPlugin.editor.getComponent('typeOrganismeParent');

            //On retire les filtres actifs, puis on filtre le store pour empêcher qu'un type d'organisme ne soit son propre parent
            typesCombo.store.clearFilter(true);
            typesCombo.clearValue();
            typesCombo.store.filter(new Ext.util.Filter({
                filterFn: function(item) {
                    return item.data.id != idTypeOrganisme;
                }
            }));


        }, this);
    }
});
