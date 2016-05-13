Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.PermisDocument', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'titre',
        type : 'string'
    }, {
        name : 'code',
        type : 'string'
    } ]
});
