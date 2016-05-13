package fr.sdis83.remocra.util;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FeatureUtil {

    public static ResponseEntity<java.lang.String> getResponse(List<? extends Featurable> featurables) {
        return getResponse(featurables, null);
    }

    public static ResponseEntity<java.lang.String> getResponse(List<? extends Featurable> featurables, FeatureCollection featureCollectionParam) {
        FeatureCollection featureCollection = featureCollectionParam == null ? new FeatureCollection() : featureCollectionParam;
        for (Featurable featurable : featurables) {
            featureCollection.addFeatures(featurable.toFeature());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json;charset=utf-8");
        return new ResponseEntity<String>(featureCollection.serialize(), responseHeaders, HttpStatus.OK);
    }

}
