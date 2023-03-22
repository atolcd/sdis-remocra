<template>
    <div id="modalFormContactGestionnaire"> <!-- Définition page de la pop up de formulaire de contact -->
        <b-modal name="modalFormContactGestionnaire" id="formContactGestionnaire" v-bind:title="buildedTitle" no-close-on-backdrop cancel-title="Annuler"
        ok-title="Enregistrer" refs="modalFormContactGestionnaire" :clickToClose="false" @ok="handleOk">
            <form id="formContactGestionnaireId" ref="formContactGestionnaire">
                <div class="grilleFormulaire"> <!-- Définition grid formulaire -->
                    <div class="cellAppartenance"> 
                        <a>Contact associé à</a>
                        <b-form-group label-for="appartenance">
                            <b-form-input id="appartenance" v-model="appartenance" type="text" size="sm" disabled></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellFonction"> 
                        <a>Fonction *</a>
                        <b-form-group label-for="fonction" invalid-feedback="La fonction doit être renseignée">
                            <b-form-input id="fonction" v-model="fonction" type="text" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellCivilite"> 
                        <a>Civilité *</a>
                        <b-form-group label-for="civilite">
                            <b-form-select id="civilite" v-model="civilite" :options="civilites" size="sm" required></b-form-select>
                        </b-form-group>
                    </div>
                    <div class="cellNom"> 
                        <a>Nom *</a>
                        <b-form-group label-for="nom" invalid-feedback="Le nom doit être renseigné">
                            <b-form-input id="nom" v-model="nom" type="text" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellPrenom"> 
                        <a>Prénom *</a>
                        <b-form-group label-for="prenom" invalid-feedback="Le prénom doit être renseigné">
                            <b-form-input id="prenom" v-model="prenom" type="text" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellNumeroVoie"> 
                        <a>Numéro</a>
                        <b-form-group label-for="numeroVoie">
                            <b-form-input id="numeroVoie" v-model="numeroVoie" type="number" size="sm"></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellSuffixeVoie"> 
                        <a>Suffixe</a>
                        <b-form-group label-for="suffixeVoie">
                            <b-form-input id="suffixeVoie" v-model="suffixeVoie" type="text" size="sm"></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellVoie"> 
                        <a>Voie *</a>
                        <b-form-group label-for="voie" invalid-feedback="La voie doit être renseignée">
                            <b-form-input id="voie" v-model="voie" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellLieuDit"> 
                        <a>Lieu dit</a>
                        <b-form-group label-for="lieuDit">
                            <b-form-input id="lieuDit" v-model="lieuDit" type="text" size="sm"></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellCP"> 
                        <a>Code Postal *</a>
                        <b-form-group label-for="cp" invalid-feedback="Le code postal doit être renseigné">
                            <b-form-input id="cp" v-model="codePostal" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellVille"> 
                        <a>Ville *</a>
                        <b-form-group label-for="ville" invalid-feedback="La ville doit être renseignée">
                            <b-form-input id="ville" v-model="ville" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellPays"> 
                        <a>Pays *</a>
                        <b-form-group label-for="pays" invalid-feedback="Le pays doit être renseigné">
                            <b-form-input id="pays" v-model="pays" size="sm" required></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellTelephone"> 
                        <a>Téléphone</a>
                        <b-form-group label-for="telephone">
                            <b-form-input id="telephone" v-model="telephone" size="sm"></b-form-input>
                        </b-form-group>
                    </div>
                    <div class="cellMail"> 
                        <a>E-mail *</a>
                        <b-form-group label-for="mail" invalid-feedback="L'adresse mail est absente ou invalide">
                            <b-form-input id="mail" v-model="email" size="sm" type="email" required></b-form-input> // Utilise les rêgles d'HTML5
                        </b-form-group>
                    </div>
                    <div class="cellContacter"> 
                        <a>A contacter pour</a>
                        <b-form-group label-for="contacter">
                            <b-card>
                                <b-form-checkbox-group v-model="contactRoles" :options="roles" stacked></b-form-checkbox-group>
                            </b-card>
                        </b-form-group>
                    </div>
                </div>
            </form>
        </b-modal>
    </div>
</template>

