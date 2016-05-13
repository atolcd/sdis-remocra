Ext.define('Sdis.Remocra.features.hydrants.bloc.Entete', {
    extend: 'Ext.container.Container',
    alias: 'widget.hydrant.entete',

    border: false,
    height: 100,
    layout: 'column',
    defaults: {
        border: false,
        defaults: {
            labelAlign: 'right'
        }
    },
    items: [{
        columnWidth: 1,
        items: [{
            xtype: 'displayfield',
            fieldLabel: 'Numéro point d\'eau',
            labelWidth: 120,
            name: 'numero'
        },{
            xtype: 'displayfield',
            name: '_info',
            labelWidth: 120
        }, {
            xtype: 'image',
            style: {
                display : 'inline-block'
            },
            imgCls: 'hydrantMiniature',
            autoEl : 'a',
            height: 40
        }]
    },{
        width: 250,
        defaults: {
            xtype: 'displayfield',
            renderer: Ext.util.Format.dateRenderer('d/m/Y')
        },
        items: [{
            fieldLabel: 'Réception',
            name: 'dateRecep'
        },{
            fieldLabel: 'Contrôle',
            name: 'dateContr'
        },{
            fieldLabel: 'Reconnaissance',
            name: 'dateReco'
        },{
            fieldLabel: 'Vérification',
            name: 'dateVerif'
        }]
    },{
        width: 250,
        items: [{
            fieldLabel: 'Disponibilité',
            xtype: 'displayfield',
            value: 'Opérationnelle',
            fieldCls: 'hydrant-disponibilite',
            name: 'dispo-op'
        }/*,{
            xtype: 'displayfield',
            value: 'Administrative',
            fieldCls: 'hydrant-disponibilite',
            name: 'dispo-admin'
        },*/,{
            margin: '0 0 5 105',
            xtype: 'displayfield',
            value: 'HBE',
            fieldCls: 'hydrant-disponibilite',
            name: 'dispo-hbe'
        }]
    }]

});