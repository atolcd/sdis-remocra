Ext.require('Ext.data.Model');
Ext.define('Sdis.Remocra.model.OgcCouche', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'nom',
        type: 'string',
        useNull: true
    }, {
        name: 'titre',
        type: 'string',
        useNull: true
    }, {
        name: 'definition',
        type: 'string',
        useNull: true
    }
    , {
        name: 'code',
        type: 'string',
        useNull: true
    }
    ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../ogccouche')
    }
});