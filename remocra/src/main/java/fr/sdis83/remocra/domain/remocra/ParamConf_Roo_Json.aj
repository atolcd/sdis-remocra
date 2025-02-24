// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect ParamConf_Roo_Json {
    
    public String ParamConf.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String ParamConf.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static ParamConf ParamConf.fromJsonToParamConf(String json) {
        return new JSONDeserializer<ParamConf>()
        .use(null, ParamConf.class).deserialize(json);
    }
    
    public static String ParamConf.toJsonArray(Collection<ParamConf> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String ParamConf.toJsonArray(Collection<ParamConf> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<ParamConf> ParamConf.fromJsonArrayToParamConfs(String json) {
        return new JSONDeserializer<List<ParamConf>>()
        .use("values", ParamConf.class).deserialize(json);
    }
    
}
