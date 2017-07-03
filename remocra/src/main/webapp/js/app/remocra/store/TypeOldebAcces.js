Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebAcces');

Ext.define('Sdis.Remocra.store.TypeOldebAcces', {
    extend : 'Ext.data.Store',
    requires : [ 'Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model : 'Sdis.Remocra.model.TypeOldebAcces',
    alias: 'store.crTypeOldebAcces',
    storeId: 'TypeOldebAcces',
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
        url : Sdis.Remocra.util.Util.withBaseUrl('../typeoldebacces'),
        reader : {
            type : 'json',
            root : 'data'
        }
    }
});