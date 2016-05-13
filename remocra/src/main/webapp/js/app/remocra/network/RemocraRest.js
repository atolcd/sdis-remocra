Ext.require('Ext.data.proxy.Rest');
Ext.require('Sdis.Remocra.bugfixes.Bugfixes'); // pour avoir accès au deepJson

Ext.define('Sdis.Remocra.network.RemocraRest', {
    extend: 'Ext.data.proxy.Rest',
    alias: 'proxy.remocra.rest',

    format: 'json',
    type: 'rest',
    headers: {
        'Accept': 'application/json,application/xml',
        'Content-Type': 'application/json'
    },
    reader: {
        type: 'json',
        root: 'data',
        totalProperty: 'total'
    },
    writer: 'deepjson'
});
