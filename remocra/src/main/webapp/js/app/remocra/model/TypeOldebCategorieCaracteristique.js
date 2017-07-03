Ext.require('Sdis.Remocra.model.TypeOldebCaracteristique');
Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.TypeOldebCategorieCaracteristique', {
    extend: 'Sdis.Remocra.model.TypeReference',

    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeOldebCaracteristique',
        associationKey: 'typeOldebCaracteristiques',
        name: 'typeOldebCaracteristiques',
        persist: false
    } ]
});
