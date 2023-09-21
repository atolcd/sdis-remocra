package fr.sdis83.remocra.web.message;

import flexjson.JSONDeserializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemSorting {

  private final String fieldName;
  private final String direction;

  public ItemSorting(String fieldName, String direction) {
    this.fieldName = fieldName;
    this.direction = direction;
  }

  public static List<ItemSorting> decodeJson(String json) {
    List<ItemSorting> sorts = new ArrayList<ItemSorting>();
    if (json != null) {
      List<HashMap<String, String>> sortList =
          new JSONDeserializer<ArrayList<HashMap<String, String>>>().deserialize(json);
      for (HashMap<String, String> sort : sortList) {
        String fieldName = sort.get("property");
        String direction = sort.get("direction");
        sorts.add(new ItemSorting(fieldName, direction));
      }
    }
    return sorts;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getDirection() {
    return direction;
  }

  public boolean isAsc() {
    return "ASC".equals(this.getDirection().toUpperCase());
  }

  public boolean isDesc() {
    return "DESC".equals(this.getDirection().toUpperCase());
  }
}
