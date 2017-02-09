package fr.sdis83.remocra.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sdis83.remocra.HydrantActivity;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.ReferentielTable;
import fr.sdis83.remocra.fragment.components.EditDate;
import fr.sdis83.remocra.util.DbUtils;

public abstract class AbstractHydrant extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int layout;
    private final String fieldRead;

    protected Cursor cursor;
    protected HydrantTable.TYPE_SAISIE typeSaisie = HydrantTable.TYPE_SAISIE.LECT;
    private Map<Integer, BindableData> bindableData = new HashMap<Integer, BindableData>();
    protected HydrantActivity mListener;

    protected AbstractHydrant(int layout, String fieldRead) {
        this.layout = layout;
        this.fieldRead = fieldRead;
    }

    public CharSequence getTabTitle() {
        return "-";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check that the container activity is HydrantActivity
        try {
            mListener = (HydrantActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be HydrantActivity");
        }
    }

    public void setCursor(HydrantTable.TYPE_SAISIE typeSaisie, Cursor cursor) {
        this.cursor = cursor;
        this.typeSaisie = typeSaisie;
        if (mListener != null) {
            if (cursor.getLong(cursor.getColumnIndex(fieldRead)) == 1) {
                int position = getArguments().getInt("position");
                mListener.setTabRead(position);
            }
        }
    }

    protected void addBindableData(int resource, String column, Class cls) {
        if (!bindableData.containsKey(resource)) {
            bindableData.put(resource, new BindableData(column, cls));
        }
    }

    protected void onBeforeBind(Cursor cursor) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(this.layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromCursor();
    }

    public void loadDataFromCursor() {
        if (getView() != null && this.cursor != null && this.cursor.moveToFirst()) {
            onBeforeBind(cursor);
            for (Map.Entry<Integer, BindableData> data : this.bindableData.entrySet()) {
                bindField(data.getKey(), data.getValue());
            }
        }
    }

    public ContentValues getDataToSave() {
        if (getView() == null) {
            return null;
        }
        final ContentValues values = new ContentValues();
        for (Map.Entry<Integer, BindableData> data : this.bindableData.entrySet()) {
            View view = getView().findViewById(data.getKey());
            if (view != null && view.isShown()) {
                Class cls = data.getValue().cls;
                if (EditText.class.equals(cls)) {
                    Editable editText = ((EditText) view).getText();
                    values.put(data.getValue().column, editText == null ? null : editText.toString());
                } else if (Spinner.class.equals(cls)) {
                    values.put(data.getValue().column, ((Spinner) view).getSelectedItemId());
                } else if (CheckBox.class.equals(cls)) {
                    values.put(data.getValue().column, ((CheckBox) view).isChecked());
                } else if (ListView.class.equals(cls)) {
                    long[] ids = ((ListView) view).getCheckedItemIds();
                    List<Long> lstIds = new ArrayList<Long>(ids.length);
                    for (long id : ids) {
                        lstIds.add(id);
                    }
                    String result = TextUtils.join(",", lstIds);
                    values.put(data.getValue().column, result);
                } else if (EditDate.class.equals(cls)) {
                    Editable editText = ((EditDate) view).getText();
                    String value = null;
                    if(editText != null) {
                        try {
                            value = String.valueOf(DbUtils.DATE_FORMAT_EDIT.parse(editText.toString()).getTime());
                        } catch (Exception e) {
                            throw new AssertionError("Format de date invalide");
                        }
                    }
                    values.put(data.getValue().column, editText == null ? null : value);
                }
            }
        }
        return values;
    }

    protected void bindField(int id) {
        if (bindableData.containsKey(id) && this.cursor != null) {
            BindableData data = bindableData.get(id);
            bindField(id, data);
        }
    }

    private void bindField(int idView, BindableData data) {
        View view = getView().findViewById(idView);
        int colIndex = cursor.getColumnIndex(data.column);
        if (view != null && view.isShown() && colIndex != -1) {
            Class cls = data.cls;
            if (EditText.class.equals(cls)) {
                ((EditText) view).setText(cursor.getString(colIndex));
            } else if (TextView.class.equals(cls)) {
                ((TextView) view).setText(cursor.getString(colIndex));
            } else if (Spinner.class.equals(cls)) {
                Spinner spinner = ((Spinner) view);
                int position = DbUtils.findPosition(spinner.getAdapter(), cursor.getInt(colIndex));
                spinner.setSelection(position);
            } else if (CheckBox.class.equals(cls)) {
                ((CheckBox) view).setChecked(cursor.getInt(colIndex) > 0);
            } else if (ListView.class.equals(cls)) {
                String ids = cursor.getString(colIndex);
                if (ids != null) {
                    String[] lstIds = ids.split(",");
                    ListView list = ((ListView) view);
                    CursorAdapter adapter = (CursorAdapter) list.getAdapter();
                    for (String id : lstIds) {
                        if (!TextUtils.isEmpty(id)) {
                            int position = DbUtils.findPosition(adapter, Long.valueOf(id).intValue());
                            list.setItemChecked(position, true);
                        }
                    }
                }
            } else if (EditDate.class.equals(cls)) {
                if(cursor.getString(colIndex) != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.valueOf(cursor.getString(colIndex)));
                    ((EditDate) view).setText(DbUtils.DATE_FORMAT_EDIT.format(cal.getTime()));
                }
            }
        }
    }

    protected boolean isPibi(Cursor cursor) {
        if (cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_HYDRANT) > 0) {
            return HydrantTable.TYPE_PIBI.equals(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_HYDRANT)));
        }
        return false;
    }


    protected void loadSpinner(int id, Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putString("URI", uri.toString());
        getLoaderManager().restartLoader(id, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri uri = Uri.parse(bundle.getString("URI"));
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                cursor,
                new String[]{ReferentielTable.COLUMN_LIBELLE},
                new int[]{android.R.id.text1},
                0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spin = (Spinner) getView().findViewById(id);
        spin.setAdapter(adapter);
        bindField(id);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        if (getView() != null) {
            Spinner spin = (Spinner) getView().findViewById(id);
            if (spin != null) {
                spin.setAdapter(null);
            }
        }
    }

    protected class BindableData {
        public String column;
        public Class cls;

        public BindableData(String column, Class cls) {
            this.column = column;
            this.cls = cls;
        }
    }
}
