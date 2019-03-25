package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.HydrantActivity;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.adapter.CommuneAdapter;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;

public class Hydrant1 extends AbstractHydrant {

    public Hydrant1() {
        super(R.layout.hydrant1, HydrantTable.COLUMN_STATE_H1);
        addBindableData(R.id.agent1, HydrantTable.COLUMN_AGENT1, EditText.class);
        addBindableData(R.id.agent2, HydrantTable.COLUMN_AGENT2, EditText.class);
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
        //on modifie l'action de multiline à suivant
        EditText loca = (EditText) view.findViewById(R.id.loca_compl);
        loca.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        loca.setRawInputType(InputType.TYPE_CLASS_TEXT);
        return view;
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        String valeurDeLaCommune = values.getAsString(HydrantTable.COLUMN_COMMUNE);
        boolean state = valeurDeLaCommune != null
                && !"".equals(valeurDeLaCommune)
                && ((HydrantActivity)getActivity()).isLibelleCommuneValid(valeurDeLaCommune);
        // Onglet valide si la commune est valide
        // (empèche l'utilisateur de synchroniser Hydrant avec une commune non valide)
        values.put(HydrantTable.COLUMN_STATE_H1, state);
        return values;
    }

}
