Ext.require('Ext.data.Model');
Ext.define('Sdis.Remocra.model.TypeOldebDebroussaillement', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'actif',
        type: 'boolean'
    }, {
        name: 'code',
        type: 'string'
    }, {
        name: 'nom',
        type: 'string'
    }

    ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebdebroussaillement')
    }
});