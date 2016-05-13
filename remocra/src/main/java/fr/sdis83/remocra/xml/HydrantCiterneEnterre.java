package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_CI_ENTERRE)
@XmlSeeAlso({ HydrantCiterneFixe.class })
public class HydrantCiterneEnterre extends HydrantPena {

    private String codeVolConstate;

    private String codeMateriau;

    private Double qAppoint;

    public HydrantCiterneEnterre() {
        super.setCodeNature(HydrantPena.CODE_NATURE_CI_ENTERRE);
    }

    public String getCodeVolConstate() {
        return codeVolConstate;
    }

    public void setCodeVolConstate(String codeVolConstate) {
        this.codeVolConstate = codeVolConstate;
    }

    public String getCodeMateriau() {
        return codeMateriau;
    }

    public void setCodeMateriau(String codeMateriau) {
        this.codeMateriau = codeMateriau;
    }

    public Double getqAppoint() {
        return qAppoint;
    }

    public void setqAppoint(Double qAppoint) {
        this.qAppoint = qAppoint;
    }

}
