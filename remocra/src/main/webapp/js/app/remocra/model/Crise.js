Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeCriseStatut');
Ext.require('Sdis.Remocra.model.TypeCrise');
Ext.require('Sdis.Remocra.model.Commune');
Ext.require('Sdis.Remocra.model.RepertoireLieu');
Ext.require('Sdis.Remocra.model.ProcessusEtlPlanification');
Ext.define('Sdis.Remocra.model.Crise', {
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
    }, {
        name: 'activation',
        type: 'date',
        dataFormat: 'c'
    }, {
        name: 'cloture',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'carte',
        type: 'string'
    }
    ],
    associations: [{
            type: 'belongsTo',
            model: 'Sdis.Remocra.model.TypeCrise',
            associationKey: 'typeCrise',
            name: 'typeCrise',
            getterName: 'getTypeCrise',
            setterName: 'setTypeCrise',
            persist: true
        }, {
              type: 'belongsTo',
              model: 'Sdis.Remocra.model.TypeCriseStatut',
              associationKey: 'typeCriseStatut',
              name: 'typeCriseStatut',
              getterName: 'getTypeCriseStatut',
              setterName: 'setTypeCriseStatut',
              persist: true
    },{
            type : 'hasMany',
            model : 'Sdis.Remocra.model.Commune',
            associationKey : 'communes',
            name : 'communes',
            persist : true
    },{
         type : 'hasMany',
         model : 'Sdis.Remocra.model.RepertoireLieu',
         associationKey : 'repertoireLieus',
         name : 'repertoireLieus',
         persist : true
    },{
          type : 'hasMany',
          model : 'Sdis.Remocra.model.ProcessusEtlPlanification',
          associationKey : 'processusEtlPlanifications',
          name : 'processusEtlPlanifications',
          persist : true
    },{
          type : 'hasMany',
          model : 'Sdis.Remocra.model.OgcCouche',
          associationKey : 'ogcCouches',
          name : 'ogcCouches',
          persist : true
    }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../crises')
    }
});