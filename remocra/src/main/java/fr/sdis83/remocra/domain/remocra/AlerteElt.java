package fr.sdis83.remocra.domain.remocra;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Geometry;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord()
public class AlerteElt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    private String commentaire;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometrie;

    @NotNull
    @ManyToOne
    private SousTypeAlerteElt sousTypeAlerteElt;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alerteElt", orphanRemoval = true)
    private Set<AlerteEltAno> alerteEltAnos = new HashSet<AlerteEltAno>();

    @ManyToOne(optional = false)
    private Alerte alerte;
}
