package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(
    versionField = "",
    finders = {"findDdeMdpsByCodeEquals"})
@Table(
    name = "dde_mdp",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
public class DdeMdp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull private String code;

  @NotNull @ManyToOne private Utilisateur utilisateur;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  @Column(
      name = "date_demande",
      columnDefinition = "timestamp without time zone NOT NULL default now()")
  private Date dateDemande;
}
