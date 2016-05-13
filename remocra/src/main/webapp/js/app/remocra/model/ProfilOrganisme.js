Ext.require('Sdis.Remocra.model.Profil');

Ext.define('Sdis.Remocra.model.ProfilOrganisme', {
    extend : 'Sdis.Remocra.model.Profil',
    
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../profilorganismes')
    }
});
