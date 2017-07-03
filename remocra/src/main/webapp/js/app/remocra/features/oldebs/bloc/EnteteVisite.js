Ext.define('Sdis.Remocra.features.oldebs.bloc.EnteteVisite', {
    extend: 'Ext.container.Container',
    alias: 'widget.oldebs.entetevisite',

    border: false,
    deferredRender: false,
    height: 100,
    layout: 'column',
    defaults: {
        border: false,
        defaults: {
            labelAlign: 'right',
            width: 300
        }
    },

    items: [{
        columnWidth: 0.5,
        padding: '0 0 0 50',
        items: [{
            xtype: 'datefield',
            fieldLabel: 'Date',
            format: 'd/m/Y',
            name: 'dateVisite',
            value: new Date()
        }, {
            fieldLabel: 'Débrouss.parcelle',
            xtype: 'comboforce',
            name: 'nomDebParcelle',
            id: 'nomDebParcelle',
            store: {
                type: 'crTypeOldebDebroussaillement',
                autoLoad: true
            },
            displayField: 'nom',
            valueField: 'id',
            forceSelection: false,
            queryCaching: false
        }, {
            fieldLabel: 'Avis',
            xtype: 'comboforce',
            name: 'nomAvis',
            id: 'nomAvis',
            store: {
                type: 'crTypeOldebAvis',
                autoLoad: true
            },
            displayField: 'nom',
            valueField: 'id',
            forceSelection: false,
            queryCaching: false
        } ]
    }, {
        columnWidth: 0.5,
        items: [{
            fieldLabel: 'Agent',
            xtype: 'textfield',
            name: 'agent'
        }, {
            fieldLabel: 'Débrouss.accès',
            xtype: 'comboforce',
            name: 'nomDebAcces',
            id: 'nomDebAcces',
            store: {
                type: 'crTypeOldebDebroussaillement',
                autoLoad: true
            },
            displayField: 'nom',
            valueField: 'id',
            forceSelection: false,
            queryCaching: false
        }, {
            fieldLabel: 'Action',
            xtype: 'comboforce',
            name: 'nomAction',
            id: 'nomAction',
            store: {
                type: 'crTypeOldebAction',
                autoLoad: true
            },
            displayField: 'nom',
            valueField: 'id',
            forceSelection: false,
            queryCaching: false
        } ]
    } ],

    initComponent: function() {
        this.callParent(arguments);
    }
});