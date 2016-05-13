Ext.define('Sdis.Remocra.model.ProfilDroit', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'nom',
        type : 'string'
    }, {
        name : 'feuilleDeStyleGeoServer',
        type : 'string'
    }, {
        name : 'code',
        type : 'string'
    } ],

    validations : [ {
        type : 'length',
        field : 'nom',
        min : 1
    }, {
        type : 'exclusion',
        field : 'nom',
        list : [ 'Nouveau nom' ]
    }, {
        type : 'length',
        field : 'code',
        min : 1
    }, {
        type : 'exclusion',
        field : 'code',
        list : [ 'Nouveau code' ]
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../profildroits')
    }
});
