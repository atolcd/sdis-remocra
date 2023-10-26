<template>
<div id="modalListContact"> <!-- Définition pop up liste des contacts d'un gestionnaire -->
  <b-modal name="modalContactGestionnaire" id="modalContactGestionnaire" v-bind:title="buildedTitle"
    refs="modalContactGestionnaire" hide-footer :clickToClose="false">
    <div>
      <p>
        <button class="btnAction addG" @click="createContact"><img src="/remocra/static/img/add.png"> Ajouter un contact</button>
      </p>
      <div class=divTabContactGestionnaire>
        <b-table id="tabContactGestionnaire" :fields="fields" :items="filteredContacts" :per-page="perPage" :current-page="currentPage" small hover bordered striped>
          <template slot="top-row" slot-scope="{fields}"> <!-- Contenu première ligne : input recherche -->
            <td v-for="field in fields" :key="field.key">
              <input v-if="field.key=='contact.nom'" id="inputNomContact" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
              <input v-else-if="field.key=='contact.prenom'" id="inputPrenomContact" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
              <input v-else-if="field.key=='contact.fonction'" id="inputFonctionContact" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
              <input v-else-if="field.key=='siteContactNom'" id="inputSiteContact" v-on:keyup="filtering()" :placeholder="'Rechercher par '+field.label" class="w-100">
            </td>
          </template>
          <template slot="actions" slot-scope="data"> <!-- Contenu spécial colonne des boutons d'actions -->
            <button class="btnAction editG" @click="editContact(data.item.contact.id)"><img src="/remocra/static/img/pencil.png"> Modifier</button>
            <button class="btnAction delG" @click="deleteContact(data.item.contact.id)"><img src="/remocra/static/img/decline.png"> Supprimer</button>
          </template>
        </b-table>
        <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage" aria-cotnrols="tabContactGestionnaire"></b-pagination>
      </div>
    </div>
  </b-modal>
  <!-- Pop up de confirmation de suppression de gestionnaire -->
  <b-modal name="modalConfirmDeleteContact" id="confirmDeleteContact" v-bind:title="'Suppression de '+nomContact"
    cancel-title="Annuler" ok-title="Supprimer" refs="confirmDeleteContact" @ok="confirmClick">
      <h2>Êtes-vous sûr de vouloir supprimer ce contact ?</h2>
      <a>La suppression de ce dernier est irréversible.</a>
  </b-modal>
  <!-- Lien vers page externe -->
  <ModalFormContactGestionnaire v-on:contactUpdate="onContactUpdated" ref="modalFormContactGestionnaire"></ModalFormContactGestionnaire>
</div>
</template>

