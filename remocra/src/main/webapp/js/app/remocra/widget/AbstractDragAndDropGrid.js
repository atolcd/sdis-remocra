Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.widget.AbstractDragAndDropGrid', {
    extend: 'Ext.form.FieldSet',
    title: null,
    layout: 'hbox',
    width: 740,
    defaults: {
        labelAlign: 'right',
        flex : 1
    },

    firstGrid: null,
    firstGridStore: null,

    secondGrid: null,
    secondGridStore: null,
    columns: null,
   initComponent: function() {
       Ext.apply(this, {
            items: this.createComponent()
       });

       this.callParent(arguments);
   },

   createComponent: function() {
      this.columns = this.getColumns();
      this.firstGridStore = this.getFirstGridStore();
      this.secondGridStore = this.getSecondGridStore();
      // Création de la grille de gauche
      this.firstGrid = this.createLeftGrid();

      // Création de la grille de droite
      this.secondGrid = this.createRightGrid();

       // Création du composant
       var items = [];
       items.push(this.firstGrid);
       items.push(this.createActionPanel());
       items.push(this.secondGrid);

       return items;
   },

   createActionPanel: function() {
        var self = this;
        return {
                    flex: 0.1,
                    border: false,
                    margin: '40 0 0 10',
                    layout: {
                        type: 'vbox',
                        pack: 'center'
                    },
                    items: [{
                        xtype: 'button',
                        height: 25,
                        text: '>',
                        itemId: 'toRight',
                        listeners: {
                            click: function() {

                                self.moveTo(self.firstGrid.getSelectionModel().getSelection(), true);
                            }
                        }
                    }, { // item de separation
                        border: 0,
                        height: 10
                    }, {
                        xtype: 'button',
                        height: 25,
                        text: '<',
                        itemId: 'toLeft',
                        listeners: {
                            click: function() {
                                self.moveTo(self.secondGrid.getSelectionModel().getSelection(), false);
                            }
                        }
                    } ]
        };
     },

    /**
     * Création de la grille de droite
     */
    createRightGrid: function() {
        var self = this;
        return Ext.create('Ext.grid.Panel', {
             store            : this.secondGridStore,
             columns          : this.columns,
             stripeRows       : true,
             title            : 'A activer',
             hideHeaders      :true,
             height           : 100
        });
    },

    /**
     * Création de la grille de gauche
     */
   createLeftGrid: function() {
        var self = this;
        return Ext.create('Ext.grid.Panel', {
             store            : this.firstGridStore,
             columns          : this.columns,
             stripeRows       : true,
             title            : 'Disponibles',
             height           : 100,
             hideHeaders      : true
        });
   },
     moveTo: function(selection, right) {
             var grid = this.secondGrid;
             if (right) {
                 this.moveToRightEvent(selection);
             } else {
                 grid = this.firstGrid;
                 this.moveToLeftEvent(selection);
             }
             // Tri automatique après avoir déplacé un élément
             // Voir s'il faut rajouter un paramètre pour trier automatiquement
             grid.getStore().sort('libelle', 'ASC');
         },
        moveToRightEvent: function(selection) {
             if (selection) {
                this.firstGrid.getStore().remove(selection);
                this.secondGrid.getStore().add(selection);
             }

         },

        moveToLeftEvent: function(selection) {
             if (selection) {
                 this.firstGrid.getStore().add(selection);
                 this.secondGrid.getStore().remove(selection);
             }
        },

        getFirstGridStore: function(){
              throw 'unimplemented method: DragAndDropGrid.getFirstGridStore';

        },

        getSecondGridStore: function() {
              throw 'unimplemented method: DragAndDropGrid.getSecondGridStore';
        },
        getColumns: function() {
           return this.columns;
        }
});

