Ext.define('Sdis.Remocra.features.prescrits.FichePrescrit', {
    extend: 'Ext.window.Window',
    alias: 'widget.fichePrescrit',
    modal: true,
    width: 500,
    layout: 'fit',

    title: 'Hydrant prescrit',
    bodyPadding: 10,
    tabItems: [],

    initComponent: function() {

        this.items = [ {
            xtype: 'form',
            name: 'fiche',
            layout: 'anchor',
            defaults: {
                labelAlign: 'right',
                labelWidth: 120
            },
            border: false,
            items: [ {
                xtype: 'numberfield',
                fieldLabel: 'Nombre de poteaux',
                name: 'nbPoteaux',
                allowDecimals: false
            }, {
                xtype: 'numberfield',
                fieldLabel: 'Débit (m³/h)',
                name: 'debit',
                allowDecimals: false
            }, {
                xtype: 'datefield',
                fieldLabel: 'Date',
                name: 'datePrescrit',
                format: 'd/m/Y'
            }, {
                xtype: 'fieldcontainer',
                anchor: '100%',
                layout: 'hbox',
                fieldLabel: 'Coord WGS X',
                items: [ {
                    xtype: 'displayfield',
                    name: 'x',
                    width: 135
                }, {
                    xtype: 'displayfield',
                    fieldLabel: 'Y',
                    name: 'y',
                    labelWidth: 40,
                    flex: 1,
                    labelAlign: 'right',
                    maxWidth: 420
                } ]
            } ]
        } ];

        this.tbar = [ {
            xtype: 'displayfield',
            cls: 'hydrant-msg-error',
            value: 'Un petit message d\'erreur ...',
            name: 'errorMsg',
            hidden: true
        } ];

        this.buttons = [ {
            text: 'Valider',
            itemId: 'ok'
        }, {
            itemId: 'close',
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.close();
            }

        } ];
        this.callParent(arguments);
    }

});