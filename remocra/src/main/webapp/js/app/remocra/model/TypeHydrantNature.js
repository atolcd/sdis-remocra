Ext.require('Sdis.Remocra.model.TypeHydrant');

Ext.define('Sdis.Remocra.model.TypeHydrantNature', {
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
    }, {
        type : 'string',
        name : 'parentName',
        persist : false,
        mapping : 'typeHydrant.nom'
    }, {
        type : 'int',
        name : 'typeHydrantId',
        persist : false,
        mapping : 'typeHydrant.id'
    }, {
        type : 'string',
        name : 'typeHydrantCode',
        persist : false,
        mapping : 'typeHydrant.code'
    }, {
        type : 'string',
        name : 'fullName',
        persist : false,
        convert : function(val, rec) {
            return rec.get('parentName') + ' - ' + rec.get('nom');
        }
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
    } ],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeHydrant',
        associationKey : 'typeHydrant',
        getterName : 'getTypeHydrant',
        associatedName : 'typeHydrant',
        persist : true
    } ]
});