Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.store.ProfilOrganismeUtilisateurDroit');

Ext.define('Sdis.Remocra.features.admin.typereference.ProfilOrganismeUtilisateurDroitGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminProfilOrganismeUtilisateurDroitGrid',
    
    modelType: 'Sdis.Remocra.model.ProfilOrganismeUtilisateurDroit',
    uniqueConstraints: [],
    store: null,

    constructor: function(config) {
        config = config || {};
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [{
                    header: '',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        return '<i>Quand</i>';
                    }
                }, {
                    header: 'Profil organisme',
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
                        var pd = record.getProfilOrganisme(); 
                        return pd?pd.get('nom'):null;
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
                                var popupdRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                popupdRecord.setProfilOrganisme(newRec);

                                var profilsUtilisateursCombo = this.editingPlugin.editor.getComponent('profilUtilisateurId');
                                // Filtrage des profils
                                var typeOrganismeId = newRec.getTypeOrganisme().get('id');
                                if (typeOrganismeId) {
                                    this.filterPUAndSetPUAccordindToTO(typeOrganismeId, profilsUtilisateursCombo, popupdRecord);
                                }
                            }, scope: this
                        }
                    }
                }, {
                    header: '',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        return '<i>et</i>';
                    }
                }, {
                    header: 'Profil utilisateur',
                    dataIndex: 'profilUtilisateurId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var pd = record.getProfilUtilisateur(); 
                        return pd?pd.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo', itemId: 'profilUtilisateurId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crProfilUtilisateur',
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
                                var popupdRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                popupdRecord.setProfilUtilisateur(newRec);
                            }, scope: this
                        }
                    }
                }, {
                    header: '',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        return '➜';
                    }
                }, {
                    header: 'Groupe de fonctionnalités',
                    dataIndex: 'profilDroitId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var pd = record.getProfilDroit(); 
                        return pd?'<b>'+pd.get('nom')+'</b>':null;
                    },
                    editor: {
                        xtype: 'combo', itemId: 'profilDroitId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crProfilDroit',
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
                                var popupdRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                popupdRecord.setProfilDroit(newRec);
                            }, scope: this
                        }
                    }
                }
            ]
        });
        
        // Paging Toolbar (store partagé)
        this.store = Ext.create('Sdis.Remocra.store.ProfilOrganismeUtilisateurDroit', {
            remoteFilter: true,
            remoteSort: true,
            autoLoad: true,
            autoSync: true,
            pageSize: 20
        });
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Eléments {0} - {1} de {2}',
            emptyMsg: 'Aucun élément à afficher'
        });
        
        this.callParent([config]);

        Sdis.Remocra.network.CurrentUtilisateurStore.getCurrentUtilisateur(this, function(user) {
          // Filtrage des profils (cas de la première édition)
          this.editingPlugin.addListener('beforeedit', function(roweditor, e, eOpts){
              var profilUtilisateursCombo = this.editingPlugin.editor.getComponent('profilUtilisateurId');
              var popupdRecord = e.record;
              var typeOrganismeId = popupdRecord.phantom?null:popupdRecord.getProfilOrganisme().getTypeOrganisme().get('id');
              this.filterPUAndSetPUAccordindToTO(typeOrganismeId, profilUtilisateursCombo, popupdRecord);
          }, this);
        });
    },

    /**
     * Filtre la combo des profils utilisateurs en fonction du type d'organisme du profil organisme et définit le profil utilisateur.
     *
     * @param typeOrganismeId Type d'organisme concerné (null = tous)
     * @param profilUtilisateursCombo
     * @param popupdRecord
     */
    filterPUAndSetPUAccordindToTO: function(typeOrganismeId, profilUtilisateursCombo, popupdRecord) {
        var profilsStore = profilUtilisateursCombo.getStore();
        // Ecoute chargement pour mise à jour du contenu de la combo profils et du profil de l'utilisateur
        profilsStore.addListener('load', function(store, records, successful, eOpts) {
            var toBeSelected = null;
            if (this.popupdRecord.phantom===true) {
                // Sélection du premier si l'ancienne valeur n'est pas présente
                toBeSelected = store.first();
            } else {
                // Sélection du premier si l'ancienne valeur n'est pas présente
                var profilId = this.popupdRecord.ProfilUtilisateurBelongsToInstance && this.popupdRecord.getProfilUtilisateur().get('id');
                var foundProfilIndex = store.find('id', profilId);
                if (foundProfilIndex<0) {
                    toBeSelected = store.first();
                } else {
                    toBeSelected = store.getAt(foundProfilIndex);
                }
            }
            // Sélection dans la combo et définition dans le record
            this.profilUtilisateursCombo.select(toBeSelected);
            this.popupdRecord.setProfilUtilisateur(toBeSelected);
        }, { profilUtilisateursCombo: profilUtilisateursCombo, popupdRecord: popupdRecord }, { single: true });

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
