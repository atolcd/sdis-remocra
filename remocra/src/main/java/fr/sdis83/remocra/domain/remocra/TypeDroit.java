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
        UTILISATEUR_FILTER_ALL("utilisateur.filter.*"), UTILISATEUR_FILTER_ORGANISME_UTILISATEUR("utilisateur.filter.organisme.utilisateur"),

        REFERENTIELS("referentiels"),

        ADRESSES("adresses"),

        DFCI("dfci"),

        HYDRANTS("hydrants"), HYDRANTS_PRESCRIT("hydrants.prescrit"), HYDRANTS_TRAITEMENT("hydrants.traitement"),

        HYDRANTS_RECONNAISSANCE("hydrants.reconnaissance"), HYDRANTS_CONTROLE("hydrants.controle"),

        HYDRANTS_NUMEROTATION("hydrants.numerotation"), HYDRANTS_MCO("hydrants.mco"),

        TOURNEE("tournee"), TOURNEE_RESERVATION("tournee.reservation"),

        PERMIS("permis"), PERMIS_DOCUMENTS("permis.documents"), PERMIS_TRAITEMENT("permis.traitement"),

        RCI("rci"),
        
        RISQUES_KML("risques.kml"),

        DOCUMENTS("documents"),

        DEPOT_DELIB("depot.delib"), DEPOT_DECLAHYDRANT("depot.declahydrant"), DEPOT_RECEPTRAVAUX("depot.receptravaux"), HYDRANTS_EXPORT_NON_NUM("hydrants.exportnonnum"), ALERTES_EXPORT("alertes.export"),

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
