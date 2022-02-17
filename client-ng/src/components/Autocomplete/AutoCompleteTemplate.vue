<template>
<div class="list-item">
  <span>{{ item.libelle }}</span>
</div>
</template>

<script>
export default {
  name: 'AutoCompleteTemplate',
  props: {
    item: {
      required: true
    },
    searchText: {
      required: true
    }
  }
};
</script>

<style>

</style>
