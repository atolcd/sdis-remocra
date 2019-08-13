<template>
<div>
  <b-modal id="modalOrganismeContact" title="Gestion des contacts" cancel-title="Annuler" ok-title="Valider" ref="modalOrganismeContact" @hidden="close()" @ok="handleOk">
    <form ref="formOrganisme" id="formOrganisme">
      <div class="row">
        <div class="col-md-6">
          <b-form-group label="Nom" label-cols-md="2" label-for="inputNom">
            <b-form-input id="inputNom" v-model="nomOrganisme">
            </b-form-input>
          </b-form-group>
        </div>
      </div>
      <b-form-group label="Contacts:" label-cols-md="2">
        <div class="row">
          <div class="col-md-12">
            <button class="btn btn-outline-primary" @click.prevent @click="createContact">Ajouter</button>
            <button class="btn btn-outline-info suppr" @click.prevent @click="modifContact">Modifier</button>
            <button class="btn btn-outline-danger suppr" @click.prevent @click="deleteContact">Supprimer</button>
          </div>
        </div>
      </b-form-group>
      <b-table striped hover selectable select-mode="single" @row-selected="rowSelected" selectedVariant="success" :items="contacts" :fields="fields">
      </b-table>
    </form>
    <form ref="formContacts" id='formContacts'>
      <div class="title">Contact</div>
      <div class="row">
        <div class="col-md-6">
          <b-form-group label="Contact associé à" label-for="appartenance" label-cols-md="5">
            <b-form-input id="appartenance" v-model="appartenance" type="text" size="sm" disabled></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-6 vertical-bottom">
          <b-form-group label="Fonction" label-for="fonction" invalid-feedback="La fonction doit être renseignée" label-cols-md="3">
            <b-form-input id="fonction" v-model="fonction" type="text" size="sm" required></b-form-input>
          </b-form-group>
        </div>
      </div>
      <div class="row">
        <div class="col-md-3">
          <b-form-group label="Civilité" label-for="civilite" label-cols-md="5">
            <b-form-select id="civilite" v-model="civilite" :options="civilites" size="sm" required></b-form-select>
          </b-form-group>
        </div>
        <div class="col-md-4 vertical-bottom">
          <b-form-group label="Nom" label-for="nom" invalid-feedback="Le nom doit être renseigné" label-cols-md="3">
            <b-form-input id="nom" v-model="nom" type="text" size="sm" required></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-5 vertical-bottom">
          <b-form-group label="Prénom" label-for="prenom" invalid-feedback="Le prénom doit être renseigné" label-cols-md="3">
            <b-form-input id="prenom" v-model="prenom" type="text" size="sm" required></b-form-input>
          </b-form-group>
        </div>
      </div>
      <div class="row">
        <div class="col-md-3">
          <b-form-group label="Numéro" label-for="numeroVoie" label-cols-md="6">
            <b-form-input id="numeroVoie" v-model="numeroVoie" type="number" size="sm"></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-3">
          <b-form-group label="Suffixe" label-for="suffixeVoie" label-cols-md="5">
            <b-form-input id="suffixeVoie" v-model="suffixeVoie" type="text" size="sm"></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-6">
          <b-form-group label="Voie" label-for="voie" invalid-feedback="La voie doit être renseignée" label-cols-md="2">
            <b-form-input id="voie" v-model="voie" size="sm" required></b-form-input>
          </b-form-group>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <b-form-group label="Lieu-dit" label-for="lieuDit" label-cols-md="1">
            <b-form-input id="lieuDit" v-model="lieuDit" type="text" size="sm"></b-form-input>
          </b-form-group>
        </div>
      </div>
      <div class="row">
        <div class="col-md-3">
          <b-form-group label="Code Postal" label-for="cp" invalid-feedback="Le code postal doit être renseigné" label-cols-md="7">
            <b-form-input id="cp" v-model="codePostal" size="sm" required></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-5">
          <b-form-group label="Ville" label-for="ville" invalid-feedback="La ville doit être renseignée" label-cols-md="2">
            <b-form-input id="ville" v-model="ville" size="sm" required></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-4">
          <b-form-group label="Pays" label-for="pays" invalid-feedback="Le pays doit être renseigné" label-cols-md="3">
            <b-form-input id="pays" v-model="pays" size="sm" required></b-form-input>
          </b-form-group>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6">
          <b-form-group label="Téléphone" label-for="telephone" label-cols-md="3">
            <b-form-input id="telephone" v-model="telephone" size="sm"></b-form-input>
          </b-form-group>
        </div>
        <div class="col-md-6">
          <b-form-group label="Email" label-for="mail" invalid-feedback="L'adresse mail doit être renseignée" label-cols-md="2">
            <b-form-input id="mail" v-model="email" size="sm" required></b-form-input>
          </b-form-group>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <b-form-group label="A contacter pour" label-for="contacter">
            <b-card>
              <b-form-checkbox-group v-model="contactRoles" :options="roles" stacked></b-form-checkbox-group>
            </b-card>
          </b-form-group>
        </div>
      </div>
      <b-form-group>
        <div class="btnContact">
          <b-button variant="primary saveBtn" @click="addContact">Enregistrer</b-button>
          <b-button variant="secondary" @click="toggleForm">Annuler</b-button>
        </div>
      </b-form-group>
    </form>
  </b-modal>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'modalOrganismeContact',
  components: {},
  data() {
    return {
      context: "CREATION",
      indexOfSelected: 0,
      selected: null,
      idContact: null,
      idOrganisme: null,
      nomOrganisme: '',
      roles: [],
      contactRoles: [],
      appartenance: '',
      fonction: '',
      civilite: 'M',
      nom: '',
      prenom: '',
      numeroVoie: '',
      suffixeVoie: '',
      voie: '',
      lieuDit: '',
      pays: '',
      codePostal: '',
      ville: '',
      telephone: '',
      email: '',
      Organisme: {},
      showContact: false,
      contacts: [],
      fields: [{
        key: 'civilite',
        label: 'Civilité'
      }, {
        key: 'nom',
        label: 'Nom'
      }, {
        key: 'prenom',
        label: 'Prenom'
      }, {
        key: 'fonction',
        label: 'Fonction'
      }, {
        key: 'telephone',
        label: 'Téléphone'
      }, {
        key: 'email',
        label: 'Email'
      }, {
        key: 'actions',
        label: ''
      }],
      civilites: [{
        value: 'M',
        text: 'M'
      }, {
        value: 'MMe',
        text: 'MME'
      }],
      currentPage: 1,
      perPage: 2,
      totalRows: 1
    }
  },
  mounted: function() {
    //la taille du tableau
    this.totalRows = 2
  },
  methods: {
    editOrganisme(id, nom) {
      if (id != null) {
        //modification
        this.idOrganisme = id;
        this.nomOrganisme = nom;
        axios.get("/remocra/contact/" + id).then(response => {
          //La liste des roles corresponds aux ids
          this.contacts = response.data.data
          _.forEach(this.contacts, contact => {
            contact.index = this.indexOfSelected;
            this.indexOfSelected = this.indexOfSelected + 1;
            var rolesId = _.map(contact.roles, r => r.id);
            contact.roles = rolesId
          })
          this.$refs.modalOrganismeContact.show()
        }).catch(function(error) {
          console.error('contact', error);
        })
      } else {
        //creation
        this.resetFormOrganisme();
        this.$refs.modalOrganismeContact.show()
      }
      this.roles = []
      axios.get('/remocra/roles').then(response => {
        _.forEach(response.data.data, role => {
          this.roles.push({
            text: role.nom,
            value: role.id
          })
        })
      }).catch(function(error) {
        console.error('roles', error);
      })
    },
    checkFormValidity() {
      const valid = this.$refs.formOrganisme.checkValidity();
      return valid
    },
    resetFormOrganisme() {
      this.resetFormContact()
      this.$refs.formOrganisme.reset();
      this.$refs.formContacts.reset();
      this.idOrganisme = null;
      this.nomOrganisme = '';
      this.contacts = [];
      this.selected = null;
      document.getElementById('formOrganisme').classList.remove('hide')
      document.getElementById('formOrganisme').classList.remove('was-validated')
      document.getElementById('formContacts').classList.remove('animate')
    },
    resetFormContact() {
      this.$refs.formContacts.reset();
      document.getElementById('formContacts').classList.remove('was-validated')
      this.idContact = null
      this.appartenance = ''
      this.fonction = ''
      this.civilite = 'M'
      this.nom = ''
      this.prenom = ''
      this.numeroVoie = ''
      this.suffixeVoie = ''
      this.voie = ''
      this.lieuDit = ''
      this.pays = ''
      this.codePostal = ''
      this.ville = ''
      this.telephone = ''
      this.email = ''
      this.contactRoles = []
      this.index = null
    },
    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault()
      if (document.getElementById('formOrganisme').checkValidity() === false) {
        document.getElementById('formOrganisme').classList.add('was-validated')
      } else {
        this.handleSubmit()
      }
    },
    handleSubmit() {
      if (!this.checkFormValidity()) {
        return;
      }
      var formData = new FormData();
      formData.append('contacts', JSON.stringify(this.contacts))
      axios.post('/remocra/organismes/contacts/' + this.idOrganisme, formData).then(() => {
        this.$nextTick(() => { //Fermeture manuelle de la modale
          this.resetFormOrganisme();
          this.resetFormContact();
          this.$refs.modalOrganismeContact.hide()
        })
      }).catch(function(error) {
        console.error('contact', error);
      });
    },
    addContact() {
      //Si le contact existe on le supprime du tableau et on le remplace
      if (document.getElementById('formContacts').checkValidity() === false) {
        document.getElementById('formContacts').classList.add('was-validated')
      } else {
        if (this.context === "MODIFICATION") {
          _.forEach(this.contacts, contact => {
            if (contact.index === this.index) {
              contact.id = this.idContact != null ? this.idContact : null,
                contact.appartenance = 'ORGANISME',
                contact.id_appartenance = '1',
                contact.numeroVoie = this.numeroVoie,
                contact.suffixeVoie = this.suffixeVoie,
                contact.lieuDit = this.lieuDit,
                contact.codePostal = this.codePostal,
                contact.ville = this.ville,
                contact.voie = this.voie,
                contact.pays = this.pays,
                contact.civilite = this.civilite,
                contact.nom = this.nom,
                contact.prenom = this.prenom,
                contact.fonction = this.fonction,
                contact.telephone = this.telephone,
                contact.email = this.email,
                contact.roles = this.contactRoles
              contact.index = this.index
            }
          })
        } else {
          this.contacts.push({
            index: this.index,
            id: this.idContact != null ? this.idContact : null,
            appartenance: 'ORGANISME',
            id_appartenance: '1',
            numeroVoie: this.numeroVoie,
            suffixeVoie: this.suffixeVoie,
            lieuDit: this.lieuDit,
            codePostal: this.codePostal,
            ville: this.ville,
            voie: this.voie,
            pays: this.pays,
            civilite: this.civilite,
            nom: this.nom,
            prenom: this.prenom,
            fonction: this.fonction,
            telephone: this.telephone,
            email: this.email,
            roles: this.contactRoles
          })
        }
        this.toggleForm();
        this.resetFormContact();
      }
    },
    deleteContact() {
      this.contacts = _.remove(this.contacts, o => o.id != this.selected[0].id)
    },
    modifContact() {
      this.context = "MODIFICATION"
      this.appartenance = this.nomOrganisme
      this.index = this.selected[0].index
      this.idContact = this.selected[0].id
      this.suffixeVoie = this.selected[0].suffixeVoie
      this.voie = this.selected[0].voie
      this.lieuDit = this.selected[0].lieuDit
      this.codePostal = this.selected[0].codePostal
      this.ville = this.selected[0].ville
      this.pays = this.selected[0].pays
      this.civilite = this.selected[0].civilite
      this.nom = this.selected[0].nom
      this.prenom = this.selected[0].prenom
      this.fonction = this.selected[0].fonction
      this.telephone = this.selected[0].telephone
      this.email = this.selected[0].email
      _.forEach(this.selected[0].roles, role => {
        this.contactRoles.push(role)
      })
      this.toggleForm()
    },
    createContact() {
      this.context = "CREATION"
      this.index = this.indexOfSelected + 1;
      this.resetFormContact();
      this.appartenance = this.nomOrganisme
      this.toggleForm()
    },
    toggleForm() {
      document.getElementById('formOrganisme').classList.toggle('hide')
      document.getElementById('formContacts').classList.toggle('animate')
    },
    rowSelected(item) {
      this.selected = item
    },
    close() {
      this.$root.$options.bus.$emit('closed')
    }
  }
};
</script>

<style>
#modalOrganismeContact .modal-content {
  width: max-content;
  left: 50%;
  transform: translate(-50%);
  background-color: #e9e9e9 !important;
}

#modalOrganismeContact .modal-backdrop {
  opacity: 0;
}

#modalOrganismeContact .modal-title {
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif, arial, verdana;
}

#modalOrganismeContact .modal-backdrop.show {
  opacity: 0;
}

#formOrganisme {
  width: 700px;
  display: block;
}

#formOrganisme.hide {
  display: none;
}

#formContacts {
  width: 500px;
  display: none;
  width: auto;
  padding: 10px;
  background: rgba(0, 0, 0, .05);
}

#formContacts.animate {
  display: block;
  right: 0;
}

.btnContact {
  float: right;
  padding: 10px;
}

.saveBtn {
  margin-right: 10px;
}

.modif {
  margin-left: 30px;
}

.suppr {
  margin-left: 30px;
}
</style>
