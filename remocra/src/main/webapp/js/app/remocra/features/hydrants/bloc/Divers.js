Ext.require ('Ext.form.FieldSet');
Ext.require ('Ext.form.Panel');
Ext.require ('Ext.form.field.File');
Ext.require ('Sdis.Remocra.widget.LinkButton');

Ext.define ('Sdis.Remocra.features.hydrants.bloc.Divers',{
    extend: 'Ext.form.FieldSet',
    title: 'Divers',
    alias: 'widget.hydrant.divers',

    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 200,
        maxWidth: 500
    },

    items: [ {
        fieldLabel: 'Courrier/convention',
        xtype: 'textfield',
        name: 'courrier'
    },{
        // on met un hidden pour stocker l'identifiant de la photo.
        // on surcharge le processRawValue pour toujours avoir une valeur
        // numérique (représentant l'identifiant)
        xtype: 'hidden',
        name: 'photo'
    },{
        xtype: 'form',
        border: false,
        defaults: {
            anchor: '100%',
            labelAlign: 'right',
            labelWidth: 200,
            maxWidth: 500
        },
        name: 'formPhoto',
        items: {
            fieldLabel: 'Photo',
            xtype: 'filefield',
            buttonText: '<span>Choisir ...</span>',
            submitValue: false,
            allowBlank: true
        }
    },{
        xtype: 'fieldcontainer',
        layout: 'hbox',
        fieldLabel: 'Photo',
        name: 'photoContainer',
        items: [ {
            xtype: 'linkbutton',
            name: 'download',
            text: 'Télécharger',
            margin: '0 20 0 0'
        },{
            xtype: 'button',
            name: 'deletePhoto',
            text: 'Supprimer'
        } ]
    },{
        fieldLabel: 'Date de dernière attestation',
        xtype: 'datefield',
        format: 'd/m/Y',
        name: 'dateAttestation'
    } ]
});