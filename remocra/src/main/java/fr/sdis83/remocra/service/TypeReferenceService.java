package fr.sdis83.remocra.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import fr.sdis83.remocra.domain.remocra.ITypeReference;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.SousTypeAlerteElt;
import fr.sdis83.remocra.domain.remocra.TypeAlerteElt;
import fr.sdis83.remocra.exception.BusinessException;

@Configuration
public class TypeReferenceService {

    private final Logger logger = Logger.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ITypeReference update(Long id, ITypeReference record) throws BusinessException {
        logger.info("updateITypeReference : " + id + ", Classe : " + record.getClass().getSimpleName());

        ITypeReference attached = entityManager.find(record.getClass(), id);

        // Dans tous les cas, on reprend le code
        attached.setCode(record.getCode());

        // On reprend les valeurs de nom et actif
        if (record instanceof ITypeReferenceNomActif) {
            ITypeReferenceNomActif itrna = (ITypeReferenceNomActif) record;
            ITypeReferenceNomActif itrnaAttached = (ITypeReferenceNomActif) attached;
            itrnaAttached.setNom(itrna.getNom());
            itrnaAttached.setActif(itrna.getActif());
        }

        // Gestion au cas par cas

        // TODO cva améliorer (rendre générique ; utiliser introspection ?) ?
        // TODO ==> se baser sur le fonctionnement AbstractService de jpt

        if (attached instanceof SousTypeAlerteElt) {
            SousTypeAlerteElt attachedTyped = (SousTypeAlerteElt) attached;
            SousTypeAlerteElt recordTyped = (SousTypeAlerteElt) record;
            TypeAlerteElt tae = TypeAlerteElt.findTypeAlerteElt(recordTyped.getTypeAlerteElt().getId());
            attachedTyped.setTypeAlerteElt(tae);
            // FIXME : setTypeGeom
            // attachedTyped.setTypeGeom(recordTyped.getTypeGeom());
        }

        return attached.merge();
    }
}
