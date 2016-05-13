Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.ZoneCompetence', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    },{
        name: 'nom',
        type: 'string'
    },{
        name: 'code',
        type: 'string'
    },{
        name: 'geometrie',
        type: 'string'
    }],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../zonecompetences')
    },

    inheritableStatics: {
        checkByXY: function(x, y, srid, config) {
            config = Ext.apply({}, config);
            config = Ext.applyIf(config, {
                baseParams: {
                    manage500: true
                },
                url: Sdis.Remocra.util.Util.withBaseUrl('../zonecompetences/check'),
                params: {
                    wkt: "POINT(" + x + " " + y + ")",
                    srid: srid
                }
            });
            Ext.Ajax.request(config);
        }
    }
});
