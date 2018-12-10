package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Geometry;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "", finders = { "findCommunesByCode" })
@RooJson
public class Commune {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 5, min = 5)
    private String insee;

    @NotNull
    private String nom;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometrie;

    @Formula("(SELECT (st_xmin(c.geometrie)) || '|' || (st_ymin(c.geometrie)) || '|' || (st_xmax(c.geometrie)) || '|' || (st_ymax(c.geometrie))  FROM remocra.commune c WHERE c.id = id)")
    private String bbox;


    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean pprif;

    @Column
    private String code;
}
