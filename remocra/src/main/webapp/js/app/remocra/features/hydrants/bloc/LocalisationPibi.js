Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.ComboForce');

Ext.define('Sdis.Remocra.features.hydrants.bloc.LocalisationPibi', {

    extend: 'Ext.form.FieldSet',
    title: 'Localisation',
    alias: 'widget.hydrant.localisationpibi',
    layout: 'anchor',
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120
    },
    items: [{
        xtype: 'combo',
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
            storeId: 'communePibi',
            model: 'Sdis.Remocra.model.Commune',
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
            name: 'voie',
            width: 275,
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
             allowBlank : true
        },{
            fieldLabel: 'Carrefour',
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
            labelAlign: 'right',
            maxWidth: 420
        }]
    },{
        xtype: 'textarea',
        fieldLabel: 'Complément',
        name: 'complement'
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