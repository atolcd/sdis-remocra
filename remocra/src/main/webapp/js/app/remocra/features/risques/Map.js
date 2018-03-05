Ext.ns('Sdis.Remocra.features.risques');

Ext.define('Sdis.Remocra.features.risques.Map', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crRisquesMap',
    
    // Items qui doivent figurer juste avant le bouton d'affichage de la barre d'édition
    moreItems: [
        { tooltip: 'Purger la couche des risques express', text: '<span>Purger</span>',
            cls: 'purger-risqueexpress', iconCls: 'purger-risqueexpressIcon',
            itemId: 'purgerRisqueexpress'
        }
    ],
    
    legendUrl: BASE_URL+'/../ext-res/js/app/remocra/features/risques/data/carte.json',
            
    initComponent: function() {
        Ext.apply(this, {
            listeners: {
                afterrender: function() {
                    var purgerRisqueexpress = this.maptbar1.getComponent('purgerRisqueexpress');
                    if (Sdis.Remocra.Rights.hasRight('RISQUES_KML_C')) {
                        purgerRisqueexpress.addListener('click', this.purgerRisqueExpress, this);
                    } else {
                        purgerRisqueexpress.hide();
                    }
                }
            }
        });
        this.callParent(arguments);
    },

    purgerRisqueExpress: function(btn, e, eOpts) {
        Ext.Msg.confirm('Risques', 'Confirmez-vous la purge des risques express ?', function(btn) {
            if (btn == "yes") {
                Ext.Ajax.request({
                    url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/purgekml"),
                    method: 'GET',
                    scope: this,
                    callback: function(options, success, response) {
                        if (success == true) {
                            Sdis.Remocra.util.Msg.msg('Purge de la couche des risques express',
                                'Votre demande a été prise en compte. Vous serez averti par un message électronique lorsque la couche sera purgée.', 5);
                        } else {
                            var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                            Ext.Msg.alert('Purge de la couche des risques express',
                                'Un problème est survenu lors de l\'enregistrement de la demande.' + msg + '.');
                        }
                    }
                });
            }
        }, this);
    },
        
    createSpecificLayer: function(layerDef) {
        // Couche KML
        if (layerDef.id == 'risquesExpressLayer') {
            if (!Sdis.Remocra.Rights.hasRight('RISQUES_KML_R')) {
                return null;
            }
            
            // Couche des risques express
            var risquesExpressLayer = new OpenLayers.Layer.Vector(layerDef.libelle, {
                code: layerDef.id,
                visibility: layerDef.visibility,
                opacity: layerDef.opacity,
                
                projection: 'epsg:4326', // TODO voir si variable (a priori, non car provient toujours du même logiciel)
                
                strategies: [new OpenLayers.Strategy.Fixed()],
                protocol: new OpenLayers.Protocol.HTTP({
                    //url: BASE_URL+'/../resources/js/app/remocra/features/risques/data/risques.kml',
                    url: Sdis.Remocra.util.Util.withBaseUrl("../risques/express"),
                    format: new OpenLayers.Format.KML({
                        extractStyles: true,
                        extractAttributes: true
                    })
                }),
                eventListeners : {
                    "featuresadded" : function() {
                        // Par défaut, zoom sur l'emprise de la couche risques express
                        this.map.zoomToExtent(this.getDataExtent());
                    }
                },
                renderers: OpenLayers.Layer.Vector.prototype.renderers,
                interrogeable: layerDef.interrogeable,
                infoMarginInPixels: layerDef.infoMarginInPixels
            });
            risquesExpressLayer.events.on({
                'loadend': this.zoomToBestExtent,
                scope: this
            });
            return risquesExpressLayer;
        }
        return this.callParent(arguments);
    },
    
    zoomToBestExtent: function() {
        var layer = this.getLayerByCode('risquesExpressLayer');
        if (layer && layer.features.length>0) {
            this.map.zoomToExtent(layer.getDataExtent());
        } else {
            this.callParent(arguments);
        }
    }
});