Ext.require('Sdis.Remocra.model.TypeAlerteAno');

Ext.define('Sdis.Remocra.model.AlerteEltAno', {
    extend : 'Ext.data.Model',

    // Champs supplémentaires
    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    } ],

    // Validations supplémentaires
    validations : [],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.AlerteElt',
        associationKey : 'alerteElt',
        getterName : 'getAlerteElt',
        setterName : 'setAlerteElt',
        associatedName : 'AlerteElt'
    }, {

        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeAlerteAno',
        associationKey : 'typeAlerteAno',
        getterName : 'getTypeAlerteAno',
        setterName : 'setTypeAlerteAno',
        associatedName : 'TypeAlerteAno',
        persist : true
    } ]
});
