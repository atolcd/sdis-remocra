// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Organisme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Organisme_Roo_Json {
    
    public String Organisme.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String Organisme.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static Organisme Organisme.fromJsonToOrganisme(String json) {
        return new JSONDeserializer<Organisme>()
        .use(null, Organisme.class).deserialize(json);
    }
    
    public static String Organisme.toJsonArray(Collection<Organisme> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String Organisme.toJsonArray(Collection<Organisme> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<Organisme> Organisme.fromJsonArrayToOrganismes(String json) {
        return new JSONDeserializer<List<Organisme>>()
        .use("values", Organisme.class).deserialize(json);
    }
    
}
