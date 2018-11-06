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
        },'->',{
            text: 'Supprimer',
            iconCls: 'deleteIcon',
            itemId: 'deleteIndispo',
            disabled: true
        }        ]
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
            text: 'Date prévisionnelle de début',
            align : 'center',
            dataIndex: 'datePrevDebut',
            xtype: 'datecolumn',
            format:'d/m/y'+' à '+'H:i:s'

        },{
            text: 'Date prévisionnelle de fin',
            align : 'center',
            dataIndex: 'datePrevFin',
            xtype: 'datecolumn',
            format:'d/m/y'+' à '+'H:i:s'
        },{
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
                               store.add(Ext.create('Sdis.Remocra.model.TypeHydrantIndispoStatut', {id: null, nom: ' Tous'}));
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
             sortable: false
        },{
            text: 'Points d\'eau',
            align : 'center',
            dataIndex: 'countHydrant',
            sortable: false
        }];

        this.callParent(arguments);
    }
});
