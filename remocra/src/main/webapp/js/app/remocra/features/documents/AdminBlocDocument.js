Ext.require('Sdis.Remocra.store.BlocDocument');

Ext.define('Sdis.Remocra.features.documents.AdminBlocDocument', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crAdminBlocDocument',

    title: 'Administration des documents',
    store: {
        type: 'crBlocDocument',
        autoLoad: true,
        pageSize: 15
    },
    
    height: 500,
    forceFit: true,
    
    enableColumnHide: false,
    enableColumnMove: false,
    sortableColumns: false,
    
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        items: [{
            text: 'Nouveau',
            itemId: 'newDocument'
        }, '->', {
            text: 'Supprimer',
            itemId: 'deleteDocument',
            disabled: true
        }]
    }, {
        xtype: 'pagingtoolbar',
        store: null, // Défini sur le rendu de la grille (store partagé avec la grille)
        dock: 'bottom',
        displayInfo: true,
        displayMsg: 'Documents {0} - {1} de {2}',
        emptyMsg: 'Aucun document'
    }],
    
    initComponent: function() {
        var me = this;
        me.columns = [{
            text: 'Identifiant',
            dataIndex: 'id',
            flex: 1,
            sortable: true
        }, {
            text: 'Titre',
            xtype:'templatecolumn',
            flex: 3,
            tpl: '<a href="'+BASE_URL+'/../telechargement/document/{code}" title="{description}" target="_blank">{titre}</a>'
        }, {
            text: 'Thématiques',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                return me.renderThematiques(record);
            },
            flex: 2
        }, {
            text: 'Profils',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                return me.renderProfilDroits(record);
            },
            flex: 2
        }, {
            text: 'Mise à jour',
            dataIndex: 'dateDoc',
            flex: 1,
            xtype: 'datecolumn', format: 'd/m/y',
            sortable: true
        }];
        
        this.callParent(arguments);
    },
    
    renderThematiques: function(record) {
        return record.thematiques().collect('nom').join(', ');
    },
    
    renderProfilDroits: function(record) {
        return record.profilDroits().collect('nom').join(', ');
    }
});