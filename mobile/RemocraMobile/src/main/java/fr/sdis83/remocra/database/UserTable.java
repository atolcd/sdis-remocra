package fr.sdis83.remocra.database;

import android.provider.BaseColumns;

/**
 * Created by jpt on 06/08/13.
 */
public final class UserTable implements BaseColumns {

    public static final String TABLE_NAME = "user";

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_CONTROLE = "controle";
    public static final String COLUMN_RECONNAISSANCE = "reconnaissance";
    public static final String COLUMN_RECEPTION = "reception";
    public static final String COLUMN_MCO = "mco";
    public static final String COLUMN_HYDRANT = "hydrant";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT not null," +
            COLUMN_PASSWORD + " TEXT not null," +
            COLUMN_RECEPTION + " TEXT not null," +
            COLUMN_CONTROLE + " TEXT not null," +
            COLUMN_RECONNAISSANCE + " TEXT not null," +
            COLUMN_MCO + " TEXT not null," +
            COLUMN_HYDRANT + " TEXT not null" +
            " )";


}
