Ext.require('Sdis.Remocra.store.Thematique');
Ext.require('Sdis.Remocra.store.Metadonnee');
Ext.require('Sdis.Remocra.features.metadonnees.Metadonnees');

Ext.define('Sdis.Remocra.controller.Metadonnee', {
    extend: 'Ext.app.Controller',

    stores: ['Thematique','Metadonnee'],
    views: [],

    refs: [{
        ref: 'contentPanel',
        selector: 'crMetadonnees #contentPanel'
    },{
        ref: 'meta',
        selector: 'crMetadonnees dataview'

    },{
        ref: 'backButton',
        selector: 'crMetadonnees button'

    },{
        ref: 'cboThematique',
        selector: 'crMetadonnees combo'
    }],

    init: function() {
        this.control({
            'crMetadonnees combo': {
                select: this.onSelectThematique
            },
            'crMetadonnees dataview': {
                itemclick: this.onSelectMetadonnee
            },
            'crMetadonnees button': {
                click: this.onClickBackButton
            },
            'crMetadonnees': {
                afterrender: this.onAfterRender
            }
        });
    },

    onAfterRender: function() {
        var cbo = this.getCboThematique();
        if (cbo.getStore().count() > 0) {
            if (cbo.getStore().getById(0)) {
                cbo.setValue(cbo.getStore().getById(0));
                this.onSelectThematique(cbo, [cbo.getStore().getById(0)]);
            }
        } else {
            cbo.getStore().on('load', function(store, records) {
                if (store.getById(0) != null) {
                    cbo.select(store.getById(0));
                    this.onSelectThematique(cbo, [store.getById(0)]);
                }
            }, this, {
                single: true
            });

        }
    },

    onSelectThematique: function(combo, records) {
        this.updateElementVisibility();
        var metaStore = this.getStore('Metadonnee');
        metaStore.clearFilter(true);
        metaStore.filter('thematique', records[0].getId());
    },

    onSelectMetadonnee: function(dataselection, record) {
        this.updateElementVisibility(record.get('urlFiche'));
    },

    onClickBackButton: function() {
        this.updateElementVisibility();
    },

    updateElementVisibility: function(url) {
        this.getMeta().setVisible(url == null);
        this.getBackButton().setVisible(url != null);
        this.getContentPanel().setVisible(url != null);
        this.getContentPanel().iframe.dom.src = (url == null ? "" : url);
    }

});