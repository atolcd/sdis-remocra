package fr.sdis83.remocra.domain.remocra;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = {"findTypeHydrantAnomaliesByActif", "findTypeHydrantAnomaliesByCode"})
public class TypeHydrantAnomalie implements ITypeReferenceNomActif {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @NotNull private String nom;

  @NotNull private String code;

  @Column(columnDefinition = "boolean default TRUE")
  private Boolean actif;

  @ManyToOne private TypeHydrantCritere critere;

  @Column private String commentaire;

  @OneToMany(mappedBy = "anomalie", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<TypeHydrantAnomalieNature> anomalieNatures = new HashSet<TypeHydrantAnomalieNature>();
}
