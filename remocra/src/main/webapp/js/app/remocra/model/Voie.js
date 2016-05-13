Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.Voie', {
    extend : 'Ext.data.Model',
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'nom',
        type : 'string'
    }, {
        name : 'motClassant',
        type : 'string'
    }, {
        name : 'geometrie',
        type : 'string'
    }, {
        name : 'source',
        type : 'string'
    } ],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Commune',
        associationKey : 'commune',
        getterName : 'getCommune',
        setterName : 'setCommune',
        associatedName : 'Commune',
        persist : true
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../voies')
    }
});