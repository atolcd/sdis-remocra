package fr.sdis83.remocra.domain.remocra;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class TypeHydrantAnomalieNature {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne @NotNull private TypeHydrantAnomalie anomalie;

  @ManyToOne @NotNull private TypeHydrantNature nature;

  @Column private Integer valIndispoTerrestre;

  @Column private Integer valIndispoHbe;

  @Column private Integer valIndispoAdmin;

  @ManyToMany(cascade = CascadeType.ALL)
  private Set<TypeHydrantSaisie> saisies = new HashSet<TypeHydrantSaisie>();
}
