Ext.require('Ext.grid.Panel');
Ext.require('Sdis.Remocra.model.OldebProprietaire');
Ext.define('Sdis.Remocra.features.oldebs.bloc.GridProprietaires', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.oldeb.gridProprietaires',

    columns: [{
        header: 'RaisonSociale',
        dataIndex: 'raisonSociale',
        flex: 1,
        editor: 'textfield'
    }, {
        header: 'Nom',
        dataIndex: 'nom',
        flex: 1,
        editor: 'textfield'
    }, {
        header: 'Prénom',
        dataIndex: 'prenom',
        flex: 1,
        editor: 'textfield'
    }, {
        header: 'Adresse',
        dataIndex: 'adresse',
        flex: 1,
        editor: 'textfield'
    }, {
        header: 'Code postal',
        dataIndex: 'codePostal',
        flex: 1,
        editor: 'textfield'
    }, {
        header: 'Ville',
        dataIndex: 'ville',
        flex: 1,
        editor: 'textfield'
    } ],
    selType: 'rowmodel',
    height: 300
});