Ext.require(['Ext.form.FieldContainer','Ext.view.View']);

Ext.define('Sdis.Remocra.features.metadonnees.Metadonnees', {
    extend: 'Ext.Panel',
    alias: 'widget.crMetadonnees',

    requires: ['Ext.form.FieldContainer','Ext.view.View'],

    title: 'Métadonnées',
    id: 'metadonnees',

    initComponent: function() {
        this.items = [{
            xtype: 'fieldcontainer',
            fieldLabel: "Afficher les métadonnées de ",
            labelWidth: 200,
            width: 600,
            layout: 'hbox',
            items: [{
                xtype: 'combo',
                store: 'Thematique',
                queryMode: 'local',
                valueField: 'id',
                displayField: 'nom',
                forceSelection: true
            },{
                xtype: 'button',
                itemId: 'backButton',
                text: 'Retour à la thématique',
                margin: '0 0 0 30',
                hidden: true
            } ]
        },{
            xtype: 'button',
            itemId: 'telechargerButton',
            text: 'Télécharger les données',
            margin: '10 10 10 10',
            hidden: true
        },{
            xtype: 'dataview',
            itemSelector: 'div.metadonnee',
            store: 'Metadonnee',
            overItemCls: 'over',
            tpl: ['<tpl for="."><div class="metadonnee">','<h2>{titre}</h2>','<img src="{urlVignette}" />','<p>{resume}</p>','</div></tpl>'],
            hidden: true,
            trackOver: true
        },{
            itemId: 'contentPanel',
            xtype: 'component',
            renderTpl: ['<iframe id="iframeCont" src="{url}" width="100%" height="610" frameborder="0"></iframe>'],
            renderSelectors: {
                iframe: 'iframe'
            },
            hidden: true
        }];

        this.callParent(arguments);
    }

});