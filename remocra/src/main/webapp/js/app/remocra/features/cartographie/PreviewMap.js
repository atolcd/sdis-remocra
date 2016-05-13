Ext.require('Ext.container.Container');
Ext.require('Sdis.Remocra.features.cartographie.StyleHolder');

Ext.define('Sdis.Remocra.features.cartographie.PreviewMap', {
    extend : 'Ext.container.Container',
    alias : 'widget.crCartographiepreviewMap',

    html : '<div class="map" style="width:150px;height:250px;background-color:#f0f0f0;"/>',
    padding : 3,

    initComponent : function() {
        this.on('afterrender', this.renderMap);
        this.callParent(arguments);
    },

    renderMap : function() {
        var mapNode = Ext.DomQuery.selectNode("div.map", this.getEl().dom);

        // Création de la carte et de la couche de prévisu
        this.map = new OpenLayers.Map(mapNode, {
            controls : [],
            allOverlays : true,
            projection : 'EPSG:2154',
            maxExtent : new OpenLayers.Bounds(-25, -20, 30, 20),
            theme : false // Evite style.css quand module serveur PageSpeed
        });
        var vectorLayer = new OpenLayers.Layer.Vector("Preview", {
            styleMap : this.getStyleMap()
        });

        // Alimentation des géométries de prévisualisation
        var pointFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(-4, 8));
        var lineFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString([ new OpenLayers.Geometry.Point(-9, -12),
                new OpenLayers.Geometry.Point(-4, -15), new OpenLayers.Geometry.Point(1, -10) ]));
        var polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([ new OpenLayers.Geometry.LinearRing([
                new OpenLayers.Geometry.Point(5, 0), new OpenLayers.Geometry.Point(10, -3), new OpenLayers.Geometry.Point(15, 6),
                new OpenLayers.Geometry.Point(9, 5), new OpenLayers.Geometry.Point(5, 0) ]) ]));
        vectorLayer.addFeatures([ pointFeature, lineFeature, polygonFeature ]);

        // Ajout de la couche vecteurs et zoom sur l'étendue max
        this.map.addLayer(vectorLayer);
        this.map.zoomToMaxExtent();
    },

    getStyleMap : function() {
        return Sdis.Remocra.features.cartographie.StyleHolder.workingLayerStyleMap();
    },

    setProps : function(props) {
        if (!props) {
            return;
        }
        Ext.Array.each(this.map.layers[0].features, function(feature, index, recs) {
            Ext.apply(feature.attributes, props);
            feature.layer.drawFeature(feature);
        });
    }
});