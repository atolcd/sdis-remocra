/**
 * Inspired by: http://iamtotti.com/blog/2010/03/extjs-vertical-tabpanel-example/
 * 
 *  @class Ext.ux.tot2ivn.VrTabPanel
 * @version		0.2		Tested working with ExtJS 3.2+ on IE6+, FireFox 2+, Chrome 4+, Opera 9.6+, Safari 4+
 * @author	Anh Nguyen (Totti)
 * @description		Vertical TabPanel implements the same set of features as those of Ext.TabPanel. 
 *	Tab position defaults to 'left'. Position 'right' is not supported.
 *	Auto-scrolling currently not implemented.
 *  Three config properties users would want to config are :
 *	@cfg border
 *	@cfg tabWidth
 *	@cfg tabMarginTop
 *	See description of config properties below.
 **/


/**
 * @author Ed Spencer
 * @modifiedby Lucian Lature
 * @class Ext.tab.Bar
 * @extends Ext.panel.Header
 * <p>TabBar is used internally by a {@link Ext.tab.Panel TabPanel} and wouldn't usually need to be created manually.</p>
 */
Ext.define('Ext.ux.tab.VerticalBar', {

    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.verticaltabbar',
    baseCls: Ext.baseCSSPrefix + 'tab-bar',

    requires: [
        'Ext.tab.Tab',
        'Ext.FocusManager'
    ],

    isTabBar: true,

    // @private
    defaultType: 'tab',

    /**
     * @cfg Boolean plain
     * True to not show the full background on the tabbar
     */
    plain: false,

    // @private
    //renderTpl: [
    //    '<div class="{baseCls}-body <tpl if="bodyCls"> {bodyCls}</tpl> <tpl if="ui"> {baseCls}-body-{ui}<tpl for="uiCls"> {parent.baseCls}-body-{parent.ui}-{.}</tpl></tpl>"<tpl if="bodyStyle"> style="{bodyStyle}"</tpl>></div>'
    //],

    /**
     * @cfg {Number} minTabWidth The minimum width for each tab. Defaults to <tt>30</tt>.
     */
    minTabWidth: 30,

    /**
     * @cfg {Number} maxTabWidth The maximum width for each tab. Defaults to <tt>undefined</tt>.
     */
    maxTabWidth: undefined,

    // @private
    initComponent: function() {
        
        var me = this,
            keys;

        if (me.plain) {
            me.setUI(me.ui + '-plain');
        }
        
        me.addClsWithUI(me.dock);

        me.addEvents(
            /**
             * @event change
             * Fired when the currently-active tab has changed
             * @param {Ext.tab.Bar} tabBar The TabBar
             * @param {Ext.Tab} tab The new Tab
             * @param {Ext.Component} card The card that was just shown in the TabPanel
             */
            'change'
        );

        Ext.applyIf(me.renderSelectors, {
            body : '.' + me.baseCls + '-body'
        });
        
        me.callParent(arguments);

        // TabBar must override the Header's align setting.
        me.layout.align = (me.orientation == 'vertical') ? 'left' : 'top';
        me.layout.overflowHandler = Ext.create('Ext.layout.container.boxOverflow.Scroller', me.layout);
        me.items.removeAt(me.items.getCount() - 1);
        me.items.removeAt(me.items.getCount() - 1);
        
        // Subscribe to Ext.FocusManager for key navigation
        keys = me.orientation == 'vertical' ? ['up', 'down'] : ['left', 'right'];
        Ext.FocusManager.subscribe(me, {
            keys: keys
        });

        Ext.apply(me.renderData, {
            bodyCls: me.bodyCls
        });
    },

    // @private
    onAdd: function(tab) {
        var me = this,
            tabPanel = me.tabPanel,
            hasOwner = !!tabPanel;

        me.callParent(arguments);
        tab.position = me.dock;
        if (hasOwner) {
            tab.minWidth = tabPanel.minTabWidth;
        }
        else {
            tab.minWidth = me.minTabWidth + (tab.iconCls ? 25 : 0);
        }
        tab.maxWidth = me.maxTabWidth || (hasOwner ? tabPanel.maxTabWidth : undefined);
    },

    // @private
    afterRender: function() {
        var me = this;

        me.mon(me.el, {
            scope: me,
            click: me.onClick,
            delegate: '.' + Ext.baseCSSPrefix + 'tab'
        });
        me.callParent(arguments);
        
    },

    afterComponentLayout : function() {
        var me = this;  
        me.callParent(arguments);
    },

    // @private
    onClick: function(e, target) {
        // The target might not be a valid tab el.
        var tab = Ext.getCmp(target.id),
            tabPanel = this.tabPanel,
            allowActive = true;

        target = e.getTarget();

        if (tab && tab.isDisabled && !tab.isDisabled()) {
            if (tab.closable && target === tab.closeEl.dom) {
                tab.onCloseClick();
            } else {
                if (tabPanel) {
                    // TabPanel will card setActiveTab of the TabBar
                    tabPanel.setActiveTab(tab.card);
                } else {
                    this.setActiveTab(tab);
                }
                tab.focus();
            }
        }
    },

    /**
     * @private
     * Closes the given tab by removing it from the TabBar and removing the corresponding card from the TabPanel
     * @param {Ext.Tab} tab The tab to close
     */
    closeTab: function(tab) {
        var me = this,
            card = tab.card,
            tabPanel = me.tabPanel,
            nextTab;
            
        if (card && card.fireEvent('beforeclose', card) === false) {
            return false;
        }

        if (tab.active && me.items.getCount() > 1) {
            nextTab = tab.next('tab') || me.items.items[0];
            me.setActiveTab(nextTab);
            if (tabPanel) {
                tabPanel.setActiveTab(nextTab.card);
            }
        }
        /*
         * force the close event to fire. By the time this function returns,
         * the tab is already destroyed and all listeners have been purged
         * so the tab can't fire itself.
         */
        tab.fireClose();
        me.remove(tab);

        if (tabPanel && card) {
            card.fireEvent('close', card);
            tabPanel.remove(card);
        }
        
        if (nextTab) {
            nextTab.focus();
        }
    },

    /**
     * @private
     * Marks the given tab as active
     * @param {Ext.Tab} tab The tab to mark active
     */
    setActiveTab: function(tab) {
        if (tab.disabled) {
            return;
        }
        var me = this;
        if (me.activeTab) {
            me.activeTab.deactivate();
        }
        tab.activate();
        
        if (me.rendered) {
            me.layout.layout();
            tab.el.scrollIntoView(me.layout.getRenderTarget());
        }
        me.activeTab = tab;
        me.fireEvent('change', me, tab, tab.card);
    }
});

 
 
