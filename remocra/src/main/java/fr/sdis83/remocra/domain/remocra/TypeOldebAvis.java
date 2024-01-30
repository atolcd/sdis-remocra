package fr.sdis83.remocra.domain.remocra;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "type_oldeb_avis", schema = "remocra")
@RooToString
public class TypeOldebAvis implements ITypeReferenceNomActif {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_oldeb_avis_seq")
  @SequenceGenerator(
      allocationSize = 1,
      name = "type_oldeb_avis_seq",
      sequenceName = "type_oldeb_avis_id_seq")
  @Column(name = "id")
  private Long id;

  @OneToMany(mappedBy = "avis")
  private Set<OldebVisite> oldebVisites;

  @Column(name = "actif")
  private Boolean actif;

  @Column(name = "code")
  @NotNull
  private String code;

  @Column(name = "nom")
  @NotNull
  private String nom;
}
