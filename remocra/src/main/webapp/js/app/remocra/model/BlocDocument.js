Ext.require('Sdis.Remocra.model.Thematique');
Ext.require('Sdis.Remocra.model.ProfilDroit');

Ext.define('Sdis.Remocra.model.BlocDocument', {
    extend : 'Ext.data.Model',

    // Champs supplémentaires
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'titre',
        type : 'string'
    }, {
        name : 'description',
        type : 'string'
    }, {
        name : 'code',
        type : 'string'
    }, {
        name : 'dateDoc',
        type : 'date',
        dateFormat : 'c'
    } ],

    associations : [ {
        type : 'hasMany',
        model : 'Sdis.Remocra.model.Thematique',
        associationKey : 'thematiques',
        name : 'thematiques',
        associatedName : 'thematiques',
        persist : true
    }, {
        type : 'hasMany',
        model : 'Sdis.Remocra.model.ProfilDroit',
        associationKey : 'profilDroits',
        name : 'profilDroits',
        associatedName : 'profilDroits',
        persist : true
    } ],

    // Validations supplémentaires
    validations : [],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../blocdocuments')
    }
});
