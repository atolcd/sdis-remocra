

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
            // On demande toujours le rafraichissement de la grille des hydrants
            // ainsi que de la carte
            this.getController('hydrant.Hydrant').refreshMap();
            this.getController('hydrant.Hydrant').updateHydrant();
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
        fiche.setTitle((fiche.hydrant.get('code') == 'PIBI' ? 'PIBI' : 'PENA')+" n° "+fiche.hydrant.data.numero+" - "+fiche.hydrant.data.nomCommune);
        window.remocraVue.buildFiche(fiche);
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
            button.up('window').close();
        }
    }
});

 