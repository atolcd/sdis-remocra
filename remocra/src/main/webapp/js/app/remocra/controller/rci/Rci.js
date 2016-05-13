Ext.require('Sdis.Remocra.features.rci.Fiche');
Ext.require('Sdis.Remocra.widget.SdisChoice');

Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.store.Voie');
Ext.require('Sdis.Remocra.store.Rci');
Ext.require('Sdis.Remocra.store.TypeRciOrigineAlerte');
Ext.require('Sdis.Remocra.store.TypeRciPromCategorie');
Ext.require('Sdis.Remocra.store.TypeRciPromFamille');
Ext.require('Sdis.Remocra.store.TypeRciPromPartition');
Ext.require('Sdis.Remocra.store.TypeRciDegreCertitude');

Ext.define('Sdis.Remocra.controller.rci.Rci', {
    extend: 'Ext.app.Controller',

    stores: ['Commune', 'Voie', 'Rci', 'TypeRciOrigineAlerte', 'TypeRciPromCategorie', 'TypeRciPromFamille', 'TypeRciPromPartition', 'TypeRciDegreCertitude'],

    refs: [{
        ref: 'rci',
        selector: 'crRci'
    },{
        ref: 'map',
        selector: 'crRciMap'
    }],

    init: function() {
        this.control({
            'crRciMap': {
                layersadded: this.initControlMap
            },
            'crRciMap #addBtn': {
                toggle: function(button, pressed) {
                    this.getMap().activateSpecificControl('drawPoint', pressed);
                }
            },
            'crRciMap #addXYBtn': {
                click: this.onFeatureAddXY
            },
            'crRciMap #selectBtn': {
                toggle: function(button, pressed) {
                    this.getMap().activateSpecificControl('selectPolygone', pressed);
                }
            },
            'crRciMap #editInfoBtn': {
                click: this.showEditModeFiche
            },
            'crRciMap #deleteBtn': {
                click: this.deleteEltFromMap
            },
            'crRciMap #activeMoveBtn': {
                toggle: function(button, pressed) {
                    this.getMap().activateSpecificControl('movePoint', pressed);
                }
            },
            'crRciMap #validMoveBtn': {
                click: this.validMove
            },
            'crRciMap #cancelMoveBtn': {
                click: this.cancelMove
            },
            'crRciMap #hideOldRciBtn': {
                click: this.toggleOldRciBtn
            }
        });
    },

    /***************************************************************************
     * Global
     **************************************************************************/

    addEventToControl: function(controlName, cfg) {
        var control = this.getMap().getSpecificControl(controlName);
        if (control) {
            Ext.applyIf(cfg, {
                scope: this
            });
            control.events.on(cfg);
        }
    },

    initControlMap: function() {
        // Sélection
        this.getMap().rciLayer.events.on({
            'featureselected': this.onSelectedFeatureChange,
            'featureunselected': this.onSelectedFeatureChange,
            scope: this
        });
        
        // Dessin
        this.addEventToControl('drawPoint', {
            'featureadded': this.onFeatureAdded
        });

        // Déplacement
        this.addEventToControl('movePoint', {
            'activate': this.onActivateMovePoint,
            'deactivate': this.onDeactivateMovePoint,
            'dragcomplete' : this.onDragCompleteMovePoint
        });
    },

    manageDisabledButtonsMove: function(disabled){
        var validMoveBtn = this.getMap().down('#validMoveBtn');
        var cancelMoveBtn = this.getMap().down('#cancelMoveBtn');
        if(validMoveBtn != null){
            validMoveBtn.setDisabled(disabled);
        }
        if(cancelMoveBtn != null){
            cancelMoveBtn.setDisabled(disabled);
        }
    },

    manageVisibilityButtonsMove: function(visible){
        var validMoveBtn = this.getMap().down('#validMoveBtn');
        var cancelMoveBtn = this.getMap().down('#cancelMoveBtn');
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
        this.manageDisabledButtonsMove(true);
    },

    onDragCompleteMovePoint: function(vector) {
        this.manageDisabledButtonsMove(false);
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
        var workingLayer = this.getMap().workingLayer;
        workingLayer.removeAllFeatures();
        workingLayer.addFeatures(workingFeature);
        var movePoint = this.getMap().getSpecificControl('movePoint');
        movePoint.selectFeature(workingFeature);
        movePoint.geometrySaved = workingFeature.geometry.clone();
        this.manageVisibilityButtonsMove(true);
    },

    onDeactivateMovePoint: function() {
        this.onCancelMovePoint();
        var movePoint = this.getMap().getSpecificControl('movePoint');
        if (movePoint.geometrySaved) {
            movePoint.geometrySaved = null;
        }
        this.getMap().workingLayer.removeAllFeatures();
        this.manageVisibilityButtonsMove(false);
    },

    onCancelMovePoint: function() {
        var feature = this.getMap().workingLayer.features[0];
        var movePoint = this.getMap().getSpecificControl('movePoint');
        if (movePoint.geometrySaved) {
            feature.move(new OpenLayers.LonLat(movePoint.geometrySaved.x,movePoint.geometrySaved.y));
        }
    },

    onOKMovePoint: function() {
        // Récupération de la feature de travail
        var feature = this.getMap().workingLayer.features[0];
        var mapProjection = this.getMap().map.getProjection();
        var wktFormat = new OpenLayers.Format.WKT({
            internalProjection: mapProjection,
            externalProjection: new OpenLayers.Projection('EPSG:2154')
        });
        var transfGeom = wktFormat.extractGeometry(feature.geometry);
        Ext.Ajax.request({
            scope: this,
            method :'POST',
            url: Sdis.Remocra.util.Util.withBaseUrl('../rci/'+feature.data['id']+'/deplacer'),
            params: {
                geometrie: transfGeom,
                srid: 2154
            },
            callback: function(param, success, response) {
                var res = Ext.decode(response.responseText);
                if (success) {
                    var movePoint = this.getMap().getSpecificControl('movePoint');
                    movePoint.geometrySaved = null;
                    Sdis.Remocra.util.Msg.msg('Départ de feu', 'Le départ de feu a bien été déplacé.');
                } else {
                    Ext.MessageBox.show({
                        title: 'Déplacer un départ',
                        msg: res.message,
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                     });
                }
                // On désactive l'outil, on désactive le bouton et on rafraichit
                // la couche de éléments
                this.onDeactivateMovePoint();
                var activeMoveBtn = this.getMap().queryById('activeMoveBtn');
                activeMoveBtn.toggle(false);
                activeMoveBtn.setDisabled(true);
                this.refreshMap();
            }
        });
    },

    onSelectedFeatureChange: function(event) {
        var rciLayer = event.object, nbSelect = rciLayer.selectedFeatures.length;
        var btnFiche = this.getMap().queryById('editInfoBtn');
        var btnDelete = this.getMap().queryById('deleteBtn');
        var btnActiveDeplacer = this.getMap().queryById('activeMoveBtn');
        btnFiche.setDisabled(nbSelect != 1);
        if (btnDelete != null) {
            btnDelete.setDisabled(nbSelect != 1);
        }
        if(btnActiveDeplacer != null){
            btnActiveDeplacer.setDisabled(nbSelect != 1);
        }
        // On détruit la fenêtre éventuelle de choix (événement généré pour chaque feature cas sélection et désélection)
        if (Ext.getCmp('choiceRciSel')) {
            Ext.getCmp('choiceRciSel').destroy();
        }
        if (nbSelect > 1) {
            var data = [], i;
            // On propose à l'utilisateur de choisir la feature de travail
            for (i=0 ; i<rciLayer.selectedFeatures.length ; i++) {
                var cFeat = rciLayer.selectedFeatures[i];
                data.push([cFeat, cFeat.data['dateIncendie'] + ' - ' + cFeat.data['coordDFCI']]);
            }
            Ext.widget('sdischoice', {
                id : 'choiceRciSel',
                title : 'Départ de feu',
                width : 600,
                height : undefined,
                bodyPadding : 10,
                okLbl : 'Ajuster la sélection',
                cancelLbl : 'Conserver la sélection',
                explanationsConfig : {
                    xtype : 'panel',
                    border : false,
                    html : '<ul style="margin-bottom:10px;">Plusieurs départs ont été sélectionnés. Vous pouvez :'
                        + '<li style="margin-left:10px;">&#8226; <b>Conserver la sélection</b> telle quelle ('
                        + data.length + ' départ' + (data.length>1?'s':'') + '),</li>'
                        + '<li style="margin-left:10px;">&#8226; <b>Ajuster la sélection</b> en retenant un seul départ dans la liste.</li>'
                        + '</ul>'
                },
                cboConfig : {
                    fieldLabel : 'Choix du départ',
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
                        this.selectedFeature(record.get('feature'));
                    }
                }
            }).show();
        }
    },

    deleteElt: function(id, featLbl, config) {
        Ext.Msg.confirm('Départ de feu', 'Confirmez-vous la suppression du départ <br/>' +
                featLbl + ' ?', function(buttonId) {
            if (buttonId == 'yes') {
                Ext.Ajax.request(Ext.applyIf({
                    url: Sdis.Remocra.model.Rci.proxy.url + '/' + id,
                    method: 'DELETE'
                }, config));
            }
        }, this);
    },

    refreshMap: function() {
        if (this.getMap() != null && this.getMap().rendered) {
            this.getMap().rciLayer.refresh({
                force: true
            });
        }
    },

    /***************************************************************************
     * Localisation
     **************************************************************************/

    onFeatureAdded: function(event) {
        var feature = event.feature;
        this.getMap().down('#addBtn').toggle(false);
        var record = this.createNewRecord();
        var mapProjection = this.getMap().map.getProjection();
        var wktFormat = new OpenLayers.Format.WKT({
            internalProjection: mapProjection,
            externalProjection: new OpenLayers.Projection('EPSG:2154')
        });
        var str = wktFormat.write(feature);
        record.set('geometrie', str);
        record.feature = feature;
        record.srid = this.getMap().getCurrentSrid();

        Ext.widget('rciFiche', {
            record : record
        }).show();
    },

    onFeatureAddXY: function() {
        this.getMap().down('#addXYBtn').toggle(false);
        this.showNewModeFiche();
    },
    
    getSelectedFeatures: function() {
        var features = this.getMap().rciLayer.selectedFeatures;
        return Ext.Array.from(features);
    },
    selectedFeature: function(feature) {
        var selectFeature = this.getMap().map.getControlsByClass('OpenLayers.Control.SelectFeature')[0];
        selectFeature.activate();
        selectFeature.unselectAll();
        if (Ext.isArray(feature)) {
            var features = feature, i;
            for (i=0 ; i<features.length ; i++) {
                selectFeature.select(features[i]);
            }
        } else {
            selectFeature.select(feature);
        }
    },

    /**
     * Ouverture d'une fiche en mode création (saisie du X, Y)
     */
    showNewModeFiche: function() {
        Ext.widget('rciFiche', {
            record : this.createNewRecord()
        }).show();
    },

    /**
     * En principe, ouverture d'une fiche en mode édition.
     * Si aucune feature n'est sélectionnée, ouverture en mode création.
     */
    showEditModeFiche: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            // Ouvrir la fiche d'un départ existant
            Sdis.Remocra.model.Rci.load(features[0].fid, {
                scope: this,
                success: function(record) {
                    Ext.widget('rciFiche', {
                        record : record
                    }).show();
                }
            });
        } else {
            this.showNewModeFiche();
        }
    },

    deleteEltFromMap: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            var cFeat = features[0];
            var featLbl = cFeat.data['dateIncendie'] + ' - ' + cFeat.data['coordDFCI'];
            this.deleteElt(cFeat.fid, featLbl, {
                scope: this,
                success: function() {
                    Sdis.Remocra.util.Msg.msg('Départ de feu', 'Le départ de feu ' + featLbl + ' a bien été supprimé.');
                    this.refreshMap();
                },
                failure: function() {
                    Ext.MessageBox.show({
                        title: 'Départ de feu',
                        msg: 'Une erreur est survenue lors de la suppression du départ.',
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                     });
                }
            });
        }
    },

    createNewRecord: function() {
        var record = Ext.create('Sdis.Remocra.model.Rci', {
            dateIncendie : new Date()
        });
        record.setUtilisateur(null);
        record.setArriveeDdtmOnf(null);
        record.setArriveeSdis(null);
        record.setArriveeGendarmerie(null);
        record.setArriveePolice(null);
        record.setOrigineAlerte(null);
        record.setCommune(null);
        record.setCategoriePromethee(null);
        record.setDegreCertitude(null);
        return record;
    },

    toggleOldRciBtn: function(btn, evt, eOpts) {
        var rciLayer = this.getMap().rciLayer;
        rciLayer.hideOldRci = btn.pressed;
        rciLayer.redraw();
    }
});
