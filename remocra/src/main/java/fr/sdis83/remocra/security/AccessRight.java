package fr.sdis83.remocra.security;

import java.util.EnumSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.security.core.GrantedAuthority;

import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;

/**
 * Stocke les informations de droit pour une clé donnée.
 * 
 * @author bpa
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessRight implements GrantedAuthority {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public enum Permission {
        CREATE, READ, UPDATE, DELETE;
    }

    @XmlAttribute(required = true, name = "code")
    private TypeDroitEnum typeDroitEnum;

    @XmlElement(required = true)
    private EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);

    public AccessRight() {
    }

    public AccessRight(TypeDroitEnum typeDroitEnum) {
        this.typeDroitEnum = typeDroitEnum;
    }

    public boolean hasPermission(Permission perm) {
        return permissions.contains(perm);
    }

    public AccessRight addPermissions(EnumSet<Permission> perms) {
        permissions.addAll(perms);
        return this;
    }

    public TypeDroitEnum getKey() {
        return this.typeDroitEnum;
    }

    public EnumSet<Permission> getPermissions() {
        return permissions;
    }

    public String permissionsToString() {
        String result = "";
        for (Permission perm : permissions) {
            result += " " + perm.toString();
        }
        return result;
    }

    @Override
    public String getAuthority() {
        // Ceci est une authorité complexe = return null et besoin
        // implémentation d'un AccessDecisionManager
        return null;
    }

}
