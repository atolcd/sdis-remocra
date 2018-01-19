Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.model.Voie');
Ext.require('Sdis.Remocra.store.Hydrant');
Ext.require('Sdis.Remocra.store.Tournee');
Ext.require('Sdis.Remocra.store.TypeHydrant');
Ext.require('Sdis.Remocra.store.Organisme');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');

Ext.require('Sdis.Remocra.model.HydrantPena');
Ext.require('Sdis.Remocra.model.HydrantPibi');

Ext.require('Sdis.Remocra.widget.SdisChoice');
Ext.require('Sdis.Remocra.features.hydrants.FichePena');
Ext.require('Sdis.Remocra.features.hydrants.FichePibi');
Ext.require('Sdis.Remocra.features.hydrants.Affectation');

Ext.define('Sdis.Remocra.controller.hydrant.Hydrant', {
    extend: 'Ext.app.Controller',

    stores: ['Tournee','Commune','Voie','Hydrant','TypeHydrant','Organisme'],

    refs: [{
        ref: 'tabPanel',
        selector: 'crHydrants'
    },{
        ref: 'tabMap',
        selector: 'crHydrantsMap'
    },{
        ref: 'tabAccess',
        selector: 'crHydrantsAccesRapide'
    },{
        ref: 'tabHydrant',
        selector: 'crHydrantsHydrant'
    },{
        ref: 'tabTournee',
        selector: 'crHydrantsTournee'
    },{
        ref: 'tabDocuments',
        selector: 'crBlocDocumentGrid'
    },{
        ref: 'choiceTypeHydrant',
        selector: 'sdischoice[name=choiceTypeHydrant]'
    },{
        ref: 'affectation',
        selector: 'affectation'
    }],

    init: function() {

        this.tplXY = new Ext.Template("hydrants/localisation/x/{valX}/y/{valY}");
        this.tplTournee = new Ext.Template("hydrants/tournees/num/{numTournee}");
        this.tplHydrantsTournee = new Ext.Template("hydrants/hydrants/tournee/{numTournee}");
        this.tplHydrant = new Ext.Template("hydrants/hydrants/num/{numero}");
        this.tplBounds = new Ext.Template("hydrants/localisation/bounds/{bounds}");

        this.lastTournee = null;

        this.control({
            // Page globale
            'crHydrants': {
                tabchange: this.onTabChange,
                urlChanged: this.onUrlChanged,
                beforerender: this.processRightHydrant,
                afterrender: this.initFromUrl
            },
            // Onglet "Accès rapide"
            'crHydrantsAccesRapide button': {
                click: this.clearError
            },
            'crHydrantsAccesRapide #locateTournee': {
                click: this.onLocateTourneeFromAcces
            },
            /*
             * 'crHydrantsAccesRapide #openTournee': { click: this.onOpenTournee },
             */
            'crHydrantsAccesRapide #locateHydrant': {
                click: this.onLocateHydrantFromAccess
            },
            /*
             * 'crHydrantsAccesRapide #openHydrant': { click: this.onOpenHydrant },
             */
            'crHydrantsAccesRapide #locateCommune': {
                click: this.onLocateCommune
            },
            'crHydrantsAccesRapide combo[name=commune]': {
                select: this.onSelectCommune
            },
            // Onlet "Tournées"
            'crHydrantsTournee': {
                selectionchange: this.onSelectTournee
            },
            'crHydrantsTournee #locateTournee': {
                click: this.onLocateTourneeFromGrid
            },
            'crHydrantsTournee #showHydrant': {
                click: this.showListHydrants
            },
            'crHydrantsTournee #deleteTournee': {
                click: this.deleteTournee
            },
            'crHydrantsTournee #cancelReservation': {
                click: this.cancelReservation
            },
            // Onglet "Localisation" (la carte)
            'crHydrantsMap': {
                layersadded: this.initControlMap
            },
            'crHydrantsMap #dessinerBtn': {
                toggle: function(button, pressed) {
                    this.getTabMap().activateSpecificControl('drawPoint', pressed);
                }
            },
            'crHydrantsMap #selectBtn': {
                toggle: function(button, pressed) {
                    this.getTabMap().activateSpecificControl('selectPolygone', pressed);
                }
            },
            'crHydrantsMap #editInfoBtn': {
                click: this.showFicheHydrantFromMap
            },
            'crHydrantsMap #editInfoBtnNoCtrl': {
                click: this.showFicheHydrantFromMap
            },
            'crHydrantsMap #deleteBtn': {
                click: this.deleteHydrantFromMap
            },
            'crHydrantsMap #affecterBtn': {
                click: this.affecteTournee
            },
            'crHydrantsMap #desaffecterBtn': {
                click: this.desaffecteTournee
            },
            'crHydrantsMap #activeMoveBtn': {
                toggle: function(button, pressed) {
                    this.getTabMap().activateSpecificControl('movePoint', pressed);
                }
            },
            'crHydrantsMap #validMoveBtn': {
                click: this.validMove
            },
            'crHydrantsMap #cancelMoveBtn': {
                click: this.cancelMove
            },
            'crHydrantsMap #downloadHydrantsNonNum': {
                click: this.downloadHydrantsNonNum
            },
            // Fenêtre "affectation"
            'affectation': {
                afterrender: this.afterShowAffectation
            },
            'affectation #ok': {
                click: this.validAffectation
            },
            'affectation radio': {
                change: this.onAffectationRadioChange
            },
            'affectation combo[name=tournee]': {
                render: this.onAffectationTourneeRender
            },
            // Onglet "Hydrants"
            'crHydrantsHydrant': {
                afterrender: this.verifDroit,
                selectionchange: this.onSelectHydrant
            },
            'crHydrantsHydrant #openHydrant': {
                click: this.showFicheHydrantFromGrid
            },
            'crHydrantsHydrant #openWithoutCtrl': {
                click: this.showFicheHydrantFromGrid
            },
            'crHydrantsHydrant #locateHydrant': {
                click: this.onLocateHydrantFromGrid
            },
            'crHydrantsHydrant #newHydrant': {
                click: this.onFeatureAdded
            },
            'crHydrantsHydrant #deleteHydrant': {
                click: this.deleteHydrantFromGrid
            },
            // Fenêtre "choix type hydrant"
            'sdischoice[name=choiceTypeHydrant]': {
                close: this.onCloseChoiceTypeHydrant
            },
            'sdischoice[name=choiceTypeHydrant] #ok': {
                click: this.onValidChoiceTypeHydrant
            }
        });
    },

    processRightHydrant: function(fiche) {
        if (!Sdis.Remocra.Rights.getRight('TOURNEE').Read) {
            fiche.down('crHydrantsTournee').tab.hide();
            fiche.down('#tourneeRapide').hide();
            fiche.down('crHydrantsHydrant gridcolumn[dataIndex=tourneeId]').hide();
        }
        if (!Sdis.Remocra.Rights.getRight('TOURNEE').Create) {
            fiche.down('crHydrantsTournee #deleteTournee').hide();
        }
        if (!Sdis.Remocra.Rights.getRight('HYDRANTS').Delete) {
            fiche.down('crHydrantsHydrant #deleteHydrant').hide();
        }
    },

    /***************************************************************************
     * Gestion URL / méthode globale
     **************************************************************************/

    initFromUrl: function() {
        var p2 = this.getTabPanel().p2, extra = this.getTabPanel().extraParams;
        this.onUrlChanged(p2, extra);
    },

    addEventToControl: function(controlName, cfg) {
        var control = this.getTabMap().getSpecificControl(controlName);
        if (control) {
            Ext.applyIf(cfg, {
                scope: this
            });
            control.events.on(cfg);
        }
    },

    initControlMap: function() {
        // TODO : factoriser ce principe
        this.addEventToControl('drawPoint', {
            'featureadded': this.onFeatureAdded
        });

        this.getTabMap().hydrantLayer.events.on({
            'featureselected': this.onSelectedFeatureChange,
            'featureunselected': this.onSelectedFeatureChange,
            scope: this
        });

        // Gestion du déplacement d'un hydrant
        this.addEventToControl('movePoint', {
            'activate': this.onActivateMovePoint,
            'deactivate': this.onDeactivateMovePoint,
            'dragcomplete' : this.onDragCompleteMovePoint,
            'dragstart': this.onDragStartMovePoint
        });
    },

    onDragStartMovePoint: function(vector){

        if(vector.feature.data['isTourneeRes'] && vector.feature.data['tournee'] != null){
            message = 'Le point d`\'eau sélectionné appartient à une tournée réservée.';
            Ext.Msg.alert('Déplacement point d\'eau',message);
            return;
        }

        Sdis.Remocra.model.ZoneCompetence.checkByXY(vector.feature.geometry.x, vector.feature.geometry.y, this.getTabMap().getCurrentSrid(), {
            scope: this,
            success: function(response, config) {
                //On ne fait rien
            },

            failure: function(response, config) {
                Sdis.Remocra.util.Msg.msg('Point d\'eau', 'Vous ne possédez pas les autorisations nécessaires pour déplacer ce point d\'eau', 3);
                this.getTabMap().getSpecificControl('movePoint').deactivate();
            }
        });

    },

    manageDisabledButtonsMoveHydrant: function(disabled){
        var validMoveBtn = this.getTabMap().down('#validMoveBtn');
        var cancelMoveBtn = this.getTabMap().down('#cancelMoveBtn');
        if(validMoveBtn != null){
            validMoveBtn.setDisabled(disabled);
        }
        if(cancelMoveBtn != null){
            cancelMoveBtn.setDisabled(disabled);
        }
    },

    manageVisibilityButtonsMoveHydrant: function(visible){
        var validMoveBtn = this.getTabMap().down('#validMoveBtn');
        var cancelMoveBtn = this.getTabMap().down('#cancelMoveBtn');
        if(validMoveBtn != null){
            validMoveBtn.setVisible(visible);
        }
        if(cancelMoveBtn != null){
            cancelMoveBtn.setVisible(visible);
        }
    },

    validMove: function(){
        this.onOKMovePoint();
    },

    cancelMove: function(){
        this.onCancelMovePoint();
        this.manageDisabledButtonsMoveHydrant(true);
    },

    onDragCompleteMovePoint: function(vector) {
        this.manageDisabledButtonsMoveHydrant(false);
    },

    onActivateMovePoint: function() {
        var feature = this.getSelectedFeatures()[0];
        if (!feature) {
            // Anormal
            this.onDeactivateMovePoint();
        }
        // On copie la feature sélectionnée dans la couche de travail
        var workingFeature = feature.clone();
        workingFeature.data['id'] = feature.fid;
        var workingLayer = this.getTabMap().workingLayer;
        workingLayer.removeAllFeatures();
        workingLayer.addFeatures(workingFeature);
        var movePoint = this.getTabMap().getSpecificControl('movePoint');
        movePoint.selectFeature(workingFeature);
        movePoint.geometrySaved = workingFeature.geometry.clone();
        this.manageVisibilityButtonsMoveHydrant(true);
    },

    onDeactivateMovePoint: function() {
        this.onCancelMovePoint();
        var movePoint = this.getTabMap().getSpecificControl('movePoint');
        if (movePoint.geometrySaved) {
            movePoint.geometrySaved = null;
        }
        this.getTabMap().workingLayer.removeAllFeatures();
        this.manageVisibilityButtonsMoveHydrant(false);
    },

    onCancelMovePoint: function() {
        var feature = this.getTabMap().workingLayer.features[0];
        var movePoint = this.getTabMap().getSpecificControl('movePoint');
        if (movePoint.geometrySaved) {
            feature.move(new OpenLayers.LonLat(movePoint.geometrySaved.x,movePoint.geometrySaved.y));
        }
    },

    onOKMovePoint: function() {
        // Récupération de la feature de travail
        var feature = this.getTabMap().workingLayer.features[0];
        var mapProjection = this.getTabMap().map.getProjection();
        var wktFormat = new OpenLayers.Format.WKT({
            internalProjection: mapProjection,
            externalProjection: new OpenLayers.Projection('EPSG:2154')
        });
        var transfGeom = wktFormat.extractGeometry(feature.geometry);
        Ext.Ajax.request({
            scope: this,
            method :'POST',
            url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/'+feature.data['id']+'/deplacer'),
            params: {
                geometrie: transfGeom,
                srid: 2154
            },
            callback: function(param, success, response) {
                var res = Ext.decode(response.responseText);
                if (success) {
                    var movePoint = this.getTabMap().getSpecificControl('movePoint');
                    movePoint.geometrySaved = null;
                    Sdis.Remocra.util.Msg.msg('Déplacer point d\'eau', res.message);
                } else {
                    Ext.MessageBox.show({
                        title: 'Déplacer point d\'eau',
                        msg: res.message,
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                     });
                }
                // On désactive l'outil, on désactive le bouton et on rafraichit la couche de hydrants
                this.onDeactivateMovePoint();
                var activeMoveBtn = this.getTabMap().queryById('activeMoveBtn');
                activeMoveBtn.toggle(false);
                activeMoveBtn.setDisabled(true);
                this.refreshMap();
            }
        });
    },

    onSelectedFeatureChange: function(event) {
        var hydrantLayer = event.object, nbSelect = hydrantLayer.selectedFeatures.length;
        var btnFiche = this.getTabMap().queryById('editInfoBtn');
        var btnFicheNoCtrl = this.getTabMap().queryById('editInfoBtnNoCtrl');
        var btnDelete = this.getTabMap().queryById('deleteBtn');
        var btnAffecter = this.getTabMap().queryById('affecterBtn');
        var btnDesaffecter = this.getTabMap().queryById('desaffecterBtn');
        var btnActiveDeplacer = this.getTabMap().queryById('activeMoveBtn');
        if(btnFiche != null){  //Dans le cas ou on n'a pas les droits le bouton n'existe pas
           btnFiche.setDisabled(nbSelect != 1);
        }
        btnFicheNoCtrl.setDisabled(nbSelect != 1);
        if (btnDelete != null) {
            btnDelete.setDisabled(nbSelect != 1);
        }
        if (btnAffecter != null) {
            btnAffecter.setDisabled(nbSelect == 0);
        }
        if (btnDesaffecter != null) {
            btnDesaffecter.setDisabled(nbSelect == 0);
        }
        if(btnActiveDeplacer != null){
            btnActiveDeplacer.setDisabled(nbSelect != 1);
        }
    },

    onTabChange: function(tabPanel, newCard, oldCard) {
        var currentHash = Sdis.Remocra.util.Util.getHashTokenNoSharp();
        if (!Ext.isEmpty(currentHash)) {
            currentHash = currentHash.split('/');
            if (currentHash.length >= 2 && currentHash[1] == newCard.itemId) {
                // Changement "programmé" d'onglet -> on ne change pas l'url
                return;
            }
        }
        // Changement "manuel" d'onglet -> on change l'url
        Sdis.Remocra.util.Util.changeHash('hydrants/' + newCard.itemId);
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

        // en fonction de l'onglet, on va faire telle ou telle chose en fonction
        // de l'url
        switch (itemId) {
        case 'localisation':
            this.updateMap(extraParams);
            break;
        case 'hydrants':
            this.updateHydrant(extraParams);
            break;
        case 'tournees':
            this.updateTournee(extraParams);
            break;
        default:
            break;
        }
    },

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

    updateHydrant: function(extraParams) {
        if (extraParams) {
            if (extraParams.tournee) {
                this.getTabHydrant().headerFilterPlugin.fields.nature.setValue(null);
                this.getTabHydrant().headerFilterPlugin.fields.dateContr.setValue(0);
                this.getTabHydrant().headerFilterPlugin.fields.dateReco.setValue(0);
                this.getTabHydrant().headerFilterPlugin.fields.numero.setValue(null);
                var cbo = this.getTabHydrant().headerFilterPlugin.fields.tournee;
                cbo.getStore().load({
                    scope: this,
                    params: {
                        filter: Ext.encode([{
                            property: 'id',
                            value: extraParams.tournee
                        }])
                    },
                    callback: function(records, operation, success) {
                        cbo.select(records[0]);
                        this.lastTournee = records[0];
                        this.getTabHydrant().headerFilterPlugin.applyFilters();
                        this.getTabHydrant().getStore().load();
                    }
                });
            } else {
                if (extraParams.num) {
                    this.getTabHydrant().headerFilterPlugin.fields.numero.setValue(decodeURIComponent(extraParams.num));
                    this.getTabHydrant().headerFilterPlugin.applyFilters();
                }
                this.getTabHydrant().getStore().load();
            }
        } else {
            this.getTabHydrant().getStore().load();
        }
    },

    updateTournee: function(extraParams) {
        if (extraParams && extraParams.num) {
            this.getTabTournee().headerFilterPlugin.fields.id.setValue(extraParams.num);
            this.getTabTournee().headerFilterPlugin.applyFilters();
        }
        this.getTabTournee().getStore().load();
    },

    deleteHydrant: function(id, typeHydrant, config) {
        var model = this.getModelFromTypeHydrantCode(typeHydrant);
        if (model != null) {
            Ext.Msg.confirm('Suppression point d\'eau', 'Confirmez-vous la suppression du point d\'eau ?', function(buttonId) {
                if (buttonId == 'yes') {
                    var hydrant = Ext.create(model, {
                        id: id
                    });
                    hydrant.destroy(config);
                }
            }, this);
        }
    },

    zoomToTournee: function(tournee) {
        if (Ext.isEmpty(tournee.get('geometrie'))) {
            Ext.Msg.show({
                title: 'Localisation d\'une tournée',
                msg: 'La tournée ne possède aucun point d\'eau, il est donc impossible de la localiser',
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.INFO
            });
        } else {
            var bounds = Sdis.Remocra.util.Util.getBounds(tournee.get('geometrie'));
            Sdis.Remocra.util.Util.changeHash(this.tplBounds.apply({
                bounds: bounds.toBBOX()
            }));
        }
    },

    refreshMap: function() {
        if (this.getTabMap() != null && this.getTabMap().rendered) {
            this.getTabMap().hydrantLayer.refresh({
                force: true
            });
        }
    },

    onLocateHydrant: function(hydrant) {
        if (hydrant) {
            if (hydrant.get('jsonGeometrie') != null) {
                var geom = Ext.decode(hydrant.get('jsonGeometrie'));
                Sdis.Remocra.util.Util.changeHash(this.tplXY.apply({
                    valX: geom.coordinates[0],
                    valY: geom.coordinates[1]
                }));
            }
        }
    },

    /***************************************************************************
     * Onglet "Accès Rapide"
     **************************************************************************/

    clearAccess: function() {
        this.clearError();
        this.getTabAccess().getForm().reset();
    },

    clearError: function() {
        this.getTabAccess().getForm().isValid();
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

    onLocateTourneeFromAcces: function() {
        var cboTournee = this.getTabAccess().down('combo[name=numTournee]');
        var tourneeId = cboTournee.getValue();
        if (tourneeId != null) {
            this.lastTournee = cboTournee.findRecordByValue(tourneeId);
            this.clearAccess();
            this.zoomToTournee(this.lastTournee);
        } else {
            this.showError('numTournee', 'Une tournée doit être sélectionnée');
        }
    },

    onLocateHydrantFromAccess: function() {
        var allValues = this.getTabAccess().getValues();
        if (!Ext.isEmpty(allValues.numHydrant)) {
            var hydrant = this.getTabAccess().down('combo[name=numHydrant]').findRecordByValue(allValues.numHydrant);
            this.clearAccess();
            this.onLocateHydrant(hydrant);
        } else {
            this.showError('numHydrant', 'Un point d\'eau doit être sélectionné');
        }
    },

    onLocateCommune: function() {
        var allValues = this.getTabAccess().getValues(), bounds, rec;
        if (!Ext.isEmpty(allValues.voie)) {
            rec = this.getTabAccess().getForm().findField('voie').findRecordByValue(allValues.voie);
            if (rec != null) {
                bounds = Sdis.Remocra.util.Util.getBounds(rec.get('geometrie'));
                this.clearAccess();
                Sdis.Remocra.util.Util.changeHash(this.tplBounds.apply({
                    bounds: bounds.toBBOX()
                }));
                return;
            }
        }
        if (!Ext.isEmpty(allValues.commune)) {
            rec = this.getTabAccess().getForm().findField('commune').findRecordByValue(allValues.commune);
            if (rec != null) {
                bounds = Sdis.Remocra.util.Util.getBounds(rec.get('geometrie'));
                this.clearAccess();
                Sdis.Remocra.util.Util.changeHash(this.tplBounds.apply({
                    bounds: bounds.toBBOX()
                }));
            }
        } else {
            this.showError('commune', 'Une commune doit être sélectionnée');
        }
    },

    onSelectCommune: function(combo, records) {
        var cboVoie = this.getTabAccess().getForm().findField('voie');
        if (records.length > 0) {
            var communeId = records[0].getId();
            var voie = cboVoie.getValueModel();
            if (!voie || voie.getCommune().getId() != communeId) {
                // Changement de commune : on filtre les voies sur la nouvelle commune
                cboVoie.getStore().clearFilter(true);
                cboVoie.getStore().filter([{property: "communeId", value: communeId}]);
            }
            cboVoie.enable();
        } else {
            cboVoie.disable();
        }
    },

    /***************************************************************************
     * Onglet "Tournée"
     **************************************************************************/

    onSelectTournee: function(sel, records, index, opt) {
        var tabTournee = this.getTabTournee();
        tabTournee.queryById('locateTournee').setDisabled(records.length == 0);
        tabTournee.queryById('showHydrant').setDisabled(records.length == 0);
        tabTournee.queryById('deleteTournee').setDisabled(records.length == 0);
        tabTournee.queryById('cancelReservation').setDisabled(records.length == 0 || records[0].get('reservation') == null);
        if (tabTournee.length > 0) {
            this.lastTournee = tabTournee[0];
        }
    },

    getSelectedTournee: function() {
        var record = this.getTabTournee().getSelectionModel().getSelection();
        if (record != null && Ext.isArray(record)) {
            record = record[0];
        }
        return record;
    },

    showListHydrants: function() {
        var tournee = this.getSelectedTournee();
        if (tournee != null) {
            this.lastTournee = tournee;
            Sdis.Remocra.util.Util.changeHash(this.tplHydrantsTournee.apply({
                numTournee: tournee.getId()
            }));
        }
    },

    onLocateTourneeFromGrid: function() {
        var tournee = this.getSelectedTournee();
        if (tournee != null) {
            this.lastTournee = tournee;
            this.zoomToTournee(tournee);
        }
    },

    deleteTournee: function() {
        var tournee = this.getSelectedTournee();
        if (tournee != null) {
            Ext.Msg.confirm('Annulation de la tournée', 'Vous allez annuler la tournée ' + tournee.getId(), function(buttonId) {
                if (buttonId == 'yes') {
                    tournee.destroy({
                        scope: this,
                        success: function(record, operation) {
                            this.lastTournee = null;
                            this.getTabTournee().getStore().remove(record);
                            Sdis.Remocra.util.Msg.msg('Annulation', 'La tournée ' + record.getId() + ' a bien été annulée.');
                        }
                    });
                }
            }, this);
        }
    },

    cancelReservation: function() {
        var tournee = this.getSelectedTournee();
        Ext.Msg.confirm('Annulation de la réservation', 'Vous allez annuler la réservation de la tournée ' + tournee.getId()+'. Voulez-vous continuer ?', function(buttonId) {
            if (buttonId == 'yes') {
                Ext.Ajax.request({
                    url: Sdis.Remocra.util.Util.withBaseUrl('../tournees/cancelreservation/'+tournee.getId()),
                    method: 'PUT',
                    scope: this,
                    callback: function(param, success, response) {
                        var res = Ext.decode(response.responseText);
                        this.getTabTournee().getStore().load();
                        Sdis.Remocra.util.Msg.msg("Annulation de la réservation", res.message);
                    }
                });
            }
        },this);
    },

    /***************************************************************************
     * Onglet "Localisation" (la Carte)
     **************************************************************************/

    onFeatureAdded: function(event) {
        var widgetConfig = {
            feature: event.feature,
            name: 'choiceTypeHydrant',
            title: 'Choix du type d\'hydrant',
            cboConfig: {
                store: 'TypeHydrant',
                fieldLabel: 'Type d\'hydrant',
                value: 'PIBI',
                valueField: 'code'
            }
        };

        Sdis.Remocra.model.ZoneCompetence.checkByXY(event.feature.geometry.x, event.feature.geometry.y, this.getTabMap().getCurrentSrid(), {
            scope: this,
            success: function(response, config) {
                this.getTabMap().down('#dessinerBtn').toggle(false);
                Ext.widget('sdischoice', widgetConfig).show();
            },
            failure: function(response, config) {
                Sdis.Remocra.util.Msg.msg('Hydrant', 'Vous ne possédez pas les autorisations nécessaires pour créer un hydrant sur cette zone', 3);
            }
        });
    },

    onCloseChoiceTypeHydrant: function(win) {
        if (win.ignoreDestroyFeature !== true && win.feature) {
            win.feature.destroy();
        }
    },

    getModelFromTypeHydrantCode: function(typeHydrantCode) {
        var model = null;
        switch (typeHydrantCode) {
        case 'PENA':
            model = Sdis.Remocra.model.HydrantPena;
            break;
        case 'PIBI':
            model = Sdis.Remocra.model.HydrantPibi;
            break;
        }
        return model;
    },

    showFicheHydrant: function(typeHydrantCode, id, controle) {
        var model = this.getModelFromTypeHydrantCode(typeHydrantCode);
        if (model != null) {
            model.load(id, {
                scope: this,
                success: function(record) {
                    this.getController('hydrant.Fiche').showFiche(record, controle);
                }
            });
        }
    },

    onValidChoiceTypeHydrant: function() {
        var win = this.getChoiceTypeHydrant(), combo = win.down('combo'), feature = win.feature;
        if (combo.isValid()) {
            var hydrant = null, typeHydrant = combo.getValueModel();
            var model = this.getModelFromTypeHydrantCode(typeHydrant.get('code'));
            if (model != null) {
                hydrant = Ext.create(model, {
                    code: typeHydrant.get('code')
                });
            } else {
                alert('Type hydrant inconnu : ' + typeHydrant.get('code'));
                win.close();
                return;
            }

            if (feature != null) {
                // TODO : externaliser ce process
                var mapProjection = this.getTabMap().map.getProjection();
                var wktFormat = new OpenLayers.Format.WKT({
                    internalProjection: mapProjection,
                    externalProjection: new OpenLayers.Projection('EPSG:2154')
                });
                var str = wktFormat.write(feature);
                hydrant.set('geometrie', str);
                hydrant.feature = feature;
                hydrant.srid = this.getTabMap().getCurrentSrid();
            }
            if (win.commune) {
                hydrant.set('commune', win.commune);
                hydrant.raw.commune = win.commune.raw;
            }
            win.ignoreDestroyFeature = true;
            win.close();
            this.getController('hydrant.Fiche').showFiche(hydrant, true);

        }
    },

    getSelectedFeatures: function() {
        var features = this.getTabMap().hydrantLayer.selectedFeatures;
        return Ext.Array.from(features);
    },

    showFicheHydrantFromMap: function(button) {
        var controle = button.itemId === 'editInfoBtnNoCtrl' ? false : true ;
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            this.showFicheHydrant(features[0].data.typeHydrantCode, features[0].fid, controle);
        }

    },

    deleteHydrantFromMap: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            this.deleteHydrant(features[0].fid, features[0].data.typeHydrantCode, {
                scope: this,
                success: function() {
                    this.refreshMap();
                },
                failure: function() {
                    console.warn("erreur lors de la suppression de l'hydrant");
                }
            });
        }
    },

    affecteTournee: function() {
        var features = this.getSelectedFeatures();
        var messagelocked = '', feature, i, countLocked = 0;

        for (i=0 ; i < features.length ; i++) {
            feature = features[i];
            if (feature.data['isTourneeRes'] && feature.data['tournee'] != null) {
                // On n'affiche que les 3 premiers, puis "..."
                if (++countLocked > 3) {
                    if (countLocked < 5) {
                        messagelocked += '<li style="list-style:disc inside;margin-left:20px;">...</li>';
                    }
                } else {
                    messagelocked += '<li style="list-style:disc inside;margin-left:20px;">' + feature.data['numero']
                        + (feature.data['tournee'] ? ', tournée ' + feature.data['tournee'] : '') + '</li>';
                }
            }
        }

        if (messagelocked != '') {
            Ext.Msg.alert('Affectation tournée',
                'L\'affectation ne peut pas être réalisée car des points d\'eau ('
                    + countLocked + ') appartiennent à une tournée reservée :<ul>'
                    + messagelocked + '</ul>');
            return;
        }

        if (features.length > 0) {
            Ext.widget('affectation').show();
        }
    },

    desaffecteTournee: function() {
        var features = this.getSelectedFeatures();
        var messagelocked = '', message = '', ids = [], i, countLocked = 0;
        if (features.length > 0) {
            for (i=0 ; i < features.length ; i++) {
                var feature = features[i];
                if (feature.data['isTourneeRes']) {
                    // On n'affiche que les 3 premiers, puis "..."
                    if (++countLocked > 3) {
                        if (countLocked < 5) {
                            messagelocked += '<li style="list-style:disc inside;margin-left:20px;">...</li>';
                        }
                    } else {
                        messagelocked += '<li style="list-style:disc inside;margin-left:20px;">' + feature.data['numero']
                            + (feature.data['tournee'] ? ', tournée ' + feature.data['tournee'] : '') + '</li>';
                    }
                } else {
                    ids.push(feature['fid']);
                    // On n'affiche que les 3 premiers, puis "..."
                    if (ids.length > 3) {
                        if (ids.length < 5) {
                            message += '<li style="list-style:disc inside;margin-left:20px;">...</li>';
                        }
                    } else {
                        message += '<li style="list-style:disc inside;margin-left:20px;">' + feature.data['numero']
                            + (feature.data['tournee'] ? ', tournée ' + feature.data['tournee'] : '') + '</li>';
                    }
                }
            }
            var globalMessage = '';
            if (countLocked > 0) {
                globalMessage += 'Les points d\'eau suivants ('
                    + countLocked + ') ne peuvent pas être désaffectés car ils appartiennent à une tournée reservée :<ul>'
                    + messagelocked + '</ul>';
            }
            if (countLocked > 0 && ids.length > 0) {
                globalMessage += '<br/>';
            }
            if (ids.length > 0) {
                globalMessage += 'Les points d\'eau suivants (' + ids.length + ') vont être désaffectés :<ul>'
                    + message + '</ul>' + '<br/>Souhaitez-vous continuer ?';
                // Demande de confirmation
                Ext.Msg.confirm("Retirer de la tournée",
                        globalMessage, Ext.bind(function(buttonId) {
                    if (buttonId == 'yes') {
                        this.desaffecteTourneeConcrete(ids);
                    }
                }, this));
                
            } else {
                // Message d'information
                Ext.Msg.alert('Retirer de la tournée', globalMessage);
            }
        }
    },

    desaffecteTourneeConcrete: function(ids) {
        if (ids && ids.length > 0) {
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/desaffecter'),
                jsonData: ids,
                callback: function(param, success, response) {
                    var res = Ext.decode(response.responseText);
                    Sdis.Remocra.util.Msg.msg("Désaffectation", res.message);
                }
            });
        }
    },

    downloadHydrantsNonNum: function(){
        Ext.Msg.confirm('Liste des points d\'eau non numérotés',
                'Votre demande va être enregistrée. Lorsque le fichier sera prêt, vous serez averti par un message électronique. Souhaitez-vous continuer ?',
                function(btn) {
                    if (btn == "yes"){
                        this.goDownloadHydrantsNonNum();
                    }
                }, this);
    },
    goDownloadHydrantsNonNum: function() {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/hydrantsnonnum"),
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Liste des points d\'eau non numérotés',
                        'Votre demande a été prise en compte.', 5);
                    var downloadHydrantsNonNum =  this.getTabMap().down('#downloadHydrantsNonNum');
                    downloadHydrantsNonNum.setDisabled(true);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Liste des points d\'eau non numérotés',
                        'Un problème est survenu lors de l\'enregistrement de la demande.' + msg + '.');
                }
            }
        });
    },

    /***************************************************************************
     * Fenêtre "Affectation"
     **************************************************************************/
    afterShowAffectation: function() {
        if (this.lastTournee != null) {
            this.getAffectation().down('radio[inputValue=2]').enable();
            this.getAffectation().down('component[name=lastTournee]').update(this.lastTournee.getId());
        }
    },

    onAffectationRadioChange: function(radioButton, newValue, oldValue) {
        if (radioButton.inputValue == 3) {
            this.getAffectation().down('combo[name=tournee]').setDisabled(oldValue);
        }
    },
    
    onAffectationTourneeRender: function(combo, eOpts) {
        // On masque les éléments non désirés de la toolbar :
        // first et tout ce qui est après next : last, refresh et séparateurs
        var hideUntilEnd = false;
        combo.getPicker().pagingToolbar.items.each(function(item) {
            if (hideUntilEnd || item.itemId == 'first') {
                item.hide();
            } else if (item.itemId == 'next'){
                hideUntilEnd = true;
            }
        });
    },

    validAffectation: function() {
        var tourneeId = null, /*
                                 * cboUtilisateur =
                                 * this.getAffectation().down('combo[name=utilisateur]'),
                                 */cboTournee = this.getAffectation().down('combo[name=tournee]');

        if (/* !cboUtilisateur.isValid() || */!cboTournee.isValid()) {
            return;
        }

        switch (this.getAffectation().down('radio[checked]').inputValue) {
        case "1": // Nouvelle tournée
            break;
        case "2": // Dernière tournée
            tourneeId = this.lastTournee.getId();
            break;
        case "3": // Tournée existante
            tourneeId = cboTournee.getValue();
            break;
        }

        var ids = Ext.Array.pluck(this.getSelectedFeatures(), "fid");
        // var idUtilisateur = cboUtilisateur.getValue();

        Ext.Ajax.request({
            scope: this,
            url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/affecter'),
            jsonData: {
                ids: ids,
                tournee: tourneeId
            /*
             * , utilisateur: idUtilisateur
             */
            },
            callback: function(param, success, response) {
                var res = Ext.decode(response.responseText);
                Sdis.Remocra.util.Msg.msg("Affectation", res.message);
                this.getAffectation().close();
            }
        });
    },

    /***************************************************************************
     * Onglet "Hydrant"
     **************************************************************************/

    getSelectedHydrant: function() {
        if (!this.getTabHydrant()) {
            console.warn('soucis ??');
            return;
        }
        var record = this.getTabHydrant().getSelectionModel().getSelection();
        if (record != null && Ext.isArray(record)) {
            record = record[0];
        }
        return record;
    },

    onSelectHydrant: function(sel, records, index, opt) {
        var tabHydrant = this.getTabHydrant();
        tabHydrant.queryById('openHydrant').setDisabled(records.length == 0);
        tabHydrant.queryById('openWithoutCtrl').setDisabled(records.length == 0);
        tabHydrant.queryById('locateHydrant').setDisabled(records.length == 0);
        tabHydrant.queryById('deleteHydrant').setDisabled(records.length == 0);
    },

    verifDroit : function() {
        var tabHydrant = this.getTabHydrant();
        tabHydrant.queryById('openHydrant').setVisible(Sdis.Remocra.Rights.getRight('HYDRANTS').Create||
        Sdis.Remocra.Rights.getRight('HYDRANTS_RECONNAISSANCE').Create || Sdis.Remocra.Rights.getRight('HYDRANTS_CONTROLE').Create);
    },

    showFicheHydrantFromGrid: function(button) {

        var controle = button.itemId === 'openWithoutCtrl' ? false : true ;
        var hydrant = this.getSelectedHydrant();
        if (hydrant) {
            this.showFicheHydrant(hydrant.get('code'), hydrant.getId(), controle);
        }
    },

    onLocateHydrantFromGrid: function() {
        this.onLocateHydrant(this.getSelectedHydrant());
    },

    deleteHydrantFromGrid: function() {
        var hydrant = this.getSelectedHydrant();
        this.deleteHydrant(hydrant.getId(), hydrant.get('code'), {
            scope: this,
            success: function(record, operation) {
                this.getTabHydrant().getStore().load();
                Sdis.Remocra.util.Msg.msg('Suppression', 'L\'hydrant ' + record.get('numero') + ' a bien été supprimé.');
                this.refreshMap();
            },
            failure: function() {

            }
        });
    }

});
