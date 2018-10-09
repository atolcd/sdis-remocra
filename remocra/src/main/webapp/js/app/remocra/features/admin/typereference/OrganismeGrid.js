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
    
    constructor: function(config) {
        config = config || {};
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                typeRefGridCols.code,
                typeRefGridCols.nom,
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
    }
});
