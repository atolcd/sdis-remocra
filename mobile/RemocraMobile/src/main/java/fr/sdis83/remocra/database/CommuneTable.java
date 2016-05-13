package fr.sdis83.remocra.database;

import android.provider.BaseColumns;

/**
 * Created by jpt on 06/08/13.
 */
public final class CommuneTable implements BaseColumns {

    public static final String TABLE_NAME = "commune";

    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LIBELLE = "libelle";
    public static final String COLUMN_INSEE = "insee";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null," +
            COLUMN_INSEE + " TEXT" +
            " )";

}
