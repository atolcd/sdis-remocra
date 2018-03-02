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
        UTILISATEUR_FILTER_ALL("utilisateur.filter.*"), UTILISATEUR_FILTER_ORGANISME_UTILISATEUR("utilisateur.filter.organisme.utilisateur"),
        REFERENTIELS("referentiels"),
        DOCUMENTS("documents"),

        // Module Alertes / adresses
        ADRESSES("adresses"),
        ALERTES_EXPORT("alertes.export"),
        DEPOT_DELIB("depot.delib"),

        // Module DFCI
        DFCI("dfci"),DFCI_EXPORTATLAS("dfci.exportatlas"),
        DEPOT_RECEPTRAVAUX("depot.receptravaux"),

        // Module PEI
        HYDRANTS("hydrants"),
        HYDRANTS_RECONNAISSANCE("hydrants.reconnaissance"), HYDRANTS_CONTROLE("hydrants.controle"),
        HYDRANTS_NUMEROTATION("hydrants.numerotation"), HYDRANTS_MCO("hydrants.mco"),
        INDISPOS("indisponibilite.temporaire"),
        TOURNEE("tournee"), TOURNEE_RESERVATION("tournee.reservation"),
        HYDRANTS_TRAITEMENT("hydrants.traitement"),
        HYDRANTS_EXPORT_NON_NUM("hydrants.exportnonnum"),
        DEPOT_DECLAHYDRANT("depot.declahydrant"),

        HYDRANTS_PRESCRIT("hydrants.prescrit"),

        // Module Permis
        PERMIS("permis"), PERMIS_DOCUMENTS("permis.documents"), PERMIS_TRAITEMENT("permis.traitement"),

        // Module RCCI
        RCI("rci"),

        // Module Risques
        RISQUES_KML("risques.kml"),

        // Module Débroussaillement
        OLDEB("obligation.debroussaillement"),

        // Module Cartographie
        CARTOGRAPHIES("cartographies");

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
