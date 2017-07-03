Ext.require('Sdis.Remocra.network.RemocraRest');

Ext.define('Sdis.Remocra.model.OldebVisiteDocument', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'titre',
        type: 'string'
    }, {
        name: 'code',
        type: 'string'
    } ]
});