package fr.sdis83.remocra.domain.remocra;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "", finders = { "findTypeOrganismesByCode" })
@RooJson
public class TypeOrganisme implements ITypeReferenceNomActif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    private String nom;
    
    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean actif;

    @ManyToOne(optional = true)
    private TypeOrganisme typeOrganismeParent;

}
