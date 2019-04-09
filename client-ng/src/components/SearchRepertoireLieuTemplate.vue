<template>
<div>
  <span>{{ item.libelle }} ({{item.origine}})</span>
</div>
</template>

<script>
export default {
  name: 'RepertoireTemplate',
  props: {
    item: {
      required: true
    },
    searchText: {
      required: true
    }
  }
}
</script>
