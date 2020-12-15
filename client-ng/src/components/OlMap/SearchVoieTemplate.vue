<template>
<div>
  <span>{{ item.nom }}</span>
</div>
</template>

<script>
export default {
  name: 'VoieTemplate',
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
