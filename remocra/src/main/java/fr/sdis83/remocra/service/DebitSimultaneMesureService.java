package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneDocument;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneHydrant;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneMesure;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.utils.JSONMap;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.deserialize.RemocraBeanObjectFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DebitSimultaneMesureService<T extends DebitSimultaneMesure> extends AbstractService<DebitSimultaneMesure> {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    protected ParamConfService paramConfService;

    public DebitSimultaneMesureService() {
        super(DebitSimultaneMesure.class);
    }

    @PersistenceContext
    protected EntityManager entityManager;

    @Bean
    public DebitSimultaneMesureService DebitSimultaneMesureService() {
        return new DebitSimultaneMesureService();
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<DebitSimultaneMesure> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        Predicate predicat = null;
        if ("id".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        } else if ("debitSimultane".equals(itemFilter.getFieldName())){
            Expression<String> cpPath = from.join("debitSimultane").get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        } else {
            logger.info("processFilterItem non traité " + itemFilter.getFieldName() + " (" + itemFilter.getValue() + ")");
        }
        return predicat;
    }

    private Method findSetter(Object obj, String fieldName) {
        String methodName = "set" + StringUtils.capitalize(fieldName);
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methodName.equals(methods[i].getName())) {
                return methods[i];
            }
        }
        return null;
    }

    /**
     * Permet de mettre à jour plusieurs instances de HydrantVisite
     * @param json Un tableau JSON contenant les informations de l'HydrantVisite
     */
    @Transactional
    public void updateMany(String json, Map<String, MultipartFile> files) throws Exception {
        ArrayList<HashMap<String, Object>> liste = new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(json);
        for(HashMap<String, Object> obj : liste){
            // ID existant -> mise à jour
            if(obj.get("id") != null) {
                DebitSimultaneMesure mesure = this.entityManager.find(this.cls, Long.valueOf(obj.get("id").toString()));

                mesure.setDateMesure(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(obj.get("dateMesure"))));
                obj.remove("dateMesure");

                JSONDeserializer<DebitSimultaneMesure> deserializer = new JSONDeserializer<DebitSimultaneMesure>();
                deserializer.use(null, this.cls).use(Date.class, RemocraDateHourTransformer.getInstance()).use(Geometry.class, new GeometryFactory()).use(Object.class,
                        new RemocraBeanObjectFactory(this.entityManager));
                deserializer.deserializeInto(JSONMap.fromMap(obj).toString(), mesure);

                /*
                 * "hack" pour gérer les valeurs "null" dans le json. Flexjson les
                 * ignore, et ce bug connu sera corriger dans la v3.0 cf :
                 * http://sourceforge.net/p/flexjson/bugs/32/
                 */
                JSONDeserializer<Map<String, Object>> deserializer2 = new JSONDeserializer<Map<String, Object>>();
                Map<String, Object> data = deserializer2.deserialize(JSONMap.fromMap(obj).toString());
                Object nullObject = null;
                if (data != null && data.size() > 0) {
                    for (String key : data.keySet()) {
                        Object value = data.get(key);
                        if (value == null) {
                            Method method = findSetter(mesure, key);
                            if (method != null) {
                                method.invoke(mesure, nullObject);
                            }
                        }

                    }
                }
                // Fin "hack"
                this.setUpInformation(mesure, files, obj);
                this.entityManager.merge(mesure);
                this.entityManager.flush();
            } else {

                //ID inexistant: création
                this.create(obj, files);
            }
        }
    }

    @Transactional
    public DebitSimultaneMesure create(HashMap<String, Object> obj, Map<String, MultipartFile> files) throws Exception {
        JSONDeserializer<DebitSimultaneMesure> deserializer = new JSONDeserializer<DebitSimultaneMesure>();
        deserializer.use(null, this.cls).use(Date.class, RemocraDateHourTransformer.getInstance()).use(Geometry.class, new GeometryFactory()).use(Object.class,
                new RemocraBeanObjectFactory(this.entityManager));

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(obj.get("dateMesure")));
        obj.remove("dateMesure");

        DebitSimultaneMesure attached = deserializer.deserialize(JSONMap.fromMap(obj).toString());

        attached.setDateMesure(date);
        //attached.setHydrants(null);

        this.setUpInformation(attached, files, obj);
        this.entityManager.persist(attached);
        return attached;
    }

    @Transactional
    public DebitSimultaneMesure setUpInformation(DebitSimultaneMesure attached, Map<String, MultipartFile> files, HashMap<String, Object> obj) throws Exception {

        // Liaison entre les hydrants et les mesures de débit simultané
        String listeHydrants = obj.get("listeHydrants").toString();
        listeHydrants = listeHydrants.replaceAll("\\[", "").replaceAll("\\]","").replaceAll(" ", "");

        for(DebitSimultaneHydrant dsHydrant : attached.getHydrants()){
            entityManager.remove(dsHydrant);
            entityManager.flush();
        }
        attached.getHydrants().clear();

        for(String s : listeHydrants.split(",")){
            Long idHydrant = Long.valueOf(s);
            Hydrant h = entityManager.find(Hydrant.class, idHydrant);
            DebitSimultaneHydrant dsh = new DebitSimultaneHydrant();
            dsh.setHydrant(this.entityManager.merge(h));
            dsh.setDebit(attached);
            attached.getHydrants().add(dsh);
        }

        // traitement des fichiers
        Integer indexAttestation = Integer.valueOf(obj.get("indexAttestation").toString());
        for (Map.Entry<String, MultipartFile> file : files.entrySet()) {
            if (file.getKey().equals("files[" + indexAttestation + "]")) {

                if (file.getValue() != null && !file.getValue().isEmpty()) {

                    if (attached.getAttestation() != null) {
                        this.deleteDocument(attached.getAttestation().getId());
                    }
                    Document d = DocumentUtil.getInstance().createNonPersistedDocument(Document.TypeDocument.ATTESTATION, file.getValue(), paramConfService.getDossierDepotAttestation());
                    DebitSimultaneDocument dsd = new DebitSimultaneDocument();
                    dsd.setDebitSimultaneMesure(attached);
                    dsd.setDocument(this.entityManager.merge(d));

                    attached.setAttestation(dsd);
                }
            }
        }
        return attached;
    }

    public void deleteDocument(Long id) throws Exception {
        DebitSimultaneDocument attached = entityManager.find(DebitSimultaneDocument.class, id);
        Document d = attached.getDocument();

        // Ici, le fait de supprimer le Document provoque la suppression du
        // DebitSimultaneDocument en cascade
        entityManager.remove(d);
        entityManager.flush();

        // Nettoyage du disque
        DocumentUtil.getInstance().deleteHDFile(d);
    }

    @Transactional
    public boolean delete(String json) throws Exception {
        ArrayList<Integer> liste = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
        for(Integer id : liste){
            super.delete(Long.valueOf(id));
        }
        return true;
    }
}