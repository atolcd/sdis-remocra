package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPibi;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ZoneSpeciale;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DOMAINE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE_DECI;
import static fr.sdis83.remocra.db.model.remocra.Tables.ZONE_SPECIALE;

@Component
public class NumeroUtilRepository {


  private static DSLContext context;

  public NumeroUtilRepository() {

  }

  @Autowired
  public NumeroUtilRepository(DSLContext context) {
    NumeroUtilRepository.context = context;
  }

  public enum MethodeNumerotation {
        M_09, M_77, M_83, M_86, M_89, M_42, M_78, M_66, M_21, M_49, M_38, M_95, M_91
    }

    public static NumeroUtilRepository.MethodeNumerotation getHydrantNumerotationMethode() {
        NumeroUtilRepository.MethodeNumerotation m = null;
        try {
            ParamConf pc = ParamConf.findParamConfsByCleEquals(ParamConf.ParamConfParam.HYDRANT_NUMEROTATION_METHODE.getCle())
                    .getSingleResult();
            m = NumeroUtilRepository.MethodeNumerotation.valueOf("M_" + pc.getValeur());
        } catch (Exception e) {
            System.err.println("HYDRANT_NUMEROTATION_METHODE non reconnu (-> 83). Valeurs autorisées (sans M_) : "
                    + ArrayUtils.toString(NumeroUtilRepository.MethodeNumerotation.values()));
            m = NumeroUtilRepository.MethodeNumerotation.M_83;
        }
        return m;
    }

    public enum MethodeNumerotationInterne {
        M_77, M_83, M_86, M_42, M_78, M_49, M_95, M_91
    }

