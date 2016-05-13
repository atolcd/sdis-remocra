Ext.require('Sdis.Remocra.features.cartographie.Config');

Ext.define('Sdis.Remocra.features.cartographie.Cartographie', {
    extend : 'Ext.tab.Panel',
    alias : 'widget.crCartographie',

    title : 'Cartographie',
    
    plain : true,
    minTabWidth : 100,
    tabBar : {
        defaults : {
            margin : '0 0 0 8'
        }
    },

    initComponent : function() {
        if (!Sdis.Remocra.Rights.getRight('CARTOGRAPHIES').Create) {
            this.items = [{
                title : 'Vous ne disposez pas de droits nécessaires pour accéder à ce module.'
            }];
            this.callParent(arguments);
            return;
        }
        this.items = [ {
            xtype : 'crCartographieConfig',
            margin : 10
        } ];
        this.callParent(arguments);
    }

});