<script>
  import axios from 'axios'
  import _ from 'lodash'
  import ModalFormContactGestionnaire from './ModalFormContactGestionnaire.vue'
  
  export default {
    components:{
      ModalFormContactGestionnaire,
    },

    data(){
      return{
        buildedTitle:'',
        nomGestionnaire: '',
        idGestionnaire:'',
        idContact:'',
        nomContact:'',
        contacts: [],
        filteredContacts:[],

        fields: [
          { key:'contact.civilite', label:'Civilité'},
          { key:'contact.nom', label:'Nom', sortable: true},
          { key:'contact.prenom', label:'Prenom', sortable: true},
          { key:'contact.fonction', label:'Fonction', sortable: true},
          { key:'siteContactNom', label:'Site', sortable: true},
          { key:'contact.telephone', label:'Téléphone'},
          { key:'contact.email', label:'Email'},
          { key:'actions', label:'', tdClass:'buttonCell'}
        ],
        civilites: [
          { value: 'M', text: 'M'},
          { value: 'MMe', text: 'MME'}
        ],

        filterNomValue: '',
        filterPrenomValue: '',
        filterFonctionValue: '',
        filterSiteValue: '',

        perPage:14,
        currentPage:1,
      }
    },
    computed:{
      rows(){
        return this.filteredContacts.length
      }
    },

    methods: {
      getContactData(idGestionnaire_){
        axios.get('/remocra/gestionnaire/listeContactGestionnaireSite/'+idGestionnaire_).then(response => {
          this.contacts = this.filteredContacts = response.data;
        })
      },
      showModalListeContacts(idGestionnaire_, nomGestionnaire_) {
        this.nomGestionnaire=nomGestionnaire_
        this.idGestionnaire=idGestionnaire_
        this.buildedTitle="Liste des contacts de "+nomGestionnaire_
        this.getContactData(idGestionnaire_)
        this.$bvModal.show('modalContactGestionnaire')
      },
      openContactForm(idContact_){
        this.$refs.modalFormContactGestionnaire.showModalFormContactGestionnaire(idContact_, this.idGestionnaire, this.nomGestionnaire)
      },
      createContact(){
        this.idContact=null
        this.openContactForm(this.idContact)
      },
      directCreateContact(idGestionnaire_, nomGestionnaire_){
        this.nomGestionnaire=nomGestionnaire_
        this.idGestionnaire=idGestionnaire_
        this.idContact=null
        this.openContactForm(this.idContact)
      },
      editContact(idContact_){
        this.idContact=idContact_
        this.openContactForm(this.idContact)
      },
      deleteContact(idContact_){
        this.idContact=idContact_
        if(idContact_==null){
          return;
        }
        else{
          axios.get('/remocra/contact/contactInfos/'+this.idContact).then(response => {
            this.nomContact = response.data ? response.data.nom : ''
          })
          this.$bvModal.show('confirmDeleteContact')
        }
      },
      confirmClick(){
        axios.delete('/remocra/contact/deleteContact/'+this.idContact).then(()=>{
          this.$nextTick(()=>{
            this.onContactUpdated()
            this.$bvModal.hide('confirmDeleteContact')
          })
        })
      },
      onContactUpdated(){
        this.$emit('gestionnaireContactsUpdate')
        this.showModalListeContacts(this.idGestionnaire, this.nomGestionnaire)
      },
      filtering(){
        this.filterNomValue = document.getElementById("inputNomContact").value;
        this.filterPrenomValue = document.getElementById("inputPrenomContact").value;
        this.filterFonctionValue = document.getElementById("inputFonctionContact").value;
        this.filterSiteValue = document.getElementById("inputSiteContact").value;
        this.filteredContacts = this.contacts;

        this.filteredContacts = this.filteringPart(this.filteredContacts, 'contact', 'nom', this.filterNomValue) // Filtrage sur les noms
        this.filteredContacts = this.filteringPart(this.filteredContacts, 'contact', 'prenom', this.filterPrenomValue) // Filtrage sur les prenoms
        this.filteredContacts = this.filteringPart(this.filteredContacts, 'contact', 'fonction', this.filterFonctionValue) // Filtrage sur les fonctions
        this.filteredContacts = this.filteringPart(this.filteredContacts, 'siteContactNom', null, this.filterSiteValue) // Filtrage sur les sites
      },
      filteringPart(list_, champ1_, champ2_, valeurFiltre_){
        let temp = []
        if(valeurFiltre_!==''){
          _.forEach(list_, row => {
            if(champ2_){
              if(row[champ1_][champ2_] && row[champ1_][champ2_].toUpperCase().includes(valeurFiltre_.toUpperCase())){
                temp.push(row)
              }
            } else {
              if(row[champ1_] && row[champ1_].toUpperCase().includes(valeurFiltre_.toUpperCase())){
                temp.push(row)
              }
            }
          })
          return temp
        }
        return list_
      }
    }
  }
</script>

<style>
  div#modalListContact___BV_modal_content_{width:unset;min-width:unset !important;}
  #modalListContact .modal-dialog.modal-md{min-width: 920px;max-width:75%;}

  #tabContact{
    width: auto;
  }

  #divTableauContact{
    max-height: 500px !important;
    overflow-y:auto;
  }

  .table-bordered{
    height:unset !important;
  }
  .divTabContactGestionnaire{
    max-height:500px !important;
    overflow-y:auto;
  }

  thead, thead td{background-color: #afafaf;}
  #tabContactGestionnaire thead th{border: solid 1pt;border-bottom: none;}
  tr.b-table-top-row td{background-color: #afafaf;border: solid 1pt;border-top:none;}

  .civiliteCell{
    width:45px;
  }
  .buttonCell{
    width:141px;
    padding:0 !important;
  }
</style>