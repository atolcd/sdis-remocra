package fr.sdis83.remocra.domain.pdi;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(
    identifierType = TraitementCcPK.class,
    versionField = "",
    table = "traitement_cc",
    schema = "pdi")
@RooDbManaged(automaticallyDelete = true)
@RooToString(excludeFields = {"idtraitement"})
public class TraitementCc {}
