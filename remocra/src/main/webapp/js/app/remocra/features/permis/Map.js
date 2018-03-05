Ext.require('Sdis.Remocra.model.Commune');
Ext.require('Sdis.Remocra.widget.map.Map');
Ext.require('Sdis.Remocra.features.permis.FichePermis');
Ext.require('Sdis.Remocra.features.permis.Recherche');
Ext.require('Sdis.Remocra.features.permis.PermisChoixWindow');

Ext.define('Sdis.Remocra.features.permis.Map', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crPermisMap',

    moreItems: [
        { tooltip: 'Rechercher un permis', text: '<span>Rechercher un permis</span>',
            cls: 'search-permis', iconCls: 'search-permisIcon',
            enableToggle: true, pressed: false, 
            itemId: 'searchPermis'
        }
    ],
    
    editItems: [
        'Sélectionner : ',
        {
            xtype: 'button', tooltip: 'Sélectionner un permis', text : '<span>Sélectionner</span>',
            cls: 'select-point', iconCls: 'select-pointIcon',
            toggleGroup: 'excl-ctrl1', itemId: 'selectPointBtn'
        },
        ' ',
        { xtype: 'tbtext', text: 'Créer', itemId: 'creerLbl' },
        { tooltip: 'Créer un permis', text: '<span>Créer un permis</span>',
            toggleGroup: 'excl-ctrl1',
            cls: 'draw-point', iconCls: 'draw-pointIcon', itemId: 'drawPointBtn' },
        ' ',
        { xtype: 'tbtext', text: 'Modifier', itemId: 'modifierLbl' },
        { tooltip: 'Déplacer le permis', text: '<span>Déplacer</span>',
            toggleGroup: 'excl-ctrl1',
            cls: 'edit-geom', iconCls: 'edit-geomIcon', itemId: 'editGeomBtn' },
        { tooltip: 'Supprimer le permis', text: '<span>Supprimer</span>',
            toggleGroup: 'excl-ctrl1',
            cls: 'delete', iconCls: 'deleteIcon', itemId: 'deleteBtn' }
    ],
    
    mapTpl: Ext.create('Ext.XTemplate',
        '<div class="maptbar maptbar1"><!-- --></div>',
        '<div class="maptbar maptbar2"><!-- --></div>',
        '<div class="map"><!-- --></div>',
        '<div class="maplegend"><!-- --></div>',
        '<div class="permis"><!-- --></div>',
        '<div class="recherche"><!-- --></div>',
        '<div class="mapinfo"><!-- --></div>'
    ),
        
    legendUrl: BASE_URL+'/../ext-res/js/app/remocra/features/permis/data/carte.json',

    initComponent: function() {
        Ext.apply(this, {
            listeners: {
                afterrender: function() {
                    var searchPermis = this.maptbar1.getComponent('searchPermis');
                    searchPermis.addListener('click', this.searchPermis, this);
                    
                    this.maptbar2.getComponent('selectPointBtn').addListener('toggle', function(button, pressed) {
                        this.activateSpecificControl('selectPermis', pressed);
                    }, this);
                    
                    var drawPointBtn = this.maptbar2.getComponent('drawPointBtn');
                    var editGeomBtn = this.maptbar2.getComponent('editGeomBtn');
                    var deleteBtn = this.maptbar2.getComponent('deleteBtn');
                    var creerLbl = this.maptbar2.getComponent('creerLbl');
                    var modifierLbl = this.maptbar2.getComponent('modifierLbl');
                    
                    if (Sdis.Remocra.Rights.hasRight('PERMIS_C')) {
                        drawPointBtn.addListener('toggle', function(button, pressed) {
                            this.activateSpecificControl('drawPermis', pressed);
                        }, this);
                        editGeomBtn.addListener('toggle', function(button, pressed) {
                            this.activateSpecificControl('modifyPermis', pressed);
                        }, this);
                        deleteBtn.addListener('click', Ext.bind(this.activateSpecificControl, this, ['removePermis']));
                    } else {
                        drawPointBtn.hide();
                        editGeomBtn.hide();
                        deleteBtn.hide();
                        creerLbl.hide();
                        modifierLbl.hide();
                    }
                    
                    var permisNode = Ext.DomQuery.selectNode("div.permis", this.getEl().dom);
                    this.permisPanel = Ext.create('Sdis.Remocra.features.permis.FichePermis', {
                        itemId: 'permisPanel',
                        renderTo: permisNode, hidden: true
                    });
                    
                    var mapProjection = this.map.getProjection();
                    this.permisPanel.addListener('ok', Ext.bind(function() {
                        var feature = this.workingLayer.features[0];
                        var permis = feature.attributes['model'];
                        if (!permis) {
                            permis = Ext.create('Sdis.Remocra.model.Permis');
                        }
                        var wktFormat = new OpenLayers.Format.WKT( {
                            internalProjection: this.map.getProjection(),
                            externalProjection: new OpenLayers.Projection('EPSG:2154')
                        });
                        var str = wktFormat.write(feature);
                        permis.set('geometrie', str);
                        
                        this.permisPanel.savePermis(permis, mapProjection);
                    }, this));
                    
                    this.permisPanel.addListener('cancel', Ext.bind(function() {
                        this.workingLayer.removeAllFeatures();
                        this.switchToMode();
                    }, this));
                    
                    this.permisPanel.addListener('permisCreated', function(rawPermis, newPermis) {
                        Sdis.Remocra.util.Msg.msg('Permis', 'Le permis a bien été '+(newPermis?'créé.':'mis à jour.'), 3);
                        this.loadPermisConcrete(rawPermis);
                        this.switchToMode('permis');
                    }, this);
                    
                    var rechercheNode = Ext.DomQuery.selectNode("div.recherche", this.getEl().dom);
                    this.recherchePanel = Ext.create('Sdis.Remocra.features.permis.Recherche', {
                        itemId: 'recherchePanel',
                        renderTo: rechercheNode, hidden: true
                    });
                    
                    // Chargement d'un permis
                    this.recherchePanel.addListener('loadPermis', function(rawPermis) {
                        this.loadPermisConcrete(rawPermis);
                        this.switchToMode('permis');
                    }, this);
                    
                    // Centrage sur une position
                    this.recherchePanel.addListener('centerToLonLat', function(lonlat, proj) {
                        if (proj) {
                            lonlat = lonlat.transform(proj, this.map.getProjection());
                        }
                        this.map.setCenter(lonlat, this.map.baseLayer.resolutions.length-1, true, true);
                    }, this);
                    
                    this.workingLayer.events.on({
                        'featureadded': function() {
                            this.switchToMode();
                        },
                        scope: this
                    });
                }
            }
        });
        this.callParent(arguments);
    },
    
    manageActivationsToggles: function() {
        this.callParent(arguments);
        
        this.manageActivationsTogglesBtnCtrl(this.maptbar2.getComponent('selectPointBtn'), 'selectPermis');
        this.manageActivationsTogglesBtnCtrl(this.maptbar2.getComponent('drawPointBtn'), 'drawPermis');
        this.manageActivationsTogglesBtnCtrl(this.maptbar2.getComponent('editGeomBtn'), 'modifyPermis');
        this.manageActivationsTogglesBtnCtrl(this.maptbar2.getComponent('deleteBtn'), 'removePermis');
    },

    destroy: function() {
        this.permisPanel.destroy();
        this.recherchePanel.destroy();
        this.callParent(arguments);
    },
    
    getCurrentMode: function(key) {
        var permis = this.maptbar2.isVisible();
        if (permis) {
            return 'permis';
        }
        var recherche = this.recherchePanel.isVisible();
        if (recherche) {
            return 'recherche';
        } 
        return 'legend';
    },

    switchToMode: function(key) {
        var rechercheBtn = this.maptbar1.getComponent('searchPermis');
        var editBtn = this.maptbar1.getComponent('edit');
        var maplegendNode = Ext.DomQuery.selectNode("div.maplegend", this.getEl().dom);
        
        var simpleRefresh = key==undefined;
        // Simple refresh (on reste dans le même mode)
        if (simpleRefresh) {
            key = this.getCurrentMode();
        }
        var permisMode = key=='permis';
        var rechercheMode = key=='recherche';
        var legendMode = !permisMode && !rechercheMode;
        
        // Panneau de droite :
        Ext.get(maplegendNode).setVisible(legendMode || (permisMode && this.workingLayer.features.length<1));
        this.permisPanel.setVisible(permisMode && this.workingLayer.features.length>0);
        this.recherchePanel.setVisible(rechercheMode);        
        
        // Gauche :
        editBtn.toggle(permisMode);
        this.maptbar2.setVisible(permisMode);
        rechercheBtn.toggle(rechercheMode);
        
        if (!simpleRefresh && !permisMode) {
            // Désactive les contrôles d'édition
            this.activateSpecificControl('aucun');
        }
    },
    edit: function() {
        this.switchToMode(this.getCurrentMode()=='permis'?'legend':'permis');
    },
    
    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            // Charger
            // Clic -> requête, choix de la feature si plusieurs, sélection de
            // la feature
            selectPermis: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point, {
                activate: Ext.bind(function() {
                    // On vide la couche
                    OpenLayers.Control.prototype.activate.apply(this.getSpecificControl('selectPermis'), arguments);
                    this.workingLayer.removeAllFeatures();
                    this.switchToMode();
                }, this),
                featureAdded: Ext.bind(this.loadPermis, this)
            }),
            
            // Dessiner
            drawPermis: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point, {
                activate: Ext.bind(function() {
                    // On vide la couche
                    OpenLayers.Control.prototype.activate.apply(this.getSpecificControl('drawPermis'), arguments);
                    this.workingLayer.removeAllFeatures();
                    this.switchToMode();
                    this.permisPanel.reset();
                }, this),
                featureAdded: Ext.bind(this.createPermis, this)
            }),
            
            // Modifier geom
            modifyPermis: new OpenLayers.Control.ModifyFeature(this.workingLayer, {
                activate: Ext.bind(function() {
                    var ctrl = this.getSpecificControl('modifyPermis');
                    OpenLayers.Control.prototype.activate.apply(ctrl, arguments);
                    var feature = this.workingLayer.features[0];
                    if (feature) {
                        ctrl.selectFeature(feature);
                    } else {
                        Sdis.Remocra.util.Msg.msg('Permis', 'Veuillez sélectionner un permis au préalable.', 3);
                        ctrl.deactivate();
                    }
                }, this)
            }),
            
            // Supprimer
            removePermis: new OpenLayers.Control.SelectFeature(this.workingLayer, {
                clickout: false, toggle: false, multiple: false, hover: false, box: false,
                // onSelect: Ext.bind(this.removePermis, this),
                activate: Ext.bind(function() {
                    var ctrl = this.getSpecificControl('removePermis');
                    OpenLayers.Control.prototype.activate.apply(ctrl, arguments);
                    var feature = this.workingLayer.features[0];
                    if (feature) {
                        if (feature.attributes['model'] && feature.attributes['model'].get('id')!=null) {
                            // Suppression réelle
                            Ext.Msg.confirm('Permis', 'Confirmez-vous la suppression du permis ?', function(btn) {
                                if (btn == "yes") {
                                    Ext.Ajax.request({
                                        url: Sdis.Remocra.util.Util.withBaseUrl("../permis/" + this.permisId),
                                        method: 'DELETE',
                                        scope: this,
                                        callback: function(options, success, response) {
                                            if (success!==true) {
                                                Ext.Msg.alert('Permis', 'Un problème est survenu lors de la suppression du permis.');
                                                return;
                                            }
                                            this.map.workingLayer.removeAllFeatures({silent: true});
                                            // refresh de la couche wms Redmine #3399
                                            this.map.map.getLayersByName('permisLayer')[0].redraw(true);
                                            this.map.switchToMode();
                                        }
                                    });
                                }
                            }, {map:this, permisId:feature.attributes['model'].get('id')});
                            
                        } else {
                            this.workingLayer.removeAllFeatures({silent: true});
                            this.switchToMode();
                        }
                    } else {
                        Sdis.Remocra.util.Msg.msg('Permis', 'Veuillez Sélectionner un permis au préalable.', 3);
                    }
                    ctrl.deactivate();
                }, this)
            })
        });
        return ctrls;
    },
    
    searchPermis: function(btn, e, eOpts) {
        this.switchToMode(this.getCurrentMode()=='recherche'?'legend':'recherche');
    },
    
    loadPermis: function(feature) {
        var x=feature.geometry.x;
        var y=feature.geometry.y;
        var srid=this.map.baseLayer.projection.projCode.split(':')[1];
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../permis/searchxy"),
            method: 'GET',
            params: {x:x, y:y, srid:srid},
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    var decoded = Ext.decode(response.responseText);
                    if (decoded.total<1) {
                        Sdis.Remocra.util.Msg.msg('Permis', 'Aucun permis n\'a été trouvé.', 3);
                        this.workingLayer.removeAllFeatures();
                        this.switchToMode();
                        return;
                    }
                    
                    if (decoded.total<2) {
                        this.loadPermisConcrete(decoded.data);
                    } else {
                        // Au moins deux permis : on fait choisir
                        var choiceWin = Ext.create('Sdis.Remocra.features.permis.PermisChoixWindow', {
                            data: decoded.data,
                            listeners: {
                                scope: this,
                                'ok': function(win, permis) {
                                    win.close();
                                    this.loadPermisConcrete(permis.raw);
                                },
                                'cancel': function(win) {
                                    this.workingLayer.removeAllFeatures();
                                    this.switchToMode();
                                }
                            }
                        });
                        choiceWin.addListener('cancel', function() {this.close();}, choiceWin);
                        choiceWin.show();
                    }
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Permis',
                        'Un problème est survenu lors du chargement d\'un permis.' + msg + '.');
                }
            }
        });
    },
    loadPermisConcrete: function(rawPermis) {
        var resultset = Sdis.Remocra.model.Permis.proxy.reader.readRecords(rawPermis);
        var permisModel = resultset.records[0];
        
        var feature = new OpenLayers.Format.WKT({
            internalProjection: this.map.getProjection(),
            externalProjection: new OpenLayers.Projection('EPSG:2154')
        }).read(permisModel.get('geometrie'));
        feature.attributes['model'] = permisModel;
        
        this.workingLayer.removeAllFeatures();
        this.workingLayer.addFeatures(feature);
        this.switchToMode();
        
        // Filtre par point (liste des voies)
        this.setXYFilter(feature);
        
        // Chargement des données du permis
        this.permisPanel.fillData(permisModel);
        
        // Zoom sur le permis
        var geom = permisModel.get('geometrie');
        var lonlat = new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y);
        this.map.setCenter(lonlat, this.map.baseLayer.resolutions.length-1, true, true);
        // Refresh couche de travail (permis), puis couche de l'outil
        this.workingLayer.redraw(true);
        var selectPermisCtrl = this.getSpecificControl('selectPermis');
        if (selectPermisCtrl.active) {
            selectPermisCtrl.handler.layer.redraw(true);
        }
    },
    
    createPermis: function() {
        // Désactivation du contrôle
        this.activateSpecificControl('drawPermis', false);
        
        if (this.getCurrentMode() == 'permis') {
            var feature = this.workingLayer.features[0];
            var x = feature.geometry.x;
            var y = feature.geometry.y;
            
            var mapSrid = this.getCurrentSrid();
            
            Sdis.Remocra.model.ZoneCompetence.checkByXY(x,y,mapSrid, {
                scope: this,
                success: function() {
                    // Refresh pour ouvrir la fiche
                    this.switchToMode();                    
                    // Filtre par point (liste des voies)
                    this.setXYFilter(feature);
                    // Recherche spatiale de la commune pour appliquer un filtre
                    // par commune (liste des voies)
                    Sdis.Remocra.model.Commune.loadByXY(x, y, mapSrid, {
                        callback: function(commune, operation) {
                            if (operation.success!==true || commune==null) {
                                Sdis.Remocra.util.Msg.msg('Permis', 'La commune n\'a pas été trouvée.', 3);
                                return;
                            }
                            this.permisPanel.setCommune(commune);
                        }, scope: this
                    });
                },
                failure: function() {
                    Sdis.Remocra.util.Msg.msg('Permis', 'Vous ne possédez pas les autorisations nécessaires pour créer un permis sur cette zone', 3);
                }
            });
            

            
        }
    },
    
    setXYFilter: function(feature) {
        var wktFormat = new OpenLayers.Format.WKT({
            internalProjection: this.map.getProjection(),
            externalProjection: new OpenLayers.Projection('EPSG:2154')
        });
        this.permisPanel.setXYFilter(wktFormat.write(feature));
    }
    
});