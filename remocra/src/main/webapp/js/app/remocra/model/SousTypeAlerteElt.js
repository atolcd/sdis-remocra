Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.SousTypeAlerteElt', {
    extend: 'Sdis.Remocra.model.TypeReference',
    
    // Champs supplémentaires
    fields: [
         { name: 'typeGeom' }
    ],
    
    associations: [
        { type: 'belongsTo', model: 'Sdis.Remocra.model.TypeAlerteElt',
            associationKey: 'typeAlerteElt', getterName: 'getTypeAlerteElt', associatedName: 'TypeAlerteElt', persist: true }
    ]
});
