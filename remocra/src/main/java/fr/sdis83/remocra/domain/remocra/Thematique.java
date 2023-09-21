package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
public class Thematique implements ITypeReferenceNomActif {

  public static enum ThematiqueEnum {
    DIVERS(0),
    POINTDEAU(1),
    ADRESSES(2),
    PERMIS(3),
    DFCI(4),
    RISQUES(5),
    CARTOTHEQUE(6),
    RCI(7),
    OLD(8);

    private final int value;

    ThematiqueEnum(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column private String nom;

  @Column(unique = true)
  @NotNull
  private String code;

  @NotNull
  @Column(columnDefinition = "boolean default true")
  private Boolean actif;
}
