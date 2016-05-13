package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import com.vividsolutions.jts.geom.Geometry;

@RooJavaBean
@RooJpaActiveRecord(versionField = "")
public class ZoneCompetence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    protected String code;

    @Column
    protected String nom;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometrie;

}
