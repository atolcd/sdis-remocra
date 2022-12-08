<template>
   <div id="importDocuments" class="container-fluid">
    <div class="row">
      <div class="col-md-12">
        <h1>Import des documents</h1>
      </div>
      <p class="paragraphe">
        Vous intégrez vos documents liés à des points d'eau via cet écran. Pour ce faire, vous devez déposer sur le serveur dans le répertoire 
        d'intégration (Il s'agit d'un paramètre de configuration "Emplacement du dossier d'intégration") :
        <ul class="list">
            <li>
                Un dossier nommé <b>"documents"</b> contenant tous les fichiers à plat à intégrer (pas de répertoire dans ce dossier)
            </li>
            <li>
                Un fichier au format csv nommé <b>data.csv</b> contenant 2 colonnes (document, numeroHydrant), avec respectivement le <b>nom du document</b> 
                (exemple : "mon_fichier.doc") et le <b>numéro d'hydrant</b> auquel il est associé.
            </li>
        </ul>
      </p>
    </div>
    <button type="button" aria-label="Importer tous les documents" @click="importDocument" class="btnImport">Importer tous les documents</button>
    <p class="paragraphe" ref="result">
    </p>
  </div>
</template>

<script>
    import axios from 'axios'
    export default {
        name: 'importDocuments',
        data() {

        },
        mounted: function() {
        },
        methods: {
            importDocument() {
              axios.get('/remocra/documents/import').then((response)=> {
                var listeErreur = response.data.data;

                var result= "";
                if(listeErreur !== null) {
                  Array.from(listeErreur).forEach((element) =>
                   result+= "\n"+element.key +" - "+ element.value
                  )
                  this.$refs.result.innerText = result;
                }

              }).catch(function(error) {
                    console.error(error)
                });
            },
        }
    };
</script>

<style scoped>
h1 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial !important
}

ul li {
    display: list-item;
}

.paragraphe {
  font-size: 1rem;
  margin: 20px;
}

.list {
  margin: 40px;
}

.btnImport {
  display: block;
  margin: auto;
  background-color: #c4050a;
  color: #fff;
  border: solid #c4050a;
  border-radius: 10px;
  padding: 5px 10px;
  font-size: 1rem;
}

.btnImport:hover {
  background-color: #fff;
  color: #c4050a;
}

</style>