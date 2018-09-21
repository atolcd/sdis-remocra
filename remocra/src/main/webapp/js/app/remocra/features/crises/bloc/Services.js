Ext.require('Sdis.Remocra.widget.AbstractDragAndDropGrid');
Ext.require('Sdis.Remocra.widget.AbstractDragAndDropTree');
Ext.require('Sdis.Remocra.reader.OgcServiceReader');

Ext.require('Sdis.Remocra.model.OgcService');
Ext.define('Sdis.Remocra.features.crises.bloc.Services', {
    extend: 'Sdis.Remocra.widget.AbstractDragAndDropTree',
    id: 'services',
    alias: 'widget.crise.Services',
    border: false,
    width: 840,
    height: 245,

      // create the data store
    getRightStore: function() {
      return Ext.create('Ext.data.TreeStore', {
                  // Model de l'objet de l'arbre de gauche
                  model: 'Sdis.Remocra.model.OgcService',
                  proxy: {
                      type: 'rest',
                      headers: {
                          'Accept': 'application/json,application/xml',
                          'Content-Type': 'application/json'
                      },
                      // URL de récupération des informations de l'abre de gauche
                      url: Sdis.Remocra.util.Util.withBaseUrl('../ogcservice'),
                      // Reader spécifique à la structure du JSON retournée par le
                      // service
                      reader: new Sdis.Remocra.reader.OgcServiceReader({
                          text: 'nom',
                          expanded: false,
                          // On ne peuple pas les caractérisiques dans le panneau de
                          // droite,
                          // il faudra prendre celles présentes dans l'oldeb
                          withCouches: true
                      })
                  },
                  sorters: [{
                      property: 'text',
                      direction: 'ASC'
                  } ]
              });
    },

    getLeftStore: function() {
            return Ext.create('Ext.data.TreeStore', {
                requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
                // Model de l'objet de l'arbre de gauche
                model: 'Sdis.Remocra.model.OgcService',
                proxy: {
                    type: 'rest',
                    sortParam: false,
                    nodeParam: null,
                    headers: {
                        'Accept': 'application/json,application/xml',
                        'Content-Type': 'application/json'
                    },
                    // URL de récupération des informations de l'abre de gauche
                    url: Sdis.Remocra.util.Util.withBaseUrl('../ogcservice'),
                    // Reader spécifique à la structure du JSON retournée par le
                    // service
                    reader: new Sdis.Remocra.reader.OgcServiceReader({
                        text: 'nom',
                        expanded: false
                    })
                },
                sorters: [{
                    property: 'text',
                    direction: 'ASC'
                } ]
            });
        },

    /**
        * Méthode de validation du drop vers le panneau de droite (A surcharger si
        * besoin)
        */
       validRightDropEvent: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
           // Règle 1 : Empêche le drop en interne des éléments de l'arbre (les
           // éléments ne peuvent pas être déplacés)
           if (data.view.id == this.id) {
               return false;
           }
           // Règle 2 : Seulement les feuilles peuvent être déplacées
           if (!data.records[0].data.leaf) {
               return false;
           }
           return true;
       },

       /**
        * Méthode de validation du drop vers le panneau de gauche (A surcharger si
        * besoin)
        */
       validLeftDropEvent: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
           // Règle 1 : Empèche le drop en interne des éléments de l'arbre (les
           // éléments ne peuvent pas être déplacés)
           if (data.view.id == this.id) {
               return false;
           }
           // Règle 2 : La réinitilisation d'une feuille peu se faire seulement sur
           // son parent
           if (data.records[0].data.parentId != overModel.data.id) {
               return false;
           }
           return true;
       },

       moveToRightEvent: function(selection) {
           // EXEMPLE (à adapter)
           // Un seul élément n'est sélectionnable d'où la récupération de l'index
           // [0]

           // Mise à jour du parentId pour pouvoir le restituer dans l'écran de
           // gauche sur le bon parent
           if (selection[0]) {
               selection[0].parentId = selection[0].parentNode.data.id;
               var rightParent = this.rightPanel.getRootNode().findChild('id', selection[0].parentId);
               rightParent.appendChild(selection[0]);
           }

       },

       moveToLeftEvent: function(selection) {
           // EXEMPLE (à adapter)
           // Recherche de l'élément parent de la selection dans la liste des
           // éléments de gauche
           if (selection[0]) {
               var leftParent = this.leftPanel.getRootNode().findChild('id', selection[0].parentNode.data.id);
               leftParent.appendChild(selection[0]);
               this.leftPanel.getStore().sort('text', 'ASC');
           }
       },

       getLeftTitle: function() {
           return 'Disponible';
       },
       getRightTitle: function() {
           return 'A activer';
       }
   });
