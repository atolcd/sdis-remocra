/**
 * Classe utilitaire
 */

Ext.ns('Sdis.Remocra.util');
Ext.define('Sdis.Remocra.util.Msg', {
            singleton: true,  
    /*******************************************/
    /* Gestion des messages furtifs            */
    /*******************************************/
    /**
     * Affiche un message furtif.
     * @param {String} title titre de la fenêtre
     * @param {String} content contenu du message
     * @param {Integer} timeSec temps d'affichage (optionnel)
     */
    msg: function(title, content, timeSec) {
        if(!this.msgCt) {
            this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-ct'}, true);
            this.msgCt.setStyle('position', 'absolute');
            this.msgCt.setStyle('z-index', 20000);
            this.msgCt.setWidth(300);
        }
        timeSec=timeSec===undefined?2:timeSec;
        //this.msgCt.alignTo(document, 'tr');
        this.msgCt.alignTo("pageTop", 'tr?', [-500, 0]);
        //var s = String.format.apply(String, Array.prototype.slice.call(arguments, 1));
        var s = content;
        var m = Ext.DomHelper.append(this.msgCt, {html:this.createBox(title, s)}, true);
        var elt = m.slideIn('t').ghost("t", {delay:timeSec*1000 , remove:true});
    },
    // private
    msgCt: null,
    createBox : function(t, s) {
        return ['<div class="msg">',
        '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
        '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', t, '</h3>', s, '</div></div></div>',
        '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
        '</div>'].join('');
    },
    idSeed: 0,
    id: function() {
        return ++this.idSeed;
    }

});