Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');

Ext.define('Sdis.Remocra.model.TypeOldebAnomalie', {
    extend: 'Sdis.Remocra.model.TypeReference',

    fields: [{
        name: 'categorie',
        type: 'fk'
    }, {
        name: 'categorie_nom',
        type: 'string',
        mapping: function(data, reader) {
            if (data['categorie']) {
                return data['categorie']['nom'];
            }
            return '-';
        },
        persist: false
    } ],
    proxy: {
        type: 'remocra.rest',
        limitParam: undefined,
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebanomalie')
    }
});