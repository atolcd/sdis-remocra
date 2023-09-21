package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Geometry;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "")
@RooJson
public class CadastreParcelle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "section", referencedColumnName = "id", nullable = false)
  private CadastreSection section;

  @NotNull
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Geometry geometrie;

  @Formula("ST_AsGeoJSON(geometrie)")
  private String jsonGeometrie;

  @Column(name = "numero")
  @NotNull
  private String numero;
}
