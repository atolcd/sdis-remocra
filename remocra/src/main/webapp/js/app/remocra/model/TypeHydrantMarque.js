Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeHydrantModele');

Ext.define('Sdis.Remocra.model.TypeHydrantMarque', {
    extend: 'Sdis.Remocra.model.TypeReference',
    
    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeHydrantModele',
        associationKey: 'modeles',
        name: 'modeles',
        associatedName: 'modeles',
        persist: true
    }],
    
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typehydrantmarques')
    }
});