<script>
    import axios from 'axios'
    import _ from 'lodash'

    export default {
        data(){
            return{
                buildedTitle:'Titre personnalisé',

                idContact:'',
                idAppartenance:'',
                appartenance:'',
                fonction:'',
                civilite:'M',
                nom:'',
                prenom:'',
                numeroVoie:'',
                suffixeVoie:'',
                lieuDit:'',
                voie:'',
                codePostal:'',
                ville:'',
                pays:'',
                telephone:'',
                email:'',

                roles: [],
                contactRoles:[],
                formatedContactRoles:[],

                civilites: [{
                    value: 'M',
                    text: 'M'
                }, {
                    value: 'MMe',
                    text: 'MME'
                }],
            }
        },

        methods:{
            showModalFormContactGestionnaire(idContact_, idGestionnaire_, nomGestionnaire_) {
                this.roles = []
                axios.get('roles').then(response => {
                    _.forEach(response.data.data, role => {
                        this.roles.push({
                            text: role.nom,
                            value: role.id
                        })
                    })
                }).catch(function(error) {
                    console.error('error recup liste globale roles', error);
                })

                if(idContact_!==null){ // Modification contact
                    this.idContact=idContact_;
                    axios.get('/remocra/contact/contactInfos/'+this.idContact).then(response => {
                        // Infos contact
                        response.data.data ? (this.appartenance = nomGestionnaire_,
                                                this.idAppartenance = response.data.data.idAppartenance,
                                                this.fonction = response.data.data.fonction,
                                                this.civilite = response.data.data.civilite,
                                                this.nom =  response.data.data.nom,
                                                this.prenom = response.data.data.prenom,
                                                this.numeroVoie = response.data.data.numeroVoie,
                                                this.suffixeVoie = response.data.data.suffixeVoie,
                                                this.voie = response.data.data.voie,
                                                this.lieuDit = response.data.data.lieuDit,
                                                this.codePostal = response.data.data.codePostal,
                                                this.ville = response.data.data.ville,
                                                this.pays = response.data.data.pays,
                                                this.telephone = response.data.data.telephone,
                                                this.email = response.data.data.email) : null
                        // Role Contact
                        this.getContactRoles(this.idContact)
                        // Titre pop up
                        this.buildedTitle='Modification de '+this.nom;
                    }).catch(function(error){
                        console.error('error recup infos contact form',error);
                    })
                    this.$bvModal.show('formContactGestionnaire')
                }
                else{ // Création contact
                    this.resetFormContact();
                    this.appartenance=nomGestionnaire_
                    this.idAppartenance=idGestionnaire_
                    this.buildedTitle="Création d'un contact";
                    this.$bvModal.show('formContactGestionnaire')
                }
            },
            getContactRoles(idContact_){
                axios.get('/remocra/contact/contactRoles/'+idContact_).then(response => {
                    this.contactRoles = response.data.data
                }).catch(function(error){
                    console.error('error recup contact roles', error);
                })
            },
            handleOk(bvModalEvt){
                bvModalEvt.preventDefault()
                if(document.getElementById('formContactGestionnaireId').checkValidity() === false){
                    document.getElementById('formContactGestionnaireId').classList.add('was-validated');
                }
                else{
                    this.handleSubmit()
                }
            },
            handleSubmit(){
                if(!this.checkFormValidity()){
                    return;
                }
                var formData = new FormData();
                formData.append('contact', JSON.stringify({
                    appartenance: "GESTIONNAIRE",
                    idAppartenance: this.idAppartenance.toString(),
                    fonction: this.fonction,
                    civilite: this.civilite,
                    nom: this.nom,
                    prenom: this.prenom,
                    numeroVoie: this.numeroVoie,
                    suffixeVoie: this.suffixeVoie,
                    lieuDit: this.lieuDit,
                    voie: this.voie,
                    codePostal: this.codePostal,
                    ville: this.ville,
                    pays: this.pays,
                    telephone: this.telephone,
                    email: this.email,
                }))
                formData.append('role', this.contactRoles)
                var url = this.idContact !== null ? '/remocra/contact/updateContact/'+this.idContact : '/remocra/contact/createContact';
                axios.post(url, formData).then(response => {
                    this.$emit('contactUpdate'); // confirme au parent qu'il y a eu un changement en base
                    this.$nextTick(() => {
                        this.resetFormContact();
                        this.$bvModal.hide('formContactGestionnaire');
                    })
                }).catch(function(error) {
                    console.error('error upsert contact', error);
                });
            },
            resetFormContact() {
                // permet de vider les champs du form pour toujours partir d'une base saine
                this.$refs.formContactGestionnaire.reset();
                document.getElementById('formContactGestionnaireId').classList.remove('was-validated')
                this.idContact = null
                this.appartenance = ''
                this.idAppartenance = ''
                this.fonction = ''
                this.civilite = 'M'
                this.nom = ''
                this.prenom = ''
                this.numeroVoie = ''
                this.suffixeVoie = ''
                this.voie = ''
                this.lieuDit = ''
                this.pays = ''
                this.codePostal = ''
                this.ville = ''
                this.telephone = ''
                this.email = ''
                this.contactRoles = []
            },
            checkFormValidity(){
                const valid = this.$refs.formContactGestionnaire.checkValidity();
                return valid
            },
        }
    }
</script>

<style>
    div#modalFormContactGestionnaire___BV_modal_content_{width:unset;min-width:unset !important;}
    #modalFormContactGestionnaire .modal-dialog.modal-md{min-width: 920px;max-width:75%;}

    .grilleFormulaire {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        grid-template-rows: repeat(6, 1fr), minmax(20px, auto);
        grid-column-gap: 0px;
        grid-row-gap: 0px;
    }
    .cellAppartenance { grid-area: 1 / 1 / 2 / 3; }
    .cellFonction { grid-area: 1 / 3 / 2 / 5; }
    .cellCivilite { grid-area: 2 / 1 / 3 / 2; }
    .cellNom { grid-area: 2 / 2 / 3 / 3; }
    .cellPrenom { grid-area: 2 / 3 / 3 / 4; }
    .cellNumeroVoie { grid-area: 3 / 1 / 4 / 2; }
    .cellSuffixeVoie { grid-area: 3 / 2 / 4 / 3; }
    .cellVoie { grid-area: 3 / 3 / 4 / 4; }
    .cellLieuDit { grid-area: 3 / 4 / 4 / 5; }
    .cellCP { grid-area: 4 / 1 / 5 / 2; }
    .cellVille { grid-area: 4 / 2 / 5 / 4; }
    .cellPays { grid-area: 4 / 4 / 5 / 5; }
    .cellTelephone { grid-area: 5 / 1 / 6 / 3; }
    .cellMail { grid-area: 5 / 3 / 6 / 5; }
    .cellContacter { grid-area: 6 / 1 / 7 / 5; }
    
    .form-control, .custom-select{width:99% !important;margin:auto !important;}

    .cellContacter .card-body{max-height: 300px;overflow-y: auto;}
</style>