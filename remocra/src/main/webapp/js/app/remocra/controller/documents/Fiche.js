Ext.require('Sdis.Remocra.model.BlocDocument');

Ext.require('Sdis.Remocra.store.Thematique');
Ext.require('Sdis.Remocra.store.ProfilDroit');

Ext.require('Sdis.Remocra.features.documents.FicheBlocDocument');

Ext.define('Sdis.Remocra.controller.documents.Fiche', {
    extend : 'Ext.app.Controller',

    stores : [ 'BlocDocument', 'Thematique'/* , 'ProfilDroit', */],

    refs : [],

    init : function() {
        this.control({
            'crBlocDocumentFiche' : {
                afterrender : this.onAfterRenderFiche,
                close : this.onClose
            },
            'crBlocDocumentFiche #ok' : {
                click : this.ok
            }
        });
    },

    onClose : function(fiche) {
        if (fiche.blocDocument.feature) {
            fiche.blocDocument.feature.destroy();
        }
        // On recharge les documents (TODO : uniquement quand c'est nécessaire uniquement)
        this.getController('documents.Admin').update();
    },

    showFiche : function(blocDocument) {
        if (blocDocument == null) {
            blocDocument = Ext.create('Sdis.Remocra.model.BlocDocument', {});
        }
        Ext.widget('crBlocDocumentFiche', {
            blocDocument : blocDocument,
            createMode : blocDocument.phantom
        }).show();
    },

    onAfterRenderFiche : function(fiche) {
        var blocDocument = fiche.blocDocument;
        if (!blocDocument) {
            this.manageFile(fiche, null);
            return;
        }
        var form = fiche.down('form').getForm();
        form.loadRecord(blocDocument);
        
        // Thematiques
        var thematiques = blocDocument.thematiques();
        if (!Ext.isEmpty(thematiques)) {
            form.findField('thematiques').setValue(thematiques.collect('id'));
        }
        
        // ProfilDroits
        var profilDroits = blocDocument.profilDroits();
        if (!Ext.isEmpty(profilDroits)) {
            form.findField('profilDroits').setValue(profilDroits.collect('id'));
        }
        
        this.manageFile(fiche, blocDocument);
    },

    manageFile : function(fiche, record) {
        var downloadBtn = fiche.down('linkbutton[name=download]');

        if (record.get('code') != null && record.get('code') != '') {
            downloadBtn.show();
            downloadBtn.setText(record.get('titre')||'Télécharger',
                    "telechargement/document/" + record.get('code'),
                    '_blank');
        } else {
            downloadBtn.hide();
        }
    },

    ok : function(button) {
        var fiche = button.up('window'), form = fiche.down(
                'form[name=fiche]').getForm();
        var blocDocument = form.getRecord();
        if (!form.isValid()) {
            Ext.MessageBox.show({
                title: 'Documents',
                msg: "Les éléments saisis ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.",
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.ERROR
             });
            return;
        }
        
        form.updateRecord();
        
        // Thématiques
        var thematiques = form.findField('thematiques').getSelected();
        blocDocument.thematiques().removeAll();
        // FIXME pourquoi passer par des copies ?
        Ext.each(thematiques, function(thematique) {
            blocDocument.thematiques().add(thematique.copy());
        });
        
        // ProfilDroits
        var profilDroits = form.findField('profilDroits').getSelected();
        blocDocument.profilDroits().removeAll();
        blocDocument.profilDroits().add(profilDroits);
        
        // Fichier
        var formDocument = fiche.down('form[name=formDocument]');
        if (Ext.isEmpty(formDocument.down('filefield').getValue()) && fiche.createMode) {
            // Pas de fichier et cas de la création
            Ext.MessageBox.show({
                title: 'Documents',
                msg: "Le choix d'un fichier est obligatoire lors de la création d'un document.<br/>Veuillez choisir votre fichier.",
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.ERROR
             });
            return;
        }
        // Fichier : POST Multipart
        var json = blocDocument.getProxy().getWriter().getRecordData(blocDocument);
        formDocument.submit({
            scope: this,
            params: {
                'data': Ext.encode(json)
            },
            url: blocDocument.getProxy().url + (blocDocument.phantom ? '' : '/' + blocDocument.getId()),
            success: function(form, operation) {
                fiche.close();
            },
            failure: function() {
                fiche.close();
            }
        });
    }
});