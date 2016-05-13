Ext.require('Ext.toolbar.Paging');

Ext.define('Sdis.Remocra.widget.LightPaging', {
    extend : 'Ext.toolbar.Paging',

    onRender : function() {
        this.callParent(arguments);
        this.items.each(this.maybeHideItem);
    },

    maybeHideItem : function(item) {
        if (item.itemId != 'prev' && item.itemId != 'next'
                && item.xtype != 'tbfill' && item.itemId != 'displayItem') {
            // On masque !
            item.hide();
        }
    }
});