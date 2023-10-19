<template>
    <div id="formGestionnaireSite"> <!-- Définition page de la pop up de formulaire de gestionnaire -->
        <b-modal name="modalFormGestionnaireSite" id="formGestionnaireSite" v-bind:title="buildedTitle" no-close-on-backdrop cancel-title="Annuler"
            ok-title="Valider" refs="modalFormGestionnaireSite" @cancel="resetFormGestionnaireSite" @ok="handleOk" :clickToClose="false">
                <form id="formGestionnaireSiteId" ref="formGestionnaireSite">
                    <div class="col-md-6" id="formGestionnaire">
                        <b-form-group id="formInputNom" label-for="inputNom" invalid-feedback="Le nom du site est manquant">
                            Nom : *
                            <b-form-input id="inputNom" v-model="nomGestionnaireSite" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="col-md-6" id="formGestionnaire">
                        <b-form-group id="formInputCode" label-for="inputCode" invalid-feedback="Le code du site est manquant">
                            Code : *
                            <b-form-input id="inputCode" v-model="codeGestionnaireSite" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="col-md-6" id="divCBoxActif">
                        <b-form-group id="formCBoxActif" label-for="inputActif">
                            Statut :
                            <b-form-checkbox id="actif" v-model="actifGestionnaireSite">
                                {{this.actifGestionnaireSite ? "Site Actif" : "Site Inactif"}}
                            </b-form-checkbox>
                        </b-form-group>
                    </div>
                    <div>
                        <b-form-group id="formInputGestionnaire" label-for="inputGestionnaire">
                            Gestionnaire :
                            <b-form-select id="comboGestionnaire" v-model="gestionnaireGestionnaireSite" :options="comboGestionnaire" size="sm"></b-form-select>
                        </b-form-group>
                    </div>
                </form>
        </b-modal>
    </div>
</template>

<script>
    import axios from 'axios'
    import _ from 'lodash'

    export default {
        data() {
            return {
                idGestionnaireSite: '',

                nomGestionnaireSite: '',
                codeGestionnaireSite: '',
                gestionnaireGestionnaireSite: null,
                actifGestionnaireSite: '',
                geometrieGestionnaireSite: '',

                comboGestionnaire: [],
                streamChampVide: ' ',

                buildedTitle: '',
            }
        },
        methods: {
            showModalFormGestionnaireSite(idGestionnaireSite_) {
                this.getGestionnaireSiteById(idGestionnaireSite_)
                this.getComboGestionnaire()
            },
            getGestionnaireSiteById(idGestionnaireSite_) {
                if(idGestionnaireSite_!==null){ // Modification du gestionnaire_site idGestionnaireSite_
                    this.idGestionnaireSite = idGestionnaireSite_;
                    axios.get('/remocra/gestionnairesite/' + this.idGestionnaireSite).then(response => {
                        response.data ? (this.nomGestionnaireSite = response.data.nom,
                                                this.codeGestionnaireSite = response.data.code,
                                                this.gestionnaireGestionnaireSite = response.data.idGestionnaire,
                                                this.actifGestionnaireSite = response.data.actif,
                                                this.geometrieGestionnaireSite = response.data.geometrie
                                                ) : null
                        this.buildedTitle = 'Modification de ' + this.nomGestionnaireSite
                        this.$bvModal.show('formGestionnaireSite');
                    })
                } else { // Création d'un gestionnaire_site
                    this.resetFormGestionnaire();
                    this.buildedTitle="Création d'un site";
                    this.actifGestionnaire = true;
                    this.$bvModal.show('formGestionnaireSite');
                }
            },
            getComboGestionnaire() {
                axios.get('/remocra/gestionnaire/findAllGestionnaires').then(response => {
                    if(response.data) {
                        _.forEach(response.data, item => {
                            this.comboGestionnaire.push(
                                {text: item.nom,
                                value: item.id})
                        })
                    }
                })
                this.comboGestionnaire.push(
                    {text: this.streamChampVide,
                    value: null})
            },
            handleOk(bvModalEvt) {
                bvModalEvt.preventDefault()
                if(!document.getElementById('formGestionnaireSiteId').checkValidity()) {
                    document.getElementById('formGestionnaireSiteId').classList.add('vas-validated');
                } else {
                    this.handleSubmit()
                }
            },
            handleSubmit() {
                if (!this.checkFormValidity()){
                    return;
                }
                let formData = new FormData();
                formData.append('gestionnaireSite', JSON.stringify({
                    nom: this.nomGestionnaireSite,
                    code: this.codeGestionnaireSite,
                    actif: this.actifGestionnaireSite,
                    idGestionnaire: this.gestionnaireGestionnaireSite,
                    geometrie: this.geometrieGestionnaireSite
                }))
                axios.post("/remocra/gestionnairesite/updateGestionnaireSite/" + this.idGestionnaireSite, formData).then(() => {
                    this.$emit('gestionnaireSiteUpdate'),
                    this.$nextTick(() => {
                        this.resetFormGestionnaireSite();
                        this.$bvModal.hide('formGestionnaireSite')
                    })
                })
            },
            resetFormGestionnaireSite() {
                this.$refs.formGestionnaireSite.reset();
                this.idGestionnaireSite = null;
                this.nomGestionnaireSite = '';
                this.codeGestionnaireSite = '';
                this.gestionnaireGestionnaireSite = null;
                this.actifGestionnaireSite = '';

                this.comboGestionnaire = [];
            },
            checkFormValidity() {
                return this.$refs.formGestionnaireSite.checkValidity();
            }
        }
    }
</script>