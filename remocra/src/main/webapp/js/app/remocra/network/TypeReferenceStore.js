Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.network.TypeReferenceStore', {
    extend : 'Ext.data.Store',
    requires : [ 'Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model : 'Sdis.Remocra.model.TypeReference',
    autoLoad : true,
    autoSync: true,
    
    // Attendus
    url: null,
    extraParams : null,
    //
    
    constructor: function(config) {
        config = config || {};
        Ext.applyIf(config, {
            proxy : {
                extraParams : this.extraParams,
                format : 'json',
                type : 'rest',
                headers : {
                    'Accept' : 'application/json,application/xml',
                    'Content-Type' : 'application/json'
                },
                url : Sdis.Remocra.util.Util.withBaseUrl('../'+(this.url!=null?this.url:'__URLINDETERMINEE__')),
                reader : {
                    type : 'json',
                    root : 'data'
                },
                writer: {
                    type: 'json'
                }
            }
        });
        this.callParent([config]);
    }
});

