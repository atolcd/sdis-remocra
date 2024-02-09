<!-- Balise meta pour spécifier l'encodage des caractères -->
<head>
<meta charset="UTF-8">
</head>
<template>

  <!--  Gestion des caractéristiques des PEI affichées dans l'appli mobile-->

  <div id="caracteristiqueMobile" class="container">
    <fieldset class="col-12 border border-1 m-2">
      <div class="row">
        <div class="col-md-12">
          <h1 class="title">Gestion des caractéristiques des PEI affichées dans l'appli mobile</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-8">
          <div class="row mt-3">
            <!--      PIBI-->
            <h2 class="col-12 m-2 ">PIBI</h2>
            <div class="col-4">
              <!--        Select des Non Choisi-->
              <!--        Id qui sert pour savoir quel "select" est cliqué-->
              <label class="col-12" for="PIBINC">Valeurs affichables</label>
              <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPibiNonChoisie" id="PIBINC"/>
            </div>
            <div class="col-3 text-center my-auto">
              <div v-if="isPibi">
                <!--          LE BOUTON AJOUTER PIBI-->
                <div v-if="isNc">
                  <button class="btn btn-success col-6 p-1" v-if="isNc"
                          @click="switchList(champPibiNonChoisie, champPibiChoisie)">Ajouter
                  </button>
                </div>
                <!--LES BOUTONS MONTER SUPPRIMER DESCENDRE PIBI-->
                <div v-else class="d-flex flex-column justify-content-center align-items-center">

                  <button @click="moveInListe(champPibiChoisie, UP)" class=" btn col-lg-2 col-5 p-0">
                    <img src="/remocra/static/img/arrow-up.png" alt="" class="w-100 m-0 p-0">
                  </button>

                  <button class="btn btn-danger col-6 p-1 m-2"
                          @click="switchList(champPibiChoisie,champPibiNonChoisie)">
                    Supprimer
                  </button>

                  <button @click="moveInListe(champPibiChoisie, DOWN)" class=" btn col-lg-2 col-5 p-0">
                    <img src="/remocra/static/img/arrow-down.png" alt="" class="w-100 m-0 p-0">
                  </button>

                </div>
              </div>
            </div>
            <div class="col-4">
              <!--        Champs Choisi-->
              <label class="col-12" for="PIBIC">Valeurs affichées</label>
              <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPibiChoisie" id="PIBIC"
                                    libelle="Valeurs affichées"/>
            </div>
          </div>


          <div class="row  mt-3">
            <!--      PENA-->
            <h2 class="col-12">PENA</h2>
            <div class="col-4">
              <!--        Select des Non Choisi-->
              <!--        Id qui sert pour savoir quel "select" est cliqué-->
              <label class="col-12" for="PENANC">Valeurs affichables</label>
              <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPenaNonChoisie" id="PENANC"
                                    libelle="Valeurs affichables"/>
            </div>
            <div class="col-3 text-center my-auto">
              <div v-if="isPena">
                <!--          LE BOUTON AJOUTER PIBI-->
                <div v-if="isNc">
                  <button class="btn btn-success col-6 p-1" v-if="isNc"
                          @click="switchList(champPenaNonChoisie, champPenaChoisie)">Ajouter
                  </button>
                </div>
                <!--LES BOUTONS MONTER SUPPRIMER DESCENDRE PIBI-->
                <div v-else class="d-flex flex-column justify-content-center align-items-center">

                  <button @click="moveInListe(champPenaChoisie, UP)" class=" btn col-lg-2 col-5 p-0">
                    <img src="/remocra/static/img/arrow-up.png" alt="" class="w-100 m-0 p-0">
                  </button>

                  <button class="btn btn-danger col-6 p-1 m-2"
                          @click="switchList(champPenaChoisie,champPenaNonChoisie)">
                    Supprimer
                  </button>

                  <button @click="moveInListe(champPenaChoisie, DOWN)" class=" btn col-lg-2 col-5 p-0">
                    <img src="/remocra/static/img/arrow-down.png" alt="" class="w-100 m-0 p-0">
                  </button>

                </div>
              </div>
            </div>
            <div class="col-4">
              <!--        Champs Choisi-->
              <label class="col-12" for="PENAC">Valeurs affichées</label>
              <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPenaChoisie" id="PENAC"
                                    libelle="Valeurs affichées"/>
            </div>
          </div>
        </div>
        <p class="col-4 my-auto">
          Permet de gérer les informations qui seront affichées dans l'infobulle des PEI de l'application mobile.
          Chaque type de PEI (PIBI, PENA) dispose de sa propre liste car les données affichables ne sont pas toutes les
          mêmes.
          Pour chaque type, en partie gauche se trouve la liste des caractéristiques affichables, que vous pouvez placer
          en partie droite pour les afficher.
          De plus, vous pouvez trier les données affichées dans l'ordre qui vous sied
        </p>
      </div>
    </fieldset>

    <!--    GESTION DES AGENTS-->


    <fieldset class="col-12 border border-1 m-2">
      <div class="row">
        <div class="col-md-12">
          <h1 class="title">Composant Agent</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <label class="col-12" for="paramAgent">Type d'agent séléctionné</label>
          <ParametreAgent :options="agentsSelectable" :selected="agentsSelected"
                          v-on:rowChange="agentChange" id="paramAgent"></ParametreAgent>
        </div>
        <p class="col-6 my-auto">
          Ce paramètre permet de définir le comportement des composants "Agent 1" et "Agent 2" présents sur le premier
          onglet de la visite d'un point d'eau sur l'application mobile. Ces deux derniers s'appuient sur ce qu'on
          appelera le "Composant agent" qui sera utile ou non selon les cas, mais s'il est utilisé, il fonctionnera
          comme décrit dans la fenêtre suivante :
          <!-- Using value -->
          <b-button v-b-modal="'my-modal'" variant="info">Plus d'info</b-button>

        </p>


      </div>
    </fieldset>


    <fieldset class="col-12 border border-1 m-2">
      <div class="row">
        <div class="col-md-12">
          <h1 class="title">Affichage hydrants</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <b-form-group label="Afficher l'état de disponibilité">

            <b-form-radio v-model="affichageIndispo" name="affichageIndispo" value="true" class="d-inline-block col-2"
                          size="lg">Oui
            </b-form-radio>
            <b-form-radio v-model="affichageIndispo" name="affichageIndispo" value="false" class="d-inline-block col-2"
                          size="lg">Non
            </b-form-radio>
          </b-form-group>
        </div>
        <p class="col-6 my-auto">
          Ajout d'une croix rouge sur le symbole de l'hydrant pour signifier l'indisponibilité
        </p>

      </div>
    </fieldset>

    <fieldset class="col-12 border border-1 m-2">
      <div class="row">
        <div class="col-md-12">
          <h1 class="title">Mot de passe admin</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <label for="passAdmin">Mot de passe</label>
          <b-form-input id='passAdmin' type="password" v-model="passwordAdmin"
                        placeholder="Mot de passe admin"></b-form-input>
        </div>
        <p class="col-6 my-auto">
          Une fois l'adresse du serveur renseignée et validée, pour accéder à la partie "Administrer" on donne la
          possibilité de mettre un mot de passe dit "Admin".
          Cela restreint l'accès à la partie administrer et donc limite la possibilité de modifier le nom du serveur.
        </p>

      </div>
    </fieldset>

    <fieldset class="col-12 border border-1 m-2">
      <div class="row">
        <div class="col-md-12">
          <h1 class="title">Durée de validité du token</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <label for="passAdmin">Durée</label>
          <b-form-input id='passAdmin' type="number" v-model="validiteToken"></b-form-input>
        </div>
        <p class="col-6 my-auto">
          Durée (en heures) de validité du jeton de connexion à l'application mobile.
        </p>

      </div>
    </fieldset>


    <div class="row  mt-3 justify-content-end d-flex col-8">
      <button class="btn btn-primary p-2" @click="valideForm()"> Valider</button>
    </div>
    <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000"/>

    <!--Modal explication param agent-->
    <div>

      <!-- The modal -->
      <b-modal id="my-modal" size="xl">
        <p class="m-2">C'est un champ texte mixé avec une liste déroulante, comme par exemple les listes déroulantes de
          l'accès rapide du module point d'eau (application web remocra).
          Par défaut il est vide.
          L'utilisateur saisit ce qu'il veut ( Michel Dupont par exemple, ou toto@sdisXX.fr ), on va stocker cette
          valeur dans les propriétés de l'application (commun à tous les utilisateurs de cette tablette)
          A la prochaine saisie, l'utilisateur peut soit rajouter un autre agent, soit retrouver les valeurs
          précédemment saisies.
          La liste des agents disponibles pour le composant n'est pas envoyée au serveur, c'est un facilitateur de
          saisie ; la tablette enverra le résultat de la saisie pour chaque PEI.
          <br>
          Pour l'instant les scénarios suivants ont été identifiés</p>
        <div class="h5">Cas 1 - Utilisateur connecté obligatoire</div>
        <p class="m-2">L'agent 1 est toujours l'utilisateur connecté, on préremplit donc le champ avec ses informations.
          On rend le champ inaccessible pour éviter une modification manuelle</p>
        <p class="m-2">L'agent 2 utilise le composant Agent</p>

        <div class="h5">Cas 2 - Utilisateur connecté</div>
        <p class="m-2">L'agent 1 est toujours l'utilisateur connecté, on préremplit donc le champ avec ses informations.
          On laisse possible la saisie manuelle</p>
        <p class="m-2">L'agent 2 utilise le composant Agent</p>

        <div class="h5">Cas 3 - Liste des agents</div>
        <p class="m-2">Les 2 champs de formulaire sont vides par défaut, et on propose l'utilisation du composant Agent
          pour les 2</p>

        <div class="h5">Cas 4 - Valeur précédente</div>
        <p class="m-2">Idem cas 2, mais les valeurs par défaut correspondent à la valeur précédemment sélectionnée</p>
      </b-modal>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import ListeCaracteristique from "./InfoPeiMobile/ListeCaracteristique.vue";
