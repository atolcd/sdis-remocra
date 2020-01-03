<template>
  <div id="Courrier">
		<!-- ================================================ Paramètres du document ========================================================= -->
    <b-modal id="modalCourrier" ref="modalCourrier"  no-close-on-backdrop title="Génération de courrier" cancel-title="Annuler" ok-title="Aperçu" @ok="handleOk" @close="fermeModifier" @cancel="fermeModifier">
    <form id='formCourrier' name='courrier' enctype="multipart/form-data" method="POST" ref="formCourrier">
			<b-form-group>
				<div class="row">
					<div class="col-md-3">
						<label>Modèle</label>
					</div>
					<div class="col-md-9 mb-2">
						<b-form-select id="modele" size="sm" v-model="choixModele" :options="comboModele" @change="getParams"></b-form-select>
						<label class="description">{{this.choixModele.description}}</label>
					</div>
				</div>
			</b-form-group>
			<p v-if="params.length > 0">Veuillez renseigner les paramètres suivants </p>
			<p v-else-if="choixModele">Aucun paramètre pour cette requête</p>
			<!--Début boucle for-->
			<div v-for="(param, index) in params" :key="index">
				<b-form-group v-if='param.formulaireTypeControle=="autocomplete"' :id="param.nom" inputType='autocomplete' :required="param.obligatoire" class="parametreModele" horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<search-process-param :ref="'searchinput'+param.id" :paramId="param.id"></search-process-param>
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="combo"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<b-form-select size="sm" :id="param.nom" :required='param.obligatoire' class="form-control parametreModele">
						<option v-for="(value, key) in getOption(param.id)" :key="key" :value='value.valeur' :selected='value.valeur==value.formulaireValeurDefaut'>
							{{value.libelle}}
						</option>
					</b-form-select>
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="checkbox"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<input type='checkbox' style="width:5%" :id="param.nom" :checked="param.formulaireValeurDefaut" inputType='checkbox' class="form-control parametreModele" />
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="textfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<b-form-input size="sm" type='text' :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreModele" />
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="numberfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<b-form-input size="sm" type="number" :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' :step='(param.typeValeur=="integer")?1:0.001' class="form-control parametreModele" />
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="datefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<b-form-input size="sm" type='date' :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreModele" />
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="timefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
					<b-form-input size="sm" type='time' :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' step='1' class="form-control parametreModele" />
				</b-form-group>
				<b-form-group v-if='param.formulaireTypeControle=="datetimefield"' :id="'input'+param.id" horizontal :label='param.formulaireEtiquette' inputType='datetimefield' class='parametreModele' :label-for="'input'+param.id">
					<b-form-input size="sm" type='date' :id="param.nom" :value='param.formulaireValeurDefaut && param.formulaireValeurDefaut.split(" ")[0]' :required='param.obligatoire' class='form-control' />
					<b-form-input size="sm" type='time' :id="param.nom" :value='param.formulaireValeurDefaut && param.formulaireValeurDefaut.split(" ")[1]' :required='param.obligatoire' step='1' class='form-control' />
				</b-form-group>
			</div>
			<div class="text-center mt-3">
					<b-spinner v-if='showApercu == true && pdfLoading == true' label="Chargement ..."></b-spinner>
			</div>
    </form>
    </b-modal>

			<!-- ================================================ Aperçu du document ========================================================= -->

    <b-modal id="modalApercu" class="text-center" ref="modalApercu" no-close-on-backdrop title="Aperçu du courrier" hide-footer>
		<div class="pdf">
			<object type="application/pdf" :data="this.urlCourrier"
					width="100%" height="100%"></object>
		</div>
		<b-form-group>
			<div class="row justify-content-md-center mt-3" id="btnApercu">
				<div class="col-md-3 text-center">
					<b-button id="bouton" @click="fermeApercu" class="modifier">Modifier</b-button>
				</div>
				<div class="col-md-3 text-center">
					<b-button id="bouton" @click="telechargerCourrier" variant="primary">Télécharger</b-button>
				</div>
				<div class="col-md-3 text-center">
					<b-button id="bouton" @click="ouvreNotifier" class="notifier" variant="primary">Notifier</b-button>
				</div>
				<div class="col-md-3 text-center">
					<b-button id="bouton" @click="fermeApercu(); fermeModifier();" >Fermer</b-button>
				</div>
			</div>
		</b-form-group>
    </b-modal>

			<!-- ================================================ Envoi du document ========================================================= -->

	<b-modal id="modalNotifier" ref="modalNotifier" no-close-on-backdrop title="Notification par mail" hide-footer>
		<div class="row">
			<div class="col-md-3">
				<label>Recherche rapide :</label>
			</div>
			<div class="col-md-9">
				<b-form-input v-model="filtre" size="sm" @input="initListeDestinataire" id="filtre" placeholder="Recherche ..." >
				</b-form-input>
			</div>
		</div>
		<div class="row mt-2 ml-2">
			<div class="col-md-4 text-right">
				<b-form-checkbox id="chkBoxOrganisme" v-model="filtreOrga" @input="initListeDestinataire"	name="chkBoxOrganisme">Organisme</b-form-checkbox>
			</div>
			<div class="col-md-4 text-center">
				<b-form-checkbox id="chkBoxUtilisateur" v-model="filtreUtil" @input="initListeDestinataire" name="chkBoxUtilisateur">Utilisateur</b-form-checkbox>
			</div>
			<div class="col-md-4">
				<b-form-checkbox id="chkBoxContact" v-model="filtreContact"	@input="initListeDestinataire" name="chkBoxContact">Contact</b-form-checkbox>
			</div>
		</div>
		<div class="row mt-4">
			<div class="col-md-6">
				<label>Destinataires potentiels: </label>
			</div>
				<div class="col-md-6 overflow-auto">
					<b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage" size="sm" align="right" aria-controls="tabDestinataireNotifierNon">					
					</b-pagination>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 tabNotif">			
					<b-table  id="tabDestinataireNotifier" small responsive selectable select-mode="multi" @row-selected="onRowSelectedAjoute" :fields="fields"
						:items="tabDestinataireNotifierNon" :per-page="perPage" :current-page="currentPage">
					</b-table>
				</div>
			</div>
			<div class="row mt-2">
				<div class="col-md-6 text-right">
					<b-button id="boutonUpDown" @click="addNotifierOui" variant="primary"><img id="arrow" src="/remocra/static/img/down-arrow.png"></b-button>
				</div>
				<div class="col-md-6">
					<b-button id="boutonUpDown" @click="addNotifierNon" variant="primary"> <img id="arrow" src="/remocra/static/img/up-arrow.png"> </b-button>
				</div>
			</div>
			<div class="row">
			<div class="col-md-12 mt-2">
				<label>Destinataires définitifs : </label>
			</div>
			</div>
			<div class="row">
				<div class="col-md-12 tabNotif">
					<template>
						<b-table id="tabDestinataireNotifier" small responsive selectable select-mode="multi" @row-selected="onRowSelectedSupprime" :fields="fields"
							:items="tabDestinataireNotifierOui">
						</b-table>
					</template>
				</div>
			</div>
			<b-form-group>
				<div class="row justify-content-md-center mt-4">
					<div class="col-md-3">
						<b-button id="bouton" @click="fermeNotifier" class="retourApercu">Retour</b-button>
					</div>
					<div class="col-md-3">
						<b-button id="bouton" @click="notificationCourrier" class="notifier" variant="primary">Notifier</b-button>
					</div>
					<div class="col-md-3">
						<b-button id="bouton" @click="fermeNotifier(); fermeApercu(); fermeModifier()" >Fermer</b-button>
					</div>
				</div>
			</b-form-group>
    </b-modal>

				<!-- ==================================== Pop up notifier ============================================ -->

	<b-modal id="modalPopupNotif" ref="modalPopupNotif" no-close-on-backdrop
		title="Succès de notification" centered ok-only ok-title="Fermer" @ok="fermeNotifier(); fermeApercu(); fermeModifier()" >
			<b-form-group>
				<div class="row">
					<div class="col-md-12 text-center">
						<label>La demande de notification à bien été prise en compte.</label>
					</div>
				</div>
			</b-form-group>
    </b-modal>

  </div>
