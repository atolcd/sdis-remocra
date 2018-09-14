Ext.require('Ext.ux.grid.plugin.HeaderFilters');

Ext.define('Sdis.Remocra.features.hydrants.TabTournee', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crHydrantsTournee',

    title: 'Tournées',
    itemId: 'tournees',

    store: 'Tournee',

    enableColumnResize: false,
    enableColumnMove: false,
    
    height: 500,
    forceFit: true,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Localiser',
            itemId: 'locateTournee',
            iconCls: 'zoomIcon',
            disabled: true
        },{
            text: 'Lister les points d\'eau',
            itemId: 'showHydrant',
            disabled: true,
            iconCls: 'edit-infoIcon'
        },{
            text: 'Annuler la réservation',
            itemId: 'cancelReservation',
            disabled: true
        },{
            text: 'Réinitialiser l\'état à 0%',
            itemId: 'resetTournee',
            disabled: true
        },'->',{
            text: 'Supprimer',
            iconCls: 'deleteIcon',
            itemId: 'deleteTournee',
            disabled: true
        }]
    },{
        xtype: 'pagingtoolbar',
        store: 'Tournee',
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
            text: 'Tournée',
            dataIndex: 'nom',
            filterable: true,
            filter: {
                xtype: 'textfield',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }
            }
        },{
            text: 'Points d\'eau',
            dataIndex: 'hydrantCount'
        },{
            text: 'Synchronisé',
            xtype: 'datecolumn',
            format: 'd/m/Y',
            dataIndex: 'lastSync'
        },{
            text: 'Etat %',
            dataIndex: 'etat',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                if (value != null) {
                    if (value == 100) {
                        metaData.tdCls = "tourneeFull";
                    } else {
                        metaData.tdCls = "tourneeNotFull";
                    }
                }
                return value;
            }
        },{
            text: 'Reservée',
            dataIndex: 'reservation',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                return value != null ? 'oui': 'non';
            }
        }];

        this.callParent(arguments);
    }
});
