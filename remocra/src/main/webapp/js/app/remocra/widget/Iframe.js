Ext.define('Sdis.Remocra.widget.Iframe', {
    extend: 'Ext.Component',
    alias: 'widget.remocraiframe',

    url: 'about:blank',
    
    renderTpl: [
        '<iframe id="iframeCont" src="{url}" width="100%" height="100%" frameborder="0"></iframe>'
    ],
    renderSelectors: {
        body: 'iframe'
    },
    renderData: null,   

    initComponent: function() {
        Ext.apply(this, {
            renderData: { url: this.url }
        });
        
        this.callParent(arguments);
        
        this.addEvents(
            /**
             * @event contentchanged
             * Déclenché lorsque le contenu de l'iframe a changé.
             */
            'contentchanged');
        
        this.on('render', Ext.bind(this.installLoadManagement, this));
    },

    getIframe: function() {
        return this.getTargetEl().child('iframe');
    },

    setSource: function(url) {
        this.getIframe().dom.src = this.url = url;
    },
    
    installLoadManagement: function() {
        this.on('contentchanged', Ext.bind(this.manageContentChanged, this));
        
        // Ecoute des chargements de l'iframe
        var theIFrame = document.getElementById("iframeCont");
        theIFrame[Ext.isIE ? "onreadystatechange" : "onload"] =
            Ext.bind(this.fireEvent, this, ['contentchanged']);
    
        // Chargement initial
        // Annulé car réalisé dans le render tpl
        //setSource(this.url);
        //theIFrame.src = this.url;
    },
    
    /**
     * <ul>Gestion des chargements de l'iframe :
     *     <li>Session : prolongement ou déconnexion,</li>
     *     <li>Style : css et ajustement de la taille de l'iframe.</li>
     * </ul>
     */
    manageContentChanged : function() {
        // Récupération du conteneur et des document/body de l'iframe
        var fCont = document.getElementById("iframeCont");
        var fDoc = fCont.contentWindow.document;
        
        // IE lance l'événement "onreadystatechange" plusieurs fois.
        // On filtre sur l'état "readyState" : loading, interactive, complete.
        if (Ext.isIE && fCont.readyState != "complete") {
            return;
        }
        
        // Ajustement de la taille du panel avec un léger différé pour IE
        Ext.Function.defer(this.setHeight, 10, this,  [fCont, fDoc]);
    },
    
    /**
     * <ul>Définit la taille du conteneur de l'iframe :
     *     <li>Taille par défaut si nécessaire</li>
     *     <li>Taille calculée sinon</li>
     * </ul>
     * 
     * @param fCont Conteneur de l'iframe
     * @param fDoc Document de l'iframe
     */
    setHeight : function(fCont, fDoc) {
        var fBody = fDoc.body;
        if (fBody) {
            if (this.hasAtLeastOneTag(fDoc, 'frameset')) {
                // frameset trouvé (cas problématique déjà rencontré) : hauteur par défaut
                this.setDefaultHeight(fCont);
            } else {
                // Cas général, on calcule la taille par rapport au contenu
                this.setComputedHeight(fCont, fBody);
            }
        } else {
            // Pas de body : hauteur par défaut
            this.setDefaultHeight(fCont);
        }
    },
    
    /**
     * Définit la hauteur du conteneur de l'iframe avec la hauteur calculée du contenu de l'iframe.
     * 
     * <ul>TODO : Surveiller ce comportement sur plus de cas, surtout :
     *     <li>scrollHeight pour IE9 : offet en dur ou bien somme des margins / paddings verticaux ?</li>
     *     <li>Nécessité de traiter les modes stricts / quirks ?</li>
     * </ul>
     * 
     * @param fCont Conteneur de l'iframe
     * @param fBody Body de l'iframe
     */
    setComputedHeight : function(fCont, fBody) {
        // On passe les margins et paddings à 0, sans quoi les calculs sont parfois faussés
        var fBstyle = fBody.style;
        fBstyle.margin = fBstyle.padding = 0;
        
        // On "nettoie" la hauteur. J'ai l'impression que cette opération permet au navigateur
        // de recaculer les données qui suivent, qui sont souvent trop élevées
        fCont.height = "0px";
        
        var offset = fBody.offsetHeight;
        var scroll = fBody.scrollHeight;
        if (scroll && Ext.isIE9) {
            // Taxe de passage IE9
            scroll += 16;
        }
        
        // Pour plus de sécurité, on prend dans l'ordre : max des 2, offset, scroll
        if(offset && scroll) {
            fCont.height = Math.max(offset, scroll);
        } else if(offset) {
            fCont.height = offset;
        } else if (scroll){
            fCont.height = scroll;
        } else {
            this.setDefaultHeight(fCont);
        }
        
        // That's all
    },
    
    /**
     * Fonction utilitaire qui indique si un tag est trouvé dans un document.
     * @param doc
     * @param tagName
     */
    hasAtLeastOneTag : function(doc, tagName) {
        var elements = doc.getElementsByTagName(tagName); 
        return elements && elements.length > 0;
    }
});