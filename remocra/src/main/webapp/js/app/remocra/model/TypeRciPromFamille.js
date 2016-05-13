Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.TypeRciPromFamille', {
    extend : 'Sdis.Remocra.model.TypeReference',

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../typercipromfamilles')
    }
});