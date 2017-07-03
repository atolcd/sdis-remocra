package fr.sdis83.remocra.domain.remocra;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Formula;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooJpaActiveRecord(identifierType = OldebVisiteSuitePK.class, versionField = "", table = "oldeb_visite_suite", schema = "remocra")
@RooToString
public class OldebVisiteSuite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "visite", referencedColumnName = "id", nullable = false)
    private OldebVisite visite;

    @ManyToOne
    @JoinColumn(name = "suite", referencedColumnName = "id", nullable = false)
    private TypeOldebSuite suite;

    @Column(name = "date_suite")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateSuite;

    @Column(name = "observation")
    private String observation;

    @Formula("(select tos.nom from remocra.type_oldeb_suite tos where tos.id = suite)")
    private String nomSuite;

}
