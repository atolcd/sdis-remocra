Ext.require('Sdis.Remocra.model.AlerteElt');
Ext.require('Sdis.Remocra.model.Utilisateur');

Ext.define('Sdis.Remocra.model.Alerte', {
    extend : 'Ext.data.Model',

    // Champs supplémentaires
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'dateModification',
        type : 'date',
        dateFormat : 'c'
    }, {
        name : 'dateConstat',
        type : 'date',
        dateFormat : 'c'
    }, {
        name : 'commentaire',
        type : 'string'
    }, {
        name : 'etat',
        type : 'boolean'
    }, {
        name : 'geometrie',
        type : 'string'
    } ],

    // Validations supplémentaires
    validations : [],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'rapporteur',
        getterName : 'getRapporteur',
        setterName : 'setRapporteur',
        associatedName : 'Rapporteur',
        persist : true
    }, {

        type : 'hasMany',
        model : 'Sdis.Remocra.model.AlerteElt',
        associationKey : 'alerteElts',
        name : 'alerteElts',
        persist : true,
        getterName : 'getAlerteElts',
        setterName : 'setAlerteElts'
    } ]
});
