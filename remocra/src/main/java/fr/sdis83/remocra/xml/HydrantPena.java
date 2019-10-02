package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ HydrantCiterneEnterre.class, HydrantCoursEau.class, HydrantPlanEau.class, HydrantPuisard.class, HydrantRetenue.class,HydrantReserveIncendie.class,
    HydrantPointAspiration.class, HydrantAspirationIndetermine.class, HydrantPuitPuisard.class, HydrantCiterneAerienne.class, HydrantCiterneEn.class, HydrantChateauEau.class, HydrantPoteauRelais.class })
public abstract class HydrantPena extends Hydrant {

    public static final String CODE_NATURE_CI_FIXE = "CI_FIXE";
    public static final String CODE_NATURE_CI_ENTERRE = "CI_ENTERRE";
    public static final String CODE_NATURE_CE = "CE";
    public static final String CODE_NATURE_PE = "PE";
    public static final String CODE_NATURE_PU = "PU";
    public static final String CODE_NATURE_RE = "RE";
    public static final String CODE_NATURE_RI = "RI";
    public static final String CODE_NATURE_CI_AE = "CI_AE";
    public static final String CODE_NATURE_CI_EN = "CI_EN";
    public static final String CODE_NATURE_PUI = "PUI";
    public static final String CODE_NATURE_PA_I = "PA_I";
    public static final String CODE_NATURE_ASP_I= "ASP_I";
    public static final String CODE_NATURE_CHE = "CHE";
    public static final String CODE_NATURE_PR = "PR";


    private String coordDFCI;

    private String dispoHbe;

    private boolean hbe;

    private boolean illimitee;

    private String capacite;

    private Integer aspirations;
    
    public HydrantPena() {
        //
    }

    public String getCoordDFCI() {
        return coordDFCI;
    }

    public void setCoordDFCI(String coordDFCI) {
        this.coordDFCI = coordDFCI;
    }
    
    public String getDispoHbe() {
        return dispoHbe;
    }

    public void setDispoHbe(String dispoHbe) {
        this.dispoHbe = dispoHbe;
    }

    public boolean isHbe() {
        return hbe;
    }

    public void setHbe(boolean hbe) {
        this.hbe = hbe;
    }
    
    public String getCapacite() {
        return capacite;
    }

    public void setCapacite(String capacite) {
        this.capacite = capacite;
    }

    public Boolean getIllimitee() {
        return illimitee;
    }

    public void setIllimitee(Boolean illimitee) {
        this.illimitee
            = illimitee;
    }
    public Integer getAspirations() {
        return aspirations;
    }

    public void setAspirations(Integer aspirations) {
        this.aspirations = aspirations;
    }

}
