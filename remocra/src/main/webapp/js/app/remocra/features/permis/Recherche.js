Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.model.Permis');

Ext.require('Sdis.Remocra.store.TypePermisAvisTousStore');

Ext.define('Sdis.Remocra.features.permis.Recherche', {
    extend: 'Ext.tab.Panel',

    title: null,
    height: 700,
    
    permisTpl: Ext.create('Ext.XTemplate',
        '{%this.setId(values.id);%}', // Uniquement pour pouvoir le récupérer à tout moment
        '<ul>',
        '<tpl for="permisData">',
            '<li style="border-bottom:1px solid grey;margin-bottom: 20px;">', // un permis
                '<b>Permis {numero}</b>',
                '<img src="/remocra/resources/images/remocra/cartes/zoom_permis.png" width="30" height="30" style="float:left;margin-right: 5px;cursor:pointer;"',
                ' title="Zoomer sur le permis" onclick="Ext.getCmp(\'{[this.getId()]}\').zoomToPermis({[values.id, values.geometrie]});"/>',
                '<ul style="margin-bottom: 5px;">',
                    '<li>{nom}</li>',
                    '<li>{observations}</li>',
                '</ul>',
            '</li>',
        '</tpl>',
        '</ul>',
        {
        setId: function(id) {
            this.id=id;
        },
        getId: function() {
            return this.id;
        }
    }),
    
    initComponent: function() {
        // -- EVENEMENTS
        this.addEvents('centerToLonLat', 'loadPermis');
        
        Ext.apply(this, {
            layout: 'fit',
            activeTab : 0,
            plain: true,
            bodyPadding: 10,
            defaults: {
                autoScroll: true
            },
            items: [
                this.createRecherchePanel(),
                this.createResultatsPanel()
            ],
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                items: [
                    '->',
                    { itemId: 'rechercher',
                        tooltip: 'Rechercher les permis',
                        text: 'Rechercher',
                        handler: Ext.bind(this.search, this)
                    },
                    { itemId: 'reset',
                        tooltip: 'Réinitialiser les options de recherche',
                        text: 'Réinitialiser', 
                        handler: Ext.bind(this.reset, this)
                    },
                    '->'
                ]
            }],
            listeners: {
                afterrender: function() {
                    this.reset();
                }
            }
        });
        
        this.callParent(arguments);
    },
    
    createRecherchePanel: function() {
        var f = Sdis.Remocra.widget.WidgetFactory;
        var labelWidth = 80;
        var fieldWidth = 235;
        
        var nom = f.createTextField('Nom', true, '', {itemId: 'nom', labelWidth: labelWidth, width: fieldWidth});
        var commune = Sdis.Remocra.widget.WidgetFactory.createCommuneCombo({
            itemId: 'commune',
            fieldLabel: 'Commune',
            labelWidth: labelWidth,
            emptyText: 'Commune de...',
            hideTrigger: false
        });
        var numero = f.createTextField('N° permis', true, '', {itemId: 'numero', labelWidth: labelWidth, width: fieldWidth});
        var sectionCadastrale = f.createTextField('N° section', true, '', {itemId: 'sectionCadastrale', labelWidth: labelWidth, width: fieldWidth});
        var parcelleCadastrale = f.createTextField('N° parcelle', true, '', {itemId: 'parcelleCadastrale', labelWidth: labelWidth, width: fieldWidth});
        
        var avis = Ext.widget('combo', {
            itemId: 'avis',
            fieldLabel: 'Avis',
            labelWidth: labelWidth, labelSeparator: '',
            store: Sdis.Remocra.store.TypePermisAvisTousStore,
            queryMode: 'local',
            displayField: 'nom', valueField: 'id',
            editable: false,
            value: 'Tous'
        });

        var sep = {height:10, border: false};
        var descriptionPanel = Ext.create('Ext.form.FormPanel', {
            itemId: 'recherchePanel',
            title: 'Recherche',
            border: false,
            defaults: { labelSeparator: '', allowBlank: false },
            items: [this.createHelpPanel(), nom, sep, commune, sep, numero, sep, sectionCadastrale, parcelleCadastrale, avis]
        });
        return descriptionPanel;
    },
    
    createHelpPanel: function() {
        return {
            border: false,
            html: '<p style="font-style:italic;color:#a9a9a9;margin-bottom:20px;">Veuillez renseigner <b>l\'ensemble de critères</b> que doivent respecter les permis'
                + ' avant de valider la recherche.</p>'
        };
    },
    
    createResultatsPanel: function() {
        return Ext.create('Ext.panel.Panel', {
            itemId: 'resultatsPanel',
            title: 'Résultats',
            border: false,
            defaults: { labelSeparator: '', allowBlank: false },
            items: { border: false, html: 'Aucun résultat' }
        });
    },
    
    // Annulation de la saisie
    reset: function() {
        // Recherche
        var recherchePanel = this.getComponent('recherchePanel');
        recherchePanel.getComponent('nom').setValue(null);
        recherchePanel.getComponent('commune').setValue(null);
        recherchePanel.getComponent('numero').setValue(null);
        recherchePanel.getComponent('sectionCadastrale').setValue(null);
        recherchePanel.getComponent('parcelleCadastrale').setValue(null);
        recherchePanel.getComponent('avis').setValue('Tous');

        // Resultats
        this.getComponent('resultatsPanel').tab.hide();
        this.setActiveTab('recherchePanel');
    },
    
    // Lancement d'une recherche
    search: function() {
        var params = this.prepareRequest();
        if (params==null) {
            return;
        }
        
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../permis/search"),
            method: 'GET',
            params: params,
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    var decoded = Ext.decode(response.responseText);
                    if (decoded.total<1) {
                        Sdis.Remocra.util.Msg.msg('Permis', 'Aucun permis n\'a été trouvé.', 3);
                        return;
                    }
                    this.manageResults(decoded.data);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Permis', 'Un problème est survenu lors du chargement d\'un permis.' + msg + '.');
                }
            }
        });
    },
    
    prepareRequest: function() {
        // On réinitialise les données déjà reçues
        this.permisData = null;
        
        var recherchePanel = this.getComponent('recherchePanel');
        var nom = recherchePanel.getComponent('nom').getValue();
        var commune = recherchePanel.getComponent('commune').getValueModel();
        var numero = recherchePanel.getComponent('numero').getValue();
        var sectionCadastrale = recherchePanel.getComponent('sectionCadastrale').getValue();
        var parcelleCadastrale = recherchePanel.getComponent('parcelleCadastrale').getValue();
        var avis = recherchePanel.getComponent('avis').getValue();

        var params = {};
        if (nom!='') {
            params.nom = nom;
        }
        if (commune!=null) {
            params.commune = commune.get('id');
        }
        if (numero!='') {
            params.numero = numero;
        }
        if (sectionCadastrale!='') {
            params.sectionCadastrale = sectionCadastrale;
        }
        if (parcelleCadastrale!='') {
            params.parcelleCadastrale = parcelleCadastrale;
        }
        if(avis != 'Tous'){
            params.avis = avis;
        }
        
        var sthgToDo = true;
        try {
            sthgToDo = Object.keys(params).length>0; 
        } catch(e) {
            sthgToDo = true; // IE<9 ?
        }
        if (!sthgToDo) {
            Sdis.Remocra.util.Msg.msg('Permis', 'Veuillez sélectionner au moins un critère', 3);
            return null;
        }
        return params;
    },
    
    manageResults: function(rawPermis) {
        if (rawPermis==null || rawPermis.length<1) {
            Sdis.Remocra.util.Msg.msg('Permis', 'Aucun permis ne correspond aux critères', 3);
            return;
        }
        var resultatsPanel = this.getComponent('resultatsPanel');
        
        resultatsPanel.tab.show();
        this.setActiveTab(resultatsPanel);
        
        // On conserve les derniers permis chargés pour passer un permis lors du zoom (événement loadPermis)
        this.permisData = rawPermis;
        
        // On affiche le résultat
        this.permisTpl.overwrite(resultatsPanel.getEl(), {id: this.id, permisData:rawPermis});
    },
    
    // Zoom sur le permis dont l'id est passé (wkt au cas où il ne soit pas trouvé (ne devrait pas arriver)
    zoomToPermis: function(permisId, wkt) {
        var foundPermis = null;
        if (this.permisData && this.permisData.length>0 && permisId) {
            var i;
            for (i=0 ; i<this.permisData.length ; i++) {
                var elt = this.permisData[i];
                if (elt.id == permisId) {
                    foundPermis = elt;
                }
            }
        }
        if (foundPermis) {
            // on vide le résultat  (redmine #3400)
            this.getComponent('resultatsPanel').tab.hide();
            this.setActiveTab('recherchePanel');            
            this.permisTpl.overwrite(this.getComponent('resultatsPanel').getEl(), {id: this.id, permisData:{}});            
            // On charge le permis
            this.zoomToGeomWkt(foundPermis.geometrie);
            this.fireEvent('loadPermis', foundPermis);
        } else if (wkt) {
            // Par sécurité
            this.zoomToGeomWkt(wkt);
        }
    },
    
    // Centrer sur une position
    zoomToGeomWkt: function(wkt) {
        var feat = new OpenLayers.Format.WKT().read(wkt);
        var pt = feat.geometry;
        var lonlat = new OpenLayers.LonLat(pt.x, pt.y);
        this.fireEvent('centerToLonLat', lonlat, 'EPSG:'+SRID); // Données brutes !
    }
});