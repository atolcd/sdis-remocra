Ext.ns('Sdis.Remocra.widget.map.controls.Measure');

// Pour la gestion des dépendances
Ext.define('Sdis.Remocra.widget.map.controls.Measure', {});

// Mesurer ; finalement, on laisse la géométrie affichée (persist: true) et l'outil actif (pas d'appel à deactivate dans handleMeasurementsEnd)
Sdis.Remocra.widget.map.controls.Measure = OpenLayers.Class(OpenLayers.Control.Measure, {

    displayClass: 'remocraControlMeasure',
    autoActivate: false,
    autoDeactivate: false,
    infoDiv: null,
    
    isArea: false,
    
    //type: OpenLayers.Control.TYPE_BUTTON,
    
    initialize: function(options) {
        var handler = options&&options.isArea===true?
                OpenLayers.Handler.Polygon : OpenLayers.Handler.Path;
        
        OpenLayers.Control.Measure.prototype.initialize.apply(this, [handler, {
            persist: true,
            geodesic: true,
            handlerOptions: {
                layerOptions: {
                    renderers: OpenLayers.Layer.Vector.prototype.renderers,
                    styleMap: this.getStyleMap()
                }
            },
            displayClass: 'remocraControlMeasure',
            title: 'Mesurer une distance',
            infoDiv: options.infoDiv,
            isArea: options.isArea,
            id: options.id,
            autoDeactivate: options.autoDeactivate
        }]);
        
        this.events.on({
            "measure": this.handleMeasurementsEnd,
            "measurepartial": this.handleMeasurements
        });
    },

    getStyleMap: function(event) {
        return new OpenLayers.Style({
            pointRadius: 4,
            graphicName: "square",
            fillColor: "white", fillOpacity : 0.25,
            strokeWidth: 1,
            strokeColor: "#fb1613"
        });
    },
    
    handleMeasurements: function(event) {
        // Si le conteneur est en cours de masquage, on annule (timer porté par infoDivElt)
        var infoDivElt = Ext.Element.get(this.infoDiv);
        if (infoDivElt.timerId) {
            clearInterval(infoDivElt.timerId);
        }
        // On l'affiche à nouveau au cas où
        infoDivElt.fadeIn();
        // On affiche la mesure
        this.infoDiv.innerHTML = this.getMesureInfo(event);
    },
    
    getMesureInfo: function(event) {
        return this.isArea ? this.getMesureInfoSurface(event)
            : this.getMesureInfoDistance(event);
    },
    getMesureInfoDistance: function(event) {
        var distance;
        if(event.units == 'm') {
            distance = Math.round(event.measure)+" m";
        } else {
            distance = event.measure.toFixed(3) + " km";
        }
        return "Distance mesurée : "+distance+".";
    },
    getMesureInfoSurface: function(event) {
        var surface;
        if (event.units == 'm') {
            if(event.measure < 10000) {
                surface = Math.round(event.measure)+" m²";
            } else {
                surface = Math.round(event.measure/100)/100 + " ha";
            }
        } else {
            // km
            if(event.measure < 10) {
                surface = (event.measure).toFixed(3)+" km²";
            } else {
                surface = (event.measure*100).toFixed(3) + " ha";
            }
        }
        return "Surface mesurée : "+surface+".";
    },
    
    handleMeasurementsEnd: function(event) {
        this.handleMeasurements(event);
        // On masque la mesure au bout de 5 secondes
        var infoDivElt = Ext.Element.get(this.infoDiv);
        infoDivElt.timerId=setInterval(function() {infoDivElt.fadeOut();}, 5000);
        if (this.autoDeactivate) {
            this.deactivate();
        }
    },
    
    draw: function(px) {
        OpenLayers.Control.Measure.prototype.draw.apply(this, arguments);
        OpenLayers.Event.observe(this.div, "click",
            OpenLayers.Function.bindAsEventListener(this.activate, this));
        return this.div;
    },

    destroy: function() {
        OpenLayers.Event.stopObservingElement(this.div);
        OpenLayers.Control.Measure.prototype.destroy.apply(this, arguments);
    },
    
    CLASS_NAME: "Sdis.Remocra.widget.map.controls.Measure"
});
