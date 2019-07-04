Ext.ns('Sdis.Remocra.widget.map.controls.Measure');
Ext.define('Sdis.Remocra.widget.map.controls.Measure', {});

Sdis.Remocra.widget.map.controls.Measure = OpenLayers.Class(OpenLayers.Control.Measure, {

    displayClass: 'remocraControlMeasure',
    autoActivate: false,
    autoDeactivate: false,
    
    isArea: false,

    //type: OpenLayers.Control.TYPE_BUTTON,
    
    initialize: function(options) {
        var handler = options&&options.isArea===true?
                OpenLayers.Handler.Polygon : OpenLayers.Handler.Path;
        
        OpenLayers.Control.Measure.prototype.initialize.apply(this, [handler, {
            persist: true,
            geodesic: true,
            immediate: true,
            handlerOptions: {
                layerOptions: {
                    renderers: OpenLayers.Layer.Vector.prototype.renderers,
                    styleMap: this.getStyleMap()
                }
            },
            displayClass: 'remocraControlMeasure',
            title: 'Mesurer une distance',
            isArea: options.isArea,
            id: options.id,
            autoDeactivate: options.autoDeactivate
        }]);
        
        this.events.on({
            "measure": this.handleMeasurementsEnd,
            "measurepartial": this.handleMeasurements,
            "activate": this.onActivate,
            "deactivate": this.onDeactivate
        });

        this.handler.destroyPersistedFeature = function(){};
    },

    onActivate: function() {
        // Création de la popup si nécessaire
        this.createNewPopup();

    },

    createNewPopup: function(){
            var popup = new OpenLayers.Popup("tooltip-measure-"+(this.isArea?'polygon':'path'), null, new OpenLayers.Size(80, 20), "xxxxxxx km", false);
            this.map.addPopup(popup);
            // Visuel de la popup
            popup.div.style.display = 'flex';
            popup.div.style.textAlign = 'center';
            popup.div.style.height = '38px';

            popup.div.style.backgroundColor = '#ff4e4e';
            popup.div.style.border = 'solid 1px #b83838';
            popup.div.style.borderRadius = '4px';
            popup.div.style.color = '#000000';
            popup.div.style.fontStyle = 'italic';
            popup.div.style.textShadow = '0px 0px 2px #ffffff, 0px 0px 2px #ffffff, 0px 0px 2px #ffffff, 0px 0px 2px #ffffff, 0px 0px 2px #ffffff';

            popup.groupDiv.style.margin = 'auto';
            popup.groupDiv.style.height = popup.div.style.height;
            popup.groupDiv.style.display = 'flex';
            popup.contentDiv.style.margin = 'auto';
            popup.contentDiv.style.overflow = 'hidden';

            popup.hide();
    },

    onDeactivate: function() {
        while(this.map.popups.length ) {
            this.map.removePopup(this.map.popups[0]);
        }
    },

    getStyleMap: function(event) {
        var sketchSymbolizers = {
            "Point": {
                pointRadius: 4,
                graphicName: "circle",
                fillColor: "white",
                fillOpacity: 1,
                strokeWidth: 1,
                strokeOpacity: 1,
                strokeColor: "#ff4e4e"
            },
            "Line": {
                strokeWidth: 1,
                strokeOpacity: 1,
                strokeColor: "#ff4e4e"
            },
            "Polygon": {
                strokeWidth: 1,
                strokeOpacity: 1,
                strokeColor: "#ff4e4e",
                fillColor: "#ff4e4e",
                fillOpacity: 0.6
            }
        };
        var style = new OpenLayers.Style();
        style.addRules([
            new OpenLayers.Rule({
                symbolizer: sketchSymbolizers
            })
        ]);
        return new OpenLayers.StyleMap({"default": style});
    },
    
    handleMeasurements: function(event) {
        var popup = this.map.popups[this.map.popups.length-1];
        popup.setContentHTML(this.getMesureInfo(event));
        popup.updateSize();

        if (this.handler.evt.xy) {
            // Récupération de la position du curseur
            var posPopup = {
                'x': this.handler.evt.xy.x - popup.size.w/2,
                'y': this.handler.evt.xy.y - 45
            };
            popup.show();
            popup.lonlat = this.map.getLonLatFromViewPortPx(posPopup);
            popup.updatePosition();
        }
    },
    
    getMesureInfo: function(event) {
        return this.isArea ? this.getMesureInfoSurface(event)
            : this.getMesureInfoDistance(event);
    },
    getMesureInfoDistance: function(event) {
        if(event.units == 'm') {
            return Math.round(event.measure)+"&nbsp;m";
        }
        return event.measure.toFixed(3) + "&nbsp;km";
    },
    getMesureInfoSurface: function(event) {
        if (event.units == 'm') {
            if(event.measure < 10000) {
                return Math.round(event.measure)+"&nbsp;m²";
            }
            return Math.round(event.measure/100)/100 + "&nbsp;ha";
        } else {
            // km
            if(event.measure < 10) {
                return (event.measure).toFixed(3)+"&nbsp;km²";
            }
            return (event.measure*100).toFixed(3) + "&nbsp;ha";
        }
    },
    
    handleMeasurementsEnd: function(event) {
        this.createNewPopup();
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
