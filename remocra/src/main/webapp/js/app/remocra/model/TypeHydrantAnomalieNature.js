Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeHydrantSaisie');

Ext.define('Sdis.Remocra.model.TypeHydrantAnomalieNature', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'valIndispoTerrestre',
        type : 'int',
        useNull : true
    }, {
        name : 'valIndispoHbe',
        type : 'int',
        useNull : true
    }, {
        name : 'valIndispoAdmin',
        type : 'int',
        useNull : true
    }, {
        name : 'nature',
        type : 'fk'
    }, {
        name : 'anomalie',
        type : 'fk'
    } ],

    associations : [ {
        type : 'hasMany',
        model : 'Sdis.Remocra.model.TypeHydrantSaisie',
        associationKey : 'saisies',
        name : 'saisies',
        associatedName : 'saisies',
        foreignKey : 'fake',
        persist : true
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util
                .withBaseUrl('../typehydrantanomalienatures')
    },

    getMaxValue : function() {
        return Ext.Array.max([ this.get('valIndispoTerrestre'),
                this.get('valIndispoAdmin'), this.get('valIndispoHbe') ]);
    },

    isHBEOnly : function() {
        return this.get('valIndispoTerrestre') == null
                && this.get('valIndispoAdmin') == null
                && this.get('valIndispoHbe') > 0;
    },

    getSaisieByCode : function(code) {
        return this.saisies().findRecord('code', code);
    }
});