Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.LocalisationPena', {

    extend: 'Ext.form.FieldSet',
    title: 'Localisation',
    alias: 'widget.hydrant.localisationpena',
    layout: 'anchor',
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120
    },
    items: [{
        xtype: 'combo',
        emptyText: 'Commune de...',
        name: 'commune',
        displayField: 'nom',
        valueField: 'id',
        queryMode: 'remote',
        triggerAction: "all",
        hideTrigger: true,
        typeAhead: true,
        minChars: 3,
        fieldLabel: 'Commune',
        maxWidth: 400,
        allowBlank: false,
        store: {
            storeId: 'communePena',
            model: 'Sdis.Remocra.model.Commune',
            proxy: {
               format: 'json',
               type: 'rest',
               headers: {
                   'Accept': 'application/json,application/xml',
                   'Content-Type': 'application/json'
               },
               url: Sdis.Remocra.util.Util.withBaseUrl('../communes/nom'),
               extraParams: {withgeom: false},
               reader: {
                   type: 'json',
                   root: 'data'
               }
            },
            pageSize: 10,
            remoteSort: true,
            remoteFilter: true
        }
    },{
        xtype: 'textfield',
        fieldLabel: 'Lieu-dit',
        name: 'lieuDit'
    },{
        fieldLabel: 'Voie',
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'combo',
            emptyText: 'Voie...',
            name: 'voie',
            store: {
                type: 'crVoie',
                proxy: {
                    format: 'json',
                    type: 'rest',
                    headers: {
                        'Accept': 'application/json,application/xml',
                        'Content-Type': 'application/json'
                    },
                    url: Sdis.Remocra.util.Util.withBaseUrl('../voies/mc'),
                    extraParams: {withgeom: false},
                    reader: {
                        type: 'json',
                        root: 'data'
                    }
                }
            },
            queryMode : 'remote',
            displayField : 'nom',
            valueField : 'nom',
            triggerAction : "all",
            hideTrigger : false,
            typeAhead : true,
            minChars : 3,
            allowBlank : true
        },{
            fieldLabel: 'Carrefour',
            emptyText: 'Carrefour...',
            xtype: 'combo',
            name: 'voie2',
            store: {
                type: 'crVoie'
            },
            queryMode : 'remote',
            displayField : 'nom',
            valueField : 'nom',
            triggerAction : "all",
            hideTrigger : false,
            typeAhead : true,
            minChars : 3,
            allowBlank : true,
            labelWidth: 60,
            flex: 1,
            labelAlign: 'right'
        }]
    },{
        xtype: 'textarea',
        fieldLabel: 'Complément',
        name: 'complement'
    },{
        fieldLabel: 'Coord DFCI',
        xtype: 'displayfield',
        width: 200,
        name: 'coordDFCI'
    },{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        fieldLabel: 'Coord WGS X',
        items: [{
            xtype: 'displayfield',
            name: 'x',
            width: 200
        },{
            xtype: 'displayfield',
            fieldLabel: 'Y',
            name: 'y',
            labelWidth: 120,
            flex: 1,
            labelAlign: 'right',
            maxWidth: 420
        }]
    }]
});