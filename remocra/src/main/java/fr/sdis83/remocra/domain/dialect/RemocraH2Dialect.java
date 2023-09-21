package fr.sdis83.remocra.domain.dialect;

import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;

public class RemocraH2Dialect extends GeoDBDialect {
  /** */
  private static final long serialVersionUID = 1L;

  public RemocraH2Dialect() {
    super();

    // TODO : N'a pas l'air d'être utile suite à mise jour du dialect avec
    // la mise à jour de hibernate 3 -> 4
    // //
    // registerColumnType(Types.VARCHAR, Integer.MAX_VALUE, "longvarchar");
    //
    // // Manque dans le GeoDBDialect... On met n'importe quoi...
    // registerColumnType(Types.STRUCT, Integer.MAX_VALUE, "integer");
  }
}
