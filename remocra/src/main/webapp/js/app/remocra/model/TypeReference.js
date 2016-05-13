Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.TypeReference', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'nom',
        type : 'string'
    }, {
        name : 'code',
        type : 'string'
    }, {
        name : 'actif',
        type : 'bool'
    } ],

    validations : [ {
        type : 'length',
        field : 'nom',
        min : 1
    }, {
        type : 'length',
        field : 'code',
        min : 1
    }, {
        type : 'exclusion',
        field : 'nom',
        list : [ 'Nouveau nom' ]
    }, {
        type : 'exclusion',
        field : 'code',
        list : [ 'Nouveau code' ]
    } ]
});
