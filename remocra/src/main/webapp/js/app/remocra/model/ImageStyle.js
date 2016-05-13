Ext.define('Sdis.Remocra.model.ImageStyle', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'url',
        type : 'string'
    }, {
        name : 'lbl',
        type : 'string'
    } ],

    proxy : {
        type : 'remocra.rest',
        url : BASE_URL + '/../ext-res/js/app/remocra/features/cartographie/data/images'
    }
});
