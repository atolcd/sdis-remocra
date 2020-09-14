package fr.sdis83.remocra.domain.remocra;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class DebitSimultaneMesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "debit_simultane", referencedColumnName = "id", nullable=false)
    private DebitSimultane debitSimultane;

    @Column(name = "debit_requis")
    private Integer debitRequis;

    @Column(name = "debit_mesure")
    private Integer debitMesure;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    @NotNull
    @Column(name = "date_mesure", columnDefinition = "timestamp without time zone NOT NULL default now()")
    private Date dateMesure;

    @Column(name = "debit_retenu")
    private Integer debitRetenu;

    @Column
    private String commentaire;

    @Column
    private Boolean irv;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "debitSimultaneMesure", orphanRemoval = true)
    private DebitSimultaneDocument attestation;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "debit", orphanRemoval = true)
    private Set<DebitSimultaneHydrant> hydrants = new HashSet<DebitSimultaneHydrant>();

}
