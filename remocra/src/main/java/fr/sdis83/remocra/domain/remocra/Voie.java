package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Geometry;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "")
@RooJson
public class Voie {

  /** Source de la voie */
  public enum Source {
    ROUTE,
    PISTE
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Size(min = 0, max = 255)
  private String nom;

  @NotNull
  @Size(min = 0, max = 255)
  private String motClassant;

  @NotNull
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Geometry geometrie;

  @NotNull @ManyToOne private Commune commune;

  @Enumerated(EnumType.STRING)
  @NotNull
  private Source source;
}
