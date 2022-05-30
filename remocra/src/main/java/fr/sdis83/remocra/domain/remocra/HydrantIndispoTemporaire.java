package fr.sdis83.remocra.domain.remocra;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "hydrant_indispo_temporaire", schema = "remocra")
@RooJson
@RooToString
public class HydrantIndispoTemporaire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_debut")
    //@DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column (name= "date_fin")
    //@DateTime(Format(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateFin;

    @Column(name = "motif")
    private String motif;

    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "date_rappel_debut")
    //@DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateRappelDebut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_rappel_fin")
    //@DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateRappelFin;

    @ManyToOne
    @JoinColumn(name = "statut", referencedColumnName = "id", nullable = false)
    private TypeHydrantIndispoStatut statut;    
    
    @Column(name= "total_hydrants")
    private Integer totalHydrants;

    @Column(name="bascule_auto_indispo")
    private boolean basculeAutoIndispo;

    @Column(name="bascule_auto_dispo")
    private boolean basculeAutoDispo;

    @Column(name="mel_avant_indispo")
    private boolean melAvantIndispo;

    @Column(name="mel_avant_dispo")
    private boolean melAvantDispo;

    @Column(name = "observation")
    private String observation;
    /*@Temporal(TemporalType.TIMESTAMP)
    //@DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date datePrevDebut;

    @Temporal(TemporalType.TIMESTAMP)
    //@DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date datePrevFin;*/
    //@Transient
   //@Formula("(select count(*) from remocra.hydrant_indispo_temporaire_hydrant ith where ith.indisponibilite = id)")

    @OneToMany
    @JoinTable(name = "hydrant_indispo_temporaire_hydrant", joinColumns = { @JoinColumn(name = "indisponibilite", nullable = false) }, inverseJoinColumns = {
        @JoinColumn(name = "hydrant", nullable = false) })
    private Set<Hydrant> hydrants;

    public String getNomStatut() {
        if (this.getStatut() != null) {
            return this.getStatut().getNom();
        }
        return null;
    }
    //methode pour eviter de mettre le nombre d'hydrants dans la base
    public int getCountHydrant(){
        Set<Hydrant> listeHydrants = this.getHydrants();
        if(listeHydrants!=null) {
            return listeHydrants.size();
        }
        return 0;
    }

    public String getCommune(){
        Set<Hydrant> listeHydrants = this.getHydrants();
        if(listeHydrants!=null) {
            if(listeHydrants.iterator().hasNext()){
                return listeHydrants.iterator().next().getCommune().getNom();
            }
        }
        return "inconnue";
    }

    public Geometry getGeometrie(){
        Geometry geom = null;
        if(this.getHydrants()!=null && this.getHydrants().size()!=0) {
            GeometryFactory geometryFactory = new GeometryFactory();
            ArrayList<Geometry> geoms = new ArrayList<Geometry>();
            Iterator<Hydrant> hydrants = this.getHydrants().iterator();
            while (hydrants.hasNext()) {
                geoms.add(hydrants.next().getGeometrie());
            }
            geom = geometryFactory.buildGeometry(geoms);
        }
        return geom;
    }

}
