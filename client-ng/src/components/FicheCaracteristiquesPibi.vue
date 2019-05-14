<template>
	<div>
		<div class="title">PEI</div>
		<div class="row">
			<div class="col-md-4">
				<b-form-group label="Diamètre nominal : " label-for="diametre">
					<b-form-select id="diametre" v-model="hydrant.diametre" class="parametre" :options="comboDiametre" size="sm"></b-form-select>
				</b-form-group>
			</div>

			<div class="col-md-4 vertical-bottom">
				<b-form-checkbox
					id="dispositif_inviolabilite"
					v-model="hydrant.dispositif_inviolabilite"
					class="parametre"
					size="sm">
					Dispositif d'inviolabilite
				</b-form-checkbox>
			</div>

			<div class="col-md-4 vertical-bottom">
				<b-form-checkbox
					id="renversable"
					v-model="hydrant.renversable"
					class="parametre"
					size="sm">
					Renversable
				</b-form-checkbox>
			</div>
		</div>

		<div class="row">
			<div class="col-md-4">
				<b-form-group label="Jumelé avec : " label-for="jumele">
					<b-form-select id="jumele" v-model="hydrant.jumele" class="parametre" :options="comboJumele" :disabled="this.hydrant.id == null" size="sm"></b-form-select>
				</b-form-group>
			</div>
		</div>

		<div class="row">
			<div class="col-md-4">
				<b-form-group label="Marque : " label-for="marque">
					<b-form-select id="marque" v-model="hydrant.marque" class="parametre" :options="comboMarque" size="sm" v-on:change="onMarqueChange"></b-form-select>
				</b-form-group>
			</div>

			<div class="col-md-4">
				<b-form-group label="Modèle : " label-for="modele">
					<b-form-select id="modele" v-model="hydrant.modele" class="parametre" :options="comboModele" size="sm"></b-form-select>
				</b-form-group>
			</div>

			<div class="col-md-4">
				<b-form-group label="Année de fabrication : " label-for="anneeFabrication" invalid-feedback="L'année n'est pas valide" :state="etats.anneeFabrication">
					<b-form-input id="anneeFabrication" v-model="hydrant.anneeFabrication" class="parametre" type="number" size="sm" :state="etats.anneeFabrication"></b-form-input>
				</b-form-group>
			</div>
		</div>

		<div class="title">Réseau</div>
			<div class="row">
				<div class="col-md-4">
					<b-form-group label="Service des eaux : " label-for="serviceEaux">
						<b-form-select id="serviceEaux" v-model="hydrant.serviceEaux" class="parametre" :options="comboServiceEaux" size="sm"></b-form-select>
					</b-form-group>
				</div>

				<div class="col-md-4">
					<b-form-group label="Type de réseau : " label-for="typeReseauAlimentation">
						<b-form-select id="typeReseauAlimentation" v-model="hydrant.typeReseauAlimentation" class="parametre" :options="comboTypeReseauAlimentation" size="sm"></b-form-select>
					</b-form-group>
				</div>

				<div class="col-md-4 vertical-bottom">
					<b-form-checkbox
						id="debitRenforce"
						v-model="hydrant.debitRenforce"
						class="parametre"
						size="sm">
						Débit renforcé
					</b-form-checkbox>
				</div>
			</div>

			<div class="row">
				<div class="col-md-4">
					<b-form-group label="Type de canalisation : " label-for="typeReseauCanalisation">
						<b-form-select id="typeReseauCanalisation" v-model="hydrant.typeReseauCanalisation" class="parametre" :options="comboTypeReseauCanalisation" size="sm"></b-form-select>
					</b-form-group>
				</div>

				<div class="col-md-4">
					<b-form-group label="Diamètre de canalisation : " label-for="diametreCanalisation" invalid-feedback="Le diamètre n'est pas valide" :state="etats.diametreCanalisation">
						<b-form-input id="diametreCanalisation" v-model="hydrant.diametreCanalisation" class="parametre" type="number" size="sm" :state="etats.diametreCanalisation"></b-form-input>
					</b-form-group>
				</div>
			</div>

			<div class="row">
				<div class="col-md-6">
					<b-form-group label="Réservoir : " label-for="reservoir" class="">
						<b-form-select id="reservoir" v-model="hydrant.reservoir" :options="comboReservoir" class="parametre" size="sm"></b-form-select>
						<div class="d-inline-flex"><button class="btnInlineForm" @click.prevent v-b-modal.modal-reservoir><img src="../assets/img/add.png"></button></div>
					</b-form-group>
				</div>
			</div>

			<div class="row">
				<div class="col-md-6">
					<b-form-checkbox
						id="surpresse"
						v-model="hydrant.surpresse"
						class="parametre"
						size="sm"
						:disabled="!isDeciPrivee">
						Réseau surpressé
					</b-form-checkbox>
				</div>

				<div class="col-md-6">
					<b-form-checkbox
						id="additive"
						class="parametre"
						v-model="hydrant.additive"
						size="sm"
						:disabled="!isDeciPrivee">
						Réseau additivé
					</b-form-checkbox>
				</div>
			</div>

			<ModalReservoir v-on:modalReservoirValues="onReservoirCreated"></ModalReservoir>
	</div>
</template>

<script>

