<template>
    <div id="manageSites" class="container-fluid"> <!-- Définition page de gestion des sites de gestionnaires -->
        <div class="row">
            <div class="col-md-12">
                <h1 class='site'>Gestion des sites</h1>
            </div>
            <p class="paragraphe">
                D'ici vous pouvez consulter la liste de tous les sites enregistrés.<br>
                Vous pouvez également les modifier indépendamment.
            </p>
        </div>
        <div>
            <div class=divTabSite>
                <b-table id="tabSite" :fields="computedFields" :sort-by.sync="sortBy" :items="filteredResultSite" :per-page="perPage"
                    :current-page="currentPage" small hover bordered striped>
                    <template slot="top-row" slot-scope="{fields}"> <!-- Contenu première ligne : input recherche -->
                        <td v-for="field in fields" :key="field.key">
                            <input v-if="field.key=='gestionnaireSite.nom'" id="inputNomSite" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
                            <input v-else-if="field.key=='gestionnaireSite.code'" id="inputCodeSite" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
                            <input v-else-if="field.key=='gestionnaireName'" id="inputGestionnaireSite" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
                        </td>
                    </template>
                    <template slot="actif" slot-scope="data" slot-class=actifCell> <!-- Contenu spécial colonne 'Actif' -->
                        <input v-if="data.item.gestionnaireSite.actif" type="checkbox" checked disabled label:none>
                        <input v-else type="checkbox" disabled label:none>
                    </template>
                    <template slot="actions" slot-scope="data"> <!-- Contenu spécial colonne des boutons d'actions -->
                        <button class="btnAction editGS" @click="editSite(data.item.gestionnaireSite.id)"><img src="/remocra/static/img/pencil.png"> Modifier</button>
                        <button class="btnAction delGS" @click="deleteSite(data.item.gestionnaireSite.id)"><img src="/remocra/static/img/decline.png"> Supprimer</button>
                    </template>
                </b-table>
                <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage" aria-controls="tabSite"></b-pagination>
            </div>
        </div>
        <!-- Pop up de confirmation de suppression de gestionnaire -->
        <b-modal name="modalconfirmDelete" id="confirmDelete" v-bind:title="'Suppression de '+nomGestionnaireSite"
          cancel-title="Annuler" ok-title="Supprimer" refs="confirmDelete" @ok="confirmClick">
            <h2>Êtes-vous sûr de vouloir supprimer ce site ?</h2>
            <a>La suppression de ce dernier est irréversible.</a><br><br>
            <a v-if="listeHydrantSite.length != 0">
                Attention, ce site est lié à un/des hydrant(s).<br>
                Les hydrants seront conservés mais les liens seront supprimés.<br>
                Ce site est lié aux hydrants suivants :<br>
                <div id="displayHydrantList" class="overflow-auto max-h-200 border rounded-sm">
                    <ul class="listHydrantSuppr" v-for="item in listeHydrantSite" v-bind:key='item'>
                        <li class="listHydrantSuppr">{{item}}</li>
                    </ul>
                </div>
            </a>
            <br v-if="listeHydrantSite.lenght !=0 && listeContactSite.length != 0">
            <a v-if="listeContactSite.length != 0">
                Attention, ce site est lié à un/des contact(s).<br>
                Les contacts seront conservés mais les liens seront supprimés.<br>
                Ce site est lié aux contacts suivants :<br>
                <div id="displayContactList" class="overflow-auto max-h-200 border rounded-sm">
                    <ul class="listContactSuppr" v-for="item in listeContactSite" v-bind:key='item'>
                        <li class="listContactSuppr">{{item}}</li>
                    </ul>
                </div>
            </a>
        </b-modal>
        <ModalFormGestionnaireSite v-on:gestionnaireSiteUpdate="onGestionnaireSiteUpdated" ref="modalFormGestionnaireSite"/>
    </div>
</template>

