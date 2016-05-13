package fr.sdis83.remocra.domain.pdi;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "statut", schema = "pdi")
@RooDbManaged(automaticallyDelete = true)
@RooToString(excludeFields = { "traitements" })
public class Statut {

    @OneToMany(mappedBy = "idstatut", cascade = CascadeType.ALL)
    private Set<Traitement> traitements;
}