import axios from 'axios'
import _ from 'lodash'

import ModalReservoir from './ModalReservoir.vue'

export default {
	name: 'FicheLocalisation',

	components: {
		ModalReservoir
	},

	data() {
		return {
			comboDiametre: [],
			comboMarque: [],
			comboModele: [],
			comboServiceEaux: [],
			comboTypeReseauAlimentation: [],
			comboTypeReseauCanalisation: [],
			comboReservoir: [],
			comboJumele: [],

			etats: {
				anneeFabrication: null,
				diametreCanalisation: null
			}
		}
	},
	props: {
		hydrant: {
			required: true,
			type: Object
		},
		hydrantRecord: {
			required: true,
			type: Object
		},
		listeNaturesDeci: {
			required: true,
			type: Object
		},
		geometrie: {
			required: true,
			type: Object
		}
	},

	computed: {

		/**
		  * Indique si le PEI possède ou non une nature DECI privée
		  */
		isDeciPrivee: function() {
			return (this.listeNaturesDeci.length > 0) ? this.hydrant.natureDeci === this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id : false;
		}
	},

	mounted: function(){

		this.$emit('resolveForeignKey', ['diametre', 'marque', 'modele', 'typeReseauAlimentation', 'typeReseauCanalisation', 'reservoir', 'serviceEaux', 'jumele']);

		this.$emit('getComboData', this, 'comboDiametre', '/remocra/typehydrantdiametres.json', 'id', 'nom');
		this.$emit('getComboData', this, 'comboMarque', '/remocra/typehydrantmarques.json', 'id', 'nom');
		this.$emit('getComboData', this, 'comboServiceEaux', '/remocra/organismes.json?filter=[{"property":"typeOrganismeCode","value":"SERVICEEAUX"}]', 'id', 'nom');
		this.$emit('getComboData', this, 'comboTypeReseauAlimentation', '/remocra/typereseaualimentation.json', 'id', 'nom');
		this.$emit('getComboData', this, 'comboTypeReseauCanalisation', '/remocra/typereseaucanalisation.json', 'id', 'nom');
		this.$emit('getComboData', this, 'comboReservoir', '/remocra/reservoir.json', 'id', 'nom', 'Aucun');

		if(this.hydrant.id !== null) {
			//this.$emit('getComboData', this, 'comboJumele', '/remocra/hydrantspibi/findjumelage?nature='+this.hydrant.nature+'&geometrie='+this.geometrie.replace(/ /g, '%20'), 'id', 'numero', 'Aucun');
		
		//this.$emit('getComboData', this, 'comboJumele', '/remocra/hydrantspibi/findjumelage?nature='+this.hydrant.nature+'&geometrie='+this.geometrie.replace(/ /g, '%20'), 'id', 'numero', 'Aucun');

			this.comboJumele = [];
			var self = this;
			axios.get('/remocra/hydrantspibi/findjumelage', {
				params: {
					nature: this.hydrant.nature,
					geometrie: this.geometrie,
					numeroInterne: this.hydrant.numeroInterne
				}
				
			}).then(response => {
				_.forEach(response.data.data, function(item) {
					self.comboJumele.push({
						text: item.numero,
						value: item.id
					});
				});

				self.comboJumele.unshift({
					text: 'Aucun',
					value: null
				});
			}).catch(function(error) {
				console.error('Retrieving combo data from /remocra/hydrantspibi/findjumelage', error);
			})
		}
		this.onMarqueChange();
	},

	methods: {

		/**
			*	En cas de changement de marque, on met à jour la liste des modèles en conséquence
			*/
		onMarqueChange() {
			var self = this;
			this.comboModele = [];
			self.comboModele.push({
				text: 'Aucun',
				value: null
			});
			if(this.hydrant.marque){
				axios.get('/remocra/typehydrantmarques.json?filter=[{"property":"id","value":"'+this.hydrant.marque+'"}]').then(response => {
					if(response.data.data){
						_.forEach(response.data.data[0].modeles, function(item) {
							self.comboModele.push({
								text: item.nom,
								value: item.id
							});
						});
					}
				}).catch(function(error) {
					console.error('Retrieving combo data from /remocra/typehydrantmarques ', error);
				});
			}

			if(this.hydrantRecord.marque && this.hydrantRecord.marque.id != this.hydrant.marque){
				this.hydrant.modele = null;
			}
			else{
				this.hydrant.modele = (this.hydrantRecord.modele) ? this.hydrantRecord.modele.id : null;
			}
		},

		checkFormValidity(){
			this.etats.anneeFabrication = (this.hydrant.anneeFabrication > 0 || !this.hydrant.anneeFabrication) ? 'valid' : 'invalid';
			this.etats.diametreCanalisation = (this.hydrant.diametreCanalisation > 0 || !this.hydrant.diametreCanalisation) ? 'valid' : 'invalid';
		},

		onReservoirCreated(values) {			  
			this.comboReservoir.push({
				text: values.nom,
				value: values.id
			});
		},

		onNatureDeciChange() {
			if(!this.isDeciPrivee) {
				this.hydrant.surpresse = false;
				this.hydrant.additive = false;
			}
		}
	}
  
};
</script>

<style>
.vertical-bottom {
  display: flex;
  align-items: flex-end
}


</style>