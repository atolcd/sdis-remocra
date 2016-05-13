Ext.require('Sdis.Remocra.model.Hydrant');

Ext.define('Sdis.Remocra.model.HydrantPena', {
    extend: 'Sdis.Remocra.model.Hydrant',

    fields: [{
        name: 'coordDFCI',
        type: 'string'
    },{
        name: 'capacite',
        type: 'string'
    },{
        name: 'QAppoint',
        type: 'float',
        useNull: true
    },{
        name: 'volConstate',
        type: 'fk'
    },{
        name: 'materiau',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'positionnement',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'hbe',
        type: 'boolean'
    },{
        name: 'pibiAssocie',
        type: 'auto',
        useNull: true,
        defaultValue: null,
        persist: false
    }],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../hydrantspena')
    }
});