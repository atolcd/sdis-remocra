package fr.sdis83.remocra.database;

import android.provider.BaseColumns;

/**
 * Created by jpt on 06/08/13.
 */
public final class AnomalieTable implements BaseColumns {

    public static final String TABLE_NAME = "anomalie";

    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LIBELLE = "libelle";
    public static final String COLUMN_CRITERE = "critere";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null," +
            COLUMN_CRITERE + " TEXT" +
            " )";
}
