<template>
	<div :class="{ 'loading': !dataLoaded }">
		<b-modal 	id="modalDebitSimultane" 
					ref="modalDebitSimultane" 
					no-close-on-backdrop 
					title="Débit simultané"
					ok-title="Fermer" 
					ok-only=true
					cancel-title="Annuler" 
					size="lg"
					@hidden="close()" 
					@ok="handleOk" 
					centered >
			<div v-if="dataLoaded">
				<div class="row">
					<div class="col-md-2">
						Site:
					</div>
					<div class="col-md-4">
						<p v-if="debitSimultane.site">{{debitSimultane.site.nom}}</p>
					</div>

					<div class="col-md-3 ">
						Type de réseau :
					</div>

					<div class="col-md-3">
						{{(typeReseauImpose) ? typeReseauImpose.nom : ''}}
					</div>
				</div>

				<div class="row">
					<div class="col-md-6">
						<b-form-group label="N° dossier" label-for="numDossier" label-cols-md="4" invalid-feedback="Le numéro de dossier est manquant" :state="etats.numDossier">
							<b-form-input id="numDossier" v-model="debitSimultane.numDossier" class="parametre" type="text" size="sm" :state="etats.numDossier" required></b-form-input>
						</b-form-group>
					</div>

					<div class="col-md-3 ">
						Diamètre de canalisation : 
					</div>

					<div class="col-md-3">
						{{ diametreImpose ? diametreImpose.nom +" mm" : ''}}
					</div>
				</div>

				<div class="row rowButtons" v-if="!isNew">
					<div class="col-md-12">
						<button class="btn btn-secondary" @click.prevent @click="createMesure" :disabled="createMesureDisabled">Ajouter</button>
						<button class="btn btn-danger right" @click.prevent @click="deleteMesure" :disabled="deleteMesureDisabled">Supprimer</button>
					</div>
				</div>

				<div class="row">
					<div class="col-md-12">
						<table class="table table-striped table-sm table-bordered" id="tableMesures">
							<thead class="thead-light">
								<th scope="col">Date</th>
								<th scope="col">Débit retenu m3/h</th>
								<th scope="col">PEI</th>
								<th scope="col">Attestation</th>
							</thead>
							<tbody>
								<tr v-for="(item, index) in mesures" :key="index" @click="onRowSelected(index)" :class="{'table-success':index==selectedRow}">
									<td>
										{{datetimeFormatee(item.formattedDate+ " " + item.formattedTime)}}
									</td>

									<td>
										{{item.irv ? 'Identique au Réseau de Ville' : item.debitRetenu}}
									</td>

									<td>
										{{ labelPEI(index) }}
									</td>

									<td>
										<div v-if="item.attestation">
											{{item.attestation.document.fichier}}
											<a :href="'telechargement/document/'+item.attestation.code" download><img src="../assets/img/pdf.png" width="32"/></a>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

				<div v-if="mesures[selectedRow] !== undefined">
					<div class="row">
						<div class="col-md-12">
							<p class="title">Mesure</p>
						</div>
					</div>

					<div class="row">
						<div class="col-md-6">
							<div class="row">
								<div class="col-md-8">
									<b-form-group label="Date " label-for="date" label-cols-md="5">
										<b-form-input id="date" v-model="mesures[selectedRow].formattedDate" type="date" size="sm" required></b-form-input>
									</b-form-group>
								</div>

								<div class="col-md-4">
									<b-form-input id="time" v-model="mesures[selectedRow].formattedTime" type="time" size="sm" required></b-form-input>
								</div>
							</div>
						</div>

						<div class="col-md-6">
							<div class="row" v-if="!isNew">
								<div class="col-md-9">
									<b-form-group label="PEI" label-for="pei" label-cols-md="2">
								
										<b-form-select 	id="pei"
											v-model="selectedPeiFromCombo"
											:options="comboAjoutHydrant()"
											size="sm"></b-form-select>
									</b-form-group>
								</div>

								<div class="col-md-3">
									<b-button @click="ajouterPei" class="mr-2 btn btn-secondary d-inline-block right" size="sm" :disabled="selectedPeiFromCombo==null">Ajouter</b-button>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-6">

							<div class="row">
								<div class="col-md-12">
									<b-form-group label="Débit requis (m3/h)" label-for="debitRequis" label-cols-md="6" invalid-feedback="Le débit requis doit être renseigné" :state="etats.debitRequis">
										<b-form-input id="debitRequis" v-model="mesures[selectedRow].debitRequis" type="number" size="sm"></b-form-input>
									</b-form-group>
								</div>
							</div>

							<div class="row">
								<div class="col-md-12">
									<b-form-group label="Débit mesuré" label-for="debitMesure" label-cols-md="6" invalid-feedback="Le débit mesuré doit être renseigné" :state="etats.debitMesure">
										<b-form-select 	id="debitMesure"
														v-model="mesures[selectedRow].debitMesure"
														:options="comboDebitMesure"
														size="sm"
														required></b-form-select>
									</b-form-group>
								</div>
							</div>

							<div class="row">
								<div class="col-md-12">
									<b-form-group label="Débit retenu (m3/h)" label-for="debitRetenu" label-cols-md="6" invalid-feedback="Le débit retenu doit être renseigné" :state="etats.debitRetenu">
										<b-form-input id="debitRetenu" v-model="mesures[selectedRow].debitRetenu" type="number" size="sm" :disabled="mesures[selectedRow].irv"></b-form-input>
									</b-form-group>
								</div>
							</div>

						</div>

						<div class="col-md-6">
							<div class="row">
								<div :class="{'col-md-9':!isNew, 'col-md-12':isNew}">
									<b-form-group label-cols-md="2">
									<ul :class="{'itemPeiContainer':true, 'hidden':!this.mesures[this.selectedRow].listeHydrants.length}">
									<li v-for="item in this.mesures[this.selectedRow].listeHydrants" 
										v-bind:key="item.id" 
										size="sm" 
										:class="{'itemPei': true, 'bg-secondary':item.id==selectedPei, 'text-light':item.id==selectedPei}" 
										@click="selectedPei = item.id">
										{{item.numero}}
									</li>
									</ul>
								</b-form-group>
								</div>

								<div class="col-md-3" v-if="!isNew">
									<b-button @click="retirerPei" class="mr-2 btn btn-secondary d-inline-block right" size="sm" :disabled="selectedPei==null">Retirer</b-button>
								</div>
							</div>
							
						</div>
					</div>

					<div class="row">
						<div class="col-md-3 offset-md-3">
							<b-form-checkbox
								id="irv"
								v-model="mesures[selectedRow].irv"
								v-on:change="onIRVChecked"
								size="sm">
								Identique Réseau Ville
							</b-form-checkbox>
						</div>
						<div class="col-md-6">
							<b-form-group label="Attestation" label-for="attestation" label-cols-md="2">
							<b-form-file
								id="attestation"
								v-model="newAttestationInputValue"
								class="mb-2"
								placeholder="Aucun fichier sélectionné"
								:file-name-formatter="newAttestationName"
								browse-text="Charger"
							></b-form-file>
						</b-form-group>
						</div>
					</div>

					<div class="row">
						<div class="col-md-12">
							<b-form-group label="Commentaire" label-for="commentaire" label-cols-md="2">
								<b-form-textarea id="commentaire" v-model="mesures[selectedRow].commentaire" size="sm"></b-form-textarea>
							</b-form-group>
						</div>
					</div>
				</div>
			</div>

			<b-modal id="modal-datetime" size="sm" hide-header ok-only ok-title="OK" centered>
				<p>Saisie invalide : </p>
				<p class="my-4">Au moins deux mesures sont renseignées pour la même date et la même heure sur ce débit simultané</p>
			</b-modal>

			<b-modal id="modal-onlyOne" size="sm" hide-header ok-only ok-title="OK" centered>
				<p>Saisie invalide : </p>
				<p class="my-4">Toutes les mesures doivent porter sur au moins 2 PEI différents</p>
			</b-modal>
		</b-modal>
	</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import moment from 'moment'
