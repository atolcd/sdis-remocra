package fr.sdis83.remocra.domain.remocra;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooJson
public class HistoVerificationHydraulique {
    private String numero;
    private Integer debitNM1;
    private Integer debitMaxNM1;
    private Double pressionNM1;
    private Double pressionDynNM1;
    private Double pressionDynDebNM1;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateTerrain;

    public HistoVerificationHydraulique() {

    }

    public HistoVerificationHydraulique(String numero, Integer debitNM1, Integer debitMaxNM1, Double pressionNM1, Double pressionDynNM1, Double pressionDynDebNM1, Date dateTerrain) {
        super();
        this.numero = numero;
        this.debitNM1 = debitNM1;
        this.debitMaxNM1 = debitMaxNM1;
        this.pressionNM1 = pressionNM1;
        this.pressionDynNM1 = pressionDynNM1;
        this.pressionDynNM1 = pressionDynNM1;
        this.pressionDynDebNM1 = pressionDynDebNM1;
        
        this.dateTerrain = dateTerrain;
    }

}
