Ext.require('Sdis.Remocra.widget.LightPaging');
Ext.require('Sdis.Remocra.widget.BlocDocumentGrid');
Ext.require('Sdis.Remocra.widget.BlocCourrierGrid');

Ext.define('Sdis.Remocra.features.index.BlocsFactory', {
    singleton: true,

    createHydrantsBloc: function(title, icon, minHeight) {

        var blocHydrants = {
            type: 'hydrants',
            minh: minHeight || this.randomHeight(),
            title: title || 'Points d\'eau',
            img: icon || 'pointseau.png',
            items: []
        };

        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_R')) {
            blocHydrants.items.push({
                type: 'href',
                href: 'hydrants',
                lbl: 'Gestion des points d\'eau'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('PLANIFIER_DECI')) {
            blocHydrants.items.push({
                type: 'href',
                href: 'hydrants/planification',
                lbl: 'Planification DECI'
            });
        }
        
        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_PRESCRIT_R')
            || Sdis.Remocra.Rights.hasRight('HYDRANTS_PRESCRIT_C')) {
            blocHydrants.items.push({
                type: 'href',
                href: 'hydrants/prescrits',
                lbl: 'Carte des points d\'eau prescrits'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('DEPOT_DECLAHYDRANT_C')) {
            if (!Ext.isEmpty(blocHydrants.items)) {
                blocHydrants.items.push({
                    type: 'sep'
                });
            }
            // Ici, on définit un onclick qui modifie le href à
            // chaque fois (cas où l'utilisateur ferme la
            // fenêtre et clique à nouveau sur le lien)
            blocHydrants.items.push({
                type: 'href',
                href: 'hydrants/declahydrant',
                lbl: 'Déclarer des points d\'eau',
                onclick: ' onclick="this.href=\'#hydrants/declahydrant/\'+Math.floor(Math.random()*999);"'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_TELEVERSER_C') && (
            Sdis.Remocra.Rights.hasRight('HYDRANTS_C') || Sdis.Remocra.Rights.hasRight('HYDRANTS_RECONNAISSANCE_C')
            || Sdis.Remocra.Rights.hasRight('HYDRANTS_CONTROLE_C'))) {
            if (!Ext.isEmpty(blocHydrants.items)) {
                blocHydrants.items.push({
                    type: 'sep'
                });
            }
            blocHydrants.items.push({
                type: 'href',
                href: 'hydrants/televerser',
                lbl: 'Téléverser un fichier de visites',
                onclick: ' onclick="this.href=\'#hydrants/televerser/\'+Math.floor(Math.random()*999);"'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('HYDRANTS_TRAITEMENT_C') || Sdis.Remocra.Rights.hasRight('TRAITEMENTS_C')) {
            if (!Ext.isEmpty(blocHydrants.items)) {
                blocHydrants.items.push({
                    type: 'sep'
                });
            }
            blocHydrants.items.push({
                type: 'href',
                href: 'traitements/index/application/1',
                lbl: 'Accéder aux traitements'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('COURRIER_C')) {
            if (!Ext.isEmpty(blocHydrants.items)) {
                blocHydrants.items.push({
                    type: 'sep'
                });
            }
            blocHydrants.items.push({
                type: 'href',
                href: 'hydrants/courrier',
                lbl: 'Générer des courriers',
                onclick: 'onClick="Sdis.Remocra.features.index.BlocsFactory.createCourrierWindow(\'POINTDEAU\');"'
            });
        }
        
        if (Ext.isEmpty(blocHydrants.items)) {
            return null;
        }
        return blocHydrants;
    },

    createCourrierWindow: function(thematique){
    var d = document.createElement('div');
    document.body.appendChild(d);
    var vueCourrier = window.remocraVue.buildCourrier(d, {thematique: thematique});
    vueCourrier.$options.bus.$on('closed',
        Ext.bind(function(data) {
            vueCourrier.$el.remove();
            vueCourrier.$destroy();
        }, this));
    },

    createAdressesBloc: function(title, icon, minHeight) {
        var blocAdresses = {
            type: 'adresses',
            minh: minHeight || this.randomHeight(),
            title: title || 'Adresses',
            img: icon || 'adresses.png',
            items: []
        };

        if (Sdis.Remocra.Rights.hasRight('ADRESSES_C')) {
            blocAdresses.items.push({
                type: 'href',
                href: 'adresses',
                lbl: 'Carte des adresses'
            });
        }
        if (Sdis.Remocra.Rights.hasRight('DEPOT_DELIB_C')) {
            if (!Ext.isEmpty(blocAdresses.items)) {
                blocAdresses.items.push({
                    type: 'sep'
                });
            }
            // Ici, on définit un onclick qui modifie le href à
            // chaque fois (cas où l'utilisateur ferme la
            // fenêtre et clique à nouveau sur le lien)
            blocAdresses.items.push({
                type: 'href',
                href: 'adresses/delib',
                lbl: 'Déposer une délibération',
                onclick: ' onclick="this.href=\'#adresses/delib/\'+Math.floor(Math.random()*999);"'
            });
        }
        if (Sdis.Remocra.Rights.hasRight('TRAITEMENTS_C')) {
            //Traitements
            if (!Ext.isEmpty(blocAdresses.items)) {
                blocAdresses.items.push({
                    type: 'sep'
                });
            }

            blocAdresses.items.push({
                type: 'href',
                href: 'traitements/index/application/2',
                lbl: 'Accéder aux traitements'
            });
        }

        if (Ext.isEmpty(blocAdresses.items)) {
            return null;
        }
        return blocAdresses;
    },
    createOldebsBloc: function(title, icon, minHeight) {
        var blocOldebs = {
            type: 'oldebs',
            minh: minHeight || this.randomHeight(),
            title: title || 'Comité de secteur',
            img: icon || 'oldeb.png',
            items: []
        };

        if (Sdis.Remocra.Rights.hasRight('OLDEB_R')) {
            blocOldebs.items.push({
                type: 'href',
                href: 'oldebs',
                lbl: 'Protection incendie des biens et personnes'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('OLDEB_R')) {
            if (!Ext.isEmpty(blocOldebs.items)) {
                blocOldebs.items.push({
                    type: 'sep'
                });
            }
            blocOldebs.items.push({
                type: 'href',
                href: 'traitements/index/application/8',
                lbl: 'Accéder aux traitements'
            });
        }
        if (Ext.isEmpty(blocOldebs.items)) {
            return null;
        }
        return blocOldebs;
    },
    createCrisesBloc: function(title, icon, minHeight) {
        var blocCrises = {
            type: 'crises',
            minh: minHeight || this.randomHeight(),
            title: title || 'Gestion de crise',
            img: icon || 'gestion-crise.png',
            items: []
        };

        if (Sdis.Remocra.Rights.hasRight('CRISE_R')) {
            blocCrises.items.push({
                type: 'href',
                href: 'crises',
                lbl: 'Suivi cartographique de crise'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('CRISE_R')) {
            if (!Ext.isEmpty(blocCrises.items)) {
                blocCrises.items.push({
                    type: 'sep'
                });
            }
            blocCrises.items.push({
                type: 'href',
                href: 'traitements/index/application/9',
                lbl: 'Accéder aux traitements'
            });
        }
        if (Ext.isEmpty(blocCrises.items)) {
            return null;
        }
        return blocCrises;
    },
    createPermisBloc: function(title, icon, minHeight) {
        if (!Sdis.Remocra.Rights.hasRight('PERMIS_R')) {
            return null;
        }
        var blocPermis = {
            type: 'permis',
            minh: minHeight || this.randomHeight(),
            title: title || 'Permis',
            img: icon || 'permis.png',
            items: [{
                type: 'href',
                href: 'permis',
                lbl: 'Carte des permis'
            }]
        };

        if (Sdis.Remocra.Rights.hasRight('PERMIS_TRAITEMENT_C')) {
            blocPermis.items.push({
                type: 'sep'
            }, {
                type: 'href',
                href: 'traitements/index/application/3',
                lbl: 'Accéder aux traitements'
            });
        }
        return blocPermis;
    },
    
    createRciBloc: function(title, icon, minHeight) {
        if (!Sdis.Remocra.Rights.hasRight('RCI_C')) {
            return null;
        }
        var bloc = {
            type: 'rci',
            minh: minHeight || this.randomHeight(),
            title: title || 'Recherche des Causes et des Circonstances Incendie',
            img: icon || 'rci.png',
            items: [{
                type: 'href',
                href: 'rci',
                lbl: 'Carte des départs'
            }]
        };

        if (Sdis.Remocra.Rights.hasRight('RCI_C') || Sdis.Remocra.Rights.hasRight('TRAITEMENTS_C')) {
            bloc.items.push({
                type: 'sep'
            }, {
                type: 'href',
                href: 'traitements/index/application/7',
                lbl: 'Accéder aux traitements'
            });
        }

        if (Sdis.Remocra.Rights.hasRight('COURRIER_C')) {
            if (!Ext.isEmpty(bloc.items)) {
                bloc.items.push({
                    type: 'sep'
                });
            }
            bloc.items.push({
                type: 'href',
                href: 'traitements/courrier',
                lbl: 'Générer des courriers',
                onclick: 'onClick="Sdis.Remocra.features.index.BlocsFactory.createCourrierWindow(\'RCI\');"'
            });
        }
        return bloc;
    },

    createDFCIBloc: function(title, icon, minHeight) {
        if (!Sdis.Remocra.Rights.hasRight('DFCI_R')) {
            return null;
        }
        var blocDfci = {
            type: 'dfci',
            minh: minHeight || this.randomHeight(),
            title: title || 'Défense de la Forêt Contre les Incendies',
            img: icon || 'dfci.png',
            items: [{
                type: 'href',
                href: 'dfci',
                lbl: 'Carte DFCI'
            }]
        };
        if (Sdis.Remocra.Rights.hasRight('DEPOT_RECEPTRAVAUX_C')) {
            // Ici, on définit un onclick qui modifie le href à
            // chaque fois (cas où l'utilisateur ferme la
            // fenêtre et clique à nouveau sur le lien)
            blocDfci.items.push({
                type: 'sep'
            });
            blocDfci.items.push({
                type: 'href',
                href: 'dfci/receptravaux',
                lbl: 'Recevoir des travaux',
                onclick: ' onclick="this.href=\'#dfci/receptravaux/\'+Math.floor(Math.random()*999);"'
            });
        }
        return blocDfci;
    },

    createRisquesBloc: function(title, icon, minHeight) {
        // Public, aucun droit
        return {
            type: 'risques',
            minh: minHeight || this.randomHeight(),
            title: title || 'Carte des risques',
            img: icon || 'risques.png',
            items: [{
                type: 'href',
                href: 'risques',
                lbl: 'Carte des risques'
            },{
                type: 'sep'
            }]
        };
    },

    createMetadonneesBloc: function(title, icon, minHeight) {
        // Public, aucun droit
        return {
            type: 'metadonnees',
            minh: minHeight || this.randomHeight(),
            title: title || 'Métadonnées',
            img: icon || 'metadonnees.png',
            items: [{
                type: 'href',
                href: 'metadonnees',
                lbl: 'Accéder aux métadonnées'
            }]
        };
    },

    createTraitementsBloc: function(title, icon, minHeight) {
        if (!Sdis.Remocra.Rights.hasRight('TRAITEMENTS_C')) {
            return null;
        }
        return {
            type: 'traitements',
            minh: minHeight || this.randomHeight(),
            title: title || 'Extraire, télécharger, téléverser',
            img: icon || 'traitements.png',
            items: [{
                type: 'href',
                href: 'traitements',
                lbl: 'Accéder aux traitements'
            }]
        };
    },

    createAdministrationBloc: function(title, icon, minHeight) {
        var items = [];
        if(Sdis.Remocra.Rights.hasRight('REFERENTIELS_C')) {
            items.push({
               type: 'href',
               href: 'admin/index/elt/paramconf',
               lbl: 'Paramètres de configuration'
             });
             items.push({
                 type: 'href',
                 href: 'hydrants/anomalies',
                 lbl: 'Gestion des anomalies'
             });
             items.push({
                 type: 'sep'
             });
             items.push({
                 type: 'href',
                 href: 'admin/index/elt/organismes',
                 lbl: 'Les organismes'
             });
             items.push({
                 type: 'sep'
             });
             items.push({
                type: 'href',
                href: 'importDocuments',
                lbl: 'Importer des documents'
            });
            items.push({
                type: 'sep'
            });
            items.push({
                type: 'href',
                href: 'manageGestionnaires',
                lbl: 'Gestion des gestionnaires'
            });
            items.push({
                type: 'sep'
            });
        }
        if (Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ALL_C')
            || Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ALL_R')
            || Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C')
            || Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_R')
            || Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C')) {
            items.push({
                type: 'href',
                href: 'admin/index/elt/utilisateurs',
                lbl: 'Les utilisateurs'
            });
        }
        if (Sdis.Remocra.Rights.hasRight('DOCUMENTS_C')) {
           items.push({
               type: 'sep'
           });
           items.push({
               type: 'href',
               href: 'admin/documents',
               lbl: 'Les documents'
           });
        }

        //Gestion des accès API
        if (Sdis.Remocra.Rights.hasRight('APP_TRANSFERTS_AUTOMATISES'))
        {
            items.push({
                type: 'sep'
            });
            items.push({
                type: 'href',
                href: 'transfertsAutomatises',
                lbl: 'Les transferts automatisés'
            });
        }

        if (items.length<1) {
            return null;
        }
        return {
           type: 'admin',
           minh: minHeight || this.randomHeight(),
           title: title || 'Administrer',
           img: icon || 'administrer.png',
           items: items
        };
    },

    createInfoBloc: function(title, icon, minHeight, content) {
        return {
            type: 'info',
            minh: minHeight || this.randomHeight(),
            title: title || 'Flash info',
            img: icon || 'info.png',
            items: [{
                type: 'html',
                content: content
            }]
        };
    },

    createDashboardBloc: function(title, icon, minHeight, content) {
        if (!Sdis.Remocra.Rights.hasRight('DASHBOARD_R')) {
            return null;
        }
        return {
            type: 'dashboard',
            minh: minHeight || this.randomHeight(),
            title: title || 'Tableau de bord',
            items: [{
                    type: 'html',
                   content: '<div><a href="#dashboard">' +
                   '<img style="max-width: 500px;" src="ext-res/images/remocra/blocs/dashboard.gif">' +
                   '</a></div>'
            }]
        };
    },


    createDocBloc: function(title, icon, minHeight, pageSize, thematiques, profils) {
        if (!Sdis.Remocra.Rights.hasRight('DOCUMENTS_R')) {
            return null;
        }
        return {
            type: 'doc',
            minh: minHeight || this.randomHeight(),
            title: title || 'Documents',
            img: icon || 'documents.png',

            // Sur le rendu du bloc,
            onBlocRender: function(cmp, eOpts) {
                cmp.add({
                    xtype : 'crBlocDocumentGrid',
                    thematiques: thematiques,
                    profils: profils,
                    pageSize: pageSize
                });
            }
        };
    },

    createCartographieBloc: function(title, icon, minHeight) {
        if (!Sdis.Remocra.Rights.hasRight('CARTOGRAPHIES_C')) {
            return null;
        }
        return {
            type: 'cartographie',
            minh: minHeight || this.randomHeight(),
            title: title || 'Cartographie',
            img: icon || 'cartographie.png',
            items: [{
                type: 'href',
                href: 'cartographie',
                lbl: 'Réaliser une cartographie'
            },{
                type: 'sep'
            }]
        };
    },

    createCourriersBloc: function(title, icon, minHeight, pageSize) {
        if (!Sdis.Remocra.Rights.hasRight('COURRIER_UTILISATEUR_R')
            && !Sdis.Remocra.Rights.hasRight('COURRIER_ORGANISME_R')
            && !Sdis.Remocra.Rights.hasRight('COURRIER_ADMIN_R')) {
            return null;
        }
        return {
            type: 'courriers',
            minh: minHeight || this.randomHeight(),
            title: title || 'Courriers',
            img: icon || 'documents.png',
            onBlocRender: function(cmp, eOpts) {
                var store = Ext.create('Sdis.Remocra.store.BlocCourrier', {
                    autoLoad: true,
                    pageSize: 1,
                    filters: [{
                        property : 'accuse',
                        value : 'false'
                    }]
                });

                store.on('load', function(store) {
                    cmp.insert(0, {
                        xtype: 'label',
                        cls: 'blocCourrierNbDocs',
                        html: '<b>'+store.getTotalCount()+'</b> nouveaux courriers'
                    });

                    if(store.getTotalCount()) {
                        cmp.add({
                            xtype : 'crBlocCourrierGrid',
                            pageSize: pageSize
                        });
                    } else {
                        cmp.setHeight(150);
                    }
                });

                cmp.add({
                    xtype: 'component',
                    cls: 'blocCourrierLien',
                    autoEl: {
                        tag: 'a',
                        href: '#courriers',
                        html: 'Recherche de courrier'
                    }
                });

            }
        };
    },

    createBloc: function(cfgBloc) {
        // Présenter ou non le bloc en fonction du profil
        // Profils (affichage du bloc ou non)
        var profils = cfgBloc.profils;
        if (profils) {
            // Filtrage activé pour ce bloc
            if (Ext.isString(profils)) {
                if (REMOCRA_USR_PROFIL_DROIT != profils) {
                    // Un seul code de profils (et non trouvé)
                    return null;
                }
            } else {
                if (Ext.Array.indexOf(profils, REMOCRA_USR_PROFIL_DROIT)<0) {
                    // Plusieurs codes de profils (et non trouvé)
                    return null;
                }
            }
        }

        // Config commune
        var title = cfgBloc.title, icon = cfgBloc.icon, minHeight = cfgBloc.minHeight;

        // Blocs INFO
        if (cfgBloc.type == 'info') {
            // title icon content
            return this.createInfoBloc(title, icon, minHeight, cfgBloc.content);
        }

        // Blocs DOCUMENTS
        if (cfgBloc.type == 'doc') {
            // title icon content
            return this.createDocBloc(title, icon, minHeight, cfgBloc.pageSize, cfgBloc.thematiques, cfgBloc.profils);
        }

        if(cfgBloc.type == 'courriers') {
            return this.createCourriersBloc(title, icon, minHeight, cfgBloc.pageSize);
        }

        // Autres blocs : uniquement config commune
        var fn = null;
        if (cfgBloc.type == 'hydrants') {
            fn = this.createHydrantsBloc;
        } else if (cfgBloc.type == 'oldebs') {
            fn = this.createOldebsBloc;
        }else if (cfgBloc.type == 'adresses') {
            fn = this.createAdressesBloc;
        } else if (cfgBloc.type == 'permis') {
            fn = this.createPermisBloc;
        } else if (cfgBloc.type == 'crises') {
            fn = this.createCrisesBloc;
        }else if (cfgBloc.type == 'rci') {
            fn = this.createRciBloc;
        } else if (cfgBloc.type == 'dfci') {
            fn = this.createDFCIBloc;
        } else if (cfgBloc.type == 'risques') {
            fn = this.createRisquesBloc;
        } else if (cfgBloc.type == 'metadonnees') {
            fn = this.createMetadonneesBloc;
        } else if (cfgBloc.type == 'traitements') {
            fn = this.createTraitementsBloc;
        } else if (cfgBloc.type == 'admin') {
            fn = this.createAdministrationBloc;
        } else if (cfgBloc.type == 'cartographie') {
            fn = this.createCartographieBloc;
        }else if (cfgBloc.type == 'dashboard') {
            fn = this.createDashboardBloc;
        }
        if (fn) {
            return Ext.bind(fn, this)(title, icon, minHeight);
        }
        // Bloc non géré
        return null;
    },

    createBlocsGridFromCfg: function(gridCfg) {
        var returned = [];
        var colCfgi;
        for (colCfgi in gridCfg) {
            var colCfg = gridCfg[colCfgi];
            if (colCfg.length > 0) {
                var col = [], i;
                for (i in colCfg) {
                    var blocCfg = colCfg[i];
                    if (blocCfg) {
                        col.push(this.createBloc(blocCfg));
                    }
                }
                returned.push(col);
            }
        }
        return returned;
    },

    // Retourne un nombre entre 100 et 200
    randomHeight: function() {
        var r = Math.random() * 100;
        var x = 25;
        r = r < x ? 0 : r;
        r = r > 100 - x ? 100 : r;
        return Math.floor(100 + r);
    }
});
