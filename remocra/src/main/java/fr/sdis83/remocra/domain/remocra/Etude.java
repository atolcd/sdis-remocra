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
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class Etude {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  // @NotNull
  @Column(name = "date_maj", columnDefinition = "timestamp without time zone")
  private Date dateMiseAJour;

  @ManyToOne @NotNull private TypeEtude type;

  @ManyToOne @NotNull private TypeEtudeStatut statut;

  @Column private String nom;

  @Column private String description;

  @Column private String numero;
}
