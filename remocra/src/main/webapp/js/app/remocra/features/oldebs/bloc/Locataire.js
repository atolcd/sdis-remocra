Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Locataire', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.locataire',

    title: 'Locataire',
    id: 'locataire',
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120

    },

    items: [{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            boxLabel: 'Le locataire est un organisme',
            labelAlign: 'right',
            xtype: 'checkbox',
            name: 'organismeLocataire',
            width: 400
        }, {
            fieldLabel: 'Raison sociale',
            xtype: 'textfield',
            name: 'raisonSocialLocataire',
            width: 424,
            disabled: true,
            allowBlank: false
        } ]
    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'comboforce',
            name: 'civiliteLocataire',
            width: 60,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'civilite' ],
                data: [['0', 'M.' ], ['1', 'Mme' ] ]
            }),
            displayField: 'civilite',
            valueField: 'id',
            forceSelection: true,
            allowBlank: false,
            queryCaching: false
        }, {
            fieldLabel: 'Nom',
            labelWidth: 115,
            width: 150,
            xtype: 'textfield',
            name: 'nomLocataire',
            flex: 1,
            allowBlank: false,
            labelAlign: 'right'

        }, {
            fieldLabel: 'Prénom',
            xtype: 'textfield',
            name: 'prenomLocataire',
            labelWidth: 60,
            width: 150,
            flex: 1,
            allowBlank: false,
            labelAlign: 'right'
        } ]
    }, {
        fieldLabel: 'Téléphone',
        xtype: 'fieldcontainer',
        labelWidth: 175,
        layout: 'hbox',
        items: [{
            xtype: 'textfield',
            name: 'telephoneLocataire',
            width: 263,
            labelAlign: 'right'
        }, {
            fieldLabel: 'Email',
            xtype: 'textfield',
            name: 'emailLocataire',
            labelWidth: 60,
            width: 380,
            labelAlign: 'right'
        } ]
    } ]
});