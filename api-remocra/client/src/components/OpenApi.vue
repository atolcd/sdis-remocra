<template>
  <div class="openapi">
    <section class="page">
      <div id="openapi"/>
    </section>
  </div>
</template>

<script>
// https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/installation.md
import SwaggerUI from 'swagger-ui'

export default {
  name: 'OpenApi',

  mounted() {
    // https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration/
    SwaggerUI({
      dom_id: '#openapi',
      url: '/openapi.json',
      defaultModelsExpandDepth: 0,
      docExpansion: 'none',
      filter: false,
      showCommonExtensions: false
    })
  }
}
</script>

<style scoped>
* {
  text-align: left;
}
>>> .swagger-ui .col {
  width: auto;
}
</style>
