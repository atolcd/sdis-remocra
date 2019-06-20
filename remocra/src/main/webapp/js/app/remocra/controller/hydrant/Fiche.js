Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.model.Voie');
Ext.require('Sdis.Remocra.store.TypeHydrant');
Ext.require('Sdis.Remocra.store.TypeHydrantAnomalie');
Ext.require('Sdis.Remocra.store.TypeHydrantDiametre');
Ext.require('Sdis.Remocra.store.TypeHydrantDomaine');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureTous');
Ext.require('Sdis.Remocra.store.TypeHydrantVolConstate');
Ext.require('Sdis.Remocra.store.TypeHydrantMarque');
Ext.require('Sdis.Remocra.store.TypeHydrantPositionnement');
Ext.require('Sdis.Remocra.store.TypeHydrantMateriau');
Ext.require('Sdis.Remocra.store.Utilisateur');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureDeci');
Ext.require('Sdis.Remocra.store.TypeHydrantNatureDeciTous');
Ext.require('Sdis.Remocra.model.HydrantPena');
Ext.require('Sdis.Remocra.model.HydrantPibi');

Ext.require('Sdis.Remocra.features.hydrants.FichePena');
Ext.require('Sdis.Remocra.features.hydrants.FichePibi');

Ext.require('Sdis.Remocra.widget.SdisChoice');

Ext.define('Sdis.Remocra.controller.hydrant.Fiche', {
    extend: 'Ext.app.Controller',

    stores: ['Commune', 'Voie', 'Hydrant', 'TypeHydrant', 'TypeHydrantAnomalie', 'TypeHydrantDiametre', 'TypeHydrantDomaine',
             'TypeHydrantNature', 'TypeHydrantNatureTous', 'TypeHydrantVolConstate',
             'TypeHydrantMarque', 'Utilisateur', 'TypeHydrantPositionnement', 'TypeHydrantMateriau', 'TypeHydrantNatureDeci', 'TypeHydrantNatureDeciTous'],

    init: function() {

        this.control({
            'hydrantFiche': {
                afterrender: this.onAfterRenderFiche,
                close: this.onClose
            },
            'hydrantFiche #ok': {
                click: this.updateHydrant
            }
        });
    },

    onClose: function(fiche) {
        if (fiche.ficheParente == null) {
            if (fiche.hydrant.feature) {
                fiche.hydrant.feature.destroy();
            }
        }
    },

    showFiche: function(hydrant, controle) {
        var xtype = null;
        if (hydrant != null) {
            switch (hydrant.get('code')) {
            case 'PENA':
                xtype = 'hydrant.fichepena';
                break;
            case 'PIBI':
                xtype = 'hydrant.fichepibi';
                break;
            }
            if (xtype != null) {
                Ext.widget(xtype, {
                    hydrant: hydrant
                }).show();
            } else {
                console.warn('xtype is null', hydrant);
            }
        } else {
            console.warn('hydrant is null');
        }
    },

    onAfterRenderFiche: function(fiche) {
        var codeHydrant = (fiche.hydrant.get('code') == 'PIBI' ? 'PIBI' : 'PENA');
        var geometrie = fiche.hydrant.data.geometrie;
        var idHydrant = fiche.hydrant.data.id;
        fiche.setTitle(idHydrant ?
            codeHydrant + " n° " + fiche.hydrant.data.numero + " - " + fiche.hydrant.data.nomCommune
            : 'Nouveau ' + codeHydrant);
        var vueFiche = window.remocraVue.peiBuildFiche('#'+fiche.getId()+'-body', {
            id: idHydrant, code: codeHydrant, geometrie: geometrie
        });
        // Lorsque le PEI est modifié, on informe le contrôleur
        vueFiche.$options.bus.$on('pei_modified', Ext.bind(function(data) {
            this.getController('hydrant.Hydrant').hydrantsChanged();
            fiche.close();
        }, this));
        fiche.on('destroy', function() {
            vueFiche.$destroy();
        });
    },

    updateHydrant: function(button){
        var hydrant = null;
        var record = document.getElementsByClassName('Fiche')[0].__vue__.$data['hydrant'];
        if(record.code == "PIBI"){
            hydrant = Ext.create('Sdis.Remocra.model.HydrantPibi', record);
        }
        else{
            hydrant = Ext.create('Sdis.Remocra.model.HydrantPena', record);
        }
        var formValid = document.getElementsByClassName('Fiche')[0].__vue__.checkFormValidity();
        if(hydrant != null && formValid){
            var url = hydrant.getProxy().url + (hydrant.phantom ? '' : '/' + hydrant.getId());
            document.getElementsByClassName('Fiche')[0].__vue__.handleSubmit(url);
        }
    }
});

 