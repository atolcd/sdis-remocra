Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.TypeAlerteElt', {
    extend: 'Sdis.Remocra.model.TypeReference',
    
    associations: [{
        type: 'hasMany', model: 'Sdis.Remocra.model.SousTypeAlerteElt', associationKey: 'sousTypeAlerteElts',  name: 'sousTypeAlerteElts', persist: true,
        getterName: 'getSousTypeAlerteElts', setterName: 'setSousTypeAlerteElts'
    }]
});
