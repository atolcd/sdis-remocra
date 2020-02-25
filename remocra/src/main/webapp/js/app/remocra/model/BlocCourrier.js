Ext.require('Sdis.Remocra.model.ProfilDroit');

Ext.define('Sdis.Remocra.model.BlocCourrier', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'codeDocument',
        type : 'string'
    }, {
        name : 'dateDoc',
        type : 'date',
        dateFormat: 'c'
    }, {
        name : 'nomDocument',
        type : 'string'
    }, {
        name : 'objet',
        type : 'string'
    } ],

    // Validations supplémentaires
    validations : [],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../courrier/courrierdocument'),
        extraParams: {
            distinct: 'true'
        }
    }
});
