package fr.sdis83.remocra.database;

/**
 * Created by rbo on 18/02/19.
 */
public final class NatureDeciTable implements ReferentielTable {

    public static final String TABLE_NAME = "natureDeci";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null" +
            " )";
}
