package fr.sdis83.remocra.parser;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.sdis83.remocra.database.ReferentielTable;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by jpt on 23/09/13.
 */
public class RemocraParser {

    HashMap<String, AbstractRemocraParser> mapParser = new HashMap<String, AbstractRemocraParser>();
    ArrayList<AbstractRemocraParser> listParser = new ArrayList<AbstractRemocraParser>();

    protected final Context context;

    private HashMap<Uri, ArrayList<ContentValues>> contents = new HashMap<Uri, ArrayList<ContentValues>>();

    public RemocraParser(Context context) {
        this(context, false);
    }

    public RemocraParser(Context context, boolean localImport) {
        this.context = context;
        if (!localImport) {
            this.registerParser(new UserParser(this));
            this.registerParser(new NatureParser(this));
            this.registerParser(new CommuneParser(this));
            this.registerParser(new DiametreParser(this));
            this.registerParser(new DomaineParser(this));
            this.registerParser(new MarqueParser(this));
            this.registerParser(new MateriauParser(this));
            this.registerParser(new PositionnementParser(this));
            this.registerParser(new AnomaliesParser(this));
            this.registerParser(new VolConstateParser(this));
            this.registerParser(new NatureDeciParser(this));

        }
        this.registerParser(new TourneeParser(this));
    }

    private void registerParser(AbstractRemocraParser parser) {
        listParser.add(parser);
        for (String name : parser.getNames()) {
            mapParser.put(name, parser);
        }
        parser.onAttach(this);
    }

    public String parse(XmlPullParser... parsers) {
        try {
            // Phase 1 : parsage de tout les fichiers
            for (XmlPullParser parser : parsers) {
                this.process(parser);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public Integer insertIntoDatabase() {
        int nb = 0;
        // Phase 2 : insertions des données
        final ContentResolver contentResolver = context.getContentResolver();
        for (Uri uri : contents.keySet()) {
            List<ContentValues> lst = contents.get(uri);
            contentResolver.delete(uri, null, null);
            if (lst.size() > 0) {
                nb += contentResolver.bulkInsert(uri, lst.toArray(new ContentValues[lst.size()]));
            }
            contents.get(uri).clear();
            contentResolver.notifyChange(uri, null);
        }

        return nb;
    }

    private void process(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        if (xmlParser != null) {
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                String name = xmlParser.getName();
                if (name != null && mapParser.containsKey(name)) {
                    AbstractRemocraParser parser = mapParser.get(name);
                    parser.processNode(xmlParser, name);
                }
            }
        }
    }

    public void addContent(AbstractRemocraParser parser, Uri uri, ContentValues values) {
        if (!this.contents.containsKey(uri)) {
            this.contents.put(uri, new ArrayList<ContentValues>());
            parser.onAddUri(uri);
        }
        if (values != null) {
            if (!values.containsKey(BaseColumns._ID)) {
                values.put(BaseColumns._ID, this.contents.get(uri).size() + 1);
            }
            this.contents.get(uri).add(values);
        }
    }

    public String getLibelleFromCodeReferentiel(String code, Uri uri) {
        ContentValues value = getContentFromCodeReferentiel(code, uri);
        if (value != null) {
            return value.getAsString(ReferentielTable.COLUMN_LIBELLE);
        }
        return null;
    }

    public Integer getIdFromCodeReferentiel(String code, Uri uri) {
        ContentValues value = getContentFromCodeReferentiel(code, uri);
        if (value != null) {
            return value.getAsInteger(ReferentielTable._ID);
        }
        return null;

    }

    public ContentValues getContentFromCodeReferentiel(String code, Uri uri) {
        // récupération depuis le cache local
        if (contents.containsKey(uri)) {
            for (ContentValues value : contents.get(uri)) {
                if (value.getAsString(ReferentielTable.COLUMN_CODE).equals(code)) {
                    return value;
                }
            }
        } else {
            // récupération depuis la bdd
            return DbUtils.getContentFromCodeReferentiel(context.getContentResolver(), uri, code);
        }
        return null;
    }
}
