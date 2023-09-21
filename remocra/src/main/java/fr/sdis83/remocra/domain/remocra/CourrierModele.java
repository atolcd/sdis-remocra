package fr.sdis83.remocra.domain.remocra;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "", table = "courrier_modele", schema = "remocra")
@RooDbManaged(automaticallyDelete = true)
public class CourrierModele {}
