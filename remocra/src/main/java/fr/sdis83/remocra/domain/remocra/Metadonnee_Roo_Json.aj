// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Metadonnee;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Metadonnee_Roo_Json {
    
    public String Metadonnee.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String Metadonnee.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static Metadonnee Metadonnee.fromJsonToMetadonnee(String json) {
        return new JSONDeserializer<Metadonnee>()
        .use(null, Metadonnee.class).deserialize(json);
    }
    
    public static String Metadonnee.toJsonArray(Collection<Metadonnee> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String Metadonnee.toJsonArray(Collection<Metadonnee> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<Metadonnee> Metadonnee.fromJsonArrayToMetadonnees(String json) {
        return new JSONDeserializer<List<Metadonnee>>()
        .use("values", Metadonnee.class).deserialize(json);
    }
    
}
