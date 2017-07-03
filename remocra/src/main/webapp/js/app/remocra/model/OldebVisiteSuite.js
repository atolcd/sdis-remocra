Ext.require('Sdis.Remocra.model.TypeOldebSuite');

Ext.define('Sdis.Remocra.model.OldebVisiteSuite', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'visite',
        type: 'fk',
        useNull: true
    }, {
        name: 'nomSuite',
        type: 'string'
    }, {
        name: 'suite',
        type: 'fk',
        useNull: true
    }, {
        name: 'dateSuite',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'observation',
        type: 'string'
    }

    ],
    associations: [{
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.TypeOldebSuite',
        associationKey: 'suite',
        name: 'suite',
        getterName: 'getSuite',
        setterName: 'setSuite',
        persist: true
    } ],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../oldebvisitesuite')
    }
});