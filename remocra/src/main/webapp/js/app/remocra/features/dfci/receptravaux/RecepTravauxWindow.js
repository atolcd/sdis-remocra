Ext.require('Sdis.Remocra.widget.DepotWindow');

Ext.define('Sdis.Remocra.features.dfci.receptravaux.RecepTravauxWindow', {
    extend: 'Sdis.Remocra.widget.DepotWindow',

    title: 'Réception de travaux',
    aFileLbl: 'un dossier de réception de travaux',
    fileLbl: 'Dossier',
    urlPart: '../depot/receptravaux',
    
    initComponent: function() {
        this.callParent(arguments);
    }
});
