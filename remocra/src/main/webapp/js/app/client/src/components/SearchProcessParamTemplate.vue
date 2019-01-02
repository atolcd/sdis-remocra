<template>
<div>
  <span>{{ Object.values(item)[1]}}</span>
</div>
</template>

<script>
export default {
  name: 'SearchProcessParamTemplate',
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
