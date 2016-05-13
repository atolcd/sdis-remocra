package fr.sdis83.remocra.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by Jean-Philippe on 08/01/2016.
 */
public class FileAdapter extends ArrayAdapter<File> implements AdapterView.OnItemClickListener {

    private final List<File> selectedFiles = new ArrayList<File>();

    public FileAdapter(Context context) {
        super(context, R.layout.file_list_item, new ArrayList<File>());
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        convertView = inflater.inflate(R.layout.file_list_item, parent, false);
        CheckedTextView name = (CheckedTextView) convertView.findViewById(R.id.text1);

        File file = getItem(position);
        name.setText(extractdata(file));
        name.setChecked(selectedFiles.contains(file));

        return convertView;
    }

    private String extractdata(File file) {
        String[] data = file.getName().split("_");
        String date;
        try {
            Date dateFile = DbUtils.DATE_FORMAT_LIGHT.parse(data[0]);
            date = DbUtils.DATE_HEURE_FORMAT_FR.format(dateFile);
        } catch (ParseException e) {
            date = "(date inconnue)";
        }
        String user = data[1];
        return date + " - " + user + " - " + Formatter.formatShortFileSize(getContext(), file.length());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = getItem(position);
        if (selectedFiles.contains(file)) {
            selectedFiles.remove(file);
        } else {
            selectedFiles.add(file);
        }
        notifyDataSetChanged();
    }

    public List<File> getSelection() {
        return this.selectedFiles;
    }

    public void replace(List<File> files) {
        this.getSelection().clear();
        this.setNotifyOnChange(false);
        this.clear();
        this.setNotifyOnChange(true);
        this.addAll(files);
    }
}
