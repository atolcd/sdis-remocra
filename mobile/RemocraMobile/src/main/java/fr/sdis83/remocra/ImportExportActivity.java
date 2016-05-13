package fr.sdis83.remocra;

import android.app.ActionBar;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import fr.sdis83.remocra.adapter.FileAdapter;
import fr.sdis83.remocra.parser.RemocraParser;
import fr.sdis83.remocra.serializer.TourneeSerializer;
import fr.sdis83.remocra.util.FileListLoader;

public class ImportExportActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<File>>, AdapterView.OnItemLongClickListener {

    private static final int FILES_LOADER = 0;
    private ListView mListFiles;
    private Button mBtnImporter;
    private Button mBtnDelete;
    private Button mBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mBtnImporter = (Button) findViewById(R.id.import_button);
        mBtnDelete = (Button) findViewById(R.id.delete_button);
        mBtnSend = (Button) findViewById(R.id.send_button);
        mListFiles = (ListView) findViewById(R.id.list);
        FileAdapter adapter = new FileAdapter(this);
        DataSetObserver dataObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                ImportExportActivity.this.onDataChange();

            }
        };
        adapter.registerDataSetObserver(dataObserver);
        mListFiles.setAdapter(adapter);
        mListFiles.setOnItemClickListener(adapter);
        mListFiles.setOnItemLongClickListener(this);

        getSupportLoaderManager().initLoader(FILES_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<File>> onCreateLoader(int i, Bundle bundle) {
        Log.i("Loader", "on passe ici onCreateLoader");
        File directory = Environment.getExternalStoragePublicDirectory("Remocra");
        directory.mkdirs();
        if (!directory.exists()) {
            directory = this.getFilesDir();
        }
        return new FileListLoader(this.getApplicationContext(), directory);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> files) {
        Log.i("Loader", "on passe ici onLoadFinished");
        FileAdapter adapter = (FileAdapter) mListFiles.getAdapter();
        adapter.replace(files);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {

    }

    // Fonction appele lors d'un changement au niveau de la liste des fichiers
    private void onDataChange() {
        FileAdapter adapter = (FileAdapter) mListFiles.getAdapter();
        mBtnImporter.setEnabled(adapter.getSelection().size() == 1);
        mBtnSend.setEnabled(adapter.getSelection().size() == 1);
        mBtnDelete.setEnabled(adapter.getSelection().size() > 0);
    }

    public void doExport(View view) {
        Toast toast;
        try {
            TourneeSerializer serializer = new TourneeSerializer(getApplicationContext());
            serializer.serialize();
            toast = Toast.makeText(this, getString(R.string.export_success), Toast.LENGTH_SHORT);
            getSupportLoaderManager().restartLoader(FILES_LOADER, null, this);
        } catch (IOException e) {
            toast = Toast.makeText(this, getString(R.string.error_export, e.getMessage()), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
        toast.show();
    }

    public void doImport(View view) {
        FileAdapter adapter = (FileAdapter) mListFiles.getAdapter();
        if (adapter.getSelection().size() != 1) {
            return;
        }
        File file = adapter.getSelection().get(0);
        if (file == null || !file.exists()) {
            return;
        }
        FileInputStream reader;
        try {
            reader = new FileInputStream(file);
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(reader, null);
            RemocraParser parser = new RemocraParser(this, true);
            parser.parse(xmlPullParser);
            reader.close();
            Toast toast;
            int nb = parser.insertIntoDatabase();
            if (nb > 0) {
                toast = Toast.makeText(this, getString(R.string.import_success, nb), Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doDelete(View view) {
        FileAdapter adapter = (FileAdapter) mListFiles.getAdapter();
        if (adapter.getSelection().size() == 0) {
            return;
        }
        for (File file : adapter.getSelection()) {
            file.delete();
        }
        getSupportLoaderManager().restartLoader(FILES_LOADER, null, this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        File file = (File) parent.getAdapter().getItem(position);
        if (file != null && file.exists()) {
            sendFile(file);
        }
        return true;
    }

    private void sendFile(File file) {
        /*
         * FIXME serait mieux d'envoyer le fichier (il faudra certainement utiliser un content prodider)
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, file.toURI());
        shareIntent.setType("text/xml");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));*/
        Toast.makeText(this, getString(R.string.file_location, file.toURI()), Toast.LENGTH_LONG).show();
    }

    public void doSendFile(View view) {
        FileAdapter adapter = (FileAdapter) mListFiles.getAdapter();
        if (adapter.getSelection().size() != 1) {
            return;
        }
        File file = adapter.getSelection().get(0);
        if (file != null && file.exists()) {
            sendFile(file);
        }
    }
}
