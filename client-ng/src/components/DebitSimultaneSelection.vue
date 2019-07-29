<template>
	<b-modal 	id="modalDebitSimultaneSelection" 
				ref="modalDebitSimultaneSelection" 
				no-close-on-backdrop 
				:title="modalTitle"
				ok-title="OK" 
				cancel-title="Annuler" 
				size="sm"
				@hidden="close()" 
				header-bg-variant="primary"
				header-text-variant="light"
				@ok="handleOk" 
				centered >

		<div class="row">
			<div class="col-md-6">
				<div v-if="typeSelection === 'mesure'"> Veuillez choisir le débit simultané à afficher: </div>
				<div v-else> Veuillez choisir le débit simultané à supprimer: </div>
			</div>

			<div class="col-md-6">
				<form ref="form" @submit.stop.prevent="handleOk">
					<b-form-select 	id="type"
									v-model="selected"
									:options="comboDebits"
									size="sm"
									:state="formValide"
									required></b-form-select>
				</form>
			</div>
		</div>
	</b-modal>
</template>

<script>
export default {
	name: 'DebitSimultaneSelection',

	data () {
		return {
			selected: null,
			formValide: null
		}
	},

	props: {

		typeSelection: {
			required: false,
			type: String
		},

		debitsData: {
			required: true,
			type: String
		},
	},

	computed: {
		modalTitle: function() {
			if(this.typeSelection == 'mesure') {
				return "Sélection d'un hydrant";
			} else {
				return "Supression d'un hydrant";
			}
		},

		comboDebits: function(){
			return JSON.parse(decodeURI(this.debitsData));
		}
	},

	mounted: function() {
		this.$refs.modalDebitSimultaneSelection.show();
	},

	methods: {

		close(){
			this.$root.$options.bus.$emit('closed')
		},

		handleOk(modalEvent) {
			modalEvent.preventDefault();
			this.handleSubmit();
		},

		handleSubmit() {
			
			if(this.selected !== null && this.typeSelection !== null) {
				this.$root.$options.bus.$emit('debit_simultane_selected', {
					id: this.selected,
					type: this.typeSelection
				});
				this.$nextTick(() => {
					this.$refs.modalDebitSimultaneSelection.hide()
				});
			} else {
				this.formValide = 'invalid';
				return
			}
		}

	}
};
</script>

<style>

</style>