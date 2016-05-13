Ext.require('Sdis.Remocra.model.TypeReference');

Ext.define('Sdis.Remocra.model.Profil', {
    extend : 'Sdis.Remocra.model.TypeReference',

    fields : [ {
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
    } ],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeOrganisme',
        associationKey : 'typeOrganisme',
        getterName : 'getTypeOrganisme',
        setterName : 'setTypeOrganisme',
        associatedName : 'TypeOrganisme',
        persist : true
    } ]
});
