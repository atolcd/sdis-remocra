Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.store.Voie');
Ext.require('Sdis.Remocra.store.Rci');
Ext.require('Sdis.Remocra.store.TypeRciOrigineAlerte');
Ext.require('Sdis.Remocra.store.TypeRciPromCategorie');
Ext.require('Sdis.Remocra.store.TypeRciPromFamille');
Ext.require('Sdis.Remocra.store.TypeRciPromPartition');
Ext.require('Sdis.Remocra.store.TypeRciDegreCertitude');

Ext.define('Sdis.Remocra.controller.rci.Fiche', {
    extend: 'Ext.app.Controller',

    stores: ['Commune', 'Voie', 'Rci', 'TypeRciOrigineAlerte', 'TypeRciPromCategorie',
             'TypeRciPromFamille', 'TypeRciPromPartition', 'TypeRciDegreCertitude'],

    refs: [{
        ref: 'rciFiche',
        selector: 'rciFiche'
    }],

    init: function() {

        this.control({
            'rciFiche': {
                afterrender: this.onAfterRenderFiche,
                close: this.onCloseFiche
            },
            'rciFiche #ok': {
                click: this.validFiche
            },
            'rciFiche #cancel': {
                click: this.cancelFiche
            },
            
            // Position
            'rciFiche #formatXY': {
                select: this.onChangeFormatXY
            },
            // dd
            'rciFiche #ddOrientX': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #ddCoordXd': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #ddOrientY': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #ddCoordYd': {
                change: this.onChangeXY,
                delay : 1000
            },
            // dm
            'rciFiche #dmOrientX': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmCoordXd': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmCoordXm': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmOrientY': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmCoordYd': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmCoordYm': {
                change: this.onChangeXY,
                delay : 1000
            },
            // dms
            'rciFiche #dmsOrientX': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsCoordXd': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsCoordXm': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsCoordXs': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsOrientY': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsCoordYd': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsCoordYm': {
                change: this.onChangeXY,
                delay : 1000
            },
            'rciFiche #dmsCoordYs': {
                change: this.onChangeXY,
                delay : 1000
            },
            
            // Commune
            'rciFiche #commune': {
                select: this.onChangeCommune
            },
            
            // Promethee
            'rciFiche #famillePromethee': {
                // -> Partition
                change: this.onChangeFamille
            },
            'rciFiche #partitionPromethee': {
                // -> Categorie
                change: this.onChangePartition
            }
        });
    },

    onChangeFormatXY: function(combo, records, eOpts) {
        var fiche = combo.up('rciFiche'), formatXY = records[0].get('value');
        this.manageFormatXYVisibilities(fiche, formatXY);
    },
    manageFormatXYVisibilities: function(fiche, formatSaisie) {
        var constatations = fiche.query('rciConstatations')[0],
            ddX = fiche.queryById('ddX'), ddY = fiche.queryById('ddY'),
            dmX = fiche.queryById('dmX'), dmY = fiche.queryById('dmY'),
            dmsX = fiche.queryById('dmsX'), dmsY = fiche.queryById('dmsY');
        ddX.setVisible(formatSaisie=='dd');
        ddY.setVisible(formatSaisie=='dd');
        dmX.setVisible(formatSaisie=='dm');
        dmY.setVisible(formatSaisie=='dm');
        dmsX.setVisible(formatSaisie=='dms');
        dmsY.setVisible(formatSaisie=='dms');
        constatations.doLayout();
    },

    // Changement X ou Y
    onChangeXY: function(cmp, newValue, oldValue, eOpts) {
        // Début du traitment, on marque pour ne pas exécuter plusieurs fois les mise à jour
        if (this.onChangeXYIsRunning) {
            return;
        }
        this.onChangeXYIsRunning = true;
        var fiche = this.getRciFiche(), formatXY = fiche.queryById('formatXY').getValue();
        var coordX, coordY;
        if (formatXY == 'dd') {
            var ddOrientX = fiche.queryById('ddOrientX');
            var ddOrientY = fiche.queryById('ddOrientY');
            var ddCoordXd = fiche.queryById('ddCoordXd');
            var ddCoordYd = fiche.queryById('ddCoordYd');
            if (ddOrientX.isValid() && ddCoordXd.isValid()
                && ddOrientY.isValid() && ddCoordYd.isValid()) {
                coordX = (ddOrientX.getValue()=='E'?1:-1) * ddCoordXd.getValue();
                coordY = (ddOrientY.getValue()=='N'?1:-1) * ddCoordYd.getValue();
                this.updDm(coordX, coordY);
                this.updDms(coordX, coordY);
            }
        } else if (formatXY == 'dm') {
            var dmOrientX = fiche.queryById('dmOrientX');
            var dmOrientY = fiche.queryById('dmOrientY');
            var dmCoordXd = fiche.queryById('dmCoordXd');
            var dmCoordXm = fiche.queryById('dmCoordXm');
            var dmCoordYd = fiche.queryById('dmCoordYd');
            var dmCoordYm = fiche.queryById('dmCoordYm');
            if (dmOrientX.isValid() && dmCoordXd.isValid() && dmCoordXm.isValid()
                && dmOrientY.isValid() && dmCoordYd.isValid() && dmCoordYm.isValid()) {
                coordX = Sdis.Remocra.util.Util.getCoordinateFromDMSO(dmCoordXd.getValue(), dmCoordXm.getValue(), 0, dmOrientX.getValue()=='E'?'E':'W');
                coordY = Sdis.Remocra.util.Util.getCoordinateFromDMSO(dmCoordYd.getValue(), dmCoordYm.getValue(), 0, dmOrientY.getValue());
                this.updDd(coordX, coordY);
                this.updDms(coordX, coordY);
            }
        } else if (formatXY == 'dms') {
            var dmsOrientX = fiche.queryById('dmsOrientX');
            var dmsOrientY = fiche.queryById('dmsOrientY');
            var dmsCoordXd = fiche.queryById('dmsCoordXd');
            var dmsCoordXm = fiche.queryById('dmsCoordXm');
            var dmsCoordXs = fiche.queryById('dmsCoordXs');
            var dmsCoordYd = fiche.queryById('dmsCoordYd');
            var dmsCoordYm = fiche.queryById('dmsCoordYm');
            var dmsCoordYs = fiche.queryById('dmsCoordYs');
            if (dmsOrientX.isValid() && dmsCoordXd.isValid() && dmsCoordXm.isValid() && dmsCoordXs.isValid()
                && dmsOrientY.isValid() && dmsCoordYd.isValid() && dmsCoordYm.isValid() && dmsCoordYs.isValid()) {
                coordX = Sdis.Remocra.util.Util.getCoordinateFromDMSO(dmsCoordXd.getValue(), dmsCoordXm.getValue(), dmsCoordXs.getValue(), dmsOrientX.getValue()=='E'?'E':'W');
                coordY = Sdis.Remocra.util.Util.getCoordinateFromDMSO(dmsCoordYd.getValue(), dmsCoordYm.getValue(), dmsCoordYs.getValue(), dmsOrientY.getValue());
                this.updDd(coordX, coordY);
                this.updDm(coordX, coordY);
            }
        }
        
        // Si X et Y définis, on met à jour geometrie et on reporte les
        // modifications
        if (Ext.isNumber(coordX) && Ext.isNumber(coordY)) {
            var wktFormat = new OpenLayers.Format.WKT();
            var wkt = "POINT(" + coordX + " " + coordY + ")";
            var geom = wktFormat.read(wkt).geometry;
            geom.transform(Sdis.Remocra.widget.map.EPSG4326, 'EPSG:2154');
            
            fiche.record.set('geometrie', 'POINT('+geom.x + ' '+geom.y+')');
            this.cascadeXY();
        }
        // Fin du traitement, on supprime le marqueur
        delete this.onChangeXYIsRunning;
    },

    updDd: function(x, y) {
        var fiche = this.getRciFiche();
        fiche.queryById('ddOrientX').setValue(x<0?'O':'E', true);
        fiche.queryById('ddOrientY').setValue(y<0?'S':'N', true);
        fiche.queryById('ddCoordXd').setRawValue(Math.abs(x));
        fiche.queryById('ddCoordYd').setRawValue(Math.abs(y));
    },
    updDm: function(x, y) {
        var fiche = this.getRciFiche();
        var dmX = Sdis.Remocra.util.Util.getFormattedLonLat(x, 'lon', 'dm', true, true);
        var dmY = Sdis.Remocra.util.Util.getFormattedLonLat(y, 'lat', 'dm', true, true);
        fiche.queryById('dmOrientX').setValue(dmX.o, true);
        fiche.queryById('dmOrientY').setValue(dmY.o, true);
        fiche.queryById('dmCoordXd').setRawValue(dmX.d);
        fiche.queryById('dmCoordXm').setRawValue(dmX.m);
        fiche.queryById('dmCoordYd').setRawValue(dmY.d);
        fiche.queryById('dmCoordYm').setRawValue(dmY.m);
    },
    updDms: function(x, y) {
        var fiche = this.getRciFiche();
        var dmsX = Sdis.Remocra.util.Util.getFormattedLonLat(x, 'lon', 'dms', true, true);
        var dmsY = Sdis.Remocra.util.Util.getFormattedLonLat(y, 'lat', 'dms', true, true);
        fiche.queryById('dmsOrientX').setValue(dmsX.o, true);
        fiche.queryById('dmsOrientY').setValue(dmsY.o, true);
        fiche.queryById('dmsCoordXd').setRawValue(dmsX.d);
        fiche.queryById('dmsCoordXm').setRawValue(dmsX.m);
        fiche.queryById('dmsCoordXs').setRawValue(dmsX.s);
        fiche.queryById('dmsCoordYd').setRawValue(dmsY.d);
        fiche.queryById('dmsCoordYm').setRawValue(dmsY.m);
        fiche.queryById('dmsCoordYs').setRawValue(dmsY.s);
    },

    // Mise à jour des informations basées sur la position : Commune, Voie, Coordonnées DFCI
    cascadeXY: function() {
        var fiche = this.getRciFiche(), form = fiche.down('form[name=fiche]').getForm(),
            cboCommune = fiche.queryById('commune');
        
        var wkt = fiche.record.get('geometrie');
        var geom = new OpenLayers.Format.WKT().read(wkt).geometry;
        Sdis.Remocra.model.Commune.loadByXY(geom.x, geom.y, '2154', {
            scope: this,
            success: function(record) {
                if (!record) {
                    // Pas de commune ?
                    return;
                }
                // Ajout de la commune au store si nécessaire
                if (!cboCommune.getStore().getById(record.getId())) {
                    cboCommune.getStore().add(record);
                }
                fiche.record.set('commune', record);
                cboCommune.select(record);
                this.onChangeCommune(cboCommune, [record]);
            },
            failure: function() {
                Sdis.Remocra.util.Msg.msg('Départ de feu', 'La commune n\'a pas été trouvée.', 3);
            }
        });
        
        // Coordonnées DFCI
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../hydrantspena/dfci'),
            params: {
                wkt: wkt,
                srid: '2154'
            },
            success: function(response) {
                var result = Ext.decode(response.responseText);
                fiche.record.set('coordDFCI', result.message);
                fiche.queryById('coordDFCI').setValue(result.message);
            }
        });
    },
    
    onChangeCommune: function(combo, records) {
        var fiche = combo.up('rciFiche'),
            voie = fiche.queryById('voie'),
            commune = records[0], filters = [], wkt = fiche.record.get('geometrie');

        if (wkt != null) {
            filters.push({
                property: "wkt",
                value: wkt
            });
        }
        if (commune != null) {
            filters.push({
                property: "communeId",
                value: commune.get('id')
            });
        }
        // Filtrage des voies et sélection de la première
        voie.store.on('load', function(store, records, successful, eOpts) {
            if (records.length>0) {
                this.setValue(records[0]);
            }
        }, voie, {single: true});
        voie.store.clearFilter(true);
        voie.store.filter(filters);
    },

    onChangeFamille: function(combo, newValue, oldValue, opt) {
        var rec = combo.getStore().getById(newValue);
        var rciCauseResultats = combo.up('rciCauseResultats');
        var cboPartition = rciCauseResultats.queryById('partitionPromethee');
        var cboCategorie = rciCauseResultats.queryById('categoriePromethee');
        cboPartition.setDisabled(rec == null);
        if (rec) {
            cboPartition.store.clearFilter(true);
            cboPartition.store.filter({
                filterFn: function(partition) {
                    return partition.getFamille().get('id') == rec.get('id');
                }
            });
            // Recherche ancienne valeur et réinitialisation si non trouvée
            var currentPartitionValue = cboPartition.getValue();
            var foundIdx = cboPartition.store.findBy(function(record, id) {
                return currentPartitionValue == id;
            });
            if (foundIdx<0) {
                cboPartition.setValue(null);
                cboCategorie.setValue(null);
            }
        }
    },
    onChangePartition: function(combo, newValue, oldValue, opt) {
        var rec = combo.getStore().getById(newValue);
        var cboCategorie = combo.up('rciCauseResultats').queryById('categoriePromethee');
        cboCategorie.setDisabled(rec == null);
        if (rec) {
            cboCategorie.store.clearFilter(true);
            cboCategorie.store.filter({
                filterFn: function(categorie) {
                    return categorie.getPartition().get('id') == rec.get('id');
                }
            });
            // Recherche ancienne valeur et réinitialisation si non trouvée
            var currentCategorieValue = cboCategorie.getValue();
            var foundIdx = cboCategorie.store.findBy(function(record, id) {
                return currentCategorieValue == id;
            });
            if (foundIdx<0) {
                cboCategorie.setValue(null);
            }
        }
    },
    
    onAfterRenderFiche: function(fiche) {
        if (!fiche.record) {
            return;
        }
        
        var form = fiche.down('form[name=fiche]').getForm();
        
        // On charge
        form.loadRecord(fiche.record);
        
        // X, Y : vers 4326
        var wkt = fiche.record.get('geometrie');
        if (!Ext.isEmpty(wkt)) {
            var wktFormat = new OpenLayers.Format.WKT();
            var result = wktFormat.read(wkt);
            var geom = result.geometry;
            geom.transform('EPSG:2154', Sdis.Remocra.widget.map.EPSG4326);
            this.updDd(geom.x, geom.y);
            this.updDm(geom.x, geom.y);
            this.updDms(geom.x, geom.y);
        }
        // Saisie dd par défaut
        this.manageFormatXYVisibilities(fiche, 'dd');
        
        // Commune
        if (fiche.record.getCommune()) {
            var cboCommune = fiche.queryById('commune');
            cboCommune.store.add(fiche.record.getCommune());
            cboCommune.setValue(fiche.record.getCommune().getId(), false); // On empêche les événements de se propager
            this.onChangeCommune(cboCommune, [fiche.record.getCommune()]);
        } else if (fiche.record.feature) {
            // Actuellement pas de commune et un point est défini (X et Y nécessaires)
            this.cascadeXY();
        }
        
        // Voie
        if (fiche.record.get('voie')) {
            var cboVoie = fiche.queryById('voie');
            cboVoie.setRawValue(fiche.record.get('voie'));
        }
        
        // Origine
        if (fiche.record.getOrigineAlerte()) {
            var cboOrigine = fiche.queryById('origineAlerte');
            cboOrigine.select(fiche.record.getOrigineAlerte());
        }
        
        // Degré certitude
        if (fiche.record.getDegreCertitude()) {
            var cboDegreCertitude = fiche.queryById('degreCertitude');
            cboDegreCertitude.select(fiche.record.getDegreCertitude());
        }
        
        // Catégorie, partition, famille
        var cboCategorie = fiche.queryById('categoriePromethee');
        var cboPartition = fiche.queryById('partitionPromethee');
        var cboFamille = fiche.queryById('famillePromethee');
        if (fiche.record.getCategoriePromethee()) {
            cboFamille.select(fiche.record.getCategoriePromethee().getPartition().getFamille());
            cboPartition.select(fiche.record.getCategoriePromethee().getPartition());
            cboCategorie.select(fiche.record.getCategoriePromethee());
        } else if (fiche.record.getPartitionPromethee()) {
            cboFamille.select(fiche.record.getPartitionPromethee().getFamille());
            cboPartition.select(fiche.record.getPartitionPromethee());
            cboCategorie.setValue(null);
        } else if (fiche.record.getFamillePromethee()) {
            cboFamille.select(fiche.record.getFamillePromethee());
            cboPartition.setValue(null);
            cboCategorie.setValue(null);
        } else {
            cboFamille.setValue(null);
            cboPartition.setValue(null);
            cboCategorie.setValue(null);
        }
        
        // Utilisateur
        fiche.queryById('info').setFieldLabel(this.msgEntete(fiche.record));

        // Arrivées
        if (fiche.record.getArriveeDdtmOnf()) {
            var arriveeDdtmOnf = fiche.queryById('arriveeDdtmOnf');
            arriveeDdtmOnf.select(fiche.record.getArriveeDdtmOnf());
        }
        if (fiche.record.getArriveeSdis()) {
            var arriveeSdis = fiche.queryById('arriveeSdis');
            arriveeSdis.select(fiche.record.getArriveeSdis());
        }
        if (fiche.record.getArriveeGendarmerie()) {
            var arriveeGendarmerie = fiche.queryById('arriveeGendarmerie');
            arriveeGendarmerie.select(fiche.record.getArriveeGendarmerie());
        }
        if (fiche.record.getArriveePolice()) {
            var arriveePolice = fiche.queryById('arriveePolice');
            arriveePolice.select(fiche.record.getArriveePolice());
        }

        // Indice Rothermel
        if (fiche.record.get('indiceRothermel') == null) {
            fiche.queryById('indiceRothermel').setValue(-1);
        }
        
        // Date incendie
        var dateIncendie = fiche.record.get('dateIncendie');
        if (dateIncendie) {
            fiche.queryById('heureIncendie').setValue(dateIncendie);
        }
        
        // Date GDH
        var gdh = fiche.record.get('gdh');
        if (gdh) {
            fiche.queryById('dateGdh').setValue(gdh);
            fiche.queryById('heureGdh').setValue(gdh);
        }
        
        // Documents : on ajoute des liens s'il en existe (un conteneur est prévu à cet effet dans FileUploadPanel)
        var documentsP = fiche.queryById('documents');
        var documents = fiche.record.rciDocuments();
        documentsP.addDocuments(documents, {
            urlStart: Sdis.Remocra.model.Rci.proxy.url + '/document',
            method: 'DELETE',
            scope: this,
            failure: function() {
                Ext.MessageBox.show({
                    title: 'Départ de feu',
                    msg: 'Une erreur est survenue lors de la suppression du départ.',
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.ERROR
                 });
            }
        });
        // Pour marquer les champs obligatoires
        form.isValid();
    },

    msgEntete: function(record) {
        if (record.get('id')) {
            var dateModification = record.get('dateModification');
            var utilisateur = record.getUtilisateur();
            return 'Dernière modification le ' + dateModification.toLocaleDateString() + ' par ' + utilisateur.get('prenomNomIdentifiant');
        }
        return 'Nouveau départ en cours de saisie par ' + Sdis.Remocra.network.ServerSession.getUserData('login');
    },

    onCloseFiche : function(win) {
        if (win.record.feature) {
            win.record.feature.destroy();
        }
        this.getController('rci.Rci').refreshMap();
    },

    cancelFiche : function(button) {
        button.up('window').close();
    },

    validFiche: function(button) {
        var x, y, point, fiche = button.up('window'), documentsPanel = fiche.queryById('documents'),
            form = fiche.down('form[name=fiche]').getForm(), record = form.getRecord();

        if(!form.isValid()){
            Ext.MessageBox.show({
                title: 'Départ de feu',
                msg: 'Les éléments saisis ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.',
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.ERROR
             });
            return;
        }

        form.updateRecord();

        // Arrivées
        record.setArriveeDdtmOnf(this.getValueModelCbo(fiche, 'arriveeDdtmOnf'));
        record.setArriveeSdis(this.getValueModelCbo(fiche, 'arriveeSdis'));
        record.setArriveeGendarmerie(this.getValueModelCbo(fiche, 'arriveeGendarmerie'));
        record.setArriveePolice(this.getValueModelCbo(fiche, 'arriveePolice'));

        record.set('dateModification', new Date());
        record.setUtilisateur(null);
        record.setOrigineAlerte(this.getValueModelCbo(fiche, 'origineAlerte'));
        // Commune : suppression de la géométrie du POST pour alléger le flux
        // Seul l'id est utilisé côté serveur et la géométrie n'est pas exploitée côté client
        var commune = this.getValueModelCbo(fiche, 'commune');
        commune.set('geometrie', null);
        record.setCommune(commune);
        record.setCategoriePromethee(this.getValueModelCbo(fiche, 'categoriePromethee'));
        record.setPartitionPromethee(this.getValueModelCbo(fiche, 'partitionPromethee'));
        record.setFamillePromethee(this.getValueModelCbo(fiche, 'famillePromethee'));
        record.setDegreCertitude(this.getValueModelCbo(fiche, 'degreCertitude'));

        // Cas spécial de la géométrie
        record.set('geometrie', fiche.record.get('geometrie'));

        // Reprise de la date indendie avec l'heure
        var dateIncendie = fiche.queryById('dateIncendie').getValue();
        var heureIncendie = fiche.queryById('heureIncendie').getValue();
        if (dateIncendie) {
            if (heureIncendie) {
                // Jour et heure précisés
                dateIncendie.setHours(heureIncendie.getHours(), heureIncendie.getMinutes(),
                        heureIncendie.getSeconds(), heureIncendie.getMilliseconds());
            } else {
                // Jour précisé mais pas l'heure : heure = minuit
                dateIncendie.setHours(0, 0, 0, 0);
            }
        } else if (heureIncendie) {
            // Jour non précisé mais l'heure oui : jour = date du jour
            dateIncendie = new Date();
            dateIncendie.setHours(heureIncendie.getHours(), heureIncendie.getMinutes(),
                    heureIncendie.getSeconds(), heureIncendie.getMilliseconds());
        }
        record.set('dateIncendie', dateIncendie);
        
        // Reprise de la date GDH avec l'heure
        var dateGdh = fiche.queryById('dateGdh').getValue();
        var heureGdh = fiche.queryById('heureGdh').getValue();
        if (dateGdh) {
            if (heureGdh) {
                // Jour et heure précisés
                dateGdh.setHours(heureGdh.getHours(), heureGdh.getMinutes(),
                        heureGdh.getSeconds(), heureGdh.getMilliseconds());
            } else {
                // Jour précisé mais pas l'heure : heure = minuit
                dateGdh.setHours(0, 0, 0, 0);
            }
        } else if (heureGdh) {
            // Jour non précisé mais l'heure oui : jour = date du jour
            dateGdh = new Date();
            dateGdh.setHours(heureGdh.getHours(), heureGdh.getMinutes(),
                    heureGdh.getSeconds(), heureGdh.getMilliseconds());
        }
        record.set('gdh', dateGdh);
        
        // Indice Rothermel
        if (record.get('indiceRothermel') == -1) {
            record.set('indiceRothermel', null);
        }
        
        // Gestion avec Panel documents
        var jsonDataField = documentsPanel.getComponent('jsonRci');
        if (jsonDataField==null) {
            jsonDataField = new Ext.form.field.Hidden({itemId: 'jsonRci', name: 'jsonRci'});
            documentsPanel.add(jsonDataField);
        }
        var preparedRci = record.getProxy().getWriter().getRecordData(record);
        jsonDataField.setValue(Ext.encode(preparedRci));
        documentsPanel.computeNames('doc');
        
        var id = record.get('id');
        var newRecord = id==null||id==0;
        
        // On réalise un POST à l'ancienne pour que les documents puissent être envoyés
        documentsPanel.submit({
            clientValidation: false, // On empêche la validation cliente car faite à la main bien que les champs cachés ne soient pas forcément valides
            url: record.getProxy().url + (record.phantom ? '' : '/' + record.getId()),
            success: function(fp, o) {
                Sdis.Remocra.util.Msg.msg('Départ de feu', 'Le départ de feu a été ' + (newRecord ? 'créé' : 'mis à jour')
                        + ' avec succès.');
                // On ferme et on raffraichit la carte
                fiche.close();
            },
            failure: function(fp, o) {
                Ext.MessageBox.show({
                    title: 'Départ de feu',
                    msg: 'Une erreur est survenue lors de l\'enregistrement du départ.',
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.ERROR
                 });
            }, scope: this
        });
    },
    
    getValueModelCbo : function(fiche, cboOrItemId) {
        var cbo = Ext.isString(cboOrItemId) ? fiche.queryById(cboOrItemId) : cboOrItemId;
        var cboVal = cbo.getValue();
        if (!cboVal) {
            // Pas de valeur
            return false;
        }
        var val = cbo.findRecordByValue(cboVal);
        if (!val) {
            if (cboOrItemId.indexOf('arrivee')>=0) {
                // Non trouvé et cas d'un utilisateur (arrivee*)
                // Possible à cause de la pagination
                val = Ext.create('Sdis.Remocra.model.Utilisateur', { id : cboVal });
                val.setOrganisme(null);
                val.setProfilUtilisateur(null);
            }
        }
        return val;
    }
});