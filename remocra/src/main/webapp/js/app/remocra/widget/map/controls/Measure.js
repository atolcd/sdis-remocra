Ext.ns('Sdis.Remocra.widget.map.controls.Measure');
Ext.define('Sdis.Remocra.widget.map.controls.Measure', {});

Sdis.Remocra.widget.map.controls.Measure = OpenLayers.Class(OpenLayers.Control.Measure, {

    displayClass: 'remocraControlMeasure',
    autoActivate: false,
    autoDeactivate: false,
    popup: null,
    
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
    },

    onActivate: function() {
        // Création de la popup si nécessaire
        if (!this.popup) {
            this.popup = new OpenLayers.Popup("tooltip-measure-"+(this.isArea?'polygon':'path'), null, new OpenLayers.Size(80, 20), "xxxxxxx km", false);
            this.map.addPopup(this.popup);

            // Visuel de la popup
            this.popup.div.style.opacity = '0.75';
            this.popup.div.style.display = 'flex';
            this.popup.div.style.textAlign = 'center';

            this.popup.div.style.backgroundColor = '#fff';
            this.popup.div.style.border = 'solid 1px #00b3b3';
            this.popup.div.style.borderRadius = '4px';
            this.popup.div.style.color = '#00b3b3';
            this.popup.div.style.fontStyle = 'italic';

            this.popup.groupDiv.style.margin = 'auto';
            this.popup.groupDiv.style.height = this.popup.div.style.height;
            this.popup.groupDiv.style.display = 'flex';
            this.popup.contentDiv.style.margin = 'auto';
            this.popup.contentDiv.style.overflow = 'hidden';

            //this.popup.fixPadding();
            this.popup.hide();
        }
    },

    onDeactivate: function() {
        this.popup.hide();
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
                strokeColor: "#00b3b3"
            },
            "Line": {
                strokeWidth: 1,
                strokeOpacity: 1,
                strokeColor: "#00b3b3"
            },
            "Polygon": {
                strokeWidth: 1,
                strokeOpacity: 1,
                strokeColor: "#00b3b3",
                fillColor: "white",
                fillOpacity: 0.3
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
        this.popup.setContentHTML(this.getMesureInfo(event));
        this.popup.updateSize();

        if (this.handler.evt.xy) {
            // Récupération de la position du curseur
            var posPopup = {
                'x': this.handler.evt.xy.x - this.popup.size.w/2,
                'y': this.handler.evt.xy.y - 45
            };
            this.popup.show();
            this.popup.lonlat = this.map.getLonLatFromViewPortPx(posPopup);
            this.popup.updatePosition();
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
