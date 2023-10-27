<template>
    <div id="manageGestionnaires" class="container-fluid"> <!-- Définition page de gestion des gestionnaires/liste des gestionnaires -->
        <div class="row">
            <div class="col-md-12">
                <h1 class='gestionnaire'>Gestion des gestionnaires</h1>
            </div>
            <p class="paragraphe">
                D'ici vous pouvez consulter la liste de tous les gestionnaires enregistrés.<br>
                Vous pouvez également en ajouter, ainsi que supprimer et modifier chaque gestionnaire indépendamment.
            </p>
        </div>
        <div>
            <a>
                <button class="btnAction addG" @click="createGestionnaire()"><img src="/remocra/static/img/add.png"> Ajouter un gestionnaire</button>
            </a>
            <div class=divTabGestionnaire>
                <b-table id="tabGestionnaire" :fields="fields" :sort-by.sync="sortBy" :items="filteredResultGestionnaire" :per-page="perPage" :current-page="currentPage" small hover bordered striped>
                    <template slot="top-row" slot-scope="{fields}"> <!-- Contenu première ligne : input recherche -->
                        <td v-for="field in fields" :key="field.key">
                            <input v-if="field.key=='gestionnaire.nom'" id="inputNomGestionnaire" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
                            <input v-else-if="field.key=='gestionnaire.code'" id="inputCodeGestionnaire" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
                            <div v-else-if="field.key=='contact'" id="selectContactFilter">
                                <select v-on:click="filtering()" id="selectBoxContact">
                                    <option selected value=''> Ne pas filtrer </option>
                                    <option value='true'> Gestionnaires avec contacts </option>
                                    <option value='false'> Gestionnaires sans contact </option>
                                </select>
                            </div>
                        </td> 
                    </template>
                    <template slot="contact" slot-scope="data"> <!-- Contenu spécial colonne 'Contact' -->
                        <button v-if="data.item.gestionnaireHasContact==true" class="btnLink" @click="openListeContacts(data.item.gestionnaire.id, data.item.gestionnaire.nom)">Ouvrir la liste</button>
                        <button v-else class="btnLink" @click="directCreateContact(data.item.gestionnaire.id, data.item.gestionnaire.nom)">Ajouter un contact</button>
                    </template>
                    <template slot="actif" slot-scope="data" slot-class=actifCell> <!-- Contenu spécial colonne 'Actif' -->
                        <input v-if="data.item.gestionnaire.actif" type="checkbox" checked disabled label:none>
                        <input v-else type="checkbox" disabled label:none>
                    </template>
                    <template slot="actions" slot-scope="data"> <!-- Contenu spécial colonne des boutons d'actions -->
                        <button class="btnAction editG" @click="editGestionnaire(data.item.gestionnaire.id)"><img src="/remocra/static/img/pencil.png"> Modifier</button>
                        <button class="btnAction delG" @click="deleteGestionnaire(data.item.gestionnaire.id)"><img src="/remocra/static/img/decline.png"> Supprimer</button>
                    </template>
                </b-table>
                <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage" aria-controls="tabGestionnaire"></b-pagination>
            </div>
        </div>
        <!-- Pop up de confirmation de suppression de gestionnaire -->
        <b-modal name="modalconfirmDelete" id="confirmDelete" v-bind:title="'Suppression de '+nomGestionnaire"
          cancel-title="Annuler" ok-title="Supprimer" refs="confirmDelete" @ok="confirmClick">
            <h2>Êtes-vous sûr de vouloir supprimer ce gestionnaire ?</h2>
            <a>La suppression de ce dernier est irréversible.</a><br>
            <a v-if="listHydrantGestionnaire.length != 0">
                Ce gestionnaire est lié aux hydrants suivants :<br>
                <ul class="listHydrantSuppr" v-for="item in listHydrantGestionnaire" v-bind:key='item'>
                    <li class="listHydrantSuppr">{{item}}</li>
                </ul>
                Les hydrants seront conservés mais ses liens seront supprimés.
            </a>
        </b-modal>
        <!-- Liens vers pages externes -->
        <ModalContactGestionnaire v-on:gestionnaireContactsUpdate="onGestionnaireUpdated" ref="modalContactGestionnaire"/>
        <ModalFormGestionnaire v-on:gestionnaireUpdate="onGestionnaireUpdated" ref="modalFormGestionnaire"/>
    </div>
</template>


