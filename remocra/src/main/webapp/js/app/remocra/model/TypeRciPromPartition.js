Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.model.TypeRciPromFamille');

Ext.define('Sdis.Remocra.model.TypeRciPromPartition', {
    extend : 'Sdis.Remocra.model.TypeReference',

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciPromFamille',
        associationKey : 'famille',
        getterName : 'getFamille',
        setterName : 'setFamille',
        associatedName : 'Famille',
        persist : true
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../typerciprompartitions')
    }
});