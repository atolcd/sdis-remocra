Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.model.Voie');
Ext.require('Sdis.Remocra.model.HydrantIndispoTemporaire');
Ext.require('Sdis.Remocra.store.Hydrant');
Ext.require('Sdis.Remocra.store.Tournee');
Ext.require('Sdis.Remocra.store.TypeHydrant');
Ext.require('Sdis.Remocra.store.Organisme');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');
Ext.require('Sdis.Remocra.model.Hydrant');
Ext.require('Sdis.Remocra.model.HydrantPena');
Ext.require('Sdis.Remocra.model.HydrantPibi');
Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.model.Voie');
Ext.require('Sdis.Remocra.store.TypeHydrant');
Ext.require('Sdis.Remocra.store.TypeHydrantAnomalie');
Ext.require('Sdis.Remocra.store.TypeHydrantDiametre');
Ext.require('Sdis.Remocra.store.TypeHydrantDomaine');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureTous');
Ext.require('Sdis.Remocra.store.TypeHydrantVolConstate');
Ext.require('Sdis.Remocra.store.TypeHydrantMarque');
Ext.require('Sdis.Remocra.store.TypeHydrantPositionnement');
Ext.require('Sdis.Remocra.store.TypeHydrantMateriau');
Ext.require('Sdis.Remocra.store.Utilisateur');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureDeci');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureDeciTous');
Ext.require('Sdis.Remocra.model.HydrantPena');
Ext.require('Sdis.Remocra.model.HydrantPibi');

Ext.require('Sdis.Remocra.widget.SdisChoice');
Ext.require('Sdis.Remocra.features.hydrants.Affectation');
Ext.require('Sdis.Remocra.features.hydrants.NouvelleIndispo');
Ext.require('Sdis.Remocra.features.hydrants.EditIndispo');
Ext.require('Sdis.Remocra.features.hydrants.ActiveIndispo');
Ext.require('Sdis.Remocra.features.hydrants.LeveIndispo');
Ext.require('Sdis.Remocra.features.hydrants.ProlongerIndispo');
Ext.require('Sdis.Remocra.features.hydrants.ListerPeiIndispo');



