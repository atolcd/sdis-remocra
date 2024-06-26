// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ProfilOrganisme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect ProfilOrganisme_Roo_Json {
    
    public String ProfilOrganisme.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String ProfilOrganisme.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static ProfilOrganisme ProfilOrganisme.fromJsonToProfilOrganisme(String json) {
        return new JSONDeserializer<ProfilOrganisme>()
        .use(null, ProfilOrganisme.class).deserialize(json);
    }
    
    public static String ProfilOrganisme.toJsonArray(Collection<ProfilOrganisme> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String ProfilOrganisme.toJsonArray(Collection<ProfilOrganisme> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<ProfilOrganisme> ProfilOrganisme.fromJsonArrayToProfilOrganismes(String json) {
        return new JSONDeserializer<List<ProfilOrganisme>>()
        .use("values", ProfilOrganisme.class).deserialize(json);
    }
    
}
