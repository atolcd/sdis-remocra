Ext.require('Sdis.Remocra.model.ProfilOrganisme');
Ext.require('Sdis.Remocra.model.ProfilUtilisateur');
Ext.require('Sdis.Remocra.model.ProfilDroit');

Ext.define('Sdis.Remocra.model.ProfilOrganismeUtilisateurDroit', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    },

    // Pour les FK
    {
        name : 'profilOrganismeId',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }
            // Après mise à jour côté client
            if (record.ProfilOrganismeBelongsToInstance) {
                return record.ProfilOrganismeBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.profilOrganisme) {
                return record.raw.profilOrganisme.id;
            }

            // Rien
            return null;
        }
    }, {
        name : 'profilUtilisateurId',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }
            // Après mise à jour côté client
            if (record.ProfilUtilisateurBelongsToInstance) {
                return record.ProfilUtilisateurBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.profilUtilisateur) {
                return record.raw.profilUtilisateur.id;
            }

            // Rien
            return null;
        }
    }, {
        name : 'profilDroitId',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }
            // Après mise à jour côté client)
            if (record.ProfilDroitBelongsToInstance) {
                return record.ProfilDroitBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.profilDroit) {
                return record.raw.profilDroit.id;
            }

            // Rien
            return null;
        }
    } ],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.ProfilOrganisme',
        associationKey : 'profilOrganisme',
        associatedName : 'ProfilOrganisme',
        getterName : 'getProfilOrganisme',
        setterName : 'setProfilOrganisme',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.ProfilUtilisateur',
        associationKey : 'profilUtilisateur',
        associatedName : 'ProfilUtilisateur',
        getterName : 'getProfilUtilisateur',
        setterName : 'setProfilUtilisateur',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.ProfilDroit',
        associationKey : 'profilDroit',
        associatedName : 'ProfilDroit',
        getterName : 'getProfilDroit',
        setterName : 'setProfilDroit',
        persist : true
    } ],

    validations : [
    // Au moins un règle de validation pour que le modèle ne soit pas
    // directement transmis au serveur !
    {
        type : 'presence',
        field : 'profilOrganismeId'
    }, {
        type : 'presence',
        field : 'profilUtilisateurId'
    }, {
        type : 'presence',
        field : 'profilDroitId'
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../proorgutidroits')
    }
});
