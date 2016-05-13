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

        this.editItems.push('Modifier : ');
        this.editItems.push({
            tooltip: 'Ouvrir la fiche du point d\'eau',
            text: '<span>Informations</span>',
            cls: 'edit-info',
            iconCls: 'edit-infoIcon',
            itemId: 'editInfoBtn',
            disabled: true
        });

        if (Sdis.Remocra.Rights.getRight('HYDRANTS').Delete) {
            this.editItems.push({
                tooltip: 'Supprimer un élément',
                text: '<span>Supprimer</span>',
                cls: 'delete',
                iconCls: 'deleteIcon',
                itemId: 'deleteBtn',
                disabled: true
            });

            //Déplacement hydrant
            this.editItems.push('Déplacer : ',{
                tooltip: 'Activer le déplacement',
                text: '<span>Activer</span>',
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

        if (Sdis.Remocra.Rights.getRight('HYDRANTS_EXPORT_NON_NUM').Create) {
            this.moreItems = [ { tooltip: 'Télécharger la liste des hydrants non numérotés', text: '<span>Télécharger</span>',
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
                    var file = 'images/remocra/cartes/legende/eau/';
                    if (feature.data['typeHydrantCode'] == 'PIBI') {
                        file += 'pibi/' + (feature.data['nature'] == 'PI' ? 'pi' : 'bi');
                        if (feature.data['debit'] < 30) {
                            file += '_low';
                        } else if (feature.data['debit'] < 60) {
                            file += '_med';
                        } else {
                            file += '_high';
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