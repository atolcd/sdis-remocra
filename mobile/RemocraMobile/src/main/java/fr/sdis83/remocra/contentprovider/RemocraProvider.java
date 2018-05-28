package fr.sdis83.remocra.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import fr.sdis83.remocra.database.AnomalieNatureTable;
import fr.sdis83.remocra.database.AnomalieTable;
import fr.sdis83.remocra.database.CommuneTable;
import fr.sdis83.remocra.database.DiametreTable;
import fr.sdis83.remocra.database.DomaineTable;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.MarqueTable;
import fr.sdis83.remocra.database.MateriauTable;
import fr.sdis83.remocra.database.ModeleTable;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.database.PositionnementTable;
import fr.sdis83.remocra.database.RemocraDbHelper;
import fr.sdis83.remocra.database.TourneeTable;
import fr.sdis83.remocra.database.UserTable;
import fr.sdis83.remocra.database.VolConstateTable;

/**
 * Created by jpt on 07/08/13.
 */
public class RemocraProvider extends ContentProvider {

    public static final String EMPTY_FIELD = "-";

    private static final String AUTHORITY = "fr.sdis83.remocra.contentprovider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public enum TYPE_NATURE {
        PIBI,
        PENA
    }

    private static final int TOURNEE = 10;
    private static final int TOURNEE_ID = 11;
    private static final int HYDRANT = 20;
    private static final int HYDRANT_ID = 21;
    private static final int ANOMALIE = 30;
    private static final int COMMUNE = 40;
    private static final int DIAMETRE = 50;
    private static final int DOMAINE = 60;
    private static final int MARQUE = 70;
    private static final int MATERIAU = 80;
    private static final int MODELE = 90;
    private static final int MODELE_BY_MARQUE = 91;
    private static final int NATURE = 100;
    private static final int NATURE_BY_TYPE = 101;
    private static final int POSITIONNEMENT = 110;
    private static final int VOL_CONSTATE = 120;
    private static final int ANOMALIE_NATURE = 130;
    private static final int USER = 140;
    private static final int SUMMARY = 200;

    private static final String TOURNEE_PATH = "tournee";
    private static final String HYDRANT_PATH = "hydrant";
    private static final String ANOMALIE_PATH = "anomalie";
    private static final String ANOMALIE_NATURE_PATH = "anomalieNature";
    private static final String COMMUNE_PATH = "commune";
    private static final String DIAMETRE_PATH = "diametre";
    private static final String DOMAINE_PATH = "domaine";
    private static final String MARQUE_PATH = "marque";
    private static final String MATERIAU_PATH = "materiau";
    private static final String MODELE_PATH = "modele";
    private static final String NATURE_PATH = "nature";
    private static final String POSITIONNEMENT_PATH = "positionnement";
    private static final String VOL_CONSTATE_PATH = "volConstate";
    private static final String USER_PATH = "user";
    private static final String SUMMARY_PATH = "summary";


    public static final Uri CONTENT_TOURNEE_URI = BASE_CONTENT_URI.buildUpon().appendPath(TOURNEE_PATH).build();
    public static final Uri CONTENT_HYDRANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(HYDRANT_PATH).build();
    public static final Uri CONTENT_ANOMALIE_NATURE_URI = BASE_CONTENT_URI.buildUpon().appendPath(ANOMALIE_NATURE_PATH).build();
    public static final Uri CONTENT_ANOMALIE_URI = BASE_CONTENT_URI.buildUpon().appendPath(ANOMALIE_PATH).build();
    public static final Uri CONTENT_COMMUNE_URI = BASE_CONTENT_URI.buildUpon().appendPath(COMMUNE_PATH).build();
    public static final Uri CONTENT_DIAMETRE_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIAMETRE_PATH).build();
    public static final Uri CONTENT_DOMAINE_URI = BASE_CONTENT_URI.buildUpon().appendPath(DOMAINE_PATH).build();
    public static final Uri CONTENT_MARQUE_URI = BASE_CONTENT_URI.buildUpon().appendPath(MARQUE_PATH).build();
    public static final Uri CONTENT_MATERIAU_URI = BASE_CONTENT_URI.buildUpon().appendPath(MATERIAU_PATH).build();
    public static final Uri CONTENT_MODELE_URI = BASE_CONTENT_URI.buildUpon().appendPath(MODELE_PATH).build();
    public static final Uri CONTENT_NATURE_URI = BASE_CONTENT_URI.buildUpon().appendPath(NATURE_PATH).build();
    public static final Uri CONTENT_POSITIONNEMENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(POSITIONNEMENT_PATH).build();
    public static final Uri CONTENT_VOL_CONSTATE_URI = BASE_CONTENT_URI.buildUpon().appendPath(VOL_CONSTATE_PATH).build();
    public static final Uri CONTENT_USER_URI = BASE_CONTENT_URI.buildUpon().appendPath(USER_PATH).build();
    public static final Uri CONTENT_SUMMARY_URI = BASE_CONTENT_URI.buildUpon().appendPath(SUMMARY_PATH).build();

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static Uri getUriNature(TYPE_NATURE typeNature) {
        return CONTENT_NATURE_URI.buildUpon().appendPath(TYPE_NATURE.PIBI.equals(typeNature) ? "PIBI" : "PENA").build();
    }

    public static Uri getUriModeleByMarque(String marque) {
        return CONTENT_MARQUE_URI.buildUpon().appendPath(String.valueOf(marque)).appendPath("modeles").build();
    }

    static {
        sURIMatcher.addURI(AUTHORITY, TOURNEE_PATH, TOURNEE);
        sURIMatcher.addURI(AUTHORITY, TOURNEE_PATH + "/#", TOURNEE_ID);
        sURIMatcher.addURI(AUTHORITY, HYDRANT_PATH, HYDRANT);
        sURIMatcher.addURI(AUTHORITY, HYDRANT_PATH + "/#", HYDRANT_ID);
        sURIMatcher.addURI(AUTHORITY, ANOMALIE_PATH, ANOMALIE);
        sURIMatcher.addURI(AUTHORITY, ANOMALIE_NATURE_PATH, ANOMALIE_NATURE);
        sURIMatcher.addURI(AUTHORITY, COMMUNE_PATH, COMMUNE);
        sURIMatcher.addURI(AUTHORITY, DIAMETRE_PATH, DIAMETRE);
        sURIMatcher.addURI(AUTHORITY, DOMAINE_PATH, DOMAINE);
        sURIMatcher.addURI(AUTHORITY, MARQUE_PATH, MARQUE);
        sURIMatcher.addURI(AUTHORITY, MATERIAU_PATH, MATERIAU);
        sURIMatcher.addURI(AUTHORITY, MODELE_PATH, MODELE);
        sURIMatcher.addURI(AUTHORITY, MARQUE_PATH + "/*/modeles", MODELE_BY_MARQUE);
        sURIMatcher.addURI(AUTHORITY, NATURE_PATH, NATURE);
        sURIMatcher.addURI(AUTHORITY, NATURE_PATH + "/*", NATURE_BY_TYPE);
        sURIMatcher.addURI(AUTHORITY, POSITIONNEMENT_PATH, POSITIONNEMENT);
        sURIMatcher.addURI(AUTHORITY, USER_PATH, USER);
        sURIMatcher.addURI(AUTHORITY, SUMMARY_PATH, SUMMARY);
        sURIMatcher.addURI(AUTHORITY, VOL_CONSTATE_PATH, VOL_CONSTATE);

    }

    // database
    private RemocraDbHelper database;

    @Override
    public boolean onCreate() {
        database = new RemocraDbHelper(getContext());
        return false;
    }

    private String getTableName(int uriType) {
        switch (uriType) {
            case TOURNEE:
                return TourneeTable.TABLE_NAME;
            case HYDRANT:
                return HydrantTable.TABLE_NAME;
            case ANOMALIE:
                return AnomalieTable.TABLE_NAME;
            case ANOMALIE_NATURE:
                return AnomalieNatureTable.TABLE_NAME;
            case COMMUNE:
                return CommuneTable.TABLE_NAME;
            case DIAMETRE:
                return DiametreTable.TABLE_NAME;
            case DOMAINE:
                return DomaineTable.TABLE_NAME;
            case MARQUE:
                return MarqueTable.TABLE_NAME;
            case MATERIAU:
                return MateriauTable.TABLE_NAME;
            case MODELE:
            case MODELE_BY_MARQUE:
                return ModeleTable.TABLE_NAME;
            case NATURE:
            case NATURE_BY_TYPE:
                return NatureTable.TABLE_NAME;
            case POSITIONNEMENT:
                return PositionnementTable.TABLE_NAME;
            case VOL_CONSTATE:
                return VolConstateTable.TABLE_NAME;
            case USER:
                return UserTable.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown uriType: " + uriType);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        ArrayList<String> mSelectionArgs = new ArrayList<String>();
        if (selectionArgs != null) {
            Collections.addAll(mSelectionArgs, selectionArgs);
        }
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TOURNEE_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(TourneeTable._ID + "=?");
                mSelectionArgs.add(uri.getLastPathSegment());
            case TOURNEE:
                queryBuilder.setTables(TourneeTable.TABLE_NAME);
                break;
            case HYDRANT_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(HydrantTable._ID + "=?");
                mSelectionArgs.add(uri.getLastPathSegment());
            case HYDRANT:
                queryBuilder.setTables(HydrantTable.TABLE_NAME);
                break;
            case ANOMALIE_NATURE:
                queryBuilder.setTables(AnomalieNatureTable.ANOMALIE_JOIN_NATURE);
                break;
            case ANOMALIE:
                queryBuilder.setTables(AnomalieTable.TABLE_NAME);
                break;
            case COMMUNE:
                queryBuilder.setTables(CommuneTable.TABLE_NAME);
                break;
            case DIAMETRE:
                queryBuilder.setTables(DiametreTable.TABLE_NAME);
                break;
            case DOMAINE:
                queryBuilder.setTables(DomaineTable.TABLE_NAME);
                break;
            case MARQUE:
                queryBuilder.setTables(MarqueTable.TABLE_NAME);
                break;
            case MATERIAU:
                queryBuilder.setTables(MateriauTable.TABLE_NAME);
                break;
            case MODELE_BY_MARQUE:
                queryBuilder.appendWhere("(" + ModeleTable.COLUMN_MARQUE + "=? OR " + ModeleTable.COLUMN_CODE + "= \"-\")");
                sortOrder = ModeleTable.COLUMN_LIBELLE;
                mSelectionArgs.add(uri.getPathSegments().get(1));
            case MODELE:
                queryBuilder.setTables(ModeleTable.TABLE_NAME);
                break;
            case NATURE_BY_TYPE:
                queryBuilder.appendWhere(NatureTable.COLUMN_TYPE_NATURE + "=?");
                mSelectionArgs.add(uri.getLastPathSegment());
            case NATURE:
                queryBuilder.setTables(NatureTable.TABLE_NAME);
                break;
            case POSITIONNEMENT:
                queryBuilder.setTables(PositionnementTable.TABLE_NAME);
                break;
            case VOL_CONSTATE:
                queryBuilder.setTables(VolConstateTable.TABLE_NAME);
                break;
            case USER:
                queryBuilder.setTables(UserTable.TABLE_NAME);
                break;
            case SUMMARY:
                SQLiteDatabase db = database.getReadableDatabase();
                String sql = "select (select count(*) from " + TourneeTable.TABLE_NAME + " ) as tournees, (select count(*) from " + HydrantTable.TABLE_NAME + " ) as hydrants";
                return db.rawQuery(sql, null);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getReadableDatabase();
        assert (db != null);
        Cursor cursor = queryBuilder.query(db, projection, selection, mSelectionArgs.toArray(new String[mSelectionArgs.size()]), null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        assert (sqlDB != null);
        long insertId = 0;
        switch (uriType) {
            case TOURNEE:
            case HYDRANT:
            case ANOMALIE:
            case ANOMALIE_NATURE:
            case COMMUNE:
            case DIAMETRE:
            case DOMAINE:
            case MARQUE:
            case MATERIAU:
            case MODELE:
            case NATURE:
            case POSITIONNEMENT:
            case VOL_CONSTATE:
            case USER:
                insertId = sqlDB.insert(getTableName(uriType), "", contentValues);
                return Uri.withAppendedPath(uri, String.valueOf(insertId));
            default:
                throw new UnsupportedOperationException("Insert unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase sqlDbW = database.getWritableDatabase();
        assert (sqlDbW != null);
        sqlDbW.beginTransaction();
        int nb = super.bulkInsert(uri, values);
        sqlDbW.setTransactionSuccessful();
        sqlDbW.endTransaction();
        return nb;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        assert (sqlDB != null);
        switch (uriType) {
            case HYDRANT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    return sqlDB.delete(HydrantTable.TABLE_NAME, HydrantTable._ID + "=" + id, new String[] {});
                } else {
                    return sqlDB.delete(HydrantTable.TABLE_NAME, HydrantTable._ID + "=" + id + " and " + selection, new String[] {});
                }
            case TOURNEE:
            case HYDRANT:
            case ANOMALIE:
            case ANOMALIE_NATURE:
            case COMMUNE:
            case DIAMETRE:
            case DOMAINE:
            case MARQUE:
            case MATERIAU:
            case MODELE:
            case NATURE:
            case POSITIONNEMENT:
            case VOL_CONSTATE:
            case USER:
                return sqlDB.delete(getTableName(uriType), selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Delete unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        assert (sqlDB != null);
        int rowsUpdated = 0;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TOURNEE_ID:
                break;
            case TOURNEE:
                break;
            case HYDRANT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(HydrantTable.TABLE_NAME, contentValues, HydrantTable._ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(HydrantTable.TABLE_NAME, contentValues, HydrantTable._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case HYDRANT:
                rowsUpdated = sqlDB.update(HydrantTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}
