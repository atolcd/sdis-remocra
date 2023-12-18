Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.store.ProfilOrganisme');
Ext.require('Sdis.Remocra.model.ZoneCompetence');

Ext.define('Sdis.Remocra.features.admin.typereference.OrganismeGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminOrganismeGrid',
    
    statics: {
        // Les colonnes sont définies au niveau de la classe pour être réutilisées ailleurs
        columns: {
            emailContact: {
                header: 'Email du contact',
                dataIndex: 'emailContact',
                field: 'textfield',
                menuDisabled: true,
                flex: 5,
                editor: {xtype: 'textfield', vtype: 'emailOrBlank'}
            }
        }
    },
    
    uniqueConstraints: ['code'],
    modelType: 'Sdis.Remocra.model.Organisme',
    store: null, // créé à la construction
    plugins: [
        Sdis.Remocra.widget.WidgetFactory.createRoweditingPluginCfg(false),
        Ext.create('Ext.ux.grid.plugin.HeaderFilters')
 ],
    
    constructor: function(config) {
        config = config || {};
        var deferredApplyFilter = Ext.Function.createBuffered(function() {
            this.headerFilterPlugin.applyFilters();
        }, 600, this);
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                typeRefGridCols.code,
                {
                    header : 'Nom',
                    dataIndex : 'nom',
                    menuDisabled : true,
                    flex: 5,
                    filterable: true,
                    filter: {
                        xtype: 'textfield',
                        hideTrigger: true,
                        listeners: {
                            change: deferredApplyFilter
                        }
                    },
                    editor: {xtype: 'textfield'}
                },
                this.statics().columns.emailContact,
                {
                    header: 'Type Organisme',
                    dataIndex: 'typeOrganismeId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var to = record.getTypeOrganisme();
                        return to?to.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo',itemId: 'typeOrganismeId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crTypeOrganisme',
                            autoLoad: true,
                            remoteFilter: false,
                            remoteSort: true,
                            sorters: [{
                                property: 'nom',
                                direction: 'ASC'
                            }],
                            pageSize: 10
                        },
                        pageSize: true, // bizarrerie ExtJS
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var organismeRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                organismeRecord.setTypeOrganisme(newRec);

                                var profilsCombo = this.editingPlugin.editor.getComponent('profilOrganismeId');
                                var profilsStore = profilsCombo.getStore();
                                
                                var typeOrganismeId = newRec.get('id');
                                if (typeOrganismeId) {
                                    this.filterProfilsAndSetProfilAccordindToOrganisme(typeOrganismeId, profilsCombo, organismeRecord);
                                    var typeOrganismeParent = newRec.get('typeOrganismeParent') ? newRec.get('typeOrganismeParent').id : null;
                                    this.filterOrganismeParent(newRec, typeOrganismeParent);
                                    organismeRecord.setOrganismeParent(null);
                                }

                                var record = this.editingPlugin.context.record;
                                var self = this;
                                var oldValue = record.data.typeOrganismeParent;
                                var newValue = combo.value;

                                if(oldValue !== null && oldValue != newValue && record.data.id){
                                    Ext.Ajax.request({ //Si l'on change le profil d'un organisme, il faut désaffecter les organismes qui l'ont comme parent
                                        url: Sdis.Remocra.util.Util.withBaseUrl('../organismes/nbOrganismesEnfants/'+record.data.id),
                                        method: 'GET',
                                        scope: this,
                                        callback: function(options, success, response) {
                                            var jsResp = Ext.decode(response.responseText);
                                            if(jsResp.success && parseInt(jsResp.message, 10) > 0){
                                                Ext.MessageBox.show({
                                                    title: 'Avertissement',
                                                    msg: "Cet organisme est parent de "+Number(jsResp.message)+" autre(s) organisme(s). Si vous modifiez le type de cet organisme"+
                                                    ", les organismes enfants seront désaffectés.",
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
                                                                url: Sdis.Remocra.util.Util.withBaseUrl('../organismes/removeOrganismeParentForSpecificParent/'),
                                                                method: 'POST',
                                                                scope: this,
                                                                params: {
                                                                    idOrganisme: record.data.id
                                                                },
                                                                callback: function(options, success, response) {
                                                                    record.setOrganismeParent(null);
                                                                    record.save({
                                                                        callback: function(records, operation, success){
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
                            }, scope: this
                        }
                    }
                }, {
                    header: 'Profil',
                    dataIndex: 'profilOrganismeId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var po = record.getProfilOrganisme(); 
                        return po?po.get('nom'):null;
                    }, 
                    editor: {
                        xtype: 'combo', itemId: 'profilOrganismeId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crProfilOrganisme',
                            autoLoad: true,
                            remoteFilter: true,
                            remoteSort: true,
                            sorters: [{
                                property: 'nom',
                                direction: 'ASC'
                            }],
                            pageSize: 10
                        },
                        pageSize: true, // bizarrerie ExtJS
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var organismeRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                organismeRecord.setProfilOrganisme(newRec);
                            }, scope: this
                        }
                    }
                }, {
                    header: 'Zone compétence',
                    dataIndex: 'zoneCompetenceId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var to = record.getZoneCompetence(); 
                        return to?to.get('nom'):null;
                    },
                    editor : {
                        xtype: 'combo',
                        emptyText: 'Nom de la zone...',
                        itemId: 'zoneCompetenceId',
                        valueField: 'id',
                        displayField: 'nom',
                        queryMode: 'remote',
                        minChars: 1,
                        store : {
                            store: 'ajax',
                            remoteFilter: true,
                            model: 'Sdis.Remocra.model.ZoneCompetence',
                            editable: false
                        },
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var organismeRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                organismeRecord.setZoneCompetence(newRec);
                            }, scope: this
                        }
                    }
                },{
                    header: 'Organisme parent',
                    dataIndex: 'organismeParent',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                    // On va chercher le nom de l'organisme
                      if (record.phantom === true
                              && record.dirty === false) {
                          // On n'a pas encore le nom
                          return null;
                      }
                      var to = record.getOrganismeParent();
                      return (to !== null) ?to.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo',
                        emptyText: 'Nom de l\'organisme parent...',
                        itemId: 'organismeParent',
                        valueField: 'id',
                        displayField: 'nom',
                        queryMode: 'local',
                        editable: true,
                        allowBlank: true,
                        forceSelection: true,
                        store: {
                            type: 'Organisme',
                            autoLoad: false,
                            remoteFilter: false,
                            pageSize: 99999,
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
                        pageSize: true,
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var organismeRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                organismeRecord.setOrganismeParent(newRec);

                                // Sélection d'aucun organisme parent
                                if(organismeRecord.organismeParentBelongsToInstance.data.id === null){
                                    organismeRecord.setOrganismeParent(null);
                                    organismeRecord.save();
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
        
        // Paging Toolbar
        this.store = Ext.create('Sdis.Remocra.store.Organisme', {autoLoad: true, autoSync: true});
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Organismes {0} - {1} de {2}',
            emptyMsg: 'Aucun organisme à afficher'
        });
        
        this.callParent([config]);
        
        // Filtrage des profils (cas de la première édition)
        this.editingPlugin.addListener('beforeedit', function(roweditor, e, eOpts){
            var profilsCombo = this.editingPlugin.editor.getComponent('profilOrganismeId');
            var organismeRecord = e.record;
            var typeOrganismeId = organismeRecord.phantom?null:organismeRecord.get('typeOrganismeId');

            this.filterProfilsAndSetProfilAccordindToOrganisme(typeOrganismeId, profilsCombo, organismeRecord);

            var idTypeOrganismeParent = null;
            if(e.record.TypeOrganismeBelongsToInstance != null){
                idTypeOrganismeParent = (e.record.TypeOrganismeBelongsToInstance.get("typeOrganismeParent") !== null
                                        && e.record.TypeOrganismeBelongsToInstance.get("typeOrganismeParent").id !== undefined)
                ? e.record.TypeOrganismeBelongsToInstance.get("typeOrganismeParent").id
                : e.record.TypeOrganismeBelongsToInstance.get("typeOrganismeParent");
            }
            this.filterOrganismeParent(e.record, idTypeOrganismeParent);

        }, this);
    },

    /**
     * Filtre la combo des profils en fonction du type d'organisme et définit le profil.
     *
     * @param typeOrganismeId Type d'organisme concerné (null = tous)
     * @param profilsCombo
     * @param organismeRecord
     */
    filterProfilsAndSetProfilAccordindToOrganisme: function(typeOrganismeId, profilsCombo, organismeRecord) {
        var profilsStore = profilsCombo.getStore();
        // Ecoute chargement pour mise à jour du contenu de la combo profils et du profil de l'organismeRecord
        profilsStore.addListener('load', function(store, records, successful, eOpts) {
            var toBeSelected = null;
            if (this.organismeRecord.phantom===true) {
                // Sélection du premier si l'ancienne valeur n'est pas présente
                toBeSelected = store.first();
            } else {
                // Sélection du premier si l'ancienne valeur n'est pas présente
                var profilId = this.organismeRecord.getProfilOrganisme().get('id');
                var foundProfilIndex = store.find('id', profilId);
                if (foundProfilIndex<0) {
                    toBeSelected = store.first();
                } else {
                    toBeSelected = store.getAt(foundProfilIndex);
                }
            }
            // Sélection dans la combo et définition dans le record
            this.profilsCombo.select(toBeSelected);
            this.organismeRecord.setProfilOrganisme(toBeSelected);
        }, { profilsCombo: profilsCombo, organismeRecord: organismeRecord }, { single: true });

        // Chargement de la combo des profils
        profilsStore.clearFilter(typeOrganismeId); // Evénement uniquement si pas de filtre par la suite
        if (typeOrganismeId) {
            profilsStore.filter([{
                property: "typeOrganismeId",
                value: typeOrganismeId
            }]);
        }
    },

    /**
      * Filtre la combo des organismes parents en fonction du type d'organisme choisi 
      *
      * @param record
      * @param idTypeOrganismeParent l'ID du type d'organisme parent. Si il n'est pas renseigné ou si il n'a pas changé, on le recoit à NULL
      */
    filterOrganismeParent: function(record, idTypeOrganismeParent){
        var organismesCombo = this.editingPlugin.editor.getComponent('organismeParent');
        var organismeId = record.get('id');

        organismesCombo.store.load(); // Refresh après une mise à jour des données

        //Nettoyage des filtres et de la combobox
        organismesCombo.store.clearFilter(true);
        organismesCombo.clearValue();

        // Filtre pour ne pas qu'un organisme ne soit son propre parent
        organismesCombo.store.filter(new Ext.util.Filter({
            filterFn: function(item) {
                return item.data.id != organismeId && item.data.typeOrganismeId === idTypeOrganismeParent;
            }
        }));
    }
});
