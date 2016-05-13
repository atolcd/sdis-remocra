package fr.sdis83.remocra.database;

import android.provider.BaseColumns;

/**
 * Created by jpt on 08/10/13.
 */
public class AnomalieNatureTable implements BaseColumns {

    public static final String TABLE_NAME = "anomalieNature";

    public static final String COLUMN_ANOMALIE = "anomalieId";
    public static final String COLUMN_NATURE = "natureId";
    public static final String COLUMN_TYPE_SAISIE = "typeSaisie";
    public static final String COLUMN_VALEUR = "valeur";
    public static final String COLUMN_VALEUR_HBE = "valeurHbe";

    public static final String ANOMALIE_JOIN_NATURE = AnomalieTable.TABLE_NAME +
            " INNER JOIN " + TABLE_NAME + " on (" + AnomalieTable.TABLE_NAME + "." + AnomalieTable._ID + "=" + TABLE_NAME + "." + COLUMN_ANOMALIE + ")";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ANOMALIE + " INTEGER not null," +
            COLUMN_NATURE + " INTEGER not null," +
            COLUMN_TYPE_SAISIE + " TEXT," +
            COLUMN_VALEUR + " INTEGER," +
            COLUMN_VALEUR_HBE + " INTEGER" +
            ")";
}
