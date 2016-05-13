Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.store.TypeOrganisme');

Ext.define('Sdis.Remocra.features.admin.typereference.TypeOrganismeGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminTypeOrganismeGrid',
    
    modelType: 'Sdis.Remocra.model.TypeOrganisme',
    store: null,

    constructor: function(config) {
        
        // Paging Toolbar (store partagé)
        this.store = Ext.create('Sdis.Remocra.store.TypeOrganisme', {
            remoteFilter: true,
            remoteSort: true,
            autoLoad: true,
            autoSync: true,
            pageSize: 20
        });
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Types d\'organismes {0} - {1} de {2}',
            emptyMsg: 'Aucun types d\'organismes à afficher'
        });
        
        this.callParent([config]);
    }
});
