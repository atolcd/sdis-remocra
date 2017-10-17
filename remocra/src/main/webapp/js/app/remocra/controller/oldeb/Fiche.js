Ext.require('Sdis.Remocra.model.TypeOldebCaracteristique');
Ext.require('Sdis.Remocra.model.OldebVisiteDocument');
Ext.require('Sdis.Remocra.model.FileUploadMulti');

Ext.require('Sdis.Remocra.store.Oldeb');
Ext.require('Sdis.Remocra.store.Voie');
Ext.require('Sdis.Remocra.store.TypeOldebResidence');
Ext.require('Sdis.Remocra.store.TypeOldebAnomalie');
Ext.require('Sdis.Remocra.store.TypeOldebAcces');
Ext.require('Sdis.Remocra.store.OldebLocataire');
Ext.require('Sdis.Remocra.store.OldebPropriete');
Ext.require('Sdis.Remocra.store.TypeOldebDebroussaillement');
Ext.require('Sdis.Remocra.store.OldebProprietaire');
Ext.require('Sdis.Remocra.store.OldebVisite');
Ext.require('Sdis.Remocra.store.TypeOldebAvis');
Ext.require('Sdis.Remocra.store.TypeOldebAction');
Ext.require('Sdis.Remocra.store.TypeOldebSuite');
Ext.require('Sdis.Remocra.store.OldebVisiteSuite');

Ext.require('Sdis.Remocra.features.oldebs.bloc.Proprietaires');
Ext.require('Sdis.Remocra.features.oldebs.FicheOldeb');

