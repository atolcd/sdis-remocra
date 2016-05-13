package fr.sdis83.remocra.domain.remocra;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooJpaActiveRecord(versionField = "")
public class SuiviPatches {
    
    @Id
    private Long numero;

    @NotNull
    private String description;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date application;
}
