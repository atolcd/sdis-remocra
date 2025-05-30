Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.Tournee');
Ext.require('Sdis.Remocra.model.TypeHydrantNature');
Ext.require('Sdis.Remocra.model.TypeHydrantDomaine');
Ext.require('Sdis.Remocra.model.TypeHydrantAnomalie');
Ext.require('Sdis.Remocra.model.HydrantDocument');
Ext.require('Sdis.Remocra.model.TypeHydrantNatureDeci');

Ext.define('Sdis.Remocra.model.HydrantRecord', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'numero',
        type: 'string'
    }, {
        name: 'numeroInterne',
        type: 'integer',
        useNull: true,
        defaultValue: null
    }, {
        name: 'code',
        type: 'string'
    }, {
        name: 'agent1',
        type: 'string'
    }, {
        name: 'agent2',
        type: 'string'
    }, {
        name: 'dateReco',
        type: 'date'
    }, {
        name: 'dateContr',
        type: 'date'
    }, {
        name: 'dateRecep',
        type: 'date'
    }, {
        name: 'dateAttestation',
        type: 'date'
    }, {
        name: 'dateVerif',
        type: 'date'
    }, {
        name: 'geometrie',
        type: 'string'
    }, {
        name: 'lieuDit',
        type: 'string',
        useNull: true
    }, {
        name: 'complement',
        type: 'string'
    }, {
        name: 'anneeFabrication',
        type: 'int',
        useNull: true
    }, {
        name: 'courrier',
        type: 'string'
    }, {
        name: 'indispoTemp',
        type: 'integer'
    }, {
        name: 'gestPointEau',
        type: 'string'
    }, {
        name: 'observation',
        type: 'string'
    }, {
        name: 'dispoTerrestre',
        type: 'string',
        useNull: true,
        defaultValue: null
    }, {
        name: 'dispoHbe',
        type: 'string',
        useNull: true,
        defaultValue: null
    }, {
        name: 'dispoAdmin',
        type: 'string',
        useNull: true,
        defaultValue: null
    }, {
        name: 'natureNom',
        type: 'string',
        useNull: true,
        persist: false
    }, {
        name: 'jsonGeometrie',
        type: 'auto',
        persist: false,
        useNull: true
    }, {
        name: 'nature',
        type: 'fk'
    }, {
        name: 'domaine',
        type: 'fk',
        useNull: true,
        defaultValue: null
    }, {
        name: 'commune',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'nomCommune',
        type: 'string',
        useNull: true,
        persist: false
    }, {
        name: 'CISCommune',
        type: 'string',
        persist: false
    },{
        name: 'nomTournee',
        type: 'string',
        persist: false
    },{
        name: 'nomTournees',
        type: 'string',
        persist: false
    }, {
       name: 'natureDeci',
       type: 'fk',
       useNull: true,
       defaultValue: null
    },{
        name: 'nomNatureDeci',
        type: 'string',
        useNull: true,
        persist: false
   },{
        name: 'codeDeci',
        type: 'string',
        useNull: true,
        persist: false
   },{
        name: 'adresse',
        type: 'string',
        useNull: true,
        persist: false
   },{
        name: 'gestionnaire',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'site',
        type: 'fk',
        useNull: true,
        defaultValue: null
    },{
        name: 'autoriteDeci',
        type: 'fk',
        useNull: true,
        defaultValue: null
    }],

    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeHydrantAnomalie',
        associationKey: 'anomalies',
        name: 'anomalies',
        associatedName: 'anomalies',
        persist: true
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.Tournee',
        associationKey: 'tournees',
        name: 'tournees',
        associatedName: 'tournees',
        persist: true
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.HydrantDocument',
        associationKey: 'hydrantDocuments',
        name: 'hydrantDocuments',
        persist: false
    }, {
        type: 'hasOne',
        model: 'Sdis.Remocra.model.HydrantDocument',
        associationKey: 'photo',
        name: 'photo',
        associatedName: 'photo',
        getterName: 'getPhoto',
        persist: false,
        foreignKey: 'hydrant'
    }],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/records')
    }
});
