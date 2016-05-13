Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.Traitements');

Ext.define('Sdis.Remocra.network.TraitementsStore', {
    extend : 'Ext.data.Store',
    requires : [ 'Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model : 'Sdis.Remocra.model.Traitements',    
    remoteFilter : true,
    autoLoad : false,
    proxy : {
        format : 'json',
        type : 'rest',
        headers : {
            'Accept' : 'application/json,application/xml',
            'Content-Type' : 'application/json'
        },
        url : Sdis.Remocra.util.Util.withBaseUrl('../traitements/modelestraitement/'),
        reader : {
            type : 'json',
            root : 'data',
            totalProperty: 'total'
        }
    }
});
