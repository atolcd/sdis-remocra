Ext.define('Sdis.Remocra.model.VectorStyle', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'code',
        type : 'string'
    }, {
        name : 'lbl',
        type : 'string'
    }, {
        name : 'def',
        type : 'auto'
    } ],

    proxy : {
        type : 'remocra.rest',
        url : BASE_URL + '/../ext-res/js/app/remocra/features/cartographie/data/styles'
    }
});
