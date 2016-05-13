Ext.ns('Sdis.Remocra.widget');

Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.widget.LinkButton');

Ext.define('Sdis.Remocra.widget.FileUploadPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.crFileupload',

    title: null,
    
    initComponent: function() {
        // -- EVENEMENTS
        this.addEvents('ok', 'cancel');
        
        Ext.apply(this, {
            items: this.createFields()
        });
        
        this.callParent(arguments);
    },
    
    createFields: function() {
        var items = [{
            cls: 'fileuploadpanel-down',
            hidden: true,
            xtype : 'fieldset',
            itemId : 'download',
            title: 'Existants',
            defaults : { border : false }
        }];
        items.push(this.createCounterField());
        items.push(this.createDescriptionField());
        items.push(this.createFileUploadField());
        return items;
    },
    createCounterField: function() {
        return Sdis.Remocra.widget.WidgetFactory.createIntField('Nouveaux :', {
            hidden: this.readOnly===true,
            itemId: 'fileCounter', name: 'fileCounter', readOnly: true, cls: 'remocra-field-read-only', value: 0, labelWidth: 125
        });
    },
    createDescriptionField: function() {
        return {
            hidden: this.readOnly===true,
            border: false,
            html : '<p style="font-style:italic;color:#a9a9a9;padding-bottom:20px">Pour <b>ajouter un document</b>'
                + ', cliquez sur le bouton "Nouveau..." et sélectionnez le fichier à transmettre.<br/>Pour <b>annuler '
                + 'l\'envoi d\'un fichier</b>, cliquez sur le bouton "Supprimer" en face du document concerné.</p>'
                + (this.moreHelp||'')
        };
    },
    createFileUploadField: function() {
        return {
            hidden: this.readOnly===true,
            fieldLabel: null,
            xtype: 'filefield', buttonText: '<span>Nouveau...</span>',
            //fieldCls: 'remocra-file-field',
            submitValue: true,
            width: 200,
            allowBlank: true, listeners: {
                'change': Ext.bind(this.addDocument, this),
                'render': Ext.bind(function(cmp) {
                    var parent = cmp.browseButtonWrap || cmp.bodyEl;
                    cmp.buttonDelete = Ext.widget('button', {
                        ui:             cmp.ui,
                        renderTo:       parent,
                        text:           'Retirer', width: 66,
                        preventDefault: true,
                        style:          'margin-left:3px',
                        handler:  Ext.bind(this.removeDocument, {panel:this, field:cmp}),
                        enableToggle:   false,
                        hidden: true
                    });
                }, this)
            }
        };
    },
    
    /** Ajoute le nombre au compteur.
     * 
     * @param nb Valeur à ajouter
     * @return la nouvelle valeur
     */
    addToCounter: function(nb) {
        var counterCmp = this.getComponent('fileCounter');
        counterCmp.setValue(counterCmp.getValue()+nb);
        return counterCmp.getValue();
    },
    addDocument: function(fileupload, value, opts) {
        if (value!=null) {
            var newValue = this.addToCounter(1);
            this.add(this.createFileUploadField());
            fileupload.buttonEl.setVisible(false);
            fileupload.buttonEl.setStyle('display', 'none');
            fileupload.buttonDelete.setVisible(true);
            this.resizeInputs();
        }
    },
    removeDocument: function(btn, evt) {
        this.panel.addToCounter(-1);
        this.panel.remove(this.field);// Le fileupload
        this.panel.resizeInputs();
    },
    resizeInputs: function() {
        // On redéfinit la largeur des noeuds input
        var inputToResize = Ext.DomQuery.select('input.x-form-text', this.getEl().dom);
        var i;
        for(i=0 ; i<inputToResize.length ; i++) {
            inputToResize[i].style.width='130px';
        }
    },
    
    reset: function() {
        this.removeAll();
        this.add(this.createFields());
    },
    
    computeNames: function(prefix) {
        var el = this.getEl();
        if (!el) {
            // Rien à faire
            return;
        }
        var inputToResizes = Ext.DomQuery.select('input.x-form-file-input', el.dom);
        var i, counter = 0;
        for(i=0 ; i<inputToResizes.length ; i++) {
            var inputToResize = inputToResizes[i];
            if (inputToResize.value==null || inputToResize.value=='') {
                inputToResize.name = null;
            } else {
                inputToResize.name=prefix+(counter++);
            }
        }
        this.resizeInputs();
    },

    /**
     * Ajoute les liens s'il existe des documents
     * 
     * @param documents les Record document avec les propriété min code et titre
     * @param cfgDeleteRequest configuration passée à la requête Ajax de suppression sachant que l'URL de suppression
     * d'un document donné est construite de la manière suivante :  cfgDeleteRequest.urlStart + '/' + doc.get('id')
     */
    addDocuments: function(documents, cfgDeleteRequest) {
        var download = this.getComponent('download');
        download.removeAll();
        download.setVisible(true);
        if (documents.count()<1) {
            download.add({
                xtype : 'label',
                text : 'Aucun document'
            });
            return;
        }
        documents.each(function(d){
            var oneDocItems = [{
                xtype : 'linkbutton',
                text : d.get('titre'),
                src : BASE_URL+'/../telechargement/document/'+d.get('code'),
                hrefTarget: '_blank',
                margin : '0 20 0 20'
            }];
            if (cfgDeleteRequest) {
                oneDocItems.push({
                    xtype : 'button',
                    tooltip : 'Supprimer "' + d.get('titre') + '"',
                    //text : '<span>Supprimer</span>',
                    iconCls : 'deleteIcon',
                    handler : function(btn, evt) {
                        Ext.Msg.confirm('Documents', 'Confirmez-vous la suppression du document <br/>' +
                                d.get('titre') + ' ?', function(buttonId) {
                            if (buttonId == 'yes') {
                                Ext.Ajax.request(Ext.applyIf({
                                    url: cfgDeleteRequest.urlStart + '/' + d.get('id'),
                                    success: function() {
                                        Sdis.Remocra.util.Msg.msg('Documents', 'Le document a bien été supprimé.');
                                        // On supprime la ligne correspondant au document
                                        download.remove('document'+d.get('code'));
                                        if (download.items.getCount()<1) {
                                            download.add({
                                                xtype : 'label',
                                                text : 'Aucun document'
                                            });
                                        }
                                    }
                                }, cfgDeleteRequest));
                            }
                        }, this);
                    }
                });
            }
            download.add({
                xtype : 'panel',
                layout : 'column',
                itemId : 'document'+d.get('code'),
                items : oneDocItems
            });
        });
    }
});