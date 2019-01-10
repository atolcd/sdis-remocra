<template>
<div>
  <b-modal id="modalProcess" ref="modal" title="Processus Etl" ok-title="Valider" cancel-title="Annuler" @ok="handleOk" @hidden="clearFields">
    <b-form-group horizontal label="Choix du Processus:" label-for="process">
      <b-form-select id="process" v-model="selected" :options="options" class="mb-3" @input="getParams" />
    </b-form-group>
     <p v-if="selected">{{selected.description}}</p>
      <hr/>
     <p v-if="params.length > 0">Veuillez renseigner les paramètres suivants :</p>
     <p v-else-if="selected">Aucun paramètre pour cette requête</p>
    <form id="formProcess" class="needs-validation" @submit.stop.prevent="handleSubmit" >
      <div v-for="(param, index) in params" :key="index">
        <b-form-group v-if='param.formulaireTypeControle=="autocomplete"' :id="'input'+param.id" inputType='autocomplete' :required = "param.obligatoire" class="parametreProcess" horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <search-process-param :ref="'searchinput'+param.id" :paramId="param.id"></search-process-param>
        </b-form-group>

        <b-form-group v-if='param.formulaireTypeControle=="combo"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <select :id="'input'+param.id" :required='param.obligatoire' class="form-control parametreProcess">
              <option v-for="(value, key) in comboOptions" :key="key" v-if="value.nomChamp == param.id" :value='value.valeur' :selected='value.valeur==value.formulaireValeurDefaut'>
                 {{value.libelle}}
               </option>
            </select>
        </b-form-group>

        <b-form-group v-if='param.formulaireTypeControle=="checkbox"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <input type='checkbox' style="width:5%" :id="'input'+param.id" :checked="param.formulaireValeurDefaut" inputType='checkbox' class="form-control parametreProcess" />
       </b-form-group>

       <b-form-group v-if='param.formulaireTypeControle=="textfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
            <input type='text' :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire'  class="form-control parametreProcess"/>
       </b-form-group>

            <b-form-group v-if='param.formulaireTypeControle=="numberfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <input type="number" :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' :step='(param.typeValeur=="integer")?1:0.001' class="form-control parametreProcess" />
            </b-form-group>

              <b-form-group v-if='param.formulaireTypeControle=="datefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
                <input type='date' :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreProcess" />
             </b-form-group>

                <b-form-group v-if='param.formulaireTypeControle=="timefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
                  <input type='time' :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' step='1' class="form-control parametreProcess" />
               </b-form-group>

                  <b-form-group v-if='param.formulaireTypeControle=="datetimefield"' :id="'input'+param.id" horizontal :label='param.formulaireEtiquette' inputType='datetimefield' class='parametreProcess' :label-for="'input'+param.id">
                    <input type='date' :id="'input'+param.id+'date'"  :value='param.formulaireValeurDefaut && param.formulaireValeurDefaut.split(" ")[0]' :required='param.obligatoire' class='form-control' />
                    <input type='time' :id="'input'+param.id+'time'"  :value='param.formulaireValeurDefaut && param.formulaireValeurDefaut.split(" ")[1]' :required='param.obligatoire' step='1' class='form-control' />
       </b-form-group>
                    <b-form-group v-if='param.formulaireTypeControle=="filefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id" >
                      <div class="custom-file b-form-file ">
                        <input :id="'input'+param.id" type="file" class="custom-file-input parametreProcess" :required="param.obligatoire" inputType='filefield' @change="handleChangeFile($event, param.id)">
                        <label class="custom-file-label"/>
                    <label class="custom-file-label" v-for="(file,key) in files" :key="key" v-if="file.id == param.id">{{file.file.name}}</label>
                      </div>
                    </b-form-group>
      </div>
    </form>
  </b-modal>
