Ext.require('Sdis.Remocra.model.TypeReference');
Ext.define('Sdis.Remocra.model.TypeOldebSuite', {
    extend: 'Sdis.Remocra.model.TypeReference',

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebsuite')
    }
});