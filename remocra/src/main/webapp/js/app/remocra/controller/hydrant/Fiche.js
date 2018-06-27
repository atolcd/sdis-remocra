Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.model.Voie');
Ext.require('Sdis.Remocra.store.TypeHydrant');
Ext.require('Sdis.Remocra.store.TypeHydrantAnomalie');
Ext.require('Sdis.Remocra.store.TypeHydrantDiametre');
Ext.require('Sdis.Remocra.store.TypeHydrantDomaine');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureTous');
Ext.require('Sdis.Remocra.store.TypeHydrantVolConstate');
Ext.require('Sdis.Remocra.store.TypeHydrantMarque');
Ext.require('Sdis.Remocra.store.TypeHydrantPositionnement');
Ext.require('Sdis.Remocra.store.TypeHydrantMateriau');
Ext.require('Sdis.Remocra.store.Utilisateur');
Ext.require('Sdis.Remocra.model.HydrantPena');
Ext.require('Sdis.Remocra.model.HydrantPibi');

Ext.require('Sdis.Remocra.features.hydrants.FichePena');
Ext.require('Sdis.Remocra.features.hydrants.FichePibi');

Ext.require('Sdis.Remocra.widget.SdisChoice');

Ext.define('Sdis.Remocra.controller.hydrant.Fiche', {
    extend: 'Ext.app.Controller',

    stores: ['Commune', 'Voie', 'Hydrant', 'TypeHydrant', 'TypeHydrantAnomalie', 'TypeHydrantDiametre', 'TypeHydrantDomaine',
             'TypeHydrantNature', 'TypeHydrantNatureTous', 'TypeHydrantVolConstate',
             'TypeHydrantMarque', 'Utilisateur', 'TypeHydrantPositionnement', 'TypeHydrantMateriau'],

    refs: [],
    CODE_CITERNE: 'CI_',
    CODE_CITERNE_FIXE: 'CI_FIXE',


    init: function() {

        this.bufferedCheckConstraint = Ext.Function.createBuffered(this.checkConstraint, 250);

        this.control({
            'hydrantFiche': {
                beforerender: this.onBeforeRenderFiche,
                afterrender: this.onAfterRenderFiche,
                close: this.onClose
            },
            'hydrantFiche #ok': {
                click: this.verifPression
            },
            'hydrantFiche tabpanel': {
                tabchange: this.onFicheTabChange
            },
            'hydrantFiche textfield[name=numeroInterne]': {
                change: this.doCheckDispo
            },
            'anomalie': {
                positionChange: this.onAnomalieChange,
                selectionChange: this.onAnomalieSelectionChange
            },
            'hydrantFiche combo[name=marque]': {
                change: this.onChangeMarque
            },
            'hydrantFiche button[name=btnPIAssocie]': {
                click: this.associerPibi
            },
            'hydrantFiche button[name=btnPIOpen]': {
                click: this.showPibiAssocie
            },
            'hydrantFiche button[name=deletePhoto]': {
                click: this.onDeletePhoto
            },
            'hydrantFiche combo[name=nature]': {
                select: this.onChangeNature
            },
            'hydrantFiche combo[name=commune]': {
                select: this.onChangeCommune
            },
            'hydrantFiche checkbox[name=allAnomalie]': {
                change: this.checkboxChange
            },
            'hydrantFiche checkbox[name=hbe]': {
                change: this.checkboxChange
            },
            'hydrantFiche combo[name=diametre]': {
                change: this.onChangeDiametre
            },
            'hydrantFiche numberfield[name=debit]': {
                change: function(field) {
                    this.bufferedCheckConstraint(field.up('hydrantFiche'));
                    this.onChangePression(field);
                }
            },
            'hydrantFiche numberfield[name=debitMax]': {
                change: function(field) {
                    this.bufferedCheckConstraint(field.up('hydrantFiche'));
                }
            },
            'hydrantFiche numberfield[name=pression]': {
                change: function(field) {
                    this.bufferedCheckConstraint(field.up('hydrantFiche'));
                }
            },
            'hydrantFiche numberfield[name=pressionDyn]': {
                change: function(field) {
                    this.bufferedCheckConstraint(field.up('hydrantFiche'));
                    this.onChangePression(field);
                }
            },
            'hydrantFiche image': {
                afterrender: function(image) {
                    image.imgEl.on({
                        load: function(evt, ele, opts) {
                            image.getEl().dom.target = "_blank";
                            image.getEl().dom.href = ele.src;
                        },
                        error: function(evt, ele, opts) {
                            image.getEl().dom.href = null;
                        }
                    });
                }
            }
        });
    },
    
    onChangePression: function(field) {

        var fiche = field.up('hydrantFiche'), pressiondyn = fiche.down('numberfield[name=pressionDyn]'), 
        debit = fiche.down('numberfield[name=debit]'), 
        erreurPression = fiche.down('displayfield[name=Error_msg]'),
        messagePression = fiche.down('displayfield[name=pressionDyn_msg]');
        erreurPression.hide();
        messagePression.show();
        if (pressiondyn.getValue() < 1 && pressiondyn.getValue() != null && debit.getValue() != null) {
            erreurPression.show();
            messagePression.hide();
        }
    },

    onClose: function(fiche) {
        if (fiche.ficheParente == null) {
            if (fiche.hydrant.feature) {
                fiche.hydrant.feature.destroy();
            }
            // On demande toujours le rafraichissement de la grille des hydrants
            // ainsi que de la carte
            this.getController('hydrant.Hydrant').refreshMap();
            this.getController('hydrant.Hydrant').updateHydrant();
        }
    },

    checkboxChange: function(checkbox) {
        var fiche = checkbox.up('hydrantFiche');
        this.doFilterAnomalie(fiche);
        this.calculateIndisponibilite(fiche);
    },

    doCheckDispo: function(cmp) {
        var fiche = cmp.up('hydrantFiche'), field = fiche.down('textfield[name=numeroInterne]'), nature = fiche.down('combo[name=nature]'), commune = fiche
                .down('combo[name=commune]');

        if (nature.isValid() && commune.isValid()) {
            var geometrie = fiche.hydrant.get('geometrie');

            Ext.Ajax.request({
                baseParams: {
                    manage500: true
                },
                url: 'hydrants/checkdispo',
                params: {
                    id: fiche.hydrant.getId(),
                    nature: nature.getValue(),
                    commune: commune.getValue(),
                    num: field.getValue(),
                    geometrie: geometrie
                },
                success: function(response) {
                    field.clearInvalid();
                },
                failure: function(response) {
                    var data = Ext.decode(response.responseText);
                    field.markInvalid(data.message);
                }
            });
        }
    },

    showFiche: function(hydrant, controle) {
        var xtype = null;
        if (hydrant != null) {
            switch (hydrant.get('code')) {
            case 'PENA':
                xtype = 'hydrant.fichepena';
                break;
            case 'PIBI':
                xtype = 'hydrant.fichepibi';
                break;
            }
            if (xtype != null) {
                typeSaisie = 'LECT';
                if (hydrant.phantom) {
                    typeSaisie = 'CREA';
                } else if(controle == false) {
                  if (Sdis.Remocra.Rights.hasRight('HYDRANTS_C')) {
                     typeSaisie = 'NOCTRL';
                  }
                } else {
                    if (hydrant.get('dateRecep') != null) {
                        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_RECONNAISSANCE_C')) {
                            typeSaisie = 'RECO';
                        }
                        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_CONTROLE_C')) {
                            typeSaisie = 'CTRL';
                        }
                    } else {
                        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_RECEPTION_C')) {
                            typeSaisie = 'RECEP';
                        }
                    }
                }
                Ext.widget(xtype, {
                    hydrant: hydrant,
                    typeSaisie: typeSaisie
                }).show();
            } else {
                console.warn('xtype is null', hydrant);
            }
        } else {
            console.warn('hydrant is null');
        }
    },

    onChangeTypeSaisie: function(fiche, typeSaisie, initial) {
        var form = fiche.down('form').getForm();
        var nature = fiche.down('combo[name=nature]').getValue();
        fiche.typeSaisie = typeSaisie;
        if (initial !== true) {
            Ext.defer(function() {
                fiche.down('anomalie').setInfo(fiche.typeSaisie, nature);
                this.doFilterAnomalie(fiche);
                this.calculateIndisponibilite(fiche);
            }, 100, this);
        }
        if (typeSaisie == 'LECT') {
            fiche.down('#ok').hide();
            fiche.down('#close').setText('Fermer');
            fiche.down('checkbox[name=allAnomalie]').setValue(true);
            fiche.down('checkbox[name=allAnomalie]').hide();
            Ext.Array.each(fiche.query('filefield'), function(item) {
                item.hide();
            });
            this.setReadOnly(fiche.down('form'));
        } else if (typeSaisie == 'NOCTRL') {
            this.setReadOnly(fiche.down('#identification'));
            this.setReadOnly(fiche.down('#tracabilite'));
            if(fiche.hydrant.get('code') != 'PENA'){
              this.setReadOnly(fiche.down('#verification'));
            }
            fiche.down('checkbox[name=allAnomalie]').setValue(true);
            fiche.down('checkbox[name=allAnomalie]').hide();
        }

        fiche.setTitle('Fiche ' + fiche.hydrant.get('code') + (typeSaisie == 'NOCTRL' ? '':( ' - ' + Ext.getStore('TypeHydrantSaisie').findRecord('code', typeSaisie).get('nom'))));

        // Gestion date contrôle en fonction du stype de saisie
        var dateSaisie = form.findField('dateSaisie');
        switch (fiche.typeSaisie) {
        case 'RECO':
            dateSaisie.setValue(fiche.hydrant.get('dateReco'));
            break;
        case 'CTRL':
            dateSaisie.setValue(fiche.hydrant.get('dateContr'));
            break;
        }
        if(!dateSaisie.getValue()) {
            dateSaisie.setValue(new Date());
        }
    },

    setReadOnly: function(component) {
        if (Ext.isFunction(component.cascade)) {
            component.cascade(function(item) {
                if (Ext.isFunction(item.setReadOnly)) {
                    item.setReadOnly(true);
                }
                if (Ext.isFunction(item.hideTrigger)) {
                    item.hideTrigger();
                }
                if (item.getToolbar) {
                    toolbar = item.getToolbar();
                }
                if (item.isXType('grid')) {
                    Ext.Array.each(item.plugins, function(plugin) {
                        plugin.beforeEdit = function() {
                            return false;
                        };
                    });
                    Ext.Array.each(item.columns, function(column) {
                        if (column.isXType('actioncolumn')) {
                            column.destroy();
                        }
                    }, null, true);
                }
            });
        }
    },

    onChangeCommune: function(combo, records) {
        var fiche = combo.up('hydrantFiche'),
            voie = fiche.down('combo[name=voie]'), voie2 = fiche.down('combo[name=voie2]'),
            commune = records[0], filters = [], wkt = fiche.hydrant.get('geometrie');

        this.doCheckDispo(combo);
        
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
        
        // Mise à jour des filtres des deux listes de voies
        if (voie != null) {
            voie.store.clearFilter(true);
            voie.store.filter(filters);
        }
        if (voie2 != null) {
            voie2.store.clearFilter(true);
            voie2.store.filter(filters);
        }
    },

    onChangeNature: function(combo, records, initial) {
        var fiche = combo.up('hydrantFiche'), nature = records[0];
        var elementMco = fiche.down('#elementMco');
        if(elementMco){
            if (nature.get('code')=="RI"){
                elementMco.setVisible(false);
            }else{
                elementMco.setVisible(true);
            }
        }
      
        if (fiche.hydrant.get('code') == 'PENA') {
            var isFixe, citerneTab = fiche.down('fieldset[xtype=hydrant.citerne]').ownerCt, cboPosition = citerneTab.down('combo[name=positionnement]'), numCapa = fiche
                    .down('numberfield[name=capacite]');
            if (nature.get('code').substring(0, 3) == this.CODE_CITERNE || nature.get('code')=="RI") {
                citerneTab.tab.show();
                isFixe = nature.get('code') == this.CODE_CITERNE_FIXE;
                if (fiche.isUpdating) {
                    citerneTab.tab.addCls('fiche-tab-notread');
                    numCapa.setMinValue(isFixe ? 0 : 20);
                    numCapa.setMaxValue(isFixe ? 1000 : 9999);
                    numCapa.isValid();
                }

                if (isFixe) {
                    cboPosition.enable();
                    cboPosition.show();
                } else {
                    cboPosition.disable();
                    cboPosition.hide();
                }
            } else {
                citerneTab.tab.hide();
                citerneTab.tab.removeCls('fiche-tab-notread');
                citerneTab.tab.removeCls('fiche-tab-reading');
                citerneTab.tab.removeCls('fiche-tab-read');
            }
        } else {
            Ext.getStore('TypeHydrantDiametre').clearFilter(true);
            Ext.getStore('TypeHydrantDiametre').filter({
                filterFn: function(diametre) {
                    return diametre.hasNature(nature.getId());
                }
            });
            if (fiche.hydrant.get('diametre') != null) {
                fiche.down('combo[name=diametre]').select(fiche.hydrant.get('diametre'));
            } else {
                fiche.down('combo[name=diametre]').select(null);
            }
            this.manageVerifTabVisibility(fiche, nature);
        }
        if (initial!==true) {
            Ext.defer(function() {
                this.checkIfAllTabRead(fiche);
                if(HYDRANT_SYMBOLOGIE === '83') {
                   this.doCheckDispo(combo);
                }
                fiche.down('anomalie').setInfo(fiche.typeSaisie, nature.getId());
                this.doFilterAnomalie(fiche);
                this.calculateIndisponibilite(fiche);
            }, 100, this);
        }
    },

    onBeforeRenderFiche: function(fiche) {
        if (!Sdis.Remocra.Rights.hasRight('HYDRANTS_MCO_C')) {
            fiche.down('#tabMCO').tab.hide();
        }
        if (!Sdis.Remocra.Rights.hasRight('HYDRANTS_NUMEROTATION_C')) {
            if (Sdis.Remocra.Rights.hasRight('HYDRANTS_NUMEROTATION_R')) {
                fiche.down('#numeroInterne').setReadOnly(true);
            } else {
                fiche.down('#numeroInterne').hide();
            }
        }
    },

    onAfterRenderFiche: function(fiche) {
        if (fiche.hydrant) {
            var form = fiche.down('form').getForm(), currentCode = fiche.hydrant.get('code'), nature = fiche.hydrant.get('nature');
            fiche.isUpdating = (fiche.typeSaisie != 'LECT' && fiche.typeSaisie != 'CREA' && fiche.typeSaisie != 'NOCTRL');
            // Si ya une commune, on la charge dans le store de la combo
            var commune = Ext.create('Sdis.Remocra.model.Commune', fiche.hydrant.raw.commune);
            var cboCommune = form.findField('commune');
            if (!commune.phantom) {
                cboCommune.getStore().add(commune);
                this.onChangeCommune(cboCommune, [commune]);
            }
            
            if (fiche.hydrant.phantom) {
                Sdis.Remocra.model.Commune.loadByXY(fiche.hydrant.feature.geometry.x, fiche.hydrant.feature.geometry.y, fiche.hydrant.srid, {
                    scope: this,
                    success: function(record) {
                        if (cboCommune.getStore().getById(record.getId()) != null) {
                            cboCommune.getStore().add(record);
                        }
                        fiche.hydrant.set('commune', record);
                        cboCommune.select(record);
                        this.onChangeCommune(cboCommune, [record]);
                    },
                    failure: function() {
                        Sdis.Remocra.util.Msg.msg('Hydrant', 'La commune n\'a pas été trouvée.', 3);
                    }
                });
            }

            this.onChangeTypeSaisie(fiche, fiche.typeSaisie, true);

            // Pour un nouveau pena, on le définir à "citerne fixe" par défaut
            if (currentCode == "PENA") {
                if (fiche.hydrant.phantom) {
                    var defaultCodePena = this.getDefaultCodePena();
                    if(defaultCodePena !== null){
                       fiche.hydrant.set('nature', Ext.getStore('TypeHydrantNature').findRecord('code', defaultCodePena).getId());
                    }
                    // on recherche les coords DFCI
                    Ext.Ajax.request({
                        url: Sdis.Remocra.util.Util.withBaseUrl('../hydrantspena/dfci'),
                        params: {
                            wkt: "POINT(" + fiche.hydrant.feature.geometry.x + " " + fiche.hydrant.feature.geometry.y + ")",
                            srid: fiche.hydrant.srid
                        },
                        success: function(response) {
                            var result = Ext.decode(response.responseText);
                            fiche.hydrant.set('coordDFCI', result.message);
                            fiche.down('displayfield[name=coordDFCI]').setValue(result.message);
                        }
                    });
                } else {
                    if (fiche.hydrant.get('nature') != null) {
                        var cbo = fiche.down('combo[name=nature]');
                        this.onChangeNature(cbo, Ext.Array.from(cbo.getStore().getById(fiche.hydrant.get('nature'))), true);
                    }
                }
                if (!Ext.isEmpty(fiche.hydrant.get('pibiAssocie'))) {
                    fiche.down('button[name=btnPIOpen]').show();
                    fiche.down('button[name=btnPIAssocie]').hide();

                }
            }
            form.findField('_info').setValue(null);

            if (fiche.isUpdating) {
                fiche.down('#ok').setDisabled(true);
                Ext.Array.each(fiche.query('tab'), function(item, index) {
                    if (!item.disabled) {
                        item.addCls(index == 0 ? 'fiche-tab-reading' : 'fiche-tab-notread');
                    }
                });
            }

            if(fiche.typeSaisie == 'LECT' || fiche.isUpdating || fiche.typeSaisie == 'NOCTRL') {
                this.traitePhoto(fiche, fiche.hydrant);
            }

         // Documents : on ajoute des liens s'il en existe (un conteneur est
            // prévu à cet effet dans FileUploadPanel)
            var documentsP = fiche.queryById('documents');
            var documents = fiche.hydrant.hydrantDocuments();
            if(documentsP != null && documents != null) {
                documentsP.addDocuments(documents, {
                    urlStart: Sdis.Remocra.model.Hydrant.proxy.url + '/document',
                    method: 'DELETE',
                    scope: this,
                    failure: function() {
                        Ext.MessageBox.show({
                            title: 'Documents de la fiche',
                            msg: 'Une erreur est survenue lors de la suppression d\'un document.',
                            buttons: Ext.Msg.OK,
                            icon: Ext.MessageBox.ERROR
                         });
                    }
                });
            }
            // Pour marquer les champs obligatoires
            form.isValid();

            form.loadRecord(fiche.hydrant);

            // Cas spéciaux des X et Y
            var wkt = fiche.hydrant.get('geometrie');
            if (!Ext.isEmpty(wkt)) {
                var wktFormat = new OpenLayers.Format.WKT();
                var result = wktFormat.read(wkt);
                var geom = result.geometry;
                geom.transform('EPSG:2154', Sdis.Remocra.widget.map.EPSG4326);

                var x = Sdis.Remocra.util.Util.getFormattedCoord('x', geom.x, COORDONNEES_FORMAT_AFFICHAGE, 5);
                var y = Sdis.Remocra.util.Util.getFormattedCoord('y', geom.y, COORDONNEES_FORMAT_AFFICHAGE, 5);

                form.findField('x').setValue(x);
                form.findField('y').setValue(y);
            }

            if (fiche.typeSaisie != 'LECT' && fiche.typeSaisie != 'NOCTRL') {
                form.findField('CISCommune').setValue(REMOCRA_USR_ORGANISME);
            }
            
            // On "bind" le store des anomalies
            var cmpAnomalie = fiche.down('anomalie');
            Ext.defer(function() {
                var store = Ext.create('Ext.data.Store', {
                    model: 'Sdis.Remocra.model.TypeHydrantAnomalie',
                    autoLoad: false,
                    remoteSort: false,
                    remotefilter: false
                });
                store.add(Ext.getStore('TypeHydrantAnomalie').getRange());
                store.each(function(record) {
                    if (record.dirty) {
                        record.reject();
                    }
                });
                cmpAnomalie.bindStore(store, true);
                cmpAnomalie.setInfo(fiche.typeSaisie, nature);
                anomalies=[];
                /*on supprime les anomalies avec critère null sauf indisponibilite temporaire
                (elles seront recalculées coté client pour éviter le problème lié au basculement des règles de calcul(debit debitMax)*/
                fiche.hydrant.anomalies().each(function(record){
                   if(record.get('critere')!= null || record.get('code')==='INDISPONIBILITE_TEMP') {
                        anomalies.push(record);
                   }
                });
                cmpAnomalie.setSelected(anomalies);
                this.doFilterAnomalie(fiche);
                if (Ext.isEmpty(fiche.hydrant.get('dispoAdmin'))) {
                    this.calculateIndisponibilite(fiche);
                }
                this.updateUiDispo(fiche);
            }, 150, this);

            // On charge l'historique débit, pression, uniquement pour les PIBI
            if(typeSaisie !== 'CREA' && currentCode == 'PIBI') {
                 Ext.Ajax.request({
                        scope : this,
                        url: Sdis.Remocra.util.Util.withBaseUrl('../hydrantspibi/histoverifhydrauforchart/'+fiche.hydrant.get('id')),
                        method :'GET',
                        callback: function(param, success, response) {
                            var res = Ext.decode(response.responseText);
                            var chartDebit = Ext.getCmp('chartDebit');
                            var gridDebit = Ext.getCmp('gridDebit');
                            if(success && res.data != null && res.data.length !== 0) {
                                var i = 0;
                                var j = 0;
                                var dataDebit = [];
                                var dataDebitForGrid = [];
                                if (res.data.length != 1) {
                                    for (i; i< res.data.length; i++){
                                       dataDebit.push({'dateOp':(res.data[i])[0], 'debit': (res.data[i])[1]});
                                    }
                                    chartDebit.store.removeAll();
                                    chartDebit.store.add(dataDebit);
                                } else {
                                      var summary = gridDebit.getView().features[0];
                                      summary.disable();
                                      chartDebit.setVisible(false);
                                      form.findField('dateTerrain').setValue('');
                                      form.findField('separator').setVisible(true);
                                }

                                for (j; j< res.data.length; j++){
                                   dataDebitForGrid.push({'dateOp':(res.data[j])[0], 'debit': (res.data[j])[1], 'debitMax': (res.data[j])[2], 'pressionStat': (res.data[j])[3],
                                    'pressionDyn': (res.data[j])[4], 'pressionDynDeb': (res.data[j])[5]});
                                }

                                 gridDebit.store.removeAll();
                                 gridDebit.store.add(dataDebitForGrid);

                            }else {
                                  gridDebit.setVisible(false);
                                  chartDebit.setVisible(false);
                                  form.findField('dateTerrain').setVisible(false);
                                  Ext.getCmp('verifHydrauliquePibi').layout.columns = 1;
                                  Ext.getCmp('verifHydrauliquePibi').doLayout();
                            }
                        }
                    });
            }
            if (nature) {
                if(currentCode == 'PIBI'){
                    Ext.getStore('TypeHydrantDiametre').clearFilter(true);
                    Ext.getStore('TypeHydrantDiametre').filter({
                        filterFn: function(diametre) {
                            return diametre.hasNature(nature);
                        }
                    });
                }
                this.manageVerifTabVisibility(fiche, Ext.getStore('TypeHydrantNature').findRecord('id', nature));

            }

        }
    },

    doFilterAnomalie: function(fiche) {
        var form = fiche.down('form').getForm(), tabPtAnomalie, nature, anomalie, store, hbeCheckbox, allAnomalie;

        tabPtAnomalie = fiche.down('container[pointAttention=true]');
        nature = form.findField('nature').getValue();
        anomalie = fiche.down('anomalie');
        storeAnomalie = anomalie.getStore();

        if (Ext.isEmpty(nature)) {
            tabPtAnomalie.tab.hide();
            if (storeAnomalie != null) {
                storeAnomalie.filter('id', -1);
            }
        } else {
            hbeCheckbox = form.findField('hbe');
            allAnomalie = fiche.down('checkbox[name=allAnomalie]').getValue();
            tabPtAnomalie.tab.show();
            if (storeAnomalie != null) {
                storeAnomalie.clearFilter(true);
                storeAnomalie.filter({
                    filterFn: function(anomalie) {
                        var anomalie_nature = anomalie.getInfoByNature(nature);
                        if (anomalie_nature != null) {
                            if (anomalie_nature.isHBEOnly()) {
                                if (hbeCheckbox == null || !hbeCheckbox.getValue()) {
                                    return false;
                                }
                            }
                            if (!allAnomalie && anomalie_nature.getSaisieByCode(fiche.typeSaisie) == null) {
                                return false;
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    },

    calculateIndisponibilite: function(fiche) {
        var hydrant = fiche.hydrant, cmpAnomalie = fiche.down('anomalie'), nature = fiche.down('combo[name=nature]'), anomalies, nbAdmin = 0, nbTerr = 0, nbHBE = 0, info;
        if (hydrant != null && nature != null && nature.isValid()) {
            nature = nature.getValue();
            anomalies = cmpAnomalie.getSelected();
            var hasNC = false;
            Ext.Array.each(anomalies, function(anomalie) {
                info = anomalie.getInfoByNature(nature);
                // la nature peu avoir changé, et donc l'anomalie peut ne pas
                // avoir d'existence pour la nouvelle nature
                if (info != null) {
                    if (Ext.isNumeric(info.get('valIndispoTerrestre'))) {
                        if (!Ext.isEmpty(anomalie.get('code').match(/_NC$/))) {
                            hasNC = true;
                        }
                        nbTerr += info.get('valIndispoTerrestre');
                    }
                    /*
                     * if (Ext.isNumeric(info.get('valIndispoAdmin'))) { nbAdmin +=
                     * info.get('valIndispoAdmin'); }
                     */
                    if (Ext.isNumeric(info.get('valIndispoHbe'))) {
                        nbHBE += info.get('valIndispoHbe');
                    }
                }
            });

            // ------------------ REGLE METIER -------------------------
            // hydrant.set('dispoAdmin', nbAdmin >= 4 ? 'INDISPO' : 'DISPO');
            //Si l'hydrant est réceptionné on calcule l'indispo sinon par défaut il est indisponible
            if (hydrant.get('dateRecep') != null) {
            hydrant.set('dispoAdmin', null);
            hydrant.set('dispoTerrestre', nbTerr >= 5 ? 'INDISPO' : (hasNC ? 'NON_CONFORME' : 'DISPO'));
            hydrant.set('dispoHbe', nbHBE >= 5 ? 'INDISPO' : 'DISPO');
            // ---------------------------------------------------------
            this.updateUiDispo(fiche);

            } else {
             hydrant.set('dispoAdmin', null);
             hydrant.set('dispoTerrestre', 'INDISPO');
             hydrant.set('dispoHbe', 'INDISPO');
             // ---------------------------------------------------------
             this.updateUiDispo(fiche);
            }

        }

    },

    updateUiDispo: function(fiche) {
        var currentCode = fiche.hydrant.get('code'), hbeCheckbox = fiche.down('checkbox[name=hbe]'), isHBE = false;
        isHbe = (hbeCheckbox != null && hbeCheckbox.getValue());
        this.processDispo(fiche.down('displayfield[name=dispo-op]'), fiche.hydrant.get('dispoTerrestre'), true);
        // this.processDispo(fiche.down('displayfield[name=dispo-admin]'),
        // fiche.hydrant.get('dispoAdmin'), true);
        this.processDispo(fiche.down('displayfield[name=dispo-hbe]'), fiche.hydrant.get('dispoHbe'), currentCode == 'PENA' && isHbe);

    },

    processDispo: function(field, value, show) {
        if (field) {
            if (show) {
                field.show();
                field.removeCls(['dispo','indispo','non_conforme']);
                if (value) {
                    field.addCls(Ext.util.Format.lowercase(value));
                }
            } else {
                field.hide();
            }
        }
    },

    onDeletePhoto: function(btn) {
        var fiche = btn.up('hydrantFiche'), rec = fiche.down('form[name=fiche]').getForm().getRecord();
        fiche.down('hidden[name=photo]').setValue(null);
        rec.photos().removeAt(0);
        this.traitePhoto(fiche, rec);
    },

    traitePhoto: function(fiche, record) {
        var photo = record.getPhoto();
        if (photo != null) {
            fiche.down('image').setSrc("telechargement/show/" + photo.get('code'));
        } else {
            fiche.down('image').setSrc(null);
        }
    },
    
    verifPression: function(button) {
        var fiche = button.up('window'), form = fiche.down('form[name=fiche]').getForm();
        var nature = fiche.down('combo[name=nature]').getValue();
        if(nature){
            var natureCode = Ext.getStore('TypeHydrantNature').findRecord('id', nature).get('code');
        }
        if (this.natureHasDebitPression(natureCode) && form.findField('Error_msg').isVisible()) {
            Ext.Msg.show({
                title: fiche.title,
                msg: 'La pression dynamique à 60 m³ ne peut pas être inférieure à 1.'+'<br/>Veuillez vérifier votre saisie.',
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.WARNING
              });
        } else {
            this.validFicheHydrant(button);
        }
    },

    validFicheHydrant: function(button) {
        // Verification si mode consultation
        var fiche = button.up('window');
        var form = fiche.down('form[name=fiche]').getForm();
        // On formate pour la comparaison au jour près (on exclut l'heure)
        var dateSaisie = Ext.Date.format(form.findField('dateSaisie').getValue(),'d/m/Y');
        var dateControle = null;
        var needConfirmation = false;
        switch (fiche.typeSaisie) {
            case 'RECO':
              dateControle = Ext.Date.format(fiche.hydrant.get('dateReco'), 'd/m/Y');
              needConfirmation = true;
              break;
            case 'CTRL':
              dateControle = Ext.Date.format(fiche.hydrant.get('dateContr'), 'd/m/Y');
              needConfirmation = true;
              break;
        }
        if(needConfirmation) {
            if(dateSaisie == dateControle) {
                Ext.Msg.confirm(fiche.title, 'Vous n\'avez pas modifié la date de visite.<br/>Confirmez-vous qu\'il s\'agit bien d\'une nouvelle saisie ?', function(buttonId) {
                    if (buttonId == 'yes') {
                        this.saveFicheHydrant(button);
                    }
                }, this);
            }else {
                this.saveFicheHydrant(button);
            }
        }else {
            this.saveFicheHydrant(button);
        }
    },

    saveFicheHydrant : function(button) {
        var x, y, point, fiche = button.up('window'), form = fiche.down('form[name=fiche]').getForm();
        var hydrant = form.getRecord();
        var msgErrorField = fiche.down('displayfield[name=errorMsg]');
        msgErrorField.hide();

        if (form.isValid() ) {
            form.updateRecord();

            var hydrantAssocie = fiche.hydrant.get('pibiAssocie');

            if (hydrantAssocie != null && !hydrantAssocie.isModel) {
                msgErrorField.setValue('Un PIBI est associé, vous devez consulter sa fiche');
                msgErrorField.show();
                fiche.down('fieldset[xtype=hydrant.citerne]').ownerCt.show();
                return;
            }

            // Cas spécial de la géométrie
            hydrant.set('geometrie', fiche.hydrant.get('geometrie'));

            // Cas des anomalies
            var selected = fiche.down('anomalie').getSelected();
            hydrant.anomalies().removeAll();
            hydrant.anomalies().add(selected);
            // on set a dirty sinon si on ne change que les anomalies l'hydrant n'est pas condidéré comme dirty Anomalie #35510
            hydrant.setDirty(true);
            // Cas de la date, on récupère et on affecte à la bonne date en
            // fonction du type de saisie
            if (fiche.ficheParente == null) {
                var dateSaisie = form.findField('dateSaisie').getValue(), dateField = '';
                if (dateSaisie == null) {
                    dateSaisie = new Date();
                }
                switch (fiche.typeSaisie) {
                case 'RECEP':
                    dateField = 'dateRecep';
                    break;
                case 'RECO':
                    dateField = 'dateReco';
                    break;
                case 'CTRL':
                    dateField = 'dateContr';
                    break;
                }
                hydrant.set(dateField, dateSaisie);
                if (hydrantAssocie != null) {
                    hydrantAssocie.set(dateField, dateSaisie);
                }
            }
            // On recalcule l'indisponibilité après l'affectation de la date réception
            this.calculateIndisponibilite(fiche);

            // Cas du fichier ...
            var formFiles = fiche.down('form[name=fiche]').down('filefield'), isNew = hydrant.phantom;

            if (!Ext.isEmpty(formFiles.getValue()) || hydrant.dirty || (hydrantAssocie != null && hydrantAssocie.dirty)) {
                if (fiche.ficheParente == null) {
                    var data = hydrant.getProxy().getWriter().getRecordData(hydrant);
                    var params = {};
                    params.hydrant = Ext.encode(data);
                    if (hydrantAssocie != null) {
                        var dataAssocie = hydrantAssocie.getProxy().getWriter().getRecordData(hydrantAssocie);
                        params.associe = Ext.encode(dataAssocie);
                        params.idpibi = hydrantAssocie.getId();
                    }
                    formFiles.up().submit({
                        scope: this,
                        params: params,
                        url: hydrant.getProxy().url + (hydrant.phantom ? '' : '/' + hydrant.getId()),
                        success: function(form, operation) {
                            Sdis.Remocra.util.Msg.msg("Point d'eau", isNew ? "Point d'eau créé avec le code "
                                    + operation.result.data.numero : "Point d'eau " + operation.result.data.numero
                                    + " mis à jour.");
                            fiche.close();
                        },
                        failure: function(form, action) {
                            var msg = "Une erreur est survenue lors de la création.<br/>";
                            if (action && action.result && action.result.message && action.result.message == "hydrant_numero_key") {
                                msg = 'Un point d\'eau ayant le même numéro existe déjà.<br/>';
                            }
                            Ext.Msg.show({
                                title: fiche.title,
                                msg: msg,
                                buttons: Ext.Msg.OK,
                                icon: Ext.Msg.WARNING
                            });
                            fiche.down('button[name=btnPIOpen]').hide();
                            fiche.down('button[name=btnPIAssocie]').show();
                        }
                    });
                } else {
                    fiche.ficheParente.hydrant.set('pibiAssocie', hydrant);
                    fiche.close();
                }
            } else {
                fiche.close();
            }
        } else {
            var invalid = form.getFields().filterBy(function(field) {
                return !field.validate();
            });
            msgErrorField.setValue('Des erreurs sont présentes dans le formulaire.');
            msgErrorField.show();
            console.warn('il faut récupérer et afficher les erreurs ...', invalid);
        }
    },

    onFicheTabChange: function(tabPanel, newPanel, oldPanel) {
        var fiche = tabPanel.up('hydrantFiche');
        if (fiche.isUpdating) {
            if (oldPanel.pointAttention != true && !oldPanel.disabled) {
                oldPanel.tab.removeCls('fiche-tab-notread');
                oldPanel.tab.removeCls('fiche-tab-reading');
                oldPanel.tab.addCls('fiche-tab-read');
            }
            if (!newPanel.tab.hasCls('fiche-tab-read') && !newPanel.disabled) {
                newPanel.tab.removeCls('fiche-tab-notread');
                newPanel.tab.addCls('fiche-tab-reading');
            }
            this.checkIfAllTabRead(fiche);
        }
    },

    onAnomalieChange: function(cmp, position, nbPosition, isLastPosition) {
        var fiche = cmp.up('hydrantFiche');
        if (fiche.isUpdating && isLastPosition) {
            var tabParent = cmp.findParentBy(function(c) {
                return c.tab != null;
            });
            if (tabParent) {
                tabParent.tab.removeCls('fiche-tab-notread');
                tabParent.tab.removeCls('fiche-tab-reading');
                tabParent.tab.addCls('fiche-tab-read');
            }
            this.checkIfAllTabRead(fiche);
        }
    },

    onAnomalieSelectionChange: function(chk, records) {
        this.calculateIndisponibilite(chk.up('hydrantFiche'));
    },

    checkIfAllTabRead: function(fiche) {
        var allRead = true;
        if (fiche.isUpdating) {
            allRead = Ext.Array.every(fiche.query('tab'), function(item) {
                return item.hasCls('fiche-tab-read') || !item.isVisible() || item.disabled;
            });
        }
        fiche.down('#ok').setDisabled(!allRead);
    },

    onChangeMarque: function(combo, newValue, oldValue, opt) {
        var rec = combo.getStore().getById(newValue);
        var cboModele = combo.up('container').query('combo[name=modele]')[0];
        cboModele.setDisabled(rec == null);
        if (rec) {
            cboModele.bindStore(rec.modeles());
            cboModele.setValue(null);
        }
    },

    onChangeDiametre: function(combo, newValue, oldValue, opt) {
        var fiche = combo.up('hydrantFiche'), rec = combo.getStore().getById(newValue);
        if (rec == null) {
            return null;
        }
        fiche.down('displayfield[name=_info]').setValue('Diamètre : ' + rec.get('nom'));
        this.checkConstraint(fiche);
    },

    // CONTRAINTE METIER
    checkConstraint: function(fiche) {
        if (fiche.hydrant.get('code') == 'PIBI') {
            var nature = fiche.down('combo[name=nature]').getValue();
            var natureCode = Ext.getStore('TypeHydrantNature').findRecord('id', nature).get('code');
           if (!this.natureHasDebitPression(natureCode)) {
                return;
            }
            var cboDiametre = fiche.down('combo[name=diametre]'), diametre = cboDiametre.getStore().getById(cboDiametre.getValue());
            var comp, valeur, result, currentAnomalie, anomalie, debit, debitMax;
            Ext.Object.each(this.cfgDebit, function(type) {
                comp = fiche.down('numberfield[name=' + type + ']');
                if (comp) {
                    anomalie = null;
                    currentAnomalie = comp.anomalie;
                    result = null;
                    debit = fiche.down('numberfield[name=debit]');
                    debitMax = fiche.down('numberfield[name=debitMax]');
                    valeur = (comp == debit && debitMax.getValue() != null) ? debitMax.getValue(): comp.getValue();
                      if (debitMax.getValue() != null) {
                           fiche.down('displayfield[name=debitMax_msg]').setValue(fiche.down('displayfield[name=debit_msg]').getValue());
                           fiche.down('displayfield[name=debitMax_msg]').show();
                           fiche.down('displayfield[name=debit_msg]').hide();
                       } else {
                           fiche.down('displayfield[name=debitMax_msg]').hide();
                           fiche.down('displayfield[name=debit_msg]').show();
                       }
                      if (valeur != null) {
                          if (diametre != null) {
                            result = this.getErrorConstraint(type, diametre.get('code'), valeur);
                            if (result != null) {
                                anomalie = this.getStore('TypeHydrantAnomalie').findRecord('code', result, 0, false, true, true);
                                if (anomalie) {
                                    result = anomalie.get('nom');
                                    comp.anomalie = anomalie;
                                } else {
                                    console.warn('Anomalie inexistante, code = ', result);
                                }
                            }
                        }
                    }

                    if (currentAnomalie != null) {
                        fiche.down('anomalie').grid.getSelectionModel().deselect(currentAnomalie);
                        this.calculateIndisponibilite(fiche);
                    }
                    if (anomalie != null) {
                        fiche.down('anomalie').grid.getSelectionModel().select(anomalie,true);
                        this.calculateIndisponibilite(fiche);
                    }
                    comp = fiche.down('displayfield[name=' + type + '_msg]');
                    if (comp) {
                        comp.setValue(result);
                    }
                }
           }, this);
        }
    },
    getErrorConstraint: function(type, diam, valeur) {
        var vals = this.cfgDebit[type];
        if (!vals) {
            return null;
        }
        var valDiam = vals[diam];
        if (!valDiam) {
            return null;
        }

        var result = Ext.Array.each(valDiam, function(item, index) {
            item = Ext.applyIf(item, {
                min: 0,
                minIncl: true,
                max: Number.POSITIVE_INFINITY,
                maxIncl: false
            });
            if (valeur < item.min || valeur > item.max) {
                return true;
            }
            if (!item.minIncl && valeur == item.min) {
                return true;
            }
            if (!item.maxIncl && valeur == item.max) {
                return true;
            }
            return false;
        });
        if (result !== true) {
            return valDiam[result].codeErreur;
        }
        return null;
    },

    // CONFIGURATION DES DEBITS ET PRESSION

    /**
     * Pour chaque "données", il y a 5 paramètres possibles :
     * <ul>
     * <li> min : valeur minimale de l'interval (0 par défaut)</li>
     * <li> minIncl : valeur minimal incluse (true par défaut)</li>
     * <li> max : valeur maximale de l'interval (POSITIVE_INFINITY par défaut)</li>
     * <li> maxIncl : valeur maximal incluse (false par défaut)</li>
     * <li> codeErreur</li>
     * </ul>
     */

    cfgDebit: {
        'debit': {
            'DIAM80': [{
                min: 0,
                max: 30,
                codeErreur: 'DEBIT_INSUFF'
            },{
                min: 90,
                minIncl: false,
                codeErreur: 'DEBIT_TROP_ELEVE'
            }],
            'DIAM100': [{
                min: 0,
                max: 30,
                codeErreur: 'DEBIT_INSUFF'
            },{
                min: 30,
                max: 60,
                codeErreur: 'DEBIT_INSUFF_NC'
            },{
                min: 130,
                minIncl: false,
                codeErreur: 'DEBIT_TROP_ELEVE'
            }],
            'DIAM150': [{
                min: 0,
                max: 60,
                codeErreur: 'DEBIT_INSUFF'
            },{
                min: 60,
                max: 120,
                codeErreur: 'DEBIT_INSUFF_NC'
            },{
                min: 150,
                minIncl: false,
                codeErreur: 'DEBIT_TROP_ELEVE'
            }]
        },
        'pression': {
            'DIAM80': [{
                min: 0,
                max: 1,
                codeErreur: 'PRESSION_INSUFF'
            },{
                min: 16,
                minIncl: false,
                codeErreur: 'PRESSION_TROP_ELEVEE'
            }],
            'DIAM100': [{
                min: 0,
                max: 1,
                codeErreur: 'PRESSION_INSUFF'
            },{
                min: 16,
                minIncl: false,
                codeErreur: 'PRESSION_TROP_ELEVEE'
            }],
            'DIAM150': [{
                min: 0,
                max: 1,
                codeErreur: 'PRESSION_INSUFF'
            },{
                min: 16,
                minIncl: false,
                codeErreur: 'PRESSION_TROP_ELEVEE'
            }]
        },
        'pressionDyn': {
            'DIAM80': [{
                min: 0,
                max: 1,
                codeErreur: 'PRESSION_DYN_INSUFF'
            },{
                min: 16,
                minIncl: false,
                codeErreur: 'PRESSION_DYN_TROP_ELEVEE'
            }],
            'DIAM100': [{
                min: 0,
                max: 1,
                codeErreur: 'PRESSION_DYN_INSUFF'
            },{
                min: 16,
                minIncl: false,
                codeErreur: 'PRESSION_DYN_TROP_ELEVEE'
            }],
            'DIAM150': [{
                min: 0,
                max: 1,
                codeErreur: 'PRESSION_DYN_INSUFF'
            },{
                min: 16,
                minIncl: false,
                codeErreur: 'PRESSION_DYN_TROP_ELEVEE'
            }]
        }
    },

    // GESTION DES PIBI ASSOCIES
    associerPibi: function(btn) {
        var fiche = btn.up('hydrantFiche');
        var nature = fiche.down('combo[name=nature]').getValue();
        var natureCode = Ext.getStore('TypeHydrantNature').findRecord('id', nature).get('code'); 
        Ext.widget('sdischoice', {
            name: 'choiceRole',
            title: 'Associer un PI / PA',
            width: 600,
            cboConfig: {
                store: {
                    remoteFilter: true,
                    model: 'Sdis.Remocra.model.HydrantPibi',
                    filters: [{
                        property: 'wkt',
                        value: fiche.hydrant.get('geometrie')
                    }, {
                        property: 'naturecode',
                        value : this.choixFiltre(natureCode)
                    }]
                },
                queryMode: 'remote',
                fieldLabel: 'PI / PA',
                displayField: 'numero',
                valueField: 'id'
            },
            listeners: {
                scope: this,
                valid: function(record) {
                    fiche.hydrant.set('pibiAssocie', record);
                    this.showPibiAssocie(btn);
                    fiche.down('button[name=btnPIOpen]').show();
                    fiche.down('button[name=btnPIAssocie]').hide();
                }
            }
        }).show();
    },

    showPibiAssocie: function(btn) {
        var fiche = btn.up('hydrantFiche');
        if (fiche.hydrant.get('pibiAssocie').isModel) {
            Ext.widget('hydrant.fichepibi', {
                hydrant: fiche.hydrant.get('pibiAssocie'),
                typeSaisie: fiche.typeSaisie,
                ficheParente: fiche,
                listeners: {
                    'close': function() {
                        Ext.defer(function() {
                            var currentPosition = this.currentPosition;
                            //on simule le changement de la page de composant anomalie pour le raffraichir
                            this.setPosition(this.currentPosition+1);
                            this.setPosition(this.currentPosition-1);
                            if (this.currentPosition!=currentPosition) {
                                this.setPosition(currentPosition);
                            }
                        }, 100, this);
                    },
                    scope: fiche.down('anomalie')
                }
            }).show();
        } else {
            Sdis.Remocra.model.HydrantPibi.load(fiche.hydrant.get('pibiAssocie').id, {
                scope: this,
                success: function(record) {
                    fiche.hydrant.set('pibiAssocie', record);
                    this.showPibiAssocie(btn);
                }
            });
        }
    },

    manageVerifTabVisibility: function(fiche, natureModel) {
        var tabVerif = fiche.down('#verification');
        if (tabVerif) {
             tabVerif.tab.setVisible(this.natureHasDebitPression(natureModel?natureModel.get('code'):null));
             if(fiche.typeSaisie == 'CTRL' || fiche.typeSaisie == 'RECEP' || fiche.typeSaisie =='CREA'){
               return;
             }else{
                this.setReadOnly(tabVerif);
             }

        }
    },
    natureHasDebitPression: function(natureCode) {
        return natureCode=='PI' || natureCode=='BI';
    },
    
   choixFiltre: function(natureCode){
       if(natureCode == 'RI'){
           return 'PA';
       }else {
           return 'PI,PA';
     }
   },

   getDefaultCodePena: function(){
       var codeCiterneFixe = Ext.getStore('TypeHydrantNature').findRecord('code', 'CIFIXE');
       if(codeCiterneFixe) {
            return codeCiterneFixe.get('code');
       } else {
            var defaultCodePena =  Ext.getStore('TypeHydrantNature').findRecord('typeHydrantId', 2);
            if(defaultCodePena){
                return defaultCodePena.get('code');
            }else {
                Sdis.Remocra.util.Msg.msg('Hydrant', 'Impossible de récupérer un code de PENA.');
                return null;
            }
       }
   }
});

 