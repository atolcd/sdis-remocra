package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "oldeb_propriete", schema = "remocra")
@RooToString
public class OldebPropriete {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "oldeb", referencedColumnName = "id", nullable = false)
  private Oldeb oldeb;

  @ManyToOne
  @JoinColumn(name = "proprietaire", referencedColumnName = "id", nullable = false)
  private OldebProprietaire proprietaire;

  @ManyToOne
  @JoinColumn(name = "residence", referencedColumnName = "id", nullable = false)
  private TypeOldebResidence residence;
}
