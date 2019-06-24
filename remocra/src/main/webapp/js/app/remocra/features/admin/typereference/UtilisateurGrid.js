Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.store.ProfilUtilisateur');
Ext.require('Ext.ux.grid.plugin.HeaderFilters');

Ext.define('Sdis.Remocra.features.admin.typereference.UtilisateurGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminUtilisateurGrid',

    statics: {
        // Les colonnes sont définies au niveau de la classe pour être réutilisées ailleurs
        columns: {
            email: {
                header: 'Email',
                dataIndex: 'email',
                field: 'textfield',
                menuDisabled: true,
                flex: 5,
                editor: {xtype: 'textfield', vtype: 'email', allowBlank: false}
            }, prenom: {
                header : 'Prénom',
                dataIndex : 'prenom',
                field : 'textfield',
                menuDisabled : true,
                flex: 5,
                editor: {xtype: 'textfield'}
            }, telephone: {
                header : 'Téléphone',
                dataIndex : 'telephone',
                field : 'textfield',
                menuDisabled : true,
                flex: 5,
                editor: {xtype: 'textfield'/*,  vtype: 'telephone' TODO voir si on met un xtype*/}
            }, messageRemocra: {
                header: '<span data-qtip="Recevoir une copie des messages envoyés avec Remocra">Copie Remocra</span>',
                dataIndex : 'messageRemocra',
                xtype: 'checkcolumn',
                menuDisabled: true,
                flex: 1, align: 'center',
                editor: {xtype: 'checkbox', cls: 'x-grid-checkheader-editor'},
                // Pour processEvent, on annule le comportement surchargé par CheckColumn : on ne veut pas d'édition "directe"
                processEvent: Ext.grid.column.Column.prototype.processEvent
            }
        }
    },
    
    uniqueConstraints: ['identifiant'],
    modelType: 'Sdis.Remocra.model.Utilisateur',
    store: null, // créé à la construction
    
    plugins: [
              Sdis.Remocra.widget.WidgetFactory.createRoweditingPluginCfg(false),
              Ext.create('Ext.ux.grid.plugin.HeaderFilters')
       ],

    constructor: function(config) {
        config = config || {};
        var deferredApplyFilter = Ext.Function.createBuffered(function() {
            this.headerFilterPlugin.applyFilters();
        }, 200, this);
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                this.statics().columns.email,
                {
                    header : 'Identifiant',
                    dataIndex : 'identifiant',
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
                this.statics().columns.prenom,
                this.statics().columns.telephone,
                this.statics().columns.messageRemocra,
                {
                    header: 'Organisme',
                    dataIndex: 'organismeId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var org = record.getOrganisme(); 
                        return org?org.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo',
                        emptyText: 'Organisme...',
                        itemId: 'organismeId',
                        queryMode: 'remote', 
                        valueField: 'id', 
                        displayField: 'nom',
                        minChars: 1,
                        store : Ext.create('Sdis.Remocra.store.Organisme', {
                            autoLoad: true,
                            pageSize: 20
                        }),
                        pageSize: true,
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var utilisateurRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                utilisateurRecord.setOrganisme(newRec);

                                var profilsCombo = this.editingPlugin.editor.getComponent('profilUtilisateurId');
                                var profilsStore = profilsCombo.getStore();
                                
                                // Filtrage des profils
                                var typeOrganismeId = newRec.getTypeOrganisme().get('id');
                                if (typeOrganismeId) {
                                    this.filterProfilsAndSetProfilAccordindToOrganisme(typeOrganismeId, profilsCombo, utilisateurRecord);
                                }
                            }, scope: this
                        }
                    }
                }, {
                    header: 'Profil',
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
                        var pu = record.getProfilUtilisateur(); 
                        return pu?pu.get('nom'):null;
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
                                var utilisateurRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                utilisateurRecord.setProfilUtilisateur(newRec);
                            }, scope: this
                        }
                    }
                }, {
                    header: 'Groupe de<br/>fonctionnalités',
                    dataIndex: 'groupeFnct',
                    menuDisabled: true,
                    flex: 4, align: 'center',
                    renderer: function(value, metadata, record, rowIndex, colIndex) {
                        metadata.tdCls = 'grid-cell-groupe-fnct';
                        var groupeFnct = record.get('groupeFnct');
                        var parts = /^(.*) \((.*)\)$/gm.exec(groupeFnct);
                        if (parts==null || parts.length<3) {
                            metadata.tdCls += ' grid-cell-groupe-fnct-warning';
                            return '∅';
                        }
                        metadata.tdAttr = 'data-qtip="Groupe de fonctionnalités : ' + parts[1]
                            + '<br/>Feuille de style GeoServer : ' + parts[2]+'"';
                        return parts[1];
                    }
                },
                typeRefGridCols.actif
            ]
        });

        this.store = Ext.create('Sdis.Remocra.store.Utilisateur', {autoLoad: true, autoSync: true, pageSize: 20});
                // Paging Toolbar1

        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Utilisateurs {0} - {1} de {2}',
            emptyMsg: 'Aucun utilisateur à afficher'
        });

        this.callParent([config]);
        Sdis.Remocra.network.CurrentUtilisateurStore.getCurrentUtilisateur(this, function(user) {
        // Filtrage des profils (cas de la première édition)
          this.editingPlugin.addListener('beforeedit', function(roweditor, e, eOpts){
            var profilsCombo = this.editingPlugin.editor.getComponent('profilUtilisateurId');
            var utilisateurRecord = e.record;
            var typeOrganismeId = utilisateurRecord.phantom?null:utilisateurRecord.getOrganisme().get('typeOrganismeId');
            this.filterProfilsAndSetProfilAccordindToOrganisme(typeOrganismeId, profilsCombo, utilisateurRecord);
        // Filtrage des organismes par rapport aux droits
            if(!Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ALL_C')) {
            var organismesCombo = this.editingPlugin.editor.getComponent('organismeId');
            var organismeValue = user.getOrganisme().get('id');
            organismesCombo.getStore().clearFilter(true);
            organismesCombo.getStore().filter([{property: 'id', value: organismeValue}]);
            }
          }, this);
        });
    },

    /**
     * Filtre la combo des profils en fonction du type d'organisme et définit le profil.
     *
     * @param typeOrganismeId Type d'organisme concerné (null = tous)
     * @param profilsCombo
     * @param utilisateurRecord
     */
    filterProfilsAndSetProfilAccordindToOrganisme: function(typeOrganismeId, profilsCombo, utilisateurRecord) {
        var profilsStore = profilsCombo.getStore();
        // Ecoute chargement pour mise à jour du contenu de la combo profils et du profil de l'utilisateur
        profilsStore.addListener('load', function(store, records, successful, eOpts) {
            var toBeSelected = null;
            if (this.utilisateurRecord.phantom===true) {
                // Sélection du premier si l'ancienne valeur n'est pas présente
                toBeSelected = store.first();
            } else {
                // Sélection du premier si l'ancienne valeur n'est pas présente
                var profilId = this.utilisateurRecord.getProfilUtilisateur().get('id');
                var foundProfilIndex = store.find('id', profilId);
                if (foundProfilIndex<0) {
                    toBeSelected = store.first();
                } else {
                    toBeSelected = store.getAt(foundProfilIndex);
                }
            }
            // Sélection dans la combo et définition dans le record
            this.profilsCombo.select(toBeSelected);
            this.utilisateurRecord.setProfilUtilisateur(toBeSelected);
        }, { profilsCombo: profilsCombo, utilisateurRecord: utilisateurRecord }, { single: true });

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
