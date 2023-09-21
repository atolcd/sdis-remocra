package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/** Permet de stocker les états successifs des synchronisations. */
@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
public class Synchronisation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull @ManyToOne Thematique thematique;

  @NotNull private Boolean succes;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  Date dateSynchro;
}
