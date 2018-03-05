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
        if (Sdis.Remocra.Rights.getRight('HYDRANTS').Create) {
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

        this.editItems.push('Saisir une visite : ');
        if(Sdis.Remocra.Rights.getRight('HYDRANTS').Create||
                Sdis.Remocra.Rights.getRight('HYDRANTS_RECONNAISSANCE').Create || Sdis.Remocra.Rights.getRight('HYDRANTS_CONTROLE').Create){
        this.editItems.push({
            tooltip: 'Saisir une visite (réception, contrôle, reconnaissance, vérification)',
            text: '<span>Informations</span>',
            cls: 'edit-info',
            iconCls: 'add',
            itemId: 'editInfoBtn',
            disabled: true
        });
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

        if (Sdis.Remocra.Rights.getRight('HYDRANTS').Delete) {
            this.editItems.push('Supprimer : ',{
                tooltip: 'Supprimer un élément',
                cls: 'delete',
                iconCls: 'deleteIcon',
                itemId: 'deleteBtn',
                disabled: true
            });
        }
        if (Sdis.Remocra.Rights.getRight('HYDRANTS_DEPLACEMENT').Create) {
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

        if (Sdis.Remocra.Rights.getRight('TOURNEE').Create) {
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
        if (Sdis.Remocra.Rights.getRight('INDISPOS').Create) {
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

        if (Sdis.Remocra.Rights.getRight('HYDRANTS_EXPORT_NON_NUM').Create) {
            this.moreItems = [ { tooltip: 'Télécharger la liste des points d\'eau non numérotés', text: '<span>Télécharger</span>',
                cls: 'download-atlas', iconCls: 'download-atlasIcon',
                itemId: 'downloadHydrantsNonNum', xtype:'button'
            }];
        }

        this.callParent(arguments);
    },

    createSpecificLayer: function(layerDef) {

        if (layerDef.id == 'hydrantLayer') {
            layerDef.url = BASE_URL + '/../hydrants/layer';
            layerDef.styleMap = this.getStyleMap();
            layerDef.infoMarginInPixels = 10;
            this.hydrantLayer = this.createGeoJsonLayer(layerDef);

            this.addSpecificControlLate('selectPolygone', new OpenLayers.Control.SelectFeature([this.workingLayer,this.hydrantLayer], {
                clickout: true,
                toggle: true,
                multiple: false,
                hover: false,
                multipleKey: "ctrlKey",
                toggleKey: "shiftKey",
                box: true
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
        if (HYDRANT_SYMBOLOGIE == '77') {
            return this.getStyleMap77();
        }
        return this.getStyleMap83();
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

    /**
     * Style de la couche de travail : une étiquette avec * si numéro disponible
     */
    workingLayerStyleMap: function() {
        if (HYDRANT_SYMBOLOGIE == '77') {
            return this.workingLayerStyleMap77();
        }
        return this.workingLayerStyleMap83();
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

    workingLayerStyleMap83: function() {
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
    }
});