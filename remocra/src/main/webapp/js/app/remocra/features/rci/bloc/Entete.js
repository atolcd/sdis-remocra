Ext.define('Sdis.Remocra.features.rci.bloc.Entete', {
    extend : 'Ext.container.Container',
    alias : 'widget.rciEntete',

    style : 'margin-left:20px;',
    defaults : {
        labelWidth : 600,
        labelSeparator : ''
    },

    items : [ {
        xtype : 'displayfield',
        fieldLabel : 'Dernière modification le 05/04/2014 par Charles-Henry Vagner',
        itemId : 'info'
    } ]
});