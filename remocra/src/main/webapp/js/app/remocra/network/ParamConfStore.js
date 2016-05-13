
Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.ParamConf');

Ext.define('Sdis.Remocra.network.ParamConfStore', {
    extend : 'Ext.data.Store',
    requires : [ 'Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    
    model : 'Sdis.Remocra.model.ParamConf',
    
    proxy : {
        batchActions: true,
        format : 'json',
        type : 'rest',
        headers : {
            'Accept' : 'application/json,application/xml',
            'Content-Type' : 'application/json'
        },
        url : Sdis.Remocra.util.Util.withBaseUrl('../paramconfs'),
        reader : {
            type : 'json',
            root : 'data',
            totalProperty: 'total'
        },
        writer : {
            type : 'json',
            root : undefined,
            allowSingle: false
        }
    }
});