    public static NumeroUtilRepository.MethodeNumerotationInterne getHydrantNumerotationInterneMethode() {
        NumeroUtilRepository.MethodeNumerotationInterne m = null;
        try {
            ParamConf pc = ParamConf.findParamConfsByCleEquals(ParamConf.ParamConfParam.HYDRANT_NUMEROTATION_INTERNE_METHODE.getCle())
                    .getSingleResult();
            m = NumeroUtilRepository.MethodeNumerotationInterne.valueOf("M_" + pc.getValeur());
        } catch (Exception e) {
            System.err.println("HYDRANT_NUMEROTATION_METHODE non reconnu (-> 83). Valeurs autorisées (sans M_) : "
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
            case M_09:
                return NumeroUtilRepository.computeNumero09(hydrant);
            case M_77:
                return NumeroUtilRepository.computeNumero77(hydrant);
            case M_86:
                return NumeroUtilRepository.computeNumero86(hydrant);
            case M_89:
                return NumeroUtilRepository.computeNumero89(hydrant);
            // même methode que 89
            case M_42:
                return NumeroUtilRepository.computeNumero89(hydrant);
            case M_78:
                return NumeroUtilRepository.computeNumero78(hydrant);
            case M_66:
                return NumeroUtilRepository.computeNumero66(hydrant);
            case M_21:
                return NumeroUtilRepository.computeNumero21(hydrant);
            case M_49:
                return NumeroUtilRepository.computeNumero49(hydrant);
            case M_38:
                return NumeroUtilRepository.computeNumero38(hydrant);
            case M_95:
                return NumeroUtilRepository.computeNumero95(hydrant);
            case M_91:
                return NumeroUtilRepository.computeNumero91(hydrant);
            default:
                return NumeroUtilRepository.computeNumero83(hydrant);
        }
    }

    /**
     * <code insee commune><numéro interne> sans espace
     * <p>
     * Exemple : 09122012
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero09(Hydrant hydrant) {
        String codeZS = (getHydrantZoneSpeciale(hydrant) != null) ? getHydrantZoneSpeciale(hydrant).getCode() : null;

        StringBuilder sb = new StringBuilder();

        sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
        return sb.append(String.format("%03d", hydrant.getNumeroInterne())).toString();
    }

    /**
     * <code insee commune><numéro interne> sans espace
     * <p>
     * Exemple : 772880012
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero77(Hydrant hydrant) {
        String codeZS = (getHydrantZoneSpeciale(hydrant) != null) ? getHydrantZoneSpeciale(hydrant).getCode() : null;

        StringBuilder sb = new StringBuilder();

        sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
        return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
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
        String codeZS = (getHydrantZoneSpeciale(hydrant) != null) ? getHydrantZoneSpeciale(hydrant).getCode() : null;

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
     * <code insee commune>0 ou A<numéro interne>
     * <p>
     * Exemple : 8904300012 ou  89043A0012 pour les Autoroutes
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero78(Hydrant hydrant) {
        StringBuilder sb = new StringBuilder();
        String codeDomaine = context.select(TYPE_HYDRANT_DOMAINE.CODE)
                .from(TYPE_HYDRANT_DOMAINE)
                .where(TYPE_HYDRANT_DOMAINE.ID.eq(hydrant.getDomaine()))
                .fetchOneInto(String.class);
        sb.append(getHydrantCommune(hydrant).getInsee());
        if("AUTOROUTE".equals(codeDomaine)){
            sb.append("A");
            return sb.append(String.format("%04d", hydrant.getNumeroInterne())).toString();
        }
        return sb.append(String.format("%05d", hydrant.getNumeroInterne())).toString();
    }

    /**
     * <code insee commune>_<numéro interne><P(pour les PEI privés)>
     * <p>
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
        String codeNature = context.select(TYPE_HYDRANT_NATURE_DECI.CODE)
                .from(TYPE_HYDRANT_NATURE_DECI)
                .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(hydrant.getNatureDeci()))
                .fetchOneInto(String.class);
        if("PRIVE".equals(codeNature)){
            sb.append("P");
        }
        return sb.toString();
    }

    /**
     * <code insee commune>_<numéro interne> (sur 4 chiffres)
     * <p>
     * Exemple : 86194_99947
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero21(Hydrant hydrant) {
        String codeZS = (getHydrantZoneSpeciale(hydrant) != null) ? getHydrantZoneSpeciale(hydrant).getCode() : null;

        StringBuilder sb = new StringBuilder();

        sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getInsee());
        return sb.append(String.format("_%04d", hydrant.getNumeroInterne())).toString();
    }

    /**
     * <code <numéro interne> (sans contrainte)
     * Exemple : 7280
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero49(Hydrant hydrant) {
        return hydrant.getNumeroInterne().toString();
    }

    /**
     * <code insee commune (5 caractères)>-<numéro interne (4 caractères)>
     * <p>
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
     * <code insee commune (5 caractères)>.<numéro interne (5 caractères)>
     * <p>
     * Exemple : 95000-00001
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero95(Hydrant hydrant) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHydrantCommune(hydrant).getInsee());
        sb.append(".");
        return sb.append(String.format("%05d", hydrant.getNumeroInterne())).toString();
    }

    /**
     * <code insee commune (5 caractères)>-<numéro interne>
     * <p>
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
     * <Code nature pour les PIBI ou les RI, PN pour les autres> <code commune>
     * <numéro interne>
     * <p>
     * Exemple : PI TLN 12
     *
     * @param hydrant
     * @return
     */
    protected static String computeNumero83(Hydrant hydrant) {
        String codeZS = (getHydrantZoneSpeciale(hydrant) != null) ? getHydrantZoneSpeciale(hydrant).getCode() : null;
        StringBuilder sb = new StringBuilder();

        String codeNature = context.select(TYPE_HYDRANT_NATURE.CODE)
                .from(TYPE_HYDRANT_NATURE)
                .where(TYPE_HYDRANT_NATURE.ID.eq(hydrant.getNature()))
                .fetchOneInto(String.class);

        if ("PIBI".equals(hydrant.getCode())) {
            sb.append(codeNature).append(" ");

            /**
             * TODO : vérifier avec le SDIS 83 si il y a besoin de garder ce comportement
             * champ pena non accessible depuis la nouvelle fiche, non pris en compte lors de la création du PEI
             * (numérotation intervenant avant ajout en base dans hydrant_pibi
            */

            if(hydrant.getId() != null) {
                HydrantPibi pibi = context.selectFrom(HYDRANT_PIBI)
                        .where(HYDRANT_PIBI.ID.eq(hydrant.getId()))
                        .fetchOneInto(HydrantPibi.class);

                if (pibi.getPena() != null && !"PA".equals(codeNature)) {
                    // Pibi lié à un Pena : on double le code zone ou commune
                    sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getCode()).append(" ");
                }
            }

        } else if ("RI".equals(codeNature)) {
            sb.append("RI ");
        } else {
            sb.append("PN ");
        }

        sb.append(codeZS != null ? codeZS : getHydrantCommune(hydrant).getCode());
        return sb.append(" ").append(hydrant.getNumeroInterne()).toString();
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
    public static ZoneSpeciale computeZoneSpeciale(Hydrant hydrant) {
        try {
            return context.selectFrom(ZONE_SPECIALE)
              .where(ZONE_SPECIALE.GEOMETRIE.isNotNull().and("ST_DISTANCE({0}, {1}) <= 0", hydrant.getGeometrie().toText(), ZONE_SPECIALE.GEOMETRIE))
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
     * Redéfinit le code, la zone spéciale éventuelle, le numéro interne et le
     * numéro
     *
     * @param hydrant Les informations de l'hydrant
     * @param typeHydrant Le type d'hydrant si celui-ci n'existe pas encore en base
     */
    public static Hydrant setCodeZoneSpecAndNumeros(Hydrant hydrant, String typeHydrant) {
        // Code
        String code = context
          .select(TYPE_HYDRANT.CODE)
          .from(HYDRANT)
          .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
          .join(TYPE_HYDRANT).on(TYPE_HYDRANT.ID.eq(TYPE_HYDRANT_NATURE.TYPE_HYDRANT))
          .where(HYDRANT.ID.eq(hydrant.getId()))
          .fetchOneInto(String.class);

        hydrant.setCode((code != null) ? code : typeHydrant);

        // Zone Spéciale
        ZoneSpeciale zp = computeZoneSpeciale(hydrant);
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
            case M_77:
            // Même numérotation interne que le 77
            case M_91:
                return NumeroUtilRepository.computeNumeroInterne77(hydrant);
            case M_42:
                return NumeroUtilRepository.computeNumeroInterne42(hydrant);
            case M_86:
                return NumeroUtilRepository.computeNumeroInterne86(hydrant);
            case M_78:
                return NumeroUtilRepository.computeNumeroInterne78(hydrant);
            case M_49:
                return NumeroUtilRepository.computeNumeroInterne49(hydrant);
            case M_95:
                return NumeroUtilRepository.computeNumeroInterne95(hydrant);
            default:
                return NumeroUtilRepository.computeNumeroInterne83(hydrant);
        }
    }

    public static Integer computeNumeroInterne77(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            // Premier numéro interne dispo pour la commune (ou de la zone spéciale)
            numInterne = context
              .resultQuery("select remocra.nextNumeroInterne(null, null, {0}, {1}, true)",
                (hydrant.getZoneSpeciale() == null) ? hydrant.getCommune() : DSL.val(null, SQLDataType.BIGINT),
                (hydrant.getZoneSpeciale() != null) ? hydrant.getZoneSpeciale() : DSL.val(null, SQLDataType.BIGINT))
              .fetchOneInto(Integer.class);

        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne;
    }

    public static Integer computeNumeroInterne42(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            // Incrementation automatique de numero interne
            numInterne = context.select(DSL.max(HYDRANT.NUMERO_INTERNE))
              .from(HYDRANT)
              .where(HYDRANT.COMMUNE.eq(hydrant.getCommune()))
              .fetchOneInto(Integer.class);
            // On prend le suivant
            numInterne ++;
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne  ;
    }

    public static Integer computeNumeroInterne78(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            // Incrementation automatique de numero interne
            numInterne = context.select(DSL.max(HYDRANT.NUMERO_INTERNE))
              .from(HYDRANT)
              .where(HYDRANT.COMMUNE.eq(hydrant.getCommune()))
              .fetchOneInto(Integer.class);

            // On prend le suivant
            numInterne ++;
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne  ;
    }

    public static Integer computeNumeroInterne83(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            // Dernier numéro interne dispo pour le couple (type hydrant, commune) ou (type hydrant, zone spéciale)
            Integer idTypeHydrant = context
              .select(TYPE_HYDRANT.ID)
              .from(HYDRANT)
              .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
              .join(TYPE_HYDRANT).on(TYPE_HYDRANT.ID.eq(TYPE_HYDRANT_NATURE.TYPE_HYDRANT))
              .where(HYDRANT.ID.eq(hydrant.getId()))
              .fetchOneInto(Integer.class);

            numInterne = context
              .resultQuery("select remocra.nextNumeroInterne(null, {0}, {1}, {2}, false)",
                DSL.val(idTypeHydrant, SQLDataType.BIGINT),
                (hydrant.getZoneSpeciale() == null) ? hydrant.getCommune() : DSL.val(null, SQLDataType.BIGINT),
                (hydrant.getZoneSpeciale() != null) ? hydrant.getZoneSpeciale() : DSL.val(null, SQLDataType.BIGINT))
              .fetchOneInto(Integer.class);
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne;
    }

    public static Integer computeNumeroInterne86(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            // Dernier numéro interne dispo sans contrainte particulière
            numInterne = context.resultQuery("select remocra.nextNumeroInterne(null, null, null, null, false)").fetchOneInto(Integer.class);
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne;
    }

    /**
     * Premier numéro interne disponible, sans remplissage
     */
    public static Integer computeNumeroInterne49(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            numInterne = context.select(DSL.max(HYDRANT.NUMERO_INTERNE))
              .from(HYDRANT)
              .fetchOneInto(Integer.class);
            numInterne++;
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne;

    }

    protected static Integer computeNumeroInterne95(Hydrant hydrant) {
        // Retour du numéro interne s'il existe
        if (hydrant.getNumeroInterne() != null && hydrant.getId() != null) {
            return hydrant.getNumeroInterne();
        }
        Integer numInterne = null;
        try {
            String codeNature = context.select(TYPE_HYDRANT_NATURE_DECI.CODE)
                    .from(TYPE_HYDRANT_NATURE_DECI)
                    .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(hydrant.getNatureDeci()))
                    .fetchOneInto(String.class);
            if("PRIVE".equals(codeNature) || "CONVENTIONNE".equals(codeNature) ){
                numInterne = context.select(DSL.max(HYDRANT.NUMERO_INTERNE))
                        .from(HYDRANT)
                        .where(HYDRANT.COMMUNE.eq(hydrant.getCommune()))
                        .and(HYDRANT.NUMERO_INTERNE.greaterOrEqual(new Integer(1000)))
                        .fetchOneInto(Integer.class);
                numInterne++;
            } else {
                numInterne = context.select(DSL.max(HYDRANT.NUMERO_INTERNE))
                        .from(HYDRANT)
                        .where(HYDRANT.COMMUNE.eq(hydrant.getCommune()))
                        .and(HYDRANT.NUMERO_INTERNE.lessThan(new Integer(1000)))
                        .fetchOneInto(Integer.class);
                numInterne++;
            }
        } catch (Exception e) {
            numInterne = 99999;
        }
        return numInterne;
    }

    private static ZoneSpeciale getHydrantZoneSpeciale(Hydrant h) {
      return context.selectFrom(ZONE_SPECIALE)
        .where(ZONE_SPECIALE.ID.eq(h.getZoneSpeciale()))
        .fetchOneInto(ZoneSpeciale.class);
    }

    private static Commune getHydrantCommune(Hydrant h) {
      return context.selectFrom(COMMUNE)
        .where(COMMUNE.ID.equal(h.getCommune()))
        .fetchOneInto(Commune.class);
    }
}
