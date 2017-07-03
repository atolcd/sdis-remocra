Ext.ns('Sdis.Remocra.widget');

Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.widget.LinkButton');

Ext.define('Sdis.Remocra.widget.FileUploadMultiPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.crFileuploadMulti',
    margin: 10,
    border: false,

    docToDelete: [],

    items: [{
        cls: 'fileuploadpanel-down',
        xtype: 'fieldset',
        itemId: 'download',
        title: 'Documents',
        defaults: {
            border: false
        },
        items: {
            xtype: 'dataview',
            tpl: new Ext.XTemplate(
                '<tpl for=".">',
                // Un document
                '<tpl if="this.isGroupCode(group)">',
                // Groupe courant
                '<div class="{[values.phantom ? "notstored" : "stored"]}" id="{code}">',
                // Bouton de suppression
                '<div class="x-btn x-column x-btn-default-small x-icon x-btn-icon x-btn-default-small-icon" style="border-width:1px 1px 1px 1px;"',
                ' onclick="Ext.getCmp(\'{[this.getId()]}\').fireEvent(\'deleteDoc\', {id}, {values});">',
                '<em><button type="button" class="x-btn-center" hidefocus="true" role="button" autocomplete="off" ',
                'data-qtip="Supprimer &quot;{titre}&quot;" style="height: 16px;">',
                '<span class="x-btn-inner" style="">&nbsp;</span>',
                '<span class="x-btn-icon deleteIcon"></span></button></em></div>',
                //
                '<div class="x-component linkbutton x-column x-component-default" style="margin:0px 20px 0px 20px;border-width:0;line-height: 20px;">',
                // Nouveau document
                '<tpl if="phantom">', '{titre}', '</tpl>',
                // Document téléchargeable
                '<tpl if="!phantom">', '<a href="telechargement/document/{code}"  target="_blank">{titre}</a>', '</tpl>',
                //
                '</div>', '<div class="x-clear"></div>', '</tpl>', '</tpl>', {
                    isGroupCode: function(group) {
                        return !this.groupCode || group == this.groupCode;
                    },
                    setId: function(id) {
                        this.id = id;
                    },
                    getId: function() {
                        return this.id;
                    }
                }),
            multiSelect: false,
            trackOver: true,
            overItemCls: 'x-item-over',
            itemSelector: 'div.thumb-wrap',
            emptyText: 'Aucun document'
        }
    }, {
        xtype: 'numberfield',
        allowDecimals: false,
        allowNegative: true,
        width: 150,
        fieldLabel: 'Nouveaux :',
        text: 'Nouveaux :',
        labelSeparator: '',
        hideTrigger: true,

        submitValue: false,
        itemId: 'fileCounter',
        readOnly: true,
        cls: 'remocra-field-read-only',
        value: 0,
        labelWidth: 125
    }, {
        border: false,
        html: '<p style="font-style:italic;'
        + 'color:#a9a9a9;padding-bottom:20px">'
        + 'Pour <b>ajouter ' + 'un document</b>'
        + ', cliquez sur le ' + 'bouton "Nouveau..." et '
        + 'sélectionnez le fichier ' + 'à transmettre.<br/>Pour <b>annuler '
        + 'l\'envoi d\'un fichier</b>' + ', cliquez sur le bouton "Supprimer"'
        + ' en face du document ' + 'concerné.</p>'
    }, {
        fieldLabel: null,
        xtype: 'filefield',
        buttonText: '<span>Nouveau...</span>',
        submitValue: true,
        width: 200,
        allowBlank: true
    }],

    setGroupCode: function(groupCode) {
        this.groupCode = groupCode;
        var filefields = this.query('.filefield');
        // Tous les "orphelins" et le nouveau reprennent le nouveau code
        Ext.each(filefields, function(filefield) {
            if (!filefield.code || !filefield.getValue()) {
                var fileCode = groupCode + '%' + Sdis.Remocra.util.Util.guid();
                filefield.code = fileCode;
                if (filefield.getEl()) {
                    var fileinput = Ext.DomQuery.select('input.x-form-file-input', filefield.getEl().dom)[0];
                    fileinput.name = fileCode;
                    fileinput.setAttribute('data', JSON.stringify({
                        code: fileCode
                    }));
                } else {
                    filefield.on('render', function() {
                        var fileinput = Ext.DomQuery.select('input.x-form-file-input', filefield.getEl().dom)[0];
                        fileinput.name = fileCode;
                        fileinput.setAttribute('data', JSON.stringify({
                            code: fileCode
                        }));
                    }, {
                        single: true
                    });
                }
            }
        });
        var dataview = this.query('.dataview')[0];
        dataview.tpl.groupCode = groupCode;
        dataview.refresh();
    }
});