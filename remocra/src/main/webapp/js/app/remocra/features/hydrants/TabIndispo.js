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

    enableColumnResize: false,
    enableColumnMove: false,
    
    height: 500,
    forceFit: true,
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
            format:'d/m/y'+' à '+'H:i:s'
        },{
            text: 'Date de fin',
            align : 'center',
            dataIndex: 'dateFin',
            xtype: 'datecolumn',
            format:'d/m/y'+' à '+'H:i:s'
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
            sortable: false

        },{
            text: 'Motif',
            dataIndex: 'motif',
            align : 'center'
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
             }
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
        }
        ,{
            text: 'Notifier avant <br /> remise en disponibilité',
            align : 'center',
            dataIndex: 'melAvantDispo',
            xtype: 'booleancolumn',
            trueText: 'Oui',
            falseText: 'Non'
        }     
        ,{
            text: 'Points d\'eau',
            align : 'center',
            dataIndex: 'countHydrant',
            sortable: false,
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                Ext.Ajax.request({
                    url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/getHydrantsIndispo/'+record.internalId),
                    method: 'GET',
                    scope: this,
                    async: false,
                    callback: function(param, success, response) {
                        var res = Ext.decode(response.responseText);
                        metaData.tdAttr = 'data-qtip="' + res.message + '"';
                    }
                });
                return value;
            }
        }];

        this.callParent(arguments);
    }
});
