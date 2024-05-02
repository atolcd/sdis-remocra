package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DOMAINE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE_DECI;
import static fr.sdis83.remocra.db.model.remocra.Tables.ZONE_SPECIALE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ZoneSpeciale;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NumeroUtilRepository {

  private static DSLContext context;

  public NumeroUtilRepository() {}

  @Autowired
  public NumeroUtilRepository(DSLContext context) {
    NumeroUtilRepository.context = context;
  }

  public enum MethodeNumerotation {
    M_01,
    M_09,
    M_14,
    M_21,
    M_38,
    M_39,
    M_42,
    M_49,
    M_53,
    M_61,
    M_66,
    M_77,
    M_78,
    M_83,
    M_86,
    M_89,
    M_91,
    M_95
  }

  public static NumeroUtilRepository.MethodeNumerotation getHydrantNumerotationMethode() {
    NumeroUtilRepository.MethodeNumerotation m = null;
    try {
      ParamConf pc =
          ParamConf.findParamConfsByCleEquals(
                  ParamConf.ParamConfParam.HYDRANT_NUMEROTATION_METHODE.getCle())
              .getSingleResult();
      m = NumeroUtilRepository.MethodeNumerotation.valueOf("M_" + pc.getValeur());
    } catch (Exception e) {
      System.err.println(
          "HYDRANT_NUMEROTATION_METHODE non reconnu (-> 83). Valeurs autorisées (sans M_) : "
              + ArrayUtils.toString(NumeroUtilRepository.MethodeNumerotation.values()));
      m = NumeroUtilRepository.MethodeNumerotation.M_83;
    }
    return m;
  }

  public enum MethodeNumerotationInterne {
    M_01,
    M_39,
    M_42,
    M_49,
    M_53,
    M_61,
    M_77,
    M_78,
    M_83,
    M_86,
    M_91,
    M_95
  }

  public static NumeroUtilRepository.MethodeNumerotationInterne
      getHydrantNumerotationInterneMethode() {
    NumeroUtilRepository.MethodeNumerotationInterne m = null;
    try {
      ParamConf pc =
          ParamConf.findParamConfsByCleEquals(
                  ParamConf.ParamConfParam.HYDRANT_NUMEROTATION_INTERNE_METHODE.getCle())
              .getSingleResult();
      m = NumeroUtilRepository.MethodeNumerotationInterne.valueOf("M_" + pc.getValeur());
    } catch (Exception e) {
      System.err.println(
          "HYDRANT_NUMEROTATION_METHODE non reconnu (-> 83). Valeurs autorisées (sans M_) : "
              + ArrayUtils.toString(NumeroUtilRepository.MethodeNumerotation.values()));
      m = NumeroUtilRepository.MethodeNumerotationInterne.M_83;
    }
    return m;
  }

  // ******************************
  // ** Numéro
  // ******************************

  /**
   * Calcule le numéro de l'hydrant.
   *
   * @param hydrant
   * @return
   */
  public static String computeNumero(Hydrant hydrant) {
    switch (getHydrantNumerotationMethode()) {
      case M_01:
      case M_61:
        return NumeroUtilRepository.computeNumero01(hydrant);
      case M_09:
        return NumeroUtilRepository.computeNumero09(hydrant);
      case M_14:
        return NumeroUtilRepository.computeNumero14(hydrant);
      case M_21:
        return NumeroUtilRepository.computeNumero21(hydrant);
      case M_38:
        return NumeroUtilRepository.computeNumero38(hydrant);
      case M_39:
        return NumeroUtilRepository.computeNumero39(hydrant);
      case M_42:
      case M_89:
        return NumeroUtilRepository.computeNumero89(hydrant);
      case M_49:
        return NumeroUtilRepository.computeNumero49(hydrant);
      case M_53:
        return NumeroUtilRepository.computeNumero53(hydrant);
      case M_66:
        return NumeroUtilRepository.computeNumero66(hydrant);
      case M_77:
        return NumeroUtilRepository.computeNumero77(hydrant);
      case M_78:
        return NumeroUtilRepository.computeNumero78(hydrant);
      case M_86:
        return NumeroUtilRepository.computeNumero86(hydrant);
      case M_91:
        return NumeroUtilRepository.computeNumero91(hydrant);
      case M_95:
        return NumeroUtilRepository.computeNumero95(hydrant);
      case M_83:
      default:
        return NumeroUtilRepository.computeNumero83(hydrant);
    }
  }

  /**
   * <code insee commune>_<numéro interne>
   * avec le numéro interne composé de 3 chiffres
   * Exemple : 01346_095
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero01(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();

    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append("_");

    // On l'adapte pour faire en sorte d'avoir 3 chiffres
    return sb.append(String.format("%03d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <code insee commune><numéro interne>
   * sans espace
   * sur 3 chiffres
   * Exemple : 09122012
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero09(Hydrant hydrant) {
    String codeZS =
        (getHydrantZoneSpeciale(hydrant) != null)
            ? getHydrantZoneSpeciale(hydrant).getCode()
            : null;

    StringBuilder sb = new StringBuilder();
    sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
    return sb.append(String.format("%03d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <insee> <numero_interne> avec un espace dans insee et le numero interne sur 4 chiffres Exemple
   * : 14 118 0001
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero14(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();

    String insee = getHydrantCommune(hydrant).getInsee();

    // On ajoute l'espace
    sb.append(insee.substring(0, 2) + " " + insee.substring(2, 5));
    sb.append(" ");

    // On l'adapte pour faire en sorte d'avoir 4 chiffres
    return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <code insee commune>_<numéro interne>
   * avec numéro interne sur 4 chiffres
   * Exemple : 86194_9994
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero21(Hydrant hydrant) {
    String codeZS =
        (getHydrantZoneSpeciale(hydrant) != null)
            ? getHydrantZoneSpeciale(hydrant).getCode()
            : null;

    StringBuilder sb = new StringBuilder();
    sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
    return sb.append(String.format("_%04d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <code insee commune>-<numéro interne>
   * avec insee sur 5 chiffres
   * avec num-interne sur 4 chiffres
   * et un tiret (-) entre les deux
   * Exemple : 86194-99947
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero38(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();
    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append("-");
    return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <code nature><code insee commune>.<numéro interne>
   * avec le numéro interne sur 5 chiffres
   * avec le code nature égal à P, B, A ou N
   * avec un point (.) entre insee et num_interne
   * Exemple : P39473.00001, A39199.21547
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero39(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();
    String codeNature =
        context
            .select(TYPE_HYDRANT_NATURE.CODE)
            .from(TYPE_HYDRANT_NATURE)
            .where(TYPE_HYDRANT_NATURE.ID.eq(hydrant.getNature()))
            .fetchOneInto(String.class);
    sb.append(codeNature);
    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append(".");
    return sb.append(String.format("%05d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <numéro interne> (sans contrainte) Exemple : 1 ou 17280
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero49(Hydrant hydrant) {
    return hydrant.getNumeroInterne().toString();
  }

  /**
   * <Nature PEI><code insee commune><numéro interne> sans espace
   * <p>
   * Exemple :
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero53(Hydrant hydrant) {
    /* <NATURE PEI> */
    String codeNature =
        context
            .select(TYPE_HYDRANT_NATURE.CODE)
            .from(TYPE_HYDRANT_NATURE)
            .where(TYPE_HYDRANT_NATURE.ID.eq(hydrant.getNature()))
            .fetchOneInto(String.class);

    /* <CODE INSEE COMMUNE> */
    String codeInsee = getHydrantCommune(hydrant).getInsee();
    Integer numIntern = null;
    String suffixe = "";
    String natureDeci =
        context
            .select(TYPE_HYDRANT_NATURE_DECI.CODE)
            .from(TYPE_HYDRANT_NATURE_DECI)
            .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(hydrant.getNatureDeci()))
            .fetchOneInto(String.class);
    numIntern = hydrant.getNumeroInterne();

    /* <SUFFIXE> */
    if ("PIBI".equalsIgnoreCase(hydrant.getCode())) {
      if ("53130".equalsIgnoreCase(codeInsee)) { // Laval
        String codeZoneSpeciale = getHydrantZoneSpeciale(hydrant).getCode();
        if (codeZoneSpeciale != null && "PRIVE".equalsIgnoreCase(natureDeci)) {
          switch (codeZoneSpeciale) {
            case "SECTEUR LAVAL 1": // Secteur 1
              suffixe = "S1";
              break;
            case "SECTEUR LAVAL 2":
              suffixe = "S2";
              break;
            case "SECTEUR LAVAL 3":
              suffixe = "S3";
              break;
            case "SECTEUR LAVAL 4":
              suffixe = "S4";
              break;
            case "SECTEUR LAVAL 5":
              suffixe = "S5";
              break;
            case "SECTEUR LAVAL 6":
              suffixe = "S6";
              break;
            case "SECTEUR LAVAL 7":
              suffixe = "S7";
              break;
            case "SECTEUR LAVAL 8":
              suffixe = "S8";
              break;
          }
        }
      } else if ("53054".equalsIgnoreCase(codeInsee)) { // Changé
        String codeZoneSpeciale = getHydrantZoneSpeciale(hydrant).getCode();
        if (codeZoneSpeciale != null) {
          switch (codeZoneSpeciale) {
            case "SECTEUR CHANGE RIVE DROITE": // Rive droite
              suffixe = "D";
              break;
            case "SECTEUR CHANGE RIVE GAUCHE": // Rive gauche
              suffixe = "G";
              break;
            case "SECTEUR CHANGE 1":
              if ("PRIVE".equalsIgnoreCase(natureDeci)) {
                suffixe = "S1";
              }
              break;
            case "SECTEUR CHANGE 2":
              if ("PRIVE".equalsIgnoreCase(natureDeci)) {
                suffixe = "S2";
              }
              break;
            case "SECTEUR CHANGE PRIVE":
              if ("PRIVE".equalsIgnoreCase(natureDeci)) {
                suffixe = "P";
              }
              break;
          }
        }
      } else if ("PRIVE".equalsIgnoreCase(natureDeci)) {
        suffixe = "P";
      }
    }

    /* Construction Numéro PEI */
    StringBuilder sb = new StringBuilder();
    sb.append(codeNature);
    sb.append(codeInsee);
    if (numIntern.toString().length() == 4) {
      sb.append(String.format("%04d", numIntern).toString());
    } else {
      sb.append(String.format("%03d", numIntern).toString());
    }
    sb.append(suffixe);
    return sb.toString();
  }

  /**
   * <code insee commune>_<numéro interne>(<P>)
   * Avec P à la fin pour PEI privé
   * Exemple : 89043_12P
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero66(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();

    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append("_");
    sb.append(hydrant.getNumeroInterne().toString());
    String codeNature =
        context
            .select(TYPE_HYDRANT_NATURE_DECI.CODE)
            .from(TYPE_HYDRANT_NATURE_DECI)
            .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(hydrant.getNatureDeci()))
            .fetchOneInto(String.class);
    if ("PRIVE".equals(codeNature)) {
      sb.append("P");
    }
    return sb.toString();
  }

  /**
   * <code insee commune><numéro interne> sans espace
   * avec num_intern sur 4 chiffres
   * Exemple : 772880012
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero77(Hydrant hydrant) {
    String codeZS =
        (getHydrantZoneSpeciale(hydrant) != null)
            ? getHydrantZoneSpeciale(hydrant).getCode()
            : null;

    StringBuilder sb = new StringBuilder();

    sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
    return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <code insee commune><numéro interne> ou <code insee commune>A<numéro interne>
   * sans espace
   * avec num_interne sur 4 chiffres pour Autoroutes
   * sinon num_interne sur 5 chiffres
   * Exemple : 8904300012 ou  89043A0012 pour les Autoroutes
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero78(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();
    String codeDomaine =
        context
            .select(TYPE_HYDRANT_DOMAINE.CODE)
            .from(TYPE_HYDRANT_DOMAINE)
            .where(TYPE_HYDRANT_DOMAINE.ID.eq(hydrant.getDomaine()))
            .fetchOneInto(String.class);
    sb.append(getHydrantCommune(hydrant).getInsee());
    if ("AUTOROUTE".equals(codeDomaine)) {
      sb.append("A");
      return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
    }
    return sb.append(String.format("%05d", hydrant.getNumeroInterne())).toString();
  }

  /**
   * <Code nature pour les PIBI ou les RI, PN pour les autres> <code commune>
   * <numéro interne>
   * <p>
   * Exemple : PI TLN 12
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero83(Hydrant hydrant) {
    String codeZS =
        (getHydrantZoneSpeciale(hydrant) != null)
            ? getHydrantZoneSpeciale(hydrant).getCode()
            : null;
    StringBuilder sb = new StringBuilder();

    String codeNature =
        context
            .select(TYPE_HYDRANT_NATURE.CODE)
            .from(TYPE_HYDRANT_NATURE)
            .where(TYPE_HYDRANT_NATURE.ID.eq(hydrant.getNature()))
            .fetchOneInto(String.class);

    if ("PIBI".equals(hydrant.getCode())) {
      sb.append(codeNature).append(" ");

      /*
       TODO prendre en compte les PI liés à une citerne :
        doubler le code de la ZS ou de la commune (ticket #177596)
      */

    } else if ("RI".equals(codeNature)) {
      sb.append("RI ");
    } else {
      sb.append("PN ");
    }

    sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getCode());
    return sb.append(" ").append(hydrant.getNumeroInterne()).toString();
  }

  /**
   * <code insee commune> <numéro interne>
   * <p>
   * Exemple : 86194 99947
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero86(Hydrant hydrant) {
    String codeZS =
        (getHydrantZoneSpeciale(hydrant) != null)
            ? getHydrantZoneSpeciale(hydrant).getCode()
            : null;

    StringBuilder sb = new StringBuilder();
    sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
    return sb.append(" ").append(hydrant.getNumeroInterne()).toString();
  }

  /**
   * <code insee commune>_<numéro interne>
   * <p>
   * Exemple : 89043_12
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero89(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();

    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append("_");
    return sb.append(hydrant.getNumeroInterne().toString()).toString();
  }

  /**
   * <code insee commune>-<numéro interne>
   * avec un tiret (-) entre les deux
   * Exemple : 91377-311
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero91(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();
    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append("-");
    return sb.append(hydrant.getNumeroInterne()).toString();
  }

  /**
   * <code insee commune>.<numéro interne>
   * avec num_interne sur 4 chiffres
   * et un tiret (-) entre les deux
   * Exemple : 95000-0001
   *
   * @param hydrant
   * @return
   */
  protected static String computeNumero95(Hydrant hydrant) {
    StringBuilder sb = new StringBuilder();
    sb.append(getHydrantCommune(hydrant).getInsee());
    sb.append(".");
    return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
  }

  // ******************************
  // ** Zone spéciale
  // ******************************

  /**
   * Calcule la zone spéciale de l'hydrant.
   *
   * @param hydrant
   * @return
   */
  public static ZoneSpeciale computeZoneSpeciale(Hydrant hydrant, int srid) {
    try {
      return context
          .select(ZONE_SPECIALE.fields())
          .from(ZONE_SPECIALE)
          .where(
              "ST_Contains({0}, st_pointfromtext({1}," + srid + "))",
              ZONE_SPECIALE.GEOMETRIE,
              hydrant.getGeometrie().toString())
          .limit(1)
          .fetchOneInto(ZoneSpeciale.class);
    } catch (Exception e) {
      return null;
    }
  }

  // ******************************
  // ** Zone spéciale et Numéro
  // ******************************

  /**
   * Redéfinit le code, la zone spéciale éventuelle, le numéro interne et le numéro
   *
   * @param hydrant Les informations de l'hydrant
   * @param typeHydrant Le type d'hydrant si celui-ci n'existe pas encore en base
   */
  public static Hydrant setCodeZoneSpecAndNumeros(Hydrant hydrant, String typeHydrant, int srid) {
    // Code
    String code =
        context
            .select(TYPE_HYDRANT.CODE)
            .from(HYDRANT)
            .join(TYPE_HYDRANT_NATURE)
            .on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
            .join(TYPE_HYDRANT)
            .on(TYPE_HYDRANT.ID.eq(TYPE_HYDRANT_NATURE.TYPE_HYDRANT))
            .where(HYDRANT.ID.eq(hydrant.getId()))
            .fetchOneInto(String.class);

    hydrant.setCode((code != null) ? code : typeHydrant);

    // Zone Spéciale
    ZoneSpeciale zp = computeZoneSpeciale(hydrant, srid);
    hydrant.setZoneSpeciale((zp != null) ? zp.getId() : null);

    // Si création : attribution d'un nouveau numéro interne
    Integer numInterne = NumeroUtilRepository.computeNumeroInterne(hydrant);
    if (hydrant.getNumeroInterne() == null || hydrant.getNumeroInterne().intValue() < 1) {
      hydrant.setNumeroInterne(numInterne);
    }

    // Calcul du numéro
    hydrant.setNumero(NumeroUtilRepository.computeNumero(hydrant));
    return hydrant;
  }

  // ******************************
  // ** Numéro interne
  // ******************************

  /**
   * Calcul le numéro interne de l'hydrant.
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne(Hydrant hydrant) {
    switch (getHydrantNumerotationInterneMethode()) {
      case M_01:
      case M_42:
      case M_61:
      case M_78:
        return NumeroUtilRepository.computeNumeroInterne01(hydrant);
      case M_39:
        return NumeroUtilRepository.computeNumeroInterne39(hydrant);
      case M_49:
        return NumeroUtilRepository.computeNumeroInterne49(hydrant);
      case M_53:
        return NumeroUtilRepository.computeNumeroInterne53(hydrant);
      case M_77:
        return NumeroUtilRepository.computeNumeroInterne77(hydrant);
      case M_86:
        return NumeroUtilRepository.computeNumeroInterne86(hydrant);
      case M_91:
        return NumeroUtilRepository.computeNumeroInterne91(hydrant);
      case M_95:
        return NumeroUtilRepository.computeNumeroInterne95(hydrant);
      case M_83:
      default:
        return NumeroUtilRepository.computeNumeroInterne83(hydrant);
    }
  }

  /**
   * numéro interne en fonction de la commune max +1 => pas de remplissage
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne01(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      // Incrementation automatique de numero interne
      numInterne =
          context
              .select(DSL.coalesce(DSL.max(HYDRANT.NUMERO_INTERNE), 0))
              .from(HYDRANT)
              .where(HYDRANT.COMMUNE.eq(hydrant.getCommune()))
              .fetchOneInto(Integer.class);
      // On prend le suivant
      numInterne++;
    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /**
   * numéro interne en fonction de la nature de l'hydrant et de la commune
   *
   * @param hydrant
   * @return numéro interne
   */
  public static Integer computeNumeroInterne39(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      numInterne =
          context
              .resultQuery(
                  "select remocra.nextNumeroInterne(null, {0}, {1}, null, true)",
                  DSL.val(hydrant.getNature(), SQLDataType.BIGINT),
                  DSL.val(hydrant.getCommune(), SQLDataType.BIGINT))
              .fetchOneInto(Integer.class);

    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /** Premier numéro interne disponible max +1 => pas de remplissage */
  public static Integer computeNumeroInterne49(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      numInterne =
          context.select(DSL.max(HYDRANT.NUMERO_INTERNE)).from(HYDRANT).fetchOneInto(Integer.class);
      numInterne++;
    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /**
   * numéro interne en fonction du code, de la nature_deci, de la commune ou de la zone spéciale si
   * renseignée complète les trous
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne53(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    String insee = getHydrantCommune(hydrant).getInsee();
    try {
      if ("PIBI".equalsIgnoreCase(hydrant.getCode())) {
        if ("53130".equalsIgnoreCase(insee)) { // Laval
          String codeZoneSpeciale = getHydrantZoneSpeciale(hydrant).getCode();
          if (codeZoneSpeciale != null) {
            switch (codeZoneSpeciale) {
              case "SECTEUR LAVAL 1": // Secteur 1
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 101, 199);
                return numInterne;
              case "SECTEUR LAVAL 2":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 201, 299);
                return numInterne;
              case "SECTEUR LAVAL 3":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 301, 399);
                return numInterne;
              case "SECTEUR LAVAL 4":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 401, 499);
                return numInterne;
              case "SECTEUR LAVAL 5":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 501, 599);
                return numInterne;
              case "SECTEUR LAVAL 6":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 601, 701);
                if (numInterne == 700) {
                  numInterne =
                      computeNumeroInterne53ZoneSpeciale(
                          hydrant, 6001,
                          6999); // reprise de la numérotation à partir de 6001 car plus de 100 PEI
                  // sur le secteur
                }
                return numInterne;
              case "SECTEUR LAVAL 7":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 701, 799);
                return numInterne;
              case "SECTEUR LAVAL 8":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 801, 899);
                return numInterne;
            }
          }
        } else if ("53054".equalsIgnoreCase(insee)) { // Changé
          String codeZoneSpeciale = getHydrantZoneSpeciale(hydrant).getCode();
          if (codeZoneSpeciale != null) {
            switch (codeZoneSpeciale) {
              case "SECTEUR CHANGE 1":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 101, 199);
                return numInterne;
              case "SECTEUR CHANGE 2":
                numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 201, 299);
                return numInterne;
              case "SECTEUR CHANGE PRIVE":
              case "SECTEUR CHANGE RIVE DROITE":
              case "SECTEUR CHANGE RIVE GAUCHE":
                numInterne = computeNumeroInterne53Private(hydrant, true);
                return numInterne;
            }
          }
        } else if ("53140".equalsIgnoreCase(insee)) { // Louverne
          ZoneSpeciale codeZoneSpeciale = getHydrantZoneSpeciale(hydrant);
          if (codeZoneSpeciale != null) {
            if (codeZoneSpeciale.getCode().equalsIgnoreCase("SECTEUR LOUVERNE 2")) {
              numInterne = computeNumeroInterne53ZoneSpeciale(hydrant, 201, 299);
              return numInterne;
            }
          } else { // PIBI à Louverne mais hors zone spéciale
            numInterne = computeNumeroInterne53Private(hydrant);
          }
        } else { // PIBI hors communes spéciale
          numInterne = computeNumeroInterne53Private(hydrant);
        }
      } else { // PENA
        numInterne = computeNumeroInterne53Private(hydrant);
      }
    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  public static Integer computeNumeroInterne53ZoneSpeciale(
      Hydrant hydrant, int startRange, int stopRange) {
    String natureDeci =
        context
            .select(TYPE_HYDRANT_NATURE_DECI.CODE)
            .from(TYPE_HYDRANT_NATURE_DECI)
            .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(hydrant.getNatureDeci()))
            .fetchOneInto(String.class);
    if ("PRIVE".equalsIgnoreCase(natureDeci)) {
      return computeNumeroInterne53Private(hydrant);
    } else {
      return computeNumeroInterne53Public(hydrant, startRange, stopRange);
    }
  }

  private static Integer computeNumeroInterne53Public(
      Hydrant hydrant, int startRange, int stopRange) {
    Integer numInterne = null;
    numInterne =
        context
            .resultQuery(
                "select remocra.nextNumeroInterne(null, {0}, {1}, {2}, {3}, {4}, {5}, true)",
                DSL.val(hydrant.getCode(), SQLDataType.VARCHAR),
                DSL.val(hydrant.getNatureDeci(), SQLDataType.BIGINT),
                (hydrant.getZoneSpeciale() == null)
                    ? hydrant.getCommune()
                    : DSL.val(null, SQLDataType.BIGINT),
                (hydrant.getZoneSpeciale() != null)
                    ? hydrant.getZoneSpeciale()
                    : DSL.val(null, SQLDataType.BIGINT),
                startRange,
                stopRange)
            .fetchOneInto(Integer.class);
    return numInterne;
  }

  private static Integer computeNumeroInterne53Private(Hydrant hydrant) {
    return computeNumeroInterne53Private(hydrant, false);
  }

  private static Integer computeNumeroInterne53Private(Hydrant hydrant, boolean ignoreDECI) {
    Integer numInterne = null;
    if ("PIBI".equalsIgnoreCase(hydrant.getCode())) {
      if (ignoreDECI) { // si on ignore la nature DECI ; Cas des secteurs Changé Rive Gauche/Droite
        // Et Changé Privé
        numInterne =
            context
                .resultQuery(
                    "select remocra.nextNumeroInterne(null, {0}, null, {1}, {2}, null, null, true)",
                    DSL.val(hydrant.getCode(), SQLDataType.VARCHAR),
                    (hydrant.getZoneSpeciale() == null)
                        ? hydrant.getCommune()
                        : DSL.val(null, SQLDataType.BIGINT),
                    (hydrant.getZoneSpeciale() != null)
                        ? hydrant.getZoneSpeciale()
                        : DSL.val(null, SQLDataType.BIGINT))
                .fetchOneInto(Integer.class);
      } else {
        numInterne =
            context
                .resultQuery(
                    "select remocra.nextNumeroInterne(null, {0}, {1}, {2}, {3}, null, null, true)",
                    DSL.val(hydrant.getCode(), SQLDataType.VARCHAR),
                    DSL.val(hydrant.getNatureDeci(), SQLDataType.BIGINT),
                    (hydrant.getZoneSpeciale() == null)
                        ? hydrant.getCommune()
                        : DSL.val(null, SQLDataType.BIGINT),
                    (hydrant.getZoneSpeciale() != null)
                        ? hydrant.getZoneSpeciale()
                        : DSL.val(null, SQLDataType.BIGINT))
                .fetchOneInto(Integer.class);
      }
    } else {
      numInterne =
          context
              .resultQuery(
                  "select remocra.nextNumeroInterne(null, {0}, null, {1}, null, null, null, true)",
                  DSL.val(hydrant.getCode(), SQLDataType.VARCHAR), hydrant.getCommune())
              .fetchOneInto(Integer.class);
    }
    return numInterne;
  }

  /**
   * numéro interne en fonction de la commune ou zone spéciale
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne77(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      // Premier numéro interne dispo pour la commune (ou de la zone spéciale)
      numInterne =
          context
              .resultQuery(
                  "select remocra.nextNumeroInterne(null, null, {0}, {1}, true)",
                  (hydrant.getZoneSpeciale() == null)
                      ? hydrant.getCommune()
                      : DSL.val(null, SQLDataType.BIGINT),
                  (hydrant.getZoneSpeciale() != null)
                      ? hydrant.getZoneSpeciale()
                      : DSL.val(null, SQLDataType.BIGINT))
              .fetchOneInto(Integer.class);

    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /**
   * numéro interne en fonction du type hydrant nature et de la commune ou zone spéciale max +1 =>
   * pas de remplissage
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne83(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      // Dernier numéro interne dispo pour le couple (type hydrant, commune) ou (type hydrant, zone
      // spéciale)
      Integer idTypeHydrant =
          context
              .select(TYPE_HYDRANT.ID)
              .from(HYDRANT)
              .join(TYPE_HYDRANT_NATURE)
              .on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
              .join(TYPE_HYDRANT)
              .on(TYPE_HYDRANT.ID.eq(TYPE_HYDRANT_NATURE.TYPE_HYDRANT))
              .where(HYDRANT.ID.eq(hydrant.getId()))
              .fetchOneInto(Integer.class);

      numInterne =
          context
              .resultQuery(
                  "select remocra.nextNumeroInterne(null, {0}, {1}, {2}, false)",
                  DSL.val(idTypeHydrant, SQLDataType.BIGINT),
                  (hydrant.getZoneSpeciale() == null)
                      ? hydrant.getCommune()
                      : DSL.val(null, SQLDataType.BIGINT),
                  (hydrant.getZoneSpeciale() != null)
                      ? hydrant.getZoneSpeciale()
                      : DSL.val(null, SQLDataType.BIGINT))
              .fetchOneInto(Integer.class);
    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /**
   * SDIS inconnu
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne86(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      // Dernier numéro interne dispo sans contrainte particulière
      numInterne =
          context
              .resultQuery("select remocra.nextNumeroInterne(null, null, null, null, false)")
              .fetchOneInto(Integer.class);
    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /**
   * Numérotation interne du 91
   *
   * <pre>
   * PIBI public : le premier numéro disponible à partir de 1
   * PIBI privés : le premier numéro disponible à partir de 500
   * PENA publics ou privés : le premier numéro disponible à partir de 800</pre>
   *
   * @param hydrant
   * @return
   */
  public static Integer computeNumeroInterne91(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      Integer idTypeHydrant =
          context
              .select(TYPE_HYDRANT.ID)
              .from(TYPE_HYDRANT)
              .join(TYPE_HYDRANT_NATURE)
              .on(TYPE_HYDRANT_NATURE.TYPE_HYDRANT.eq(TYPE_HYDRANT.ID))
              .where(TYPE_HYDRANT_NATURE.ID.eq(hydrant.getNature()))
              .fetchOneInto(Integer.class);

      numInterne =
          context
              .resultQuery(
                  "select remocra.nextNumeroInterne(null, {0}, {1}, {2}, {3})",
                  DSL.val(idTypeHydrant, SQLDataType.BIGINT),
                  DSL.val(hydrant.getNatureDeci(), SQLDataType.BIGINT),
                  (hydrant.getZoneSpeciale() == null)
                      ? hydrant.getCommune()
                      : DSL.val(null, SQLDataType.BIGINT),
                  (hydrant.getZoneSpeciale() != null)
                      ? hydrant.getZoneSpeciale()
                      : DSL.val(null, SQLDataType.BIGINT))
              .fetchOneInto(Integer.class);

    } catch (Exception e) {
      numInterne = 99999;
    }
    return numInterne;
  }

  /**
   * numéro interne en fonction de la commune ou zone speciale ET nature DECI max +1 => pas de
   * remplissage
   *
   * @param hydrant
   * @return
   */
  protected static Integer computeNumeroInterne95(Hydrant hydrant) {
    // Retour du numéro interne s'il existe
    if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
      return hydrant.getNumeroInterne();
    }
    Integer numInterne = null;
    try {
      String codeNature =
          context
              .select(TYPE_HYDRANT_NATURE_DECI.CODE)
              .from(TYPE_HYDRANT_NATURE_DECI)
              .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(hydrant.getNatureDeci()))
              .fetchOneInto(String.class);
      if ("PRIVE".equals(codeNature) || "CONVENTIONNE".equals(codeNature)) {
        numInterne =
            context
                .resultQuery(
                    "select remocra.nextNumeroInterne(null, null, {0}, {1}, {2}, {3}, true)",
                    (hydrant.getZoneSpeciale() == null)
                        ? hydrant.getCommune()
                        : DSL.val(null, SQLDataType.BIGINT),
                    (hydrant.getZoneSpeciale() != null)
                        ? hydrant.getZoneSpeciale()
                        : DSL.val(null, SQLDataType.BIGINT),
                    1000,
                    9999)
                .fetchOneInto(Integer.class);

      } else {
        numInterne =
            context
                .resultQuery(
                    "select remocra.nextNumeroInterne(null, null, {0}, {1}, null, {2}, true)",
                    (hydrant.getZoneSpeciale() == null)
                        ? hydrant.getCommune()
                        : DSL.val(null, SQLDataType.BIGINT),
                    (hydrant.getZoneSpeciale() != null)
                        ? hydrant.getZoneSpeciale()
                        : DSL.val(null, SQLDataType.BIGINT),
                    999)
                .fetchOneInto(Integer.class);
      }
    } catch (Exception e) {
      numInterne = 9999;
    }
    return numInterne;
  }

  private static ZoneSpeciale getHydrantZoneSpeciale(Hydrant h) {
    return context
        .selectFrom(ZONE_SPECIALE)
        .where(ZONE_SPECIALE.ID.eq(h.getZoneSpeciale()))
        .fetchOneInto(ZoneSpeciale.class);
  }

  private static Commune getHydrantCommune(Hydrant h) {
    return context
        .selectFrom(COMMUNE)
        .where(COMMUNE.ID.equal(h.getCommune()))
        .fetchOneInto(Commune.class);
  }
}
