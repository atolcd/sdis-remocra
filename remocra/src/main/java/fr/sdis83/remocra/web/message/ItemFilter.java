package fr.sdis83.remocra.web.message;

import flexjson.JSONDeserializer;
import flexjson.ObjectBinder;
import flexjson.factories.IntegerObjectFactory;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemFilter {

  private final String fieldName;
  private final String value;

  public ItemFilter(String fieldName, String value) {
    this.fieldName = fieldName;
    this.value = value;
  }

  public static List<ItemFilter> decodeJson(String json) {
    List<ItemFilter> itemFilters = new ArrayList<ItemFilter>();
    if (json != null) {
      List<HashMap<String, String>> filterList =
          new JSONDeserializer<ArrayList<HashMap<String, String>>>()
              .use(
                  Integer.class,
                  new IntegerObjectFactory() {
                    @SuppressWarnings("rawtypes")
                    @Override
                    /** On n'utilise que des String, un entier => Un string... */
                    public Object instantiate(
                        ObjectBinder context, Object value, Type targetType, Class targetClass) {
                      return super.instantiate(context, value, targetType, targetClass).toString();
                    }
                  })
              .deserialize(json);
      for (HashMap<String, String> filter : filterList) {
        String fieldName = filter.get("property");
        String direction = filter.get("value");
        itemFilters.add(new ItemFilter(fieldName, direction));
      }
    }
    return itemFilters;
  }

  public static boolean hasFilter(List<ItemFilter> filters, String property) {
    return getFilter(filters, property) != null;
  }

  public static ItemFilter getFilter(List<ItemFilter> filters, String property) {
    if (filters == null) {
      return null;
    }
    for (ItemFilter f : filters) {
      if (property.equals(f.getFieldName())) {
        return f;
      }
    }
    return null;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getValue() {
    return value;
  }
}
