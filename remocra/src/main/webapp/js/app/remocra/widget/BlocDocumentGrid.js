Ext.define('Sdis.Remocra.widget.BlocDocumentGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crBlocDocumentGrid',
    
    cls: 'blocdocumentgrid',
    
    pageSize: null,
    thematiques: null,
    
    initComponent: function() {
        var filters = [];
        // Thématiques (contenu)
        var thematiquesStr = Ext.Array.from(this.thematiques).join(',');
        if (thematiquesStr) {
            filters.push({
                property : 'thematiqueCodes',
                value : thematiquesStr
            });
        }
        // Profils (contenu)
        var profilsStr = Ext.Array.from(this.profils).join(',');
        if (profilsStr) {
            filters.push({
                property : 'profilCodes',
                value : profilsStr
            });
        }
        
        this.store = Ext.create('Sdis.Remocra.store.BlocDocument', {
            autoLoad: true,
            pageSize: this.pageSize||5,
            filters: filters.length>0?filters:undefined
        });
        var bbar = Ext.create('Sdis.Remocra.widget.LightPaging', {
            store: this.store,
            displayInfo: true,
            displayMsg: '{0} - {1} de {2}',
            emptyMsg: 'Aucun document'
        });
        
        Ext.apply(this, {
            store: this.store,
            bbar: bbar,
            hideHeaders: true, frame: true,
            columns: [ {
                text: 'Titre',
                xtype:'templatecolumn',
                flex: 3,
                tpl: '<a href="'+BASE_URL+'/../telechargement/document/{code}" title="{description}" target="_blank" data-qtip="{titre}">{titre}</a>',
                menuDisabled: true
            }, {
                text: 'Mise à jour',
                dataIndex: 'dateDoc',
                flex: 2,
                xtype: 'datecolumn', format: 'd/m/y',
                menuDisabled: true
            }]
        });
        
        this.callParent(arguments);
    }
});