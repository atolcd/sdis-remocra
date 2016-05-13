package fr.sdis83.remocra.database;

/**
 * Created by jpt on 06/08/13.
 */
public final class ModeleTable implements ReferentielTable {

    public static final String TABLE_NAME = "modele";

    public static final String COLUMN_MARQUE = "marque";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null," +
            COLUMN_MARQUE + " INTEGER not null" +
            " )";

}
