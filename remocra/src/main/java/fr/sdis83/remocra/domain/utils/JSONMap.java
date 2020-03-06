package fr.sdis83.remocra.domain.utils;

import java.util.HashMap;
import java.util.Map;

public class JSONMap<K,V> extends HashMap<K,V> {
    private static final long serialVersionUID = 1L;
    private static String SEPARATOR = null;
    private static final char QUOTE = '"';
    private static final char EQUALS = '=';
    private static final char START = '{';
    private static final char END = '}';

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(START);
        SEPARATOR = "";
        for (Map.Entry<K, V> entry : this.entrySet()) {
            if(SEPARATOR.length()>0) {
              sb.append(SEPARATOR);
            }

            sb.append(entry.getKey());
            sb.append(EQUALS);

            // Chaine de caractère n'étant pas une valeur booléenne
            if(entry.getValue() != null &&
                !"true".equalsIgnoreCase(entry.getValue().toString())
                && !"false".equalsIgnoreCase(entry.getValue().toString())
                && entry.getValue().toString().matches(".*[a-zA-Z].*")) {
              sb.append(QUOTE);
              sb.append(entry.getValue());
              sb.append(QUOTE);
            } else {
              sb.append(entry.getValue());
            }

            SEPARATOR = ", ";
        }
        sb.append(END);
        return sb.toString();
    }

  /**
   * Transforme un objet HashMap (crée depuis une chaîne de caractère transmise au serveur) en objet JSONMap
   * Contrairement à ces HashMap, les JSONMap peuvent supporter les caractères spéciaux en étant compatible avec le deserializer de FlexJSON
   */
  public static JSONMap fromMap(HashMap<String, Object> m) {
      JSONMap<String, Object> r = new JSONMap<String, Object>();
      for(Map.Entry<String, Object> entry : m.entrySet()) {
        r.put(entry.getKey(), entry.getValue());
      }
      return r;
    }

}