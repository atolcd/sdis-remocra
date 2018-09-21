Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');

Ext.define('Sdis.Remocra.model.TypeCriseStatut', {
    extend: 'Sdis.Remocra.model.TypeReference',

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typecrisestatut')
    }
});