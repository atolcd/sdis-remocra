package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.MarqueTable;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.fragment.components.DatePickerFragment;
import fr.sdis83.remocra.fragment.components.EditDate;
import fr.sdis83.remocra.util.DbUtils;

public class Hydrant3 extends AbstractHydrant {

    public Hydrant3() {
        super(R.layout.hydrant3, HydrantTable.COLUMN_STATE_H3);

        addBindableData(R.id.mco_marque, HydrantTable.COLUMN_MARQUE, Spinner.class);
        addBindableData(R.id.mco_modele, HydrantTable.COLUMN_MODELE, Spinner.class);
        addBindableData(R.id.mco_annee, HydrantTable.COLUMN_ANNEE_FAB, EditText.class);
        addBindableData(R.id.mco_choc, HydrantTable.COLUMN_CHOC, CheckBox.class);
        addBindableData(R.id.gest_domaine, HydrantTable.COLUMN_DOMAINE, Spinner.class);
        addBindableData(R.id.gest_pt_eau, HydrantTable.COLUMN_GEST_PTEAU, EditText.class);
        addBindableData(R.id.gest_reseau, HydrantTable.COLUMN_GEST_RESEAU, EditText.class);
        addBindableData(R.id.divers_courrier, HydrantTable.COLUMN_COURRIER, EditText.class);
        addBindableData(R.id.date_attestation, HydrantTable.COLUMN_DATE_ATTESTATION, EditDate.class);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.mco);
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
        boolean pibi = isPibi(cursor);
        String codeNature = NatureTable.getCodeById(getActivity(), cursor.getInt(cursor.getColumnIndex(HydrantTable.COLUMN_NATURE)));
        getView().findViewById(R.id.mco_layout_annee).setVisibility(codeNature!=null && codeNature.equals("RI") ? View.GONE : View.VISIBLE);
        getView().findViewById(R.id.title_mco).setVisibility(codeNature!=null && codeNature.equals("RI") ? View.GONE : View.VISIBLE);
        getView().findViewById(R.id.mco_marque_modele).setVisibility(pibi ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.mco_choc).setVisibility(pibi ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.gest_layout_reseau).setVisibility(pibi ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        loadSpinner(R.id.gest_domaine, RemocraProvider.CONTENT_DOMAINE_URI);

        loadSpinner(R.id.mco_marque, RemocraProvider.CONTENT_MARQUE_URI);
        loadSpinner(R.id.mco_modele, RemocraProvider.getUriModeleByMarque("-"));

        Spinner spinnerMarque = (Spinner) view.findViewById(R.id.mco_marque);
        spinnerMarque.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor item = (Cursor) adapterView.getItemAtPosition(position);
                loadSpinner(R.id.mco_modele, RemocraProvider.getUriModeleByMarque(item.getString(item.getColumnIndex(MarqueTable.COLUMN_CODE))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        // Initialisation du fragment manager obligatoire pour l'affichage de la dialog de date.
        ((EditDate) view.findViewById(R.id.date_attestation)).setFragmentManager(getFragmentManager());

        return view;
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H3, true);
        return values;
    }
}
