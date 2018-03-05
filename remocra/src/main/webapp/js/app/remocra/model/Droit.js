Ext.require('Sdis.Remocra.model.TypeDroit');
Ext.require('Sdis.Remocra.model.ProfilDroit');

Ext.define('Sdis.Remocra.model.Droit', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    },

    // Pour les FK
    {
        name : 'typeDroitId',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }
            // Après mise à jour côté client
            if (record.TypeDroitBelongsToInstance) {
                return record.TypeDroitBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.typeDroit) {
                return record.raw.typeDroit.id;
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
        model : 'Sdis.Remocra.model.TypeDroit',
        associationKey : 'typeDroit',
        associatedName : 'TypeDroit',
        getterName : 'getTypeDroit',
        setterName : 'setTypeDroit',
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
        field : 'typeDroitId'
    }, {
        type : 'presence',
        field : 'profilDroitId'
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../droits')
    }
});
