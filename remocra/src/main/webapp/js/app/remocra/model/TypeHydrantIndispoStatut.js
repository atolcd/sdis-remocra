Ext.require('Sdis.Remocra.model.TypeReference');
Ext.define('Sdis.Remocra.model.TypeHydrantIndispoStatut', {
    extend: 'Sdis.Remocra.model.TypeReference',
    itemId:'TypeIndispoStatut',
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeindispostatut')
    }
});