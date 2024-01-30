package fr.sdis83.remocra.domain.remocra;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "type_oldeb_caracteristique", schema = "remocra")
@RooToString
public class TypeOldebCaracteristique {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_oldeb_caracteristique_seq")
  @SequenceGenerator(
      allocationSize = 1,
      name = "type_oldeb_caracteristique_seq",
      sequenceName = "type_oldeb_caracteristique_id_seq")
  @Column(name = "id")
  private Long id;

  @ManyToMany(mappedBy = "typeOldebCaracteristiques")
  private Set<Oldeb> oldebs;

  @ManyToOne
  @JoinColumn(name = "categorie", referencedColumnName = "id", nullable = false)
  private TypeOldebCategorieCaracteristique categorie;

  @Column(name = "actif")
  private Boolean actif;

  @Column(name = "code")
  @NotNull
  private String code;

  @Column(name = "nom")
  @NotNull
  private String nom;
}
