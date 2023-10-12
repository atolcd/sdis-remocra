Ext.require('Ext.data.Model');
Ext.define('Sdis.Remocra.model.RepertoireLieu', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'libelle',
        type: 'string'
    }, {
        name: 'sourceSql',
        type: 'string'
    }, {
        name: 'sourceSqlValeur',
        type: 'string'
    }, {
        name: 'sourceSqlLibelle',
        type: 'string'
    }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../repertoirelieu/records')
    }
});