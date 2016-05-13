package fr.sdis83.remocra.domain.pdi;

import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "vue_telechargements", schema = "pdi", finders = { "findTelechargementsByCodeEquals" })
public class Telechargement {

    @Id
    private Integer idtraitement;

    private String code;

    private String ressource;
}
