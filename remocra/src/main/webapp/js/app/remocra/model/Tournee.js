Ext.define('Sdis.Remocra.model.Tournee', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    },{
        name : 'nom',
        type : 'string',
        useNull : true
    }, {
        name : 'hydrantCount',
        type : 'int'
    }, {
        name : 'debSync',
        type : 'date'
    }, {
        name : 'lastSync',
        type : 'date'
    }, {
        name : 'etat',
        type : 'int'
    }, {
        name : 'geometrie',
        type : 'string'
    }, {
        name : 'reservation',
        type : 'fk',
        useNull : true,
        defaultValue : null
    } ],
    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Organisme',
        associationKey : 'affectation',
        getterName : 'getAffectation',
        associatedName : 'affectation',
        persist : true
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../tournees')
    }
});