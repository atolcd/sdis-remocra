Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.OldebVisiteSuite');
Ext.require('Sdis.Remocra.model.TypeOldebAnomalie');
Ext.define('Sdis.Remocra.model.OldebVisite', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'code',
        type: 'string'
    }, {
        name: 'dateVisite',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'agent',
        type: 'string'
    }, {
        name: 'observation',
        type: 'string'
    }, {
        name: 'nomAvis',
        type: 'string'
    }, {
        name: 'nomAction',
        type: 'string'
    }, {
        name: 'nomDebAcces',
        type: 'string'
    }, {
        name: 'nomDebParcelle',
        type: 'string'
    }, {
        name: 'totalAnomalies',
        type: 'string'
    }, {
        name: 'oldeb',
        type: 'fk',
        useNull: true
    }, {
        name: 'debroussaillementParcelle',
        type: 'fk',
        useNull: true
    }, {
        name: 'debroussaillementAcces',
        type: 'fk',
        useNull: true
    }, {
        name: 'avis',
        type: 'fk',
        useNull: true
    }, {
        name: 'action',
        type: 'fk',
        useNull: true
    } ],

    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.OldebVisiteDocument',
        associationKey: 'oldebVisiteDocuments',
        name: 'oldebVisiteDocuments',
        persist: false
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeOldebAnomalie',
        associationKey: 'typeOldebAnomalies',
        name: 'typeOldebAnomalies',
        persist: true
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.OldebVisiteSuite',
        associationKey: 'oldebVisiteSuites',
        getterName: 'getOldebVisitesSuites',
        setterName: 'setOldebVisitesSuites',
        name: 'oldebVisiteSuites',
        persist: true
    } ],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../oldebvisite')
    }
});