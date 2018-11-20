package fr.sdis83.remocra.web.serialize.ext;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.AuthenticationException;

import fr.sdis83.remocra.domain.utils.RemocraInstantTransformer;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vividsolutions.jts.geom.Geometry;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.json.JsonObjectResponse;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;

public abstract class AbstractExtListSerializer<T> {

    private final Logger logger = Logger.getLogger(getClass());

    private String successMessage;

    @SuppressWarnings("rawtypes")
    public static AbstractExtListSerializer<?> getDummy() {
        return new AbstractExtListSerializer("") {
            @Override
            protected List<?> getRecords() throws BusinessException, AuthenticationException {
                return Collections.emptyList();
            }

        };
    }

    public AbstractExtListSerializer(String successMessage) {
        this.successMessage = successMessage;
    }

    public ResponseEntity<String> serialize() {
        HttpStatus returnStatus = HttpStatus.OK;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            List<T> records = getRecords();
            Long countRecords = this.countRecords();
            response.setMessage(successMessage);
            response.setSuccess(true);
            response.setTotal(countRecords != null ? countRecords : records.size());
            response.setData(records);
        } catch (Exception e) {

            logger.error(e.getMessage(), e);

            response.setMessage(e.getMessage() != null ? e.getMessage() : "Erreur inconnue");
            response.setSuccess(false);
            response.setTotal(0L);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json;charset=utf-8");
        return new ResponseEntity<String>(getJsonSerializer() .serialize(response), responseHeaders, returnStatus);

    }

    protected JSONSerializer getJsonSerializer() {
        return additionnalIncludeExclude(
            new JSONSerializer().exclude("*.class").exclude("*.password").exclude("*.salt").transform(new GeometryTransformer(), Geometry.class)).transform(
            RemocraDateHourTransformer.getInstance(), Date.class).transform(new RemocraInstantTransformer(), Instant.class);
        }


    protected abstract List<T> getRecords() throws BusinessException, AuthenticationException;

    /**
     * Par defaut Null quand le count est egal au nombre de resultats de
     * GetRecords (cas où il n'y a pas de paging).
     * 
     * @return
     * @throws BusinessException
     */
    protected Long countRecords() throws BusinessException {
        return null;
    }

    /**
     * Par defaut, supprime le champ class des objets
     * 
     * @param serializer
     * @return
     */
    protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer;
    }
}
