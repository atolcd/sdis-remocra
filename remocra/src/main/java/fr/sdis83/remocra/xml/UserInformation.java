package fr.sdis83.remocra.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import fr.sdis83.remocra.security.AccessRight;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(name = "user")
public class UserInformation {

    private List<AccessRight> rights;

    public UserInformation() {
        //
    }

    @XmlElement(name = "right")
    public List<AccessRight> getRights() {
        return rights;
    }

    public void setRights(List<AccessRight> rights) {
        this.rights = rights;
    }

}
