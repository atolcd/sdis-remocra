Ext.require(['Sdis.Remocra.store.Hydrant']);
Ext.require('Ext.ux.grid.plugin.HeaderFilters');

Ext.define('Sdis.Remocra.features.hydrants.TabHydrant', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crHydrantsHydrant',

    title: 'Points d\'eau',
    itemId: 'hydrants',

    enableColumnResize: false,
    enableColumnMove: false,    
    
    store: 'Hydrant',
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
        },'->',{
            text: 'Supprimer',
            iconCls: 'deleteIcon',
            itemId: 'deleteHydrant',
            disabled: true
        }]
    },{
        xtype: 'pagingtoolbar',
        store: 'Hydrant',
        dock: 'bottom',
        displayInfo: true
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
                if (value != null) {
                    var dateFutur = Ext.Date.add(value, Ext.Date.DAY, cfg.delai_rnvl_reco), now = new Date();
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
                    var dateFutur = Ext.Date.add(value, Ext.Date.DAY, cfg.delai_rnvl_ctrl), now = new Date();
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
            }
        }];

        me.callParent(arguments);
    }
});