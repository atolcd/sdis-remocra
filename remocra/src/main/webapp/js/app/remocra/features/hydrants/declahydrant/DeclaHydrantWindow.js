Ext.require('Sdis.Remocra.widget.DepotWindow');

Ext.define('Sdis.Remocra.features.hydrants.declahydrant.DeclaHydrantWindow', {
    extend: 'Sdis.Remocra.widget.DepotWindow',

    title: 'Déclaration de point(s) d\'eau',
    aFileLbl: 'un dossier de déclaration de point(s) d\'eau',
    fileLbl: 'Dossier',
    urlPart: '../depot/declahydrant',
    
    initComponent: function() {
        this.callParent(arguments);
    }
});
