Ext.define('Sdis.Remocra.model.RequeteModele', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'spatial',
        type: 'boolean'
    }, {
        name: 'categorie',
        type: 'string'
    }, {
        name: 'code',
        type: 'string'
    }, {
        name: 'libelle',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'sourceSql',
        type: 'string'
    }
    ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../requetemodele')
    }
});