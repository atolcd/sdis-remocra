package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findTypeDroitsByCode" })
@RooJson
public class TypeDroit {

    public static enum TypeDroitEnum {

        // Général
        UTILISATEUR_FILTER_ALL_R("utilisateur.filter.*_R"),
        UTILISATEUR_FILTER_ALL_C("utilisateur.filter.*_C"),
        UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_R("utilisateur.filter.organisme.utilisateur_R"),
        UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C("utilisateur.filter.organisme.utilisateur_C"),
        REFERENTIELS_C("referentiels_C"),
        TRAITEMENTS_C("traitements_C"),
        DOCUMENTS_R("documents_R"),
        DOCUMENTS_C("documents_C"),

        // Module Alertes / adresses
        ADRESSES_C("adresses_C"),
        ALERTES_EXPORT_C("alertes.export_C"),
        DEPOT_DELIB_C("depot.delib_C"),

        // Module DFCI
        DFCI_R("dfci_R"),
        DFCI_EXPORTATLAS_C("dfci.exportatlas_C"),
        DEPOT_RECEPTRAVAUX_C("depot.receptravaux_C"),

        // Module PEI
        HYDRANTS_R("hydrants_R"),
        HYDRANTS_C("hydrants_C"),
        HYDRANTS_D("hydrants_D"),
        HYDRANTS_DEPLACEMENT_C("hydrants.deplacement_C"),
        HYDRANTS_ANALYSE_C("hydrants.analyse_C"),
        HYDRANTS_RECEPTION_C("hydrants.reception_C"),
        HYDRANTS_RECONNAISSANCE_C("hydrants.reconnaissance_C"),
        HYDRANTS_CONTROLE_C("hydrants.controle_C"),
        HYDRANTS_VERIFICATION_C("hydrants.verification_C"),
        HYDRANTS_NUMEROTATION_R("hydrants.numerotation_R"),
        HYDRANTS_NUMEROTATION_C("hydrants.numerotation_C"),
        HYDRANTS_MCO_C("hydrants.mco_C"),
        INDISPOS_R("indisponibilite.temporaire_R"),
        INDISPOS_C("indisponibilite.temporaire_C"),
        INDISPOS_U("indisponibilite.temporaire_U"),
        INDISPOS_D("indisponibilite.temporaire_D"),
        TOURNEE_R("tournee_R"),
        TOURNEE_C("tournee_C"),
        TOURNEE_RESERVATION_D("tournee.reservation_D"),
        HYDRANTS_EXPORT_NON_NUM_C("hydrants.exportnonnum_C"),
        HYDRANTS_TRAITEMENT_C("hydrants.traitement_C"),
        DEPOT_DECLAHYDRANT_C("depot.declahydrant_C"),
        HYDRANTS_PRESCRIT_R("hydrants.prescrit_R"),
        HYDRANTS_PRESCRIT_C("hydrants.prescrit_C"),

        // Module Permis
        PERMIS_R("permis_R"),
        PERMIS_C("permis_C"),
        PERMIS_DOCUMENTS_C("permis.documents_C"),
        PERMIS_TRAITEMENT_C("permis.traitement_C"),

        // Module RCCI
        RCI_C("rci_C"),

        // Module Risques
        RISQUES_KML_R("risques.kml_R"),
        RISQUES_KML_C("risques.kml_C"),

        // Module Débroussaillement
        OLDEB_R("obligation.debroussaillement_R"),
        OLDEB_C("obligation.debroussaillement_C"),
        OLDEB_U("obligation.debroussaillement_U"),
        OLDEB_D("obligation.debroussaillement_D"),

        // Module Cartographie
        CARTOGRAPHIES_C("cartographies_C");

        private final String value;

        TypeDroitEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true)
    private String code;

    private String nom;

    private String description;

    private String categorie;

    public TypeDroitEnum getValue() {
        try {
            return TypeDroitEnum.valueOf(code);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static TypeDroit findByValue(TypeDroitEnum value) {
        TypedQuery<TypeDroit> findTypeDroitByCode = TypeDroit.findTypeDroitsByCode(value.getValue());
        TypeDroit singleResult;
        try {
            singleResult = findTypeDroitByCode.getSingleResult();
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
        return singleResult;
    }

}
