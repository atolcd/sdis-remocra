Ext.ns('Sdis.Remocra.widget.map');

Ext.define('Sdis.Remocra.widget.map.LegendTemplate', {
    extend: 'Ext.XTemplate',
    
    tts: null,
    
    constructor: function () {
        this.callParent([
            '{%this.setId(values.id);%}', // Uniquement pour pouvoir le récupérer à tout moment
            '<ul>',
                '<div class="legend-all" title="Afficher la légende"><span>&#x2807;</span></div>',
                '<div class="blocN1">Couches</div>',
                '<tpl for="legendData.items">', // L1 (groupes)
                    '<li class="blocN2" {[this.styImg(values)]}><span>{libelle}</span><ul>',
                        '<tpl for="items">', // L2 (couches)
                            '<li class="blocN3 {class}"><div class="layer">{[this.printImgL2(values)]}',
                                
                                '<div id="{[this.getId()]}{id}" class="layer-cfg {[this.getLayerVisibleClass(values)]}"><span>&radic;</span></div>',
                                '{libelle}',
                                
                                '</div><ul class="legend-hideifnotover">',// La classe est ajoutée pour masquer le groupe du dessous
                                '<tpl if="values.items && (values.items.length &gt; 1 || values.items[0].keepsize)">',
                                    '<tpl for="items">', // L3 (elts de légende)
                                            '<li class="blocN4">',
                                                '<img {[this.imgWidthHeight(values)]} src="{[this.printImg(values)]}" class="blocN5" onerror="this.style.visibility=\'hidden\';">',
                                                '<span>{libelle}</span>',
                                            '</li>',
                                    '</tpl>',
                                '</tpl>',
                            '</ul></li>',
                        '</tpl>',
                    '</ul></li>',
                '</tpl>',
            '</ul>',
            
            '<tpl for="legendData.items">', // L1 (groupes)
                '<tpl for="items">', // L2 (couches)
                    '{%this.installCfg(values);%}',
                '</tpl>',
            '</tpl>',
            
            '{%this.installLegendAll();%}' // Installation de l'écoute pour afficher toute la légende
        ]);
        this.tts= [];
    },
    
    setId: function(id) {
        this.id=id;
    },
    getId: function() {
        return this.id;
    },
    imgWidthHeight: function(values) {
        return values.keepsize?'':'height="10" width="15"';
    },
    getImg: function(values) {
        return (values.items && values.items.length==1 && values.items[0].image && values.items[0].keepsize!==true?
            values.items[0].image
            :values.image);
    },
    getImgUrl: function(image) {
        return image.indexOf('http')<0 && image.indexOf('/remocra/geoserver/')<0?'ext-res/images/remocra/cartes/legende/'+image:image;
    },
    styImg: function(values) {
        var image = this.getImg(values);
        if (!image) {
            return '';
        }
        var imageurl='ext-res/images/remocra/cartes/legende/'+image;
        return ' style="background-image: url(\''+this.getImgUrl(image)+'\');"';
    },
    printImg:function(values){
        var image = this.getImg(values);
        if (!image) {
            return '';
        }
        return this.getImgUrl(image);
    },
    printImgL2:function(values){
        var image = this.getImg(values);
        if (!image) {
            return ' <img height="10" width="15" style="visibility:hidden;"/>';
        }
        return ' <img height="10" width="15" src="'+this.getImgUrl(image)+'" onerror="this.style.visibility=\'hidden\';"/>';
    },
    getLayerVisibleClass: function(values) {
        var visible = typeof(values.visibility)=='boolean'?values.visibility:true;
        return visible?'layervisible':'';
    },
    
    installCfg: function(layer) {
        Ext.defer(function(tpl, layerCfgId, layer) {

            // Visibilité de la couche sur clic sur le carré de la légende
            var checkElt = Ext.get(this.getId()+layer.id);
            checkElt.on('click', function(evt, t, eOpts) {
                var layerByCode = this.mapCmp.getLayerByCode(this.layer.id);
                var newVisibility = !layerByCode.visibility;
                if (layerByCode.setVisibility) {
                    layerByCode.setVisibility(newVisibility);
                } else {
                    layerByCode.visibility = newVisibility;
                }
                this.checkElt.toggleCls('layervisible');
            }, {layer:layer, checkElt:checkElt, mapCmp:Ext.getCmp(this.getId())});

            var tt = Ext.create('Ext.tip.ToolTip', {
                target: layerCfgId,
                html: '<div style="margin-bottom:5px;"><b>'+layer.libelle+'</b></div><div class="azerty"/>',
                bodyStyle: 'background-color:#fff;',
                cls: 'legend-xtip',
                anchor: 'right',
                autoHide: false, 
                listeners: {
                    scope: this,
                    render: function(tooltip) {
                        var el = tooltip.getEl();
                        el.addListener('mouseleave', function(e, t, eOpts) {
                            tooltip.hide();
                        });
                        var node = Ext.dom.Query.selectNode('.azerty', el.dom);
                        var slider = null;
                        if (this.legendHideSliders!==true) {
                            slider = Ext.create('Ext.slider.Single', {
                                width: 200,
                                increment: 10,
                                minValue: 0,
                                maxValue: 100,
                                renderTo: node,
                                value: typeof(layer.opacity)=='number'?layer.opacity*100:100,
                                listeners: {
                                    scope: this,
                                    change: function(slider, newValue, thumb, eOpts) {
                                        var pct = newValue;
                                        this.setLayerOpacity(layer.id, pct/100.0);
                                    },
                                    drag: function(slider, e, eOpts) {
                                        var pct = slider.getValue();
                                        this.setLayerOpacity(layer.id, pct/100.0);
                                    }
                                }
                            });
                        }
                        tooltip.addListener('beforedestroy', function() {
                            if (this.slider) {
                                this.slider.destroy();
                            }
                        }, {slider:slider});
                    }
                }
            });
            // Dès que la carte est détruire, on détruit le tooltip
            this.addListener('destroy', function() {
                this.destroy();
            }, tt);
            tpl.tts.push(tpl);
        }, 100, Ext.getCmp(this.getId()), [this, this.getId()+layer.id, layer], true);
    },
    
    installLegendAll: function() {
        Ext.defer(function(tpl, layerCfgId, layer) {
            // Test gestion over pour toute la légende
            var legendNode = Ext.get(Ext.DomQuery.selectNode("div.maplegend", this.getEl().dom));
            if (legendNode) {
                var allHandlerNode = Ext.get(Ext.DomQuery.selectNode("div.legend-all", legendNode.dom));
                if (allHandlerNode) {
                    allHandlerNode.addListener('click', function(e, t, o) {
                        this.toggleCls('forceover');
                    }, legendNode);
                    allHandlerNode.addListener('mouseenter', function(e, t, o) {
                        this.addCls('over');
                    }, legendNode);
                    allHandlerNode.addListener('mouseleave', function(e, t, o) {
                        this.removeCls('over');
                    }, legendNode);
                }
            }
        }, 100, Ext.getCmp(this.getId()));
    }
});
