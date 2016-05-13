package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Geometry;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(versionField = "", finders = { "findZoneSpecialesByCode" })
public class ZoneSpeciale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    protected String code;

    @NotNull
    protected String nom;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometrie;
}
