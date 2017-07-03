Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeOldebAnomalie');

Ext.define('Sdis.Remocra.model.TypeOldebCategorieAnomalie', {
    extend: 'Sdis.Remocra.model.TypeReference',

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebcategorieanomalie')
    }
});