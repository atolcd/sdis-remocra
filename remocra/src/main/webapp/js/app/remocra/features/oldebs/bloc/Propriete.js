Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Propriete', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.propriete',

    title: 'Propriétaire',
    layout: 'anchor',
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120

    },
    items: [{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            boxLabel: 'Le propriétaire est un organisme',
            labelAlign: 'right',
            xtype: 'checkbox',
            name: 'organismeProprietaire',
            width: 400
        }, {
            fieldLabel: 'Raison sociale',
            xtype: 'textfield',
            name: 'raisonSocialProprietaire',
            width: 424,
            allowBlank: false,
            disabled: true
        } ]
    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'comboforce',
            name: 'civiliteProprietaire',
            width: 60,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'civilite' ],
                data: [['0', 'M.' ], ['1', 'Mme' ] ]
            }),
            displayField: 'civilite',
            valueField: 'id',
            forceSelection: false,
            allowBlank: false,
            queryCaching: false
        }, {
            fieldLabel: 'Nom',
            labelWidth: 115,
            width: 150,
            xtype: 'textfield',
            name: 'nomProprietaire',
            id: 'nomProprietaire',
            flex: 1,
            labelAlign: 'right',
            allowBlank: false,
            enableKeyEvents: true

        }, {
            fieldLabel: 'Prénom',
            xtype: 'textfield',
            name: 'prenomProprietaire',
            labelWidth: 60,
            width: 150,
            flex: 1,
            allowBlank: false,
            labelAlign: 'right'
        } ]
    }, {
        fieldLabel: 'Adresse',
        labelAlign: 'left',
        labelStyle: 'font-weight:bold;',
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            fieldLabel: 'Numéro',
            xtype: 'textfield',
            name: 'numVoieProprietaire',
            labelWidth: 50,
            width: 150

        }, {
            fieldLabel: 'Voie',
            xtype: 'textfield',
            name: 'voieProprietaire',
            labelWidth: 100,
            flex: 1,
            labelAlign: 'right'
        } ]
    }, {
        xtype: 'textfield',
        fieldLabel: 'Lieu-dit',
        name: 'lieuDitProprietaire',
        labelWidth: 175,
        flex: 1
    }, {

        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            fieldLabel: 'Code postal',
            xtype: 'textfield',
            name: 'codePostalProprietaire',
            labelWidth: 175,
            width: 100,
            flex: 1,
            allowBlank: false,
            labelAlign: 'right'
        }, {
            fieldLabel: 'Ville',
            xtype: 'textfield',
            name: 'villeProprietaire',
            labelWidth: 100,
            flex: 1,
            allowBlank: false,
            labelAlign: 'right'
        }, {
            fieldLabel: 'Pays',
            xtype: 'textfield',
            name: 'paysProprietaire',
            value: 'France',
            labelWidth: 100,
            flex: 1,
            allowBlank: false,
            labelAlign: 'right'
        } ]
    }, {
        fieldLabel: 'Téléphone',
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'textfield',
            name: 'telephoneProprietaire',
            labelWidth: 150,
            flex: 1,
            labelAlign: 'right'
        }, {
            fieldLabel: 'Email',
            xtype: 'textfield',
            name: 'emailProprietaire',
            labelWidth: 100,
            width: 100,
            flex: 1,
            labelAlign: 'right'
        } ]
    }, {
        fieldLabel: 'Type de résidence',

        xtype: 'comboforce',
        name: 'residence',
        store: {
            type: 'crTypeOldebResidence'
        },
        displayField: 'nom',
        valueField: 'id',
        forceSelection: false,
        labelAlign: 'right',
        queryCaching: false,
        allowBlank: false,
        width: 150
    }, {

        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            name: 'newProprio',
            xtype: 'button',
            itemId: 'newProprio',
            text: 'nouveau propriétaire',
            margin: '0 0 0 20'
        }, {
            boxLabel: 'La propriété est en location',
            labelAlign: 'right',
            xtype: 'checkbox',
            name: 'location',
            margin: '0 0 0 500'

        } ]
    } ]
});