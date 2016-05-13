package fr.sdis83.remocra.domain.remocra;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(versionField = "")
public class BlocDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document document;

    @ManyToMany
    private Set<Thematique> thematiques;

    @ManyToMany
    private Set<ProfilDroit> profilDroits;

    private String titre;

    private String description;

    /**
     * Ajouté pour pouvoir réaliser des tris avec distinct
     */
    @Formula("(select d.date_doc from remocra.document d where d.id = document)")
    private Date dateDoc;

    public String getCode() {
        return document.getCode();
    }

    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    Date getDateDoc() {
        return document.getDateDoc();
    }
}
