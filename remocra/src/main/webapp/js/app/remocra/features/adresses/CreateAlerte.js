Ext.ns('Sdis.Remocra.features.adresses');

Ext.require('Sdis.Remocra.widget.FileUploadPanel');
Ext.require('Sdis.Remocra.model.Alerte');

Ext.define('Sdis.Remocra.features.adresses.CreateAlerte', {
    extend: 'Ext.tab.Panel',

    title: null,
    
    layout: 'fit',
    height: 700-26, // hauteur carte moins entête tab panel
    
    initComponent: function() {
        // -- EVENEMENTS
        this.addEvents('ok', 'cancel', 'alerteCreated');
        
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
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                items: [
                    '->',
                    { itemId: 'validerAlerte',
                        tooltip: 'Valider l\'ensemble des informations saisies (éléments graphiques, anomalies, descriptions et documents)',
                        text: 'Valider l\'alerte',
                        handler: Ext.bind(this.fireEvent, this, ['ok', this]), disabled: true},
                    { itemId: 'annulerAlerte',
                        tooltip: 'Abandonner toutes les modifications saisies depuis la dernière activation de la barre d\'édition',
                        text: 'Réinitialiser la saisie', 
                        handler: Ext.bind(this.fireEvent, this, ['cancel', this]) },
                    '->'
                ]
            }]
        });
        
        this.callParent(arguments);
    },
    
    createDescriptionPanel: function() {
        var f = Sdis.Remocra.widget.WidgetFactory;
        
        var rapporteurLbl = { xtype: 'label', text: 'Rapporté par ' + Sdis.Remocra.network.ServerSession.getUserData('login'),
                style:'clear:left;display:block;margin-bottom:5px;' };
        var dateConstat = { itemId: 'dateConstat', xtype: 'datefield', format:'d/m/Y', fieldLabel: 'Constaté le', labelWidth: 65, value: new Date()};
        var descriptionLbl = {xtype: 'label', text: 'Description'};
        var description = f.createTextArea(null, true, 20, '', {itemId: 'description', width: 225});
        
        var descriptionPanel = Ext.create('Ext.form.FormPanel', {
            itemId: 'descriptionPanel',
            title: 'Description',
            border: false,
            defaults: { labelSeparator: '', allowBlank: false },
            items: [this.createHelpPanel(), rapporteurLbl, dateConstat, descriptionLbl, description]
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
            createAlerte: function(alerte, mapProjection) {
                Sdis.Remocra.network.CurrentUtilisateurStore.getCurrentUtilisateur(this, function(user) {
                    // this : FileUploadPanel
                    var jsonDataField = this.getComponent('jsonAlerte');
                    if (jsonDataField==null) {
                        jsonDataField = new Ext.form.field.Hidden({itemId: 'jsonAlerte', name: 'jsonAlerte'});
                        this.add(jsonDataField);
                    }
                    var preparedAlerte = Ext.create('Ext.data.writer.DeepJson').getRecordData(alerte);
                    jsonDataField.setValue(Ext.encode(preparedAlerte));
                    this.computeNames('doc');
                    
                    // On réalise un POST à l'ancienne pour que les documents puissent être envoyés
                    this.submit({
                        clientValidation: false, // On empêche la validation cliente car faite à la main et les champs cachés ne sont pas forcément valides 
                        url: Sdis.Remocra.util.Util.withBaseUrl('../adresses/alerte'),
                        success: function(fp, o) {
                            // Succès, on le signale pour que l'utilisateur puisse commencer la saisie d'une nouvelle alerte
                            var geojson = o.result.data;
                            var format = new OpenLayers.Format.GeoJSON({
                                ignoreExtraDims: true,
                                internalProjection: mapProjection,
                                externalProjection: new OpenLayers.Projection('EPSG:'+SRID)
                            });
                            var centroidFeat = format.parseFeature(geojson);
                            this.ownerCt.fireEvent('alerteCreated', centroidFeat);
                        },
                        failure: function(fp, o) {
                            Ext.MessageBox.show({
                                title: 'Alerte',
                                msg: 'Une erreur est survenue lors de l\'enregistrement de l\'alerte.',
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.ERROR
                             });
                        }, scope: this
                    });
                });
            }
        });
    },
    
    // DONNEES
    
    // Annulation de la saisie
    reset: function() {
        // Description
        var dateConstat = this.getComponent('descriptionPanel').getComponent('dateConstat').setValue(new Date());
        var description = this.getComponent('descriptionPanel').getComponent('description').setValue('');
        
        // Documents
        this.getComponent('documents').reset();
    },
    
    // Création de l'alerte
    // Le POST est réalisé dans le panneau des documents pour que tout soit dans une transaction
    createAlerte: function(newAlerteElts, mapProjection) {
        var alerte = Ext.create('Sdis.Remocra.model.Alerte');
        
        alerte.set('dateModification', new Date());
        alerte.setRapporteur(null); // Défini côté serveur
        alerte.set('etat', null); // Après synchro Oracle
        alerte.set('geometrie', null); // Défini côté serveur (barycentre des AlerteElts)
        
        var alerteElts = alerte.alerteElts();
        alerteElts.removeAll();
        alerteElts.add(newAlerteElts);
        // Retravailler les alerteElts pour définir la géométrie et le sousTypeAlerteElt (récupérés de la feature)
        
        // Description
        var dateConstat = this.getComponent('descriptionPanel').getComponent('dateConstat').getValue();
        var description = this.getComponent('descriptionPanel').getComponent('description').getValue();

        alerte.set('dateConstat', dateConstat);
        alerte.set('commentaire', description);
        
        // Documents
        this.getComponent('documents').createAlerte(alerte, mapProjection);
    }
});