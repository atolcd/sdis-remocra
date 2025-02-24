// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.TypeRciOrigineAlerte;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect TypeRciOrigineAlerte_Roo_Json {
    
    public String TypeRciOrigineAlerte.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String TypeRciOrigineAlerte.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static TypeRciOrigineAlerte TypeRciOrigineAlerte.fromJsonToTypeRciOrigineAlerte(String json) {
        return new JSONDeserializer<TypeRciOrigineAlerte>()
        .use(null, TypeRciOrigineAlerte.class).deserialize(json);
    }
    
    public static String TypeRciOrigineAlerte.toJsonArray(Collection<TypeRciOrigineAlerte> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String TypeRciOrigineAlerte.toJsonArray(Collection<TypeRciOrigineAlerte> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<TypeRciOrigineAlerte> TypeRciOrigineAlerte.fromJsonArrayToTypeRciOrigineAlertes(String json) {
        return new JSONDeserializer<List<TypeRciOrigineAlerte>>()
        .use("values", TypeRciOrigineAlerte.class).deserialize(json);
    }
    
}
