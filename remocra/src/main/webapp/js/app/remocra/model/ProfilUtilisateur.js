Ext.require('Sdis.Remocra.model.Profil');

Ext.define('Sdis.Remocra.model.ProfilUtilisateur', {
    extend : 'Ext.data.Model',
    fields:[
    {
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
        },
        {
            name: 'urlDashboard',
            type: 'string'
        },  // Pas réellement chargé, mais uniquement pour l'édition dans les grilles
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
              }
    ],
        associations : [ {
            type : 'belongsTo',
            model : 'Sdis.Remocra.model.TypeOrganisme',
            associationKey : 'typeOrganisme',
            getterName : 'getTypeOrganisme',
            associatedName : 'TypeOrganisme',
            persist : true
        }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../profilutilisateurs')
    }
});
