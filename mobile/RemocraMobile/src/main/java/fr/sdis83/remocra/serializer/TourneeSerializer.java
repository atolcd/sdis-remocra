package fr.sdis83.remocra.serializer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.TourneeTable;
import fr.sdis83.remocra.parser.AbstractRemocraParser;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by Jean-Philippe on 05/01/2016.
 * Permet de serialiser une tournee, pour pouvoir la reimporter si besoin
 */
public class TourneeSerializer extends AbstractSerializer {

    static final String TOURNEES_XSI = "xsi:tournees";
    static final String TOURNEE = "tournee";

    private final HydrantSerializer hydrantSerializer;
    private boolean fullExportMode = false;


    public TourneeSerializer(Context context) {
        super(context);
        this.hydrantSerializer = new HydrantSerializer(context);
    }

    public void serialize() throws IOException {
        this.fullExportMode = true;
        Cursor cursor = this.getContext().getContentResolver().query(RemocraProvider.CONTENT_TOURNEE_URI, null, null, null, TourneeTable._ID);
        writeFile(cursor);

    }

    //En upload on appelle cette fonction ça retourne un xml contenant les tournées
    public String serialize(Cursor cursor) throws IOException {
        this.fullExportMode = false;
        StringWriter writer = new StringWriter();
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(writer);
        serializer.startDocument(AbstractRemocraParser.ns, false);
        addTournees(serializer, cursor);
        serializer.endDocument();
        writeFile(cursor);
        return writer.toString();
    }

    private void writeFile(Cursor cursor) throws IOException {
        String login = GlobalRemocra.getInstance(getContext()).getLogin();
        String filename = DbUtils.DATE_FORMAT_LIGHT.format(new Date()) + "_" + login + "_export.xml";
        File directory = Environment.getExternalStoragePublicDirectory("Remocra");
        directory.mkdirs();
        if (!directory.exists()) {
            directory = this.getContext().getFilesDir();
        }
        File f = new File(directory, filename);
        FileWriter writer = new FileWriter(f);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(writer);
        serializer.startDocument(AbstractRemocraParser.ns, false);
        addTournees(serializer, cursor);
        serializer.endDocument();
        writer.flush();
        writer.close();
        Log.i("TourneeSerialize", "Fichier backup cree : " + f.getAbsolutePath());
    }

    private void addTournees(XmlSerializer serializer, Cursor cursor) throws IOException {
        serializer.startTag(AbstractRemocraParser.ns, TOURNEES_XSI);
        serializer.attribute(AbstractRemocraParser.ns, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                addTournee(serializer, cursor);
                cursor.moveToNext();
            }
        }
        serializer.endTag(AbstractRemocraParser.ns, TOURNEES_XSI);
    }

    private void addTournee(XmlSerializer serializer, Cursor cursor) throws IOException {
        serializer.startTag(AbstractRemocraParser.ns, TOURNEE);
        int tourneeId = cursor.getInt(cursor.getColumnIndex(TourneeTable._ID));

        addBalise(serializer, "id", tourneeId);
        addBalise(serializer, "nom", cursor.getString(cursor.getColumnIndex(TourneeTable.COLUMN_NOM)));
        addBaliseDate(serializer, "debSync", cursor.getLong(cursor.getColumnIndex(TourneeTable.COLUMN_NAME_DEB_SYNC)));
        addBaliseDate(serializer, "lastSync", cursor.getLong(cursor.getColumnIndex(TourneeTable.COLUMN_NAME_LAST_SYNC)));
        if(fullExportMode){
            String[] params = {String.valueOf(tourneeId)};
            Cursor cursorHydrant = this.getContext().getContentResolver().query(RemocraProvider.CONTENT_HYDRANT_URI,
                    null,
                    HydrantTable.COLUMN_TOURNEE + "=?",
                    params, HydrantTable._ID);

            hydrantSerializer.serialize(serializer, cursorHydrant);
        }else {
            Double valeurPrct = (Math.ceil(cursor.getInt(cursor.getColumnIndex(TourneeTable.FIELD_TOTAL_HYDRANT))));
            Integer prct = valeurPrct.intValue();
            addBalise(serializer,"pourcent",prct.toString());
        }
        serializer.endTag(AbstractRemocraParser.ns, TOURNEE);
    }
}
