package fr.sdis83.remocra.web.serialize.ext;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vividsolutions.jts.geom.Geometry;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.json.JsonObjectResponse;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;

public abstract class AbstractExtObjectSerializer<T> {

    public final static String DEFAULT_CONTENT_TYPE = "application/json;charset=utf-8";

    private final Logger logger = Logger.getLogger(getClass());

    private String successMessage;
    private String contentType = null;

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public AbstractExtObjectSerializer(String successMessage) {
        this.successMessage = successMessage;
    }

    public AbstractExtObjectSerializer(String successMessage, String contentType) {
        this(successMessage);
        this.contentType = contentType;
    }

    public ResponseEntity<String> serialize() {
        HttpStatus returnStatus = HttpStatus.OK;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            T record = getRecord();

            response.setMessage(successMessage);
            response.setSuccess(true);
            response.setTotal(1);
            response.setData(record);
        } catch (Exception e) {

            logger.error(e.getMessage(), e);

            response.setMessage(e.getMessage() != null ? e.getMessage() : "Erreur inconnue");
            response.setSuccess(false);
            response.setTotal(0L);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", this.contentType == null ? DEFAULT_CONTENT_TYPE : this.contentType);
        return new ResponseEntity<String>(additionnalIncludeExclude(
                new JSONSerializer().exclude("*.class").exclude("*.password").exclude("*.salt")
                        .transform(new GeometryTransformer(), Geometry.class)).transform(RemocraDateHourTransformer.getInstance(),
                Date.class).serialize(response), responseHeaders, returnStatus);
    }

    protected abstract T getRecord() throws BusinessException;

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
