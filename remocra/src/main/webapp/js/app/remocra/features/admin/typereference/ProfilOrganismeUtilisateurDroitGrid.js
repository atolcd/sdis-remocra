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
                                var droitRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                droitRecord.setProfilOrganisme(newRec);
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
                                var droitRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                droitRecord.setProfilUtilisateur(newRec);
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
                                var droitRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                droitRecord.setProfilDroit(newRec);
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
    }
});
