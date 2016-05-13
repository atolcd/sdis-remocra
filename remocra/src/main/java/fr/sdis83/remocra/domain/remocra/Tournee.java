package fr.sdis83.remocra.domain.remocra;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Tournee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date debSync;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date lastSync;

    @Formula("(select ST_Envelope(ST_extent(h.geometrie)) from remocra.hydrant h where h.tournee = id)")
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometrie;

    @ManyToOne
    private Organisme affectation;

    @ManyToOne
    private Utilisateur reservation;

    @OneToMany(mappedBy = "tournee")
    private Set<Hydrant> hydrants;

    // @Formula("(select count(*) from remocra.hydrant h where h.tournee = id)")
    private Integer hydrantCount;

    // @Formula("(case when deb_sync is null then 0 else" +
    // " case when (select count(*) from remocra.hydrant h where h.tournee = id) = 0 then 100 else"
    // + " (select count(*) from remocra.hydrant h where h.tournee = id"
    // +
    // " and date_trunc('day', deb_sync) <= greatest(date_trunc('day', h.date_contr), date_trunc('day', h.date_reco),"
    // +
    // " date_trunc('day', h.date_recep), date_trunc('day', h.date_verif))) * 100 / (select count(*) from remocra.hydrant h where h.tournee = id) "
    // + " end end)")
    private Integer etat;

    @PreRemove
    protected void onPreRemove() {
        for (Hydrant hydrant : this.hydrants) {
            hydrant.setTournee(null);
        }
    }
}
