Ext.require('Sdis.Remocra.store.Thematique');
Ext.require('Sdis.Remocra.store.Metadonnee');
Ext.require('Sdis.Remocra.features.metadonnees.Metadonnees');

Ext.define('Sdis.Remocra.controller.Metadonnee', {
    extend: 'Ext.app.Controller',

    stores: [ 'Thematique', 'Metadonnee' ],
    views: [],

    refs: [ {
        ref: 'contentPanel',
        selector: 'crMetadonnees #contentPanel'
    }, {
        ref: 'meta',
        selector: 'crMetadonnees dataview'

    }, {
        ref: 'backButton',
        selector: 'crMetadonnees button#backButton'

    }, {
        ref: 'telechargerButton',
        selector: 'crMetadonnees button#telechargerButton'

    }, {
        ref: 'cboThematique',
        selector: 'crMetadonnees combo'
    } ],

    init: function() {
        this.control({
            'crMetadonnees combo': {
                select: this.onSelectThematique
            },
            'crMetadonnees dataview': {
                itemclick: this.onSelectMetadonnee
            },
            'crMetadonnees button#backButton': {
                click: this.onClickBackButton
            },
            'crMetadonnees button#telechargerButton': {
                click: this.onTelechargerButton
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
        this.updateElementVisibility(record.get('urlFiche'), record.get('codeExport'));
    },

    onClickBackButton: function() {
        this.updateElementVisibility();
    },

    onTelechargerButton: function() {
        // Message d'information au clic sur le bouton
        Ext.Msg.show({
            title: 'Extraction cartographique',
            msg: 'Votre demande va être enregistrée. Lorsque le fichier sera prêt, vous serez averti par un message électronique.',
            buttons: Ext.Msg.OK,
            icon: Ext.Msg.INFO
          });

        // Récupération de l'information sur le codeExport associé au bouton de la métadonnée
        var telechargerButton = this.getTelechargerButton();
        var codeExport = telechargerButton.getEl().getAttribute("codeexport");

        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/metadonnees/"+codeExport),
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Téléchargement des données',
                        'Votre demande a été prise en compte.', 5);

                    telechargerButton.setDisabled(true);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Téléchargement des données',
                        'Un problème est survenu lors de l\'enregistrement de la demande.' + msg + '.');
                }
            }
        });
    },

    updateElementVisibility: function(url, codeExport) {
        this.getMeta().setVisible(url == null);
        this.getBackButton().setVisible(url != null);
        var isLogged = Sdis.Remocra.network.ServerSession.getUserData('login');
        // Vérification que :
        // - l'utilisateur est identifié
        // - qu'une requête d'export a été enregistrée (codeExport utilisé pour
        // faire le lien)
        // pour que l'utilisateur puisse télécharger les données
        var telechargerButton = this.getTelechargerButton();
        if(codeExport != undefined) {
            telechargerButton.getEl().set({
                "codeexport": codeExport
            });
        }
        telechargerButton.setVisible(isLogged != "" && url != null && codeExport != "");
        this.getContentPanel().setVisible(url != null);
        this.getContentPanel().iframe.dom.src = (url == null ? "" : url);
    }

});