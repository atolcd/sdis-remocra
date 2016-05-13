Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.model.Voie');
Ext.require('Sdis.Remocra.model.HydrantPrescrit');

Ext.require('Sdis.Remocra.features.prescrits.FichePrescrit');

Ext.define('Sdis.Remocra.controller.hydrant.Prescrit', {
    extend: 'Ext.app.Controller',

    stores: ['Commune','Voie'],

    refs: [{
        ref: 'map',
        selector: 'prescritsMap'
    }],

    init: function() {
        this.control({
            'prescritsMap': {
                layersadded: this.initControlMap
            },
            'prescritsMap #dessinerBtnPrescrit': {
                toggle: function(button, pressed) {
                    this.getMap().activateSpecificControl('drawPoint', pressed);
                }
            },
            'prescritsMap #selectBtn': {
                toggle: function(button, pressed) {
                    this.getMap().activateSpecificControl('selectPolygone', pressed);
                }
            },
            'prescritsMap #editInfoBtn': {
                click: this.showFicheHydrant
            },
            'prescritsMap #deleteBtn': {
                click: this.deleteHydrant
            },
            'fichePrescrit': {
                afterrender: this.onAfterRenderFiche,
                close: this.onClose
            },
            'fichePrescrit #ok': {
                click: this.validFicheHydrant
            }
        });
    },

    refreshMap: function() {
        if (this.getMap() != null && this.getMap().rendered) {
            this.unselectAll();
            this.getMap().hydrantLayer.refresh({
                force: true
            });
        }
    },

    onClose: function(fiche) {
        if (fiche.hydrant.feature) {
            fiche.hydrant.feature.destroy();
        }
        this.refreshMap();
    },

    unselectAll: function() {
        this.getMap().getSpecificControl('selectPolygone').unselectAll();
    },

    addEventToControl: function(controlName, cfg) {
        var control = this.getMap().getSpecificControl(controlName);
        if (control) {
            Ext.applyIf(cfg, {
                scope: this
            });
            control.events.on(cfg);
        }
    },

    initControlMap: function() {
        this.addEventToControl('drawPoint', {
            'featureadded': this.onFeatureAdded
        });

        this.getMap().hydrantLayer.events.on({
            'featureselected': this.onSelectedFeatureChange,
            'featureunselected': this.onSelectedFeatureChange,
            scope: this
        });

    },

    onSelectedFeatureChange: function() {
        var nbSelect = this.getMap().hydrantLayer.selectedFeatures.length;
        var btnFiche = this.getMap().queryById('editInfoBtn');
        var btnDelete = this.getMap().queryById('deleteBtn');
        btnFiche.setDisabled(nbSelect != 1);
        btnDelete.setDisabled(nbSelect != 1);
    },

    onFeatureAdded: function(event) {
        this.unselectAll();
        var hydrant = Ext.create('Sdis.Remocra.model.HydrantPrescrit', {
            datePrescrit: new Date()
        });
        if (event.feature != null) {
            var mapProjection = this.getMap().map.getProjection();
            var wktFormat = new OpenLayers.Format.WKT({
                internalProjection: mapProjection,
                externalProjection: new OpenLayers.Projection('EPSG:2154')
            });
            var str = wktFormat.write(event.feature);
            hydrant.set('geometrie', str);
            hydrant.feature = event.feature;
            hydrant.srid = this.getMap().getCurrentSrid();
        }

        Ext.widget('fichePrescrit', {
            hydrant: hydrant
        }).show();

    },

    onAfterRenderFiche: function(fiche) {
        if (fiche.hydrant) {
            var form = fiche.down('form').getForm();

            form.loadRecord(fiche.hydrant);
            // Cas spéciaux des X et Y
            var wkt = fiche.hydrant.get('geometrie');
            if (!Ext.isEmpty(wkt)) {
                var wktFormat = new OpenLayers.Format.WKT();
                var result = wktFormat.read(wkt);
                var geom = result.geometry;
                geom.transform('EPSG:2154', Sdis.Remocra.widget.map.EPSG4326);
                form.findField('x').setValue(Ext.Number.toFixed(geom.x, 4));
                form.findField('y').setValue(Ext.Number.toFixed(geom.y, 4));
            }
        }
    },

    validFicheHydrant: function(button) {
        var fiche = button.up('window'), form = fiche.down('form[name=fiche]').getForm();

        var hydrant = form.getRecord();

        if (form.isValid()) {
            form.updateRecord();
            if (hydrant.dirty || hydrant.phantom) {
                hydrant.save({
                    scope: this,
                    success: function(record, operation) {
                        fiche.close();
                    },
                    failure: function(record, operation) {
                        console.log("success", record);
                    }
                });
            } else {
                fiche.close();
            }
        }
    },

    getSelectedFeatures: function() {
        var features = this.getMap().hydrantLayer.selectedFeatures;
        return Ext.Array.from(features);
    },

    showFicheHydrant: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            Sdis.Remocra.model.HydrantPrescrit.load(features[0].fid, {
                scope: this,
                success: function(record) {
                    Ext.widget('fichePrescrit', {
                        hydrant: record
                    }).show();
                }
            });
        }
    },

    deleteHydrant: function() {
        var features = this.getSelectedFeatures();
        if (features.length == 1) {
            Ext.Msg.confirm('Suppression Hydrant prescrit', 'Confirmez-vous la suppression de l\'hydrant prescrit ?', function(buttonId) {
                if (buttonId == 'yes') {
                    var hydrant = Ext.create(Sdis.Remocra.model.HydrantPrescrit, {
                        id: features[0].fid
                    });
                    hydrant.destroy({
                        scope: this,
                        success: function(record, operation) {
                            this.getMap().hydrantLayer.removeFeatures(features);
                            Sdis.Remocra.util.Msg.msg('Suppression', 'L\'hydrant a bien été supprimé.');
                            this.onSelectedFeatureChange();
                        }
                    });
                }
            }, this);
        }
    }
});