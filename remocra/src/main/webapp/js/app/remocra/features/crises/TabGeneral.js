Ext.require('Ext.ux.grid.plugin.HeaderFilters');
Ext.require('Sdis.Remocra.store.TypeCriseStatut');
Ext.define('Sdis.Remocra.features.crises.TabGeneral', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crCrisesGeneral',
    itemId: 'general',
    title: 'Général',
    store: {
    type : 'crCrise'
    },

    enableColumnResize: false,
    enableColumnMove: false,
    
    height: 500,
    forceFit: true,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Créer',
            itemId: 'creerCrise',
            iconCls: 'add',
            disabled: true
        },{
            text: 'Ouvrir',
            itemId: 'ouvrirCrise',
            iconCls: 'openIcon',
            disabled: true
        },{
            text: 'Clore',
            iconCls : 'deleteIcon',
            itemId: 'cloreCrise',
            disabled: true
        },{
            text: 'Configurer',
            itemId: 'configureCrise',
            iconCls: 'cogIcon',
            disabled: true
        },{
            text: 'Exporter',
            itemId: 'exportCrise',
            iconCls: 'diskIcon',
            disabled: true
        },{
            text: 'Fusionner',
            itemId: 'fusionneCrise',
            iconCls: 'connectIcon',
            disabled: true
        }]
    },{
        xtype: 'pagingtoolbar',
        //store: {},
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
              text: 'Type de crise',
              dataIndex: 'typeCrise',
              align : 'center',
              renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                      return record.getTypeCrise().get('nom');
              },
              filterable: true,
              filter: {
                  xtype: 'combo',
                  filterName: 'typeCrise',
                  displayField: 'nom',
                  valueField: 'id',
                  queryMode: 'local',
                  typeAhead: true,
                  store: {
                     autoLoad: true,
                     xtype: 'crTypeCrise',
                     model: 'Sdis.Remocra.model.TypeCrise',
                         listeners: {
                             load: function(store, records, successful, opt) {
                                 store.add(Ext.create('Sdis.Remocra.model.TypeCrise', {id: null, nom: ' Tous'}));
                                 store.sort('id', 'ASC');
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
            text: 'Nom de crise',
            align : 'center',
            dataIndex: 'nom',
            // TODO cva retirer (utile pour les tests)
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                return '<a href="http://localhost:8081/#/olmap?hash ='
                    + record.get('id')+'" target="_blank">'+record.get('nom')+"</a>";
            }
        },{
            text: 'Description',
            align : 'center',
            dataIndex: 'description'
        },{
            text: 'Activation de la crise',
            align : 'center',
            dataIndex: 'activation',
            xtype: 'datecolumn',
            format:'d/m/Y à H:i:s'
        },{
            text: 'Clôture de la crise',
            align : 'center',
            dataIndex: 'cloture',
            xtype: 'datecolumn',
            format: 'd/m/Y à H:i:s'
        },{
              text: 'Statut',
              dataIndex: 'typeCriseStatut',
              align : 'center',
              renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                      return record.getTypeCriseStatut().get('nom');
              },
              filterable: true,
              filter: {
                  xtype: 'combo',
                  filterName: 'typeCriseStatut',
                  displayField: 'nom',
                  valueField: 'id',
                  queryMode: 'local',
                  typeAhead: true,
                  store: {
                     autoLoad: true,
                     xtype: 'crTypeCriseStatut',
                     model: 'Sdis.Remocra.model.TypeCriseStatut',
                         listeners: {
                             load: function(store, records, successful, opt) {
                                 store.add(Ext.create('Sdis.Remocra.model.TypeCriseStatut', {id: null, nom: ' Tous'}));
                                 store.sort('id', 'ASC');
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
             text: 'Commune',
             align : 'center',
             dataIndex: 'commune',
             sortable: false,
              renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                                  var commune = '';
                                  var communestri = record.communesStore.data.items.sort();
                                  Ext.Array.forEach(communestri, function(item){
                                     commune = commune +'- '+item.data.nom + '</br>';
                                   });
                                   metaData.tdAttr ='<div  data-qclass="qtip-commune-crise" data-qtip="' + commune + '"></div';
                                   return record.communesStore.data.items.length;
                           }
        }];

        this.callParent(arguments);
    }
});
