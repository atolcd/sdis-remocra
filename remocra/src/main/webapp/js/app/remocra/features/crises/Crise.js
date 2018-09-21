Ext.require('Sdis.Remocra.features.crises.TabGeneral');
Ext.require('Sdis.Remocra.features.crises.TabMapCrise');


Ext.define('Sdis.Remocra.features.crises.Crise', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.crCrise',
    title: 'Suivi cartographique de crise',
    id: 'crises',
    tabPosition: 'top',
    plain: true,
    minTabWidth: 100,
    tabBar: {
        defaults: {
            margin: '0 0 0 8'
        }
    },

    initComponent: function() {
        this.items = [{
                  xtype: 'crCrisesGeneral'
              }];
        this.callParent(arguments);
    },
    urlChanged: function(desc) {
        this.fireEvent('urlChanged', desc);
    }

});
