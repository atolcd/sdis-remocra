Ext.require('Ext.ux.grid.plugin.HeaderFilters');
Ext.require('Sdis.Remocra.store.TypeHydrantIndispoStatut');
Ext.require('Sdis.Remocra.model.TypeHydrantIndispoStatut');
Ext.require(['Sdis.Remocra.store.HydrantIndispoTemporaire']);
Ext.define('Sdis.Remocra.features.hydrants.TabIndispo', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crHydrantsIndispo',

    title: 'Indisponibilités temporaires',
    itemId: 'indispos',

    store: {type: 'crIndispos'},

    enableColumnResize: true,
    enableColumnMove: true,
    
    height: 500,
    forceFit: false,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Localiser',
            itemId: 'locateIndispo',
            iconCls: 'zoomIcon',
            disabled: true
        },{
            text: 'Activer',
            itemId: 'activeIndispo',
            disabled: true
        },{
            text: 'Lever',
            itemId: 'leverIndispo',
            disabled: true
        },{
            text: 'Gérer',
            itemId: 'gererIndispo',
            disabled: true
        },{
            text: 'Prolonger',
            itemId: 'prolongerIndispo',
            disabled: true
        },{
            text: 'Lister les points d\'eau',
            itemId: 'listerPeiIndispo',
            disabled: true
        },'->',{
            text: 'Supprimer',
            iconCls: 'deleteIcon',
            itemId: 'deleteIndispo',
            disabled: true
        }]
    },{
        xtype: 'pagingtoolbar',
        store: {type: 'crIndispos'},
        dock: 'bottom',
        displayInfo: true
    }],

    initComponent: function() {

        var me = this, headerfilter = Ext.create('Ext.ux.grid.plugin.HeaderFilters');
        me.plugins = headerfilter;

        var deferredApplyFilter = Ext.Function.createBuffered(function() {
            headerfilter.applyFilters();
        }, 200);

        me.columns = [{
            text: 'Date de début',
            align : 'center',
            dataIndex: 'dateDebut',
            xtype: 'datecolumn',
            format:'d/m/y'+' à '+'H:i:s',
            width: '8%'
        },{
            text: 'Date de fin',
            align : 'center',
            dataIndex: 'dateFin',
            xtype: 'datecolumn',
            format:'d/m/y'+' à '+'H:i:s',
            width: '8%'
        },{
            text: 'Statut',
            dataIndex: 'statut',
            align : 'center',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                    return record.getStatut().get('nom');
            },
            filterable: true,
            filter: {
                xtype: 'combo',
                filterName: 'statut',
                displayField: 'nom',
                valueField: 'id',
                queryMode: 'local',
                typeAhead: true,
                store: {
                   autoLoad: true,
                   xtype: 'crTypeIndispoStatut',
                   model: 'Sdis.Remocra.model.TypeHydrantIndispoStatut',
                       listeners: {
                           load: function(store, records, successful, opt) {
                               store.add(Ext.create('Sdis.Remocra.model.TypeHydrantIndispoStatut', {id: null, nom: 'Tous'}));
                               store.add(Ext.create('Sdis.Remocra.model.TypeHydrantIndispoStatut', {id: -3, nom: 'Planifié / En cours'}));
                               store.sort('id', 'ASC');
                           }
                       }
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
            },
            sortable: false,
            flex: 1
        },{
            text: 'Motif',
            dataIndex: 'motif',
            align : 'center',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + record.raw.motif + '"';
                return value;
            },
            width: '18%',
            flex: 1
        },{
             text: 'Commune',
             align : 'center',
             dataIndex: 'commune',
             sortable: true,
             filterable: true,
             filter: {
                 xtype: 'combo',
                 name: 'commune',
                 displayField: 'nom',
                 valueField: 'id',
                 queryMode: 'remote',
                 triggerAction: "all",
                 typeAhead: true,
                 minChars: 3,
                 store: {
                     storeId: 'communeIndispo',
                     model: 'Sdis.Remocra.model.Commune',
                     pageSize: 10,
                     remoteSort: true,
                     remoteFilter: true,
                     listeners: {
                        load: function(store, records, successful, opt) {
                            store.add(Ext.create('Sdis.Remocra.model.Commune', {id: null, nom: 'Toutes'}));
                        }
                     }
                 },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    }
                }
             },
             flex: 1
        }
        ,{
            text: 'Mise en <br /> indisponibilité automatique',
            align : 'center',
            dataIndex: 'basculeAutoIndispo',
            xtype: 'booleancolumn',
            trueText: 'Oui',
            falseText: 'Non'
        }
        ,{
            text: 'Notifier avant <br /> mise en indisponibilité',
            align : 'center',
            dataIndex: 'melAvantIndispo',
            xtype: 'booleancolumn',
            trueText: 'Oui',
            falseText: 'Non'
        }
        ,{
            text: 'Remise en <br /> disponibilité automatique',
            align : 'center',
            dataIndex: 'basculeAutoDispo',
            xtype: 'booleancolumn',
            trueText: 'Oui',
            falseText: 'Non'
        },{
            text: 'Notifier avant <br /> remise en disponibilité',
            align : 'center',
            dataIndex: 'melAvantDispo',
            xtype: 'booleancolumn',
            trueText: 'Oui',
            falseText: 'Non'
        },{
            text: 'Points d\'eau',
            align : 'center',
            dataIndex: 'totalHydrants',
            sortable: false,
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + record.raw.hydrantsTooltip + '"';
                return value;
            }
        },{
             text: 'Observations',
             dataIndex: 'observation',
             align : 'center',
             renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                 tooltipContent = record.raw.observation != null ? record.raw.observation : '';
                 metaData.tdAttr = 'data-qtip="' + tooltipContent + '"';
                 return value;
             },
             width: '18%',
             flex: 1
        }];

        this.callParent(arguments);
    }
});
