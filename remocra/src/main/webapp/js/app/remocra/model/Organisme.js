Ext.require('Ext.data.Model');
Ext.require('Sdis.Remocra.model.TypeOrganisme');
Ext.require('Sdis.Remocra.model.ProfilOrganisme');

Ext.define('Sdis.Remocra.model.Organisme', {
    extend : 'Ext.data.Model',

    // Champs supplémentaires
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'nom',
        type : 'string'
    }, {
        name : 'code',
        type : 'string'
    }, {
        name : 'actif',
        type : 'bool'
    }, {
        name : 'type',
        type : 'string'
    }, {
        name : 'typeDisplay',
        type : 'string'
    }, {
        name : 'emailContact',
        type : 'string'
    },

    // Pas réellement chargé, mais uniquement pour l'édition dans les grilles
    // (éviter une déselection du record de la combo associée)
    // Ce champ est utilisé dans le dataIndex. Sur sélect de la combo, penser à
    // faire un set de la référence et de l'id.
    {
        name : 'typeOrganismeId',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }
            // Après mise à jour côté client
            if (record.TypeOrganismeBelongsToInstance) {
                return record.TypeOrganismeBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.typeOrganisme) {
                return record.raw.typeOrganisme.id;
            }

            // Rien
            return null;
        }
    }, {
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
        name : 'zoneCompetenceId',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }
            // Après mise à jour côté client
            if (record.ZoneCompetenceBelongsToInstance) {
                return record.ZoneCompetenceBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.zoneCompetence) {
                return record.raw.zoneCompetence.id;
            }

            // Rien
            return null;
        }
    } ],

    // Validations supplémentaires
    validations : [ {
        type : 'length',
        field : 'code',
        min : 1
    }, {
        type : 'length',
        field : 'nom',
        min : 1
    }, {
        type : 'emailOrBlank',
        field : 'emailContact'
    }, {
        type: 'presence',
        field: 'typeOrganismeId'
    }, {
        type: 'presence',
        field: 'profilOrganismeId'
    }, {
        type: 'presence',
        field: 'zoneCompetenceId'
    }],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeOrganisme',
        associationKey : 'typeOrganisme',
        getterName : 'getTypeOrganisme',
        associatedName : 'TypeOrganisme',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.ProfilOrganisme',
        associationKey : 'profilOrganisme',
        getterName : 'getProfilOrganisme',
        setterName : 'setProfilOrganisme',
        associatedName : 'ProfilOrganisme',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.ZoneCompetence',
        associationKey : 'zoneCompetence',
        getterName : 'getZoneCompetence',
        setterName : 'setZoneCompetence',
        associatedName : 'ZoneCompetence',
        persist : true
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../organismes')
    }
});
