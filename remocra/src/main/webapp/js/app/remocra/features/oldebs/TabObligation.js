Ext.require(['Sdis.Remocra.store.Oldeb' ]);
Ext.require(['Sdis.Remocra.store.CadastreParcelle' ]);
Ext.require(['Sdis.Remocra.store.CadastreSection' ]);
Ext.require(['Sdis.Remocra.store.TypeOldebZoneUrbanisme' ]);
Ext.require(['Sdis.Remocra.store.TypeOldebAvis' ]);
Ext.require(['Sdis.Remocra.store.TypeOldebDebroussaillement' ]);
Ext.require(['Sdis.Remocra.model.CadastreParcelle' ]);
Ext.require(['Sdis.Remocra.model.CadastreSection' ]);

Ext.require('Ext.ux.grid.plugin.HeaderFilters');
Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.define('Sdis.Remocra.features.oldebs.TabObligation', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.crOldebObligation',

    title: 'Obligations légales',
    itemId: 'obligation',

    enableColumnResize: false,
    enableColumnMove: false,

    store: {
        type: 'crOldeb',
        pageSize: 15,
        autoLoad: false,
        filters: [{
            // filtrer à -1 pour empecher le premier load de store
            property: 'communeId',
            value: '-1'
        }, {
            property: 'actif',
            value: 'true'
        } ]

    },
    height: 500,
    forceFit: true,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Localiser',
            itemId: 'locateOldeb',
            iconCls: 'zoomIcon',
            disabled: true
        }, {
            text: 'Ouvrir la fiche',
            itemId: 'openOldeb',
            iconCls: 'edit-infoIcon',
            disabled: true
        }, {

            iconCls: 'download-atlasIcon',
            itemId: 'printOldeb',
            text: 'Exporter la fiche',
            disabled: true
        } ]
    }, {
        xtype: 'pagingtoolbar',
        store: null,
        dock: 'bottom',
        displayInfo: true
    }, {

        items: [{
            xtype: 'combo',
            emptyText: 'Commune de...',
            id: 'commune',
            fieldLabel: 'Commune',
            margin: '10 10 10 10',
            width: 300,
            minChars: 2,
            hideTrigger: false,
            name: 'commune',
            displayField: 'nom',
            valueField: 'id',
            store: {
                type: 'crCommune',
                autoLoad: true,
                filters: [{
                    property: 'zoneCompetence',
                    value: ' '
                } ]
            }
        } ]
    } ],

    initComponent: function() {

        var me = this, headerfilter = Ext.create('Ext.ux.grid.plugin.HeaderFilters', {
            pluginId: 'headerfilters'
        });
        me.plugins = headerfilter;

        var cfg = Sdis.Remocra.appInstance.configOldeb;

        var deferredApplyFilter = Ext.Function.createBuffered(function() {
            headerfilter.applyFilters();
        }, 200);

        me.columns = [{
            text: 'Section',
            dataIndex: 'section',
            filterable: true,
            filter: {
                xtype: 'combo',
                emptyText: 'Section...',
                id: 'section',
                filterName: 'section',
                displayField: 'numero',
                valueField: 'numero',
                queryMode: 'local',
                typeAhead: true,
                store: {
                    type: 'crCadastreSection'

                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                        this.getStore().load();
                    },
                    render: function(combo, options) {
                        var store = combo.getStore();
                        if (!store.loaded) {
                            store.load();
                        }
                    },
                    change: deferredApplyFilter
                }
            }
        }, {
            text: 'Parcelle',
            dataIndex: 'parcelle',
            filterable: true,
            filter: {
                xtype: 'textfield',
                emptyText: 'Numéro de parcelle...',
                hideTrigger: true,
                listeners: {
                    change: deferredApplyFilter
                }

            }
        }, {
            text: 'Adresse',
            dataIndex: 'adresse',
            align: 'center',
            filterable: true

        }, {
            text: 'Type zone',
            dataIndex: 'nomZoneUrbanisme',
            filterable: true,
            filter: {
                xtype: 'combo',
                filterName: 'zoneUrbanismeId',
                displayField: 'nom',
                valueField: 'id',
                queryMode: 'remote',
                typeAhead: true,
                store: {
                    type: 'crTypeOldebZoneUrbanisme'

                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    },
                    render: function(combo, options) {
                        var store = combo.getStore();
                        if (!store.loaded) {
                            store.load();
                        }
                    },
                    change: deferredApplyFilter
                }
            }
        }, {
            text: 'Dernière visite',
            dataIndex: 'dateDerniereVisite',
            align: 'center',
            renderer: Ext.util.Format.dateRenderer('d/m/Y')

        }, {
            text: 'Débroussaillement',
            dataIndex: 'debroussaillement',
            filterable: true,
            filter: {
                xtype: 'combo',
                filterName: 'debroussaillementId',
                displayField: 'nom',
                valueField: 'id',
                queryMode: 'local',
                typeAhead: true,
                store: {
                    type: 'crTypeOldebDebroussaillement'
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    },
                    render: function(combo, options) {
                        var store = combo.getStore();
                        if (!store.loaded) {
                            store.load();
                        }
                    },
                    change: deferredApplyFilter
                }
            }
        }, {
            text: 'Avis',
            dataIndex: 'avis',
            filterable: true,
            filter: {
                xtype: 'combo',
                filterName: 'avisId',
                displayField: 'nom',
                valueField: 'id',
                queryMode: 'local',
                typeAhead: true,
                store: {
                    type: 'crTypeOldebAvis'
                },
                listeners: {
                    select: function() {
                        headerfilter.applyFilters();
                    },
                    render: function(combo, options) {
                        var store = combo.getStore();
                        if (!store.loaded) {
                            store.load();
                        }
                    },
                    change: deferredApplyFilter
                }
            }
        } ];

        me.callParent(arguments);
    }
});