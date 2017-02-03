package fr.sdis83.remocra.domain.remocra;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Point;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(inheritanceType = "JOINED", finders = { "findHydrantsByNumero" })
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "numero" }) })
public class Hydrant implements Featurable {

    /**
     * Titre associé à la photo de l'hydrant
     */
    public static String TITRE_PHOTO = "hydrant.jpg";

    public static enum Disponibilite {
        DISPO, INDISPO, NON_CONFORME
    }

    public static enum TYPE_SAISIE {
        LECT, CREA, RECEP, RECO, CTRL, VERIF
    }

    // identifiant / géométrie
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    @Column
    protected String code;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geometrie;

    @Formula("ST_AsGeoJSON(geometrie)")
    private String jsonGeometrie;

    @ManyToOne
    private Tournee tournee;

    // Identification

    @ManyToOne
    private TypeHydrantNature nature;

    @Column
    private String numero;

    @Column
    private Integer numeroInterne;

    @ManyToOne
    private ZoneSpeciale zoneSpeciale;

    // Tracabilité

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateRecep;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateReco;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateContr;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateVerif;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateModification;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateGps;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateAttestation;

    @Column
    private String agent1;

    @Column
    private String agent2;

    @ManyToOne
    private Organisme organisme;

    // Localisation

    @Column
    private String lieuDit;

    @ManyToOne
    private Commune commune;

    @Column
    private String voie;

    @Column
    private String voie2;

    @Column
    private String complement;

    // Element MCO

    @Column
    private Integer anneeFabrication;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hydrant", orphanRemoval = true)
    private Set<HydrantDocument> hydrantDocuments = new HashSet<HydrantDocument>();

    @Column
    private String courrier;

    // Gestionnaire

    @ManyToOne
    private TypeHydrantDomaine domaine;

    @Column
    private String gestPointEau;
    // Point d'attention

    @Column
    private String observation;

    @ManyToMany
    private Set<TypeHydrantAnomalie> anomalies;

    // Disponibilité

    @Enumerated(EnumType.STRING)
    private Disponibilite dispoTerrestre;

    @Enumerated(EnumType.STRING)
    private Disponibilite dispoHbe;

    @Enumerated(EnumType.STRING)
    private Disponibilite dispoAdmin;

    // Autre
    @Override
    public Feature toFeature() {
        Feature feature = new Feature(this.id, this.getJsonGeometrie());
        feature.addProperty("typeHydrantCode", this.getCode());
        feature.addProperty("dispo", this.getDispoTerrestre());
        feature.addProperty("nature", this.getNature().getCode());
        feature.addProperty("numero", this.getNumero());
        feature.addProperty("tournee", this.getTourneeId());
        feature.addProperty("isTourneeRes", this.getTournee() != null ? this.getTournee().getReservation() != null : false);
        return feature;
    }

    public Long getTourneeId() {
        if (this.getTournee() != null) {
            return this.getTournee().getId();
        }
        return null;
    }

    public String getNatureNom() {
        if (this.getNature() != null) {
            return this.getNature().getNom();
        }
        return null;
    }

    public String getCISCommune() {
        if (this.getOrganisme() != null) {
            return this.getOrganisme().getNom();
        }
        return null;
    }

    /**
     * Calcule le numéro de l'hydrant.
     * 
     * @param hydrant
     * @param codeZS
     *            le code de la zone si l'hydrant est en zone spéciale (null
     *            sinon)
     * @return
     */
    public static String computeNumero(Hydrant hydrant) {
        String codeZS = hydrant.getZoneSpeciale() == null ? null : hydrant.getZoneSpeciale().getCode();

        StringBuilder sb = new StringBuilder();
        if ("PIBI".equals(hydrant.getCode())) {
            sb.append(hydrant.getNature().getCode()).append(" ");
            HydrantPibi pibi = (HydrantPibi) hydrant;
            if (pibi.getPena() != null) {
                // Pibi lié à un Pena : on double le code zone ou commune
                sb.append(codeZS != null ? codeZS : hydrant.getCommune().getCode()).append(" ");
            }
        } else {
            sb.append("PN ");
        }

        sb.append(codeZS != null ? codeZS : hydrant.getCommune().getCode());
        return sb.append(" ").append(hydrant.getNumeroInterne()).toString();
    }

    public static Integer computeNumeroInterne(Hydrant hydrant) {
        String codeZS = hydrant.getZoneSpeciale() != null ? hydrant.getZoneSpeciale().getCode() : null;

        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            StringBuffer sb = new StringBuffer("SELECT min(h.numero_interne) FROM remocra.hydrant h WHERE");
            sb.append(" h.code = :code and h.numero_interne > 90000  and ");
            if (codeZS != null) {
                sb.append("h.zone_speciale = :zs");
            } else {
                sb.append("h.commune = :commune and h.zone_speciale is null");
            }
            if (hydrant.getId() != null) {
                sb.append(" and h.id != :id ");
            }
            Query query = Hydrant.entityManager().createNativeQuery(sb.toString()).setParameter("code", hydrant.getCode());
            if (codeZS != null) {
                query.setParameter("zs", hydrant.getZoneSpeciale());
            } else {
                query.setParameter("commune", hydrant.getCommune());
            }
            if (hydrant.getId() != null) {
                query.setParameter("id", hydrant.getId());
            }
            numInterne = Integer.valueOf(query.getSingleResult().toString()) - 1;
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne;
    }

    public static ZoneSpeciale computeZoneSpeciale(Hydrant hydrant) {
        try {
            String codeZS = (String) Hydrant.entityManager()
                    .createNativeQuery("select code from remocra.zone_speciale " + "where :geometrie && geometrie and " + "st_distance(:geometrie, geometrie)<=0")
                    .setParameter("geometrie", hydrant.getGeometrie()).getSingleResult();
            return ZoneSpeciale.findZoneSpecialesByCode(codeZS).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static void setCodeZoneSpecAndNumeros(Hydrant hydrant) {
        // Code
        hydrant.setCode(hydrant.getNature().getTypeHydrant().getCode());

        // Zone Spéciale
        hydrant.setZoneSpeciale(computeZoneSpeciale(hydrant));

        // Si création : attribution d'un numéro en 99999
        if (hydrant.getNumeroInterne() == null || hydrant.getNumeroInterne().intValue() < 1) {
            hydrant.setNumeroInterne(computeNumeroInterne(hydrant));
        }

        // Calcul du numéro
        hydrant.setNumero(Hydrant.computeNumero(hydrant));
    }

    public HydrantDocument getPhoto() {
        HydrantDocument photo = null;
        for (HydrantDocument hydrantDocument : hydrantDocuments) {
            if (TITRE_PHOTO.equals(hydrantDocument.getTitre())) {
                return hydrantDocument;
            }
        }
        return photo;
    }

    public void setPhoto(HydrantDocument photo) {
        HydrantDocument toRemove = null;
        for (HydrantDocument hydrantDocument : hydrantDocuments) {
            if (TITRE_PHOTO.equals(hydrantDocument.getTitre())) {
                toRemove = hydrantDocument;
            }
            break;
        }
        if (toRemove != null) {
            hydrantDocuments.remove(toRemove);
        }
        hydrantDocuments.add(photo);
    }
}
