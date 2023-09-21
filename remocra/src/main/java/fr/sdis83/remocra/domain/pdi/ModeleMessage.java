package fr.sdis83.remocra.domain.pdi;

import java.util.Set;
import javax.persistence.OneToMany;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "modele_message", schema = "pdi")
@RooDbManaged(automaticallyDelete = true)
@RooToString(excludeFields = {"modeleTraitements", "modeleTraitements1"})
public class ModeleMessage {

  @OneToMany(
      mappedBy = "messageSucces",
      cascade = {javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE})
  private Set<ModeleTraitement> modeleTraitements;

  @OneToMany(
      mappedBy = "messageEchec",
      cascade = {javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE})
  private Set<ModeleTraitement> modeleTraitements1;
}
