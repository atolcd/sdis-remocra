Ext.require('Sdis.Remocra.model.Hydrant');

Ext.define('Sdis.Remocra.model.HydrantPibi', {
    extend: 'Sdis.Remocra.model.Hydrant',

    fields: [{
        name: 'numeroSCP',
        type: 'string'
    },{
        name: 'gestReseau',
        type: 'string'
    },{
        name: 'diametre',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'debit',
        type: 'int',
        useNull: true
    },{
        name: 'debitMax',
        type: 'int',
        useNull: true
    },{
        name: 'choc',
        type: 'boolean',
        defaultValue: false
    },{
        name: 'pression',
        type: 'float',
        useNull: true
    },{
        name: 'pressionDyn',
        type: 'float',
        useNull: true
    },{
        name: 'marque',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'modele',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'pena',
        type: 'auto',
        useNull: true,
        defaultValue: null,
        persist: false
    }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../hydrantspibi')
    }
});