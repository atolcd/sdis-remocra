Ext.ns('Sdis.Remocra.features.dfci');

Ext.require('Sdis.Remocra.widget.map.Map');
Ext.define('Sdis.Remocra.features.dfci.Map', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crDfciMap',
    

    moreItems: [
        { tooltip: 'Télécharger l\'Atlas DFCI de mon territoire', text: '<span>Télécharger l\'Atlas DFCI</span>',
            cls: 'download-atlas', iconCls: 'download-atlasIcon',
            itemId: 'downloadAtlas'
        }
    ],
    
    legendUrl: BASE_URL+'/../ext-res/js/app/remocra/features/dfci/data/carte.json',

    initComponent: function() {
        Ext.apply(this, {
            listeners: {
                afterrender: function() {
                    var downloadAtlas = this.maptbar1.getComponent('downloadAtlas');
                      if (Sdis.Remocra.Rights.hasRight('DFCI_EXPORTATLAS_C')) {
                        downloadAtlas.addListener('click', this.downloadAtlas, this);
                      }else {
                        downloadAtlas.hide();
                      }
                }
            }
        });
        this.callParent(arguments);
    },
    
    downloadAtlas: function(btn, e, eOpts) {
        Ext.Msg.confirm('Téléchargement de l\'Atlas',
            'Votre demande va être enregistrée. Lorsque le fichier sera prêt, vous serez averti par un message électronique. Souhaitez-vous continuer ?',
            function(btn) {
                if (btn == "yes"){
                    this.goDownloadAtlas();
                }
            }, this);
    },

    goDownloadAtlas: function() {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/specifique/atlas"),
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Téléchargement de l\'Atlas',
                        'Votre demande a été prise en compte.', 5);
                    
                    var downloadAtlas = this.maptbar1.getComponent('downloadAtlas');
                    downloadAtlas.setDisabled(true);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Téléchargement de l\'Atlas',
                        'Un problème est survenu lors de l\'enregistrement de la demande.' + msg + '.');
                }
            }
        });
    }


});