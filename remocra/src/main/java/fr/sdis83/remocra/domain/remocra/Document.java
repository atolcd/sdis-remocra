package fr.sdis83.remocra.domain.remocra;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(versionField = "", finders = { "findDocumentsByCodeEquals" })
@Table(name = "document", uniqueConstraints = { @UniqueConstraint(columnNames = { "code" }), @UniqueConstraint(columnNames = { "fichier", "repertoire" }) })
public class Document {

    public enum TypeDocument {
        // Dépots
        DELIB, RECEPTRAVAUX, DECLAHYDRANT,
        // Blocs (documents génériques)
        BLOC,
        // "Métiers"
        ALERTE, HYDRANT, PERMIS, RCI, OLDEBVISITE, CRISE,
        // Courrier
        COURRIER
    }

    public enum SousTypeDocument {
        IMAGE, CARTEHORODATE, MEDIA, AUTRE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    private String fichier;

    @NotNull
    private String repertoire;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    @Column(name = "dateDoc", columnDefinition = "timestamp without time zone NOT NULL default now()")
    private Date dateDoc;

    @NotNull
    private String code;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeDocument type;
}
