package fr.sdis83.remocra.domain.pdi;

import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "traitement", schema = "pdi")
@RooDbManaged(automaticallyDelete = true)
@RooToString(excludeFields = {"traitementParametres", "idmodele", "idstatut", "traitementCcs"})
public class Traitement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idtraitement")
  private Integer idtraitement;

  @Column(name = "demande")
  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(style = "MM")
  private Date demande;

  @Column(name = "execution")
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(style = "MM")
  private Date execution;

  @OneToMany(mappedBy = "idtraitement", cascade = CascadeType.ALL)
  private Set<TraitementParametre> traitementParametres;
}
