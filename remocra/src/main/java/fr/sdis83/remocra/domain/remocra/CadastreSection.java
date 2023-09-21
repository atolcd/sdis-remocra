package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "")
@RooJson
public class CadastreSection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @OneToMany(mappedBy = "section")
  private Set<CadastreParcelle> cadastreParcelles;

  @NotNull @ManyToOne private Commune commune;

  @NotNull
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Geometry geometrie;

  @Column(name = "numero")
  @NotNull
  private String numero;
}
