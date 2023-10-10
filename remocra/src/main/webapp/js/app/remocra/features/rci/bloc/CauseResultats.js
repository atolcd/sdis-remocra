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
        store : {
            autoLoad: true,
            xtype: 'crTypeRciPromFamille',
            model: 'Sdis.Remocra.model.TypeRciPromFamille',
            listeners: {
                load: function(store, records, successful, opt) {
                    store.insert(0, [Ext.create('Sdis.Remocra.model.TypeRciPromFamille', {id: null, nom: 'Non renseigné'})]);
                    store.sort('id', 'ASC');
                    var combo = Ext.getCmp('famillePromethee');
                    if (combo.value === undefined || combo.value === null)  {
                        combo.select(combo.store.data.items.filter(function(v) { return v.data.id === null; })[0]);
                    }
                }
            }
        },
        value: null,
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'famillePromethee',
        name : 'famillePromethee',
        id : 'famillePromethee'
    }, {
        xtype : 'combo',
        fieldLabel : 'Degré de certitude',
        store : {
            autoLoad: true,
            xtype: 'crTypeRciDegreCertitude',
            model: 'Sdis.Remocra.model.TypeRciDegreCertitude',
            listeners: {
                load: function(store, records, successful, opt) {
                    store.insert(0, [Ext.create('Sdis.Remocra.model.TypeRciDegreCertitude', {id: null, nom: 'Non renseigné'})]);
                    store.sort('id', 'ASC');
                    var combo = Ext.getCmp('degreCertitude');
                    if (combo.value === undefined || combo.value === null)  {
                        combo.select(combo.store.data.items.filter(function(v) { return v.data.id === null; })[0]);
                    }
                }
            }
        },
        value: null,
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'degreCertitude',
        name : 'degreCertitude',
        id : 'degreCertitude'
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
        name : 'partitionPromethee',
        disabled: true
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
        name : 'categoriePromethee',
        disabled: true
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