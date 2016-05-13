Ext.require('Sdis.Remocra.model.SousTypeAlerteElt');
Ext.require('Sdis.Remocra.model.AlerteEltAno');

Ext.define('Sdis.Remocra.model.AlerteElt', {
    extend : 'Ext.data.Model',

    // Champs supplémentaires
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'commentaire',
        type : 'string'
    }, {
        name : 'geometrie',
        type : 'string'
    } ],

    // Validations supplémentaires
    validations : [],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.SousTypeAlerteElt',
        associationKey : 'sousTypeAlerteElt',
        getterName : 'getSousTypeAlerteElt',
        setterName : 'setSousTypeAlerteElt',
        associatedName : 'SousTypeAlerteElt',
        persist : true
    }, {

        type : 'hasMany',
        model : 'Sdis.Remocra.model.AlerteEltAno',
        associationKey : 'alerteEltAnos',
        name : 'alerteEltAnos',
        persist : true,
        getterName : 'getAlerteEltAnos',
        setterName : 'setAlerteEltAnos'
    } ]
});
