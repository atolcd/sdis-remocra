Ext.require('Sdis.Remocra.model.ProcessusEtlModelePlanificationParametre');
Ext.define('Sdis.Remocra.model.ProcessusEtlModele', {
    extend: 'Ext.data.Model',
    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'categorie',
        type: 'string',
        useNull: true
    }, {
        name: 'code',
        type: 'string',
        useNull: true
    }, {
        name: 'libelle',
        type: 'string',
        useNull: true
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'expressionCron',
        type: 'string'
    }
    ],
    associations: [{
        type : 'hasMany',
        model : 'Sdis.Remocra.model.ProcessusEtlModelePlanificationParametre',
        associationKey : 'parametres',
        name : 'parametres',
        persist : true
    }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../processusetlmodele')
    }
});