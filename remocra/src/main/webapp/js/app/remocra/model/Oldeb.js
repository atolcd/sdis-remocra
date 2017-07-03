Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.Commune');
Ext.require('Sdis.Remocra.model.TypeOldebAcces');
Ext.require('Sdis.Remocra.model.TypeOldebZoneUrbanisme');
Ext.require('Sdis.Remocra.model.OldebLocataire');
Ext.require('Sdis.Remocra.model.OldebPropriete');
Ext.require('Sdis.Remocra.model.OldebVisite');
Ext.require('Sdis.Remocra.model.TypeOldebCaracteristique');

Ext.define('Sdis.Remocra.model.Oldeb', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'geometrie',
        type: 'string'
    }, {
        name: 'section',
        type: 'string'
    }, {
        name: 'parcelle',
        type: 'string'
    }, {
        name: 'voie',
        type: 'string'
    }, {
        name: 'volume',
        type: 'integer'
    }, {
        name: 'largeurAcces',
        type: 'integer'
    }, {
        name: 'portailElectrique',
        type: 'boolean'
    }, {
        name: 'codePortail',
        type: 'string'
    }, {
        name: 'jsonGeometrie',
        type: 'auto',
        persist: false,
        useNull: true
    }, {
        name: 'actif',
        type: 'boolean',
        defaultValue: true

    }, {
        name: 'numVoie',
        type: 'string'
    }, {
        name: 'nomZoneUrbanisme',
        type: 'string'
    }, {
        name: 'adresse',
        type: 'string'
    }, {
        name: 'avis',
        type: 'string'
    }, {
        name: 'dateDerniereVisite',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'debroussaillement',
        type: 'string'
    }, {
        name: 'lieuDit',
        type: 'string'
    } /*
         * , { name: 'commune', type: 'fk', useNull: true, defaultValue: null }
         */],

    associations: [{
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.Commune',
        associationKey: 'commune',
        getterName: 'getCommune',
        setterName: 'setCommune',
        associatedName: 'Commune',
        persist: true
    }, {
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.TypeOldebAcces',
        associationKey: 'acces',
        getterName: 'getAcces',
        setterName: 'setAcces',
        associatedName: 'Acces',
        persist: true
    }, {
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.TypeOldebZoneUrbanisme',
        associationKey: 'zoneUrbanisme',
        getterName: 'getZoneUrbanisme',
        setterName: 'setZoneUrbanisme',
        associatedName: 'ZoneUrbanisme',
        persist: true
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.OldebLocataire',
        associationKey: 'oldebLocataires',
        name: 'oldebLocataires',
        persist: true,
        getterName: 'getOldebLocataires',
        setterName: 'setOldebLocataires'

    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.OldebPropriete',
        associationKey: 'oldebProprietes',
        name: 'oldebProprietes',
        getterName: 'getOldebProprietes',
        setterName: 'setOldebProprietes',
        associatedName: 'oldebProprietes',
        persist: true
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeOldebCaracteristique',
        associationKey: 'typeOldebCaracteristiques',
        name: 'typeOldebCaracteristiques',
        getterName: 'getTypeOldebCaracteristiques',
        setterName: 'setTypeOldebCaracteristiques',
        associatedName: 'typeOldebCaracteristiques',
        persist: true
    }, {
        type: 'hasMany',
        model: 'Sdis.Remocra.model.OldebVisite',
        name: 'oldebVisites',
        associationKey: 'oldebVisites',
        getterName: 'getOldebVisites',
        setterName: 'setOldebVisites',
        associatedName: 'oldebVisites',
        persist: true
    } ],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../oldeb')
    }
});