// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.AlerteDocument;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect AlerteDocument_Roo_Json {
    
    public String AlerteDocument.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String AlerteDocument.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static AlerteDocument AlerteDocument.fromJsonToAlerteDocument(String json) {
        return new JSONDeserializer<AlerteDocument>()
        .use(null, AlerteDocument.class).deserialize(json);
    }
    
    public static String AlerteDocument.toJsonArray(Collection<AlerteDocument> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String AlerteDocument.toJsonArray(Collection<AlerteDocument> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<AlerteDocument> AlerteDocument.fromJsonArrayToAlerteDocuments(String json) {
        return new JSONDeserializer<List<AlerteDocument>>()
        .use("values", AlerteDocument.class).deserialize(json);
    }
    
}
