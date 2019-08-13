<template>
<div class="Contacts">
  <modalOrganismeContact ref="modalOrganismeContact" :title="title"></modalOrganismeContact>
</div>
</template>

<script>
import modalOrganismeContact from './ModalOrganismeContact.vue'
export default {
  name: 'Contacts',
  components: {
    modalOrganismeContact
  },
  props: {
    id: {
      required: true,
      type: Number
    },
    title: {
      required: false,
      type: String
    },
    nom: {
      required: true,
      type: String
    }
  },
  mounted() {
    this.$refs.modalOrganismeContact.editOrganisme(this.id, this.nom)
  },
  data() {
    return {}
  }
}
</script>

<style>
.Contacts {
  width: 600px;
}
</style>
