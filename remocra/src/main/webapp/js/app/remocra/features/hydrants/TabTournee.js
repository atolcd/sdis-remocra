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
            text: 'Forcer à 0%',
            itemId: 'resetTournee',
            disabled: true
        },{
            text: 'Forcer à 100%',
            itemId: 'finaliseTournee',
            disabled: true
        },{
            text: 'Renommer la tournée',
            itemId: 'renameTournee',
            disabled: true
        },{
            text: 'Saisir la visite',
            itemId: 'saisirVisite',
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
        }, 800);

        me.columns = [{
            text: 'Tournée',
            dataIndex: 'nom',
            filterable: true,
            filter: {
                xtype: 'textfield',
                emptyText: 'Nom...',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }
            }
        },{
            text: 'Points d\'eau',
            dataIndex: 'hydrantCount',
            renderer: function(value, metaData, record, rowIdx, colIdx, store, successCallback) {
                Ext.Ajax.request({
                    url: Sdis.Remocra.util.Util.withBaseUrl('../tournees/getHydrantTournee/'+record.internalId),
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
        },{
            text: 'Organisme',
            dataIndex: 'affectation',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                return record.affectationBelongsToInstance.data.nom;
            },
            filter: {
                xtype: 'textfield',
                emptyText: 'Nom...',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }
            }
        }, {
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
