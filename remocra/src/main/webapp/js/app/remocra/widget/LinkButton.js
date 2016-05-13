Ext.define('Sdis.Remocra.widget.LinkButton', {
    extend: 'Ext.Component',
    xtype: 'linkbutton',

    cls: 'linkbutton',
    text: null,
    srcDefault: '#',
    hrefTargetDefault: '_self',

    initComponent: function() {
        var me = this;
        me.html = '<a href="#">' + me.text + '</a>';
        me.callParent();
        // Si src, on le definit directement
        if (me.src) {
            me.setText(me.text, me.src, me.hrefTarget);
        }
    },

    afterRender: function() {
        var me = this;
        me.callParent(arguments);
        me.mon(me.el, {
            scope: me,
            delegate: 'a',
            click: me.handleClick
        });
    },

    handleClick: function(e) {
        if (this.handler) {
            e.stopEvent();
            this.handler.call(this, this);
        }
    },

    setText: function(text, src, hrefTarget) {
        this.update('<a href="' + (src || this.srcDefault) + '" target="' + (hrefTarget || this.hrefTargetDefault) + '">' + text + '</a>');
    },

    setRawText: function(text) {
        this.update(text);
    }
});