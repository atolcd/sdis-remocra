// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.HistoVerificationHydraulique;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect HistoVerificationHydraulique_Roo_Json {
    
    public String HistoVerificationHydraulique.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String HistoVerificationHydraulique.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static HistoVerificationHydraulique HistoVerificationHydraulique.fromJsonToHistoVerificationHydraulique(String json) {
        return new JSONDeserializer<HistoVerificationHydraulique>()
        .use(null, HistoVerificationHydraulique.class).deserialize(json);
    }
    
    public static String HistoVerificationHydraulique.toJsonArray(Collection<HistoVerificationHydraulique> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String HistoVerificationHydraulique.toJsonArray(Collection<HistoVerificationHydraulique> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<HistoVerificationHydraulique> HistoVerificationHydraulique.fromJsonArrayToHistoVerificationHydrauliques(String json) {
        return new JSONDeserializer<List<HistoVerificationHydraulique>>()
        .use("values", HistoVerificationHydraulique.class).deserialize(json);
    }
    
}