<script>
    import axios from 'axios'
    import _ from 'lodash'
    import ModalFormGestionnaireSite from './ModalFormGestionnaireSite.vue'

    import { GESTIONNAIRE_E } from '../GlobalConstants.js'

    export default {
        components: {
            ModalFormGestionnaireSite
        },

        data() {
            return {
                listeSiteSource: [],
                filteredResultSite: [],
                fields: [
                    { key: 'gestionnaireSite.nom', label: 'Nom', tdClass: 'nomCell', sortable: true },
                    { key: 'gestionnaireSite.code', label: 'Code', tdClass: 'codeCell', sortable: true },
                    { key: 'gestionnaireName', label: 'Gestionnaire', tdClass: 'gestionnaireCell', sortable: true },
                    { key: 'actif', label: 'Actif', tdClass: 'actifCell' },
                    { key: 'actions', label: '', tdClass: 'buttonCell', requiresAdmin: true}
                ],

                allowed: false,

                filterNomSiteValue: '',
                filterCodeSiteValue: '',
                filterGestionnaireSiteValue: '',

                perPage:13,
                currentPage:1,
                sortBy:'gestionnaireSite.nom',

                idGestionnaireSite: null,
                nomGestionnaireSite: '',
                listeHydrantSite: [], // Liste des hydrants lié au site
                listeContactSite: [], // Liste des contacts lié au site
            }
        },

        mounted : function() {
            this.getUserRights();
            this.getGestionnaireSiteData();
        },

        computed: {
            rows() {
                return this.filteredResultSite.length;
            },
            computedFields() {
                if(!this.allowed){
                    return this.fields.filter(field => !field.requiresAdmin);
                } else {
                    return this.fields;
                }
            }
        },

        methods: { 
            getUserRights(){
                axios.get('/remocra/utilisateurs/current/getRight/' + GESTIONNAIRE_E).then(response => {
                    this.allowed = response.data;
                });
            },
            getGestionnaireSiteData(){
                axios.get('/remocra/gestionnairesite/manageGestionnaireSite').then(response => {
                    if(response.data) {
                        this.listeSiteSource = this.filteredResultSite = response.data;
                    }
                })
            },
            openGestionnaireSiteForm(idGestionnaireSite_){
                this.$refs.modalFormGestionnaireSite.showModalFormGestionnaireSite(idGestionnaireSite_)
            },
            editSite(idGestionnaireSite_) {
                this.openGestionnaireSiteForm(idGestionnaireSite_)
            },
            deleteSite(idGestionnaireSite_) {
                if (idGestionnaireSite_ == null) {
                    return;
                } else {
                    this.idGestionnaireSite = idGestionnaireSite_
                    axios.get('/remocra/gestionnairesite/' + this.idGestionnaireSite).then(response => {
                        this.nomGestionnaireSite = response.data ? response.data.nom : ''
                    })
                    axios.get('/remocra/gestionnairesite/gethydrant/' + this.idGestionnaireSite).then(response => {
                        this.listeHydrantSite = response.data
                    })
                    axios.get('/remocra/gestionnairesite/getcontact/' + this.idGestionnaireSite).then(response => {
                        this.listeContactSite = response.data
                    })
                    this.$bvModal.show('confirmDelete')
                }
            },
            confirmClick() {
                axios.delete('/remocra/gestionnairesite/deleteGestionnaireSite/' + this.idGestionnaireSite).then(() => {
                    this.$nextTick(() => {
                        this.onGestionnaireSiteUpdated()
                        this.$bvModal.hide('confirmDelete')
                    })
                });
            },
            filtering(){
                this.filterNomSiteValue = document.getElementById("inputNomSite").value.toUpperCase();
                this.filterCodeSiteValue = document.getElementById("inputCodeSite").value.toUpperCase();
                this.filterGestionnaireSiteValue = document.getElementById("inputGestionnaireSite").value.toUpperCase();
                this.filteredResultSite = this.listeSiteSource;

                this.filteredResultSite = this.filteringPart(this.filteredResultSite, 'gestionnaireSite','nom', this.filterNomSiteValue) // Filtrage sur les noms
                this.filteredResultSite = this.filteringPart(this.filteredResultSite, 'gestionnaireSite','code', this.filterCodeSiteValue) // Filtrage sur les codes
                this.filteredResultSite = this.filteringPart(this.filteredResultSite, 'gestionnaireName', null, this.filterGestionnaireSiteValue) // Filtrage sur les noms de gestionnaires
            },
            filteringPart(list_, champ1_, champ2_, valeurFiltre_){
                let temp = []
                if(valeurFiltre_!=''){
                    _.forEach(list_, row => {
                        if(champ2_){
                            if(row[champ1_][champ2_] && row[champ1_][champ2_].toUpperCase().includes(valeurFiltre_)){
                                temp.push(row)
                            } 
                        } else {
                            if(row[champ1_] && row[champ1_].toUpperCase().includes(valeurFiltre_)){
                                temp.push(row)
                            }
                        }
                    })
                    return temp
                }
                return list_
            },
            onGestionnaireSiteUpdated(){
                this.getGestionnaireSiteData()
            }
        }
    }

</script>

<style>
    h1.site {
        font-size: 18px !important;
        text-transform:uppercase;
        color: #7b7b7b;
        font-family: Helvetica, Arial !important
    }

    .btnAction {
        display: inline-block;
        border-radius: 5px;
        padding: 0px 2px;
        font-size:10px;
    }

    .addGS{border: solid #007bff;background-color: #007bff45;margin: 5px;}
    .addGS:hover{color: #007bff;background-color: #fff;}
    .editGS{border: solid #17a2b8;background-color: #17a2b845;}
    .editGS:hover{color: #17a2b8;background-color: #fff;}
    .delGS{border: solid #c4050a;background-color: #c4050a45;}
    .delGS:hover{color: #c4050a;background-color: #fff;}

    .nomCell{min-width:33%;}
    .codeCell{width:20%;min-width:60px;}
    .gestionnaireCell{width:33%;min-width:110px;}
    .buttonCell{width:141px;padding:0 !important;}
    .actifCell{width:23px;text-align: center;}

    .max-h-200{max-height: 200px;}

    ul.listHydrantSuppr, ul.listContactSuppr{margin:5px!important;}
    li.listHydrantSuppr, li.listContactSuppr{list-style:disc!important; margin-left:34px!important;}

    thead, thead td{background-color: #afafaf;}
    #tabSite thead th{border: solid 1pt ;border-bottom: none;}
    tr.b-table-top-row td{background-color: #afafaf;border: solid 1pt;border-top:none;}
</style>