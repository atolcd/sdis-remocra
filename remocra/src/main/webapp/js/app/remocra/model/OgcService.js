Ext.require('Sdis.Remocra.model.OgcCouche');
Ext.define('Sdis.Remocra.model.OgcService', {
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
        name: 'description',
        type: 'string'
    }
    ],
    associations: [{
            type: 'hasMany',
            model: 'Sdis.Remocra.model.OgcCouche',
            associationKey: 'ogcCouches',
            name: 'ogcCouches',
            persist: false
        } ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../ogcservice')
    }
});