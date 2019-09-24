Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeHydrantNature');

Ext.define('Sdis.Remocra.model.HydrantPrescrit', {
    extend : 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    },{
        name: 'datePrescrit',
        type: 'date'
    },{
        name: 'geometrie',
        type: 'string'
    },{
        name: 'jsonGeometrie',
        type: 'auto',
        persist: false,
        useNull: true
    },{
        name: 'nbPoteaux',
        type: 'int',
        useNull: true
    },{
        name: 'debit',
        type: 'int',
        useNull: true
    },{
        name: 'agent',
        type: 'string',
        useNull: true
    },{
        name: 'commentaire',
        type: 'string',
        useNull: true
    },{
        name: 'numDossier',
        type: 'string',
        useNull: true
    }],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../hydrantprescrits')
    }
});