package fr.sdis83.remocra.domain.dialect;

import java.sql.Types;

import org.hibernate.spatial.dialect.postgis.PostgisDialect;

public class RemocraPostgreSQLDialect extends PostgisDialect {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RemocraPostgreSQLDialect() {

        // On ne met pas de limite par defaut : utile lors de la génération du
        // schema
        registerColumnType(Types.VARCHAR, Integer.MAX_VALUE, "character varying");

        // TODO : N'a pas l'air d'être utile suite à mise jour du dialect avec
        // la mise à jour de hibernate 3 -> 4
        // Manque dans le PostgisDialect... On met n'importe quoi...
        // registerColumnType(Types.ARRAY, Integer.MAX_VALUE, "integer");
    }
}
