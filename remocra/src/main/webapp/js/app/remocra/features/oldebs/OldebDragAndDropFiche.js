// Implémentation du composant AbstractDragAndDropTree
Ext.require('Sdis.Remocra.features.oldebs.OldebDragAndDrop');

Ext.define('Sdis.Remocra.features.oldebs.OldebDragAndDropFiche', {
    extend: 'Ext.window.Window',
    alias: 'widget.oldebdragdropfiche',
    modal: true,
    width: 900,
    height: 500,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: 'A REDEFINIR',
    bodyPadding: 10,
    tabItems: [],

    initComponent: function() {
        this.items = [{
            xtype: 'oldebdragdroptree',
            flex: 1
        } ];

        this.buttons = [{
            text: 'Valider',
            // disabled: true,
            itemId: 'ok'
        }, {
            itemId: 'close',
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.close();
            }
        } ];
        this.callParent(arguments);
    }

});
