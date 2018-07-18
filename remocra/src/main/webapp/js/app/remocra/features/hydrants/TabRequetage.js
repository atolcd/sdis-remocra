Ext.require(['Sdis.Remocra.features.hydrants.Recherche', 'Sdis.Remocra.features.hydrants.TabAnalyses']);
Ext.define('Sdis.Remocra.features.hydrants.TabRequetage', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.crHydrantsRequetage',

    title: 'Recherches et analyses',
    itemId: 'requetage',
    width: 700,
    height: 600,
    layout: 'border',
         items: [{
               region:'west',
               xtype: 'crHydrantsAnalyses',
               width: 400
           },{
               region: 'center',     // center region is required, no width/height specified
               xtype: 'crRecherche',
               margins: '5 5 0 0'
           }],
    initComponent: function() {
        this.callParent(arguments);
    }
});

