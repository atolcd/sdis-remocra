Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.TypeOrganisme', {
    extend : 'Sdis.Remocra.model.TypeReference',
    requires: ['Sdis.Remocra.model.TypeReference'],


    fields : [ {
        name : 'typeOrganismeParent',
        type : 'int',
        convert : function(v, record) {
            if (!record) {
                return null;
            }

            // Après mise à jour côté client
            if (record.typeOrganismeParentBelongsToInstance) {
                return record.typeOrganismeParentBelongsToInstance.getId();
            }
            // Par les raw data (chargement par le serveur)
            if (record.raw && record.raw.typeOrganismeParent) {
                return record.raw.typeOrganismeParent.id;
            }

            // Rien
            return null;
        }
    } ],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeOrganisme',
        associationKey : 'typeOrganismeParent',
        getterName : 'getTypeOrganismeParent',
        setterName : 'setTypeOrganismeParent',
        associatedName : 'typeOrganismeParent',
        persist : true
    } ],

    proxy: {
            type: 'remocra.rest',
            url: Sdis.Remocra.util.Util.withBaseUrl('../typeorganisme')
        }
});
