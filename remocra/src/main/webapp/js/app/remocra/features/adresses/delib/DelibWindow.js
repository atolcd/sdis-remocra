Ext.require('Sdis.Remocra.widget.DepotWindow');

Ext.define('Sdis.Remocra.features.adresses.delib.DelibWindow', {
    extend: 'Sdis.Remocra.widget.DepotWindow',

    title: 'Délibération',
    aFileLbl: 'une délibération',
    fileLbl: 'Délibération',
    urlPart: '../depot/delib',
    
    initComponent: function() {
        this.callParent(arguments);
    }
});
