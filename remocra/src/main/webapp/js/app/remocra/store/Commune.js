Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.Commune');

Ext.define('Sdis.Remocra.store.Commune', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    alias: 'store.crCommune',

    storeId: 'commune',
    model: 'Sdis.Remocra.model.Commune',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../communes/nom'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
// Supprimé pour ne pas recharger les combo après une restauration du state
// ,autoLoad : true,
});
