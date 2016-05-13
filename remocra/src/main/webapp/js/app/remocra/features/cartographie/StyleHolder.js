/**
 * Holder des styles carto pour le module de cartographies
 */
Ext.define('Sdis.Remocra.features.cartographie.StyleHolder', {
    singleton : true,

    /**
     * Style courant utilisé lors de la création de chaque nouvelle feature
     */
    currentStyleAttributes : {
        // -- Point
        pointRadius : 5,
        graphicName : '', // Attention, graphicName ne doit pas être null
        externalGraphic : '',
        // ou undefined (d'où chaine vide par défaut)
        rotation : 0, // en degrés sens des aiguilles d'une montre
        // -- Ligne
        strokeColor : '#64C0FF',
        strokeOpacity : 1,
        strokeWidth : 2,
        strokeLinecap : 'round',
        strokeDashstyle : 'solid',
        // -- Remplissage
        fillColor : '#64C0FF',
        fillOpacity : 0.8,
        // -- Texte
        label : '',
        fontColor : 'red',
        fontSize : 15
    // -- Non gérés
    // Pour externalGraphic : graphicWidth, graphicHeight (alternative à
    // pointRadius) et graphicXOffset, graphicYOffset
    // Pour texte : fontFamily, fontWeight
    },

    /**
     * Création du style des features de la couche de travail. Les informations
     * de style de chaque feature sont stockées dans les attributs de la
     * feature.
     */
    workingLayerStyleMap : function() {
        var defaultStyle = new OpenLayers.Style({
            // Point
            pointRadius : '${pointRadius}',
            graphicName : '${graphicName}',
            externalGraphic : '${externalGraphic}',
            rotation : '${rotation}',
            // Ligne
            strokeColor : '${strokeColor}',
            strokeOpacity : '${strokeOpacity}',
            strokeWidth : '${strokeWidth}',
            strokeLinecap : '${strokeLinecap}',
            strokeDashstyle : '${strokeDashstyle}',
            // Remplissage
            fillColor : '${fillColor}',
            fillOpacity : '${fillOpacity}',
            // Texte
            label : '${label}',
            fontColor : '${fontColor}',
            fontSize : '${fontSize}',
            labelOutlineColor : "white",
            labelOutlineWidth : 3
        }, {
            // Les dernières valeurs définies par l'utilisateur sont récupérées
            // pour chaque feature lors du premier rendu
            context : {
                // Point
                pointRadius : function(feature) {
                    if (!Ext.isDefined(feature.attributes['pointRadius'])) {
                        feature.attributes.pointRadius = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.pointRadius;
                    }
                    return feature.attributes['pointRadius'];
                },
                graphicName : function(feature) {
                    if (!Ext.isDefined(feature.attributes['graphicName'])) {
                        feature.attributes.graphicName = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.graphicName;
                    }
                    return feature.attributes['graphicName'];
                },
                externalGraphic : function(feature) {
                    if (!Ext.isDefined(feature.attributes['externalGraphic'])) {
                        feature.attributes.externalGraphic = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.externalGraphic;
                    }
                    return feature.attributes['externalGraphic'];
                },
                rotation : function(feature) {
                    if (!Ext.isDefined(feature.attributes['rotation'])) {
                        feature.attributes.rotation = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.rotation;
                    }
                    return feature.attributes['rotation'];
                },
                // Ligne
                strokeColor : function(feature) {
                    if (!Ext.isDefined(feature.attributes['strokeColor'])) {
                        feature.attributes.strokeColor = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.strokeColor;
                    }
                    return feature.attributes['strokeColor'];
                },
                strokeOpacity : function(feature) {
                    if (!Ext.isDefined(feature.attributes['strokeOpacity'])) {
                        feature.attributes.strokeOpacity = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.strokeOpacity;
                    }
                    return feature.attributes['strokeOpacity'];
                },
                strokeWidth : function(feature) {
                    if (!Ext.isDefined(feature.attributes['strokeWidth'])) {
                        feature.attributes.strokeWidth = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.strokeWidth;
                    }
                    return feature.attributes['strokeWidth'];
                },
                strokeLinecap : function(feature) {
                    if (!Ext.isDefined(feature.attributes['strokeLinecap'])) {
                        feature.attributes.strokeLinecap = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.strokeLinecap;
                    }
                    return feature.attributes['strokeLinecap'];
                },
                strokeDashstyle : function(feature) {
                    if (!Ext.isDefined(feature.attributes['strokeDashstyle'])) {
                        feature.attributes.strokeDashstyle = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.strokeDashstyle;
                    }
                    return feature.attributes['strokeDashstyle'];
                },
                // Remplissage
                fillColor : function(feature) {
                    if (!Ext.isDefined(feature.attributes['fillColor'])) {
                        feature.attributes.fillColor = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.fillColor;
                    }
                    return feature.attributes['fillColor'];
                },
                fillOpacity : function(feature) {
                    if (!Ext.isDefined(feature.attributes['fillOpacity'])) {
                        feature.attributes.fillOpacity = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.fillOpacity;
                    }
                    if (feature.geometry instanceof OpenLayers.Geometry.Point && feature.attributes['externalGraphic'].length > 0) {
                        // Cas où une image est définie pour un point : pas de
                        // transparence
                        return 1;
                    }
                    return feature.attributes['fillOpacity'];
                },
                // Texte
                label : function(feature) {
                    if (!Ext.isDefined(feature.attributes['label'])) {
                        feature.attributes.label = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.label;
                    }
                    return feature.attributes['label'];
                },
                fontColor : function(feature) {
                    if (!Ext.isDefined(feature.attributes['fontColor'])) {
                        feature.attributes.fontColor = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.fontColor;
                    }
                    return feature.attributes['fontColor'];
                },
                fontSize : function(feature) {
                    if (!Ext.isDefined(feature.attributes['fontSize'])) {
                        feature.attributes.fontSize = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.fontSize;
                    }
                    return feature.attributes['fontSize'];
                }
            }
        });

        // Style de sélection : style par défaut hormis deux exceptions liées
        // aux images :
        // * le rayon qui conditionne la taille de l'image
        // * l'opacité qui doit être complète pour une image (transparence mal
        // gérée)
        var selectWithFillOpacityCfg = OpenLayers.Util.applyDefaults({
            pointRadius : '${pointRadius}',
            fillOpacity : '${fillOpacity}'
        }, OpenLayers.Feature.Vector.style['select']);
        var selectStyle = new OpenLayers.Style(selectWithFillOpacityCfg, {
            context : {
                pointRadius : function(feature) {
                    if (!Ext.isDefined(feature.attributes['pointRadius'])) {
                        feature.attributes.pointRadius = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.pointRadius;
                    }
                    return feature.attributes['pointRadius'];
                },
                fillOpacity : function(feature) {
                    if (!Ext.isDefined(feature.attributes['fillOpacity'])) {
                        feature.attributes.fillOpacity = Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.fillOpacity;
                    }
                    if (feature.geometry instanceof OpenLayers.Geometry.Point && feature.attributes['externalGraphic'].length > 0) {
                        // Cas où une image est définie pour un point : pas de
                        // transparence
                        return 1;
                    }
                    return feature.attributes['fillOpacity'];
                }
            }
        });

        return new OpenLayers.StyleMap({
            'default' : defaultStyle,
            'select' : selectStyle,
            // Ici : ajouter select et temporary si nécessaire
            'modifyHandles' : new OpenLayers.Style({
                // -- Point
                pointRadius : 5,
                graphicName : '',
                externalGraphic : '',
                rotation : 0,
                // -- Ligne
                strokeColor : 'red',
                strokeOpacity : 1,
                strokeWidth : 2,
                strokeLinecap : 'round',
                strokeDashstyle : 'solid',
                // -- Remplissage
                fillColor : 'red',
                fillOpacity : 0.8
            })
        });
    }
});