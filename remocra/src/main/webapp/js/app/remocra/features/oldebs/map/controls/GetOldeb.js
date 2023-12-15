Ext.ns('Sdis.Remocra.features.oldebs.map.controls.GetOldeb');

// Pour la gestion des dépendances
Ext.define('Sdis.Remocra.features.oldebs.map.controls.GetOldeb', {});

// Classe de récupération d'une obligation de débroussaillement et de sélection
// d'une oldeb
Sdis.Remocra.features.oldebs.map.controls.GetOldeb = OpenLayers.Class(OpenLayers.Control, {
    defaultHandlerOptions: {
        'single': true,
        'double': false,
        'pixelTolerance': 0,
        'stopSingle': false,
        'stopDouble': false
    },

    /**
     * layer = layer utilisée pour récupérer l'évènement de clic
     */
    initialize: function(options) {
        this.handlerOptions = OpenLayers.Util.extend({}, this.defaultHandlerOptions);
        OpenLayers.Control.prototype.initialize.apply(this, arguments);
        this.handler = new OpenLayers.Handler.Click(this, {
            'click': this.getOldebForClick
        }, this.handlerOptions);
    },

    /**
     * Récupération des oldebs via un serveur
     */
    getOldebForClick: function(e) {

        // /!\ this = MousePosition (on est dans un callback)
        var lonlat = this.getLonLatFromViewPortPx(e.xy);
        Ext.Ajax.request({
            url: 'oldeb/layer',
            method: 'GET',
            params: {
                point: lonlat.lon + ', ' + lonlat.lat,
                projection: this.map.getProjection()
            },
            callback: Ext.bind(this.manageResponse, this, [], true),
            scope: this
        });
    },

    manageResponse: function(opts, success, response) {
        if (!success) {
            Ext.Msg.alert('Erreur', 'Une erreur est survenue lors de la récupération de l\'obligation de débroussaillement.');
            return;
        }

        var features = new OpenLayers.Format.GeoJSON().read(response.responseText);
        if (features.length == 0) {
            Sdis.Remocra.util.Msg.msg('Obligation de débroussaillement', 'Aucune obligation de débroussaillement à cet emplacement.', 3);
            this.events.triggerEvent('oldeb_unselected', {
                features: []
            });
            return;
        }

        // Oldebs trouvées
        // Conversion des géometries dans le bon format
        for (i = 0; i < features.length; i++) {
            features[i].geometry.transform('EPSG:'+SRID, this.map.getProjection());
        }
        this.events.triggerEvent('oldeb_selected', {
            features: features
        });
    }

});
