package fr.sdis83.remocra.domain.remocra;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "type_oldeb_suite", schema = "remocra")
@RooToString
public class TypeOldebSuite {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @OneToMany(mappedBy = "suite")
  private Set<OldebVisiteSuite> oldebVisiteSuites;

  @Column(name = "actif")
  private Boolean actif;

  @Column(name = "code")
  @NotNull
  private String code;

  @Column(name = "nom")
  @NotNull
  private String nom;
}
