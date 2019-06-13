Ext.ns('Sdis.Remocra.widget.map.controls.FindFeaturesClick');
Ext.define('Sdis.Remocra.widget.map.controls.FindFeaturesClick', {});

Sdis.Remocra.widget.map.controls.FindFeaturesClick = OpenLayers.Class(OpenLayers.Control, {

    // Marge de recherche
    defaultMarginInPixels: 10,

    // Couches concernées par la sélection
    layers: null,

    initialize: function(layers, options) {
        options = options || {};
        options.handlerOptions = options.handlerOptions || {};

        OpenLayers.Control.prototype.initialize.apply(this, [options]);

        this.handler = new OpenLayers.Handler.Click(
            this, {"click": this.onClick}, this.handlerOptions.click || {});

        this.layers = layers;
    },

    onClick: function(evt) {
        var selectedFeatures = this.findAllLayersFeaturesXY(evt.xy);
        this.events.triggerEvent("found", {features: selectedFeatures});
    },

    findAllLayersFeaturesXY: function(xy) {
        var returned = [];
        for(i=0; i<this.layers.length; i++) {
            var features = this.findLayerFeaturesXY(this.layers[i], xy);
            var j;
            for (j=0 ; j<features.length ; j++) {
                var feature = features[j];
                if (feature) {
                    returned.push(feature);
                }
            }
        }
        return returned;
    },

    findLayerFeaturesXY: function(layer, xy) {
        var returned = [];
        if (!layer.features) {
            return returned;
        }
        var lonlat = this.map.getLonLatFromPixel(xy);
        var point = new OpenLayers.Geometry.Point(lonlat.lon, lonlat.lat);
        var i;
        for (i=0 ; i<layer.features.length ; i++) {
            var feature = layer.features[i];
            var marginInMapUnit = (layer.infoMarginInPixels||this.defaultMarginInPixels)*this.map.getResolution();
            if (point.intersects(feature.geometry)
                    || point.distanceTo(feature.geometry)<marginInMapUnit
                ) {
                returned.push(feature);
            }
        }
        return returned;
    },

    CLASS_NAME: "Sdis.Remocra.widget.map.controls.FindFeaturesClick"
});
