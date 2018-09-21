Ext.require('Sdis.Remocra.model.ProcessusEtlModelePlanificationParametre');
Ext.define('Sdis.Remocra.model.ProcessusEtlPlanification', {
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
        name: 'objetConcerne',
        type: 'int',
        useNull: true
    },{
      name: 'modele',
      type: 'fk',
      useNull: true
    }
    ],
     associations: [{
         type : 'hasMany',
         model : 'Sdis.Remocra.model.ProcessusEtlPlanificationParametre',
         associationKey : 'processusEtlPlanificationParametres',
         name : 'processusEtlPlanificationParametres',
         persist : true
     }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../processusetlplanification')
    }
});