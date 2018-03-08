
Ext.ns('Sdis.Remocra');
Ext.define('Sdis.Remocra.Rights', {
    singleton : true,

    load : function(rights) {
        this.userRights = [];
        Ext.Array.forEach(rights, function(right){
            this.userRights.push(right.key);
        }, this);
    },

    hasRight : function(key) {
        if(this.disabledRights){
            return true;
        }
        if (!(this.userRights instanceof Object)) {
            return false;
        }
        return Ext.Array.contains(this.userRights, key);
    },

    toggleRights : function() {
        this.disabledRights = !this.disabledRights;
    }
});
