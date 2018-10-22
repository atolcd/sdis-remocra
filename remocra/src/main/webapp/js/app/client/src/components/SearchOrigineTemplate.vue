<template>
<div>
  <span>{{ item }}</span>
</div>
</template>

<script>
export default {
  name: 'OrigineTemplate',
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
