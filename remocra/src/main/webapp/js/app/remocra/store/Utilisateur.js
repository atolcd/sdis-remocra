Ext.require('Sdis.Remocra.model.Utilisateur');

Ext.define('Sdis.Remocra.store.Utilisateur', {
    extend: 'Ext.data.Store',
    alias: 'store.utilisateur',
    storeId: 'Utilisateur',

    model: 'Sdis.Remocra.model.Utilisateur',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 20
});

// Pour mémoire
// TODO cva supprimer si non nécessaire
//Ext.define('Sdis.Remocra.store.UtilisateurSdis', {
//extend : 'Sdis.Remocra.store.Utilisateur',
//storeId: 'UtilisateurSdis',
//proxyConfig : {
//  extraParams : {
//      //Seulement les utilisateurs Sdis
//      filter : Ext.JSON.encode([{"property":"organismeCode", value: 'SDIS'}])
//  }
//}
//});