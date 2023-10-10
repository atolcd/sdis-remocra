Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeRciDegreCertitude');
Ext.require('Sdis.Remocra.model.TypeRciOrigineAlerte');
Ext.require('Sdis.Remocra.model.TypeRciRisqueMeteo');
Ext.require('Sdis.Remocra.model.TypeRciPromCategorie');
Ext.require('Sdis.Remocra.model.RciDocument');

Ext.define('Sdis.Remocra.model.Rci', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'dateModification',
        type : 'date',
        dateFormat : 'c'
    }, {
        name : 'dateIncendie',
        type : 'date',
        dateFormat : 'c'
    }, {
        name : 'voie',
        type : 'string',
        useNull : true
    }, {
        name : 'complement',
        type : 'string',
        useNull : true
    }, {
        name : 'geometrie',
        type : 'string'
    }, {
        name : 'jsonGeometrie',
        type : 'auto',
        persist : false,
        useNull : true
    }, {
        name : 'coordDFCI',
        type : 'string'
    }, {
        name : 'pointEclosion',
        type : 'string',
        useNull : true
    }, {
        name : 'gdh',
        type : 'date',
        dateFormat : 'c'
    }, {
        name : 'ventLocal',
        type : 'boolean',
        convert: function (v, record){ // v = nouvelle valeur, sert a convertir un int en boolean
            if(v == 1){
                return true;
            } else if(v == 0){
                   return false;
               } else if(v == -1 || v == null){
                    return null;
               }
        },
        useNull : true,
        allowNull : true
    }, {
        name : 'hygrometrie',
        type : 'int',
        useNull : true
    }, {
        name : 'directionVent',
        type : 'string',
        convert: function (v, record){
            if (v == 'none'){
                return null;
            }
            return v;
        },
        useNull : true
    }, {
        name : 'temperature',
        type : 'float',
        useNull : true
    }, {
        name : 'forceVent',
        type : 'int',
        useNull : true
    }, {
        name : 'indiceRothermel',
        type : 'int',
        useNull : true
    }, {
        name : 'superficieSecours',
        type : 'float',
        useNull : true
    }, {
        name : 'superficieReferent',
        type : 'float',
        useNull : true
    }, {
        name : 'superficieFinale',
        type : 'float',
        useNull : true
    }, {
        name : 'premierEngin',
        type : 'string',
        useNull : true
    }, {
        name : 'premierCos',
        type : 'string',
        useNull : true
    }, {
        name : 'forcesOrdre',
        type : 'string',
        useNull : true
    }, {
        name : 'gelLieux',
        type : 'boolean',
        convert: function (v, record){ // v = nouvelle valeur, sert a convertir un int en boolean
            if(v == 1){
                return true;
            } else if(v == 0){
                   return false;
               } else if(v == -1 || v == null){
                    return null;
               }
        },
        useNull : true,
        allowNull : true
    }, {
        name : 'commentaireConclusions',
        type : 'string',
        useNull : true
    }

    ],

    associations : [ {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'utilisateur',
        getterName : 'getUtilisateur',
        setterName : 'setUtilisateur',
        associatedName : 'Utilisateur',
        persist : false
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'arriveeDdtmOnf',
        getterName : 'getArriveeDdtmOnf',
        setterName : 'setArriveeDdtmOnf',
        associatedName : 'ArriveeDdtmOnf',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'arriveeGendarmerie',
        getterName : 'getArriveeGendarmerie',
        setterName : 'setArriveeGendarmerie',
        associatedName : 'ArriveeGendarmerie',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'arriveePolice',
        getterName : 'getArriveePolice',
        setterName : 'setArriveePolice',
        associatedName : 'ArriveePolice',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Utilisateur',
        associationKey : 'arriveeSdis',
        getterName : 'getArriveeSdis',
        setterName : 'setArriveeSdis',
        associatedName : 'ArriveeSdis',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciOrigineAlerte',
        associationKey : 'origineAlerte',
        getterName : 'getOrigineAlerte',
        setterName : 'setOrigineAlerte',
        associatedName : 'OrigineAlerte',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.Commune',
        associationKey : 'commune',
        getterName : 'getCommune',
        setterName : 'setCommune',
        associatedName : 'Commune',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciPromCategorie',
        associationKey : 'categoriePromethee',
        getterName : 'getCategoriePromethee',
        setterName : 'setCategoriePromethee',
        associatedName : 'CategoriePromethee',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciPromPartition',
        associationKey : 'partitionPromethee',
        getterName : 'getPartitionPromethee',
        setterName : 'setPartitionPromethee',
        associatedName : 'PartitionPromethee',
        persist : true
    }, {
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciPromFamille',
        associationKey : 'famillePromethee',
        getterName : 'getFamillePromethee',
        setterName : 'setFamillePromethee',
        associatedName : 'FamillePromethee',
        persist : true
    },{
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciRisqueMeteo',
        associationKey : 'risqueMeteo',
        getterName : 'getRisqueMeteo',
        setterName : 'setRisqueMeteo',
        associatedName : 'RisqueMeteo',
        persist : true
    },{
        type : 'belongsTo',
        model : 'Sdis.Remocra.model.TypeRciDegreCertitude',
        associationKey : 'degreCertitude',
        getterName : 'getDegreCertitude',
        setterName : 'setDegreCertitude',
        associatedName : 'DegreCertitude',
        persist : true
    }, {
        type : 'hasMany',
        model : 'Sdis.Remocra.model.RciDocument',
        associationKey : 'rciDocuments',
        name : 'rciDocuments',
        persist : false
    } ],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../rci')
    }
});