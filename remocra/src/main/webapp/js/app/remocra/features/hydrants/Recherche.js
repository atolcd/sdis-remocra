Ext.require(['Sdis.Remocra.features.hydrants.TabMapRequetage','Sdis.Remocra.features.hydrants.TabDonnees']);
Ext.define('Sdis.Remocra.features.hydrants.Recherche', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.crRecherche',
    id: 'recherche',
    tabPosition: 'top',
    plain: true,
    minTabWidth : 100,
    tabBar: {
        defaults: {
            margin: '0 0 0 8'
        }
    },
    deferredRender: false,
    initComponent: function() {
        this.items = [{
            xtype: 'crHydrantsDonnees',
            title: 'Données'
        },{
            xtype: 'crHydrantsMapRequetage',
            title: 'Carte',
            width: 1250,
            hidden: true
        }];
        this.dockedItems= [{
           xtype: 'toolbar',
           dock: 'top',
           items: ['->',{
            text: 'Réinitialiser la sélection',
            itemId: 'deleteRequete',
            iconCls : 'refresh',
            disabled: true
          },{
             text: 'Exporter la sélection',
             itemId: 'exportRequete',
             iconCls: 'download-atlasIcon',
             disabled: true
          }]
       }];
        this.callParent(arguments);
    }
});