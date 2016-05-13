Ext.define('Sdis.Remocra.model.TypeDroit', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'code',
        type : 'string'
    }, {
        name : 'nom',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../typedroits')
    }
});
