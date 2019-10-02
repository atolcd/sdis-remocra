package fr.sdis83.remocra.database;

/**
 * Created by yaz on 26/09/19.
 */
public final class TypeSaisiesTable implements ReferentielTable {

    public static final String TABLE_NAME = "typeSaisies";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null" +
            " )";

}
