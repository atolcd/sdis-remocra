/*!
 * CTemplate
 * Version 1.0
 * Copyright(c) 2011 Skirtle's Den
 * License: http://skirtlesden.com/ux/ctemplate
 */
Ext.define('Skirtle.CTemplate', {
    extend: 'Ext.XTemplate',

    // May need to be increased if components are included deeper in the data object
    copyDepth: 10,

    // Placeholder element template. Should be changed in tandem with getPlaceholderEl()
    cTpl: '<div id="ctemplate-{0}"></div>',

    // Flag
    isCTemplate: true,

    constructor: function() {
        this.callParent(arguments);

        this.reset();
    },

    // Override
    applyTemplate: function(values) {
        // As we're returning an HTML string we can't actually complete the injection here
        this.doPolling(10);

        return this.callParent([this.copyValues(values)]);
    },

    /* Takes a recursive copy of the values provided, switching out components for placeholder values. The component ids
     * are recorded and injectComponents() uses the ids to find the placeholder elements in the DOM and switch in the
     * components.
     */
    copyValues: function(values, depth) {
        var me = this,
            id,
            copy = {},
            copyDepth = depth || me.copyDepth;

        if (copyDepth === 1) {
            return values;
        }

        if (Ext.isArray(values)) {
            return Ext.Array.map(values, function(value) {
                return me.copyValues(value, copyDepth - 1);
            });
        }

        if (!Ext.isObject(values)) {
            return values;
        }

        // This is the key sleight-of-hand that makes the whole thing work
        if (values.isComponent) {
            id = values.getId();
            me.ids.push(id);
            return Ext.String.format(me.cTpl, id);
        }

        Ext.Object.each(values, function(key, value) {
            copy[key] = me.copyValues(value, copyDepth - 1);
        });

        return copy;
    },

    // Override
    doInsert: function() {
        var ret = this.callParent(arguments);

        // There's no guarantee this will succeed so we still need polling as well
        this.injectComponents();

        return ret;
    },

    /* We have to resort to polling for component injection as we don't have full control over when the generated HTML
     * will be added to the DOM
     */
    doPolling: function(interval) {
        var me = this;

        me.pollInterval = interval;

        if (me.pollId) {
            clearTimeout(me.pollId);
        }

        me.pollId = Ext.defer(me.injectComponents, interval, me);
    },

    getPlaceholderEl: function(id) {
        return Ext.get('ctemplate-' + id);
    },

    /* Attempts to substitute all placeholder elements with the real components. If a component is successfully injected
     * or it has been destroyed then it won't be attempted again. This method is repeatedly invoked by a polling
     * mechanism until no components remain, however relying on the polling is not advised. Instead it is preferable to
     * call this method directly as soon as the generated HTML is inserted into the DOM.
     */
    injectComponents: function() {
        var me = this,
            ids = me.ids,
            index = ids.length - 1,
            id,
            cmp,
            placeholderEl;

        // Iterate backwards because we remove some elements in the loop
        for ( ; index >= 0 ; --index) {
            id = ids[index];
            cmp = Ext.getCmp(id);
            placeholderEl = me.getPlaceholderEl(id);

            if (me.renderComponent(cmp, placeholderEl) || !cmp) {
                // Either we've successfully done the switch or the component has been destroyed
                Ext.Array.splice(ids, index, 1);

                if (placeholderEl) {
                    placeholderEl.remove();
                }
            }
        }

        if (ids.length) {
            // Some components have not been injected. Polling acts both to do deferred injection and as a form of GC
            me.doPolling(me.pollInterval * 1.5);
        }
    },

    // Override
    overwrite: function() {
        var ret = this.callParent(arguments);

        // There's no guarantee this will succeed so we still need polling as well
        this.injectComponents();

        return ret;
    },

    renderComponent: function(cmp, placeholderEl) {
        if (cmp && placeholderEl) {
            var parent = placeholderEl.parent();

            cmp.render(placeholderEl.parent(), placeholderEl);

            if (Ext.isIE6) {
                // Some components (mostly form fields) reserve space but fail to show up without a repaint in IE6
                parent.repaint();
            }

            return true;
        }

        return false;
    },

    reset: function() {
        var me = this;

        // The ids of injected components that haven't yet been rendered
        me.ids = [];

        if (me.pollId) {
            clearTimeout(me.pollId);
            me.pollId = null;
        }
    }
}, function() {
    this.createAlias('apply', 'applyTemplate');
});