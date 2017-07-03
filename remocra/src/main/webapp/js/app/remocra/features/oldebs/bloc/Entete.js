Ext.define('Sdis.Remocra.features.oldebs.bloc.Entete', {
    extend: 'Ext.container.Container',
    alias: 'widget.oldebs.entete',

    layout: {
        type: 'hbox',
        labelWidth: 100
    },
    items: [{
        fieldLabel: 'Commune',
        labelWidth: 60,
        xtype: 'combo',
        minChars: 2,
        hideTrigger: false,
        name: 'commune',
        displayField: 'nom',
        valueField: 'id',
        allowBlank: false,
        readOnly: true,
        store: {
            autoLoad: false,
            type: 'crCommune',
            filters: [{
                property: 'oldebGeom',
                value: ' '
            } ]
        }
    }, {
        xtype: 'textfield',
        name: 'section',
        fieldLabel: 'Section',
        labelWidth: 40,
        allowBlank: false,
        flex: 0.5,
        width: 10,
        margin: '0 0 0 5'
    }, {
        xtype: 'numberfield',
        allowDecimals: false,
        hideTrigger: true,
        name: 'parcelle',
        fieldLabel: 'Parcelle',
        labelWidth: 45,
        allowBlank: false,
        flex: 0.5,
        width: 50,
        margin: '0 5 0 5'
    }, {
        fieldLabel: 'Type de zone',
        labelWidth: 80,
        xtype: 'comboforce',
        name: 'zoneUrbanisme',
        store: {
            type: 'crTypeOldebZoneUrbanisme'
        },
        displayField: 'nom',
        valueField: 'id',
        forceSelection: true,
        queryCaching: false,
        allowBlank: false,
        width: 80,
        flex: 1
    }, {
        xtype: 'displayfield',
        name: 'uniqueKey',
        hidden: true
    } ]

});