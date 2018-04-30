Ext.require('Ext.ux.grid.plugin.HeaderFilters');

Ext.define('Sdis.Remocra.features.hydrants.TabDonnees', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crHydrantsDonnees',
    sortableColumns: false,
    dockedItems: [ {
        xtype: 'pagingtoolbar',
        dock: 'bottom',
        displayInfo: true
    }],

    initComponent: function() {

        var me = this;
        me.columns = [];

        this.callParent(arguments);
    }
});