</template>

       <!-- ======================================= Script ================================================= -->

<script>
  import axios from 'axios'
  import _ from 'lodash'

export default {
	components: {
		//pdf
	},
	name: 'Courrier',
	
  data() {
    return {

			codeCourrier : "",
			/************* Partie paramètres document ****************/
			comboModele: [],
			choixModele: "",
			comboOptions: [],
			choixCommune: "",
			comboAdresseA: [],
			comboSite: [],
			params: [],
			files: [],

			/************** Partie aperçu document *******************/

			showApercu: false,
			urlCourrier: "",
			nomCourrier: "",
			pdfLoading: false,

			/************** Partie notification ***********************/
			filtre: null,
			filtreOrga: true,
			filtreUtil: true,
			filtreContact: true,
			selectAll: false,
			destinatairesLoaded: false,
			ajouteDestinataire: [],
			retireDestinataire: [],
			tabDestinataires:[],
			tabDestinataireNotifierNon: [],
			tabDestinataireNotifierOui: [],
			fields: ['Type', 'Nom', 'Email', 'Fonction' ],
			perPage: 100,
			currentPage: 1,
    }
	},
	
  props:{
  },

  computed: {
		rows() {
        return this.tabDestinataireNotifierNon.length
      }
  },

  mounted: function(){
		this.$refs.modalCourrier.show();
		this.initComboModele();
  },

  methods: {

		selectAllMethod(selectAll){
			if(selectAll == true){
				this.tabDestinataires = this.tabDestinataireNotifierNon;
			} else {
				this.tabDestinataires =[];
			}
		},

		ouvreModifier(){
			this.$refs.modalCourrier.show();
		},

		fermeModifier(){
			var element = document.getElementById("Courrier");
			element.parentNode.removeChild(element);
		},

		ouvreApercu(){
			this.$refs.modalApercu.show();
		},

		fermeApercu(){
			this.$refs.modalApercu.hide();
		},

		ouvreNotifier(){
			if(!this.destinatairesLoaded){
				this.initListeDestinataire();
			}
			this.$refs.modalNotifier.show();
		},

		fermeNotifier(){
			this.$refs.modalNotifier.hide();
		},

		onRowSelectedAjoute(items){
			this.ajouteDestinataire = items;
		},

		onRowSelectedSupprime(items){
			this.retireDestinataire = items;
		},

		addNotifierOui(){
			for(var i = 0; i<this.ajouteDestinataire.length; i++){
				this.tabDestinataireNotifierOui.push(this.ajouteDestinataire[i]);
				this.tabDestinataireNotifierNon.splice(this.tabDestinataireNotifierNon.indexOf(this.ajouteDestinataire[i]),1);
			}
			this.ajouteDestinataire = [];
		},

		addNotifierNon(){
			for(var i = 0; i<this.retireDestinataire.length; i++){
				this.tabDestinataireNotifierNon.push(this.retireDestinataire[i]);
				this.tabDestinataireNotifierOui.splice(this.tabDestinataireNotifierOui.indexOf(this.retireDestinataire[i]),1);
			}
			this.retireDestinataire = [];
		},

		initComboModele(){
			axios.get('/remocra/courrier').then((response)=> {
					var courriers = response.data.data;
					_.forEach(courriers, courrier => {
            this.comboModele.push({
            'value': {
              'id': courrier.id,
              'description': courrier.description
            },
            'text': courrier.libelle
          })
          });

        }).catch(function(error) {
              console.error(error)
          });
		},

		//Récupère les paramètres du modèle de courrier selectionné
		getParams() {
      if (this.choixModele !== null) {
        axios.get('/remocra/courrier/courrierParams/' + this.choixModele.id).then((response) => {
          this.params = _.sortBy(response.data.data, item => item.formulaireNumOrdre)
          _.forEach(this.params, param => {
            if (param.formulaireTypeControle === 'combo') {
              axios.get('/remocra/courrier/courriermodparalst/' + param.id).then((response) => {
                _.forEach(response.data.data, option => {
                  var o = {
                    idChamp: param.id,
                    valeur: option[param.sourceSqlValeur],
                    libelle: option[param.sourceSqlLibelle],
                    obligatoire: param.obligatoire,
                    formulaireValeurDefaut: param.formulaireValeurDefaut
                  }
                  this.comboOptions.push(o)
                })
              }).catch(function(error) {
                console.error('Combo', error)
              })
            }
          })
        })
      }
		},
		
		getOption: function (id) {
			return this.comboOptions.filter(function (value) {
				return value.idChamp === id
			})
		},

		telechargerCourrier() {
			axios({
				method: 'GET',
				url: this.urlCourrier,
				responseType: 'blob'
				}).then((response) => {
					var fileUrl = window.URL.createObjectURL(new Blob([response.data]));
					var fileLink = document.createElement('a');

					fileLink.href = fileUrl;
					fileLink.setAttribute('download', this.nomCourrier);
					document.body.appendChild(fileLink);

					fileLink.click();
					})
					.catch(function(error) {
						console.error('Erreur lors du téléchargement du courrier : ', error)
			})
    },

		handleOk(evt){
			evt.preventDefault();
			if(!this.choixModele == ""){
				if (document.getElementById('formCourrier').checkValidity() === false) {
					this.$notify({
						group: 'remocra',
						title: 'Génération de courrier',
						type: 'warn',
						text: 'Veuillez saisir les champs obligatoires'
					})
					document.getElementById('formCourrier').classList.add('was-validated')
				} else {
						let formData = new FormData()
						formData.append("modele", this.choixModele.id)
						_.forEach(evt.target.getElementsByClassName('parametreModele'), item => {
						if (item.getAttribute('inputType') === 'datetimefield') {
							var date = document.querySelector('input[id=' + item.id + 'date ]').value
							var time = document.querySelector('input[id=' + item.id + 'time]').value
							formData.append(item.id, date + ' ' + time)
						} else if (item.getAttribute('inputType') === 'autocomplete') {
							var autocomplete = this.$refs['search' + item.id][0]
							formData.append(item.getAttribute('id'), autocomplete.selected !== null ? autocomplete.selected.id : autocomplete.searchInput)
						} else if (item.getAttribute('inputType') === 'filefield') {
							var rawValueParts = item.value.split('\\')
							var value = rawValueParts[rawValueParts.length - 1]
							formData.append(item.getAttribute('id'), value)
						} else if (item.getAttribute('inputType') === 'checkbox') {
							formData.append(item.getAttribute('id'), item.checked)
						} else {
							formData.append(item.getAttribute('id'), item.value)
						}
					})
					for (var i = 0; i < this.files.length; i++) {
						var file = this.files[i]
						formData.append(file.id, file.file)
					}
					this.handleSubmitParams(formData)
				}
			}
		},

		handleSubmitParams(formData){
			this.showApercu = true;
			this.pdfLoading = true;
			// Envoi des données
      axios.post('/remocra/courrier/generecourrier/' + this.choixModele.id, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((response) => {
        if (response.data.success) {
					this.urlCourrier = "/remocra/ext-courrier/courrier_temp/"+response.data.message;
					this.nomCourrier = response.data.message.split("/")[1];
					this.codeCourrier = response.data.message.split("/")[0];
					this.ouvreApercu();
					this.pdfLoading=false;
        }
      }).catch(function(error) {
        console.error(error)
      })
		},
		
		initListeDestinataire() {
			axios.get('/remocra/courrier/contacts',{
				params: {
					filter: JSON.stringify([
					this.filtre,
					this.filtreOrga,
					this.filtreUtil,
					this.filtreContact
					])
				}
				}).then((response)=> {
					this.tabDestinataireNotifierNon = [];
					this.tabDestinataires = [];
					var contacts = response.data.data;
					_.forEach(contacts, contact => {
						var contactSplit = contact.split(";");
            this.tabDestinataires.push({
							'id': contactSplit[1],
							'Type': contactSplit[0],
							'Nom' : contactSplit[2],
							'Email' : contactSplit[3],
							'Fonction' : contactSplit[4]
					})
          });
        }).then(()=>{
					var i =0;
					for(i = 0; i<this.tabDestinataires.length; i++){
						this.tabDestinataireNotifierNon.push(this.tabDestinataires[i]);
					}
					this.destinatairesLoaded = true;
        }).catch(function(error) {
              console.error(error)
				});
		},

		/**
		 * codeCourrier
		 * tabNotifierOui
		 */
		notificationCourrier(){
			if(this.tabDestinataireNotifierOui.length != 0){
				var datas = {
					"codeCourrier": this.codeCourrier,
					"nomCourrier": this.nomCourrier,
					"destinataires": this.tabDestinataireNotifierOui
				}
				axios.post('/remocra/courrier/notifier', datas, {
					headers: {
						'Content-Type': 'application/json'
					}
				}).then((response) => {
					if (response.data.success) {
						this.$refs.modalPopupNotif.show();
					}
				}).catch(function(error) {
					console.error(error)
				})
			}

		},

  }
};
</script>

    <!-- ============================================ Style =============================================== -->

<style>

#modalCourrier .modal-content{
  background-color: #e9e9e9 !important;
  font-size: 1rem;
	width: 650px;
  padding-bottom: 0px;
  padding-top: 0px;
}

