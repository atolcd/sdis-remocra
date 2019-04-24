package fr.sdis83.remocra.domain.remocra;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Point;

@RooJavaBean
@RooToString
public class HydrantAspiration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String numero;

    @Column
    private Boolean normalise;

    @Column
    private Boolean hauteur;

    @ManyToOne
    @NotNull
    private TypeHydrantAspiration typeAspiration;

    @Column
    private Boolean deporte;

    @Column
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geometrie;

    @ManyToOne
    @NotNull
    private HydrantPena pena;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;





}
