package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.adapter.CommuneAdapter;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;

public class Hydrant1 extends AbstractHydrant {

    public Hydrant1() {
        super(R.layout.hydrant1, HydrantTable.COLUMN_STATE_H1);
        addBindableData(R.id.loca_commune, HydrantTable.COLUMN_COMMUNE, EditText.class);
        addBindableData(R.id.loca_lieudit, HydrantTable.COLUMN_LIEUDIT, EditText.class);
        addBindableData(R.id.loca_voie, HydrantTable.COLUMN_VOIE, EditText.class);
        addBindableData(R.id.loca_voie2, HydrantTable.COLUMN_VOIE2, EditText.class);
        addBindableData(R.id.loca_compl, HydrantTable.COLUMN_COMPLEMENT, EditText.class);

        addBindableData(R.id.ident_num, HydrantTable.COLUMN_NUMERO_INTERNE, TextView.class);
        addBindableData(R.id.ident_pibi_scp, HydrantTable.COLUMN_NUMERO_SCP, TextView.class);

        addBindableData(R.id.ident_pibi_type, HydrantTable.COLUMN_NATURE, Spinner.class);
        addBindableData(R.id.ident_pibi_diam, HydrantTable.COLUMN_DIAMETRE, Spinner.class);
        addBindableData(R.id.ident_pena_nature, HydrantTable.COLUMN_NATURE, Spinner.class);
        addBindableData(R.id.ident_pena_hbe, HydrantTable.COLUMN_HBE, CheckBox.class);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.identification);
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
        if (!(typeSaisie.equals(HydrantTable.TYPE_SAISIE.CTRL) || typeSaisie.equals(HydrantTable.TYPE_SAISIE.VERIF))) {
            getView().findViewById(R.id.bloc_type_saisie).setVisibility(View.GONE);
        } else {
            Switch switcher = (Switch) getView().findViewById(R.id.switch_verification);
            if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_SAISIE)))) {
                switcher.setEnabled(false);
                if (typeSaisie.equals(HydrantTable.TYPE_SAISIE.VERIF)) {
                    switcher.setChecked(true);
                } else {
                    getView().findViewById(R.id.bloc_type_saisie).setVisibility(View.GONE);
                }
            } else {
                switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        HydrantTable.TYPE_SAISIE newTypeSaisie = isChecked ? HydrantTable.TYPE_SAISIE.VERIF : HydrantTable.TYPE_SAISIE.CTRL;
                        mListener.setTypeSaisie(newTypeSaisie);
                    }
                });
            }
        }
        boolean pibi = isPibi(cursor);
        getView().findViewById(R.id.ident_pibi).setVisibility(pibi ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.ident_pena).setVisibility(pibi ? View.GONE : View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        AutoCompleteTextView commune = (AutoCompleteTextView) view.findViewById(R.id.loca_commune);
        commune.setAdapter(new CommuneAdapter(getActivity()));

        loadSpinner(R.id.ident_pibi_type, RemocraProvider.getUriNature(RemocraProvider.TYPE_NATURE.PIBI));
        loadSpinner(R.id.ident_pibi_diam, RemocraProvider.CONTENT_DIAMETRE_URI);
        loadSpinner(R.id.ident_pena_nature, RemocraProvider.getUriNature(RemocraProvider.TYPE_NATURE.PENA));

        return view;
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H1, true);
        return values;
    }

}
