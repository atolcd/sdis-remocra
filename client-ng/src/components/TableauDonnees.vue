<template>
<table :id="'tableauDonnees'+criseId" class='table table-bordered table-hover table-sm w-auto'>
  <thead class='thead-light'>
    <tr>
      <th v-for="(colonne, key)  in colonnes" :key="key" v-on:click="sortTable(colonne)">
        {{ colonne }}
        <span class="arrow" v-if="sortColumn==colonne" :class="ascending ? 'asc' : 'dsc'"></span>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr v-for="(ligne, key) in donnees.slice(pageNumber * pageSize, pageNumber * pageSize + pageSize)" :key="key">
      <td v-for="(index, key) in colonnes" :key="key">{{ligne[index]}}</td>
    </tr>
  </tbody>
  <tfoot>
    <div class='pagination' v-if='donnees.slice(pageNumber * pageSize, pageNumber * pageSize + pageSize).length!=0'>
      <div class='btn-group'>
        <button @click="pageNumber=0" :disabled='pageNumber==0' class='btn'>
          <img src='/remocra/static/img/resultset_first.png' alt='first' />
        </button>
        <button @click="pageNumber--" :disabled='pageNumber==0' class='btn'>
          <img src='/remocra/static/img/resultset_previous.png' alt='previous' />
        </button>
      </div>
      <p class='text-center'>Page {{pageNumber+1}}/{{Math.ceil(donnees.length/pageSize)}}</p>
      <div class='btn-group'>
        <button @click="pageNumber++" :disabled='pageNumber >= Math.ceil(donnees.length/pageSize) -1' class='btn'>
          <img src='/remocra/static/img/resultset_next.png' alt='next' />
        </button>
        <button @click="pageNumber = Math.ceil(donnees.length/pageSize) -1" :disabled='pageNumber >= Math.ceil(donnees.length/pageSize) -1' class='btn'>
          <img src='/remocra/static/img/resultset_last.png' alt='last' />
        </button>
      </div>
    </div>
  </tfoot>
</table>
</template>

<script>
import _ from 'lodash'
export default {
  name: 'TableauDonnees',
  data() {
    return {
      colonnes: [],
      donnees: [],
      pageNumber: 0,
      donneesPaginees: [],
      ascending: false,
      sortColumn: '',
      pageSize: 15
    }
  },
  props: {
    criseId: {
      required: true,
      type: Number
    }
  },
  methods: {
    eventDrawTableau(header, data) {
      var self = this
      this.colonnes = []
      this.donnees = data
      this.pageNumber = 0
      this.sortColumn = ''
      this.ascending = false
      _.forEach(header, function(item) {
        self.colonnes.push(item.header)
      })
    },
    sortTable(col) {
      if (this.sortColumn === col) {
        this.ascending = !this.ascending
      } else {
        this.ascending = true
        this.sortColumn = col
      }
      var ascending = this.ascending
      this.donnees.sort(function(a, b) {
        // Gestion des cellules sans valeur
        a[col] = (a[col] === null) ? '' : a[col]
        b[col] = (b[col] === null) ? '' : b[col]
        if (a[col] === '') {
          return 1
        }
        if (b[col] === '') {
          return -1
        }
        // Tri
        if (a[col] > b[col]) {
          return ascending ? 1 : -1
        } else if (a[col] < b[col]) {
          return ascending ? -1 : 1
        }
        return 0
      })
      this.pageNumber = 0
    },
    setPageSize(size) {
      this.pageSize = size
    }
  }
}
</script>

<style scoped>
.table {
  table-layout: fixed;
  position: relative;
  min-width: 100%;
}

.table tfoot {
  position: absolute;
  right: 10px;
  bottom: -30px;
  text-align: right;
}

.table.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table th {
  cursor: pointer;
}

.table .pagination button {
  height: 70%;
  border: none;
}

.table .pagination p {
  margin-left: 10px;
  margin-right: 10px;
  margin-bottom: 0;
}

.table td {
  font-size: 10px;
  padding: 3px;
}

.table .arrow {
  display: inline-block;
  vertical-align: middle;
  width: 0;
  height: 0;
  margin-left: 5px;
  opacity: 0.66;
}

.table .arrow.asc {
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-bottom: 4px solid #000;
}

.table .arrow.dsc {
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 4px solid #000;
}
</style>