export default {
	name: 'DebitSimultaneFiche',

	data () {
		return {
			debitSimultane: null,
			mesures: [],
			dataLoaded: false,
			comboDebitMesure: null,
			diametreImpose: null,
			typeReseauImpose: null,
			selectedRow: null,
			newAttestationInputValue: null,
			hydrantsAdequats: [], // Tableau regroupant les hydrants pouvant faire partie de ce Débit Simultané
			selectedPeiFromCombo: null,
			selectedPei: null,
			mesuresASupprimer: [],
			etats: {
				numDossier: null,
				noSameDateTime: null,
				date: null,
				time: null,
				debitRequis: null,
				debitRetenu: null,
				debitMesure: null,
				plusieursPeiParMesure: null,
			}
		}
	},

	props: {
		idDebitSimultane: {
			type: Number,
			required: false
		},

		// Liste des hydrants à prendre en compte lors de la création du débit simultané
		listeHydrantsOnCreate: {
			type: String,
			required: false
		},

		vitesseEau: {
			type: Number,
			required: true
		}
	},

	computed: {
		/**
		  * Date au format dd/MM/yyyy pour l'affichage dans la datagrid des visites
		  */
		datetimeFormatee: function(){
			return function(datetime){
				return moment(datetime).format('DD/MM/YYYY HH:mm')
			}
		},

		/**
		  * Règles d'activation du bouton de saisie des mesures:
		  *		- L'utilisateur a le droit
		  */
		createMesureDisabled: function() {
			return false;
		},

		/**
		 *	Règles d'activation du bouton de suppresion des mesures :
		 * 		- Une ligne est sélectionnée
		 *		- Il y a eu moins deux mesures présentes (impossible de pouvoir supprimer la dernière mesure)
		 *		- L'utilisateur a le droit
		 */
		deleteMesureDisabled: function() {
			return this.selectedRow === null || this.mesures.length <= 1;
		},

		/**
		  * Calcul automatique du débit retenu
		  */
		debitSimultaneCalcule: function() {
			// Formule: Q (m3/s) = V * S = Vitesse_eau*(PI*D*D)/4; Q (m3/h) = Q(m3/s)*3600
			return Math.floor(this.vitesseEau*Math.pow(Number(this.diametreImpose.nom)/1000, 2)*2826);
		},

		/**
		  * Label affiché dans la colonne "PEI" des mesures
		  */
		labelPEI: function(){
			var self = this;
			return function(index){
				var liste = self.mesures[index].listeHydrants;
				if(!liste.length) {
					return "-"
				}
				var label = liste[0].numero;
				if(liste.length > 1) {
					label += " et " +(liste.length-1) + " autre" + ((liste.length > 2) ? "s" : "");
				}
				return label
			}
		},

		/**
		  * Renvoie un booléen indiquant si il s'agit de la création d'un débit simultané ou non
		  */
		isNew: function() {
			return this.debitSimultane.id === null;
		}

	},


	mounted: function() {
		this.$refs.modalDebitSimultane.show();

		this.comboDebitMesure = [];
		for(var i = 60; i <= 2400; i += 60){
			this.comboDebitMesure.push({
				text: i,
				value: i
			});
		}

		if(this.idDebitSimultane > -1 && this.listeHydrantsOnCreate === 'null') {
			this.modification();
		} else {
			this.creation();
		}
	},

	methods: {

		creation() {
			var listeHydrants = this.listeHydrantsOnCreate.split(',');
			this.debitSimultane = {
				id: null,
				numDossier: ''
			};

			var nbRequetesEnAttente = listeHydrants.length;
			var nbRequetesTerminees = 0;
			var liste = [];
			_.forEach(listeHydrants, idHydrant => {
				axios.get('/remocra/hydrantspibi/'+idHydrant).then(response => {
					var hydrant = response.data.data;

					liste.push({
						id: hydrant.id,
						numero: hydrant.numero
					});
					if(this.diametreImpose === null) {
						this.diametreImpose = hydrant.diametre;
					}
					if(this.typeReseauImpose === null) {
						this.typeReseauImpose = hydrant.typeReseauAlimentation;
					}

					// On récupère les informations du site depuis l'un des hydrants
					// Normalement, les hydrants font partie du même site. En pratique, il n'y a pas encore de vérifications pour cela
					if(hydrant.site && hydrant.site.id && hydrant.site.nom) {
						this.debitSimultane.site = {
							id: hydrant.site.id,
							nom: hydrant.site.nom
						}
					}

					nbRequetesTerminees++;
					if(nbRequetesTerminees === nbRequetesEnAttente){
						this.createMesure();
						this.mesures[0].listeHydrants = liste;
						this.dataLoaded = true;
					}
				});
			});

		},

		modification() {
			axios.get('/remocra/debitsimultane/'+this.idDebitSimultane).then(response => { // Récupération des infos du débit simultané
				this.debitSimultane = response.data.data;
				
				axios.get('/remocra/debitsimultanemesure', { // Récupération des mesures effectuées
                    params: {
                        filter: JSON.stringify([{"property":"debitSimultane","value":this.debitSimultane.id}]),
                        sort: JSON.stringify([{"property":"dateMesure","direction":"DESC"}])
                    }
                }).then(response => {
					this.mesures = response.data.data;
					var nbRequetesEnAttente = response.data.data.length;
					var nbRequetesTerminees = 0;

					_.forEach(this.mesures, mesure => {
						mesure.newAttestation = null;
						var splitDate = mesure.dateMesure.split("T");
						mesure.formattedDate = splitDate[0];
						mesure.formattedTime = splitDate[1].substr(0, splitDate[1].length - 3);

						axios.get('/remocra/debitsimultanehydrant', { // On récupère les hydrants composant chacunes des mesures
							params: {
							filter: JSON.stringify([{"property":"debit","value":mesure.id}]),
							}
						}).then(response => {
							var listeHydrants = [];
							_.forEach(response.data.data, hydrant => {
								listeHydrants.push({
									id: hydrant.hydrant.id,
									numero: hydrant.hydrant.numero
								});
								if(this.diametreImpose === null) {
									this.diametreImpose = hydrant.hydrant.diametre;
								}
								if(this.typeReseauImpose === null) {
									this.typeReseauImpose = hydrant.hydrant.typeReseauAlimentation;
								}
							})
							mesure.listeHydrants = listeHydrants;

							// Permet de savoir si toutes les requêtes ont été jouées
							// (une requête dans un forEach lance autant de requête que d'itération, il faut attendre la fin de toutes les requêtes)
							nbRequetesTerminees++;
							if(nbRequetesEnAttente == nbRequetesTerminees){

								this.hydrantsAdequats = [];
								axios.get('/remocra/hydrantspibi', {
									params: {
										filter: JSON.stringify([
											{"property":"codeNatureDeci","value":"PRIVE"},
											//{"property":"site","value":this.debitSimultane.site.id}, // Pour l'instant, le site n'est pas une caractéristique discriminante
											{"property":"diametre","value":this.diametreImpose.id},
											{"property":"typeReseau","value":this.typeReseauImpose.id}
										]),
									}
								}).then(response => {
									_.forEach(response.data.data, hydrant => {
										this.hydrantsAdequats.push({
											id: hydrant.id,
											numero: hydrant.numero
										}) 
									});
									this.dataLoaded = true;
								});
								
							}
						});
						
					});

				});
			});	
		},

		onIRVChecked(value) {
			if(value) {
				this.mesures[this.selectedRow].debitRetenu = null;
				document.querySelectorAll('[for="debitRetenu"]')[0].classList.add("labelDisabled");
			} else {
				this.mesures[this.selectedRow].debitRetenu = this.debitSimultaneCalcule;
				document.querySelectorAll('[for="debitRetenu"]')[0].classList.remove("labelDisabled");
			}
		},

		newAttestationName: function() {
			if(this.newAttestationInputValue){
				return this.newAttestationInputValue.name;
			} else {
				return "Aucun fichier sélectionné"
			}
		},

		/**
		  * Valeur de la combobox permettant d'ajouter des hydrants à une mesure
		  * Les hydrants présents sont les hydrants compatibles moins ceux déjà présents dans la mesure
		  */
		comboAjoutHydrant: function() {
			if(this.selectedRow !== null){
				var liste = this.hydrantsAdequats.filter(h => this.mesures[this.selectedRow].listeHydrants.map(a => a.id).indexOf(h.id) === -1);
				var combo = [];
				_.forEach(liste, item => {
					combo.push({
						text: item.numero,
						value: item.id
					});
				});
				return combo;
			}
			return null;
		},

		/**
		  * Clic sur le bouton "Ajouter" de la sélection des PEIs d'une mesure
		  */
		ajouterPei: function() {
			if(this.selectedRow !== null && this.selectedPeiFromCombo !== null) {
				var pei = this.hydrantsAdequats.filter(a => a.id == this.selectedPeiFromCombo)[0];
				this.mesures[this.selectedRow].listeHydrants.push({
					id: pei.id,
					numero: pei.numero
				});
				this.selectedPeiFromCombo = null;
			}			
		},

		/**
		  * Clic sur le bouton "Supprimer" de la sélection des PEIs d'une mesure
		  */
		retirerPei: function() {			
			if(this.selectedRow !== null && this.selectedPei !== null) {
				this.mesures[this.selectedRow].listeHydrants.splice(this.mesures[this.selectedRow].listeHydrants.findIndex(h => h.id == this.selectedPei), 1);
				this.selectedPei = null;
			}
		},

		/**
		  * Lorsqu'une ligne est sélectionnée, elle obtient la classe "table-success"
		  * Une ligne est sélectionnée si l'on clique dessus
		  */
		onRowSelected(index) {			
			if(this.selectedRow !== null) {
				this.mesures[this.selectedRow].newAttestation = this.newAttestationInputValue;
				this.newAttestationInputValue = this.mesures[index].newAttestation;
			}
			this.selectedRow = index;
			
		},

		createMesure() {
			var date = moment(new Date()).format("YYYY-MM-DD HH:mm");

			this.mesures.unshift({
				dateMesure: '',
				formattedDate: date.split(" ")[0],
				formattedTime: date.split(" ")[1],
				debitRequis: null,
				debitMesure: null,
				debitRetenu: this.debitSimultaneCalcule,
				commentaire: null,
				irv: false,
				listeHydrants: [],
				attestation: null,
				newAttestation: null,
				debitSimultane: this.debitSimultane.id
			});

			this.onRowSelected(0); // Sélection automatique de la mesure
		},

		deleteMesure() {
			if(this.selectedRow == null){
				return null;
			}

			var idMesure = this.mesures[this.selectedRow].id;
			if(idMesure != undefined) {
				this.mesuresASupprimer.push(idMesure);
			}

			this.mesures.splice(this.selectedRow, 1);

			this.selectedRow = null;
			this.onRowSelected(null);
		},

		close(){
			this.$root.$options.bus.$emit('closed');
		},

		handleOk(modalEvent) {
			modalEvent.preventDefault();
			if(this.checkFormValidity()){
				this.handleSubmit();
			}
		},

		checkFormValidity(){
			var regexDate = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))/;
			var regexTime = /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/;

			var invalidMesure = null;

			this.etats.numDossier = (this.debitSimultane.numDossier.length > 0) ? 'valid' : 'invalid';

			this.etats.debitRequis = 'valid';
			this.etats.debitRetenu = 'valid';
			this.etats.date = 'valid';
			this.etats.time = 'valid';
			this.etats.debitMesure = 'valid';
			_.forEach(this.mesures, mesure => {
				if(!mesure.debitRequis || mesure.debitRequis.length == 0) {
					this.etats.debitRequis = 'invalid';
					invalidMesure = mesure;
				}

				if((!mesure.debitRetenu || mesure.debitRetenu.length == 0) && !mesure.irv) {
					this.etats.debitRetenu = 'invalid';
					invalidMesure = mesure;
				}

				if(!regexDate.test(mesure.formattedDate)) {
					this.etats.date = 'invalid';
					invalidMesure = mesure;
				}

				if(!regexTime.test(mesure.formattedTime)) {
					this.etats.time = 'invalid';
					invalidMesure = mesure;
				}

				if(mesure.debitMesure === null) {
					this.etats.debitMesure = 'invalid';
					invalidMesure = mesure;
				}
			});

			if(this.mesures.length > 0){
				this.etats.noSameDateTime = 'valid';
				this.etats.plusieursPeiParMesure = 'valid';
				var tabDates = [];
				_.forEach(this.mesures, mesure => {
					var date = mesure.formattedDate+" "+mesure.formattedTime;

					if(tabDates.indexOf(date) != -1){
						this.etats.noSameDateTime = 'invalid';
						this.$bvModal.show("modal-datetime");
					} else {
						tabDates.push(date);
					}

					if(mesure.listeHydrants.length < 2){
						invalidMesure = mesure;
						this.etats.plusieursPeiParMesure = 'invalid';
						this.$bvModal.show("modal-onlyOne");
					}

				})
			}

			if(invalidMesure) {
				_.forEach(this.mesures, (mesure, index) => {
					if(mesure === invalidMesure) {
						this.onRowSelected(index);
					}
				});
			}

			return !this.hasInvalidState(this.etats);
		},

		hasInvalidState(etats){
			var hasInvalidState = false;
			for(var key in etats){
				hasInvalidState = hasInvalidState || etats[key] == "invalid";
			}		
			return hasInvalidState;
		},

		handleSubmit() {

			var url = 'debitsimultane' + (this.debitSimultane.id == null ? '' : '/' + this.debitSimultane.id);

			var formData = new FormData();
			formData.append("debitSimultane", JSON.stringify(this.debitSimultane));

			// Mise à jour du débit simultané
			axios.post(url, formData, {
				headers: {
					'Content-Type': 'multipart/form-data'
				}
			}).then((response) => {
				this.debitSimultane.id = response.data.data.id; // En cas de création d'un débit simultané, on récupère son ID pour le répercuter sur les mesures qui le composent
				// Mise à jour des mesures
				if(this.selectedRow !== null) {
					this.mesures[this.selectedRow].newAttestation = this.newAttestationInputValue;
				}
				
				var fichiers = [];
				_.forEach(this.mesures, mesure => {

					mesure.debitSimultane = this.debitSimultane.id;

					mesure.dateMesure = mesure.formattedDate+" "+mesure.formattedTime+":00";
				
					var hydrants = [];
					_.forEach(mesure.listeHydrants, hydrant => {
						hydrants.push(hydrant.id);
					});

					mesure.listeHydrants = hydrants;
					mesure.indexAttestation = -1;

					if(mesure.newAttestation) {
						fichiers.push(mesure.newAttestation);
						mesure.indexAttestation = fichiers.length -1;
					}

					// Retrait des infos ajoutées dans l'objet côté client pour simplification
					delete mesure.attestation;
					delete mesure.newAttestation;
					delete mesure.formattedDate;
					delete mesure.formattedTime;
				});

				formData = new FormData();
				formData.append("mesures", JSON.stringify(this.mesures));
				for (var i = 0; i < fichiers.length; i++) {
					let file = fichiers[i];
					formData.append('files[' + i + ']', file)
				}

				// Suppression des mesures
				axios.delete('debitsimultanemesure/', {
					data: this.mesuresASupprimer
				}).catch(function(error) {
					console.error('postEvent', error)
				});

				// Ajout des mesures. Une fois ceci fait, on peut mettre à jour la géométrie du débit simultané
				axios.post('debitsimultanemesure/updatemany', formData, {
					headers: {
						'Content-Type': 'multipart/form-data'
					}
				}).then(() => {
					axios.get('debitsimultane/updategeometry/' + (this.debitSimultane.id))
				});
			});

			this.$nextTick(() => {
				this.$refs.modalDebitSimultane.hide()
			});
		}

	}
};	
</script>

<style>
.loading .modal-body::after {
  content: 'Chargement des données...';
  margin-left: calc(50% - 67px);
  animation: opacity-anim 1s linear infinite;
}

.modal-footer {
	justify-content: center;
}

.text-right {
	text-align: right;
}

#tableMesures td, #tableMesures th {
	text-align: center;
}

#tableMesures th {
	text-align: left;
	font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif,"Apple Color Emoji","Segoe UI Emoji","Segoe UI Symbol";
	font-size: 1rem;
	color: #495057;
	font-size: 1rem;
	font-weight: 400;
	line-height: 1.5;
	vertical-align: middle;
}

.right {
	margin-left: 10px;
}

.rowButtons {
	margin-bottom: 5px;
}

.labelDisabled {
	color: #6c757d !important;
}

.itemPeiContainer {
	border-radius: 0.2em;
	border: 1px solid #CED4DA;
}

.itemPei {
	padding: 2px 0px 0px 5px;
	color: #495057;
}

.hidden {
	visibility: hidden;
}
</style>