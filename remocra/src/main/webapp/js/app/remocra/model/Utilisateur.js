Ext.require('Ext.data.Model');
Ext.require('Sdis.Remocra.model.Organisme');
Ext.require('Sdis.Remocra.model.ProfilUtilisateur');

Ext.define('Sdis.Remocra.model.Utilisateur', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'email', type: 'string' },
        { name: 'password', type: 'string' },
        { name: 'nom', type: 'string' },
        { name: 'prenom', type: 'string' },
        { name: 'telephone', type: 'string' },
        { name: 'messageRemocra', type: 'boolean' },
        { name: 'identifiant', type: 'string' },
        { name: 'actif', type: 'bool' },
        { name: 'groupeFnct', type: 'string' },
        
        // Pas réellement chargé, mais uniquement pour l'édition dans les grilles (éviter une déselection du record de la combo associée)
        // Ce champ est utilisé dans le dataIndex. Sur sélect de la combo, penser à faire un set de la référence et de l'id.
        { name: 'organismeId', type: 'int', convert: function(v, record){
                if (!record) {
                    return null;
                }
                // Après mise à jour côté client
                if(record.OrganismeBelongsToInstance) {
                    return record.OrganismeBelongsToInstance.getId();
                }
                // Par les raw data (chargement par le serveur)
                if (record.raw && record.raw.organisme) {
                    return record.raw.organisme.id;
                }
                
                // Rien
                return null;
            }
        },
        { name: 'profilUtilisateurId', type: 'int', convert: function(v, record){
                if (!record) {
                    return null;
                }
                // Après mise à jour côté client
                if(record.ProfilUtilisateurBelongsToInstance) {
                    return record.ProfilUtilisateurBelongsToInstance.getId();
                }
                // Par les raw data (chargement par le serveur)
                if (record.raw && record.raw.profilUtilisateur) {
                    return record.raw.profilUtilisateur.id;
                }
                
                // Rien
                return null;
            }
        },
    
        { name: 'orgTiretPrenomNom', mapping: 'organisme', convert : function(v, record){
                if(v && !Ext.isEmpty(v.nom)){
                    return v.nom + ' - ' + record.get('prenom') + ' ' + record.get('nom');
                }
                return record.get('prenom') + ' ' + record.get('nom');
            }
        },
        { name: 'prenomNomIdentifiant', convert : function(v, record){
                return record.get('prenom') + ' ' + record.get('nom') + ' (' + record.get('identifiant') + ')';
            }
        }
    ],
    
    associations: [ {
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.Organisme',
        associationKey: 'organisme',
        getterName : 'getOrganisme',
        associatedName : 'Organisme',
        persist : true
    }, {
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.ProfilUtilisateur',
        associationKey: 'profilUtilisateur',
        getterName : 'getProfilUtilisateur',
        setterName: 'setProfilUtilisateur',
        associatedName : 'ProfilUtilisateur',
        persist : true
    } ],
   
    validations: [
        {type: 'length', field: 'identifiant', min: 2},
        {type: 'length', field: 'nom', min: 2},
        {type: 'length', field: 'email', min: 5},
        {type: 'format', field: 'email', matcher: /^(\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6}$/},
        {type: 'presence', field: 'profilUtilisateurId'},
        {type: 'presence', field: 'organismeId'}
    ],
   
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../utilisateurs')
    }
});
