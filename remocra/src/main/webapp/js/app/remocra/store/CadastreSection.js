Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.CadastreSection');

Ext.define('Sdis.Remocra.store.CadastreSection', {
    extend: 'Ext.data.Store',
    alias: 'store.crCadastreSection',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.CadastreSection',
    storeId: 'CadastreSection',
    pageSize: 10,
    remoteSort: true,
    remoteFilter: true,
    proxy: {
        format: 'json',
        type: 'rest',
        headers: {
            'Accept': 'application/json,application/xml',
            'Content-Type': 'application/json'
        },
        url: Sdis.Remocra.util.Util.withBaseUrl('../cadastresection/numero'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }

});