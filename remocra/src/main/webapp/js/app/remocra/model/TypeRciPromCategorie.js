Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.model.TypeRciPromPartition');

Ext.define('Sdis.Remocra.model.TypeRciPromCategorie', {
    extend : 'Sdis.Remocra.model.TypeReference',

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciPromPartition',
        associationKey : 'partition',
        getterName : 'getPartition',
        setterName : 'setPartition',
        associatedName : 'Partition',
        persist : true
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../typercipromcategories')
    }
});