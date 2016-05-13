Ext.require('Ext.form.field.Trigger');
Ext.require('Ext.picker.Color');

Ext.define('Sdis.Remocra.widget.ColorPickerCombo', {
    extend : 'Ext.form.field.Trigger',
    alias : 'widget.colorcombo',

    triggerTip : 'Veuillez sélectionner une couleur.',

    onTriggerClick : function() {
        this.picker.alignTo(this.inputEl, 'tl-bl?');
        this.picker.show(this.inputEl);
    },

    initComponent : function() {
        this.callParent(arguments);

        // Création du picker
        this.picker = Ext.create('Ext.picker.Color', {
            pickerField : this,
            ownerCt : this,
            renderTo : document.body,
            floating : true,
            hidden : true,
            focusOnShow : true,
            style : 'background-color:white',
            listeners : {
                scope : this,
                select : function(field, value, opts) {
                    this.setValue('#' + value);
                    this.picker.hide();
                },
                show : function(field, opts) {
                    field.getEl().monitorMouseLeave(500, field.hide, field);
                }
            }
        });

        // On met à jour la couleur sur le rendu puis lorsque la valeur de la
        // combo change
        this.on('render', function(cmp, eOpts) {
            this.updGuiColor();
            this.on('change', function(cmp, newValue, oldValue, eOpts) {
                this.updGuiColor();
            }, this);
        });

        // Comme on a créé un ColorPicker à la volée, on le détruit quand c'est
        // nécessaire
        this.on('beforedestroy', function(cmp, eOpts) {
            this.picker.destroy();
        });
    },

    // Mise à jour graphique de la couleur
    updGuiColor : function() {
        // Border autour de la combo de la combo
        this.inputEl.setStyle({
            borderColor : this.getValue() || '#a1a1a1'
        });
        // Carré gauche de la combo
        this.inputEl.parent().setStyle({
            borderColor : this.getValue() || 'transparent',
            borderWidth : this.inputEl.getHeight() + 'px',
            borderLeftStyle : 'solid'
        });
    }
});