Ext.ns('Sdis.Remocra.features.adresses');

Ext.require('Sdis.Remocra.widget.map.Map');
Ext.require('Sdis.Remocra.features.adresses.CreateAlerte');
Ext.require('Sdis.Remocra.model.AlerteElt');
Ext.require('Sdis.Remocra.features.adresses.AlerteEltWindow');
        
Ext.define('Sdis.Remocra.features.adresses.Map', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crAdressesMap',

    alertePointRadius: 10,
    
    // Items qui doivent figurer juste avant le bouton d'affichage de la barre d'édition
    moreItems: [
        {
            tooltip: 'Télécharger nombre d\'alertes par utilisateur', text: '<span>Télécharger nombre d\'alertes par utilisateur</span>',
            cls: 'download-atlas', iconCls: 'download-atlasIcon',
            itemId: 'downloadNbAlertesParUtil'
        }
    ],

    // Items de la barre d'édition
    editItems: [
        'Dessiner : ',
        {
            xtype: 'button', tooltip: 'Dessiner un élément', text : '<span>Dessiner</span>', cls: 'dessiner', iconCls: 'dessinerIcon',
            enableToggle: true, pressed: false, toggleGroup: 'excl-ctrl1', allowDepress: true,
            itemId: 'dessinerBtn'
            // Un menu est ajouté par la suite, dont le contenu dépend d'une requête Ajax
        },
        ' ',
        'Modifier : ',
        { tooltip: 'Modifier la géométrie d\'un élément', text: '<span>Géométrie</span>',
            toggleGroup: 'excl-ctrl1',
            cls: 'edit-geom', iconCls: 'edit-geomIcon', itemId: 'editGeomBtn' },
        { tooltip: 'Modifier les attributs d\'un élément', text: '<span>Informations</span>',
            toggleGroup: 'excl-ctrl1',
            cls: 'edit-info', iconCls: 'edit-infoIcon', itemId: 'editInfoBtn' },
        { tooltip: 'Supprimer un élément', text: '<span>Supprimer</span>',
            toggleGroup: 'excl-ctrl1',
            cls: 'delete', iconCls: 'deleteIcon', itemId: 'deleteBtn' }
    ],

    mapTpl: Ext.create('Ext.XTemplate',
        '<div class="maptbar maptbar1"><!-- --></div>',
        '<div class="maptbar maptbar2"><!-- --></div>',
        '<div class="map"><!-- --></div>',
        '<div class="maplegend"><!-- --></div>',
        '<div class="createalerte"><!-- --></div>',
        '<div class="mapinfo"><!-- --></div>'
    ),
        
    // URL de la légende (qui conditionne le chargement des couches)
    legendUrl: BASE_URL+'/../ext-res/js/app/remocra/features/adresses/data/carte.json',
    
    // Sauvegarde du dernier SousTypeAlerteElt pour la création 
    lastSousTypeAlerteElt: null,
    
    initComponent: function() {
        Ext.apply(this, {
            listeners: {
                afterrender: function() {
                    // Ajout du menu à Dessiner
                    var btn = this.maptbar2.getComponent('dessinerBtn');
                    // S'il y a des éléments dans le store, on les traite directement
                    if (Sdis.Remocra.network.TypeAlerteEltStore.getCount()>0) {
                        this.addDessinerMenu(btn);
                    } else {
                        Sdis.Remocra.network.TypeAlerteEltStore.addListener('load', Ext.bind(this.addDessinerMenu, this, [btn], {single: true}));
                    }
                    
                    this.maptbar2.getComponent('editGeomBtn').addListener('click', Ext.bind(this.activateSpecificControl, this, ['modifyGeom']));
                    this.maptbar2.getComponent('editInfoBtn').addListener('click', Ext.bind(this.activateSpecificControl, this, ['modifyAttributes']));
                    this.maptbar2.getComponent('deleteBtn').addListener('click', Ext.bind(this.activateSpecificControl, this, ['remove']));
                    
                    var createalerteNode = Ext.DomQuery.selectNode("div.createalerte", this.getEl().dom);
                    this.createAlertePanel = Ext.create('Sdis.Remocra.features.adresses.CreateAlerte', {
                        itemId: 'createAlertePanel',
                        renderTo: createalerteNode, hidden: true
                    });
                    
                    this.createAlertePanel.addListener('ok', Ext.bind(function() {
                        var mapProjection = this.map.getProjection();
                        var alerteElts = [];
                        var i;
                        var wktFormat = new OpenLayers.Format.WKT({
                            internalProjection: mapProjection,
                            externalProjection: new OpenLayers.Projection('EPSG:2154')
                        });
                        for (i=0 ; i<this.workingLayer.features.length ; i++) {
                            var feature = this.workingLayer.features[i];
                            var alerteElt = feature.data['alerteElt'];
                            var str = wktFormat.write(feature);
                            alerteElt.set('geometrie', str);
                            alerteElts.push(alerteElt);
                        }
                        this.createAlertePanel.createAlerte(alerteElts, mapProjection);
                    }, this));
                    this.createAlertePanel.addListener('cancel', Ext.bind(function() {
                        Ext.Msg.confirm('Alerte', 'Confirmez vous la réinitialisation de la saisie ?'
                            + '<br/><p style="margin-top:10px;margin-left:20px;font-style:italic;color:#a9a9a9;">'
                            + 'En choississant "Oui", vous perdrez les éléments que vous avez dessinés, la description de l\'alerte fournie et les documents chargés.'
                            + '<br/>Ces derniers ne seront pas supprimés de votre disque dur.</p>', function(btn) {
                            if (btn == "yes"){
                                // On vide la couche de travail
                                this.workingLayer.removeAllFeatures();
                                // Puis on réinitialise les informations saisies
                                this.createAlertePanel.reset();
                                this.activateSpecificControl('aucun');
                                this.enableDisableButtons();
                            }
                        }, this);
                    }, this));
                    this.createAlertePanel.addListener('alerteCreated', function(centroidFeat) {
                        Sdis.Remocra.util.Msg.msg('Alerte', 'L\'alerte a bien été créée', 3);
                        // On vide la couche de travail
                        this.workingLayer.removeAllFeatures();
                        // Puis on réinitialise les informations saisies
                        this.createAlertePanel.reset();
                        this.activateSpecificControl('aucun');
                        if (centroidFeat) {
                            // On ajoute le point de la nouvelle alerte
                            this.getLayerByCode('alertesLayer').addFeatures(centroidFeat);
                        }
                        this.enableDisableButtons();
                    }, this);

                    var nbAlertesUtils = this.maptbar1.getComponent('downloadNbAlertesParUtil');
                    if (Sdis.Remocra.Rights.getRight('REFERENTIELS').Create || Sdis.Remocra.Rights.getRight('ALERTES_EXPORT').Create) {
                        nbAlertesUtils.addListener('click', this.downloadNbAlertesUtils, this);
                    } else {
                        nbAlertesUtils.hide();
                    }
                }
            }
        });
        this.callParent(arguments);
    },
    
    manageActivationsToggles: function() {
        this.callParent(arguments);
        
        var dessinerBtn = this.maptbar2.getComponent('dessinerBtn');
        dessinerBtn.on('click', Ext.bind(function(btn, e, eOpts) {
            if (!btn.pressed) {
                // On désactive les contrôles de dessin et on cache le menu
                this.activateSpecificControl('drawpoint', false);
                this.activateSpecificControl('drawlinestring', false);
                this.activateSpecificControl('drawpolygon', false);
                btn.hideMenu();
            }
        }, this));
        this.manageActivationsTogglesBtnCtrl(dessinerBtn, 'drawpoint');
        this.manageActivationsTogglesBtnCtrl(dessinerBtn, 'drawlinestring');
        this.manageActivationsTogglesBtnCtrl(dessinerBtn, 'drawpolygon');
    },
    
    destroy: function() {
        this.createAlertePanel.destroy();
        this.callParent(arguments);
    },
    
    switchToMode: function(key) {
        var createalerteMode = key=='alrt';
        var maplegendNode = Ext.DomQuery.selectNode("div.maplegend", this.getEl().dom);
        Ext.get(maplegendNode).setVisible(!createalerteMode);
        this.createAlertePanel.setVisible(createalerteMode);
        
        if (!createalerteMode) {
            // Désactive les contrôles d'édition
            this.activateSpecificControl('aucun');
        }
    },
    edit: function() {
        this.maptbar2.setVisible(!this.maptbar2.isVisible());
        this.switchToMode(this.maptbar2.isVisible()?'alrt':'legend');
    },
    
    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            // Dessiner
            drawpoint: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point, {
                featureAdded: Ext.bind(this.createAlerteElt, this)
            }),
            drawlinestring: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Path, {
                featureAdded: Ext.bind(this.createAlerteElt, this)
            }),
            drawpolygon: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Polygon, {
                featureAdded: Ext.bind(this.createAlerteElt, this)
            }),

            // Modifier geom
            modifyGeom: new OpenLayers.Control.ModifyFeature(this.workingLayer),
            
            // Modifier attributs
            modifyAttributes: new OpenLayers.Control.SelectFeature(this.workingLayer, {
                clickout: false, toggle: false, multiple: false, hover: false, box: false,
                    onSelect: Ext.bind(this.modifyAttributes, this)
            }),
        
            // Supprimer
            remove: new OpenLayers.Control.SelectFeature(this.workingLayer, {
                clickout: false, toggle: false, multiple: false, hover: false, box: false,
                    onSelect: Ext.bind(this.removeAlerteElt, this)
            })
        });
        return ctrls;
    },
    
    createSpecificLayer: function(layerDef) {
        // Couche GeoJSON
        if (layerDef.id == 'alertesLayer') {
            layerDef.url = BASE_URL+'/../adresses/alerte';
            layerDef.styleMap = this.alerteStyleMap();
            layerDef.infoMarginInPixels = this.alertePointRadius;
            var alertesLayer = this.createGeoJsonLayer(layerDef);
            alertesLayer.events.on({
                'loadend': this.zoomToBestExtent,
                scope: this
            });
            return alertesLayer;
        }
        return this.callParent(arguments);
    },
    
    getSpecificInfoFormaters: function() {
        // On reprend les formateurs du parent
        var formaters = this.callParent(arguments);
        Ext.apply(formaters, {
            'alertesLayer': new Ext.XTemplate(
                '<div><ul style="margin-bottom:10px;">',
                    '<b>Alerte {raw.id}</b>',
                    '<tpl if="raw.rapporteur">',
                        '<li style="margin-left:20px;">',
                            'Rapporteur : {raw.rapporteur}',
                        '</li>',
                    '</tpl>',
                    '<tpl if="raw.constat">',
                        '<li style="margin-left:20px;">',
                            'Date de constat : {raw.constat}',
                        '</li>',
                    '</tpl>',
                    '<tpl if="raw.modification">',
                        '<li style="margin-left:20px;">',
                            'Date de modification : {raw.modification}',
                        '</li>',
                    '</tpl>',
                    '<li style="margin-left:20px;">',
                        'Etat : ',
                        '<tpl if="raw.etat===true">',
                            '<span style="color:green;">Acceptée</span>',
                        '<tpl elseif="raw.etat===false">',
                            '<span style="color:red;">Refusée</span>',
                        '<tpl else>',
                            '<span style="color:blue;">En attente de traitement</span>',
                        '</tpl>',
                    '</li>',
                    '<tpl if="raw.commentaire">',
                        '<li style="margin-left:20px;">',
                            'Commentaire : {raw.commentaire}',
                        '</li>',
                    '</tpl>',
                    // Documents
                    '<tpl if="raw.documents && raw.documents.length&gt;0">',
                    '<li style="margin-left:20px;">',
                        '<ul>Documents :',
                            '<tpl for="raw.documents">',
                                '<li style="margin-left:20px"><a href="telechargement/document/{code}" target="_blank" >{nom}</a></li>',
                            '</tpl>',
                        '</ul>',
                    '</li>',
                '</tpl>',
                '</ul></div>'
            )
        });
        return formaters;
    },
    
    addDessinerMenu: function(btn) {
        var menuItems = [];
        
        var i, j;
        // Pour tout type
        var typeAlerteElts = Sdis.Remocra.network.TypeAlerteEltStore.data.items;
        for (i=0 ; i<typeAlerteElts.length ; i++) {
            var typeAlerteElt = typeAlerteElts[i];
            
            // Pour tout sous-type
            var soustypeItems = [];
            var sousTypeAlerteElts = typeAlerteElt.sousTypeAlerteEltsStore.data.items;
            for (j=0 ; j<sousTypeAlerteElts.length ; j++) {
                var sousTypeAlerteElt = sousTypeAlerteElts[j];
                
                soustypeItems.push({
                    text: sousTypeAlerteElt.get('nom'),
                    iconCls: 'draw-'+sousTypeAlerteElt.get('typeGeom').toLowerCase()+'Icon',
                    checkedCls: '', uncheckedCls: '', // Pour éviter d'avoir une case à cocher
                    toggleGroup: 'excl-ctrl1', checked: false, group: 'measure',
                    listeners: {
                        click: Ext.bind(this.dessiner, this, [sousTypeAlerteElt])
                    }
                });
            }
            
            var typeItem = { text: typeAlerteElt.get('nom'), menu: new Ext.menu.Menu({items: soustypeItems}) };
            menuItems.push(typeItem);
        }
        
        btn.setMenu(new Ext.menu.Menu({
            items: menuItems
        }));
    },
    
    alerteStyleMap: function() {
        return new OpenLayers.StyleMap({
            "default": new OpenLayers.Style({
                fillColor: '${color}', fillOpacity: 0.5,
                strokeColor: '${color}', strokeOpacity: 1, strokeWidth: 3,
                pointRadius: this.alertePointRadius
            }, {
                context: {
                    color: function(feature) {
                        var etat = feature.data['etat'];
                        if (etat===true) {
                            return 'green';
                        } else if (etat===false) {
                            return 'red';
                        } else {
                            return 'blue';
                        }
                    }
                }
            })
        });
    },
    
    dessiner: function(sousTypeAlerteElt) {
        var typeGeom = sousTypeAlerteElt.get('typeGeom');
        this.lastSousTypeAlerteElt = sousTypeAlerteElt;
        this.activateSpecificControl('draw'+typeGeom.toLowerCase());
    },
    
    createAlerteElt: function(feature) {
        var alerteEltWindow = Ext.create('Sdis.Remocra.features.adresses.AlerteEltWindow');
        alerteEltWindow.show();
        alerteEltWindow.addListener('ok', function(alerteElt) {
            alerteElt.setSousTypeAlerteElt(this.lastSousTypeAlerteElt);
            feature.data['alerteElt'] = alerteElt; 
            alerteEltWindow.close();
            this.enableDisableButtons();
        }, this);
        alerteEltWindow.addListener('close', function() {
            if (!feature.data['alerteElt']) {
                // Annulation => nettoyage
                feature.layer.removeFeatures(feature);
            }
        }, this);
    },
    
    modifyAttributes: function(feature) {
        var alerteElt = feature.data['alerteElt']; 
        var alerteEltWindow = Ext.create('Sdis.Remocra.features.adresses.AlerteEltWindow', {alerteElt: alerteElt});
        alerteEltWindow.show();
        alerteEltWindow.addListener('ok', function(alerteElt) {
            this.getSpecificControl('modifyAttributes').unselectAll();
            alerteEltWindow.close();
        }, this);
        alerteEltWindow.addListener('cancel', function() {
            this.getSpecificControl('modifyAttributes').unselectAll();
            alerteEltWindow.close();
        }, this);
    },
    
    // On retire la feature. On sait que pour les alertes, les objets dessinés n'ont pas encore d'existence côté serveur.
    removeAlerteElt: function(feature) {
        Ext.Msg.confirm('Alerte', 'Supprimer l\'élément ?', function(btn) {
            if (btn == "yes"){
                feature.layer.removeFeatures(feature);
            } else {
                this.getSpecificControl('remove').unselectAll();
            }
            this.enableDisableButtons();
        }, this);
    },
    
    enableDisableButtons: function() {
        var toolbar = this.createAlertePanel.getDockedItems('toolbar[dock="bottom"]')[0];
        toolbar.getComponent('validerAlerte').setDisabled(this.workingLayer.features.length<1);
    },
    
    zoomToBestExtent: function() {
        var layer = this.getLayerByCode('alertesLayer');
        if (layer && layer.features.length>0) {
            this.map.zoomToExtent(layer.getDataExtent());
        } else {
            this.callParent(arguments);
        }
    },

    downloadNbAlertesUtils: function() {
        Ext.Msg.confirm('Nombre d\'alertes par utilisateur',
                'Votre demande va être enregistrée. Lorsque le fichier sera prêt, vous serez averti par un message électronique. Souhaitez-vous continuer ?',
                function(btn) {
                    if (btn == "yes"){
                        this.goDownloadNbAlertesUtils();
                    }
        }, this);
    },

    goDownloadNbAlertesUtils: function() {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/nbalertesutilisateur"),
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Nombre d\'alertes par utilisateur',
                        'Votre demande a été prise en compte.', 5);
                    var downloadNbAlertesParUtil =  this.maptbar1.down('#downloadNbAlertesParUtil');
                    downloadNbAlertesParUtil.setDisabled(true);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Nombre d\'alertes par utilisateur',
                        'Un problème est survenu lors de l\'enregistrement de la demande.' + msg + '.');
                }
            }
        });
    }
});