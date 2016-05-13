Ext.require('Ext.ux.form.ItemSelector');

Ext.define('Sdis.Remocra.widget.SdisItemSelector', {
    extend : 'Ext.ux.form.ItemSelector',

    alias : [ 'widget.sdiditemselectorfield', 'widget.sdisitemselector' ],

    buttons : [ 'addAll', 'add', 'remove', 'removeAll' ],
    buttonsText : {
        addAll : 'Tout ajouter',
        removeAll : 'Tout retirer',

        top : "Déplacer en haut",
        up : "Monter",
        add : "Ajouter",
        remove : "Retirer",
        down : "Descendre",
        bottom : "Déplacer en bas"
    },

    msgTarget : 'under',

    fromTitle : 'Disponibles',
    toTitle : 'Sélectionnés', // Attention au "e"

    initComponent : function() {
        this.addEvents('selectionchanged');
        this.callParent();
    },

    getSelected : function() {
        var list = this.toField.boundList;
        return list.getStore().getRange();
    },
    
    onRemoveAllBtnClick : function() {
        // On sélectionne tout à droite avant d'appeler le déplacement à gauche
        this.toField.boundList.getSelectionModel().selectAll();
        this.onRemoveBtnClick();
    },
    onAddAllBtnClick : function() {
        // On sélectionne tout à gauche avant d'appeler le déplacement à droite
        this.fromField.boundList.getSelectionModel().selectAll();
        this.onAddBtnClick();
    },

    onTopBtnClick : function() {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    },

    onBottomBtnClick : function() {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    },

    onUpBtnClick : function() {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    },

    onDownBtnClick : function() {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    },

    onAddBtnClick : function() {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    },

    onRemoveBtnClick : function() {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    },

    onItemDblClick : function(view) {
        var me = this;
        me.callParent();
        me.fireEvent('selectionchanged', me, me.getValue());
    }

});
