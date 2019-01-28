package fr.sdis83.remocra.database;

import android.provider.BaseColumns;

/**
 * Created by jpt on 06/08/13.
 */
public final class TourneeTable implements BaseColumns {

    public static final String TABLE_NAME = "tournee";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_NAME_DEB_SYNC = "debSync";
    public static final String COLUMN_NAME_LAST_SYNC = "lastSync";
    public static final String COLUMN_NB_HYDRANT = "nbHydrant";
    public static final String COLUMN_POURCENT = "pourcent";

    public static final String FIELD_TOTAL_HYDRANT = "totalHydrant";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NOM+" STRING not null,"+
            COLUMN_NAME_DEB_SYNC + " INTEGER null," +
            COLUMN_NAME_LAST_SYNC + " INTEGER null," +
            COLUMN_NB_HYDRANT + " INTEGER not null, " +
            COLUMN_POURCENT + " INTEGER" +
            " )";

}
