Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.ComboForce');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Localisation', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.localisation',

    title: 'Localisation et accès',
    layout: 'anchor',
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120
    },

    items: [{
        fieldLabel: 'Adresse',
        labelAlign: 'left',
        labelStyle: 'font-weight:bold;',
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            fieldLabel: 'Numéro',
            xtype: 'textfield',
            name: 'numVoie',
            labelWidth: 50
        }, {
            fieldLabel: 'Voie',
            xtype: 'comboforce',
            name: 'voie',
            store: {
                type: 'crVoie',
                autoLoad: true
            },
            displayField: 'nom',
            valueField: 'nom',
            forceSelection: true,
            flex: 1,
            labelAlign: 'right',
            width: 200,
            queryCaching: false
        } ]
    }, {
        xtype: 'textfield',
        fieldLabel: 'Lieu-dit',
        name: 'lieuDit',
        labelWidth: 175
    }, {
        fieldLabel: 'Type d\'accès',
        labelWidth: 175,
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'comboforce',
            name: 'acces',
            width: 200,
            store: {
                type: 'crTypeOldebAcces'
            },
            displayField: 'nom',
            valueField: 'id',
            forceSelection: false,
            queryCaching: false
        }, {
            fieldLabel: 'Largeur d\'accès en m',
            labelWidth: 150,
            xtype: 'textfield',
            name: 'largeurAcces',
            flex: 1,
            labelAlign: 'right'
        } ]
    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'label',
            fieldLabel: ' ',
            width: 175
        }, {
            boxLabel: 'Portail électrique',
            labelAlign: 'right',
            xtype: 'checkbox',
            name: 'portailElectrique',
            value: 'portailElectrique',
            width: 280
        }, {
            fieldLabel: 'Code portail',
            labelWidth: 75,
            xtype: 'textfield',
            name: 'codePortail',
            value: '',
            width: 368,
            disabled: true,
            allowBlank: false
        } ]
    } ]
});