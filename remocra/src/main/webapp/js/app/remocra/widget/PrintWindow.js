/**
 * Fenêtre d'impression dans deux modes :<br/>
 * <ul>
 * <li>mode 1 : un élément HTML et de ses fils, via son identifiant</li>
 * <li>mode 2 : une page complète via l'URl en cours.</li>
 * </ul>
 * 
 * Cette classe inspirée de loin par Ext.ux.PrinterFriendly.
 * 
 * @class Sdis.Remocra.widget.PrintWindow
 * @extends Ext.Window
 */
Ext.define('Sdis.Remocra.widget.PrintWindow', {
    extend : 'Ext.window.Window',
    alias : 'widget.printwin',

    closable : false,
    collapsible : false,
    draggable : false,
    maximizable : false,
    minimizable : false,
    titleCollapse : false,
    plain : true,
    modal : true,

    /**
     * @cfg {String} title defaults to 'Aperçu avant
     *      impression'.
     */
    /**
     * @cfg {String} closeText defaults to 'Fermer'.
     */
    /**
     * @cfg {String} printText defaults to 'Imprimer'.
     */
    /**
     * @cfg {String} eltId id of the element to print (defaults
     *      to null, equivalent to print from current URL).
     */

    initComponent : function() {
        this.id = 'printer-window';
        this.width = this.width || 800;
        this.height = this.height || 600;
        this.title = null;
        this.titleText = this.titleText
                || 'Aperçu avant impression';
        this.closeText = this.closeText || 'Fermer';
        this.printText = this.printText || 'Imprimer';

        if (this.eltId) {
            // Création d'une iframe avec le contenu d'un
            // élément html identifié
            this.html = {
                id : 'printer-iframe',
                name : 'printer-iframe',
                tag : 'iframe',
                width : '100%',
                height : '100%',
                frameborder : '0'
            };

            this.on('activate', function(window) {
                // Partie HTML à imprimer
                var printContent = '';
                var source = document.getElementById(this.eltId); // Element à imprimer
                if (!source) {
                    Ext.MessageBox.alert(this.title,
                        'Impossible de trouver l\'élément à imprimer...',
                        function() {
                            this.close();
                        }, this);
                    return;
                }
                if (source.parentNode) {
                    // Astuce pour récupérer le noeud parent et l'élément à imprimer, mais sans les
                    // autres noeuds frères
                    var target = source.parentNode.cloneNode(false);// clone 1 niveau
                    target.appendChild(source.cloneNode(true)); // clone profond
                    printContent = target.innerHTML;
                } else {
                    // Par sécurité
                    printContent = source.innerHTML;
                }

                // CSS
                var strCss = '', i;
                if (this.printCSS) {
                    if (!Ext.isArray(this.printCSS)) {
                        this.printCSS = [ this.printCSS ];
                    }
                    for (i = 0; i < this.printCSS.length; i++) {
                        strCss += String.format(
                            '<link rel="stylesheet" type="text/css" href="{0}"/>',
                            this.printCSS[i]);
                    }
                }

                // "Voile" qui empêche les  actions de l'utilisateur dans l'iframe (liens, etc.)
                var veilLayer = '<div class="veilLayer">&nbsp;</div>';

                // Construction de l'iframe
                var iFrame = document
                        .getElementById("printer-iframe");
                var ifDoc = iFrame.contentDocument
                        || iFrame.contentWindow.document;
                ifDoc.open();
                ifDoc.write(String.format(
                    '<html><head>{0}<title>{1}</title></head><body onload="{2}" ',
                    'class="print">{3}{4}</body></html>',
                    strCss,
                    this.title,
                    Ext.isIE ? 'document.execCommand(\'print\');' : 'window.print();',
                    printContent,
                    veilLayer));
                ifDoc.close();
                iFrame.style.visibility = "visible";

            }, this);

        } else {
            // Création d'une iframe avec l'adresse avec ajout
            // d'un paramètre "_format=printerfriendly" dans l'URL
            this.html = {
                id : 'printer-iframe',
                name : 'printer-iframe',
                tag : 'iframe',
                src : this.iframeSrc(),
                width : '100%',
                height : '100%',
                frameborder : '0',
                onload : 'Sdis.Remocra.widget.PrintWindow.addPrintCss();'
            };
        }

        this.dockedItems = [ {
            defaults : {
                iconCls : 'toolbar-icon',
                iconAlign : 'right'
            },
            xtype : 'toolbar',
            dock : 'top',
            cls : 'remocra-toolbar',
            items : [ {
                text : this.titleText,
                xtype : 'label',
                style : 'margin-left:30px'
            }, '->', {
                text : this.printText,
                handler : this.print,
                scope : this
            }, {
                text : this.closeText,
                handler : this.close,
                scope : this,
                style : 'margin-right:30px'
            }, '-' ]
        } ];

        this.callParent(arguments);
    },

    show : function(animateTarget, cb, scope) {
        this.callParent(arguments);
        this.maximize(true); // Parfois, il faut forcer la
                                // maximisation
    },

    print : function() {
        var iframe_window = this.getIframeWindow();
        if (Ext.isIE) {
            iframe_window.focus();
        }
        iframe_window.print();
    },

    getIframe : function() {
        return Ext.get('printer-iframe');
    },

    getIframeWindow : function() {
        return this.getIframe().dom.contentWindow;
    },

    // private
    iframeSrc : function() {
        if (this.url) {
            return this.url;
        }
        var href = window.location.href.split('#')[0];
        return href + (href.match(/\?/) ? '&' : '?')
                + '_format=printerfriendly';
    },

    statics : {
        /**
         * Impression via l'élément identifié.
         * 
         * @param {String}
         *            eltId identifiant de l'élément à imprimer
         * @param {String}
         *            title titre de l'impression
         * @param {Array}
         *            printCSS feuilles de styles à ajouter
         * @moreCfg {Object} configuration supplémentaire
         */
        showFromId : function(eltId, title, printCSS, moreCfg) {
            Sdis.Remocra.widget.PrintWindow.show(eltId, null,
                    title, printCSS, moreCfg);
        },
        /**
         * Impression via l'URL en cours.
         * 
         * @param {String}
         *            title titre de l'impression
         * @param {String}
         *            url url de la page à imprimer
         * @param {Array}
         *            printCSS feuilles de styles à ajouter
         * @moreCfg {Object} configuration supplémentaire
         */
        showFromUrl : function(url, title, printCSS, moreCfg) {
            Sdis.Remocra.widget.PrintWindow.show(null, url,
                    title, printCSS, moreCfg);
        },
        /**
         * Impression via l'URL en cours.
         * 
         * @param {String}
         *            title titre de l'impression
         * @param {Array}
         *            printCSS feuilles de styles à ajouter
         * @moreCfg {Object} configuration supplémentaire
         */
        showFromCurrentUrl : function(title, printCSS, moreCfg) {
            Sdis.Remocra.widget.PrintWindow.show(null, null,
                    title, printCSS, moreCfg);
        },
        /**
         * Impression via l'élément identifié ou via l'URL en
         * cours
         * 
         * @param {String}
         *            eltId identifiant de l'élément à imprimer.
         *            Laisser null pour imprimer par rapport à
         *            l'URL (prend le dessus sur l'URL)
         * @param {String}
         *            url identifiant de l'élément à imprimer.
         *            Laisser null pour imprimer par rapport à
         *            l'URL
         * @param {String}
         *            title titre de l'impression
         * @param {Array}
         *            printCSS feuilles de styles à ajouter
         * @moreCfg {Object} configuration supplémentaire
         */
        show : function(eltId, url, title, printCSS, moreCfg) {
            if (!title) {
                title = 'Impression';
            }
            // Reprise des feuilles de style de la page en cours
            if (!printCSS)  {
                printCSS = [];
            }
            if (!Ext.isArray(printCSS)) {
                printCSS = [ printCSS ];
            }
            var links = document.getElementsByTagName("link"), i;
            for (i = 0; i < links.length; i++) {
                if (links[i].rel == 'stylesheet'
                        && links[i].type == 'text/css') {
                    printCSS.push(links[i].href);
                }
            }
            // Ajout des feuilles de style spécifique pour l'impression
            printCSS.push(URL_SITE+'/resources/styles/remocra/main-print.css');
            printCSS.push(URL_SITE+'/ext-res/styles/remocra/override-print.css');

            var config = {
                title : title,
                printCSS : printCSS
            };
            if (eltId) {
                // Id de l'élément qu'on veut imprimer
                config.eltId = eltId;
            }
            if (url) {
                // url concernée
                config.url = url;
            }
            Ext.create('Sdis.Remocra.widget.PrintWindow',
                    Ext.applyIf(config, moreCfg || {})).show();
        },

        addPrintCss : function() {
            // Ajout des feuilles de style spécifique pour l'impression
            var printWin = Ext.getCmp('printer-window');
            var ifDom = Ext.get('printer-iframe').dom;
            var ifDoc = ifDom.contentDocument || ifDom.contentWindow.document;

            Ext.DomHelper.append(ifDoc.head, {
                tag : 'link',
                rel : 'stylesheet',
                type : 'text/css', 
                href : URL_SITE+'resources/styles/remocra/main-print.css'
            });
            Ext.DomHelper.append(ifDoc.head, {
                tag : 'link',
                rel : 'stylesheet',
                type : 'text/css',
                href : URL_SITE+'ext-res/styles/remocra/override-print.css'
            });
        }
    }
});