Ext.define('Ext.ux.tab.VerticalPanel', {
    
    extend: 'Ext.panel.Panel',
    alias: 'widget.verticaltabpanel',
    alternateClassName: ['Ext.VerticalTabPanel'],

    requires: ['Ext.layout.container.Card', 'Ext.tab.Bar'],
    
    /**
     * @cfg {Object} tabBar Optional configuration object for the internal {@link Ext.tab.Bar}. If present, this is 
     * passed straight through to the TabBar's constructor
     */

    /**
     * @cfg {Object} layout Optional configuration object for the internal {@link Ext.layout.container.Card card layout}.
     * If present, this is passed straight through to the layout's constructor
     */

    /**
     * @cfg {Boolean} removePanelHeader True to instruct each Panel added to the TabContainer to not render its header 
     * element. This is to ensure that the title of the panel does not appear twice. Defaults to true.
     */
    removePanelHeader: true,

    /**
     * @cfg Boolean plain
     * True to not show the full background on the TabBar
     */
    plain: true,

    /**
     * @cfg {String} itemCls The class added to each child item of this TabPanel. Defaults to 'x-tabpanel-child'.
     */
    itemCls: 'x-tabpanel-child x-vertical-tab-panel',

    /**
     * @cfg {Number} minTabWidth The minimum width for a tab in the {@link #tabBar}. Defaults to <code>30</code>.
     */

    /**
     * @cfg {Boolean} deferredRender
     * <p><tt>true</tt> by default to defer the rendering of child <tt>{@link Ext.container.Container#items items}</tt>
     * to the browsers DOM until a tab is activated. <tt>false</tt> will render all contained
     * <tt>{@link Ext.container.Container#items items}</tt> as soon as the {@link Ext.layout.container.Card layout}
     * is rendered. If there is a significant amount of content or a lot of heavy controls being
     * rendered into panels that are not displayed by default, setting this to <tt>true</tt> might
     * improve performance.</p>
     * <br><p>The <tt>deferredRender</tt> property is internally passed to the layout manager for
     * TabPanels ({@link Ext.layout.container.Card}) as its {@link Ext.layout.container.Card#deferredRender}
     * configuration value.</p>
     * <br><p><b>Note</b>: leaving <tt>deferredRender</tt> as <tt>true</tt> means that the content
     * within an unactivated tab will not be available</p>
     */
    deferredRender : true,
   
    tabsConfig: {
        /**
         * @cfg {Number} tabWidth The initial width in pixels of each new tab title (defaults to 130).
         */
        width : 80, 
        /**
         * @cfg {Number} tabMarginTop The initial top margin in pixels of the tab strip. (defaults to 15).
         */
        marginTop : 15,
        /**
         * @cfg {Number} tabMargin The number of pixels of space to calculate into the sizing and scrolling of
         * tabs. If you change the margin in CSS, you will need to update this value so calculations are correct
         * with either <tt>{@link #resizeTabs}</tt> or scrolling tabs. (defaults to <tt>2</tt>)
         */
        margin : 2,
        
        /**
         * @cfg {String} text align
         */
        textAlign : 'right'
    },

    //inherit docs
    initComponent: function() {
   
        var me = this,
            dockedItems = me.dockedItems || [],
            activeTab = me.activeTab || 0;
        
        me.layout = Ext.create('Ext.layout.container.Card', Ext.apply({
            owner: me,
            deferredRender: me.deferredRender,
            itemCls: me.itemCls
        }, me.layout));
        /**
         * @property tabBar
         * @type Ext.TabBar
         * Internal reference to the docked TabBar
         */
         
        me.tabBar = Ext.create('Ext.ux.tab.VerticalBar', Ext.apply({}, me.tabBar, {
            dock: 'left',
            plain: me.plain,
            border: me.border,
			orientation: 'vertical',
            cardLayout: me.layout,
            style: {
                'paddingTop': me.tabsConfig.marginTop
            },
            tabPanel: me
        }));
        
        // 
        
        if (dockedItems && !Ext.isArray(dockedItems)) {
            dockedItems = [dockedItems];
        }
        
        dockedItems.push(me.tabBar);
        me.dockedItems = dockedItems;
        
        me.addEvents(
            /**
             * @event beforetabchange
             * Fires before a tab change (activated by {@link #setActiveTab}). Return false in any listener to cancel
             * the tabchange
             * @param {Ext.tab.Panel} tabPanel The TabPanel
             * @param {Ext.Component} newCard The card that is about to be activated
             * @param {Ext.Component} oldCard The card that is currently active
             */
            'beforetabchange',

            /**
             * @event tabchange
             * Fires when a new tab has been activated (activated by {@link #setActiveTab}).
             * @param {Ext.tab.Panel} tabPanel The TabPanel
             * @param {Ext.Component} newCard The newly activated item
             * @param {Ext.Component} oldCard The previously active item
             */
            'tabchange'
        );
        
        me.callParent(arguments);

        //set the active tab
        me.setActiveTab(activeTab);
        //set the active tab after initial layout
        me.on('afterlayout', me.afterInitialLayout, me, {single: true});
    },

    /**
     * @private
     * We have to wait until after the initial layout to visually activate the activeTab (if set).
     * The active tab has different margins than normal tabs, so if the initial layout happens with
     * a tab active, its layout will be offset improperly due to the active margin style. Waiting
     * until after the initial layout avoids this issue.
     */
    afterInitialLayout: function() {
        var me = this,
            card = me.getComponent(me.activeTab);
            
        if (card) {
            me.layout.setActiveItem(card);
        }
    },
    
    /*
    onResize: function() {
        var me = this;
        var internal = me.layout.activeItem.body;
        me.el.setHeight(internal.getHeight() + internal.getPadding('t, r'));
    },
    */
	
    /**
     * Makes the given card active (makes it the visible card in the TabPanel's CardLayout and highlights the Tab)
     * @param {Ext.Component} card The card to make active
     */
    setActiveTab: function(card) {
        var me = this,
            previous;

        card = me.getComponent(card);
        
        if (card) {
            
            previous = me.getActiveTab();
            
            if (previous && previous !== card && me.fireEvent('beforetabchange', me, card, previous) === false) {
                return false;
            }
            
            me.tabBar.setActiveTab(card.tab);
            me.activeTab = card;
            
            if (me.rendered) {
				me.layout.setActiveItem(card);
            }
            
            if (previous && previous !== card) {
                me.fireEvent('tabchange', me, card, previous);
            }
        }
    },

    /**
     * Returns the item that is currently active inside this TabPanel. Note that before the TabPanel first activates a
     * child component this will return whatever was configured in the {@link #activeTab} config option 
     * @return {Ext.Component/Integer} The currently active item
     */
    getActiveTab: function() {
        return this.activeTab;
    },

    /**
     * Returns the {@link Ext.tab.Bar} currently used in this TabPanel
     * @return {Ext.TabBar} The TabBar
     */
    getTabBar: function() {
        return this.tabBar;
    },

    /**
     * @ignore
     * Makes sure we have a Tab for each item added to the TabPanel
     */
    onAdd: function(item, index) {
        var me = this,
            cfg = item.tabConfig || {},
            defaultConfig = {
                xtype: 'tab',
                card: item,
                width: me.tabsConfig.width,
                disabled: item.disabled,
                closable: item.closable,
                hidden: item.hidden,
                tabBar: me.tabBar
            };
            
        if (item.closeText) {
            defaultConfig.closeText = item.closeText;
        }
        cfg = Ext.applyIf(cfg, defaultConfig);
        
        item.tab = me.tabBar.insert(index, cfg);
        
        item.on({
            scope : me,
            enable: me.onItemEnable,
            disable: me.onItemDisable,
            beforeshow: me.onItemBeforeShow,
            iconchange: me.onItemIconChange,
            titlechange: me.onItemTitleChange
        });
        
        item.tab.on({
            scope: me,
            render: me.onVTabRender           
        });
		
        if (item.isPanel) {
            if (me.removePanelHeader) {
                item.preventHeader = true;
                if (item.rendered) {
                    item.updateHeader();
                }
            }
            if (item.isPanel && me.border) {
                item.setBorder(false);
            }
        }

        // ensure that there is at least one active tab
        if (this.rendered && me.items.getCount() === 1) {
            me.setActiveTab(0);
        }
    },
    
    /**
     * @private
     * Enable corresponding tab when item is enabled.
     */
    onItemEnable: function(item){
        item.tab.enable();
    },
    
    onVTabRender: function(tab) {
        var me = this;
        tab.el.down('span.x-tab-inner').setStyle('text-align', me.tabsConfig.textAlign);
    },

    /**
     * @private
     * Disable corresponding tab when item is enabled.
     */    
    onItemDisable: function(item){
        item.tab.disable();
    },
    
    /**
     * @private
     * Sets activeTab before item is shown.
     */
    onItemBeforeShow: function(item) {
		var me = this;
        if (item !== this.activeTab) {
            this.setActiveTab(item);
            return false;
        }    
    },
    
    /**
     * @private
     * Update the tab iconCls when panel iconCls has been set or changed.
     */
    onItemIconChange: function(item, newIconCls) {
        item.tab.setIconCls(newIconCls);
        this.getTabBar().doLayout();
    },
    
    /**
     * @private
     * Update the tab title when panel title has been set or changed.
     */
    onItemTitleChange: function(item, newTitle) {
        item.tab.setText(newTitle);
        this.getTabBar().doLayout();
    },


    /**
     * @ignore
     * If we're removing the currently active tab, activate the nearest one. The item is removed when we call super,
     * so we can do preprocessing before then to find the card's index
     */
    doRemove: function(item, autoDestroy) {
        var me = this,
            items = me.items,
            /**
             * At this point the item hasn't been removed from the items collection.
             * As such, if we want to check if there are no more tabs left, we have to
             * check for one, as opposed to 0.
             */
            hasItemsLeft = items.getCount() > 1;

        if (me.destroying || !hasItemsLeft) {
            me.activeTab = null;
        } else if (item === me.activeTab) {
             me.setActiveTab(item.next() || items.getAt(0)); 
        }
        me.callParent(arguments);

        // Remove the two references
        delete item.tab.card;
        delete item.tab;
    },

    /**
     * @ignore
     * Makes sure we remove the corresponding Tab when an item is removed
     */
    onRemove: function(item, autoDestroy) {
        var me = this;
        
        item.un({
            scope : me,
            enable: me.onItemEnable,
            disable: me.onItemDisable,
            beforeshow: me.onItemBeforeShow
        });
        if (!me.destroying && item.tab.ownerCt == me.tabBar) {
            me.tabBar.remove(item.tab);
        }
    }
});