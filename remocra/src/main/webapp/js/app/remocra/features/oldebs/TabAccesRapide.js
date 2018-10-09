Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require(['Sdis.Remocra.store.CadastreParcelle' ]);
Ext.require(['Sdis.Remocra.store.CadastreSection' ]);
Ext.require(['Sdis.Remocra.store.Oldeb' ]);
Ext.require(['Sdis.Remocra.model.CadastreParcelle' ]);
Ext.require(['Sdis.Remocra.model.CadastreSection' ]);

Ext.define('Sdis.Remocra.features.oldebs.TabAccesRapide', {
    extend: 'Ext.form.Panel',
    alias: 'widget.crOldebAccesRapide',

    title: 'Accès rapide',
    itemId: 'access',
    bodyPadding: 20,

    defaults: {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        width: 900,
        labelWidth: 120,
        defaults: {
            margin: '0 20 0 0',
            width: 200
        }
    },

    items: [{
        fieldLabel: 'Rechercher dans',
        layout: 'hbox',
        items: [{
            xtype: 'radio',
            name: 'radio',
            width: 400,
            inputValue: '1',
            checked: true,
            boxLabel: 'Parcelles soumises à une obligation légale de débroussaillement'
        }, {
            xtype: 'radio',
            name: 'radio',
            inputValue: '2',
            width: 300,
            labelSeparator: '',
            hideEmptyLabel: false,
            boxLabel: 'Parcelles cadastrales'
        } ]
    }, {
        fieldLabel: 'Commune',
        items: [{
            xtype: 'combo',
            emptyText: 'Commune de...',
            minChars: 2,
            hideTrigger: false,
            name: 'commune',
            displayField: 'nom',
            valueField: 'id',
            store: {
                type: 'crCommune',
                filters: [{
                    property: 'zoneCompetence',
                    value: ' '
                } ]
            }
        } ]
    }, {
        fieldLabel: 'Section',
        margin: '0 0 20 0',
        items: [{
            emptyText: 'Section...',
            disabled: true,
            xtype: 'combo',
            name: 'section',
            minChars: 1,
            store: {
                type: 'crCadastreSection'
            },
            displayField: 'numero',
            valueField: 'id',
            forceSelection: true,
            queryCaching: false
        // Changement section avec saisie identique : requête à rejouer
        // quand-même
        } ]
    }, {
        fieldLabel: 'Parcelle',
        margin: '0 0 20 0',
        items: [{
            disabled: true,
            xtype: 'combo',
            emptyText: 'Numéro de parcelle...',
            hideTrigger: true,
            name: 'parcellecadastre',
            minChars: 1,
            store: {
                type: 'crCadastreParcelle'
            },
            displayField: 'numero',
            valueField: 'id',
            forceSelection: true,
            queryCaching: false, // Changement parcelle avec saisie identique
                                    // : requête à rejouer quand-même
            hidden: true
        }, {
            disabled: true,
            xtype: 'combo',
            emptyText: 'Numéro de parcelle...',
            name: 'parcelleoldeb',
            hideTrigger: true,
            minChars: 1,
            store: {
                type: 'crOldeb'
            },
            displayField: 'parcelle',
            valueField: 'id',
            forceSelection: true,
            queryCaching: false, // Changement commune avec saisie identique
                                    // : requête à rejouer quand-même
            hidden: false
        }, {
            xtype: 'button',
            text: 'Localiser',
            itemId: 'locateOldeb',
            width: 100
        } ]
    } ],
    initComponent: function() {
        this.callParent(arguments);
    }

});