Ext.define('Sdis.Remocra.controller.hydrant.Hydrant', {
    extend: 'Ext.app.Controller',

    stores: ['Tournee','Commune','Voie','Hydrant','TypeHydrant','Organisme','Commune', 'Voie', 'Hydrant', 'TypeHydrant',
     'TypeHydrantAnomalie', 'TypeHydrantDiametre', 'TypeHydrantDomaine','TypeHydrantNature', 'TypeHydrantNatureTous',
      'TypeHydrantVolConstate','TypeHydrantMarque', 'Utilisateur', 'TypeHydrantPositionnement', 'TypeHydrantMateriau','TypeHydrantNatureDeci',
       'TypeHydrantNatureDeciTous'],

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
        ref: 'tabIndispo',
        selector: 'crHydrantsIndispo'
    },{
        ref: 'tabDocuments',
        selector: 'crBlocDocumentGrid'
    },{
        ref: 'choiceTypeHydrant',
        selector: 'sdischoice[name=choiceTypeHydrant]'
    },{
        ref: 'affectation',
        selector: 'affectation'
    },{
        ref: 'nouvelleIndispo',
        selector: 'nouvelleIndispo'
    },{
        ref: 'editIndispo',
        selector: 'editIndispo'
    },{
        ref: 'activeIndispo',
        selector: 'activeIndispo'
    },{
        ref: 'leveIndispo',
        selector: 'leveIndispo'
    },{
        ref: 'prolongerIndispo',
        selector: 'prolongerIndispo'
    },{
        ref: 'listerPeiIndispo',
        selector: 'listerPeiIndispo'
    }],

    init: function() {

        this.tplXY = new Ext.Template("hydrants/localisation/x/{valX}/y/{valY}/h/{idPEI}");
        this.tplTournee = new Ext.Template("hydrants/tournees/num/{numTournee}");
        this.tplHydrantsTournee = new Ext.Template("hydrants/hydrants/tournee/{numTournee}");
        this.tplHydrant = new Ext.Template("hydrants/hydrants/num/{numero}");
        this.tplBounds = new Ext.Template("hydrants/localisation/bounds/{bounds}");
        this.tplBoundsTournee = new Ext.Template("hydrants/localisation/bounds/{bounds}/t/{idTournee}");
        this.tplBoundsIndispo = new Ext.Template("hydrants/localisation/bounds/{bounds}/i/{idIndispo}");

        this.lastTournee = null;

        this.doHighlightOnce = true;

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
            'crHydrantsTournee #resetTournee': {
                click: this.resetTournee
            },
            'crHydrantsTournee #renameTournee': {
                click: this.renameTournee
            },
            'crHydrantsTournee #finaliseTournee': {
                click: this.finaliseTournee
            },
            'crHydrantsTournee #deleteTournee': {
                click: this.deleteTournee
            },
            'crHydrantsTournee #cancelReservation': {
                click: this.cancelReservation
            },
            // Onglet "Localisation" (la carte)
            'crHydrantsMap': {
                layersadded: this.initControlMap,
                newVisiteRapide: this.createVisiteRapide,
                debitSimultaneClick: this.onClickDebitSimultaneFromMap
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
            'crHydrantsMap #editInfoVisiteRapideBtn': {
                toggle: function(button, pressed) {
                    if (pressed) {
                        // Désélection de toutes les features sélectionnées
                        this.getTabMap().map.getControlsByClass('OpenLayers.Control.SelectFeature')[0].unselectAll();
                    }
                    // (Dés)activation de l'outil de sélection par clic
                    this.getTabMap().activateSpecificControl('visiteRapide', pressed);
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
            'crHydrantsMap #indispoBtn': {
                click: this.showFicheIndispoTempFromMap
            },
             'crHydrantsMap #editIndispoBtn': {
              click: this.editIndispo
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

            'crHydrantsMap #debitSimultaneBtn' : {
                click: this.createDebitSimultane
            },

            'crHydrantsMap #saisirMesureBtn' : {
                toggle: function(button, pressed) {
                    if (pressed) {
                        // Désélection de toutes les features sélectionnées
                        this.getTabMap().map.getControlsByClass('OpenLayers.Control.SelectFeature')[0].unselectAll();
                    }
                    this.getTabMap().activateSpecificControl('saisirMesure', pressed);
                }
            },

            'crHydrantsMap #deleteDebitSimultaneBtn' : {
                toggle: function(button, pressed) {
                    if (pressed) {
                        // Désélection de toutes les features sélectionnées
                        this.getTabMap().map.getControlsByClass('OpenLayers.Control.SelectFeature')[0].unselectAll();
                    }
                    this.getTabMap().activateSpecificControl('deleteDebitSimultane', pressed);
                }
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
            },
            // Fenêtre "nouvelleIndispo"
            'nouvelleIndispo': {
                afterrender: this.afterShowNouvelleIndispo
            },
            'nouvelleIndispo #addHydrantIndispo': {
                click: this.addHydrantIndispo
            },
            'nouvelleIndispo #deleteHydrantIndispo': {
                click: this.deleteHydrantIndispo
            },
            'nouvelleIndispo #validHydrantIndispo': {
                click: this.validHydrantIndispo
            },
            'nouvelleIndispo checkbox[name=activationImmediate]': {
                change: this.onChangeImmediate
            },
            'editIndispo #gridIndispos': {
                afterrender: this.onEditIndispo,
                select: this.onSelectIndispoFromMap
            },
            'crHydrantsIndispo': {
                selectionchange: this.onSelectIndispo
            },
            'crHydrantsIndispo #activeIndispo': {
                click: this.showActiveIndispo
            },
            'activeIndispo': {
                afterrender: this.loadHydrantsIndispo
            },
            'leveIndispo': {
                afterrender: this.loadHydrantsIndispo
            },
            'prolongerIndispo': {
                afterrender: this.loadHydrantsIndispo
            },
            'listerPeiIndispo': {
                afterrender: this.loadHydrantsIndispo
            },
            'activeIndispo #activIndispo': {
                click: this.onActiveIndispo
            },
            'crHydrantsIndispo #leverIndispo': {
                click: this.showLeveIndispo
            },
            'crHydrantsIndispo #deleteIndispo': {
                click: this.deleteIndispo
            },

            'crHydrantsIndispo #gererIndispo': {
                click: this.showFicheIndispoTempFromGrid
            },
            'crHydrantsIndispo #locateIndispo': {
                click: this.onLocateIndispo
            },
            'leveIndispo #levIndispo': {
                click: this.onLeveIndispo
            },
             'editIndispo #levIndispo': {
                click: this.showLeveIndispo
            },
             'editIndispo #activIndispo': {
                click: this.showActiveIndispo
            },
             'prolongerIndispo #prolongeIndispo': {
                click: this.onProlongerIndispo
            },
             'crHydrantsIndispo #prolongerIndispo': {
                click: this.showProlongerIndispo
            },
             'crHydrantsIndispo #listerPeiIndispo': {
                click: this.showListerPeiIndispo
            }
        });
    },

    processRightHydrant: function(fiche) {
        if (!Sdis.Remocra.Rights.hasRight('TOURNEE_R')) {
            fiche.down('crHydrantsTournee').tab.hide();
            fiche.down('#tourneeRapide').hide();
            var nomTourneeCol = fiche.down('crHydrantsHydrant gridcolumn[dataIndex=nomTournee]');
            if (nomTourneeCol) {
                nomTourneeCol.hide();
            }
        }
        if (!Sdis.Remocra.Rights.hasRight('TOURNEE_C')) {
            fiche.down('crHydrantsTournee #deleteTournee').hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('TOURNEE_POURCENTAGE_C')) {
             fiche.down('crHydrantsTournee #finaliseTournee').hide();
             fiche.down('crHydrantsTournee #resetTournee').hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('TOURNEE_RESERVATION_D')) {
            fiche.down('crHydrantsTournee #cancelReservation').hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('HYDRANTS_D')) {
            fiche.down('crHydrantsHydrant #deleteHydrant').hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('INDISPOS_R')) {
            fiche.down('crHydrantsIndispo').tab.hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('INDISPOS_U')) {
           fiche.down('crHydrantsIndispo #activeIndispo').hide();
           fiche.down('crHydrantsIndispo #leverIndispo').hide();
           fiche.down('crHydrantsIndispo #gererIndispo').hide();
           fiche.down('crHydrantsIndispo #prolongerIndispo').hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('INDISPOS_D')) {
           fiche.down('crHydrantsIndispo #deleteIndispo').hide();
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

        // Synchronisation éventuelle des 2 couches hydrant vector / raster
        // Déclencheurs : visibilité, opacité, rafraîchissement
        var hydrantLayer = this.getTabMap().getLayerByCode('hydrantLayer');
        var hydrantLayerWms = this.getTabMap().getLayerByCode('hydrantLayerWms');
        if (hydrantLayer && hydrantLayerWms) {
            hydrantLayer.events.on({
               'visibilitychanged': function() {
                   // Changement de visibilité
                   hydrantLayerWms.setVisibility(hydrantLayer.getVisibility());
               },
               'refresh': function() {
                   // Rafraîchissement en fonction du type de couche (via disponibilité des méthodes)
                   if (hydrantLayerWms.clearGrid) {
                       hydrantLayerWms.clearGrid();
                   }
                   if (hydrantLayerWms.redraw) {
                       hydrantLayerWms.redraw(true);
                   }
                   if (hydrantLayerWms.refresh) {
                       hydrantLayerWms.refresh({
                           force: true
                       });
                   }
               }
            });
            this.getTabMap().map.events.on({
                'changelayer': function(eOpts) {
                   if (eOpts.layer == hydrantLayer && eOpts.property=='opacity') {
                       if (!Number.isNaN(hydrantLayer.opacity)) {
                           hydrantLayerWms.setOpacity(hydrantLayer.opacity);
                       }
                   }
                }
            });
        }
    },

    createVisiteRapide: function(features) {
        if (!features || features.length<1) {
            return;
        }
        var featuresCount = features.length;
        if (featuresCount == 1) {
            // Une seule feature : on ouvre la fiche directement
            var f = features[0];
            this.showFicheHydrant(f.attributes.typeHydrantCode, f.fid, true/*visite*/);
        } else {
            // Plusieurs features : on laisse le choix avant d'ouvrir la fiche
            var data = [], i;
            // On propose à l'utilisateur de choisir la feature de travail
            for (i=0 ; i<features.length ; i++) {
                var cFeat = features[i];
                data.push([cFeat, cFeat.data['numero'] + ' - ' + cFeat.data['nomCommune']]);
            }
            Ext.widget('sdischoice', {
                title : 'Saisir une visite',
                width : 600,
                height : undefined,
                bodyPadding : 10,
                explanationsConfig : {
                    xtype : 'panel',
                    border : false,
                    html : '<ul style="margin-bottom:10px;">Veuillez préciser le point d\'eau visité.'
                },
                cboConfig : {
                    fieldLabel : 'Point d\'eau ',
                    store : new Ext.data.SimpleStore({
                        fields: ['feature', 'display'],
                        data : data
                    }),
                    displayField : 'display',
                    valueField : 'feature',
                    forceSelection : true,
                    editable : false,
                    queryMode : 'local',
                    value : data[0][0]
                },
                listeners: {
                    scope: this,
                    valid: function(record) {
                        var f = record.get('feature');
                        this.showFicheHydrant(f.attributes.typeHydrantCode, f.fid, true/*visite*/);
                    }
                }
            }).show();
        }
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
        this.enableBtnIf('editInfoBtn', nbSelect == 1);
        this.enableBtnIf('editInfoBtnNoCtrl', nbSelect == 1);
        this.enableBtnIf('deleteBtn', nbSelect == 1);
        this.enableBtnIf('affecterBtn', nbSelect > 0);
        this.enableBtnIf('desaffecterBtn', nbSelect > 0);
        this.enableBtnIf('activeMoveBtn', nbSelect == 1);
        this.enableBtnIf('indispoBtn', nbSelect > 0);
        this.enableBtnIf('editIndispoBtn', nbSelect == 1);
        this.enableBtnIf('debitSimultaneBtn', nbSelect > 1);
        this.enableBtnIf('saisirMesureBtn', nbSelect == 0);
        this.enableBtnIf('deleteDebitSimultaneBtn', nbSelect == 0);
    },
    enableBtnIf: function(btnId, enableCond) {
        var btn = this.getTabMap().queryById(btnId);
        if (btn) {
            btn.setDisabled(!enableCond);
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
        case 'indispos':
            this.updateIndispo(extraParams);
            break;
        default:
            break;
        }
    },

    updateMap: function(extraParams) {
        var self = this;
        this.lastExtraParams = extraParams;
        this.doHighlightOnce = true;
        // Mise en évidence de la sélection
        if(this.getTabMap().hydrantLayer){
            self.getTabMap().highlightSelection(extraParams);
        }

        this.getTabMap().map.events.register('addlayer', this, function(layer){
            if(layer.layer.code == 'hydrantLayer')
            {
                layer.layer.events.register('featuresadded', this, function(){
                    if(self.doHighlightOnce)
                    {
                        self.getTabMap().highlightSelection(this.lastExtraParams);
                        self.doHighlightOnce = false;
                    }
                });
            }
        });

        if (extraParams.x && extraParams.y) {
                this.getTabMap().centerToPoint(extraParams.x, extraParams.y);

        }else if (extraParams.bounds) {
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

    updateIndispo: function(extraParams) {
        this.getTabIndispo().getStore().load();
        var tbar = this.getTabIndispo().query('pagingtoolbar')[0];
                tbar.bindStore(this.getTabIndispo().getStore());
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
            Sdis.Remocra.util.Util.changeHash(this.tplBoundsTournee.apply({
                bounds: bounds.toBBOX(),
                idTournee: tournee.internalId
            }));
        }
    },

    hydrantsChanged: function() {
        this.refreshMap();
        this.updateHydrant();
    },

    refreshMap: function() {
        if (this.getTabMap() != null && this.getTabMap().rendered) {
            this.getTabMap().hydrantLayer.refresh({
                force: true
            });

            var debitsSimultanesLayer = this.getTabMap().getLayerByCode("debitsSimultanesLayer");
            if(debitsSimultanesLayer) {
                debitsSimultanesLayer.redraw(true);
            }
        }
    },

    onLocateHydrant: function(hydrant) {
        if (hydrant) {
            if (hydrant.get('jsonGeometrie') != null) {
                var geom = Ext.decode(hydrant.get('jsonGeometrie'));
                Sdis.Remocra.util.Util.changeHash(this.tplXY.apply({
                    valX: geom.coordinates[0],
                    valY: geom.coordinates[1],
                    idPEI: hydrant.internalId
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
                bounds = rec.get('bbox').replace(/\|/g, ',');
                this.clearAccess();
                Sdis.Remocra.util.Util.changeHash(this.tplBounds.apply({
                    bounds: bounds
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
        tabTournee.queryById('resetTournee').setDisabled(records.length == 0);
        tabTournee.queryById('renameTournee').setDisabled(records.length == 0);
        tabTournee.queryById('finaliseTournee').setDisabled(records.length == 0);
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
    resetTournee: function() {
        var tournee = this.getSelectedTournee();
        if (tournee != null) {
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../tournees/resetTournee/'+tournee.getId()),
                method: 'PUT',
                scope: this,
                callback: function(param, success, response) {
                    var res = Ext.decode(response.responseText);
                    this.getTabTournee().getStore().load();
                    Sdis.Remocra.util.Msg.msg("Réinitialisation de la tournée", res.message);
                }
            });
        }
    },
    finaliseTournee: function() {
        var tournee = this.getSelectedTournee();
        if (tournee != null) {
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../tournees/finaliseTournee/'+tournee.getId()),
                method: 'PUT',
                scope: this,
                callback: function(param, success, response) {
                    var res = Ext.decode(response.responseText);
                    this.getTabTournee().getStore().load();
                    Sdis.Remocra.util.Msg.msg("Réinitialisation de la tournée", res.message);
                }
            });
        }
    },

    renameTournee: function() {
            var tournee = this.getSelectedTournee();
            if (tournee != null) {
                 Ext.Msg.prompt('Renommer la tournée',
                     'Veuillez saisir le nouveau nom:',
                     function (button, value) {
                         if (button === 'ok' && value !=='') {
                              Ext.Ajax.request({
                                 url: Sdis.Remocra.util.Util.withBaseUrl('../tournees/renameTournee/'+tournee.getId()),
                                 method: 'POST',
                                 params: {nom: value},
                                 scope: this,
                                 callback: function(param, success, response) {
                                     var res = Ext.decode(response.responseText);
                                     if(success){
                                        Sdis.Remocra.util.Msg.msg("Renommer une tournée", res.message);
                                     }else {
                                        Ext.Msg.show({
                                        title: 'Renommer une tournée',
                                        msg: res.message,
                                        buttons: Ext.Msg.OK,
                                        icon: Ext.Msg.WARNING
                                      });
                                     }
                                     this.getTabTournee().getStore().load();
                                 }
                             });
                         }
                     }, this, false, null);
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
            Ext.Msg.confirm('Suppression de la tournée', 'Vous allez supprimer la tournée ' + tournee.data.nom, function(buttonId) {
                if (buttonId == 'yes') {
                    tournee.destroy({
                        scope: this,
                        success: function(record, operation) {
                            this.lastTournee = null;
                            this.getTabTournee().getStore().remove(record);
                            Sdis.Remocra.util.Msg.msg('Suppression', 'La tournée ' + record.data.nom + ' a bien été supprimée.');
                        }
                    });
                }
            }, this);
        }
    },

    cancelReservation: function() {
        var tournee = this.getSelectedTournee();
        Ext.Msg.confirm('Annulation de la réservation', 'Vous allez annuler la réservation de la tournée ' + tournee.data.nom+'. Voulez-vous continuer ?', function(buttonId) {
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
        if (win.feature) {
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
                    this.showFiche(record, controle);
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
                hydrant.feature = feature.clone();
                hydrant.srid = this.getTabMap().getCurrentSrid();
            }
            if (win.commune) {
                hydrant.set('commune', win.commune);
                hydrant.raw.commune = win.commune.raw;
            }
            win.close();
            this.showFiche(hydrant, false);

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

    declareIndispo: function() {
       var features = this.getSelectedFeatures();
          var i = 0;
          var communeId = features[0].data.commune;

                for (i; i < features.length ; i++) {
                   if (features[i].data.commune !== communeId) {
                      Ext.Msg.alert('Indisponibilité temporaire','Merci de vérifier que tous les points d\'eau sélectionnés sont localisés sur la même commune');
                      return;
                   }
                }

       Ext.widget('nouvelleIndispo').show();
    },


    showEditIndispo: function() {
        var indispo = this.getSelectedIndispo();
       Ext.widget('nouvelleIndispo').show();
    },

    editIndispo: function() {
       Ext.widget('editIndispo').show();
    },

    affecteTournee: function() {
        var features = this.getSelectedFeatures();
        var messagelocked = '', feature, i, countLocked = 0, ids = [];

        for (i=0 ; i < features.length ; i++) {
          ids.push(features[i].fid);
        }
        if (ids && ids.length > 0) {
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/checkHydrantsNatureDeci'),
                jsonData: ids,
                callback: function(param, success, response) {
                    if(success){
                       Ext.widget('affectation').show();
                    }else {
                          var res = Ext.decode(response.responseText);
                          Ext.Msg.show({
                          title: 'Affectation',
                          msg: res.message,
                          buttons: Ext.Msg.OK,
                          icon: Ext.Msg.WARNING
                        });

                    }
                }
            });
        }
    },

    desaffecteTournee: function() {
        var features = this.getSelectedFeatures();
        var messagelocked = '', message = '', ids = [], i, countLocked = 0;

        for (i=0 ; i < features.length ; i++) {
                  ids.push(features[i].fid);
        }
        if (ids && ids.length > 0) {
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/checkReservation'),
                jsonData: ids,
                callback: function(param, success, response) {
                    if(success){

                        var hydrants = [];
                        for (i=0; i<features.length;i++){
                            hydrants.push(features[i].data.internalId);
                        }

                        Ext.Ajax.request({
                            url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/getDesaffectationMesssage'),
                            params: {
                                hydrants: JSON.stringify(hydrants)
                            },
                            callback: function(param, success, response) {
                                Ext.Msg.show({
                                    title: 'Désaffectation',
                                    msg: JSON.parse(response.responseText).message,
                                    icon: Ext.Msg.WARNING,
                                    buttonText: {
                                        yes: 'Le mien',
                                        no: 'Tous',
                                        cancel: 'Annuler'
                                    },
                                    buttons: Ext.Msg.YESNOCANCEL,
                                    fn: function(btn){
                                        Ext.Ajax.request({
                                              url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/desaffecter'),
                                              params: {
                                                json: JSON.stringify(ids),
                                                allOrganismes: btn === 'no'
                                              },
                                              callback: function(param, success, response) {
                                                  var res = Ext.decode(response.responseText);
                                                  Sdis.Remocra.util.Msg.msg("Désaffectation", res.message);
                                              }
                                          });
                                    }
                                });
                            }
                        });


                    }else {
                       var res = Ext.decode(response.responseText);
                        Ext.Msg.show({
                          title: 'Désaffectation',
                          msg: res.message,
                          buttons: Ext.MessageBox.YESNO,
                          icon: Ext.Msg.WARNING,
                          buttonText:{
                              yes: "Continuer",
                              no: "Annuler"
                          },
                        fn: function(btn){
                          if(btn === 'yes'){
                             Ext.Ajax.request({
                                  url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/desaffecter'),
                                  jsonData: ids,
                                  callback: function(param, success, response) {
                                      var res = Ext.decode(response.responseText);
                                      Sdis.Remocra.util.Msg.msg("Désaffectation", res.message);
                                  }
                              });
                          }
                        }
                      });

                    }
                }
            });
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
            this.getAffectation().down('component[name=lastTournee]').update(this.lastTournee.data.nom);
        }
    },

    onAffectationRadioChange: function(radioButton, newValue, oldValue) {
        if (radioButton.inputValue == 3) {
            this.getAffectation().down('combo[name=tournee]').setDisabled(oldValue);
        }else if (radioButton.inputValue == 1) {
            this.getAffectation().down('textfield[name=nom]').setDisabled(oldValue);
            this.getAffectation().down('combo[name=organisme]').setDisabled(oldValue);
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
        var tourneeId = null,
        tourneeNom = null,
        organismeId = null,
        nomTournee =  this.getAffectation().down('textfield[name=nom]'),
        cboOrganisme = this.getAffectation().down('combo[name=organisme]'),
        cboTournee = this.getAffectation().down('combo[name=tournee]');

        if ( !nomTournee.isValid() || !cboTournee.isValid() || !cboOrganisme.isValid()) {
            return;
        }

        switch (this.getAffectation().down('radio[checked]').inputValue) {
        case "1": // Nouvelle tournée
            tourneeNom = nomTournee.getValue();
            organismeId = cboOrganisme.getValue();
            break;
        case "2": // Dernière tournée
            if(this.lastTournee.data.reservation && this.lastTournee.data.reservation !== null ){
              Ext.Msg.show({
                  title: "Affectation",
                  msg: 'La tournée sélectionnée est réservée',
                  buttons: Ext.Msg.OK,
                  icon: Ext.Msg.WARNING
                });
              return;
            }
            tourneeId = this.lastTournee.getId();
            organismeId = this.lastTournee.affectationBelongsToInstance.get('id');
            break;
        case "3": // Tournée existante
            tourneeId = cboTournee.getValue();
            organismeId = cboTournee.getValueModel().get('affectation').id;
            break;
        }

        var ids = Ext.Array.pluck(this.getSelectedFeatures(), "fid");

        Ext.Ajax.request({
            scope: this,
            url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/checkTournee'),
            params: {
                json: JSON.stringify(ids),
                idOrganisme: organismeId
            },
            callback: function(param, success, response) {
                var res = Ext.decode(response.responseText);
                if(res.success){ // Si toutes les vérif sont OK, on affecte la tournée
                    Ext.Ajax.request({
                        scope: this,
                        url: Sdis.Remocra.util.Util.withBaseUrl('../hydrants/affecter'),
                        jsonData: {
                            ids: ids,
                            tournee: tourneeId,
                            nom: tourneeNom,
                            organisme: organismeId
                        },
                        callback: function(param, success, response) {
                            var res = Ext.decode(response.responseText);
                            if(success){
                                Sdis.Remocra.util.Msg.msg("Affectation", res.message);
                                this.getAffectation().close();
                            }else {
                                 Ext.Msg.show({
                                      title: "Affectation",
                                      msg: res.message,
                                      buttons: Ext.Msg.OK,
                                      icon: Ext.Msg.WARNING
                                });
                            }

                        }
                    });
                }
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
        tabHydrant.queryById('openHydrant').setVisible(Sdis.Remocra.Rights.hasRight('HYDRANTS_C')||
        Sdis.Remocra.Rights.hasRight('HYDRANTS_RECONNAISSANCE_C') || Sdis.Remocra.Rights.hasRight('HYDRANTS_CONTROLE_C'));
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
    },

   /******************************************************************************
   *Fenetre "Nouvelle Indisponibilite temporaire"
   ******************************************************************************/
   afterShowNouvelleIndispo: function() {
         var grid = this.getNouvelleIndispo().down('#gridHydrantIndispos');
   if(this.getTabIndispo() && this.getTabIndispo().isVisible()){
     this.getNouvelleIndispo().setTitle("Gestion de l'indisponibilité temporaire");
     var indispo = this.getSelectedIndispo();
     var datePrevDebut = indispo.get('datePrevDebut');
     var datePrevFin = indispo.get('datePrevFin');
     var dateDebut = indispo.get('dateDebut');
     this.getNouvelleIndispo().down('checkbox[name=activationImmediate]').setValue(true);
     this.getNouvelleIndispo().down('datefield[name=dateDebut]').setValue(dateDebut);
     this.getNouvelleIndispo().down('timefield[name=timeDebut]').setValue(dateDebut);
     this.getNouvelleIndispo().down('datefield[name=datePrevDebut]').setValue(datePrevDebut);
     this.getNouvelleIndispo().down('timefield[name=timePrevDebut]').setValue(datePrevDebut);
     this.getNouvelleIndispo().down('datefield[name=datePrevFin]').setValue(datePrevFin);
     this.getNouvelleIndispo().down('timefield[name=timePrevFin]').setValue(datePrevFin);
     this.getNouvelleIndispo().down('textfield[name=motif]').setValue(indispo.get('motif'));
            Ext.defer(function() {
                this.getNouvelleIndispo().down('#gridHydrantIndispos').bindStore(indispo.hydrants(), true);
            }, 150, this);
      if(indispo.getStatut().get('code') !== 'PLANIFIE') {
           this.setReadOnly(this.getNouvelleIndispo());
           this.getNouvelleIndispo().queryById('validHydrantIndispo').hide(true);
           this.getNouvelleIndispo().queryById('annulHydrantIndispo').setText('Fermer');
           var msgErrorField = this.getNouvelleIndispo().down('displayfield[name=errorMsg]');
           msgErrorField.setValue('Seules les indisponibilités en état "Planifié" peuvent être modifiées.');
           msgErrorField.show();
      }
   }else {
     this.getNouvelleIndispo().down('checkbox[name=activationImmediate]').setValue(false);
     this.getNouvelleIndispo().down('datefield[name=dateDebut]').setValue(new Date());
     this.getNouvelleIndispo().down('timefield[name=timeDebut]').setValue(new Date());
     this.getNouvelleIndispo().down('datefield[name=datePrevDebut]').setValue(new Date());
     this.getNouvelleIndispo().down('timefield[name=timePrevDebut]').setValue(new Date());
     this.getNouvelleIndispo().down('datefield[name=datePrevFin]').setValue(new Date());
     this.getNouvelleIndispo().down('timefield[name=timePrevFin]').setValue(new Date());
     this.getNouvelleIndispo().down('textfield[name=motif]').setValue(null);
      var features = this.getSelectedFeatures();
      var i = 0;
                    for (i; i < features.length ; i++) {
                       var feature = features[i];
                       feature.data.id=feature.fid;
                       grid.store.add(feature.data);
                    }
                  grid.getView().refresh();
     }

   },

   addHydrantIndispo : function() {
      var grid = this.getNouvelleIndispo().down('#gridHydrantIndispos');
      var comboIndispo = this.getNouvelleIndispo().down('combo[name=numHydrant]');
                  var hydrant = comboIndispo.findRecordByValue(comboIndispo.getValue());
                    if(hydrant){
                        //On teste si la commune de l'hydrant est la meme
                        if (hydrant.data.commune !== grid.store.getAt(0).data.commune) {
                             Ext.Msg.alert('Indisponibilité temporaire','Merci de vérifier que tous les points d\'eau sélectionnés sont localisés sur la même commune');
                             return;
                        }
                        if(grid.store.findRecord("id", hydrant.getId()) == null) {
                          grid.store.add(hydrant);
                          grid.getView().refresh();
                        }
                    }

   },

   deleteHydrantIndispo : function() {
      var grid = this.getNouvelleIndispo().down('#gridHydrantIndispos');
      var hydrant = grid.getSelectionModel().getSelection();
      grid.getStore().remove(hydrant);
   },
   validHydrantIndispo: function() {
              var indispo = Ext.create('Sdis.Remocra.model.HydrantIndispoTemporaire');
              if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
                 indispo = this.getSelectedIndispo();
                 indispo.set("id",indispo.get('id'));
              }
              var grid = this.getNouvelleIndispo().down('#gridHydrantIndispos');
              var hydrantData = [];
              var i=0;
              for (i; i < grid.store.data.items.length; i++) {
                 hydrantData.push(grid.store.data.items[i].data);
              }
              //on récupère les Ids des hydrants concernés
              var ids = Ext.Array.pluck(hydrantData, "id");
              if(this.getNouvelleIndispo().down('checkbox[name=activationImmediate]').getValue() == true) {
                            var dateDebut = this.getNouvelleIndispo().down('datefield[name=datePrevDebut]').getValue();
                            var timeDebut = this.getNouvelleIndispo().down('timefield[name=timePrevDebut]').getValue();
                            dateDebut.setHours(timeDebut.getHours(),timeDebut.getMinutes());
                            var dateFin = this.getNouvelleIndispo().down('datefield[name=datePrevFin]').getValue();
                            var timeFin = this.getNouvelleIndispo().down('timefield[name=timePrevFin]').getValue();
                            dateFin.setHours(timeFin.getHours(),timeFin.getMinutes());

                            indispo.set('datePrevDebut',dateDebut);
                            indispo.set('datePrevFin',dateFin);
              }else {
                            var dateReelDebut = this.getNouvelleIndispo().down('datefield[name=dateDebut]').getValue();
                            var timeReelDebut = this.getNouvelleIndispo().down('timefield[name=timeDebut]').getValue();
                            dateReelDebut.setHours(timeReelDebut.getHours(),timeReelDebut.getMinutes());
                            indispo.set("dateDebut", dateReelDebut);
              }
              indispo.set("motif",this.getNouvelleIndispo().down('textfield[name=motif]').getValue());
              //On compare les dates pour éviter que la date de fin soit superieure à la date de debut
           if(indispo.get('dateFin')!= null && indispo.get('dateFin') < indispo.get('dateDebut')) {
              Ext.Msg.alert('Indisponibilité temporaire','La date de Fin ne doit pas être antérieure à la date de début');
              return;
           }
           if(indispo.get('dateFin')== null && indispo.get('datePrevFin') < indispo.get('datePrevDebut')) {
              Ext.Msg.alert('Indisponibilité temporaire','La date prévisionnelle de Fin ne peut pas être antérieure à la date prévisionnelle de début');
              return;
           }
          if(grid.getStore().getCount()==0) {
              Ext.Msg.alert('Indisponibilité temporaire','Veuillez renseigner au moins un point d\'eau.');
              return;
          }
           Ext.Ajax.request({
                  scope: this,
                  url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/indispoTemp'),
                  jsonData: {
                      ids: ids,
                      indispo: indispo.data
                  },
                  callback: function(param, success, response) {
                      this.refreshMap();
                      var res = Ext.decode(response.responseText);
                      Sdis.Remocra.util.Msg.msg("Indisponibilite", res.message);
                      this.getNouvelleIndispo().close();
                       if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
                         this.getTabIndispo().getStore().load();
                       }
                  }
              });
   },

    onChangeImmediate: function(checkbox) {
            this.getNouvelleIndispo().down('#containDebReel').setVisible(!checkbox.getValue());
            this.getNouvelleIndispo().down('#containDebPrev').setVisible(checkbox.getValue());
            this.getNouvelleIndispo().down('#containFinPrev').setVisible(checkbox.getValue());
    },

    onEditIndispo: function() {
       var gridIndispos = this.getEditIndispo().down('#gridIndispos');
       var features =this.getSelectedFeatures();
       gridIndispos.getStore().load({
                         scope: this,
                         params: {
                             filter: Ext.encode([{
                                 property: 'hydrantId',
                                 value: features[0].fid
                             }])
                         }
       });
       if (!Sdis.Remocra.Rights.hasRight('INDISPOS_U')) {
          this.getEditIndispo().down('#activIndispo').hide();
          this.getEditIndispo().down('#levIndispo').hide();
       }
    },
    /***************************************************************************
     * Onglet "Indisponibilités temporaires"
     **************************************************************************/

     showActiveIndispo: function(){
     Ext.widget('activeIndispo').show();

     },

     showLeveIndispo: function(){
     Ext.widget('leveIndispo').show();

     },

     showProlongerIndispo: function(){
        Ext.widget('prolongerIndispo').show();
        var indispo = null;
        if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
            indispo = this.getSelectedIndispo();
        }else{
            indispo = this.getSelectedIndispoFromMap();
        }
        var dateFin = indispo.get('dateFin');
        var date = this.getFormattedDate(dateFin, false);
        var time = this.getFormattedTime(dateFin, false);
        this.getProlongerIndispo().down('datefield[name=dateFin]').setValue(date);
        this.getProlongerIndispo().down('timefield[name=timeFin]').setValue(time);        
    },

     showListerPeiIndispo: function(){
        Ext.widget('listerPeiIndispo').show();
     },

     onSelectIndispo: function(sel, records, index, opt){
       var tabIndispos = this.getTabIndispo();
       var indispo = records[0];
          if(indispo!=null) {
              //tant qu'il y'a une selection on autorise la localisation et la suppression

               tabIndispos.queryById('deleteIndispo').setDisabled(records.length == 0);
               tabIndispos.queryById('locateIndispo').setDisabled(records.length == 0);
               tabIndispos.queryById('gererIndispo').setDisabled(records.length == 0);
               tabIndispos.queryById('listerPeiIndispo').setDisabled(records.length == 0);

             if(indispo.getStatut().get('code') == 'EN_COURS'){ // On permet la levée de l'indispo & on interdit la suppression
               tabIndispos.queryById('activeIndispo').setDisabled(records.length != 0);
               tabIndispos.queryById('leverIndispo').setDisabled(records.length == 0);
               tabIndispos.queryById('gererIndispo').setDisabled(records.length != 0);
               tabIndispos.queryById('deleteIndispo').setDisabled(records.length != 0);
               if(indispo.get('dateFin') != null){
                tabIndispos.queryById('prolongerIndispo').setDisabled(records.length == 0);
               } else {
                tabIndispos.queryById('prolongerIndispo').setDisabled(records.length != 0);
               }
             }else if(indispo.getStatut().get('code') == 'TERMINE'){ // indispo terminée on desactive tout
               tabIndispos.queryById('activeIndispo').setDisabled(records.length != 0);
               tabIndispos.queryById('leverIndispo').setDisabled(records.length != 0);
               tabIndispos.queryById('gererIndispo').setDisabled(records.length != 0);
               tabIndispos.queryById('prolongerIndispo').setDisabled(records.length != 0);
             }else if(indispo.getStatut().get('code') == 'PLANIFIE'){ //indispo en previsionnelle on autorise l'activation et la gestion
               tabIndispos.queryById('activeIndispo').setDisabled(records.length == 0);
               tabIndispos.queryById('leverIndispo').setDisabled(records.length != 0);
               tabIndispos.queryById('prolongerIndispo').setDisabled(records.length != 0);
                         
             }
          }else {// En cas de suppression on reinitialise tout
            tabIndispos.queryById('gererIndispo').setDisabled(records.length == 0);
            tabIndispos.queryById('activeIndispo').setDisabled(records.length == 0);
            tabIndispos.queryById('leverIndispo').setDisabled(records.length == 0);
            tabIndispos.queryById('deleteIndispo').setDisabled(records.length == 0);
            tabIndispos.queryById('locateIndispo').setDisabled(records.length == 0);
            tabIndispos.queryById('prolongerIndispo').setDisabled(records.length == 0);
            tabIndispos.queryById('listerPeiIndispo').setDisabled(records.length == 0);
          }
     },

     onSelectIndispoFromMap: function(sel, records, index, opt){
       var editIndispos = this.getEditIndispo();
       var indispo = this.getSelectedIndispoFromMap();
             if(indispo.getStatut().get('code') == 'EN_COURS'){
             editIndispos.queryById('activIndispo').setDisabled(records.length != 0);
             editIndispos.queryById('levIndispo').setDisabled(records.length == 0);
             }else if(indispo.getStatut().get('code') == 'TERMINE') {
               editIndispos.queryById('activIndispo').setDisabled(records.length != 0);
               editIndispos.queryById('levIndispo').setDisabled(records.length != 0);
             }else {
               editIndispos.queryById('activIndispo').setDisabled(records.length == 0);
               editIndispos.queryById('levIndispo').setDisabled(records.length != 0);
             }
     },

    getSelectedIndispo: function() {
        if (!this.getTabIndispo()) {
            console.warn('soucis ??');
            return;
        }
        var record = this.getTabIndispo().getSelectionModel().getSelection();
        if (record != null && Ext.isArray(record)) {
            record = record[0];
        }
        return record;
    },
    
    getSelectedIndispoFromMap: function() {
        if (!this.getEditIndispo()) {
            console.warn('soucis ??');
            return;
        }
        var record = this.getEditIndispo().queryById('gridIndispos').getSelectionModel().getSelection();
        if (record != null && Ext.isArray(record)) {
            record = record[0];
        }
        return record;
    },

    onActiveIndispo : function() {
        var indispo = null;
        if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
            indispo = this.getSelectedIndispo();
        }else{
            indispo = this.getSelectedIndispoFromMap();
            var features =this.getSelectedFeatures();
        }
        var dateDebut = this.getFormattedDate(new Date(), true);
        var timeDebut = this.getFormattedTime(new Date(), true);
        var dateTimeDebut = new Date(dateDebut + "T" + timeDebut);
        Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/activeIndispoTemp/'+indispo.getId()),
                method: 'POST',
                params: {dateDebut: dateTimeDebut},
                scope: this,
                callback: function(param, success, response) {
                    this.refreshMap();
                    var res = Ext.decode(response.responseText);
                    this.getActiveIndispo().close();
                    if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
                        this.getTabIndispo().getStore().load();
                    }else {
                    this.getEditIndispo().queryById('activIndispo').setDisabled(true);
                        this.getEditIndispo().queryById('gridIndispos').getStore().load({
                                scope: this,
                                params: {
                                filter: Ext.encode([{
                                property: 'hydrantId',
                                value: features[0].fid
                                }])
                            }
                        });
                    }
                    Sdis.Remocra.util.Msg.msg("Activation de l'indisponibilité", res.message);
                }
        });

    },

    onLeveIndispo : function() {
        var indispo = null;
        if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
            indispo = this.getSelectedIndispo();
        }else{
            indispo = this.getSelectedIndispoFromMap();
            var features =this.getSelectedFeatures();
        }
        var dateFin = this.getFormattedDate(new Date(), true);
        var timeFin = this.getFormattedTime(new Date(), true);
        var dateTimeFin = new Date(dateFin + "T" + timeFin);
        //On compare les dates pour éviter que la date de fin soit superieur à la date de debut
        if(dateFin < indispo.get('dateDebut')) {
            Ext.Msg.alert('Indisponibilité temporaire','La date de fin ne doit pas être antérieure à la date de début.');
            return;
        }
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/leveIndispoTemp/'+indispo.getId()),
            method: 'POST',
            params: {dateFin: dateTimeFin},
            scope: this,
            callback: function(param, success, response) {
                this.refreshMap();
                var res = Ext.decode(response.responseText);
                this.getLeveIndispo().close();
                if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
                    this.getTabIndispo().getStore().load();

                }else {
                    this.getEditIndispo().queryById('levIndispo').setDisabled(true);
                    this.getEditIndispo().queryById('gridIndispos').getStore().load({
                        scope: this,
                        params: {
                            filter: Ext.encode([{
                                property: 'hydrantId',
                                value: features[0].fid
                            }])
                        }
                    });
                }
                Sdis.Remocra.util.Msg.msg("Fin de l'indisponibilité", res.message);
            }
        });

    },

    getFormattedDate: function(fullDate, yearFirst) {
      var myDate = fullDate;
      var month = ('0' + (myDate.getMonth() + 1)).slice(-2);
      var date = ('0' + myDate.getDate()).slice(-2);
      var year = myDate.getFullYear();
      var formattedDate = '';
      if(yearFirst){
        formattedDate = year + '-' + month + '-' + date;
      } else {
        formattedDate = date + '/' + month + '/' + year;
      }
      return formattedDate;
    },

    getFormattedTime: function(fullDate, avecZeros){
      var myTime = fullDate;
      var hour = ('0' + (myTime.getHours() )).slice(-2);
      var minutes = ('0' + myTime.getMinutes()).slice(-2);
      var formattedTime = '';
      if(avecZeros){
        formattedTime = hour+':'+minutes+':'+'00';
      } else {
        formattedTime = hour+':'+minutes;
      }
      return formattedTime;
    },

    onLocateIndispo: function() {
    var indispo = this.getSelectedIndispo();
     if (Ext.isEmpty(indispo.get('geometrie'))) {
              Ext.Msg.show({
                  title: 'Localisation d\'une indisponibilité',
                  msg: 'La localisation est impossible car aucun point d\'eau n\'a été renseigné pour cette indisponibilité.',
                  buttons: Ext.Msg.OK,
                  icon: Ext.Msg.INFO
              });
          } else {
              var bounds = Sdis.Remocra.util.Util.getBounds(indispo.get('geometrie'));
              Sdis.Remocra.util.Util.changeHash(this.tplBoundsIndispo.apply({
                  bounds: bounds.toBBOX(),
                  idIndispo: indispo.internalId
              }));
          }
    },

    deleteIndispo: function() {
    var indispo = this.getSelectedIndispo();
    Ext.Ajax.request({
        url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/'+indispo.getId()),
        method: 'DELETE',
        scope: this,
        callback: function(param, success, response) {
            this.refreshMap();
            var res = Ext.decode(response.responseText);
            if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
              this.getTabIndispo().getStore().load();
              this.refreshMap();
            }
            Sdis.Remocra.util.Msg.msg("Indisponibilité temporaire supprimée", res.message);
        }
    });

    },

    loadHydrantsIndispo: function(){
    //on teste si on est en mode map ou grid
      if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
           if (this.getActiveIndispo() && this.getActiveIndispo().isVisible()){
               this.getActiveIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispo().hydrants().data.items);
           }else if(this.getLeveIndispo() && this.getLeveIndispo().isVisible()){
               this.getLeveIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispo().hydrants().data.items);
           } else if(this.getProlongerIndispo() && this.getProlongerIndispo().isVisible()){
               this.getProlongerIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispo().hydrants().data.items);
           } else if(this.getListerPeiIndispo() && this.getListerPeiIndispo().isVisible()){
            this.getListerPeiIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispo().hydrants().data.items);
           }
      } else if(this.getTabMap() && this.getTabMap().isVisible()) {
          if (this.getActiveIndispo() && this.getActiveIndispo().isVisible()){
               this.getActiveIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispoFromMap().hydrants().data.items);
           }else if(this.getLeveIndispo() && this.getLeveIndispo().isVisible()){
               this.getLeveIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispoFromMap().hydrants().data.items);
           } else if(this.getProlongerIndispo() && this.getProlongerIndispo().isVisible()){
            this.getProlongerIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispo().hydrants().data.items);
           } else if(this.getListerPeiIndispo() && this.getListerPeiIndispo().isVisible()){
            this.getListerPeiIndispo().queryById('gridHydrantIndispos').getStore().add (this.getSelectedIndispo().hydrants().data.items);
           }
      }
    },
     setReadOnly: function(component) {
        if (Ext.isFunction(component.cascade)) {
            component.cascade(function(item) {
                if (Ext.isFunction(item.setReadOnly)) {
                    item.setReadOnly(true);
                }
                if (Ext.isFunction(item.hideTrigger)) {
                    item.hideTrigger();
                }
                if (item.getToolbar) {
                    toolbar = item.getToolbar();
                }
                if (item.isXType('button')) {
                   item.setDisabled(true);
                }
                if (item.isXType('grid')) {
                    Ext.Array.each(item.plugins, function(plugin) {
                        plugin.beforeEdit = function() {
                            return false;
                        };
                    });
                    Ext.Array.each(item.columns, function(column) {
                        if (column.isXType('actioncolumn')) {
                            column.destroy();
                        }
                    }, null, true);
                }
            });
        }
     },
      showFiche: function(hydrant, controle) {
              var codeHydrant = (hydrant.get('code') == 'PIBI' ? 'PIBI' : 'PENA');
              var geometrie = hydrant.data.geometrie;
              var idHydrant = hydrant.data.id;
              var title = idHydrant ?
              codeHydrant + " n° " + hydrant.data.numero + " - " + hydrant.data.nomCommune.replace(/'/g, ' ')
              : 'Nouveau ' + codeHydrant;
              var d = document.createElement('div');
              var id = "show-fiche-"+(++Ext.AbstractComponent.AUTO_ID);
              d.id=id;
              document.body.appendChild(d);
               var vueFiche = window.remocraVue.peiBuildFiche(d, {
                         id: idHydrant, code: codeHydrant, geometrie: geometrie, newVisite: controle, title: title
                     });
               vueFiche.$options.bus.$on('pei_modified', Ext.bind(function(data) {
                       this.hydrantsChanged();
               }, this));

               vueFiche.$options.bus.$on('closed', Ext.bind(function(data) {
                       vueFiche.$el.remove();
                       vueFiche.$destroy();
               }, this));
      },

      showFicheIndispoTempFromMap: function(){
        var features = Object.values(this.getSelectedFeatures());
        var cpt = 0;
        var communeId = features[0].data.commune;

        for (cpt; cpt < features.length ; cpt++) {
            if (features[cpt].data.commune !== communeId) {
                Ext.Msg.alert('Indisponibilité temporaire','Merci de vérifier que tous les points d\'eau sélectionnés sont localisés sur la même commune');
                return;
            }
        }
        var idIndispoTemp = null;
        var tabNumPeiSelected = [];
        var tabIdPeiSelected = [];

        var i = 0;
        for(i; i < features.length; i++)
        {
            tabNumPeiSelected[i] = features[i].data.numero;
            tabIdPeiSelected[i] = features[i].fid;
        }

        var d = document.createElement('div');
        document.body.appendChild(d);
        var vueFicheIndispo = window.remocraVue.indispoTempBuildFiche(d, {idIndispoTemp: idIndispoTemp, tabIdPeiSelected: tabIdPeiSelected, tabNumPeiSelected: tabNumPeiSelected});

        vueFicheIndispo.$options.bus.$on('closed', Ext.bind(function(data) {
            vueFicheIndispo.$el.remove();
            vueFicheIndispo.$destroy();
            }, this));
      },

      showFicheIndispoTempFromGrid: function(){
        var indispo = this.getSelectedIndispo();

        var idIndispoTemp = indispo.get('id');

        var d = document.createElement('div');
        document.body.appendChild(d);
        var vueFicheIndispo = window.remocraVue.indispoTempBuildFiche(d, {idIndispoTemp: idIndispoTemp, tabIdPeiSelected: null, tabNumPeiSelected: null});

        vueFicheIndispo.$options.bus.$on('closed', Ext.bind(function(data) {
            vueFicheIndispo.$el.remove();
            vueFicheIndispo.$destroy();
            this.getTabIndispo().getStore().load();
            }, this));
    },

    onProlongerIndispo: function(){
        var indispo = null;
        if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
            indispo = this.getSelectedIndispo();
        }else{
            indispo = this.getSelectedIndispoFromMap();
            var features =this.getSelectedFeatures();
        }
        var newDateFin = this.getProlongerIndispo().down('datefield[name=dateFin]').getValue();
        var newTimeFin = this.getProlongerIndispo().down('timefield[name=timeFin]').getValue();
        newDateFin.setHours(newTimeFin.getHours(),newTimeFin.getMinutes());
        //On compare les dates pour éviter que la date de fin soit superieur à la date de debut
        if(newDateFin < indispo.get('dateFin')) {
            Ext.Msg.alert('Indisponibilité temporaire','La nouvelle date de fin ne doit pas être antérieure à la date de fin initiale.');
            return;
        }
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/prolongerIndispo/'+indispo.getId()),
            method: 'POST',
            params: {dateFin: newDateFin},
            scope: this,
            callback: function(param, success, response) {
                this.refreshMap();
                var res = Ext.decode(response.responseText);
                this.getProlongerIndispo().close();
                if(this.getTabIndispo() && this.getTabIndispo().isVisible()) {
                    this.getTabIndispo().getStore().load();
                }
                Sdis.Remocra.util.Msg.msg("Prolongation de l'indisponibilité", res.message);
            }
        });           
    },

  createDebitSimultane: function() {
        var features = this.getSelectedFeatures();

        var title="Création du débit simultané impossible";
        var text = "";

        var hydrantsCompatibles = true;
        var listeCodesDeci = [];
        var listeTypesReseau = [];
        var listeDiametres = [];

        var dataCreation = {
            hydrants: []
        };

        // Filtre permettant de supprimer les doublons d'une collection
        var filtre = function onlyUnique(value, index, self) {
            return self.indexOf(value) === index;
        };

        features.forEach(function(hydrant){

            dataCreation.hydrants.push({
                id: hydrant.data.internalId,
                numero: hydrant.data.numero
            });

            dataCreation.site = {
                nom: hydrant.data.siteNom,
                id: hydrant.data.siteId
            };

            dataCreation.diametre = {
                nom: hydrant.data.diametreNom,
                id: hydrant.data.diametreId
            };

            dataCreation.typeReseau = {
                nom: hydrant.data.typeReseauNom,
                id: hydrant.data.typeReseauNom
            };

            if(hydrant.data.codeNatureDeci){
                listeCodesDeci.push(hydrant.data.codeNatureDeci);
            }

            if(hydrant.data.typeReseau) {
                listeTypesReseau.push(hydrant.data.typeReseau);
            }

            if(hydrant.data.diametre) {
                listeDiametres.push(hydrant.data.diametre);
            }
        });


        // Vérifiation sur la nature DECI
        if(listeCodesDeci.length != features.length && hydrantsCompatibles) {
            hydrantsCompatibles = false;
            text = features.length-listeCodesDeci.length+" hydrant(s) n'a/n'ont pas de nature DECI attribuée";
        }
        else if(hydrantsCompatibles && (listeCodesDeci.filter(filtre).length > 1 || (listeCodesDeci.filter(filtre).length > 0 && listeCodesDeci.filter(filtre)[0] !== "PRIVE"))) {
            hydrantsCompatibles = false;
            text = "Les hydrants ne sont pas tous de nature DECI privée";
        }

        // Vérification sur le type de réseau
        if(listeTypesReseau.length != features.length && hydrantsCompatibles) {
            hydrantsCompatibles = false;
            text = features.length-listeTypesReseau.length+" hydrant(s) n'a/n'ont pas de type de réseau attribué";
        }
        else if(listeTypesReseau.filter(filtre).length > 1 && hydrantsCompatibles) {
            hydrantsCompatibles = false;
            text = "Les hydrants sélectionnés n'ont pas tous le même type de réseau";
        }

        // Vérification sur le diamètre
        if(listeDiametres.length != features.length && hydrantsCompatibles) {
            hydrantsCompatibles = false;
            text = features.length-listeDiametres.length+" hydrant(s) n'a/n'ont pas de diamètre nominal attribué";
        }
        else if(listeDiametres.filter(filtre).length > 1 && hydrantsCompatibles) {
            hydrantsCompatibles = false;
            text = "Les hydrants sélectionnés n'ont pas tous le même diamètre nominal";
        }

        if(hydrantsCompatibles){
            this.showDebitSimultaneFiche(null, dataCreation);
        } else {
            Ext.Msg.show({
                title : title,
                msg : text,
                buttons : Ext.Msg.OK,
                icon : Ext.Msg.WARNING
            });
        }

      },

      /**
        * Clic sur la map avec le bouton de saisie de mesure ou de suppression de débit simultané activé
        * On recherche les DS autour du point cliqué, et on effectue l'action correspondante
        * Si plusieurs points regroupés au même endroit, on affiche une fenêtre pour sélectionner le bon DS
        * @param coords Les coordonnées du point cliqué (Système de projection WGS 84 / Pseudo-Mercator)
        * @param typeEvent Le type d'évènement qui a déclenché la fonction:
        *   - 'mesure' lors d'une saisie de mesure,
        *   - 'suppression' lors d'une suppression de débit simultané
        **/
      onClickDebitSimultaneFromMap: function(coords, typeEvent) {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../debitsimultane/getdebitssimultanesfromlonlat'),
            method: 'GET',
            params: {
                lon: coords.lon,
                lat: coords.lat,
                srid: 3857,
                distance: 15
            },
            scope: this,
            callback: function(options, success, response) {
                if(success){
                    var data = JSON.parse(response.responseText);
                    if(data.features.length == 1) {

                        if(typeEvent == 'mesure') {
                            this.showDebitSimultaneFiche(data.features[0].id);
                        } else {
                            this.deleteDebitSimultane(data.features[0].id);
                        }

                    } else if(data.features.length > 1) {
                        var listeDS = [];
                        data.features.forEach(function(debit) {
                            listeDS.push({
                                text: debit.properties.numDossier,
                                value: debit.properties.id
                            });
                        });
                        this.showDebitSimultaneSelection(typeEvent, listeDS);
                    }
                }
            }
        });
      },

      /**
        * Modale de sélection du débit simultané
        * S'affiche si lors du clic utilsiateur, plusieurs DS présents sur une zone restreinte
        * @param typeSelection 'mesure' si modification du DS, 'suppression' sinon
        * @param comboDebits Un tableau contenant les informations pour la combobox des débits simultanés
        */
      showDebitSimultaneSelection: function(typeSelection, comboDebits) {
        var d = document.createElement('div');
        var id = "show-debitSimultaneSelection-"+(++Ext.AbstractComponent.AUTO_ID);
        d.id=id;
        document.body.appendChild(d);
        var vueDebitSimultaneSelection = window.remocraVue.debitSimultaneSelection(d, {
          typeSelection: typeSelection, comboDebits: comboDebits
        });

        // Validation du formulaire: le DS est choisi, on redirige vers la bonne fonction
        vueDebitSimultaneSelection.$options.bus.$on('debit_simultane_selected', Ext.bind(function(data) {
            if(data.type === 'mesure') {
                this.showDebitSimultaneFiche(data.id);
            } else {
                this.deleteDebitSimultane(data.id);
            }
        }, this));

        vueDebitSimultaneSelection.$options.bus.$on('closed', Ext.bind(function(data) {
            vueDebitSimultaneSelection.$el.remove();
            vueDebitSimultaneSelection.$destroy();
        }, this));
      },

      // Affichage de la fiche d'un débit simultané
      showDebitSimultaneFiche: function(idDebitSimultane, dataCreation) {
        var d = document.createElement('div');
        var id = "show-debitSimultaneFiche-"+(++Ext.AbstractComponent.AUTO_ID);
        d.id=id;
        document.body.appendChild(d);
        var vueDebitSimultaneFiche = window.remocraVue.debitSimultaneFiche(d, {
          idDebitSimultane: idDebitSimultane,
          dataOnCreate: dataCreation || null,
          vitesseEau: VITESSE_EAU
        });

        vueDebitSimultaneFiche.$options.bus.$on('closed', Ext.bind(function(data) {
            vueDebitSimultaneFiche.$el.remove();
            vueDebitSimultaneFiche.$destroy();

            this.getTabMap().map.getControlsByClass('OpenLayers.Control.SelectFeature')[0].unselectAll();
            this.refreshMap();
        }, this));
      },

      // Suppression d'un débit simultané
      deleteDebitSimultane: function(id) {
        Ext.Msg.confirm('Suppression débit simultané', 'Confirmez-vous la suppression du débit simultané ?', function(buttonId) {
            if (buttonId == 'yes') {
                Ext.Ajax.request({
                    url: Sdis.Remocra.util.Util.withBaseUrl('../debitsimultane/'+id),
                    method: 'DELETE',
                    scope: this,
                    callback: function(options, success, response) {
                        if(success){
                            this.refreshMap();
                            Sdis.Remocra.util.Msg.msg('Le débit simultané a bien été supprimé');
                        }
                    }
                });
            }
        }, this);
      }

});
