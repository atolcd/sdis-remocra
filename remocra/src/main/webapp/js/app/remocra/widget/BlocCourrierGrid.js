Ext.define('Sdis.Remocra.widget.BlocCourrierGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crBlocCourrierGrid',

    cls: 'bloccourriergrid',

    pageSize: null,

    initComponent: function() {
        var filters = [{
            property : 'accuse',
            value : 'false'
        }];

        this.store = Ext.create('Sdis.Remocra.store.BlocCourrier', {
            autoLoad: true,
            pageSize: this.pageSize||5,
            filters: filters.length>0?filters:undefined
        });
        var bbar = Ext.create('Sdis.Remocra.widget.LightPaging', {
            store: this.store,
            displayInfo: true,
            displayMsg: '{0} - {1} de {2}',
            emptyMsg: 'Aucun courrier'
        });

        Ext.apply(this, {
            store: this.store,
            bbar: bbar,
            hideHeaders: true, frame: true,
            columns: [ {
                text: 'Titre',
                xtype:'templatecolumn',
                flex: 3,
                tpl: '<a href="'+BASE_URL+'/../courrier/getdocumentfromcode/{codeDocument}" title="{description}" target="_blank">{nomDocument}</a>',
                menuDisabled: true
            }, {
                text: 'Date d envoi',
                dataIndex: 'dateDoc',
                flex: 2,
                xtype: 'datecolumn', format: 'd/m/y',
                menuDisabled: true
            }]
        });

        this.callParent(arguments);
    }
});