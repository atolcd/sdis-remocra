Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.Commune', {
    extend : 'Ext.data.Model',
    
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'nom',
        type : 'string'
    }, {
        name : 'insee',
        type : 'string'
    }, {
        name : 'geometrie',
        type : 'string'
    }, {
        name : 'pprif',
        type : 'boolean'
    } ],
    associations : [],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../communes/nom')
    },

    inheritableStatics : {
        loadByXY : function(x, y, srid, config) {
            config = Ext.apply({}, config);
            config = Ext.applyIf(config, {
                action : 'read',
                url : Sdis.Remocra.util.Util
                        .withBaseUrl('../communes/xy'),
                params : {
                    wkt : "POINT(" + x + " " + y + ")",
                    srid : srid
                }
            });

            var operation = new Ext.data.Operation(config), scope = config.scope
                    || this, record = null, callback;

            callback = function(operation) {
                if (operation.wasSuccessful()) {
                    record = operation.getRecords()[0];
                    Ext.callback(config.success, scope, [
                            record, operation ]);
                } else {
                    Ext.callback(config.failure, scope, [
                            record, operation ]);
                }
                Ext.callback(config.callback, scope, [ record,
                        operation ]);
            };

            this.proxy.read(operation, callback, this);
        }
    }
});
