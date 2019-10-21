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
                          // il faudra prendre celles présentes dans la crise
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

       moveToRightEvent: function(selection) {
           // Mise à jour du parentId pour pouvoir le restituer dans l'écran de
           // gauche sur le bon parent
           var self = this;
           Ext.Array.forEach(selection, function(item) {
             item.parentId = item.parentNode.data.id;
             var rightParent = self.rightPanel.getRootNode().findChild('id', item.parentId);
             item.data.isAnt = true;
             item.data.isOp = true;
             rightParent.appendChild(item);
           });
       },

       moveToLeftEvent: function(selection) {
           // Recherche de l'élément parent de la selection dans la liste des
           // éléments de gauche
           var self = this;
           Ext.Array.forEach(selection, function(item) {
               var leftParent = self.leftPanel.getRootNode().findChild('id', item.parentNode.data.id);
               leftParent.appendChild(item);
               self.leftPanel.getStore().sort('text', 'ASC');
          });
       },

       getLeftTitle: function() {
           return 'Disponible';
       },
       getRightTitle: function() {
           return 'A activer';
       },
       getMultiSelect: function() {
          return true;
       },

       getRightColumns: function (){
         return [{
                xtype: 'treecolumn', //this is so we know which column will show the tree
                text: 'Couches',
                flex: 3,
                dataIndex: 'nom'
            }, {
                xtype: 'mytreecheckcolumn',
                header: 'Opérationel',
                dataIndex: 'isOp',
                stopSelection : false

            }, {
                 xtype: 'mytreecheckcolumn',
                 header: 'Anticipation',
                 dataIndex: 'isAnt',
                 stopSelection : false
            }];
       },
         getLeftColumns: function (){
            return [{
                   xtype: 'treecolumn', //this is so we know which column will show the tree
                   text: 'Couches',
                   flex: 3,
                   dataIndex: 'nom'
               }];
          }
    });



Ext.define('My.tree.column.CheckColumn', {
    extend: 'Ext.ux.CheckColumn',
    alias: 'widget.mytreecheckcolumn',

       processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
                   if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
                       var record = view.store.getAt(recordIndex),
                           dataIndex = this.dataIndex,
                           checked = !record.get(dataIndex);

                       record.set(dataIndex, checked);
                       this.fireEvent('checkchange', this, recordIndex, checked);
                       // cancel selection.
                       return false;
                   } else {
                       return this.callParent(arguments);
                   }
               },

           renderer : function(value, meta, record) {
               if (record.isLeaf()) {
                   return this.callParent(arguments);
               }
               return ;
           },
           listeners: {
             "checkchange" : function(column, rowIndex, checked, opts ){

             }
           }


});
