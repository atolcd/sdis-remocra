package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "oldeb_locataire", schema = "remocra")
@RooToString
public class OldebLocataire {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "organisme")
  private Boolean organisme;

  @Column(name = "raison_sociale")
  private String raisonSociale;

  @Column(name = "civilite")
  private String civilite;

  @Column(name = "nom")
  @NotNull
  private String nom;

  @Column(name = "prenom")
  private String prenom;

  @Column(name = "telephone")
  private String telephone;

  @Column(name = "email")
  private String email;

  @ManyToOne
  @JoinColumn(name = "oldeb", referencedColumnName = "id")
  private Oldeb oldeb;
}
