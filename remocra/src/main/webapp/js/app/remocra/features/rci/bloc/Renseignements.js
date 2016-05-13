Ext.require('Ext.form.Panel');

Ext.define('Sdis.Remocra.features.rci.bloc.Renseignements', {
    extend : 'Ext.form.Panel',
    alias : 'widget.rciRenseignements',

    title : 'Renseignements incendie',

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

    items : [{
        colspan : 2,
        xtype : 'displayfield',
        labelAlign : 'left',
        style : 'margin-left:20px;',
        fieldLabel : '<b>Renseignements</b>'
    }, {
        fieldLabel : 'Date ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            columnWidth : 0.5
        },
        items : [ {
            xtype : 'datefield',
            itemId : 'dateIncendie',
            name : 'dateIncendie',
            allowBlank : false
        }, {
            xtype : 'timefield',
            format : 'H:i',
            increment: 30,
            itemId : 'heureIncendie',
            name : 'heureIncendie'
        } ]
    }, {
        xtype : 'combo',
        fieldLabel : 'Origine de l\'alerte',
        store : 'TypeRciOrigineAlerte',
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'origineAlerte',
        name : 'origineAlerte',
        allowBlank : false
    }, {
        xtype : 'combo',
        fieldLabel : 'Commune',
        emptyText : 'Commune...',
        store : {
            storeId : 'commune',
            model : 'Sdis.Remocra.model.Commune',
            pageSize : 10,
            remoteSort : true,
            remoteFilter : true
        },
        displayField : 'nom',
        valueField : 'id',
        forceSelection : true,
        queryMode : 'remote',
        triggerAction : "all",
        hideTrigger : true,
        typeAhead : true,
        minChars : 3,
        itemId : 'commune',
        name : 'commune',
        allowBlank : false
    }, {
        xtype : 'combo',
        fieldLabel : 'Voie',
        emptyText : 'Voie...',
        store : {
            type : 'crVoie'
        },
        queryMode : 'remote',
        displayField : 'nom',
        valueField : 'nom',
        triggerAction : "all",
        hideTrigger : false,
        typeAhead : true,
        minChars : 3,
        allowBlank : true,
        itemId : 'voie',
        name : 'voie'
    }, {
        colspan : 2,
        xtype : 'textarea',
        fieldLabel : 'Complément',
        width : 800,
        itemId : 'complement',
        name : 'complement'
    }, {
        colspan : 2,
        xtype : 'displayfield',
        labelAlign : 'left',
        style : 'margin-left:20px;',
        fieldLabel : '<b>Arrivée sur site des référents</b>'
    }, {
        xtype: 'combo',
        fieldLabel : 'DDTM - ONF',
        queryMode: 'remote',
        valueField: 'id',
        displayField: 'prenomNomIdentifiant',
        editable: false,
        forceSelection: false,
        store: {
            type: 'utilisateur',
            autoLoad: true,
            remoteFilter: true,
            remoteSort: true,
            sorters: [{
                property: 'nom',
                direction: 'ASC'
            }],
            filters : [
                {'property':'organismeTypeCodes', value:'DDTM%ONF'},
                {'property':'hasRight', value:'RCI.Create'},
                {'property':'dnasp', value:'true'}],
            pageSize: 15
        },
        pageSize: true, // bizarrerie ExtJS
        itemId : 'arriveeDdtmOnf',
        name : 'arriveeDdtmOnf'
    }, {
        xtype: 'combo',
        fieldLabel : 'SDIS',
        queryMode: 'remote',
        valueField: 'id',
        displayField: 'prenomNomIdentifiant',
        editable: false,
        forceSelection: false,
        store: {
            type: 'utilisateur',
            autoLoad: true,
            remoteFilter: true,
            remoteSort: true,
            sorters: [{
                property: 'nom',
                direction: 'ASC'
            }],
            filters : [
                {'property':'organismeTypeCodes', value:'SDIS%CIS%CIS-ETAPE-1%CIS-ETAPE-2'},
                {'property':'hasRight', value:'RCI.Create'},
                {'property':'dnasp', value:'true'}],
            pageSize: 15
        },
        pageSize: true, // bizarrerie ExtJS
        itemId : 'arriveeSdis',
        name : 'arriveeSdis'
    }, {
        xtype: 'combo',
        fieldLabel : 'Gendarmerie',
        queryMode: 'remote',
        valueField: 'id',
        displayField: 'prenomNomIdentifiant',
        editable: false,
        forceSelection: false,
        store: {
            type: 'utilisateur',
            autoLoad: true,
            remoteFilter: true,
            remoteSort: true,
            sorters: [{
                property: 'nom',
                direction: 'ASC'
            }],
            filters : [
                {'property':'organismeTypeCodes', value:'GENDARMERIE'},
                {'property':'hasRight', value:'RCI.Create'},
                {'property':'dnasp', value:'true'}],
            pageSize: 15
        },
        pageSize: true, // bizarrerie ExtJS
        itemId : 'arriveeGendarmerie',
        name : 'arriveeGendarmerie'
    }, {
        xtype: 'combo',
        fieldLabel : 'Police',
        queryMode: 'remote',
        valueField: 'id',
        displayField: 'prenomNomIdentifiant',
        editable: false,
        forceSelection: false,
        store: {
            type: 'utilisateur',
            autoLoad: true,
            remoteFilter: true,
            remoteSort: true,
            sorters: [{
                property: 'nom',
                direction: 'ASC'
            }],
            filters : [
                {'property':'organismeTypeCodes', value:'POLICE'},
                {'property':'hasRight', value:'RCI.Create'},
                {'property':'dnasp', value:'true'}],
            pageSize: 15
        },
        pageSize: true, // bizarrerie ExtJS
        itemId : 'arriveePolice',
        name : 'arriveePolice'
    }]
});