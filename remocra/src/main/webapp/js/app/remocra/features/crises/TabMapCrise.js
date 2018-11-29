Ext.require('Sdis.Remocra.widget.map4.Map');
Ext.define('Sdis.Remocra.features.crises.TabMapCrise', {
    extend: 'Sdis.Remocra.widget.map4.Map',
    alias: 'widget.crCrisesMapCrise',
    cls: 'hydrants-map-requetage',
    title: 'Localisation',
    width: 1250,
    closable: true,
    extraLayers: null,
    idCrise: null,
    legendUrl: BASE_URL + '/../ext-res/js/app/remocra/features/hydrants/data/carte-crise.json',
    initComponent: function() {
        this.callParent(arguments);
        //on passe par vue js à commenter si on utilise ext
        this.updateIframe();
        this.updateTitleFiche();
        this.addExtraLayers();
    },

   updateTitleFiche : function() {
          if (Ext.isDefined(this.nomCrise)) {
          this.title =  this.nomCrise;
       }
   },

   updateIframe : function() {
      if (Ext.isDefined(this.idCrise)) {
        this.mapTpl = Ext.create('Ext.XTemplate', '<iframe id="olmapCrise" src ="'
            + window.document.location.protocol + '//' + window.document.location.hostname
            + ':8081/#/olmap?hash='+this.idCrise
        +'" width="100%" height="750" allowfullscreen="true" frameBorder="0">/iframe>');

      }
   },
    getNomCrise : function() {
       return this.nomCrise;
    },

    getIdCrise : function() {
           return this.idCrise;
    },

    getCarteCrise: function() {
      return this.carteCrise;
    },

   addExtraLayers : function(){
     if (Ext.isDefined(this.carteCrise)) {
        this.extraLayers = this.carteCrise;
     }
   }

});
