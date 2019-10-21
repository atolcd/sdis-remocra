Ext.require('Sdis.Remocra.model.TypeOldebCategorieCaracteristique');
Ext.require('Sdis.Remocra.reader.TypeOldebCategorieCaracteristiqueReader');

Ext.define('Sdis.Remocra.widget.AbstractDragAndDropTree', {
    extend: 'Ext.Panel',

    title: null,

    defaults: {
        anchor: '90%',
        labelAlign: 'right',
        margin: '0 0 10 0'
    },

    layout: {
        type: 'hbox',
        pack: 'start',
        align: 'stretch'
    },

    // Pas de drag and drop par défaut
    enableDD: false,

    // Elements de l'arbre de gauche
    leftPanel: null,
    leftStore: null,
    leftTitle: null,

    // Elements de l'arbre de droite
    rightPanel: null,
    rightStore: null,
    rightTitle: null,

    //colonne dans le tree panel
    leftColumns: [],
    rightColumns: [],

    // multiSelection
    multiSelect: false,

    initComponent: function() {
        Ext.apply(this, {
            items: this.createComponent()
        });

        this.callParent(arguments);
    },

    createComponent: function() {
        this.multiSelect = this.getMultiSelect();
        this.leftColumns = this.getLeftColumns();
        this.rightColumns = this.getRightColumns();


        // Création du panneau de droite
        this.rightStore = this.getRightStore();
        this.rightTitle = this.getRightTitle();
        this.rightPanel = this.createRightPanel();

        // Création du panneau de gauche
        this.leftStore = this.getLeftStore();
        this.leftTitle = this.getLeftTitle();
        this.leftPanel = this.createLeftPanel();


        // Création du composant
        var items = [];
        items.push(this.leftPanel);
        items.push(this.createActionPanel());
        items.push(this.rightPanel);

        return items;
    },

    /**
     * Création du panneau de gauche
     */
    createLeftPanel: function() {
        var self = this;
        return Ext.create('Ext.tree.Panel', {
            id: 'leftTree',
            flex: 1,
            title: this.leftTitle,
            store: this.leftStore,
            multiSelect: this.multiSelect,
            columns: this.leftColumns,
            border: true,
            useArrows: true,
            rootVisible: false,

            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    enableDrag: this.enableDD,
                    enableDrop: this.enableDD
                },
                listeners: {
                    beforedrop: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
                        return self.validLeftDropEvent(node, data, overModel, dropPosition, dropFunction, eOpts);
                    }
                }
            },
            listeners: {
                itemdblclick: function(tree, record, index, e, eOpts) {
                    self.moveTo([record ], true);
                },
                'itemclick': function() {
                    // Désélection automatique du panneau de gauche quand un
                    // item à droite
                    // est sélectionné
                    self.rightPanel.getSelectionModel().deselectAll();
                }
            }
        });
    },

    /**
     * Méthode de validation du drop vers le panneau de gauche
     */
    validLeftDropEvent: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
        // A surcharger si besoin
        return true;
    },

    /**
     * Création du panneau de droite
     */
    createRightPanel: function() {
        var self = this;
        return Ext.create('Ext.tree.Panel', {
            id: 'rightTree',
            flex: 1,
            multiSelect: self.multiSelect,
            title: this.rightTitle,
            store: this.rightStore,
            columns: this.rightColumns,
            border: true,
            useArrows: true,
            rootVisible: false,
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    enableDrag: this.enableDD,
                    enableDrop: this.enableDD
                },
                listeners: {
                    beforedrop: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
                        return self.validRightDropEvent(node, data, overModel, dropPosition, dropFunction, eOpts);
                    }
                }
            },
            listeners: {
                itemdblclick: function(tree, record, index, e, eOpts) {
                    self.moveTo([record ], false);
                },
                'itemclick': function() {
                    // Désélection automatique du panneau de gauche quand un
                    // item à droite
                    // est sélectionné
                    self.leftPanel.getSelectionModel().deselectAll();
                }
            }
        });
    },

    /**
     * Méthode de validation du drop vers le panneau de droite
     */
    validRightDropEvent: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
        // (A surcharger si besoin)
        return true;
    },

    createActionPanel: function() {
        var self = this;
        return {
            id: 'actionPanel',
            xtype: 'panel',
            width: 33, // 22 + 2 (taille du bouton + bordure) + (5 + 5) de
            // padding )
            padding: 5,
            border: false,
            layout: {
                type: 'vbox',
                // alignement automatique au centre
                pack: 'center'
            },
            items: [{
                xtype: 'button',
                height: 25,
                text: '>',
                itemId: 'toRight',
                listeners: {
                    click: function() {
                        // Elements sélectionnés de l'arbre de gauche
                        self.moveTo(self.leftPanel.getSelectionModel().getSelection(), true);
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
                        // Elements sélectionnés de l'arbre de droite
                        self.moveTo(self.rightPanel.getSelectionModel().getSelection(), false);
                    }
                }
            } ]
        };
    },

    /**
     * Méthode générale de déplacement des éléments d'un arbre vers l'autre
     */
    moveTo: function(selection, right) {
        var tree = this.rightPanel;
        if (right) {
            this.moveToRightEvent(selection);
        } else {
            tree = this.leftPanel;
            this.moveToLeftEvent(selection);
        }
        // Tri automatique après avoir déplacé un élément
        // Voir s'il faut rajouter un paramètre pour trier automatiquement
        tree.getStore().sort('text', 'ASC');
    },

    moveToRightEvent: function(selection) {
        throw 'unimplemented method: DragAndDropTree.moveToRightEvent';
    },

    moveToLeftEvent: function(selection) {
        throw 'unimplemented method: DragAndDropTree.moveToLeftEvent';
    },

    /**
     * TreeStore de l'arbre à gauche
     */
    getLeftStore: function() {
        throw 'unimplemented method: DragAndDropTree.getLeftStore';
    },

    /**
     * TreeStore de l'arbre à droite
     */
    getRightStore: function() {
        throw 'unimplemented method: DragAndDropTree.getRightStore';
    },

    getRightPanel: function() {
        return this.rightPanel;
    },

    getLeftPanel: function() {
        return this.leftPanel;
    },

    getLeftTitle: function() {
        return 'A_DEFINIR';
    },

    getRightTitle: function() {
        return 'A_DEFINIR';
    },

    getMultiSelect: function() {
        return false;
    },

    getLeftColumns: function() {
        return this.leftColumns;
    },

    getRightColumns: function() {
        return this.rightColumns;
    }


});
