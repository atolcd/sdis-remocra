Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.store.Oldeb');
Ext.require('Sdis.Remocra.model.Oldeb');
Ext.require('Sdis.Remocra.model.CadastreParcelle');
Ext.require('Sdis.Remocra.model.CadastreSection');

Ext.define('Sdis.Remocra.controller.oldeb.Oldeb', {
    extend: 'Ext.app.Controller',

    stores: ['CadastreParcelle', 'Commune', 'Oldeb', 'CadastreSection' ],

    refs: [{
        ref: 'tabPanel',
        selector: 'crOldeb'
    }, {
        ref: 'tabMap',
        selector: 'crOldebMap'
    }, {
        ref: 'tabAccess',
        selector: 'crOldebAccesRapide'
    }, {
        ref: 'tabObligation',
        selector: 'crOldebObligation'
    },{
        ref: 'tabDocuments',
        selector: 'crBlocDocumentGrid'
    } ],

    init: function() {
        this.tplXY = new Ext.Template("oldebs/localisation/x/{valX}/y/{valY}");
        this.tplBounds = new Ext.Template("oldebs/localisation/bounds/{bounds}");

        this.control({
            // Page globale
            'crOldeb': {
                tabchange: this.onTabChange,
                urlChanged: this.onUrlChanged,
                afterrender: this.initFromUrl
            },

            // Onglet "Obligation"
            'crOldebObligation': {

                beforerender: this.gridRender,
                selectionchange: this.onSelectOldeb
            },
            'crOldebObligation combo[name=commune]': {
                select: this.onSelectCommuneOldeb
            },
            'crOldebObligation #openOldeb': {
                click: this.showFicheOldebFromGrid
            },
            'crOldebObligation #printOldeb': {
                click: this.printFiche
            },
            'crOldebObligation #locateOldeb': {
                click: this.onLocateOldebFromGrid
            },

            // Onglet "Accès rapide"
            'crOldebAccesRapide combo[name=commune]': {
                select: this.onSelectCommune
            },
            'crOldebAccesRapide combo[name=section]': {
                select: this.onSelectSection
            },
            'crOldebAccesRapide radio': {
                change: this.onChoiceParcelle

            },
            'crOldebAccesRapide #locateOldeb': {
                click: this.onLocateOldebFromAccess
            },

            // Onglet "Localisation" (la carte)
            'crOldebMap': {
                layersadded: this.initControlMap
            },
            'crOldebMap #selectBtn': {
                toggle: function(button, pressed) {
                    this.getTabMap().activateSpecificControl('selectOldeb', pressed);
                }
            },
            'crOldebMap #dessinerBtn': {
                toggle: function(button, pressed) {
                    this.getTabMap().activateSpecificControl('createOldeb', pressed);
                }
            },
            'crOldebMap #editInfoBtn': {
                click: this.showFicheOldebFromMap
            },
            'crOldebMap #deleteBtn': {
                click: this.deleteOldebFromMap
            },
            'crOldebMap #modifBtn': {
                toggle: function(button, pressed) {
                    this.getTabMap().activateSpecificControl('modifyOldeb', pressed);
                }
            },
            'crOldebMap #validModifBtn': {
                click: this.validModication
            },
            'crOldebMap #cancelModifBtn': {
                click: this.cancelModification
            }
        });
    },

    /***********************************************************
     * Gestion URL / méthode globale
     **********************************************************/

    onTabChange: function(tabPanel, newCard, oldCard) {
        var currentHash = Sdis.Remocra.util.Util.getHashTokenNoSharp();
        if (!Ext.isEmpty(currentHash)) {
            currentHash = currentHash.split('/');
            if (currentHash.length >= 2 && currentHash[1] == newCard.itemId) {
                // Changement "programmé" d'onglet -> on ne
                // change pas l'url
                return;
            }
        }
        // Changement "manuel" d'onglet -> on change l'url
        Sdis.Remocra.util.Util.changeHash('oldebs/' + newCard.itemId);
    },
    initFromUrl: function() {
        var p2 = this.getTabPanel().p2, extra = this.getTabPanel().extraParams;
        this.onUrlChanged(p2, extra);
    },

    onUrlChanged: function(itemId, extraParams) {
        var currentTab = this.getTabPanel().getActiveTab();
        if (Ext.isObject(itemId)) {
            extraParams = itemId.extraParams;
            itemId = itemId.p2;
        }
        extraParams = extraParams || {};
        if (currentTab.itemId != itemId) {
            // on switch sur le bon onglet
            this.getTabPanel().setActiveTab(this.getTabPanel().queryById(itemId));
        }

        switch (itemId) {
        case 'localisation':
            this.updateMap(extraParams);
            break;
        case 'obligation':
            this.updateObligation(extraParams);
            break;
        default:
            break;
        }
    },

    updateObligation: function(extraParams) {
        this.getTabObligation().getStore().load();
    },

    /***********************************************************
     * Onglet "Localisation"
     **********************************************************/

    updateMap: function(extraParams) {
        if (extraParams.x && extraParams.y) {
            this.getTabMap().centerToPoint(extraParams.x, extraParams.y);
        } else if (extraParams.bounds) {
            var params = extraParams.bounds.split(',');
            if (params.length == 4) {
                var bounds = new OpenLayers.Bounds(params);
                this.getTabMap().zoomToBounds(bounds);
            }
        }
    },

    initControlMap: function() {
        // Gestion de la sélection d'une oldeb
        this.addEventToControl('selectOldeb', {
            'oldeb_selected': this.onOldebSelected,
            'oldeb_unselected': this.onOldebUnselected
        });

        // Gestion de la modification d'une oldeb
        this.addEventToControl('modifyOldeb', {
            'activate': this.onActivateModificationOldeb,
            'deactivate': this.onDeactivateModificationOldeb
        });

        // Gestion du controle de création d'un oldeb une fois
        // que la feature à été ajoutée
        this.addEventToControl('createOldeb', {
            'activate': this.onBeforeCreateOldeb,
            'featureadded': this.onCreateOldeb
        });

        this.getTabMap().workingLayer.events.on({
            'featuremodified': this.onOldebModified,
            scope: this
        });
    },

    onBeforeCreateOldeb: function() {
        this.getTabMap().workingLayer.removeAllFeatures();
    },

    onCreateOldeb: function(event) {
        this.selectedFeature = event.feature;

        // Création d'une oldeb depuis l'outil de dessin
        this.showFicheOldebViergeFromMap();

        // Désactivation du bouton de création
        var dessinerBtn = this.getTabMap().queryById('dessinerBtn');
        dessinerBtn.toggle(false);
    },

    deleteOldebFromMap: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            this.deleteOldeb(features[0].fid, {
                scope: this,
                success: function() {
                    this.refreshMap();
                    // Désactivation des boutons de modification
                    this.manageVisibilityButtonsModificationOldeb(false);
                    this.manageActivationModificationButtons(false);
                },
                failure: function() {
                    console.warn("Erreur lors de la suppression de l'obligation de débroussaillement");
                }
            });
        }
    },

    refreshMap: function() {
        if (this.getTabMap() != null && this.getTabMap().rendered) {
            // Rafraîchissement de la couche
            this.getTabMap().refreshZonesLayer();
            // Suppression des éléments
            this.getTabMap().workingLayer.removeAllFeatures();
        }
    },

    deleteOldeb: function(id, config) {
        var model = Sdis.Remocra.model.Oldeb;
        if (model != null) {
            model.load(id, {
                scope: this,
                success: function(record) {

                    Ext.Msg.confirm("Suppression de l'obligation de débroussaillement",
                            "Confirmez-vous la suppression de l'obligation de débroussaillement ?", function(buttonId) {
                                if (buttonId == 'yes') {
                                    record.destroy(config);
                                }
                            }, this);
                }
            });
        }
    },

    /**
     * Méthode commune à la sélection et à la désélection d'un
     * oldeb
     */
    onOldebSelected: function(event) {
        var nbSelect = event.features.length;
        this.manageActivationModificationButtons(nbSelect > 0);
        // Désactivation des boutons valider/annuler de la
        // modification
        // à chaque nouvelle sélection
        this.manageDisabledButtonsModificationOldeb(true);

        // Sélection automatique de la première feature renvoyée
        this.selectFeature(event.features[0]);

        // Cas 3 : Plusieurs oldeb
        if (nbSelect > 1) {
            var data = [], i;
            // On propose à l'utilisateur de choisir la feature
            // de travail
            for (i = 0; i < event.features.length; i++) {
                var cFeat = event.features[i];
                data.push([cFeat, cFeat.data['section'] + ' - ' + cFeat.data['parcelle'] ]);
            }
            this.showChoiceDialog(data);
        }
    },

    selectFeature: function(feature) {
        // Nettoyage des features existantes
        this.getTabMap().workingLayer.removeAllFeatures();
        // Ajout de la feature sur le workingLayer
        this.selectedFeature = feature;
        this.getTabMap().workingLayer.addFeatures(feature);
    },

    /**
     * Affichage de la boite de dialogue qui donne le choix de
     * sélection d'un oldeb
     */
    showChoiceDialog: function(data) {
        Ext.widget(
                'sdischoice',
                {
                    id: 'choiceOldebSel',
                    title: 'Obligation de débroussaillement',
                    width: 400,
                    height: undefined,
                    bodyPadding: 10,
                    explanationsConfig: {
                        xtype: 'panel',
                        border: false,
                        html: '<ul style="margin-bottom:10px;">Plusieurs obligations de débroussaillement ont été trouvées.'
                                + '<br/> Veuillez en selectionner une seule.'
                    },
                    cboConfig: {
                        fieldLabel: 'Choix :',
                        store: new Ext.data.SimpleStore({
                            fields: ['feature', 'display' ],
                            data: data
                        }),
                        displayField: 'display',
                        valueField: 'feature',
                        forceSelection: true,
                        editable: false,
                        queryMode: 'local',
                        value: data[0][0]
                    },
                    listeners: {
                        scope: this,
                        cancel: function() {
                            // Désélection
                            this.onOldebUnselected();
                        },
                        change: function(record) {
                            var selectedFeature = record.get('feature');
                            this.selectFeature(selectedFeature);
                        }
                    }
                }).show();
    },

    manageActivationModificationButtons: function(activate) {
        var btnFiche = this.getTabMap().queryById('editInfoBtn');
        var btnDelete = this.getTabMap().queryById('deleteBtn');
        var btnModif = this.getTabMap().queryById('modifBtn');

        btnModif.setDisabled(!activate);
        // Activation/Désactivation du bouton de la fiche
        btnFiche.setDisabled(!activate);
        // Activation/Désactivation du bouton de suppression
        if (btnDelete != null) {
            btnDelete.setDisabled(!activate);
        }
    },

    onOldebUnselected: function(event) {
        this.selectedFeature = null;
        this.manageActivationModificationButtons(false);
        this.getTabMap().workingLayer.removeAllFeatures();
        this.onDeactivateModificationOldeb();
    },

    getSelectedFeatures: function() {
        return Ext.Array.from(this.selectedFeature);
    },

    showFicheOldebFromMap: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            this.showFicheOldeb(features[0].fid);
        } else {
            this.showFicheOldeb();
        }
    },
    showFicheOldebViergeFromMap: function() {
        var features = this.getSelectedFeatures();
        feature = features[0];
        if (features.length == 1) {
            var oldeb = null, model = Sdis.Remocra.model.Oldeb;
            if (model != null) {
                oldeb = Ext.create(model, null);
            }

            if (feature != null) {
                var mapProjection = this.getTabMap().map.getProjection();
                var wktFormat = new OpenLayers.Format.WKT({
                    internalProjection: mapProjection,
                    externalProjection: new OpenLayers.Projection('EPSG:'+SRID)
                });
            }
            var str = wktFormat.write(feature);
            oldeb.set('geometrie', str);
            this.getController('oldeb.Fiche').showFiche(oldeb);

        } else {
            this.showFicheOldeb();
        }
    },

    onOldebModified: function(vector) {
        // Activation des boutons valider/annuler dès la
        // modification de la forme
        this.manageDisabledButtonsModificationOldeb(false);
    },

    onActivateModificationOldeb: function() {
        var feature = this.getSelectedFeatures()[0];
        if (!feature) {
            // Anormal
            this.onDeactivateModificationOldeb();
        }

        // On copie la feature sélectionnée dans la couche de
        // travail
        var workingFeature = feature.clone();
        workingFeature.data['id'] = feature.fid;
        var workingLayer = this.getTabMap().workingLayer;
        workingLayer.removeAllFeatures();
        workingLayer.addFeatures(workingFeature);

        // Ajout de la feature sélectionnée sur la couche de
        // travail pour effectuer la modification
        var modifyControl = this.getTabMap().getSpecificControl('modifyOldeb');
        modifyControl.selectFeature(workingFeature);

        // Sauvegarde de la geometry initiale pour revenir à
        // l'état d'origine s'il y a annulation
        modifyControl.geometrySaved = workingFeature.geometry.clone();
        this.manageVisibilityButtonsModificationOldeb(true);
        // S'il y a une activation c'est que la géométrie n'a
        // pas encore été modifiée
        // donc désactivation par défaut des boutons
        // valider/annuler
        this.manageDisabledButtonsModificationOldeb(true);
    },

    onDeactivateModificationOldeb: function() {
        // A la désactivation de la modification les boutons
        // valider/annuler sont masqués
        this.manageVisibilityButtonsModificationOldeb(false);
    },

    onCancelModifyGeom: function() {
        // Remise à niveau de la géometrie anciennement modifiée
        var modifyControl = this.getTabMap().getSpecificControl('modifyOldeb');
        // Désactivation / Activation du contrôle pour de
        // nouveau avoir la modification active après
        // passage de l'activation de la feature sélectionnée
        modifyControl.deactivate();
        // Remise à niveau de l'old sélectionnée
        this.onActivateModificationOldeb();
        modifyControl.activate();
    },

    validModication: function() {
        // Récupération de la feature de travail
        var feature = this.getTabMap().workingLayer.features[0];
        var mapProjection = this.getTabMap().map.getProjection();
        var wktFormat = new OpenLayers.Format.WKT({
            internalProjection: mapProjection,
            externalProjection: new OpenLayers.Projection('EPSG:'+SRID)
        });
        var transfGeom = wktFormat.extractGeometry(feature.geometry);
        Ext.Ajax.request({
            scope: this,
            method: 'POST',
            url: Sdis.Remocra.util.Util.withBaseUrl('../oldeb/' + feature.data['id'] + '/updategeom'),
            params: {
                geometrie: transfGeom,
                srid: SRID
            },
            callback: function(param, success, response) {
                var res = Ext.decode(response.responseText);
                if (success) {
                    var modifyOldeb = this.getTabMap().getSpecificControl('modifyOldeb');
                    modifyOldeb.geometrySaved = null;
                    Sdis.Remocra.util.Msg.msg('Obligation de débroussaillement', res.message);
                } else {
                    Ext.MessageBox.show({
                        title: "Mise à jour de l'obligation de débroussaillement",
                        msg: res.message,
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                }
                // On désactive l'outil, on désactive le bouton
                // et on rafraîchit la couche des oldebs
                this.onDeactivateModificationOldeb();
                var modifBtn = this.getTabMap().queryById('modifBtn');
                modifBtn.toggle(false);
                this.manageActivationModificationButtons(false);
                this.refreshMap();
            }
        });
    },

    cancelModification: function() {
        this.onCancelModifyGeom();
        this.manageDisabledButtonsModificationOldeb(true);
    },

    manageDisabledButtonsModificationOldeb: function(disabled) {
        var validModifBtn = this.getTabMap().down('#validModifBtn');
        var cancelModifBtn = this.getTabMap().down('#cancelModifBtn');
        if (validModifBtn != null) {
            validModifBtn.setDisabled(disabled);
        }
        if (cancelModifBtn != null) {
            cancelModifBtn.setDisabled(disabled);
        }
    },

    manageVisibilityButtonsModificationOldeb: function(visible) {
        var validModifBtn = this.getTabMap().down('#validModifBtn');
        var cancelModifBtn = this.getTabMap().down('#cancelModifBtn');
        if (validModifBtn != null) {
            validModifBtn.setVisible(visible);
        }
        if (cancelModifBtn != null) {
            cancelModifBtn.setVisible(visible);
        }
    },

    /***********************************************************
     * Onglet "Accès Rapide"
     **********************************************************/

    onChoiceParcelle: function(radioButton, newValue, oldValue) {
        var cboParcellecadastre = this.getTabAccess().getForm().findField('parcellecadastre');
        var cboParcelleoldeb = this.getTabAccess().getForm().findField('parcelleoldeb');
        if (radioButton.inputValue == 1) {
            cboParcelleoldeb.hide();
            cboParcellecadastre.show();
        } else if (radioButton.inputValue == 2) {
            cboParcelleoldeb.show();
            cboParcellecadastre.hide();
        }

    },

    onSelectCommune: function(combo, records) {
        var cboSection = this.getTabAccess().getForm().findField('section');
        cboSection.clearValue();
        if (records.length > 0) {
            var communeId = records[0].getId();
            var section = cboSection.getValueModel();
            if (!section || section.getCommune().getId() != communeId) {
                // Changement de commune : on filtre les
                // sections sur la nouvelle commune
                cboSection.getStore().clearFilter(true);
                cboSection.getStore().filter([{
                    property: "communeId",
                    value: communeId
                } ]);
            }
            cboSection.enable();
        } else {
            cboSection.disable();
        }
    },

    onSelectSection: function(combo, records) {
        var cboParcellecadastre = this.getTabAccess().getForm().findField('parcellecadastre');
        var cboParcelleoldeb = this.getTabAccess().getForm().findField('parcelleoldeb');
        var cboCommune = this.getTabAccess().getForm().findField('commune');
        var communeId = cboCommune.getValue();
        cboParcelleoldeb.clearValue();
        cboParcellecadastre.clearValue();
        if (records.length > 0) {
            var parcellecadastre = cboParcellecadastre.getValueModel();
            var parcelleoldeb = cboParcelleoldeb.getValueModel();
            if (!parcellecadastre || parcellecadastre.getSection().getId() != sectionId) {
                // Changement de commune : on filtre les
                // parcellecadastrale sur la nouvelle section et
                // la commune
                cboParcellecadastre.getStore().clearFilter(true);
                cboParcellecadastre.getStore().filter([{
                    property: "sectionId",
                    value: records[0].getId()
                }, {
                    property: "communeId",
                    value: communeId
                } ]);
            }
            if (!parcelleoldeb || parcelleoldeb.getSection().getId() != sectionId) {
                // Changement de commune : on filtre les
                // parcelleoldeb sur la nouvelle section et la
                // commune
                cboParcelleoldeb.getStore().clearFilter(true);
                cboParcelleoldeb.getStore().filter([{
                    property: "section",
                    value: records[0].get('numero')
                }, {
                    property: "communeId",
                    value: communeId
                }, {
                    property: "actif",
                    value: 'true'
                } ]);
            }
            // on affiche l'un ou l'autre
            cboParcellecadastre.enable();
            cboParcelleoldeb.enable();
        } else {
            cboParcellecadastre.disable();
            cboParcelleoldeb.disable();
        }
    },

    onLocateOldebFromAccess: function() {
        var allValues = this.getTabAccess().getValues();
        if (allValues.radio == 1 && !Ext.isEmpty(allValues.parcelleoldeb)) {
            var oldeb = this.getTabAccess().down('combo[name=parcelleoldeb]').findRecordByValue(allValues.parcelleoldeb);
            this.clearAccess();
            this.onLocateParcelle(oldeb);
        } else {
            this.showError('parcelleoldeb', 'Une parcelle doit être sélectionné');
        }
        if (allValues.radio == 2 && !Ext.isEmpty(allValues.parcellecadastre)) {
            var parcelle = this.getTabAccess().down('combo[name=parcellecadastre]').findRecordByValue(allValues.parcellecadastre);
            this.clearAccess();
            this.onLocateParcelle(parcelle);
        } else {
            this.showError('parcellecadastre', 'Une parcelle doit être sélectionné');
        }
    },

    showError: function(fieldNames, msgs) {
        this.getTabAccess().getForm().isValid();
        if (fieldNames != null) {
            fieldNames = Ext.Array.from(fieldNames);
            msgs = Ext.Array.from(msgs);
            Ext.Array.each(fieldNames, function(fieldName, index) {
                var msg = msgs[0], field = this.getTabAccess().getForm().findField(fieldName);
                if (field) {
                    if (msgs.length > index) {
                        msg = msgs[index];
                    }
                    field.markInvalid(msg);
                } else {
                    console.warn("le champs '" + fieldName + "' n'existe pas");
                }
            }, this);
        }
    },
    onLocateParcelle: function(parcelle) {
        if (parcelle) {
            if (parcelle.get('geometrie') != null) {
                var bounds = Sdis.Remocra.util.Util.getBounds(parcelle.get('geometrie'));
                Sdis.Remocra.util.Util.changeHash(this.tplBounds.apply({
                    bounds: bounds.toBBOX()
                }));
            }
        }
    },
    clearAccess: function() {
        this.clearError();
        this.getTabAccess().getForm().reset();
    },

    clearError: function() {
        this.getTabAccess().getForm().isValid();
    },

    /***********************************************************
     * Onglet "Obligation"
     **********************************************************/

    gridRender: function() {
        // On lie le store de la grille à la PagingToolbar
        var grid = this.getTabObligation();
        this.filterCommune();
        var tbar = grid.query('pagingtoolbar')[0];
        tbar.bindStore(grid.getStore());
        // tbar.doRefresh();
    },
    filterCommune: function() {
        var grid = this.getTabObligation();
        var cboCommune = grid.queryById('commune');
        cboCommune.store.load(function(records, operation, success) {
            // on force la première commune
            cboCommune.setValue(records[0]);
            var communeId = records[0].get('id');
            // on recharge la grille par rapport à la première
            // commune
            grid.getStore().clearFilter(true);
            grid.getStore().filter([{
                property: "communeId",
                value: communeId
            }, {
                property: "actif",
                value: 'true'
            } ]);

        });
    },

    onSelectOldeb: function(sel, records, index, opt) {
        var tabObligation = this.getTabObligation();
        tabObligation.queryById('openOldeb').setDisabled(records.length == 0);
        tabObligation.queryById('locateOldeb').setDisabled(records.length == 0);
        tabObligation.queryById('printOldeb').setDisabled(records.length == 0);
    },

    onSelectCommuneOldeb: function(combo, records) {
        var cboSection = this.getTabObligation().getPlugin('headerfilters').fields['section'];
        var grid = this.getTabObligation();
        cboSection.clearValue();
        if (records.length > 0) {
            var communeId = records[0].getId();
            var section = cboSection.getValueModel();
            if (!section || section.getCommune().getId() != communeId) {
                // Changement de commune : on filtre les
                // sections sur la nouvelle commune
                cboSection.getStore().clearFilter(true);
                cboSection.getStore().filter([{
                    property: "communeId",
                    value: communeId
                } ]);
                grid.getStore().clearFilter(true);
                grid.getStore().filter([{
                    property: "communeId",
                    value: communeId
                }, {
                    property: "actif",
                    value: 'true'
                } ]);
            }
        }
    },
    getSelectedOldeb: function() {
        if (!this.getTabObligation()) {
            console.warn('soucis ??');
            return;
        }
        var record = this.getTabObligation().getSelectionModel().getSelection();
        if (record != null && Ext.isArray(record)) {
            record = record[0];
        }
        return record;
    },

    showFicheOldebFromGrid: function() {
        var oldeb = this.getSelectedOldeb();
        if (oldeb) {
            this.showFicheOldeb(oldeb.getId());
        }
    },
    printFiche: function() {
        // Message d'information au clic sur le bouton
        var oldeb = this.getSelectedOldeb();
        if (oldeb) {
            Ext.Msg
                    .confirm(
                            'Téléchargement de la fiche',
                            'Votre demande va être enregistrée. Lorsque le fichier sera prêt, vous serez averti par un message électronique. Souhaitez-vous continuer ?',
                            function(btn) {
                                if (btn == "yes") {
                                    this.downloadFiche(oldeb);
                                }
                            }, this);
        }
    },

    downloadFiche: function(oldeb) {
        var commune = oldeb.getCommune().getId(), section = oldeb.data.section, parcelle = oldeb.data.parcelle;
        if (commune != null && section != null && parcelle != null) {
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/oldeb"),
                method: 'GET',
                params: {
                    section: section,
                    commune: commune,
                    parcelle: parcelle
                },
                scope: this,
                callback: function(options, success, response) {
                    if (success == true) {
                        Sdis.Remocra.util.Msg.msg('Téléchargement de la fiche', 'Votre demande a été prise en compte.', 5);

                        this.getTabObligation().queryById('printOldeb').setDisabled(true);
                    } else {
                        var msg = o.result && o.result.message ? ' :<br/>' + o.result.message : '';
                        Ext.Msg.alert('Téléchargement de la fiche',
                                'Un problème est survenu lors de l\'enregistrement de la demande.' + msg + '.');
                    }
                }
            });
        }
    },

    showFicheOldeb: function(id) {
        var model = Sdis.Remocra.model.Oldeb;
        if (model != null) {
            model.load(id, {
                scope: this,
                success: function(record) {
                    a = record;// return;
                    this.getController('oldeb.Fiche').showFiche(record);
                }
            });
        }
    },
    onLocateOldebFromGrid: function() {
        this.onLocateParcelle(this.getSelectedOldeb());
    },

    addEventToControl: function(controlName, cfg) {
        var control = this.getTabMap().getSpecificControl(controlName);
        if (control) {
            Ext.applyIf(cfg, {
                scope: this
            });
            control.events.on(cfg);
        }
    }
});
