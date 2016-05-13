Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeHydrantAnomalie');

Ext.define('Sdis.Remocra.model.TypeHydrantCritere', {
    extend: 'Sdis.Remocra.model.TypeReference',

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typehydrantcriteres')
    }
});