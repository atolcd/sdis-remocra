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
         name: 'convertedLibelle',
         type: 'string',
            convert: function(newValue, model) {
                return model.get('libelle') + '  (' + model.get('origine')+')';
            }
    }, {
        name: 'code',
        type: 'string',
        useNull: true
    }, {
        name: 'sourceSql',
        type: 'string'
    }, {
        name: 'sourceSqlValeur',
        type: 'string'
    }, {
        name: 'sourceSqlLibelle',
        type: 'string'
    }, {
        name: 'geometrie',
        type: 'string'
    }, {
        name: 'origine',
        type: 'string'
    }

    ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../repertoirelieu')
    }
});