#modalApercu .modal-content{
  background-color: #e9e9e9 !important;
  font-size: 1rem;
	width: 650px;
}

#modalApercu .modal-body{
	padding: 5px;
}

#modalNotifier .modal-content{
  background-color: #e9e9e9 !important;
  font-size: 1rem;
	width: 650px;
  padding-bottom: 0px;
  padding-top: 0px;
}

#modalPopupNotif .modal-content{
	background-color: #e9e9e9 !important;
  font-size: 1rem;
	min-width: 650px;
	padding-bottom: 0px;
}

#modalPopupNotif .modal-body{
	padding-bottom: 0px;
}

.chargement{
  animation: opacity-anim 1s linear infinite;
}

#bouton{
	width: 115px;
}

#boutonUpDown{
	width: 50px;
	height: 40px;
}

#arrow{
	vertical-align: center;
}

#addNotifierNon{
	width: 38px;
}

#addNotifierOui{	
	width: 38px;
}

#zoom{
	width: 31px;
}

.pdf{
	align-content: center;
	height: 500px;
}

.tabNotif .table-responsive{
	max-height: 250px;
	background-color: white;
	font-size: 10pt;
}

.description{
	font-size: 15px; 
}

.pagination{
		margin-bottom: 0; 
}

#tabDestinataireNotifier tbody {
    display:block;
    max-height:200px;
    overflow:auto;
}

#tabDestinataireNotifier thead, #tabDestinataireNotifier tbody tr {
    display:table;
    width:100%;
    table-layout:fixed;
		word-wrap: break-word;
}

</style>