Ext.define('Sdis.Remocra.controller.oldeb.Fiche', {
    extend: 'Ext.app.Controller',

    stores: ['Oldeb', 'Voie', 'TypeOldebResidence', 'TypeOldebAnomalie', 'TypeOldebAcces', 'OldebLocataire', 'OldebPropriete',
            'TypeOldebDebroussaillement', 'OldebProprietaire', 'OldebVisite', 'TypeOldebAvis', 'TypeOldebAction', 'TypeOldebSuite',
            'OldebVisiteSuite' ],

    refs: [],

    init: function() {
        this.control({
            'oldebsFiche': {
                beforerender: this.onBeforeRenderFiche,
                afterrender: this.onAfterRenderFiche
            },
            'oldebsFiche checkbox[name=portailElectrique]': {
                change: this.onChangePortail
            },
            'oldebsFiche checkbox[name=organismeProprietaire]': {
                change: this.onChangeOrganismeProprietaire
            },
            'oldebsFiche checkbox[name=organismeLocataire]': {
                change: this.onChangeOrganismeLocataire
            },
            'oldebsFiche checkbox[name=location]': {
                change: this.onChangeLocation
            },
            'oldebsFiche numberfield[name=parcelle]': {
                change: this.checkDispo
            },
            'oldebsFiche #ok': {
                click: this.saveFiche
            },
            'oldebsFiche #print': {
                click: this.printFiche
            },
            'oldebsFiche #newProprio': {
                click: this.onChangeCurrentProprio
            },
            'oldebsFiche #gridVisite': {
                beforerender: this.filterOldeb,
                viewready: this.selectLast,
                select: this.onSelect
            },
            'oldebsFiche #oldebEnvironnement': {
                render: this.filterCaracteristique
            },
            'oldebsFiche #gridSuites': {
                selectionchange: this.changeSuite
            },
            'oldebsFiche #removeSuite': {
                click: this.deleteSuite
            },
            'oldebsFiche #addSuite': {
                click: this.addSuite
            },
            'oldebsFiche #removeVisite': {
                click: this.deleteVisite
            },
            'oldebsFiche #addVisite': {
                click: this.addVisite
            },
            'oldebsFiche #oldebAnomalies': {
                // positionChange: this.onAnomalieChange,
                selectionChange: this.onAnomalieSelectionChange
            },
            'oldebsFiche combo[name=nomAvis]': {
                change: this.onChangeCombo
            },
            'oldebsFiche combo[name=nomDebParcelle]': {
                change: this.onChangeCombo
            },
            'oldebsFiche combo[name=nomDebAcces]': {
                change: this.onChangeCombo
            },
            'oldebsFiche textfield[name=agent]': {
                change: this.onChangeTextField
            },
            'oldebsFiche textfield[name=nomProprietaire]': {
                blur: this.onChoiceProprietaire
            },
            'oldebsFiche textarea[name=oldebObservation]': {
                change: this.onChangeTextArea
            },
            'oldebsFiche combo[name=nomAction]': {
                change: this.onChangeCombo
            },
            'oldebsFiche datefield[name=dateVisite]': {
                change: this.onChangeDateField
            },
            'oldebsFiche #detailVisite': {
                render: function(detailVisite, eOpts) {
                    detailVisite.body.removeCls('x-item-disabled');
                },
                defer: 3000
            }
        });
    },

    onBeforeRenderFiche: function(fiche) {
        if (Sdis.Remocra.Rights.getRight('OLDEB').Read && !Sdis.Remocra.Rights.getRight('OLDEB').Create && !Sdis.Remocra.Rights.getRight('OLDEB').Update) {
            fiche.down('#ok').hide();
            fiche.down('#close').setText('Fermer');
            Ext.Array.each(fiche.query('filefield'), function(item) {
                item.hide();
            });
            this.setReadOnly(fiche.down('form'));
        }
    },

    printFiche: function(button) {
        // Message d'information au clic sur le bouton
        Ext.Msg
                .confirm(
                        'Téléchargement de la fiche',
                        'Votre demande va être enregistrée. Lorsque le fichier sera prêt, vous serez averti par un message électronique. Souhaitez-vous continuer ?',
                        function(btn) {
                            if (btn == "yes") {
                                this.downloadFiche(button);
                            }
                        }, this);
    },

    downloadFiche: function(button) {
        var fiche = button.up('oldebsFiche'), form = fiche.down('form').getForm(), commune = form.findField('commune'), section = form
                .findField('section'), parcelle = form.findField('parcelle');

        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/oldeb"),
            method: 'GET',
            params: {
                section: section.getValue(),
                commune: commune.getValue(),
                parcelle: parcelle.getValue()
            },
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Téléchargement de la fiche', 'Votre demande a été prise en compte.', 5);
                    button.setDisabled(true);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>' + o.result.message : '';
                    Ext.Msg.alert('Téléchargement de la fiche', 'Un problème est survenu lors de l\'enregistrement de la demande.'
                            + msg + '.');
                }
            }
        });
    },

    setReadOnly: function(component) {
        if (Ext.isFunction(component.cascade)) {
            component.cascade(function(item) {
                if (Ext.isFunction(item.setReadOnly)) {
                    item.setReadOnly(true);
                    item.clearListeners();
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
                if (item.isXType('button')) {
                    item.setDisabled(true);
                }
                if (item.isXType('oldebdragdroptree')) {
                    item.getLeftPanel().clearListeners();
                    item.getRightPanel().clearListeners();
                }
            });
        }
    },

    onChangeCurrentProprio: function(button) {
        var fiche = button.up('oldebsFiche'), form = fiche.down('form').getForm();
        var fieldRaisonSocialeProprietaire = form.findField('raisonSocialProprietaire'), fieldNomProprietaire = form
                .findField('nomProprietaire'), fieldPrenomProprietaire = form.findField('prenomProprietaire'), fieldNumVoieProprietaire = form
                .findField('numVoieProprietaire'), fieldVoieProprietaire = form.findField('voieProprietaire'), fieldlieuDitProprietaire = form
                .findField('lieuDitProprietaire'), fieldCodePostaleProprietaire = form.findField('codePostalProprietaire'), fieldVilleProprietaire = form
                .findField('villeProprietaire'), fieldPaysProprietaire = form.findField('paysProprietaire'), fieldTelephoneProprietaire = form
                .findField('telephoneProprietaire'), fieldEmailProprietaire = form.findField('emailProprietaire'), cboCiviliteProprietaire = form
                .findField('civiliteProprietaire'), checkOrganismeProprietaire = form.findField('organismeProprietaire');
        fieldRaisonSocialeProprietaire.reset();
        checkOrganismeProprietaire.reset();
        fieldRaisonSocialeProprietaire.reset();
        fieldNomProprietaire.reset();
        fieldPrenomProprietaire.reset();
        fieldNumVoieProprietaire.reset();
        fieldVoieProprietaire.reset();
        fieldlieuDitProprietaire.reset();
        fieldCodePostaleProprietaire.reset();
        fieldVilleProprietaire.reset();
        fieldPaysProprietaire.reset();
        fieldTelephoneProprietaire.reset();
        fieldEmailProprietaire.reset();
        cboCiviliteProprietaire.reset();
    },

    onChoiceProprietaire: function(textfield) {
        if (Sdis.Remocra.Rights.getRight('OLDEB').Create || Sdis.Remocra.Rights.getRight('OLDEB').Update) {
            var fiche = textfield.up('oldebsFiche'), form = fiche.down('form').getForm(), oldeb = form.getRecord();
            var fieldRaisonSocialeProprietaire = form.findField('raisonSocialProprietaire'), fieldNomProprietaire = form
                    .findField('nomProprietaire'), fieldPrenomProprietaire = form.findField('prenomProprietaire'), fieldNumVoieProprietaire = form
                    .findField('numVoieProprietaire'), fieldVoieProprietaire = form.findField('voieProprietaire'), fieldlieuDitProprietaire = form
                    .findField('lieuDitProprietaire'), fieldCodePostaleProprietaire = form.findField('codePostalProprietaire'), fieldVilleProprietaire = form
                    .findField('villeProprietaire'), fieldPaysProprietaire = form.findField('paysProprietaire'), fieldTelephoneProprietaire = form
                    .findField('telephoneProprietaire'), fieldEmailProprietaire = form.findField('emailProprietaire'), cboCiviliteProprietaire = form
                    .findField('civiliteProprietaire'), checkOrganismeProprietaire = form.findField('organismeProprietaire');
            var proprietaire = null;

            var win = Ext.widget('oldebs.proprietaire', {
                listeners: {
                    scope: this,
                    beforerender: function() {
                        var grid = Ext.getCmp('gridProprio');
                        if (checkOrganismeProprietaire.getValue() == false) {
                            grid.columns[0].hide();
                        }
                    },
                    valid: function(record) {
                        if (oldeb.oldebProprietes()) {
                            if (record) {
                                proprietaire = record[0];
                                if (oldeb.oldebProprietes().getAt(0) != null) {
                                    oldeb.oldebProprietes().getAt(0).setProprietaire(proprietaire);
                                } else {
                                    propriete = Ext.create('Sdis.Remocra.model.OldebPropriete', null);
                                    propriete.setProprietaire(proprietaire);
                                    oldeb.oldebProprietes().add(propriete);

                                }
                                fieldRaisonSocialeProprietaire.setValue(proprietaire.data.raisonSociale);
                                checkOrganismeProprietaire.setValue(proprietaire.data.organisme);
                                fieldRaisonSocialeProprietaire.setDisabled(!proprietaire.data.organisme);
                                fieldNomProprietaire.setValue(proprietaire.data.nom);
                                fieldPrenomProprietaire.setValue(proprietaire.data.prenom);
                                fieldNumVoieProprietaire.setValue(proprietaire.data.numVoie);
                                fieldVoieProprietaire.setValue(proprietaire.data.voie);
                                fieldlieuDitProprietaire.setValue(proprietaire.data.lieuDit);
                                fieldCodePostaleProprietaire.setValue(proprietaire.data.codePostal);
                                fieldVilleProprietaire.setValue(proprietaire.data.ville);
                                fieldPaysProprietaire.setValue(proprietaire.data.pays);
                                fieldTelephoneProprietaire.setValue(proprietaire.data.telephone);
                                fieldEmailProprietaire.setValue(proprietaire.data.email);
                                var sexProprietaire = null;
                                if (proprietaire.data.civilite == 'M') {
                                    sexProprietaire = '0';
                                } else if (proprietaire.data.civilite == 'Mme') {
                                    sexProprietaire = '1';
                                }
                                this.setValueCbo(cboCiviliteProprietaire, sexProprietaire);
                            }
                        }
                    }
                }
            });
            // on affiche la fenêtre s'il y a des propriétaires
            // similaires

            win.grid.getStore().load({
                callback: function() {
                    if (win.grid.getStore().getCount() != 0) {
                        win.show();
                    } else {
                        win.destroy();
                    }
                }
            });

        }
    },
    onAnomalieSelectionChange: function(chk, records) {
        var fiche = chk.up('window'), form = fiche.down('form[name=fiche]').getForm(), oldeb = form.getRecord(), grid = Ext
                .getCmp('gridVisite');
        var visite = grid.getSelectionModel().getSelection()[0];
        visite.set('totalAnomalies', records.length);
        visite.typeOldebAnomalies().removeAll();
        visite.typeOldebAnomalies().add(records);
        oldeb.setDirty(true);
        chk.grid.getView().refresh();
        grid.getView().refresh();
    },

    onChangeDateField: function(datefield, newValue, oldValue, opt) {
        var fiche = datefield.up('oldebsFiche'), grid = Ext.getCmp('gridVisite');
        if (newValue == null) {
            return null;
        }
        var selection = grid.getView().getSelectionModel().getSelection()[0];
        selection.set('dateVisite', newValue);
    },

    onChangeTextField: function(textfield, newValue, oldValue, opt) {
        var fiche = textfield.up('oldebsFiche'), grid = Ext.getCmp('gridVisite');
        if (newValue == null) {
            return null;
        }
        var selection = grid.getView().getSelectionModel().getSelection()[0];
        selection.set('agent', newValue);
    },

    onChangeTextArea: function(textarea, newValue, oldValue, opt) {
        var fiche = textarea.up('oldebsFiche'), grid = Ext.getCmp('gridVisite');
        if (newValue == null) {
            return null;
        }
        var selection = grid.getView().getSelectionModel().getSelection()[0];
        selection.set('observation', newValue);
    },

    // synchronisier la valeur de visite ajoutée (le nom de combo
    // doit être identique au dataIndex)
    onChangeCombo: function(combo, newValue, oldValue, opt) {
        var fiche = combo.up('oldebsFiche'), grid = Ext.getCmp('gridVisite'), rec = combo.getStore().getById(newValue);
        if (rec == null) {
            return null;
        }
        var selection = grid.getView().getSelectionModel().getSelection()[0];
        selection.set(combo.getName(), rec.get('nom'));
    },

    deleteVisite: function() {
        if (Sdis.Remocra.Rights.getRight('OLDEB').Create || Sdis.Remocra.Rights.getRight('OLDEB').Update) {
            var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), detailVisite = fiche.down("#detailVisite"), removeVisite = grid
                    .down('#removeVisite');
            var selection = grid.getSelectionModel().getSelection()[0];
            if (selection) {
                Ext.Msg.confirm(fiche.title, 'Confirmez-vous la suppression de la visite ?', function(buttonId) {
                    if (buttonId == 'yes') {
                        grid.store.remove(selection);
                        grid.getSelectionModel().select(grid.store.getAt(0));

                        if (grid.store.getAt(0) == null) {
                            detailVisite.hide();
                            detailVisite.setDisabled(true);
                            removeVisite.setDisabled(true);
                        }
                    }
                }, this);
            }
        }
    },

    addVisite: function() {
        if (Sdis.Remocra.Rights.getRight('OLDEB').Create || Sdis.Remocra.Rights.getRight('OLDEB').Update) {
            var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), detailVisite = fiche.down("#detailVisite");
            var visite = Ext.create('Sdis.Remocra.model.OldebVisite', null);
            visite.set('dateVisite', new Date());
            visite.set('code', Sdis.Remocra.util.Util.guid());
            grid.store.insert(0, visite);
            detailVisite.show();
            detailVisite.setDisabled(false);
            detailVisite.setDisabled(true);

            detailVisite.setDisabled(false);
            detailVisite.body.removeCls('x-item-disabled');
            this.selectLast();
        }
    },

    deleteSuite: function() {
        if (Sdis.Remocra.Rights.getRight('OLDEB').Create || Sdis.Remocra.Rights.getRight('OLDEB').Update) {
            var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), gridSuite = fiche.down('#gridSuites');
            var selection = gridSuite.getView().getSelectionModel().getSelection()[0];
            if (selection) {
                Ext.Msg.confirm(fiche.title, 'Confirmez-vous la suppression de la suite ?', function(buttonId) {
                    if (buttonId == 'yes') {
                        gridSuite.store.remove(selection);
                    }
                }, this);
            }
        }
    },

    addSuite: function() {
        if (Sdis.Remocra.Rights.getRight('OLDEB').Create || Sdis.Remocra.Rights.getRight('OLDEB').Update) {
            var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), gridSuite = fiche.down('#gridSuites');
            var suite = Ext.create('Sdis.Remocra.model.OldebVisiteSuite', null);
            suite.set('dateSuite', new Date());
            gridSuite.store.insert(0, suite);
        }
    },

    changeSuite: function(selModel, selections) {
        var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), gridSuite = fiche.down('#gridSuites');
        fiche.down('#removeSuite').setDisabled(selections.length === 0);
    },

    selectLast: function() {
        var grid = Ext.getCmp('gridVisite');
        if (grid.getStore().getCount() != 0) {
            grid.getSelectionModel().select(0);
        }
    },

    loadDetails: function(record) {
        var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), gridSuite = fiche.down('#gridSuites'), form = fiche.down(
                'form[name=fiche]').getForm(), cmpAnomalie = fiche.down('#oldebAnomalies'), documentsP = fiche
                .down('#oldebVisiteDocument'), cmpObservation = fiche.down('#oldebObservation');
        Ext.defer(function() {
            var store = Ext.create('Ext.data.Store', {
                model: 'Sdis.Remocra.model.TypeOldebAnomalie',
                autoLoad: false,
                remoteSort: false,
                remotefilter: false
            });
            store.add(Ext.getStore('TypeOldebAnomalie').getRange());
            store.each(function(anomalie) {
                if (anomalie.dirty) {
                    anomalie.reject();
                }
            });
            // on recrée le panel en changement de visite
            var owner = cmpAnomalie.ownerCt;
            owner.remove(cmpAnomalie);
            cmpAnomalie = Ext.create('Sdis.Remocra.widget.AnomalieOldeb', {
                title: '',
                border: false,
                id: 'oldebAnomalies'
            });
            owner.add(cmpAnomalie);

            cmpAnomalie.bindStore(store, true);
            if (record != null) {
                // on récupère les anomalies dans un tableau
                // pour pouvoir les selectionner
                var visite = record;
                var anomalies = [];
                var storeAnomalies = visite.typeOldebAnomalies();
                if (storeAnomalies) {
                    Ext.each(storeAnomalies.getRange(), function(anomalie, id) {
                        anomalies.push(anomalie.data);
                    });
                }

                cmpAnomalie.setSelected(anomalies);
                cmpAnomalie.checkSelected();

                // on charge les suites
                gridSuite.bindStore(visite.oldebVisiteSuites(), true);

                // Changement du groupe (visite)
                documentsP.setGroupCode(visite.get('code'));

                // on charge l'observation
                cmpObservation.setValue(visite.data.observation);
            }

        }, 150, this);

    },

    filterOldeb: function() {
        var grid = Ext.getCmp('gridVisite'), fiche = grid.up('window'), detailVisite = fiche.down("#detailVisite"), form = fiche
                .down('form[name=fiche]').getForm(), oldebId = form.getRecord().getId(), storeVisite = form.getRecord()
                .oldebVisites();

        // Documents : on crée un store alimenté avec l'ensemble
        // des documents.
        // Les documents sont regroupés en "pages" de visites
        // (code)
        var documentsP = fiche.down('#oldebVisiteDocument');
        var documentsData = [];
        storeVisite.each(function(visite) {
            visite.oldebVisiteDocuments().each(function(document) {
                documentsData.push({
                    id: document.getId(),
                    titre: document.get('titre'),
                    code: document.get('code'),
                    group: visite.get('code'),
                    phantom: false
                });
            });
        });
        var store = Ext.create('Ext.data.Store', {
            model: 'Sdis.Remocra.model.FileUploadMulti',
            data: documentsData
        });
        var dataview = documentsP.query('dataview')[0];
        dataview.tpl.setId(dataview.getId());
        dataview.bindStore(store);
        grid.bindStore(storeVisite, true);
        // on trie par date
        grid.getStore().sort('dateVisite', 'DESC');
        // on sélectionne le premier par défaut
        if (grid.getStore().getCount() == 0) {
            detailVisite.hide();
            detailVisite.setDisabled(true);
        } else {
            detailVisite.show();
        }
    },

    filterCaracteristique: function() {
        var tree = Ext.getCmp('oldebEnvironnement'), fiche = tree.up('window'), form = fiche.down('form[name=fiche]').getForm(), caracteristiques = form
                .getRecord().typeOldebCaracteristiques();
        var tabRight = tree.getRightPanel().getRootNode();
        var tabLeft = tree.getLeftPanel().getRootNode();

        tabRight.eachChild(function(child, index, array) {
            // eachChild() doesn't make a copy of the children
            // before processing them, so adding or removing
            // children in the callback will break eachChild.
            var children = child;
            var childNodes = [].concat(children.childNodes);
            Ext.each(childNodes, function(child) {
                var codes = [];
                var i = 0;
                caracteristiques.each(function(record) {
                    codes.push(record.data.code);
                });
                if (Ext.Array.contains(codes, child.data.code)) {
                    // on supprime l'équivalent à gauche
                    leftChild = tabLeft.findChild('code', child.data.code, true);
                    leftChild.remove();
                } else {
                    children.removeChild(child);
                }
            });
        });
    },

    onSelect: function(sm, record) {
        var grid = Ext.getCmp('gridVisite'), removeVisite = grid.down('#removeVisite'), fiche = grid.up('window'), form = fiche.down(
                'form[name=fiche]').getForm(), detailVisite = fiche.down("#detailVisite"), cboDebParcelle = fiche
                .down('#nomDebParcelle'), cboAvis = fiche.down('#nomAvis'), cboAction = fiche.down('#nomAction'), cboDebAcces = fiche
                .down('#nomDebAcces'), agent = form.findField('agent'), dateVisite = form.findField('dateVisite');
        cboDebParcelle.allowBlank = false;
        cboAction.allowBlank = false;
        cboAvis.allowBlank = false;
        cboDebAcces.allowBlank = false;
        agent.allowBlank = false;
        dateVisite.allowBlank = false;
        if (record) {
            removeVisite.setDisabled(record == null);
            // on charge les details de la visite
            dateVisite.setValue(record.data.dateVisite);
            agent.setValue(record.data.agent);
            this.setValueCbo(cboDebParcelle, record.data.nomDebParcelle);
            this.setValueCbo(cboDebAcces, record.data.nomDebAcces);
            this.setValueCbo(cboAvis, record.data.nomAvis);
            this.setValueCbo(cboAction, record.data.nomAction);
        }
        this.loadDetails(record);
    },

    setValueCbo: function(comboBox, value) {
        comboBox.reset();
        comboBox.store.load();
        comboBox.setValue(value);
    },

    onChangeLocation: function(checkbox, newValue, oldValue, eOpts) {
        var fiche = checkbox.up('oldebsFiche'), locataire = fiche.down('#locataire');
        if (newValue) {
            locataire.show();
            locataire.setDisabled(false);
        } else {
            locataire.hide();
            locataire.setDisabled(true);
        }
    },

    onChangePortail: function(checkbox) {
        var fiche = checkbox.up('oldebsFiche'), field = fiche.down('textfield[name=codePortail]'), form = fiche.down('form')
                .getForm();
        if (checkbox.getValue() == false) {
            field.setValue('');
            // on fait l'update de formulaire directement car le
            // setDisabled(true) ne garde pas la nouvelle valeur
            // du textfield
            form.updateRecord();
            field.setDisabled(true);
        } else if (checkbox.getValue() == true) {
            field.setDisabled(false);
        }
    },

    onChangeOrganismeProprietaire: function(checkbox) {
        var fiche = checkbox.up('oldebsFiche'), field = fiche.down('textfield[name=raisonSocialProprietaire]');
        if (checkbox.getValue() == false) {
            field.reset();
            field.setDisabled(true);
        } else if (checkbox.getValue() == true) {
            field.setDisabled(false);
        }
    },
    onChangeOrganismeLocataire: function(checkbox) {
        var fiche = checkbox.up('oldebsFiche'), field = fiche.down('textfield[name=raisonSocialLocataire]');
        if (checkbox.getValue() == false) {
            field.reset();
            field.setDisabled(true);
        } else if (checkbox.getValue() == true) {
            field.setDisabled(false);
        }
    },

    onAfterRenderFiche: function(fiche) {
        // on charge les données de l'objet json dans le
        // formulaire
        // localisation
        var form = fiche.down('form').getForm(), fieldCommune = form.findField('commune'), cboZoneUrbanisme = form
                .findField('zoneUrbanisme'), ongletLocataire = fiche.down('#locataire'), location = fiche
                .down('checkbox[name=location]'), cboVoie = fiche.down('comboforce[name=voie]'), cboAcces = form.findField('acces'), documentsP = fiche
                .down('#oldebVisiteDocument');

        // Document à supprimer : réinitialisation
        documentsP.docToDelete = [];

        form.loadRecord(fiche.oldeb);
        if (!fiche.oldeb.phantom) {
            var commune = form.getRecord().getCommune();
            fieldCommune.setValue(commune);

            if (form.getRecord().getZoneUrbanisme() != null) {
                var zoneUrbanisme = form.getRecord().getZoneUrbanisme().data.id;
                this.setValueCbo(cboZoneUrbanisme, zoneUrbanisme);
            }
            if (form.getRecord().getAcces() != null) {
                var acces = form.getRecord().getAcces().data.id;
                this.setValueCbo(cboAcces, acces);
            }
            // Mise à jour des filtres de la liste de voies
            var filters = [], wkt = form.getRecord().data.geometrie;
            if (wkt != null) {
                filters.push({
                    property: "wkt",
                    value: wkt
                });
            }
            if (commune != null) {
                filters.push({
                    property: "communeId",
                    value: commune.data.id
                });
            }
            if (cboVoie != null) {
                cboVoie.store.clearFilter(true);
                cboVoie.store.filter(filters);
            }
            var voie = form.getRecord().data.voie;
            this.setValueCbo(cboVoie, voie);

            // Propriétaire
            if (form.getRecord().oldebProprietes() && form.getRecord().oldebProprietes().getCount() != 0) {
                var propriete = form.getRecord().oldebProprietes().data.getAt(0);
                if (propriete) {
                    var residence = propriete.get('residence');
                    var cboResidence = form.findField('residence');
                    this.setValueCbo(cboResidence, residence);
                    if (propriete.getProprietaire()) {
                        var proprietaire = propriete.getProprietaire(), fieldRaisonSocialeProprietaire = form
                                .findField('raisonSocialProprietaire'), fieldNomProprietaire = form.findField('nomProprietaire'), fieldPrenomProprietaire = form
                                .findField('prenomProprietaire'), fieldNumVoieProprietaire = form.findField('numVoieProprietaire'), fieldVoieProprietaire = form
                                .findField('voieProprietaire'), fieldlieuDitProprietaire = form.findField('lieuDitProprietaire'), fieldCodePostaleProprietaire = form
                                .findField('codePostalProprietaire'), fieldVilleProprietaire = form.findField('villeProprietaire'), fieldPaysProprietaire = form
                                .findField('paysProprietaire'), fieldTelephoneProprietaire = form.findField('telephoneProprietaire'), fieldEmailProprietaire = form
                                .findField('emailProprietaire'), cboCiviliteProprietaire = form.findField('civiliteProprietaire'), checkOrganismeProprietaire = form
                                .findField('organismeProprietaire');
                        fieldRaisonSocialeProprietaire.setValue(proprietaire.data.raisonSociale);
                        checkOrganismeProprietaire.setValue(proprietaire.data.organisme);
                        fieldRaisonSocialeProprietaire.setDisabled(!proprietaire.data.organisme);
                        fieldNomProprietaire.setValue(proprietaire.data.nom);
                        fieldPrenomProprietaire.setValue(proprietaire.data.prenom);
                        fieldNumVoieProprietaire.setValue(proprietaire.data.numVoie);
                        fieldVoieProprietaire.setValue(proprietaire.data.voie);
                        fieldlieuDitProprietaire.setValue(proprietaire.data.lieuDit);
                        fieldCodePostaleProprietaire.setValue(proprietaire.data.codePostal);
                        fieldVilleProprietaire.setValue(proprietaire.data.ville);
                        fieldPaysProprietaire.setValue(proprietaire.data.pays);
                        fieldTelephoneProprietaire.setValue(proprietaire.data.telephone);
                        fieldEmailProprietaire.setValue(proprietaire.data.email);
                        var sexProprietaire = null;
                        if (proprietaire.data.civilite == 'M') {
                            sexProprietaire = '0';
                        } else if (proprietaire.data.civilite == 'Mme') {
                            sexProprietaire = '1';
                        }
                        this.setValueCbo(cboCiviliteProprietaire, sexProprietaire);
                    }
                }
            }
            // Locataire
            var fieldRaisonSocialeLocataire = form.findField('raisonSocialLocataire'), fieldNomLocataire = form
                    .findField('nomLocataire'), fieldPrenomLocataire = form.findField('prenomLocataire'), fieldTelephoneLocataire = form
                    .findField('telephoneLocataire'), fieldEmailLocataire = form.findField('emailLocataire'), cboCiviliteLocataire = form
                    .findField('civiliteLocataire'), chckOrganismeLocataire = form.findField('organismeLocataire');

            if (form.getRecord().oldebLocataires().getCount() != 0) {
                ongletLocataire.hide();
                ongletLocataire.setDisabled(false);
                location.setValue(true);
                var locataire = form.getRecord().oldebLocataires().data.getAt(0);
                if (locataire) {
                    fieldRaisonSocialeLocataire.setDisabled(!locataire.data.organisme);
                    chckOrganismeLocataire.setValue(locataire.data.organisme);
                    fieldRaisonSocialeLocataire.setValue(locataire.data.raisonSociale);
                    fieldNomLocataire.setValue(locataire.data.nom);
                    fieldPrenomLocataire.setValue(locataire.data.prenom);
                    fieldTelephoneLocataire.setValue(locataire.data.telephone);
                    fieldEmailLocataire.setValue(locataire.data.email);
                    var sexLocataire = null;
                    if (locataire.data.civilite == 'M') {
                        sexLocataire = '0';
                    } else if (locataire.data.civilite == 'MME') {
                        sexLocataire = '1';
                    }
                    this.setValueCbo(cboCiviliteLocataire, sexLocataire);
                }
            } else {
                // on cache l'onglet locataire
                ongletLocataire.hide();
                ongletLocataire.setDisabled(true);
                location.setValue(false);
            }
        } else {
            // proposer les commmunes en intersection avec la
            // géométrie de loldeb
            fieldCommune.getStore().clearFilter(true);
            fieldCommune.getStore().filter([{
                property: "oldebGeom",
                value: fiche.oldeb.data.geometrie
            } ]);
            fieldCommune.setReadOnly(false);

            // Mise à jour des filtres de la liste de voies
            filters = [];
            wkt = fiche.oldeb.data.geometrie;
            if (wkt != null) {
                filters.push({
                    property: "wkt",
                    value: wkt
                });
            }
            commune = fieldCommune.getValue();
            if (commune != null) {
                filters.push({
                    property: "communeId",
                    value: commune.data.id
                });
            }
            if (cboVoie != null) {
                cboVoie.store.clearFilter(true);
                cboVoie.store.filter(filters);
            }
            // on cache l'onglet locataire
            ongletLocataire.hide();
            ongletLocataire.setDisabled(true);
            location.setValue(false);
        }
    },

    checkDispo: function(button) {
        var fiche = button.up('window'), form = fiche.down('form[name=fiche]').getForm(), commune = form.findField('commune'), section = form
                .findField('section'), parcelle = form.findField('parcelle');
        var uniqueKey = false;
        // unicité commune/section/parcelle
        if (commune.isValid() && section.isValid() && parcelle.isValid()) {
            Ext.Ajax.request({
                baseParams: {
                    manage500: true
                },
                url: 'oldeb/checkdispo',
                params: {
                    id: fiche.oldeb.getId(),
                    section: section.getValue(),
                    commune: commune.getValue(),
                    parcelle: parcelle.getValue()
                },
                success: function(response) {
                    section.clearInvalid();
                    parcelle.clearInvalid();
                    uniqueKey = true;
                },
                failure: function(response) {
                    var data = Ext.decode(response.responseText);
                    section.markInvalid(data.message);
                    parcelle.markInvalid(data.message);
                }
            });
        }
    },

    saveFiche: function(button) {
        var fiche = button.up('window'), form = fiche.down('form[name=fiche]').getForm(), gridSuite = fiche.down('#gridSuites'), location = fiche
                .down('checkbox[name=location]'), oldeb = form.getRecord(), fieldCommune = form.findField('commune'), msgErrorField = fiche
                .down('displayfield[name=errorMsg]'), documentsP = fiche.down('#oldebVisiteDocument');
        if (form.isValid()) {
            msgErrorField.hide();
            form.updateRecord();
            if (oldeb.phantom) {
                oldeb.setCommune(fieldCommune.getValueModel());
            }
            // mettre à jour l'accès et le type de zone
            // urbanisme
            oldeb.setZoneUrbanisme(form.findField('zoneUrbanisme').getValueModel());
            oldeb.setAcces(form.findField('acces').getValueModel());

            // mettre à jour proprieté (proprietaire /
            // résidence)
            if (oldeb.oldebProprietes().getCount() != 0) {
                var propriete = oldeb.oldebProprietes().data.getAt(0);
            } else {
                propriete = Ext.create('Sdis.Remocra.model.OldebPropriete', null);
            }

            if (propriete.getProprietaire() != null) {
                var proprietaire = propriete.getProprietaire();
            } else {
                proprietaire = Ext.create('Sdis.Remocra.model.OldebProprietaire', null);
            }
            proprietaire.set('organisme', form.findField('organismeProprietaire').getValue());
            proprietaire.set('raisonSociale', form.findField('raisonSocialProprietaire').getValue());
            if (form.findField('civiliteProprietaire').getValue() == '0') {
                proprietaire.set('civilite', 'M');
            } else if (form.findField('civiliteProprietaire').getValue() == '1') {
                proprietaire.set('civilite', 'Mme');
            }
            proprietaire.set('nom', form.findField('nomProprietaire').getValue());
            proprietaire.set('prenom', form.findField('prenomProprietaire').getValue());
            proprietaire.set('lieuDit', form.findField('lieuDitProprietaire').getValue());
            proprietaire.set('numVoie', form.findField('numVoieProprietaire').getValue());
            proprietaire.set('telephone', form.findField('telephoneProprietaire').getValue());
            proprietaire.set('ville', form.findField('villeProprietaire').getValue());
            proprietaire.set('voie', form.findField('voieProprietaire').getValue());
            proprietaire.set('codePostal', form.findField('codePostalProprietaire').getValue());
            proprietaire.set('pays', form.findField('paysProprietaire').getValue());
            proprietaire.set('email', form.findField('emailProprietaire').getValue());
            propriete.setProprietaire(proprietaire);

            if (form.findField('residence').getValue() != null) {
                propriete.set('residence', form.findField('residence').getValueModel());
            }
            if (propriete.dirty) {
                oldeb.oldebProprietes().add(propriete);
            }
            // mettre à jour le locataire
            var locataire = null;
            if (!oldeb.oldebLocataires().phantom && oldeb.oldebLocataires().getCount() != 0 && location.getValue() != false) {
                locataire = oldeb.oldebLocataires().getAt(0);
            } else if (oldeb.oldebLocataires() && oldeb.oldebLocataires().getCount() == 0 && location.getValue() != false) {
                locataire = Ext.create('Sdis.Remocra.model.OldebLocataire', null);
            } else if (oldeb.oldebLocataires() && oldeb.oldebLocataires().getCount() != 0 && location.getValue() == false) {
                oldeb.oldebLocataires().removeAll();
            }
            if (locataire && location.getValue() != false) {
                locataire.set('nom', form.findField('nomLocataire').getValue());
                locataire.set('prenom', form.findField('prenomLocataire').getValue());
                locataire.set('organisme', form.findField('organismeLocataire').getValue());
                locataire.set('rasion_sociale', form.findField('raisonSocialLocataire').getValue());
                locataire.set('telephone', form.findField('telephoneLocataire').getValue());
                locataire.set('email', form.findField('emailLocataire').getValue());

                if (form.findField('civiliteLocataire').getValue() == '0') {
                    locataire.set('civilite', 'M');
                } else if (form.findField('civiliteLocataire').getValue() == '1') {
                    locataire.set('civilite', 'MME');
                }
                oldeb.oldebLocataires().add(locataire);
            }
            // mettre à jour l'environnement
            var tree = Ext.getCmp('oldebEnvironnement');
            var tabRight = tree.getRightPanel().getRootNode();
            var caracteristiques = [];
            // on vérifie si le composant est rendu (sinon il va
            // prendre toutes les caractéristiques par défaut)
            if (tree.rendered) {
                tabRight.eachChild(function(child, index, array) {
                    var children = child;
                    var childNodes = [].concat(children.childNodes);
                    Ext.each(childNodes, function(child) {
                        caracteristiques.push(child);
                    });
                });
            }
            if (caracteristiques.length > 0) {
                oldeb.typeOldebCaracteristiques().removeAll();
                Ext.each(caracteristiques, function(caracteristique) {
                    // création de l'objet typeOldebCaracteristique
                    var typeOldebCaracteristique = Ext.create('Sdis.Remocra.model.TypeOldebCaracteristique', caracteristique);
                    typeOldebCaracteristique.set('id', caracteristique.raw.initialId);
                    typeOldebCaracteristique.set('actif', true);
                    typeOldebCaracteristique.set('nom', caracteristique.data.nom);
                    typeOldebCaracteristique.set('code', caracteristique.data.code);
                    typeOldebCaracteristique.set('categorie', caracteristique.data.parentId);
                    oldeb.typeOldebCaracteristiques().add(typeOldebCaracteristique);
                    oldeb.setDirty(true);
                });
            }
            // mettre à jour les visites
            var visites = oldeb.oldebVisites();

            var cboDebParcelle = fiche.down('#nomDebParcelle'), cboAvis = fiche.down('#nomAvis'), cboTypeSuite = Ext
                    .getCmp('nomSuite'), cboAction = fiche.down('#nomAction'), cboDebAcces = fiche.down('#nomDebAcces');
            visites.each(function(record) {
                oldeb.setDirty(true);
                record.set('debroussaillementParcelle', cboDebParcelle.findRecord('nom', record.data.nomDebParcelle));
                record.set('debroussaillementAcces', cboDebAcces.findRecord('nom', record.data.nomDebAcces));
                record.set('avis', cboAvis.findRecord('nom', record.data.nomAvis));
                record.set('action', cboAction.findRecord('nom', record.data.nomAction));
                // cas des suites
                var suites = record.oldebVisiteSuites();
                suites.each(function(suite) {
                    if (suite.phantom || suite.dirty) {
                        if (suite.data.dateSuite != null && suite.data.nomSuite != null) {
                            suite.setSuite(cboTypeSuite.findRecord('nom', suite.data.nomSuite));
                        }
                    }
                });
            });
            // On réalise un POST à l'ancienne pour que les
            // documents puissent être envoyés
            var formFiles = fiche.down('form[name=fiche]').down('filefield');
            if (fiche.ficheParente == null) {
                var id = oldeb.get('id');
                var newRecord = id == null || id == 0;
                var data = oldeb.getProxy().getWriter().getRecordData(oldeb);
                var params = {
                    oldeb: Ext.encode(data),
                    visiteDocumentsToDelete: Ext.encode(documentsP.docToDelete)
                };
                formFiles.up().submit({
                    clientValidation: false, // On empêche la
                                                // validation
                                                // cliente car
                                                // faite à la
                                                // main bien que
                                                // les champs
                                                // cachés ne
                                                // soient pas
                                                // forcément
                                                // valides
                    url: oldeb.getProxy().url + (oldeb.phantom ? '' : '/' + oldeb.getId()),
                    params: params,
                    success: function(fp, o) {
                        Sdis.Remocra.util.Msg.msg('old', 'old a été ' + (newRecord ? 'créé' : 'mis à jour') + ' avec succès.');

                        var grid = Ext.ComponentQuery.query('#obligation')[0];
                        // pour afficher les changements sur la
                        // grille en cas d'ajout ou suppression
                        // de visite
                        setTimeout(function() {
                            grid.getStore().load();
                        }, 1000);
                        var map = Ext.ComponentQuery.query('#localisation')[0];
                        if (map.isVisible()) {
                            map.refreshZonesLayer();
                            map.workingLayer.removeAllFeatures();
                            map.zoomOut();
                        }
                        // On ferme et on rafraîchit la carte
                        fiche.close();
                    },
                    failure: function(fp, o) {
                        Ext.MessageBox.show({
                            title: 'Oldeb',
                            msg: 'Une erreur est survenue lors de l\'enregistrement de l\'oldeb.',
                            buttons: Ext.Msg.OK,
                            icon: Ext.MessageBox.ERROR
                        });
                    },
                    scope: this
                });

            }
        } else {

            var invalid = form.getFields().filterBy(function(field) {
                return !field.validate();
            });
            // compteur d'erreurs
            /*
             * if(invalid.length > 1 ){ msgErrorField.setValue(
             * invalid.length + ' erreurs sont présentes dans le
             * formulaire.'); }else if (invalid.length == 1){
             * msgErrorField.setValue( invalid.length + ' erreur
             * est présente dans le formulaire.'); }
             */
            msgErrorField.setValue(' Des erreurs sont présentes dans le formulaire.');
            msgErrorField.show();
        }
    },

    showFiche: function(oldeb) {
        var xtype = null;
        if (oldeb != null) {
            xtype = 'oldebs.ficheOldeb';
            if (xtype != null) {
                Ext.widget(xtype, {
                    oldeb: oldeb
                }).show();
            } else {
                console.warn('xtype is null', oldeb);
            }

        } else {
            console.warn('oldeb is null');
        }
    }
});