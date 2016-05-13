Ext.require('Sdis.Remocra.model.ProfilOrganismeUtilisateurDroit');

Ext.define('Sdis.Remocra.store.ProfilOrganismeUtilisateurDroit', {
    extend: 'Ext.data.Store',
    alias: 'store.crProfilOrganismeUtilisateurDroit',
    
    storeId: 'ProfilOrganismeUtilisateurDroit',
    model: 'Sdis.Remocra.model.ProfilOrganismeUtilisateurDroit',
    remoteSort: true,
    remoteFilter: false
});