<script>
    import axios from 'axios'
    import _ from 'lodash'
    import ModalContactGestionnaire from './ModalContactGestionnaire.vue'
    import ModalFormGestionnaire from './ModalFormGestionnaire.vue'

    export default{
        components:{
            ModalContactGestionnaire,
            ModalFormGestionnaire,
        },

        data(){
            return {
                listeGestionnaireSource: [],
                filteredResultGestionnaire: [],
                fields: [
                    { key:'gestionnaire.nom', label:'Nom', tdClass:'nomCell', sortable: true},
                    { key:'gestionnaire.code', label:'N° SIREN/SIRET', tdClass:'codeCell', sortable: true},
                    { key:'contact', label:'Contact', tdClass:'contactCell'},
                    { key:'actif', label:'Actif', tdClass:'actifCell'},
                    { key:'actions', label:'', tdClass:'buttonCell'}
                ],

                filterNomGestionnaireValue: '',
                filterCodeGestionnaireValue: '',
                filterContactGestionnaireValue: '',

                perPage:13,
                currentPage:1,
                sortBy:'gestionnaire.nom',

                
                idGestionnaire:'',
                nomGestionnaire:'',

                listHydrantGestionnaire:[], // Liste des hydrants gérés par le gestionnaire
            };
        },

        mounted : function() {
            this.getGestionnaireData()
        },

        computed: {
            rows() {
                return this.filteredResultGestionnaire.length
            }
        },

        methods: {
            getGestionnaireData(){
                axios.get('/remocra/gestionnaire/manageGestionnaire').then(response => {
                    if(response.data) {
                        this.listeGestionnaireSource = this.filteredResultGestionnaire = response.data.data;
                    }
                })
            },
            openListeContacts(idGestionnaire_, nomGestionnaire_) {
                this.$refs.modalContactGestionnaire.showModalListeContacts(idGestionnaire_, nomGestionnaire_)
            },
            directCreateContact(idGestionnaire_, nomGestionnaire_){
                this.$refs.modalContactGestionnaire.directCreateContact(idGestionnaire_, nomGestionnaire_)
            },
            openGestionnaireForm(idGestionnaire_){
                this.$refs.modalFormGestionnaire.showModalFormGestionnaire(idGestionnaire_)
            },
            createGestionnaire(){
                this.idGestionnaire=null
                this.openGestionnaireForm(this.idGestionnaire)
            },
            editGestionnaire(id_){
                this.openGestionnaireForm(id_)
            },
            deleteGestionnaire(id_){
                if(id_==null){
                    return;
                }
                else{
                    this.idGestionnaire = id_
                    axios.get('/remocra/gestionnaire/' + this.idGestionnaire).then(response => {
                        this.nomGestionnaire = response.data.data ? response.data.data.nom:''
                    })
                    axios.get('/remocra/gestionnaire/getHydrant/'+this.idGestionnaire).then(response => {
                        this.listHydrantGestionnaire = response.data.data
                    })
                    this.$bvModal.show('confirmDelete')
                }
            },
            confirmClick(){
                axios.delete('/remocra/gestionnaire/deleteGestionnaire/'+this.idGestionnaire).then(()=>{
                    this.$nextTick(()=>{
                        this.onGestionnaireUpdated()
                        this.$bvModal.hide('confirmDelete')
                    })
                });
            },
            filtering(){
                this.filterNomGestionnaireValue = document.getElementById("inputNomGestionnaire").value.toUpperCase();
                this.filterCodeGestionnaireValue = document.getElementById("inputCodeGestionnaire").value.toUpperCase();
                this.filterContactGestionnaireValue = document.getElementById("selectBoxContact").value;
                this.filteredResultGestionnaire = this.listeGestionnaireSource;
                let temp=[]

                if(this.filterNomGestionnaireValue!=''){ // Filtrage sur 'Nom'
                    temp = [] 
                    _.forEach(this.filteredResultGestionnaire, row => {
                        if(row.gestionnaire.nom.toUpperCase().startsWith(this.filterNomGestionnaireValue)){
                        temp.push(row)
                        }
                    })
                    this.filteredResultGestionnaire = temp
                }

                if(this.filterCodeGestionnaireValue!=''){ // Filtrage sur 'N° Siren'
                    temp = [] 
                    _.forEach(this.filteredResultGestionnaire, row => {
                        if(row.gestionnaire.code != null 
                            && row.gestionnaire.code.toUpperCase().includes(this.filterCodeGestionnaireValue)){
                        temp.push(row)
                        }
                    })
                    this.filteredResultGestionnaire = temp
                }

                if(this.filterContactGestionnaireValue!=''){ // Filtrage sur 'possède des contacts'
                    temp = [] 
                    if(this.filterContactGestionnaireValue=='true'){
                        _.forEach(this.filteredResultGestionnaire, row => {
                            if(row.gestionnaireHasContact==true){
                                temp.push(row)
                            }
                        })
                    }
                    else if(this.filterContactGestionnaireValue=='false'){
                        _.forEach(this.filteredResultGestionnaire, row => {
                            if(row.gestionnaireHasContact==false){
                                temp.push(row)
                            }
                        })
                    }
                    this.filteredResultGestionnaire = temp
                }
            },
            onGestionnaireUpdated(){
                this.getGestionnaireData()
            }
        }
    };
</script>

<style>
    h1.gestionnaire {
        font-size: 18px !important;
        text-transform:uppercase;
        color: #7b7b7b;
        font-family: Helvetica, Arial !important
    }

    .paragraphe{margin-left:20px; padding:5px;}
    .row{padding-bottom:5px;}

    ul.listHydrantSuppr{margin:5px!important;}
    li.listHydrantSuppr{list-style:disc!important; margin-left:34px!important;}

    .btnAction {
        display: inline-block;
        border-radius: 5px;
        padding: 0px 2px;
        font-size:10px;
    }
    .addG{border: solid #007bff;background-color: #007bff45;margin: 5px;}
    .addG:hover{color: #007bff;background-color: #fff;}
    .editG{border: solid #17a2b8;background-color: #17a2b845;}
    .editG:hover{color: #17a2b8;background-color: #fff;}
    .delG{border: solid #c4050a;background-color: #c4050a45;}
    .delG:hover{color: #c4050a;background-color: #fff;}

    select#selectBoxContact{min-width:75%!important;margin:unset;height:22px;}
    li#listHydrantSuppr{list-style: disc;}

    .nomCell{min-width:33%;}
    .codeCell{width:33%;min-width:60px;}
    .contactCell{width:20%;min-width:110px;}
    .buttonCell{width:141px;padding:0 !important;}
    .actifCell{width:23px;text-align: center;}

    .btnLink {
        background: none!important;
        border: none;
        padding: 0!important;
        color: #069;
        cursor: pointer;
    }
    .btnLink:hover{text-decoration: underline;}

    thead, thead td{background-color: #afafaf;}
    #tabGestionnaire thead th{border: solid 1pt;border-bottom: none;}
    tr.b-table-top-row td{background-color: #afafaf;border: solid 1pt;border-top:none;}
</style>