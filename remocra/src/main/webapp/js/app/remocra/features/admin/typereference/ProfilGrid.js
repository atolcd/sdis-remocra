Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.model.Profil');
Ext.require('Sdis.Remocra.store.TypeOrganisme');

Ext.define('Sdis.Remocra.features.admin.typereference.ProfilGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminProfilGrid',
    
    modelType: 'Sdis.Remocra.model.Profil',
    store: null,
    
    constructor: function(config) {
        config = config || {};
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                typeRefGridCols.code,
                typeRefGridCols.nom,
                {
                    header: 'Type Organisme',
                    dataIndex: 'typeOrganismeId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var to = record.getTypeOrganisme(); 
                        return to?to.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo',itemId: 'typeOrganismeId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crTypeOrganisme',
                            autoLoad: true,
                            remoteFilter: false,
                            remoteSort: true,
                            sorters: [{
                                property: 'nom',
                                direction: 'ASC'
                            }],
                            pageSize: 10
                        },
                        pageSize: true, // bizarrerie ExtJS
                        listeners: {
                            select: function(combo, records, eOpts) {
                                var profilRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                profilRecord.setTypeOrganisme(newRec);
                                //profilRecord.raw.typeOrganisme = {id : newRec.get('id')};
                                profilRecord.data.typeOrganisme = {id : newRec.get('id')};
                            }, scope: this
                        }
                    }
                },
                typeRefGridCols.actif
            ]
        });
        
        this.store = config.store;
        // Paging Toolbar (store partagé)
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Profils {0} - {1} de {2}',
            emptyMsg: 'Aucun profil à afficher'
        });
        
        this.callParent([config]);
    }
});
