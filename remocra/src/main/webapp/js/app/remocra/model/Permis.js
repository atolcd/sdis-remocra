Ext.require('Sdis.Remocra.model.Commune');
Ext.require('Sdis.Remocra.model.TypeOrganisme');
Ext.require('Sdis.Remocra.model.TypePermisAvis');
Ext.require('Sdis.Remocra.model.TypePermisInterservice');
Ext.require('Sdis.Remocra.model.Utilisateur');
Ext.require('Sdis.Remocra.model.PermisDocument');

Ext.define('Sdis.Remocra.model.Permis', {
    extend : 'Ext.data.Model',

    // Champs supplémentaires
    fields : [
            {
                name : 'id',
                type : 'int',
                useNull : true
            },
            {
                name : 'dateModification',
                type : 'date',
                dateFormat : 'c'
            },
            {
                name : 'datePermis',
                type : 'date',
                dateFormat : 'c'
            },
            {
                name : 'nom',
                type : 'string'
            },
            {
                name : 'voie',
                type : 'string'
            },
            {
                name : 'complement',
                type : 'string'
            },
            {
                name : 'numero',
                type : 'string'
            },
            {
                name : 'sectionCadastrale',
                type : 'string'
            },
            {
                name : 'parcelleCadastrale',
                type : 'string'
            },
            {
                name : 'observations',
                type : 'string'
            },
            {
                name : 'annee',
                type : 'int'
            },
            {
                name : 'geometrie',
                type : 'string'
            },
            {
                name : 'display',
                persist : false,
                convert : function(v, record) {
                    return record.get('annee') + ' - ' + record.get('numero')
                            + ' ' + record.get('nom');
                }
            } ],

    // Validations supplémentaires
    validations : [],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Commune',
        associationKey : 'commune',
        getterName : 'getCommune',
        setterName : 'setCommune',
        associatedName : 'Commune',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypePermisAvis',
        associationKey : 'avis',
        getterName : 'getAvis',
        setterName : 'setAvis',
        associatedName : 'Avis',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypePermisInterservice',
        associationKey : 'interservice',
        getterName : 'getInterservice',
        setterName : 'setInterservice',
        associatedName : 'Interservice',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeOrganisme',
        associationKey : 'serviceInstructeur',
        getterName : 'getServiceInstructeur',
        setterName : 'setServiceInstructeur',
        associatedName : 'ServiceInstructeur',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'instructeur',
        getterName : 'getInstructeur',
        setterName : 'setInstructeur',
        associatedName : 'Instructeur',
        persist : false
    }, {
        type : 'hasMany',
        model : 'Sdis.Remocra.model.PermisDocument',
        associationKey : 'permisDocuments',
        name : 'permisDocuments',
        associatedName : 'permisDocuments',
        persist : false
    } ]
});
