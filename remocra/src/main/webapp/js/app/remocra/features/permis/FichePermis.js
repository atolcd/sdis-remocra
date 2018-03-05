Ext.require('Sdis.Remocra.store.TypePermisAvisPprifStore');
Ext.require('Sdis.Remocra.store.TypePermisAvisNoPprifStore');
Ext.require('Sdis.Remocra.store.TypePermisAvisStore');

Ext.require('Sdis.Remocra.store.TypePermisInterservicePprifStore');
Ext.require('Sdis.Remocra.store.TypePermisInterserviceNoPprifStore');
Ext.require('Sdis.Remocra.store.TypePermisInterserviceStore');

Ext.require('Sdis.Remocra.store.TypeOrganisme');

Ext.require('Sdis.Remocra.store.Voie');

Ext.define('Sdis.Remocra.features.permis.FichePermis', {
    extend: 'Ext.tab.Panel',
    
    title: null,
    height: 700-26, // hauteur carte moins entête tab panel
    
    initComponent: function() {
        // -- EVENEMENTS
        this.addEvents('ok', 'cancel', 'permisCreated');
        
        var dockedItems = Sdis.Remocra.Rights.hasRight('PERMIS_C') ? {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [ '->', {
                itemId: 'validerPermis',
                tooltip: 'Valider le permis',
                text: 'Valider le permis',
                handler: Ext.bind(this.fireEvent, this, ['ok', this])
            }, { itemId: 'annulerPermis',
                tooltip: 'Abandonner les modifications saisies',
                text: 'Réinitialiser la saisie',
                handler: Ext.bind(this.fireEvent, this, ['cancel', this])
            }, '->' ]
        }
            : null;
        
        Ext.apply(this, {
            layout: 'fit',
            activeTab : 0,
            plain: true,
            bodyPadding: 10,
            defaults: {
                autoScroll : true
            },
            items: [
                this.createDescriptionPanel(),
                this.createDocumentsPanel()
            ],
            dockedItems: dockedItems
        });
        
        this.callParent(arguments);
    },
    
    createDescriptionPanel: function() {
        var f = Sdis.Remocra.widget.WidgetFactory;
        var labelWidth = 80;
        var fieldWidth = null;
        
        var id = f.createTextField('Id', true, '', {itemId: 'id', labelWidth: labelWidth, width: fieldWidth, hidden: true});
        var nom = f.createTextField('Nom', false, '', {itemId: 'nom', labelWidth: labelWidth, width: fieldWidth});

        var datePermis = {fieldLabel: 'Date permis', itemId: 'datePermis', xtype: 'datefield',labelWidth: labelWidth, width: fieldWidth};
        var dateModification = f.createTextField('Mise à jour', false, new Date().toLocaleDateString(), {
            itemId: 'dateModification', labelWidth: labelWidth, width: fieldWidth,
            readOnly: true, cls: 'remocra-field-read-only'
        });

        var commune = Sdis.Remocra.widget.WidgetFactory.createCommuneCombo({
            allowBlank: false,
            itemId: 'commune',
            fieldLabel: 'Commune',
            labelWidth: labelWidth,
            labelSeparator: '',
            emptyText: 'Commune de...',
            hideTrigger: false,
            listeners: {
                'select': this.filterAccordingToCommune, scope: this
            }
        });
        var voie = Ext.widget('combo', {
            store: Ext.create('Sdis.Remocra.store.Voie'),
            queryMode: 'remote',
            displayField: 'nom',
            valueField: 'nom',
            triggerAction: "all",
            hideTrigger: false,
            typeAhead: true,
            minChars: 3,
            
            allowBlank: true,
            itemId: 'voie',
            fieldLabel: 'Voie',
            labelWidth: labelWidth,
            labelSeparator: '',
            emptyText: 'Voie...'
        });
        var complement = f.createTextField('Complément adresse', true, '', {itemId: 'complement', labelWidth: labelWidth, width: fieldWidth});
        var numero = f.createTextField('N° permis', false, '', {itemId: 'numero', labelWidth: labelWidth, width: fieldWidth});
        
        var avis = Ext.widget('combo', {
            allowBlank: false,
            itemId: 'avis',
            fieldLabel: 'Avis',
            labelWidth: labelWidth, labelSeparator: '',
            store: Sdis.Remocra.store.TypePermisAvisStore,
            queryMode: 'local',
            displayField: 'nom', valueField: 'id',
            editable: false
        });
        var interservice = Ext.widget('combo', {
            allowBlank: false,
            itemId: 'interservice',
            fieldLabel: 'Interservice',
            labelWidth: labelWidth, labelSeparator: '',
            store: Sdis.Remocra.store.TypePermisInterserviceStore,
            queryMode: 'local',
            displayField: 'nom', valueField: 'id',
            editable: false
        });
        var sectionCadastrale = f.createTextField('N° section', true, '', {itemId: 'sectionCadastrale', labelWidth: labelWidth, width: fieldWidth});
        var parcelleCadastrale = f.createTextField('N° parcelle', true, '', {itemId: 'parcelleCadastrale', labelWidth: labelWidth, width: fieldWidth});
        var observationsLbl = { xtype: 'label', text: 'Observations' };
        var observations = f.createTextArea(null, true, 5, '', {itemId: 'observations', width: 235});
        var serviceInstructeur = Ext.widget('combo', {
            allowBlank: false,
            itemId: 'serviceInstructeur',
            fieldLabel: 'Service instructeur',
            labelWidth: labelWidth, labelSeparator: '',
            store: Ext.create('Sdis.Remocra.store.TypeOrganisme'),
            displayField: 'nom', valueField: 'id',
            editable: false
        });
        var annee = f.createIntField('Année', {allowBlank: false, value: new Date().getFullYear(), itemId: 'annee', labelWidth: labelWidth, width: fieldWidth});
        var instructeur = { itemId: 'instructeur', xtype: 'label', text: 'Instructeur ' + Sdis.Remocra.network.ServerSession.getUserData('login'),
                style:'clear:left;display:block;margin-bottom:5px;margin-left:85px;' };
        
        var sep = {height:10, border: false};
        var descriptionPanel = Ext.create('Ext.form.FormPanel', {
            itemId: 'descriptionPanel',
            title: 'Description',
            border: false,
            defaults: { labelSeparator: '', allowBlank: false },
            items: [this.createHelpPanel(), id, nom, datePermis, dateModification, sep, commune, voie, complement, sep, numero, sep,
                    avis, interservice, sep, sectionCadastrale, parcelleCadastrale, sep,
                    observationsLbl, observations, sep, serviceInstructeur, annee, instructeur]
        });
        return descriptionPanel;
    },
    
    createHelpPanel: function() {
        return {
            hidden: true,
            border: false,
            html : '<p style="font-style:italic;color:#a9a9a9;padding-bottom:20px;">Ici, des explications sont fournies pour <b>guider l\'utilisateur</b>.'
                + ' Ce contenu sera complété ultérieurement.</p>'
        };
    },
    
    createDocumentsPanel: function() {
        return Ext.create('Sdis.Remocra.widget.FileUploadPanel', {title: 'Documents', itemId: 'documents',
            readOnly: !Sdis.Remocra.Rights.hasRight('PERMIS_DOCUMENTS_C') || !Sdis.Remocra.Rights.hasRight('PERMIS_C'),
            moreHelp: '<p style="font-style:italic;color:#a9a9a9;padding-bottom:20px">Les <b>documents déjà transmis</b> ne sont plus accessibles.</p>',
            savePermis: function(permis, mapProjection) {
                Sdis.Remocra.network.CurrentUtilisateurStore.getCurrentUtilisateur(this, function(user) {
                    // this : FileUploadPanel
                    var jsonDataField = this.getComponent('jsonPermis');
                    if (jsonDataField==null) {
                        jsonDataField = new Ext.form.field.Hidden({itemId: 'jsonPermis', name: 'jsonPermis'});
                        this.add(jsonDataField);
                    }
                    var preparedPermis = Ext.create('Ext.data.writer.DeepJson').getRecordData(permis);
                    jsonDataField.setValue(Ext.encode(preparedPermis));
                    this.computeNames('doc');
                    
                    var id = permis.get('id');
                    var newPermis = id==null||id==0;
                    
                    // On réalise un POST à l'ancienne pour que les documents puissent être envoyés
                    this.submit({
                        clientValidation: false, // On empêche la validation cliente car faite à la main et les champs cachés ne sont pas forcément valides 
                        url: Sdis.Remocra.util.Util.withBaseUrl('../permis')+(newPermis?'':'/'+id),
                        success: function(fp, o) {
                            // Succès, on le signale pour que l'utilisateur puisse commencer la saisie d'une nouvelle alerte
                            var rt = o.response.responseText;
                            var decoded = Ext.decode(rt);
                            var wkt = decoded.data.geometrie;
                            var centroidFeat = new OpenLayers.Format.WKT({
                                internalProjection: mapProjection,
                                externalProjection: new OpenLayers.Projection('EPSG:2154')
                            }).read(wkt);
                            
                            this.reset(); // Panneau des documents
                            this.ownerCt.fireEvent('permisCreated', decoded.data, newPermis);
                        },
                        failure: function(fp, o) {
                            Ext.MessageBox.show({
                                title: 'Permis',
                                msg: 'Une erreur est survenue lors de l\'enregistrement du permis.',
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.ERROR
                             });
                        }, scope: this
                    });
                });
            }
        });
    },
    
    filterAccordingToCommune: function() {
        var dp = this.getComponent('descriptionPanel');
        var communeCombo = dp.getComponent('commune');
        var commune = communeCombo.getValueModel();
        
        // Filtre Avis
        var avisCombo = dp.getComponent('avis');
        var avisValue = avisCombo.getValue();
        var avisStore = commune==null?Sdis.Remocra.store.TypePermisAvisStore:
            (commune.get('pprif')===true?Sdis.Remocra.store.TypePermisAvisPprifStore:Sdis.Remocra.store.TypePermisAvisNoPprifStore);
        avisCombo.bindStore(avisStore, false);
        if (avisStore.find('id', avisValue)<0) {
            avisCombo.select(avisStore.first());
        }
        
        // Filtre Interservice
        var interserviceCombo = dp.getComponent('interservice');
        var interserviceValue = interserviceCombo.getValue();
        var interserviceStore = commune==null?Sdis.Remocra.store.TypePermisInterserviceStore:
            (commune.get('pprif')===true?Sdis.Remocra.store.TypePermisInterservicePprifStore:Sdis.Remocra.store.TypePermisInterserviceNoPprifStore);
        interserviceCombo.bindStore(interserviceStore, false);
        if (interserviceStore.find('id', interserviceValue)<0) {
            interserviceCombo.select(interserviceStore.first());
        }
        
        // Filtre des voies par commune et par position (XXm autour du point seulement)
        var voie = dp.getComponent('voie');
        voie.store.clearFilter(true); // Pas de refresh de l'interface
        var filters = [];
        if (this.wkt != null) {
            filters.push({property: "wkt", value: this.wkt});
        }
        if (commune != null) {
            filters.push({property: "communeId", value: commune.get('id')});
        }
        voie.store.filter(filters);
    },
    
    // Annulation de la saisie
    reset: function() {
        // Description
        
        var dp = this.getComponent('descriptionPanel');
        dp.getComponent('id').setValue(null);
        dp.getComponent('datePermis').setValue(new Date().toLocaleDateString());
        dp.getComponent('dateModification').setValue(new Date().toLocaleDateString());
        dp.getComponent('nom').setValue(null);
        dp.getComponent('voie').setValue(null);
        dp.getComponent('complement').setValue(null);
        dp.getComponent('numero').setValue(null);
        dp.getComponent('sectionCadastrale').setValue(null);
        dp.getComponent('parcelleCadastrale').setValue(null);
        dp.getComponent('observations').setValue(null);
        dp.getComponent('annee').setValue(new Date().getFullYear());
        
        dp.getComponent('commune').setValue(null);
        dp.getComponent('avis').setValue(null);
        dp.getComponent('interservice').setValue(null);
        dp.getComponent('serviceInstructeur').setValue(null);
        dp.getComponent('instructeur').setText('Instructeur ' + Sdis.Remocra.network.ServerSession.getUserData('login'));
        
        this.filterAccordingToCommune();
        
        // Documents
        this.getComponent('documents').reset();
    },
    
    fillData: function(permis) {
        var dp = this.getComponent('descriptionPanel');
        dp.getComponent('id').setValue(permis.get('id'));

        var datePermis = permis.get('datePermis');
        dp.getComponent('datePermis').setValue(datePermis==null?new Date():datePermis);

        var dateModification = permis.get('dateModification');
        dp.getComponent('dateModification').setValue((dateModification==null?new Date():dateModification).toLocaleDateString());

        dp.getComponent('nom').setValue(permis.get('nom'));
        dp.getComponent('complement').setValue(permis.get('complement'));
        dp.getComponent('numero').setValue(permis.get('numero'));
        dp.getComponent('sectionCadastrale').setValue(permis.get('sectionCadastrale'));
        dp.getComponent('parcelleCadastrale').setValue(permis.get('parcelleCadastrale'));
        dp.getComponent('observations').setValue(permis.get('observations'));
        dp.getComponent('annee').setValue(permis.get('annee'));
        
        // Voie fictive basée sur le nom
        var voie = permis.get('voie');
        dp.getComponent('voie').setValue(Ext.create('Sdis.Remocra.model.Voie', {
            nom: voie
        }));
        
        var avis = Ext.create('Sdis.Remocra.model.TypePermisAvis', permis.raw.avis);
        dp.getComponent('avis').setValue(avis);
        var interservice = Ext.create('Sdis.Remocra.model.TypePermisInterservice', permis.raw.interservice);
        dp.getComponent('interservice').setValue(interservice);
        var serviceInstructeur = Ext.create('Sdis.Remocra.model.TypePermisAvis', permis.raw.serviceInstructeur);
        dp.getComponent('serviceInstructeur').setValue(serviceInstructeur);
        dp.getComponent('instructeur').setText('Instructeur ' + permis.raw.instructeur.identifiant);
        
        var commune = Ext.create('Sdis.Remocra.model.Commune', permis.raw.commune);
        this.setCommune(commune);
        
        // Documents : on ajoute des liens s'il en existe (un coneneur est prévu à cet effet dans FileUploadPanel)
        var documentsP = this.getComponent('documents');
        var documents = permis.permisDocuments();
        documentsP.addDocuments(documents);
    },
    
    setXYFilter: function(wkt) {
        this.wkt = wkt;
    },
    
    setCommune: function(commune) {
        var dp = this.getComponent('descriptionPanel');
        dp.getComponent('commune').setValue(commune);
        this.filterAccordingToCommune();
    },

    // Création du permis
    // Le POST est réalisé dans le panneau des documents pour que tout soit dans une transaction
    savePermis: function(permis, mapProjection) {
        var dp = this.getComponent('descriptionPanel');
        if (!dp.getForm().isValid()) {
            Ext.MessageBox.show({
                title: 'Permis',
                msg: "Les éléments saisis ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.",
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.ERROR
             });
            return;
        }

        var id = dp.getComponent('id').getValue();
        var newPermis = id==null||id==0;

        // Définis côté serveur
        permis.set('dateModification', new Date());
        permis.set('instructeur', null);

        permis.set('id', id);
        permis.set('nom', dp.getComponent('nom').getValue());
        permis.set('complement', dp.getComponent('complement').getValue());
        permis.set('numero', dp.getComponent('numero').getValue());
        permis.set('sectionCadastrale', dp.getComponent('sectionCadastrale').getValue());
        permis.set('parcelleCadastrale', dp.getComponent('parcelleCadastrale').getValue());
        permis.set('observations', dp.getComponent('observations').getValue());
        permis.set('annee', dp.getComponent('annee').getValue());
        permis.set('nom', dp.getComponent('nom').getValue());
        permis.set('datePermis', dp.getComponent('datePermis').getValue());

        // On renseigne la voie si une sélection existe
        var voie = dp.getComponent('voie').getValueModel();
        permis.set('voie', voie?voie.get('nom'):null);

        permis.setCommune(dp.getComponent('commune').getValueModel());
        permis.setAvis(dp.getComponent('avis').getValueModel());
        permis.setInterservice(dp.getComponent('interservice').getValueModel());
        permis.setServiceInstructeur(dp.getComponent('serviceInstructeur').getValueModel());
        permis.setAvis(dp.getComponent('avis').getValueModel());

        // Documents
        this.getComponent('documents').savePermis(permis, mapProjection);
    }
});