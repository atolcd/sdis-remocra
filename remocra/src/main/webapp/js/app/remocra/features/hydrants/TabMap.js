Ext.require('Sdis.Remocra.widget.map.Map');

Ext.define('Sdis.Remocra.features.hydrants.TabMap', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crHydrantsMap',

    title: 'Localisation',
    itemId: 'localisation',

    moreItems: [],

    legendUrl: BASE_URL + '/../ext-res/js/app/remocra/features/hydrants/data/carte.json',

    initComponent: function() {

        this.editItems = ['Sélectionner : ',{
            xtype: 'button',
            tooltip: 'Sélectionner des points d\'eau',
            text: '<span>Sélectionner</span>',
            iconCls: 'select-polygoneIcon',
            toggleGroup: 'excl-ctrl1',
            enableToggle: true,
            pressed: false,
            allowDepress: true,
            itemId: 'selectBtn'
        }];

        this.editItems.push('Créer : ');
        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_C')) {
            this.editItems.push({
                xtype: 'button',
                tooltip: 'Ajouter un point d\'eau',
                text: '<span>Dessiner</span>',
                cls: 'dessiner',
                iconCls: 'dessinerIcon',
                toggleGroup: 'excl-ctrl1',
                enableToggle: true,
                pressed: false,
                allowDepress: true,
                itemId: 'dessinerBtn'
            });
        }

        if(Sdis.Remocra.Rights.hasRight('HYDRANTS_C')
                || Sdis.Remocra.Rights.hasRight('HYDRANTS_RECONNAISSANCE_C')
                || Sdis.Remocra.Rights.hasRight('HYDRANTS_CONTROLE_C')) {
            this.editItems.push('Saisir une visite : ');
            if(!HYDRANT_VISITE_RAPIDE) {
                this.editItems.push({
                    tooltip: 'Saisir une visite (réception, contrôle, reconnaissance, vérification)',
                    xtype: 'button',
                    text: '<span>Informations</span>',
                    cls: 'edit-info',
                    iconCls: 'add',
                    itemId: 'editInfoBtn',
                    disabled: true
                });
            } else {
                this.editItems.push({
                    tooltip: 'Saisir une visite (réception, contrôle, reconnaissance, vérification)',
                    xtype: 'button',
                    text: '<span>Informations</span>',
                    cls: 'edit-info',
                    iconCls: 'add',
                    toggleGroup: 'excl-ctrl1',
                    enableToggle: true,
                    pressed: false,
                    allowDepress: true,
                    itemId: 'editInfoVisiteRapideBtn'
                });
            }
        }

        this.editItems.push('Ouvrir la fiche : ');
        this.editItems.push({
            tooltip: 'Ouvrir la fiche du point d\'eau',
            text: '<span>Informations</span>',
            cls: 'edit-info',
            iconCls: 'edit-infoIcon',
            itemId: 'editInfoBtnNoCtrl',
            disabled: true
        });

        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_D')) {
            this.editItems.push('Supprimer : ', {
                tooltip: 'Supprimer un élément',
                cls: 'delete',
                iconCls: 'deleteIcon',
                itemId: 'deleteBtn',
                disabled: true
            });
        }
        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_DEPLACEMENT_C')) {
            //Déplacement hydrant
            this.editItems.push('Déplacer : ',{
                tooltip: 'Activer le déplacement',
                iconCls: 'editIcon',
                itemId: 'activeMoveBtn',
                disabled: true,
                enableToggle: true,
                pressed: false,
                allowDepress: true,
                toggleGroup: 'excl-ctrl1'
            });

            this.editItems.push({
                tooltip: 'Valider le déplacement',
                text: 'Valider',
                width: 50,
                itemId: 'validMoveBtn',
                disabled: true,
                hidden: true
            });

            this.editItems.push({
                tooltip: 'Annuler le déplacement',
                text: 'Annuler',
                width: 50,
                itemId: 'cancelMoveBtn',
                disabled: true,
                hidden: true
            });
        }

        if (Sdis.Remocra.Rights.hasRight('TOURNEE_C')) {
            this.editItems.push('-');
            this.editItems.push('Tournée', {
                tooltip: 'Désaffecte les points d\'eau de leur tournée',
                text: '<span>Affecter</span>',
                cls: 'edit-info',
                iconCls: 'leftIcon',
                itemId: 'desaffecterBtn',
                disabled: true
            }, {
                tooltip: 'Affecte les points d\'eau à une tournée',
                text: '<span>Affecter</span>',
                cls: 'edit-info',
                iconCls: 'rightIcon',
                itemId: 'affecterBtn',
                disabled: true
            });
        }

        this.editItems.push('Indisponibilité temporaire : ');
        if (Sdis.Remocra.Rights.hasRight('INDISPOS_C')) {
            this.editItems.push({
                xtype: 'button',
                tooltip: 'Déclarer une indisponibilité temporaire',
                text: '<span>Déclarer indispo temp.</span>',
                cls: 'time-add',
                iconCls: 'time-addIcon',
                itemId: 'indispoBtn',
                disabled: true
            });
        }

            this.editItems.push({
                xtype: 'button',
                tooltip: 'Modifier une indisponibilité temporaire',
                text: '<span>Modifier indispo temp.</span>',
                cls: 'edit-info',
                iconCls: 'edit-infoIcon',
                itemId: 'editIndispoBtn',
                disabled: true
            });

        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_EXPORT_NON_NUM_C')) {
            this.moreItems = [ { tooltip: 'Télécharger la liste des points d\'eau non numérotés', text: '<span>Télécharger</span>',
                cls: 'download-atlas', iconCls: 'download-atlasIcon',
                itemId: 'downloadHydrantsNonNum', xtype:'button'
            }];
        }
        this.timeoutHighlight = null;

        this.callParent(arguments);
    },

    createSpecificLayer: function(layerDef) {

        if (layerDef.id == 'hydrantLayer') {
            layerDef.url = BASE_URL + '/../hydrants/layer';
            layerDef.styleMap = this.getStyleMap();
            layerDef.infoMarginInPixels = 10;
            this.hydrantLayer = this.createGeoJsonLayer(layerDef);

            // Sélection par tracé d'un rectangle
            this.addSpecificControlLate('selectPolygone', new OpenLayers.Control.SelectFeature([this.workingLayer,this.hydrantLayer], {
                clickout: true,
                toggle: true,
                multiple: false,
                hover: false,
                multipleKey: "ctrlKey",
                toggleKey: "shiftKey",
                box: true
            }));
            // Sélection par clic
            this.addSpecificControlLate('selectPoint', new OpenLayers.Control.SelectFeature([this.workingLayer,this.hydrantLayer], {
                clickout: true,
                toggle: true,
                multiple: false,
                hover: false,
                multipleKey: "ctrlKey",
                toggleKey: "shiftKey",
                box: false
            }));

            this.addSpecificControlLate('movePoint', new OpenLayers.Control.ModifyFeature(this.workingLayer, {
                standalone: true,
                dragComplete: function(feature) {
                    this.events.triggerEvent('dragcomplete', {feature : feature});
                },
                dragStart: function(feature) {
                    this.events.triggerEvent('dragstart', {feature : feature});
                }
            }));

            return this.hydrantLayer;
        }
        return this.callParent(arguments);

    },

    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            drawPoint: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point)
        });
        return ctrls;
    },

    getStyleMap: function() {

        switch (HYDRANT_SYMBOLOGIE){
            case '42' :
                return this.getStyleMap42();
            case '77' :
                return this.getStyleMap77();
            case '83' :
                return this.getStyleMap83();
            case '89' :
                return this.getStyleMap89();
            default :
                return this.getStyleMapGEN();
        }
    },

    getStyleMapGEN: function() {
        var customStyle = new OpenLayers.Style({
            fillOpacity: 1,
            externalGraphic: '${imgLegend}',
            graphicWidth: 20,
            graphicHeight: 20,
            graphicYOffset: -10,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '${fontColor}'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return;
                    }
                    return numero;
                },
                imgLegend: function(feature) {
                    var file = 'ext-res/images/remocra/cartes/legende/eau/';
                    var nature = feature.data['nature'];

                    file += nature=='PI'||nature=='BI'?nature:'PN';
                    file += '_'+(feature.data['dispo']||'INCONNU');
                    if (feature.renderIntent == 'select') {
                        file += '_on';
                    }
                    return file + '.png';
                },
                fontColor: function(feature) {
                    return feature.renderIntent=='select'?'#000':'#005ce6';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": customStyle,
            "select": customStyle
        });
    },

    getStyleMap42: function() {
        var customStyle = new OpenLayers.Style({
            fillOpacity: 1,
            externalGraphic: '${imgLegend}',
            graphicWidth: 24,
            graphicHeight: 24,
            graphicYOffset: -12,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '${fontColor}'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return;
                    }
                    var splited = numero.split('_');
                    if (splited.length>1) {
                        numero = splited[1];
                    }
                    return numero;
                },
                imgLegend: function(feature) {
                    var file = 'ext-res/images/remocra/cartes/legende/eau/';
                    var unknown = file + 'INCONNU' + (feature.renderIntent=='select'?'_on':'') + '.png';
                    file += feature.data['nature'];
                    if (feature.data['nature'] == 'PI') {
                        if (!feature.data['diametre']) {
                            return unknown;
                        }
                        file += '_'+feature.data['diametre'];
                    }
                    if (feature.data['nature'] == 'PI' || feature.data['nature'] == 'BI') {
                        var debit = feature.data['debit'];
                        if (debit === undefined || debit === null) {
                            return unknown;
                        }
                        var debitCateg = null;
                        if (debit < 30) {
                            debitCateg = 'L30';
                        } else if (debit < 60) {
                            debitCateg = 'L60';
                        } else if (debit < 90) {
                             debitCateg = 'L90';
                        } else if (debit < 120) {
                            debitCateg = 'L120';
                        } else if (debit < 150) {
                            debitCateg = 'L150';
                        } else if (debit < 180) {
                            debitCateg = 'L180';
                        } else if (debit < 240) {
                            debitCateg = 'L240';
                        } else if (debit >= 240) {
                            debitCateg = 'GE240';
                        } else {
                            return unknown;
                        }
                        file += '_' +debitCateg;
                    }
                    if (!feature.data['dispo']) {
                        return unknown;
                    }
                    file += '_'+feature.data['dispo'];
                    if (feature.renderIntent == 'select') {
                        file += '_on';
                    }
                    return file + '.png';
                },
                fontColor: function(feature) {
                    return feature.renderIntent=='select'?'#ff00ff':'#000';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": customStyle,
            "select": customStyle
        });
    },

    getStyleMap77: function() {
        var customStyle = new OpenLayers.Style({
            fillOpacity: 1,
            externalGraphic: '${imgLegend}',
            graphicWidth: 20,
            graphicHeight: 20,
            graphicYOffset: -10,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#000'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return;
                    }
                    if (numero.length>4) {
                        numero = numero.substring(numero.length-4);
                    }
                    return numero;
                },
                imgLegend: function(feature) {
                    var file = 'ext-res/images/remocra/cartes/legende/eau/';
                    file += feature.data['nature'];
                    if (feature.data['typeHydrantCode'] == 'PIBI') {
                        file += '_'+(feature.data['diametre']||'INCONNU');
                    } else {
                        file += '_'+(feature.data['positionnement']||'INCONNU');
                    }
                    file += '_'+(feature.data['dispo']||'INCONNU');
                    if (feature.renderIntent == 'select') {
                        file += '_on';
                    }
                    return file + '.png';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": customStyle,
            "select": customStyle
        });
    },

    getStyleMap83: function() {
        var customStyle = new OpenLayers.Style({
            fillOpacity: 1,
            externalGraphic: '${imgLegend}',
            graphicWidth: '${imgWidth}',
            graphicHeight: '${imgHeight}',
            graphicYOffset: '${imgYOffset}',
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#0f0fff'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return;
                    }
                    // Si uniquement partie numérique : return numero.replace(/[^0-9\.]+/g, '');
                    return numero;
                },
                imgLegend: function(feature) {
                    var file = 'ext-res/images/remocra/cartes/legende/eau/';
                    if (feature.data['typeHydrantCode'] == 'PIBI') {
                        file += 'pibi/';
                        if (feature.data['nature'] == 'PA') {
                            // Pas de débit / pression pour les PA
                            file += 'pa';
                        } else {
                            file += (feature.data['nature'] == 'PI' ? 'pi' : 'bi');
                            var debitRetenu = (feature.data['debitMax']!=null)?feature.data['debitMax']:feature.data['debit'];
                            if (debitRetenu < 30) {
                                file += '_low';
                            } else if (debitRetenu < 60) {
                                file += '_med';
                            } else {
                                file += '_high';
                            }
                        }
                    } else {
                        file += 'pena/pn_' + (feature.data['nature'] == 'CI_FIXE' ? 'citerne_fixe' : 'autre');
                        if (feature.data['hbe']) {
                            file += '_hbe';
                        }
                    }
                    if (feature.data['dispo'] == 'INDISPO') {
                        file += '_indispo';
                    }
                    if (feature.renderIntent == 'select') {
                        file += '_on';
                    }
                    return file + '.png';
                },
                imgWidth: function(feature) {
                    if (feature.data['typeHydrantCode'] == 'PIBI') {
                        return 18;
                    }
                    return 17;
                },
                imgHeight: function(feature) {
                    if (feature.data['typeHydrantCode'] == 'PIBI') {
                        return 18;
                    }
                    if (feature.data['hbe']) {
                        return 25;
                    }
                    return 17;
                },
                imgYOffset: function(feature) {
                    if (feature.data['typeHydrantCode'] == 'PIBI') {
                        return -(18 / 2);
                    }
                    if (feature.data['hbe']) {
                        return -16.5;
                    }
                    return -(17 / 2);
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": customStyle,
            "select": customStyle
        });
    },

    getStyleMap89: function() {
        var customStyle = new OpenLayers.Style({
            fillOpacity: 1,
            externalGraphic: '${imgLegend}',
            graphicWidth: 20,
            graphicHeight: 20,
            graphicYOffset: -10,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#000'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return;
                    }
                    return numero;
                },
                imgLegend: function(feature) {
                    var file = 'ext-res/images/remocra/cartes/legende/eau/';
                    file += feature.data['nature'];
                    file += '_'+(feature.data['dispo']||'INCONNU');
                    if (feature.renderIntent == 'select') {
                        file += '_on';
                    }
                    return file + '.png';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": customStyle,
            "select": customStyle
        });
    },

    /**
     * Style de la couche de travail : une étiquette avec * si numéro disponible
     */
    workingLayerStyleMap: function() {
        if (HYDRANT_SYMBOLOGIE == '42') {
            return this.workingLayerStyleMap42();
        } else if (HYDRANT_SYMBOLOGIE == '77') {
            return this.workingLayerStyleMap77();
        }
        return this.workingLayerStyleMapGEN();
    },

    workingLayerStyleMapGEN: function() {
        var sm = new OpenLayers.Style({
            fillColor: '#64C0FF',
            fillOpacity: 0.8,
            strokeColor: '#64C0FF',
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 5,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#64C0FF'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return '';
                    }
                    return '*' + numero + '*';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": sm,
            "select": sm,
            "temporary": sm
        });
    },

    workingLayerStyleMap42: function() {
        var sm = new OpenLayers.Style({
            fillColor: '#64C0FF',
            fillOpacity: 0.8,
            strokeColor: '#64C0FF',
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 5,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#64C0FF'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return;
                    }
                    var splited = numero.split('_');
                    if (splited.length>1) {
                        numero = splited[1];
                    }
                    return '*' + numero + '*';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": sm,
            "select": sm,
            "temporary": sm
        });
    },

    workingLayerStyleMap77: function() {
        var sm = new OpenLayers.Style({
            fillColor: '#64C0FF',
            fillOpacity: 0.8,
            strokeColor: '#64C0FF',
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 5,
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#64C0FF'
        }, {
            context: {
                label: function(feature) {
                    var numero = feature.data['numero'];
                    if (!numero) {
                        return '';
                    }
                    if (numero.length>4) {
                        numero = numero.substring(numero.length-4);
                    }
                    return '*' + parseInt(numero, 10) + '*';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default": sm,
            "select": sm,
            "temporary": sm
        });
    },

    assumeBaseLayer: function() {
        this.callParent(arguments);

        // Mise en évidence de PEI
        this.highlightLayer = new OpenLayers.Layer.Vector('highlightLayer', {
            style: {
                externalGraphic: "ext-res/images/remocra/cartes/legende/highlight/highlight.png",
                graphicWidth: 32,
                graphicHeight: 32,
                fillOpacity: 1
            }
        });

        this.map.addLayer(this.highlightLayer);
    },

    highlightSelection: function(extraParams){
        var self = this;
        clearTimeout(this.timeoutHighlight);
        if(extraParams.i){
            // PEIs d'une indisponibilité temporaire
            Ext.Ajax.request({
                url: Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites/getHydrantsIndispo/'+extraParams.i),
                method: 'GET',
                scope: this,
                async: false,
                callback: function(param, success, response) {
                    var res = Ext.decode(response.responseText).message;
                    var peiIndispo = res.replace(/<\/?[^>]+(>|$)/g, ";").split(';'); //On récupère la liste des PEI sans le formatage HTML
                    this.hydrantLayer.features.forEach(function(item){
                        if(peiIndispo.indexOf(item.attributes.numero) != -1){
                            self.highlightLayer.addFeatures(new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(item.geometry.x, item.geometry.y)));
                            self.clearHighlightLayerDelayed();
                        }
                    });
                }
            });
        }
        else if(extraParams.t){
            // PEIs d'une tournée
            this.hydrantLayer.features.forEach(function(item){
                if(item.attributes.tournees !== null && item.attributes.tournees.indexOf(extraParams.t) != -1){
                    self.highlightLayer.addFeatures(new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(item.geometry.x, item.geometry.y)));
                    self.clearHighlightLayerDelayed();
                }
            });
        }
        else if(extraParams.h){
            //Un PEI
            this.hydrantLayer.features.forEach(function(item){
                if(item.attributes.internalId == extraParams.h){
                    self.highlightLayer.addFeatures(new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(item.geometry.x, item.geometry.y)));
                    self.clearHighlightLayerDelayed();
                }
            });
        }

    },

    clearHighlightLayerDelayed: function() {
        var self = this;
        this.timeoutHighlight = setTimeout(function(){
            self.highlightLayer.removeAllFeatures();
        }, HYDRANT_HIGHLIGHT_DUREE);
    }
});