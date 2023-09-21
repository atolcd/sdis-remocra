package fr.sdis83.remocra.domain.remocra;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = {"findTypeHydrantMarquesByActif", "findTypeHydrantMarquesByCode"})
public class TypeHydrantMarque implements ITypeReferenceNomActif {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @NotNull private String nom;

  @NotNull private String code;

  @NotNull
  @Column(columnDefinition = "boolean default true")
  private Boolean actif;

  @OneToMany(mappedBy = "marque")
  private Set<TypeHydrantModele> modeles = new HashSet<TypeHydrantModele>();
}
