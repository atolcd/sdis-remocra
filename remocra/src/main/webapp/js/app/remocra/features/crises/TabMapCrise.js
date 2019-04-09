Ext.require('Sdis.Remocra.widget.map4.Map');
//Ext.require('Vue.src.main');
Ext.define('Sdis.Remocra.features.crises.TabMapCrise', {
    extend: 'Sdis.Remocra.widget.map4.Map',
    alias: 'widget.crCrisesMapCrise',
    cls: 'hydrants-map-requetage',
    title: 'Localisation',
    width: 1250,
    height: 800,
    closable: true,
    extraLayers: null,
    idCrise: null,
    legendUrl: BASE_URL + '/../ext-res/js/app/remocra/features/crises/data/carte.json',

    listeners: {
    'afterrender': function(){
         if (Ext.isDefined(this.idCrise)) {
            if(Ext.isDefined(window.remocraVue)) {
              remocraVue.buildVue(this.id, this.idCrise);
            } else {
                console.log('Crise : remocraVue undefined');
            }
         }
    },
    'destroy': function(){
       window.remocraVue.destroyVue(this.idCrise);

    },
    'beforehide': function(){
      console.log('hide');
      //window.remocraVue.offEvent()
    }

    },
    initComponent: function() {
        this.callParent(arguments);
        //this.mapTpl = Ext.create('Ext.XTemplate','<div width="100%" height="750" class="azerty" id="'+this.id+'"></div>')
        this.mapTpl = Ext.create('Ext.XTemplate','<div id="crise-'+this.id+'"></div>');
        //on passe par vue js à commenter si on utilise ext
        this.updateTitleFiche();
        this.addExtraLayers();
        // Ext.Loader.loadScript({url:'/app.js', scope:this, onLoad: function(){this.updateIframe()},onError: function(){console.log('onerror')}});
    },

   updateTitleFiche : function() {
          if (Ext.isDefined(this.nomCrise)) {
          this.title =  this.nomCrise;
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
