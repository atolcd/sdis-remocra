/**
 * Classe utilitaire
 */

//Ext.require('Sdis.Remocra.util.Util');

Ext.ns('Sdis.Remocra.util');
Ext.define('Sdis.Remocra.util.Msg', {
	singleton: true,  
    
	count:0,
		
	/*******************************************/
    /* Mock class pour remplacer l'affichage des messages dans les tests           */
    /*******************************************/
    /**
     * Affiche un message furtif.
     * @param {String} title titre de la fenêtre
     * @param {String} content contenu du message
     * @param {Integer} timeSec temps d'affichage (optionnel)
     */
    msg: function(title, content, timeSec) {
        this.count = this.count + 1;
    },

	getCount:function(){
		return count;
	}

});