import ParametreAgent from "./Parametre/ParametreAgents.vue";

const PIBI = "PIBI";
const PENA = "PENA";
const NON_CHOISIE = "NC";
const typeParametre = {
  MDP_ADMINISTRATEUR: 'MDP_ADMINISTRATEUR',
  AFFICHAGE_INDISPO: 'AFFICHAGE_INDISPO',
  AGENT: 'AGENT',
  CARACTERISTIQUE: 'CARACTERISTIQUE',
  DUREE_VALIDITE_TOKEN: 'DUREE_VALIDITE_TOKEN'

};
export default {

  name: 'caracteristiqueMobile',
  components: {
    ParametreAgent,
    ListeCaracteristique
  },
  data() {
    return {
      "selectedRow": null,
      "UP": true,
      "DOWN": false,
      "selectedWindows": null,
      "isPibi": false,
      "isPena": false,
      "isNc": false,
      "champPenaChoisie": null,
      "champPibiChoisie": null,
      "champPenaNonChoisie": null,
      "champPibiNonChoisie": null,
      "agentsSelected": null,
      "agentsSelectable": [],
      "affichageIndispo": null,
      "validiteToken": null,
      "passwordAdmin": ''

    }
  },
  mounted: function () {
    this.loadData();
  },
  methods: {
    getValiditeToken() {
      axios.get('/remocra/parametre/validiteToken')
          .then((response) => {
            this.validiteToken = response.data;

          }).catch(
          () => {

            this.$notify({
              group: 'remocra',
              type: 'error',
              title: 'Erreur',
              text: 'La durée de validité du token n\'a pas pu être récupérée correctement'
            });
          }
      )
    },
    getAffichageIndispo() {
      axios.get('/remocra/parametre/affichageIndispo')
          .then((response) => {
            this.affichageIndispo = response.data;

          }).catch(
          () => {

            this.$notify({
              group: 'remocra',
              type: 'error',
              title: 'Erreur',
              text: 'Le paramaètre AFFICHAGE_INDISPO n\'a pas pu être récupéré correctement'
            });
          }
      )
    },
    getPasswordAdmin() {
      axios.get('/remocra/parametre/passwordAdmin')
          .then((response) => {
            this.passwordAdmin = response.data;

          }).catch(
          () => {

            this.$notify({
              group: 'remocra',
              type: 'error',
              title: 'Erreur',
              text: 'Le mot de passe admin pas n\'a pu être récupéré correctement'
            });
          }
      )
    },
    getCaracteristiqueNonChoisies(type) {
      axios.get('/remocra/parametre/caracteristiques/nonChoisie/' + type)
          .then((response) => {
            if (type === PIBI) {
              this.champPibiNonChoisie = response.data;
            } else if (type === PENA) {
              this.champPenaNonChoisie = response.data;
            } else {
              this.$notify({
                group: 'remocra',
                type: 'error',
                title: 'Erreur',
                text: 'Le type ' + type + ' n\'est pas connu.'
              });
            }
          })
    },
    getCaracteritiquesChoisies(type) {
      axios.get('/remocra/parametre/caracteristiques/choisie/' + type).then((response) => {
        if (type === PIBI) {
          this.champPibiChoisie = _.orderBy(response.data, 'ordre', 'asc');
        } else if (type === PENA) {
          this.champPenaChoisie = _.orderBy(response.data, 'ordre', 'asc');
        } else {
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur',
            text: 'Le type ' + type + ' n\'est pas connu'
          });
        }
      }).catch(error => {
        return error;
      })
    },

    loadData() {

      this.getCaracteritiquesChoisies(PENA);
      this.getCaracteritiquesChoisies(PIBI);
      this.getCaracteristiqueNonChoisies(PENA);
      this.getCaracteristiqueNonChoisies(PIBI);
      this.getAffichageIndispo();
      this.getPasswordAdmin();
      this.getValiditeToken();

    },
    onChangeRow(valueSelected, idSelected) {
      this.selectedRow = valueSelected
      this.selectedWindows = idSelected
      this.isPibi = idSelected.includes(PIBI);
      this.isPena = idSelected.includes(PENA);
      this.isNc = idSelected.includes(NON_CHOISIE);
    },
    /**
     * Ajoute le champ sélectionné dans le select de gauche a la liste des champsChoisie
     *
     */
    switchList(_listeDelete, _listeAdd) {
      if (this.selectedRow !== null) {

        _listeDelete.splice(_listeDelete.indexOf(this.selectedRow), 1)
        _listeAdd.push(this.selectedRow)
        this.selectedRow = null;
      }
    }, moveInListe(_liste, _up) {

      let indexDeBase = _liste.indexOf(this.selectedRow);
      let indexCible;
      if (_up) {
        indexCible = indexDeBase - 1;
      } else {
        indexCible = indexDeBase + 1;
      }

      //Si tout en haut on monte pas plus (sans ça je ne sais pour quel raison mais de index 0
      // on passait a index "max-1"
      if (indexCible >= 0) {
        _liste.splice(_liste.indexOf(this.selectedRow), 1);
        _liste.splice(indexCible, 0, this.selectedRow);
      }
    },
    agentChange(value) {
      this.agentsSelected = value;
      console.log(this.agentsSelected)
    },

    valideForm() {

      for (let typeParametreKey in typeParametre) {
        this.updateParam(typeParametreKey);
      }

    },
    updateParam(param) {
      let url, textSucces, textError;
      let dataToUpdate;
      /**
       * Réordonne les items des caractéristiques pour assurer l'ordre en back
       */
      dataToUpdate = new FormData();
      this.champPibiChoisie.forEach((element, index) => {
        element.ordre = index;
      });
      this.champPenaChoisie.forEach((element, index) => {
        element.ordre = index;
      });

      dataToUpdate.append("pibi", JSON.stringify(this.champPibiChoisie))
      dataToUpdate.append("pena", JSON.stringify(this.champPenaChoisie))
      dataToUpdate.append("agent", JSON.stringify(this.agentsSelected))
      dataToUpdate.append("affichageIndispo", JSON.stringify(this.affichageIndispo))
      dataToUpdate.append("passwordAdmin", JSON.stringify(this.passwordAdmin))
      dataToUpdate.append("validiteToken", JSON.stringify(this.validiteToken))

      switch (param) {
        case typeParametre.MDP_ADMINISTRATEUR :
          url = "/remocra/parametre/passwordAdmin/update/";
          textSucces = 'Le mot de passe administrateur a bien été mis à jour';
          textError = 'Le mot de passe administrateur n\'a pas été mis à jour';
          break;
        case typeParametre.AFFICHAGE_INDISPO :
          url = "/remocra/parametre/affichageIndispo/update/";
          textSucces = 'Le paramètre d\'indispo a bien été mis à jour';
          textError = 'Le paramètre d\'indispo n\'a pas été mis à jour';
          break;
        case typeParametre.AGENT :
          url = "/remocra/parametre/agents/update/";
          textSucces = 'L\'agent a bien été mis à jour';
          textError = 'L\'agent n\'a pas été mis à jour';
          break;
        case typeParametre.CARACTERISTIQUE :
          url = "/remocra/parametre/caracteristiques/update/";
          textSucces = 'Les listes des caractéristiques ont bien été mise à jour';
          textError = 'Les listes des caractéristiques n\'ont pas pu être mise à jour';
          break;
        case typeParametre.DUREE_VALIDITE_TOKEN :
          url = "/remocra/parametre/validiteToken/update/";
          textSucces = 'La durée du token a bien été mise à jour';
          textError = 'La durée du token n\'a pas pu être mise à jour';
          break;

      }

      axios.post(url, dataToUpdate)
          .then(() => {
            this.$notify({
              group: 'remocra',
              type: 'success',
              title: 'Succès',
              text: textSucces
            });
          }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Erreur',
          text: textError
        });
      });
    }
  },

};


</script>

<style scoped>
h1.title {
  font-size: 1.5rem !important;
  margin-bottom: 1rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial, serif !important;
}

</style>