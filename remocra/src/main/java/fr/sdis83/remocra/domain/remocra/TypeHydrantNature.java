package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = {"findTypeHydrantNaturesByActif", "findTypeHydrantNaturesByCode"})
public class TypeHydrantNature implements ITypeReferenceNomActif {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull private String nom;

  @NotNull private String code;

  @NotNull
  @Column(columnDefinition = "boolean default true")
  private Boolean actif;

  @ManyToOne private TypeHydrant typeHydrant;
}
