package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.database.HydrantTable;

public  class Hydrant0 extends AbstractHydrant {

    public Hydrant0() {
        super(R.layout.hydrant0, HydrantTable.COLUMN_STATE_H0);
    }

    private TextView localisation, caracteristique, disponibilite, complement;

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.resume);
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        return values;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
         localisation = (TextView) view.findViewById(R.id.localisation);
         caracteristique = (TextView) view.findViewById(R.id.caracteristique);
         disponibilite = (TextView) view.findViewById(R.id.disponibilite);
         complement = (TextView) view.findViewById(R.id.complement);
        return view;
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
       String adresse = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_ADRESSE));
       String voie = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_VOIE));
       String commune = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_COMMUNE));
       String complementAdresse = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_COMPLEMENT));
       String dr = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DEBIT_RENFORCE));
       String gd = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_GROS_DEBIT));
       String jumele = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_JUMELE));
       String capacite = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_CAPACITE));
       String dispo = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DISPO));
       String aspirations = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_ASPIRATIONS));

       localisation.setText(adresse);
       complement.setVisibility((complementAdresse != null) && !("".equals(complementAdresse)) ?  View.VISIBLE : View.GONE);
       complement.setText(Html.fromHtml("<br><u> Commentaire de localisation :</u> <br>" + complementAdresse));
       caracteristique.setText((("1".equals(dr) ? "Débit renforcé" + '\n' : ' ') + ("1".equals(gd) ? "Gros Débit" + '\n' : "")
               + (!("".equals(jumele)) ? "BI jumelée avec " + jumele  : "")));
       disponibilite.setBackgroundColor("DISPO".equals(dispo)? getResources().getColor(R.color.green) : getResources().getColor(R.color.error));
       disponibilite.setText(("DISPO".equals(dispo) ? "OUI" : "NON"));


    }

}