</div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import moment from 'moment'
import EventBus from '../bus'
import * as eventTypes from '../bus/event-types.js'
import SearchProcessParam from './SearchProcessParam.vue'
import _ from 'lodash'
export default {
  name: 'Process',
  components: {
    SearchProcessParam
  },
  data() {
    return {
      selected: null,
      options: [],
      params: [],
      comboOptions: [],
      files: [],
      valid: true
    }
  },
  methods: {
    showModal() {
      var jsonFilters = JSON.stringify([{
        property: 'categorie',
        value: 'GESTION_CRISE'
      }])
      axios.get('/remocra/processusetlmodele', {
          params: {
            filter: jsonFilters
          }
        })
        .then((response) => {
          _.forEach(response.data.data, item => {
            this.options.push({
              'value': {'id' : item.id, 'description' : item.description},
              'text': item.code
            })
          })
        })
        .catch(function(error) {
          console.error('message', error)
        })
      this.$refs.modal.show()
    },
    clearFields() {
      this.selected =  null
      this.options =  []
      this.params = []
      this.comboOptions = []
      this.files= []
      document.getElementById('formProcess').classList.remove('was-validated');
    },
    handleOk(evt) {
      evt.preventDefault()
      if (document.getElementById('formProcess').checkValidity() === false) {
        alert('Veuillez saisir les champs obligatoires')
        document.getElementById('formProcess').classList.add('was-validated');
      } else {
          let formData = new FormData()
          formData.append('processus', this.selected.id)
          formData.append('priorite', 3)
          _.forEach(evt.target.getElementsByClassName("parametreProcess"), item => {
            if (item.getAttribute("inputType") == 'datetimefield') {
              var date = document.querySelector("input[id=" + item.id + "date ]").value
              var time =  document.querySelector("input[id=" + item.id + "time]").value
              formData.append(item.id, date + " " + time)
            } else if (item.getAttribute("inputType") === 'autocomplete') {
              var autocomplete = this.$refs['search' + item.id][0]
              formData.append(item.getAttribute("id"), autocomplete.selected !== null ? autocomplete.selected.id : autocomplete.searchInput)
            } else if (item.getAttribute("inputType") === 'filefield') {
              var rawValueParts = item.value.split('\\');
              var value = rawValueParts[rawValueParts.length - 1];
              formData.append(item.getAttribute("id"), value)
            } else if (item.getAttribute("inputType") === 'checkbox') {
              formData.append(item.getAttribute("id"), item.checked)
            } else {
              formData.append(item.getAttribute("id"), item.value)
            }
          })
          for (var i = 0; i < this.files.length; i++) {
            var file = this.files[i];
            formData.append(file.id, file.file);
          }
        this.handleSubmit(formData)
      }
    },
    handleSubmit(formData) {
      //Envoi des données
      axios.post('/remocra/processusetlmodele/' + this.selected.id, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then((response) => {
          if (response.data.success) {
            this.$refs.modal.hide()
          }
        })
        .catch(function(error) {
          console.error('postProcess', error)
        })
    },
    getParams() {
      if(this.selected !== null){
        axios.get('/remocra/processusetlmodele/processusetlmodelparam/' + this.selected.id)
          .then((response) => {
            this.params = _.sortBy(response.data.data, item => item.formulaireNumOrdre)
            _.forEach(this.params, param => {
              if (param.formulaireTypeControle == 'combo') {
                axios.get('remocra/processusetlmodele/processusetlmodparalst/' + param.id).then((response) => {
                    _.forEach(response.data.data, option => {
                      var o = {
                        nomChamp: param.id,
                        valeur: option[param.sourceSqlValeur],
                        libelle: option[param.sourceSqlLibelle],
                        obligatoire: param.obligatoire,
                        formulaireValeurDefaut: param.formulaireValeurDefaut
                      };
                      this.comboOptions.push(o);
                    });
                    console.log(this.comboOptions)
                  })
                  .catch(function(error) {
                    console.error('Combo', error)
                  })
              }
            })
          })
      }

    },
    handleChangeFile(event, index) {
      this.files = _.reject(this.files, file => {
        return index == file.id
      })
      var file = event.target.files[0]
      if (file && file.name != null) {
        this.files.push({
          id: index,
          file: file
        })
      }
    }
  }
}
</script>

<style scoped>
>>>input {
  width: 100%;
}
</style>
