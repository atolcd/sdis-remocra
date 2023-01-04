Ext.require(['Sdis.Remocra.store.HydrantRecord']);
Ext.require('Ext.ux.grid.plugin.HeaderFilters');

Ext.define('Sdis.Remocra.features.hydrants.TabHydrant', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crHydrantsHydrant',

    title: 'Points d\'eau',
    itemId: 'hydrants',

    enableColumnResize: false,
    enableColumnMove: false,    
    
    store: 'HydrantRecord',
    height: 500,
    forceFit: true,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Localiser',
            itemId: 'locateHydrant',
            iconCls: 'zoomIcon',
            disabled: true
        },{
            text: 'Saisir une visite',
            itemId: 'openHydrant',
            tooltip: 'Saisir une visite (réception, contrôle, reconnaissance, vérification)',
            iconCls: 'add',
            disabled: true,
            hidden : true
        },{
            text: 'Ouvrir la fiche',
            itemId: 'openWithoutCtrl',
            tooltip: 'Ouvrir la fiche du point d\'eau',
            iconCls: 'edit-infoIcon',
            disabled: true
        },{
            text: 'Import CTP',
            itemId: 'importCTP',
            tooltip: 'Importer un fichier CTP',
            iconCls: 'add',
            disabled: false
        },{
           text: 'Export CTP',
           itemId: 'exportCTP',
           tooltip: 'export CTP au format Excel',
           iconCls: 'add',
           disabled: false
        },{
           text: 'Effacer les filtres',
           iconCls: 'deleteIcon',
           itemId: 'clearFilters',
           disabled: false
        },
        '->',{
           text: 'Supprimer',
           iconCls: 'deleteIcon',
           itemId: 'deleteHydrant',
           disabled: true
        }]
    },{
        xtype: 'pagingtoolbar',
        store: 'HydrantRecord',
        dock: 'bottom',
        displayInfo: true
    },{

        text:'fiche',
        id:'show-fiche'

    }],

    initComponent: function() {
        var me = this, headerfilter = Ext.create('Ext.ux.grid.plugin.HeaderFilters');
        me.plugins = headerfilter;

        var cfg = Sdis.Remocra.appInstance.configHydrant;

        var deferredApplyFilter = Ext.Function.createBuffered(function() {
            headerfilter.applyFilters();
        }, 200);

        me.columns = [{
            text: 'Point d\'eau',
            dataIndex: 'numero',
            filterable: true,
            filter: {
                emptyText: 'Numéro...',
                xtype: 'textfield',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }
            }
        },{
            text: 'Tournée',
            dataIndex: 'nomTournee',
            filterable: true,
            filter: {
                emptyText: 'Nom...',
                xtype: 'combo',
                filterName: 'tournee',
                displayField: 'nom',
                valueField: 'id',
                minChars: 1,
                queryMode: 'remote',
                typeAhead: true,
                pageSize: 50,
                store: {
                    model: 'Sdis.Remocra.model.Tournee',
                    listeners: {
                        load: function(store, records, successful, operation, eOpts) {
                            // Ajout de l'élément "Toutes" en première position
                            store.insert(0, {id: null, nom:'Toutes'});

                        }
                    }
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
            }
        },{
            text: 'Type',
            dataIndex: 'natureNom',
            filterable: true,
            filter: {
                xtype: 'combo',
                filterName: 'nature',
                displayField: 'nom',
                valueField: 'id',
                queryMode: 'local',
                typeAhead: true,
                store: 'TypeHydrantNatureTous',
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
            }
        },{
            text: 'Prochaine Reconnaissance',
            dataIndex: 'dateReco',
            align: 'center',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                value = (record.data.dateReco != null) ? record.data.dateReco : record.data.dateRecep;
                if (value != null) {
                    var dateFutur = (record.get('codeNatureDeci') === 'PRIVE') ?
                                    Ext.Date.add(value, Ext.Date.DAY, cfg.delai_rnvl_reco_prive) :
                                    Ext.Date.add(value, Ext.Date.DAY, cfg.delai_rnvl_reco_public);
                    var now = new Date();
                    if (dateFutur < now) {
                        metaData.tdCls = "hydrantAlertErr";
                    } else if (dateFutur < Ext.Date.add(now, Ext.Date.DAY, cfg.delai_reco_urgent)) {
                        metaData.tdCls = "hydrantAlertUrgent";
                    } else if (dateFutur < Ext.Date.add(now, Ext.Date.DAY, cfg.delai_reco_warn)) {
                        metaData.tdCls = "hydrantAlertWarn";
                    }
                    return Ext.util.Format.date(dateFutur, 'd/m/Y');
                }
                return '';
            },
            filterable: true,
            filter: {
                xtype: 'combo',
                displayField: 'text',
                valueField: 'id',
                forceSelection: true,
                mode: 'local',
                store: {
                    type: 'array',
                    fields: ['id','text'],
                    data: [[0,'Tous'],[-1,'Date passée'],[1,'< 1 Mois'],[2,'< 2 Mois '],[6,'< 6 Mois'],[12,'< 12 Mois'],[24,'< 24 Mois']]
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
            }
        },{
            text: 'Prochain Contrôle',
            dataIndex: 'dateContr',
            format: 'd/m/Y',
            align: 'center',
            filterable: true,
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                if (value != null) {
                    var dateFutur = (record.get('codeNatureDeci') === 'PRIVE') ?
                                    Ext.Date.add(value, Ext.Date.DAY, cfg.delai_rnvl_ctrl_prive) :
                                    Ext.Date.add(value, Ext.Date.DAY, cfg.delai_rnvl_ctrl_public);
                    var now = new Date();
                    if (dateFutur < now) {
                        metaData.tdCls = "hydrantAlertErr";
                    } else if (dateFutur < Ext.Date.add(now, Ext.Date.DAY, cfg.delai_ctrl_urgent)) {
                        metaData.tdCls = "hydrantAlertUrgent";
                    } else if (dateFutur < Ext.Date.add(now, Ext.Date.DAY, cfg.delai_ctrl_warn)) {
                        metaData.tdCls = "hydrantAlertWarn";
                    }
                    return Ext.util.Format.date(dateFutur, 'd/m/Y');
                }
                return '';
            },
            filter: {
                xtype: 'combo',
                displayField: 'text',
                valueField: 'id',
                forceSelection: true,
                mode: 'local',
                store: {
                    type: 'array',
                    fields: ['id','text'],
                    data: [[0,'Tous'],[-1,'Date passée'],[1,'< 1 Mois'],[2,'< 2 Mois '],[6,'< 6 Mois'],[12,'< 12 Mois'],[24,'< 24 Mois']]
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
            }
        },{
            text: 'Indisponible',
            dataIndex: 'dispoTerrestre',
            align: 'center',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                if (Ext.isEmpty(value) || value == 'DISPO') {
                    return '';
                }
                metaData.tdCls = 'grid-hydrant-' + Ext.util.Format.lowercase(value) + (record.get('indispoTemp') === 0 ?'':' indispo-tmp');
                if (value == 'INDISPO') {
                    return 'Indisponible'+(record.get('indispoTemp') === 0 ?'':' temporairement');
                }
                return 'Non conforme';
            },
            filterable: true,
            filter: {
                xtype: 'combo',
                displayField: 'nom',
                valueField: 'id',
                forceSelection: true,
                mode: 'local',
                typeAhead: true,
                filterName: 'dispoTerrestre',
                values: null,
                store: {
                    type: 'array',
                    fields: ['id','nom'],
                    data: [[null,'Tous'],['INDISPO','Indisponible'],['NON_CONFORME','Non conforme'],['DISPO','Disponible']]
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }

            }

        },{
            text: 'Indisponible HBE',
            dataIndex: 'dispoHbe',
            align: 'center',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                // Les PIBI et les non HBE ne sont pas concernés : classe particulière
                if (record.get('code')=='PIBI' || (record.raw['hbe']!==true)) {
                    metaData.tdCls = 'grid-hydrant-nonconcerne';
                    return 'Non concerné';
                }
                if (Ext.isEmpty(value) || value == 'DISPO') {
                    return '';
                }
                metaData.tdCls = 'grid-hydrant-' + Ext.util.Format.lowercase(value);
                return 'Indisponible HBE';
            },
            filterable: true,
            filter: {
                xtype: 'combo',
                displayField: 'nom',
                valueField: 'id',
                forceSelection: true,
                mode: 'local',
                typeAhead: true,
                filterName: 'dispoHbe',
                values: null,
                store: {
                    type: 'array',
                    fields: ['id','nom'],
                    data: [[null,'Tous'],['INDISPO','Indisponible'],['DISPO','Disponible']]
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }

            }
        },{
            text: 'Commune',
            dataIndex: 'nomCommune',
            filter: {
               xtype: 'combo',
               minChars: 2,
               hideTrigger: false,
               name: 'commune',
               displayField: 'nom',
               valueField: 'id',
               emptyText: 'Commune de...',
               store: {
                   model: 'Sdis.Remocra.model.Commune',
                   proxy: {
                      format: 'json',
                      type: 'rest',
                      headers: {
                          'Accept': 'application/json,application/xml',
                          'Content-Type': 'application/json'
                      },
                      url: Sdis.Remocra.util.Util.withBaseUrl('../communes/nom'),
                      extraParams: {withgeom:false},
                      reader: {
                          type: 'json',
                          root: 'data'
                      }
                   },
                   pageSize: 10,
                   remoteSort: true,
                   remoteFilter: true
               },
                listeners: {
                    change: deferredApplyFilter
                }
            }
        },{
            text: 'Statut',
            dataIndex: 'nomNatureDeci',
            filterable: true,
            filter: {
                xtype: 'combo',
                filterName: 'nomNatureDeci',
                displayField: 'nom',
                valueField: 'id',
                queryMode: 'local',
                typeAhead: true,
                store: 'TypeHydrantNatureDeciTous',
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
            }

        },{
            text: 'Adresse',
            dataIndex: 'adresse',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                if(value !== null){
                    metaData.tdAttr = 'data-qtip="' + value + '"';
                }

                return value;
            },
            filter: {
                emptyText: 'Nom...',
                xtype: 'textfield',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }
            }

        },{
            text: 'Numéro interne',
            dataIndex: 'numeroInterne',
            filterable: true,
            filter: {
                emptyText: 'Numéro...',
                xtype: 'textfield',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }
            }
        }];

        var colonnesOrdonnees = [];
        HYDRANT_COLONNES.forEach(function(colonne){
            me.columns.forEach(function(c){
                if(c.dataIndex == colonne){
                    colonnesOrdonnees.push(c);
                }
            });
        });

        me.columns = colonnesOrdonnees;

        me.callParent(arguments);
    }
});
