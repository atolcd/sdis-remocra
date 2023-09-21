package fr.sdis83.remocra.domain.pdi;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(
    identifierType = TraitementParametrePK.class,
    versionField = "",
    table = "traitement_parametre",
    schema = "pdi")
@RooDbManaged(automaticallyDelete = true)
@RooJson
@RooToString(excludeFields = {"idparametre", "idtraitement"})
public class TraitementParametre {}
