Ext.require(['Sdis.Remocra.features.rci.Map']);

Ext.define('Sdis.Remocra.features.rci.Rci', {
    extend : 'Ext.Panel',
    alias : 'widget.crRci',

    title : 'Carte des départs de feu',
    id : 'rci',

    initComponent : function() {
        if (!Sdis.Remocra.Rights.hasRight('RCI_C')) {
            this.items = [{
                html : 'Vous ne disposez pas de droits nécessaires pour accéder à ce module.'
            }];
            this.callParent(arguments);
            return;
        }
        this.items = [ {
            xtype : 'crRciMap'
        } ];
        if (Sdis.Remocra.Rights.hasRight('DOCUMENTS_R')) {
            this.items.push({
                xtype : 'crBlocDocumentGrid',
                thematiques : 'RCI'
            });
        }
        this.callParent(arguments);
    }
});