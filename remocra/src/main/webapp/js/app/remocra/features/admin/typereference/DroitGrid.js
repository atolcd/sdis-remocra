Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.store.TypeDroit');
Ext.require('Sdis.Remocra.store.ProfilDroit');
Ext.require('Sdis.Remocra.store.Droit');

Ext.define('Sdis.Remocra.features.admin.typereference.DroitGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminDroitGrid',
    
    modelType: 'Sdis.Remocra.model.Droit',
    store: null,
    uniqueConstraints: [],

    constructor: function(config) {
        config = config || {};
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                {
                    header: 'Profil de droits',
                    dataIndex: 'profilDroitId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var pd = record.getProfilDroit(); 
                        return pd?pd.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo', itemId: 'profilDroitId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crProfilDroit',
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
                                var droitRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                droitRecord.setProfilDroit(newRec);
                            }, scope: this
                        }
                    }
                }, {
                    header: 'Type de droit',
                    dataIndex: 'typeDroitId',
                    menuDisabled: true,
                    flex: 5, align: 'center',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        // On va chercher le nom de l'organisme
                        if (record.phantom === true/*édition d'un nouveau*/
                                && record.dirty === false/*premier passage (nouveau et pas édition d'un nouveau)*/) {
                            // On n'a pas encore le nom
                            return null;
                        }
                        var td = record.getTypeDroit(); 
                        return td?td.get('nom'):null;
                    },
                    editor: {
                        xtype: 'combo', itemId: 'typeDroitId',
                        queryMode: 'local', valueField: 'id', displayField: 'nom',
                        editable: false,
                        store: {
                            type: 'crTypeDroit',
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
                                var droitRecord = this.editingPlugin.context.record;
                                var newRec = records[0];
                                droitRecord.setTypeDroit(newRec);
                            }, scope: this
                        }
                    }
                }, {
                    header: 'Création',
                    dataIndex : 'droitCreate',
                    xtype: 'checkcolumn',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    editor: {xtype: 'checkbox', cls: 'x-grid-checkheader-editor'},
                    // Pour processEvent, on annule le comportement surchargé par CheckColumn : on ne veut pas d'édition "directe"
                    processEvent: Ext.grid.column.Column.prototype.processEvent
                    //
                },  {
                    header: 'Lecture',
                    dataIndex : 'droitRead',
                    xtype: 'checkcolumn',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    editor: {xtype: 'checkbox', cls: 'x-grid-checkheader-editor'},
                    // Pour processEvent, on annule le comportement surchargé par CheckColumn : on ne veut pas d'édition "directe"
                    processEvent: Ext.grid.column.Column.prototype.processEvent
                    //
                },  {
                    header: 'Mise à jour',
                    dataIndex : 'droitUpdate',
                    xtype: 'checkcolumn',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    editor: {xtype: 'checkbox', cls: 'x-grid-checkheader-editor'},
                    // Pour processEvent, on annule le comportement surchargé par CheckColumn : on ne veut pas d'édition "directe"
                    processEvent: Ext.grid.column.Column.prototype.processEvent
                    //
                },  {
                    header: 'Suppression',
                    dataIndex : 'droitDelete',
                    xtype: 'checkcolumn',
                    menuDisabled: true,
                    flex: 1, align: 'center',
                    editor: {xtype: 'checkbox', cls: 'x-grid-checkheader-editor'},
                    // Pour processEvent, on annule le comportement surchargé par CheckColumn : on ne veut pas d'édition "directe"
                    processEvent: Ext.grid.column.Column.prototype.processEvent
                    //
                }
            ]
        });
        
        // Paging Toolbar (store partagé)
        this.store = Ext.create('Sdis.Remocra.store.Droit', {
            remoteFilter: true,
            remoteSort: true,
            autoLoad: true,
            autoSync: true,
            pageSize: 30
        });
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Droits {0} - {1} de {2}',
            emptyMsg: 'Aucun droit à afficher'
        });
        
        this.callParent([config]);
        
        // Ajout des boutons spécifiques
        var tbar = this.getDockedItems('toolbar[dock="top"]')[0];
        tbar.add({
            itemId: 'operationslots',
            text: 'Réaliser un lot d\'opérations (Profil de droits)',
            tooltip: 'Les opérations en lots sur les droits sont réalisées à partir de l\'interface des profils de droits',
            iconCls: 'cogIcon',
            handler: function() {
                Sdis.Remocra.util.Util.changeHash('admin/index/elt/profildroits');
            },
            scope: this
        });
    }
});
