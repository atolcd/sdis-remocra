<template>
  <div>
    <b-modal id="modaleInfo" title="Informations" ok-only v-if="feature !== null">
      <table>
        <tr v-for="(property,index) in getProperties()" :key="index">
          <td class="propertyName">{{property | capitalize | removeUnderscore}} : </td>
          <td class="propertyValue">{{feature.properties[property] != null ? feature.properties[property] : "Non renseigné"}}</td>
        </tr>
      </table>
    </b-modal>
  </div>
</template>

<script>
export default {
  name: 'ModaleInfo',

  data() {
    return {
      hiddenProperties: ["id", "img"]
    }
  },

  props: {
    feature: {
      type: Object
    },
  },

  filters: {
    capitalize: function (value) {
      if (!value) return ''
      value = value.toString()
      return value.charAt(0).toUpperCase() + value.slice(1)
    },

    removeUnderscore: function(value) {
      return value.replaceAll('_', ' ');
    }
  },

  methods: {

    /**
      * Retourne la liste des propriétés d'une feature
      * Les propriétés listées dans hiddenProperties sont ignorées, ainsi que les propriétés étant des tableaux
      */
    getProperties() {
      return Object.keys(this.feature.properties).filter(p => this.hiddenProperties.indexOf(p) == -1 && !Array.isArray(this.feature.properties[p]));
    }
  }
};
</script>

<style scoped>

table {
  margin-left: auto;
  margin-right: auto;
}

.propertyName {
  font-size: 14px;
  font-weight: bold;
  padding-right: 20px;
}

.propertyValue {
  font-size: 14px;
}
</style>
