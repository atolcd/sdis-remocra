<template>
	<b-modal 	id="modalErrorMessage" 
				ref="modalErrorMessage" 
				no-close-on-backdrop 
				:title="title"
				ok-title="OK" 
				cancel-title="Annuler" 
				size="lg"
				@hidden="close()" 
				ok-only=true 
				ok-variant="danger"
				@ok="close()" 
				centered 
				header-bg-variant="danger"
				header-text-variant="light">
			<p class="text-danger">{{msg}}</p>
	</b-modal>
</template>

<script>

export default {
	name: 'ErrorMessage',

	data () {
		return {
		}
	},

	props: {

		title: {
			required: false,
			type: String
		},

		msg: {
			required: true,
			type: String
		},
	},

	mounted: function() {
		this.$refs.modalErrorMessage.show();
	},

	methods: {

		close(){
			this.$root.$options.bus.$emit('closed')
		},

	}
};
</script>

<style>
.modal-title {
	font-family: Segoe UI,Roboto,Helvetica Neue,Arial;
	font-weight: 500;
	font-size: 1.25rem;
}

.text-danger {
	font-size: 15px;
}
</style>