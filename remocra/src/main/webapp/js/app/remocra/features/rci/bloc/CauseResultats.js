Ext.require('Ext.form.Panel');

Ext.define('Sdis.Remocra.features.rci.bloc.CauseResultats', {
    extend : 'Ext.form.Panel',
    alias : 'widget.rciCauseResultats',

    title : 'Cause et résultats des investigations',

    defaults : {
        labelAlign : 'right',
        labelWidth : 180,
        labelSeparator : '',
        width : 400
    },

    layout : {
        type : 'table',
        columns : 2
    },

    items : [ {
        xtype : 'combo',
        fieldLabel : 'Prométhée famille',
        store : 'TypeRciPromFamille',
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'famillePromethee',
        name : 'famillePromethee'
    }, {
        xtype : 'combo',
        fieldLabel : 'Degré de certitude',
        store : 'TypeRciDegreCertitude',
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'degreCertitude',
        name : 'degreCertitude'
    }, {
        colspan : 2,
        xtype : 'combo',
        fieldLabel : 'Partition',
        store : 'TypeRciPromPartition',
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'partitionPromethee',
        name : 'partitionPromethee'
    }, {
        colspan : 2,
        xtype : 'combo',
        fieldLabel : 'Catégorie',
        store : 'TypeRciPromCategorie',
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'categoriePromethee',
        name : 'categoriePromethee'
    }, {
        colspan : 2,
        xtype : 'displayfield',
        labelAlign : 'left',
        style : 'margin-left:20px;',
        fieldLabel : 'Commentaires et conclusions'
    }, {
        colspan : 2,
        xtype : 'textarea',
        style : 'margin-left:20px;',
        width : 780, // 800 - marge gauche
        itemId : 'commentaireConclusions',
        name : 'commentaireConclusions'
    } ]
});