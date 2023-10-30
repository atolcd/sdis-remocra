<!-- Balise meta pour spécifier l'encodage des caractères -->
<head>
<meta charset="UTF-8">
</head>
<template>
  <div id="caracteristiqueMobile" class="container">
    <div class="row">
      <div class="col-md-12">
        <h1 class="title">Gestion des caractéristiques des PEI affichées dans l'appli mobile</h1>
      </div>
    </div>
    <div class="row">
      <p class="paragraphe col-12">
        Permet de gérer les informations qui seront affichées dans l'infobulle des PEI de l'application mobile.
        Chaque type de PEI (PIBI, PENA) dispose de sa propre liste car les données affichables ne sont pas toutes les mêmes.
        Pour chaque type, en partie gauche se trouve la liste des caractéristiques affichables, que vous pouvez placer en partie droite pour les afficher.
        De plus, vous pouvez trier les données affichées dans l'ordre qui vous sied
      </p>
    </div>
    <div class="row mt-3">
      <!--      PIBI-->
      <h2 class="col-12 m-2 ">PIBI</h2>
      <div class="col-3">
        <!--        Select des Non Choisi-->
        <!--        Id qui sert pour savoir quel "select" est cliqué-->
        <label class="col-12" for="PIBINC" >Valeurs affichables</label>
        <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPibiNonChoisie" id="PIBINC"/>
      </div>
      <div class="col-2 text-center my-auto">
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

            <button class="btn btn-danger col-6 p-1 m-2" @click="switchList(champPibiChoisie,champPibiNonChoisie)">
              Supprimer
            </button>

            <button @click="moveInListe(champPibiChoisie, DOWN)" class=" btn col-lg-2 col-5 p-0">
              <img src="/remocra/static/img/arrow-down.png" alt="" class="w-100 m-0 p-0">
            </button>

          </div>
        </div>
      </div>
      <div class="col-3">
        <!--        Champs Choisi-->
        <label class="col-12" for="PIBIC">Valeurs affichées</label>
        <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPibiChoisie" id="PIBIC" libelle="Valeurs affichées"/>
      </div>
    </div>


    <div class="row  mt-3">
      <!--      PENA-->
      <h2 class="col-12">PENA</h2>
      <div class="col-3">
        <!--        Select des Non Choisi-->
        <!--        Id qui sert pour savoir quel "select" est cliqué-->
        <label class="col-12" for="PENANC">Valeurs affichables</label>
        <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPenaNonChoisie" id="PENANC"
                              libelle="Valeurs affichables"/>
      </div>
      <div class="col-2 text-center my-auto">
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

            <button class="btn btn-danger col-6 p-1 m-2" @click="switchList(champPenaChoisie,champPenaNonChoisie)">
              Supprimer
            </button>

            <button @click="moveInListe(champPenaChoisie, DOWN)" class=" btn col-lg-2 col-5 p-0">
              <img src="/remocra/static/img/arrow-down.png" alt="" class="w-100 m-0 p-0">
            </button>

          </div>
        </div>
      </div>
      <div class="col-3">
        <!--        Champs Choisi-->
        <label class="col-12" for="PENAC">Valeurs affichées</label>
        <ListeCaracteristique v-on:rowChange="onChangeRow" :datas="champPenaChoisie" id="PENAC" libelle="Valeurs affichées"/>
      </div>
    </div>
    <div class="row  mt-3 justify-content-end d-flex col-8">
      <button class="btn btn-primary p-2" @click="valideForm()"> Valider</button>
    </div>
    <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000"/>
  </div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import ListeCaracteristique from "./InfoPeiMobile/ListeCaracteristique.vue";

const PIBI = "PIBI";
const PENA = "PENA";
const NON_CHOISIE = "NC";
export default {

  name: 'caracteristiqueMobile',
  components: {
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
      "champPibiNonChoisie": null

    }
  },
  mounted: function () {
    this.loadData();
  },
  methods: {
    getCaracteristiqueNonChoisies(type) {
      axios.get('/remocra/parametre/caracteristiques/nonChoisie/' + type)
          .then((response) => {
            if (type === PIBI) {
              this.champPibiNonChoisie = response.data;
            } else if (type === PENA) {
              this.champPenaNonChoisie = response.data;
            }else{
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
    }, valideForm() {
      /**
       * Réordonne les item pour assurer l'ordre en back
       */
      let dataToUpdate = new FormData();
      this.champPibiChoisie.forEach((element, index) => {
        element.ordre = index;
      });
      this.champPibiChoisie.forEach((element, index) => {
        element.ordre = index;
      })
      dataToUpdate.append("pibi", JSON.stringify(this.champPibiChoisie))
      dataToUpdate.append("pena", JSON.stringify(this.champPenaChoisie))
      axios.post('/remocra/parametre/caracteristiques/update/', dataToUpdate)
          .then(() => {
            this.$notify({
              group: 'remocra',
              type: 'success',
              title: 'Succès',
              text: 'Les listes ont bien été mises à jour'
            });
          }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Erreur',
          text: 'Une erreur s\'est produite lors de la mise à jour.'
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