package fr.sdis83.remocra.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.sdis83.remocra.domain.remocra.BlocDocument;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class BlocDocumentService extends AbstractService<BlocDocument> {

    @Autowired
    private ParamConfService paramConfService;

    public BlocDocumentService() {
        super(BlocDocument.class);
    }

    @Override
    protected boolean isDistinct() {
        return true;
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<BlocDocument> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        Predicate predicat = null;
        if ("profilDroitId".equals(itemFilter.getFieldName())) {
            Expression<Integer> cpPath = from.join("profilDroits").get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        } else if ("thematiqueCodes".equals(itemFilter.getFieldName())) {
            // Exemples de valeur : '5,2' ou '4' ou ''
            List<String> codes = Arrays.asList(itemFilter.getValue().split(","));
            Expression<Integer> cpPath = from.join("thematiques").get("code");
            predicat = cpPath.in(codes);
        } else if ("profilCodes".equals(itemFilter.getFieldName())) {
            // Exemples de valeur : cf. thematiqueCodes avec strings
            List<String> codes = Arrays.asList(itemFilter.getValue().split(","));
            Expression<Integer> cpPath = from.join("profilDroits").get("code");
            predicat = cpPath.in(codes);
        } else {
            predicat = super.processFilterItem(itemQuery, parameters, from, itemFilter);
        }
        return predicat;
    }

    @Override
    protected boolean processItemSortings(ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<BlocDocument> from) {
        if ("dateDoc".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.get("dateDoc");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else {
            return super.processItemSortings(orders, itemSorting, cBuilder, from);
        }
    }

    @Transactional
    @Override
    public BlocDocument setUpInformation(BlocDocument attached, Map<String, MultipartFile> files, Object... params) throws Exception {

        MultipartFile file = files != null && !files.isEmpty() ? files.values().iterator().next() : null;
        boolean aFile = file != null && file.getSize() > 0;

        boolean createMode = attached.getId() == null;

        if (createMode && !aFile) {
            // Mode création sans fichier : erreur
            throw new Exception("Le fichier du document n'a pas été trouvé");
        }

        if (aFile) {
            // Mise à jour avec fichier : on supprime l'ancien
            if (!createMode) {
                attached.getDocument().clear();
            }

            // Traitement fichier
            Document d = DocumentUtil.getInstance().createNonPersistedDocument(TypeDocument.BLOC, file, paramConfService.getDossierDepotBloc());
            this.entityManager.persist(d);
            attached.setDocument(d);
        }
        return attached;
    }

    @Transactional
    @Override
    public boolean delete(Long id) throws Exception {
        BlocDocument attached = this.entityManager.find(BlocDocument.class, id);
        Document d = attached.getDocument();

        // Ici, le fait de supprimer le Document provoque la suppression du
        // BlocDocument en cascade
        this.entityManager.remove(d);

        // Mais il faut gérer les ManyToMany (TODO : voir si on met des cascades
        // par sécurité directement en base)
        attached.getProfilDroits().clear();
        attached.getThematiques().clear();

        this.entityManager.flush();

        // Nettoyage du disque
        DocumentUtil.getInstance().deleteHDFile(d);
        return true;
    }
}
