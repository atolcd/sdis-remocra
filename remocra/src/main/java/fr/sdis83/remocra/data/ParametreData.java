package fr.sdis83.remocra.data;

import fr.sdis83.remocra.db.model.remocra.enums.TypeParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParametreData {
  private final Map<String, CleValeurClasseData> mapValueByKey = new HashMap<>();

  public ParametreData(
      Collection<CleValeurClasseData> listParamConfData, Collection<Parametre> listParametreData) {
    for (CleValeurClasseData CleValeurClasseData : listParamConfData) {
      mapValueByKey.put(
          CleValeurClasseData.getCle(),
          new CleValeurClasseData(
              CleValeurClasseData.getCle(),
              CleValeurClasseData.getValeur(),
              ParamConf.ParamConfParam.getClassFromCle(CleValeurClasseData.getCle())));
    }
    for (Parametre parametre : listParametreData) {
      mapValueByKey.put(
          parametre.getCleParametre(),
          new CleValeurClasseData(
              parametre.getCleParametre(),
              parametre.getValeurParametre(),
              getClassFromTypeParametre(parametre.getTypeParametre())));
    }
  }

  /**
   * Retourne la classe attendue pour un type de paramètre donné
   *
   * @param typeParametre {@link TypeParametre}
   * @return Class<?>
   */
  private static Class<?> getClassFromTypeParametre(TypeParametre typeParametre) {
    switch (typeParametre) {
      case INTEGER:
        return Integer.class;
      case DOUBLE:
        return Double.class;
      case GEOMETRY:
      case BINARY:
      case STRING:
      default:
        return String.class;
    }
  }

  public String getValeurString(String cle) {
    if (!mapValueByKey.containsKey(cle)) {
      return null;
    }
    return mapValueByKey.get(cle).getValeur().toString();
  }

  public Object getValeur(String cle) {
    if (!mapValueByKey.containsKey(cle)) {
      return null;
    }
    if (mapValueByKey.get(cle).getClazz().equals(Integer.class)) {
      return Integer.valueOf((String) mapValueByKey.get(cle).getValeur());
    }

    // TODO pour chaque type concret, transtyper proprement
    return mapValueByKey.get(cle).getValeur().toString();
  }
}
