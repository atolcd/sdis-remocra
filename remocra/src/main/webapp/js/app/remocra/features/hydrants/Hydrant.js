Ext.require(['Sdis.Remocra.features.hydrants.TabAccesRapide','Sdis.Remocra.features.hydrants.TabTournee','Sdis.Remocra.features.hydrants.TabMap',
        'Sdis.Remocra.features.hydrants.TabHydrant','Sdis.Remocra.features.hydrants.TabIndispo', 'Sdis.Remocra.features.hydrants.TabBlocDocument',
        'Sdis.Remocra.features.hydrants.TabRequetage']);

Ext.define('Sdis.Remocra.features.hydrants.Hydrant', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.crHydrants',

    title: 'Gestion des points d\'eau',
    id: 'pointseau',
    tabPosition: 'top',
    plain: true,
    minTabWidth : 100,
    tabBar: {
        defaults: {
            margin: '0 0 0 8'
        }
    },

    initComponent: function() {
        this.items = [{
            xtype: 'crHydrantsAccesRapide'
        },{
            xtype: 'crHydrantsTournee'
        },{
            xtype: 'crHydrantsHydrant'
        },{
            xtype: 'crHydrantsMap'
        },{
            xtype: 'crHydrantsIndispo'
        }];

        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_ANALYSE_C')) {
            this.items.push({
                xtype: 'crHydrantsRequetage'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('DOCUMENTS_R')) {
            this.items.push({
                xtype: 'crHydrantsBlocDocument'
            });
        }
        this.callParent(arguments);
    },

    // Méthode appelée par le router. On "redirige" vers le contrôleur
    urlChanged: function(desc) {
        this.fireEvent('urlChanged', desc);
    }
});