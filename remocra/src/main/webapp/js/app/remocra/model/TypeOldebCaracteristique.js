Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.TypeOldebCaracteristique', {
    extend: 'Sdis.Remocra.model.TypeReference',

    fields: [{
        name: 'categorie',
        type: 'fk',
        useNull: true,
        defaultValue: null
    } ]
});
