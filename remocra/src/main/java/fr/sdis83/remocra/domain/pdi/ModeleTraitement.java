package fr.sdis83.remocra.domain.pdi;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(
    versionField = "",
    table = "modele_traitement",
    schema = "pdi",
    finders = {"findModeleTraitementsByCode"})
@RooDbManaged(automaticallyDelete = true)
@RooToString(
    excludeFields = {"modeleTraitementParametres", "traitements", "messageSucces", "messageEchec"})
public class ModeleTraitement {

  @OneToMany(mappedBy = "idmodele", cascade = CascadeType.ALL)
  private Set<ModeleTraitementParametre> modeleTraitementParametres;

  @OneToMany(mappedBy = "idmodele", cascade = CascadeType.ALL)
  private Set<Traitement> traitements;
}
