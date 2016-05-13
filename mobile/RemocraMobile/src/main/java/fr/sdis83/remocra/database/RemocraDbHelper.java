package fr.sdis83.remocra.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jpt on 06/08/13.
 */
public final class RemocraDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "remocra.db";
    private static final String TAG = "RemocraDbHelper";

    public RemocraDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(TourneeTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(HydrantTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(AnomalieTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(AnomalieNatureTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(CommuneTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(DiametreTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(DomaineTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(MarqueTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(MateriauTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(ModeleTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(NatureTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(PositionnementTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(VolConstateTable.CREATE_TABLE);
    }

    private void deleteAllTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + HydrantTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TourneeTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnomalieNatureTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnomalieTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CommuneTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiametreTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DomaineTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MarqueTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MateriauTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ModeleTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NatureTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PositionnementTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VolConstateTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        deleteAllTable(db);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Downgrade database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        deleteAllTable(db);
        onCreate(db);
    }
}
