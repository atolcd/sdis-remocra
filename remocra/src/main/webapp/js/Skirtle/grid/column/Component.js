/*!
 * Component Column
 * Version 1.0
 * Copyright(c) 2011 Skirtle's Den
 * License: http://skirtlesden.com/ux/component-column
 */
Ext.define('Skirtle.grid.column.Component', {
    alias: 'widget.componentcolumn',
    extend: 'Ext.grid.column.Column',
    requires: ['Skirtle.CTemplate'],

    // Whether or not to automatically resize the components when the column resizes
    autoWidthComponents: true,

    // Whether or not to destroy components when they are removed from the DOM
    componentGC: true,

    /* Defer durations for updating the component width when a column resizes. Required when a component has an animated
     * resize that causes the scrollbar to appear/disappear. Otherwise the animated component can end up the wrong size.
     */
    widthUpdateDelay: [10, 400],

    constructor: function(cfg) {
        var me = this;

        me.callParent(arguments);

        // Array of component ids for component GC
        me.compIds = [];

        // We need a dataIndex, even if it doesn't correspond to a real field
        me.dataIndex = me.dataIndex || Ext.id(null, 'cc-dataIndex-');

        me.tpl = me.createTemplate(me.tpl);
        me.renderer = me.createRenderer(me.renderer);
    },

    applyTemplate: function(data, value) {
        if (Ext.isDefined(value)) {
            data[this.dataIndex] = value;
        }

        return this.tpl.apply(data);
    },

    calculateFrameWidth: function(component) {
        var el = component.getEl(),
            parentDiv = el && el.parent(),
            // By default the TD has no padding but it is quite common to add some via a tdCls
            parentTd = parentDiv && parentDiv.parent();

        if (parentTd) {
            return parentDiv.getFrameWidth('lr') + parentTd.getFrameWidth('lr');
        }
    },

    createRenderer: function(renderer) {
        var me = this;

        return function(value, p, record) {
            var data = Ext.apply({}, record.data, record.getAssociatedData());

            if (renderer) {
                value = renderer.apply(me, arguments);
            }

            // Process the value even with no renderer defined as the record may contain a component config
            value = me.processValue(value);

            return me.applyTemplate(data, value);
        };
    },

    createTemplate: function(tpl) {
        return tpl && tpl.isTemplate
            ? tpl
            : Ext.create('Skirtle.CTemplate', tpl || ['{', this.dataIndex ,'}']);
    },

    destroyChild: function(child) {
        child.destroy();
    },

    onChildAfterRender: function(child) {
        this.resizeChild(child);
    },

    onChildResize: function() {
        this.redoScrollbars();
    },

    onColumnDestroy: function(column) {
        // this is the component, not the column
        column.destroyChild(this);
    },

    onColumnResize: function(column) {
        // this is the component, not the column
        column.resizeChild(this);
    },

    // Override
    onRender: function() {
        this.registerViewListeners();
        this.callParent(arguments);
    },

    // View has changed, may be a full refresh or just a single row
    onViewChange: function() {
        var me = this,
            tpl = me.tpl;

        if (tpl.isCTemplate) {
            // No need to wait for the polling, the sooner we inject the less painful it is
            tpl.injectComponents();
        }

        // A view change could mean scrollbar problems
        me.redoScrollbars();
        
        me.performGC();
    },

    // Component GC, try to stop components leaking
    performGC: function() {
        var compIds = this.compIds,
            index = compIds.length - 1,
            comp,
            el;

        for ( ; index >= 0 ; --index) {
            // Could just assume that the component id is the el id but that seems risky
            comp = Ext.getCmp(compIds[index]);
            el = comp && comp.getEl();

            if (!el || !el.dom || Ext.getDom(Ext.id(el)) !== el.dom) {
                // The component is no longer in the DOM
                comp.destroy();
                Ext.Array.splice(compIds, index, 1);
            }
        }
    },

    // TODO: Future versions may support arrays or objects as hashes containing multiple components
    processValue: function(value) {
        if (Ext.isObject(value) && !value.isComponent && value.xtype) {
            // Do not default to a panel, not only would it be an odd default but it makes future enhancements trickier
            value = Ext.widget(value.xtype, value);
        }

        if (value && value.isComponent) {
            if (this.componentGC) {
                this.compIds.push(value.getId());
            }

            this.registerListeners(value);
        }

        return value;
    },

    redoScrollbars: function() {
        var grid = this.up('tablepanel');

        if (grid) {
            // Fix the scrollbars. e.g. panel collapse in a cell
            grid.invalidateScroller();
            grid.determineScrollbars();
        }
    },

    registerListeners: function(component) {
        var me = this;

        // Destroy the child component when the column is destroyed
        component.mon(me, 'destroy', me.onColumnDestroy, component);

        if (me.autoWidthComponents) {
            // Need to resize children when the column resizes
            component.mon(me, 'resize', me.onColumnResize, component);

            // Need to resize children after render as some components (e.g. comboboxes) get it wrong otherwise
            component.on('afterrender', me.onChildAfterRender, me, {single: true});
        }

        // Need to redo scrollbars when a child resizes
        component.on('resize', me.onChildResize, me);
    },

    registerViewListeners: function() {
        var me = this,
            view = me.up('tablepanel').getView();

        me.mon(view, 'refresh', me.onViewChange, me);
        me.mon(view, 'itemupdate', me.onViewChange, me);

        // Possibly overkill as refresh tends to get fired for adds and removes too
        me.mon(view, 'itemadd', me.onViewChange, me);
        me.mon(view, 'itemremove', me.onViewChange, me);
    },

    resizeChild: function(component, defer) {
        var me = this,
            frameWidth = me.calculateFrameWidth(component),
            newWidth,
            oldWidth;

        // TODO: Should we destroy the component here if it doesn't have a parent element? Already picked up anyway?
        if (Ext.isNumber(frameWidth)) {
            newWidth = me.getWidth() - frameWidth;
            oldWidth = component.getWidth();

            // Returns true if a resize actually happened
            if (me.setChildWidth(component, newWidth, oldWidth)) {
                // Avoid an infinite resizing loop, deferring will only happen once
                if (defer !== false) {
                    // Do the sizing again after a delay. This is because child panel collapse animations undo our sizing
                    Ext.each(me.widthUpdateDelay, function(delay) {
                        Ext.defer(me.resizeChild, delay, me, [component, false]);
                    });
                }
            }
        }
    },

    setChildWidth: function(component, newWidth, oldWidth) {
        if (oldWidth === newWidth) {
            return false;
        }

        component.setWidth(newWidth);

        return true;
    }
});