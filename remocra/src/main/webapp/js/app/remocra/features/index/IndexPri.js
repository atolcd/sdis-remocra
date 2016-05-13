
Ext.require('Ext.XTemplate');
Ext.require('Sdis.Remocra.features.index.BlocsFactory');
Ext.require('Sdis.Remocra.store.BlocDocument');

Ext.define('Sdis.Remocra.features.index.IndexPri', {
    extend: 'Ext.Panel',
    alias: 'widget.crIndexPri',

    title: 'Plateforme départementale des risques REMOcRA',
    id: 'indexpri',
    
    url: null,

    layout:'column',
    defaults: { },
    
    blocTpl: new Ext.XTemplate(
            '<div class="index-bloc-priv" style="',
            '<tpl if="typeof(img)==\'string\'">',
                'background-image:url(\'',
                // URL relative
                '<tpl if="img.indexOf(\'http\')==-1">',
                    'ext-res/images/remocra/blocs/{img}',
                '</tpl>',
                // URL absolue
                '<tpl if="img.indexOf(\'http\')!==-1">',
                    '{img}',
                '</tpl>',
                '\');background-repeat:no-repeat;',
            '</tpl>',
            'min-height:{[this.getMinH(arguments[1])]}px;',
            '">',
                '<tpl if="typeof(items)==\'object\'">',
                    '<ul style="',
                        '<tpl if="this.hasImg(img)">',
                            'margin-left: 90px;',
                        '</tpl>',
                    '">',
                        '<tpl for="items">',
                            '<tpl if="this.itemIsSep(type)">',
                                '<li style="list-style-image:none;height:17px;"></li>',
                            '</tpl>',
                            '<tpl if="this.itemIsHtml(type)">',
                                '<div class="html-content">{content}</div>',
                            '</tpl>',
                            '<tpl if="this.itemIsGen(type)">',
                                '<li><a href="#{href}" style="text-decoration:none;font-size:15px;"',
                                '<tpl if="onclick">',
                                    '{onclick}',
                                '</tpl>',
                                '>{lbl}</a></li>',
                            '</tpl>',
                        '</tpl>',
                    '</ul>',
                '</tpl>',
                '<tpl if="typeof(html)==\'string\'">',
                    '<div style="',
                        '<tpl if="this.hasImg(img)">',
                            'margin-left: 140px;',
                        '</tpl>',
                    '">',
                        '{html}',
                    '</div>',
                '</tpl>',
            '</div>',
        {
            hasItems: function(items) {
                return items !== null;
            },
            hasHtml: function(html) {
                return html !== null;
            },
            getMinH: function(args) {
                return typeof(args.minh)=="number"?args.minh:100;
            },
            hasImg: function(img) {
                return img !== null;
            },
            itemIsSep: function(type) {
                return type == 'sep';
            },
            itemIsHtml: function(type) {
                return type == 'html';
            },
            itemIsGen: function(type) {
                return type == 'href';
            }
        }
    ),
        
    initComponent: function() {
        Ext.getCmp('backHome').setVisible(false);
        
        Ext.apply(this, {
            defaults: { border: false, defaults: { border: false} }, // les columnWidth sont déterminés après le calcul des colonnes
            items: [],
            buttonAlign: 'left'
        });
        
        this.callParent(arguments);
        
        this.loadData();
    },
    
    // Crée une grille à partir d'un tableau de colonnes de configurations de blocs
    createGrid: function(gridCfg) {
        var returned = [];
        // Une passe pour créer les colonnes de blocs (si non vides)
        var colCfgi;
        for(colCfgi in gridCfg) {
            var colCfg = gridCfg[colCfgi];
            if (colCfg.length>0) {
                var col = [], i;
                for(i in colCfg) {
                    var blocCfg = colCfg[i];
                    if (blocCfg) {
                        col.push(this.createBloc(blocCfg));
                    }
                }
                returned.push({items: col, columnWidth: 1/2});
            }
        }
        // Une passe sur les colonnes pour fixer les columnWidth
        if (returned.length>0) {
            var returnedColi;
            for(returnedColi in returned) {
                returned[returnedColi].columnWidth = 1/returned.length;
            }
        }
        return returned;
    },
    
    // Crée un bloc à partir d'une configuration de base (ajoute  la config qui va bien)
    createBloc: function(blocCfg) {
        var bloc = {
            style: 'margin: 0px 10px 10px 10px',
            bodyStyle: 'padding: 0px 0 10px 10px',
            cls: 'index-bloc',
            title: '<div class="x-panel-header-text">'+blocCfg.title+'</div>',
            items: { html: this.blocTpl.apply(blocCfg) }
        };
        // Quand le composant effectue son rendu dans le bloc
        if (blocCfg.onBlocRender) {
            bloc.items.listeners = {'render': blocCfg.onBlocRender};
        }
        return bloc;
    },
    
    loadData: function() {
        Ext.Ajax.request({
            url: BASE_URL+'/../ext-res/html/index/index-pri.json',
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success !== true) {
                    Ext.Msg.alert('Index', 'Un problème est survenu lors du chargement de la configuration.');
                    return;
                }
                // On renseigne les données des couches
                var cfgBlocs = Ext.decode(response.responseText);
                var jsBlocs = Sdis.Remocra.features.index.BlocsFactory.createBlocsGridFromCfg(cfgBlocs);
                var panelBlocs = this.createGrid(jsBlocs); 
                this.add(panelBlocs);
            }
        });
    }
});