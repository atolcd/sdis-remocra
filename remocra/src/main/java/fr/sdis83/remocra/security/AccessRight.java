package fr.sdis83.remocra.security;

import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.springframework.security.core.GrantedAuthority;

/** Stocke les informations de droit pour une clé donnée. */
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessRight implements GrantedAuthority {

  /** */
  private static final long serialVersionUID = 1L;

  @XmlAttribute(required = true, name = "code")
  private TypeDroitEnum typeDroitEnum;

  public AccessRight() {}

  public AccessRight(TypeDroitEnum typeDroitEnum) {
    this.typeDroitEnum = typeDroitEnum;
  }

  public TypeDroitEnum getKey() {
    return this.typeDroitEnum;
  }

  @Override
  public String getAuthority() {
    // Ceci est une autorité complexe = return null et besoin
    // implémentation d'un AccessDecisionManager
    return null;
  }
}
