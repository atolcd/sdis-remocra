Ext.require('Sdis.Remocra.widget.FileUploadMultiPanel');
Ext.require('Sdis.Remocra.model.FileUploadMulti');

Ext.define('Sdis.Remocra.controller.widget.FileUploadMultiPanel', {
    extend: 'Ext.app.Controller',

    stores: [],

    refs: [{
        ref: 'fileuploadMulti',
        selector: 'crFileuploadMulti'
    }, {
        ref: 'fileCounter',
        selector: 'crFileuploadMulti #fileCounter'
    }, {
        ref: 'dataview',
        selector: 'crFileuploadMulti dataview'
    } ],

    init: function() {
        this.control({
            'crFileuploadMulti': {
                render: function() {
                    var fileuploadMulti = this.getFileuploadMulti();
                    if (fileuploadMulti.groupCode) {
                        // On initialise le premier avec le code
                        this.setGroupCode(fileuploadMulti.groupCode);
                    }
                }
            },

            '.crFileuploadMulti .filefield': {
                change: this.addDocument,
                render: this.addRemoveBtn
            },

            'crFileuploadMulti .dataview': {
                refresh: this.dataviewRefresh,
                deleteDoc: this.deleteDoc
            }
        });
    },

    dataviewRefresh: function() {
        var newCount = 0;
        var dataview = this.getDataview();
        var groupCode = dataview.tpl.groupCode;
        dataview.getStore().each(function(record) {
            if (record.get('phantom') === true && record.get('group') == groupCode) {
                newCount += 1;
            }
        });
        this.setCounter(newCount);
    },

    deleteDoc: function(id) {
        var dataview = this.getDataview();
        var store = dataview.getStore();
        var d = store.getAt(store.findExact('id', id));

        var dExists = d.getId();
        Ext.Msg.confirm('Documents', 'Confirmez-vous ' + (dExists ? 'la suppression' : 'l\'annulation') + ' du document <br/>' + d.get('titre')
                + ' ?', function(buttonId) {
            if (buttonId == 'yes') {
                if (dExists) {
                    // Document à supprimer côté serveur
                    this.getFileuploadMulti().docToDelete.push(id);
                } else {
                    // Document qui n'est pas encore posté : on retire le
                    // fileupload
                    var fileupload = Ext.getCmp(d.get('fileuploadid'));
                    fileupload.ownerCt.remove(fileupload);
                }
                // Retrait du document et actualisation du store
                store.remove(d);
                dataview.refresh();
            }
        }, this);
    },

    setGroupCode: function(groupCode) {
        this.getFileuploadMulti().setGroupCode(groupCode);
    },

    resizeInputs: function() {
        // On redéfinit la largeur des noeuds input
        var inputToResize = Ext.DomQuery.select('input.x-form-text', this.getFileuploadMulti().getEl().dom);
        var i;
        for (i = 0; i < inputToResize.length; i++) {
            inputToResize[i].style.width = '130px';
        }
    },

    addDocument: function(fileupload, value, opts) {
        var dataview = this.getDataview();
        var groupCode = this.getFileuploadMulti().groupCode;
        var fileCode = groupCode + '%' + Sdis.Remocra.util.Util.guid();

        dataview.getStore().add({
            id: null,
            titre: fileupload.fileInputEl.dom.files[0].name,
            code: fileCode,
            group: groupCode,
            'phantom': true,
            'fileuploadid': fileupload.getId()
        // pour supprimer le fileupload avec le document
        });
        dataview.refresh();
        fileupload.hide();

        this.getFileuploadMulti().add({
            fieldLabel: null,
            xtype: 'filefield',
            buttonText: '<span>Nouveau...</span>',
            submitValue: true,
            width: 200,
            allowBlank: true,
            code: fileCode,
            listeners: {
                'render': function() {
                    var fileinput = Ext.DomQuery.select('input.x-form-file-input', this.getEl().dom)[0];
                    fileinput.name = fileCode;
                    fileinput.setAttribute('data', JSON.stringify({
                        code: fileCode
                    }));
                }
            }
        });
        fileupload.buttonEl.setVisible(false);
        fileupload.buttonEl.setStyle('display', 'none');
        fileupload.buttonDelete.setVisible(true);
        this.resizeInputs();
    },
    removeDocument: function(btn, evt, filefield) {
        this.getFileuploadMulti().remove(filefield);
        this.resizeInputs();
    },

    addRemoveBtn: function(cmp) {
        var parent = cmp.browseButtonWrap || cmp.bodyEl;
        cmp.buttonDelete = Ext.widget('button', {
            ui: cmp.ui,
            renderTo: parent,
            text: 'Retirer',
            width: 66,
            preventDefault: true,
            style: 'margin-left:3px',
            enableToggle: false,
            hidden: true,
            handler: Ext.bind(this.removeDocument, this, [cmp ], true)
        });

    },

    setCounter: function(count) {
        this.getFileCounter().setValue(count);
    },

    submit: function(opts) {
        this.getFileuploadMulti().submit(opts);
    }
});
