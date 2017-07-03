Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebAction');

Ext.define('Sdis.Remocra.store.TypeOldebAction', {
    extend : 'Ext.data.Store',
    requires : [ 'Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model : 'Sdis.Remocra.model.TypeOldebAction',
    alias: 'store.crTypeOldebAction',
    storeId: 'TypeOldebAction',
    pageSize : 10,
    remoteSort : true,
    remoteFilter : true,
    proxy : {
        format : 'json',
        type : 'rest',
        headers : {
            'Accept' : 'application/json,application/xml',
            'Content-Type' : 'application/json'
        },
        url : Sdis.Remocra.util.Util.withBaseUrl('../typeoldebaction'),
        reader : {
            type : 'json',
            root : 'data'
        }
    }
});