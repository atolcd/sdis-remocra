package fr.sdis83.remocra.domain.pdi;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(
    versionField = "",
    table = "modele_traitement_parametre",
    schema = "pdi",
    finders = {
      "findModeleTraitementParametresByNom",
      "findModeleTraitementParametresByNomAndIdmodele"
    })
@RooDbManaged(automaticallyDelete = true)
@RooJson
@RooToString(excludeFields = {"traitementParametres", "idmodele"})
public class ModeleTraitementParametre {

  @OneToMany(mappedBy = "idparametre", cascade = CascadeType.ALL)
  private Set<TraitementParametre> traitementParametres;
}
