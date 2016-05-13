Ext.require('Ext.form.field.ComboBox');

/**
 * Combo avec "forceSelection" par défaut, et qui, en vidant la zone, vide son
 * contenu
 */

Ext.define('Sdis.Remocra.widget.ComboForce', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.comboforce',
    forceSelection: true,
    assertValue: function() {
        var me = this, value = me.getRawValue(), rec;
        if (me.forceSelection) {
            if (me.multiSelect) {
                if (value !== me.getDisplayValue()) {
                    me.setValue(me.lastSelection);
                }
            } else {
                rec = me.findRecordByDisplay(value);
                if (rec) {
                    me.select(rec);
                } else {
                    // -- début "surcharge"
                    if (Ext.isEmpty(value)) {
                        me.setValue(null);
                    } else {
                        // -- fin "surcharge"
                        me.setValue(me.lastSelection);
                    }
                }
            }
        }
        me.collapse();
    }

});
