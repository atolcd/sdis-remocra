package fr.sdis83.remocra.domain.remocra;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class HydrantVisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @NotNull
    private Hydrant hydrant;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    @NotNull
    @Column(name = "date", columnDefinition = "timestamp without time zone NOT NULL default now()")
    private Date date;

    @ManyToOne
    private TypeHydrantSaisie type;

    @Column
    private Boolean ctrl_debit_pression;

    @Column
    private String agent1;

    @Column
    private String agent2;

}
