/**
 * Classe utilitaire
 */
Ext.ns('Sdis.Remocra.util');
Ext.define('Sdis.Remocra.util.Util', {
    singleton: true,
    
    SL: '/',
    
    /**
     * Force un rechargement de la page actuelle
     */
    forceReload: function() {
        window.location.reload();
    },
    softReload : function(){
        var token = this.getHashTokenNoSharp();
        Sdis.Remocra.appInstance.getController('Router').handleHistoryChange(token, undefined, true);
    },
    
    /**
     * Changement du Hash de la page (nouvelle URL avec navigation cliente)
     * @param {} hash
     */
    changeHash: function(hash) {
        if (!hash || hash==="") {
            hash = "#";
        }
        if (hash.charAt(0) != "#") {
            hash = "#" + hash;
        }
        var current = window.document.location.href;
        window.document.location.href = current.split('#')[0]+hash;
    },
    
    withBaseUrl: function(endurl) {
        if (endurl==null||endurl.length==0) {
            // Pas de fin => base
            if (BASE_URL==null||BASE_URL.length==0) {
                return this.SL;
            }
            return BASE_URL;
        }
        var endBase = BASE_URL.substr(-1);
        var beginEndurl = endurl.substr(0, 1);
        
        if (endBase==this.SL && beginEndurl==this.SL) {
             // Deux / => on retire un /
             return BASE_URL+endurl.substr(1);
         } else if (endBase==this.SL || beginEndurl==this.SL) {
             // Un seul / => rien à faire
             return BASE_URL+endurl;
         }
         // Aucun / => on ajoute un /
         return BASE_URL+this.SL+endurl;
    },
    
    /**
     * Fournit le token, sans le #.
     * Exemple : service/203/cns/document/830
     * @param {} url facultative (non précisée => url de la fenêtre)
     * @return {}
     */
    getHashTokenNoSharp: function(url) {
        // if no url specified, take it from the location bar
        url = url || window.location.href;

        if (url.indexOf('#') != -1) {
            return url.substring(url.indexOf('#')+1, url.length);
        }
        return null;
    },
    /** FROM OPENLAYERS
     * Function: getParameters
     * Parse the parameters from a URL or from the current page itself into a
     *     JavaScript Object. Note that parameter values with commas are separated
     *     out into an Array.
     *
     * Parameters:
     * url - {String} Optional url used to extract the query string.
     *                If null, query string is taken from page location.
     *
     * Returns:
     * {Object} An object of key/value pairs from the query string.
     */
    getParameters: function(url) {
        // if no url specified, take it from the location bar
        url = url || window.location.href;

        //parse out parameters portion of url string
        var paramsString = "";
        if (url.indexOf('?') != -1) {
            var start = url.indexOf('?') + 1;
            var end = url.indexOf('#') != -1 ?
            url.indexOf('#') : url.length;
            paramsString = url.substring(start, end);
        }

        var parameters = {};
        var pairs = paramsString.split(/[&;]/);
        var i, j, key, value;
        for(i=0, len=pairs.length; i<len; ++i) {
            var keyValue = pairs[i].split('=');
            if (keyValue[0]) {
                key = decodeURIComponent(keyValue[0]);
                value = keyValue[1] || ''; //empty string if no value

                //decode individual values
                value = value.split(",");
                for(j=0, jlen=value.length; j<jlen; j++) {
                    value[j] = decodeURIComponent(value[j]);
                }

                //if there's only one value, do not return as array
                if (value.length == 1) {
                    value = value[0];
                }

                parameters[key] = value;
            }
        }
        return parameters;
    },
    /**
     * Récupère la valeur du paramètre de nom.
     * arguments : url, name OU ALORS name
     * @return {}
     */
    getParameter: function(url, name) {
        if (arguments.length == 1) {
            name = url;
            url = null;
        }
        var parameters = this.getParameters(url);
        return parameters[name];
    },

    // Internet Explorer : numéro de version le cas échéant, false sinon
    IEVersion: (Ext.isIE ? parseFloat(navigator.appVersion.split("MSIE")[1]) : false),
    
    
    /**
     * Reprise de OpenLayers.util.getFormattedLonLat avec ajout du paramètre retWholeData :
     * 
     * Parameters:
     * coordinate - {Float} the coordinate value to be formatted
     * axis - {String} value of either 'lat' or 'lon' to indicate which axis is to
     *          to be formatted (default = lat)
     *
     * retWholeData - {Boolean} données séparées ou juste pour affichage. true implique un retour de la forme : {d:XX, m:YY, s:ZZ, o:'W'}
     *          
     * dmsOption - {String} specify the precision of the output can be one of:
     *           'dms' show degrees minutes and seconds
     *           'dm' show only degrees and minutes
     *           'd' show only degrees
     * 
     * Returns:
     * {String} the coordinate value formatted as a string
     */
    getFormattedLonLat: function(coordinate, axis, dmsOption, retWholeData, dontround) {
        if (!dmsOption) {
            dmsOption = 'dms';    //default to show degree, minutes, seconds
        }
        
        coordinate = (coordinate+540)%360 - 180; // normalize for sphere being round
        var abscoordinate = Math.abs(coordinate);
        var coordinatedegrees = Math.floor(abscoordinate);

        var coordinateminutes = (abscoordinate - coordinatedegrees)/(1/60);
        var tempcoordinateminutes = coordinateminutes;
        if ((dontround!==true && dmsOption == 'dm') || dmsOption == 'dms') {
            coordinateminutes = Math.floor(coordinateminutes);
        }
        var coordinateseconds = (tempcoordinateminutes - coordinateminutes)/(1/60);
        if (dontround!==true && dmsOption == 'dms') {
            coordinateseconds =  Math.round(coordinateseconds*10);
            coordinateseconds /= 10;

            // CVA : on supprime la virgule
            coordinateseconds = Math.round(coordinateseconds);
        }
        
        if( coordinateseconds >= 60) { 
            coordinateseconds -= 60; 
            coordinateminutes += 1;
            if( coordinateminutes >= 60) { 
                coordinateminutes -= 60; 
                coordinatedegrees += 1; 
            } 
        }
        
        if( coordinatedegrees < 10 ) {
            coordinatedegrees = "0" + coordinatedegrees;
        }
        var str = coordinatedegrees + "\u00B0";

        if (dmsOption.indexOf('dm') >= 0) {
            if( coordinateminutes < 10 ) {
                coordinateminutes = "0" + coordinateminutes;
            }
            str += ' '/*CVA : espace*/ + coordinateminutes + "'";
      
            if (dmsOption.indexOf('dms') >= 0) {
                if( coordinateseconds < 10 ) {
                    coordinateseconds = "0" + coordinateseconds;
                }
                str += ' '/*CVA : espace*/ + coordinateseconds + '"';
            }
        }
        
        /*CVA : conteneur ensemble des données*/
        var wholeData = {d: coordinatedegrees, m: coordinateminutes, s: coordinateseconds};
        
        str += ' ';/*CVA : espace*/
        if (axis == "lon") {
            wholeData.o = coordinate < 0 ? OpenLayers.i18n("W") : OpenLayers.i18n("E");
            str += wholeData.o;
        } else {
            wholeData.o = coordinate < 0 ? OpenLayers.i18n("S") : OpenLayers.i18n("N");
            str += wholeData.o;
        }
        /*CVA : wholeData => toutes les données */
        if (retWholeData){
            return wholeData;
        }
        return str;
    },
    
    /**
     * Formattage d'une coordonnée x ou y suivant le format passé en paramètre
     *
     * @param coord  coordonnée 'x' ou 'y'
     * @param value  coordonnée au format par défaut (DD_DDDD)
     * @param format  'DD_DDDD', 'DD_MM_MM', 'DD_MM_SSSS'
     * @param decimal  nombre de décimales
     *
     * Returns: {String} donnée formattée
     */
    getFormattedCoord: function(coord, value, format, decimal) {

        if(format == 'DD_DDDD') {
            return Number.parseFloat(value).toFixed(decimal);
        }

        if(format == 'DD_MM_MM' && coord == 'x') {
            var dmX = Sdis.Remocra.util.Util.getFormattedLonLat(value, 'lon', 'dm', true, true);
            return (dmX.o=='O'?'Ouest ':'Est ') + dmX.d + '° ' + Number.parseFloat(dmX.m).toFixed(decimal) +'\'';
        }
        if(format == 'DD_MM_MM' && coord == 'y') {
            var dmY = Sdis.Remocra.util.Util.getFormattedLonLat(value, 'lat', 'dm', true, true);
            return (dmY.o=='N'?'Nord ' :'Sud ') + dmY.d + '° ' + Number.parseFloat(dmY.m).toFixed(decimal) +'\'';
        }

        if(format == 'DD_MM_SSSS' && coord == 'x') {
            var dmsX = Sdis.Remocra.util.Util.getFormattedLonLat(value, 'lon', 'dms', true, true);
            return (dmsX.o=='O'?'Ouest ':'Est ') + dmsX.d + '° ' + dmsX.m +'\' ' + Number.parseFloat(dmsX.s).toFixed(decimal) + '"';
        }
        if(format == 'DD_MM_SSSS' && coord == 'y') {
            var dmsY = Sdis.Remocra.util.Util.getFormattedLonLat(value, 'lat', 'dms', true, true);
            return (dmsY.o=='N'?'Nord ' :'Sud ') + dmsY.d + '° ' + dmsY.m +'\' ' + Number.parseFloat(dmsY.s).toFixed(decimal) + '"';
        }

        return value;
    },

    /**
     * Récupération 'une coordonnées à partir des info dms et orientation
     * @param deg
     * @param min
     * @param sec
     * @param orientation 'W', 'E', 'N', 'S'
     */
    getCoordinateFromDMSO: function(deg, min, sec, orientation) {
        deg = deg%180.0;
        min = min%60.0;
        sec = sec%60.0;
        
        var decMin = (parseFloat(min) + (sec/60.0));
        var coordinate = parseFloat(deg) + (decMin/60.0);

        var factOrient = orientation=='W'||orientation=='S'?-1:1;
        return factOrient*coordinate;
    },
    
    getIgnKey: function() {
        /*var ignKeys = {
             // api.ign.fr, compte cvagner
            'localhost': '4n507j21zeha5rp5pkll48vj',
            // professionnels.ign.fr compte cda@atolcd.com
            'www.sdis83-remocra.atolcd.com': 'axlckthtqpigwwhsees77yed'
        };*/
        var ignKeys = REMOCRA_IGN_KEYS;
        // Chaine de caractères : on la retourne directement
        if (typeof ignKeys == 'string') {
            if (ignKeys.length<1) {
                return null;
            }
            return ignKeys;
        }
        // Associations : on retourne la clé associée au nom
        var host = window.location.host.split(':')[0];
        return ignKeys[host];
    },
    
    getFeature: function(wkt) {
        var feature = new OpenLayers.Format.WKT().read(wkt);
        return feature;
    },
    
    getBounds: function(feature) {
        if (Ext.isString(feature)) {
            feature = this.getFeature(feature);
        }
        var bounds = feature.geometry.getBounds();
        return bounds;
    }, 
    
    copyStore: function(store, storeId, fn) {
        var newStore;
        newStore = Ext.create(store.$className, {
            storeId: storeId,
            autoLoad: false,
            autoSync: false,
            model: store.model
        });
        data = store.getRange();
        data = Ext.Array.filter(data, fn);
        newStore.add(data);
    }
    
});
