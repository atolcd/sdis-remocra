package fr.sdis83.remocra.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.sdis83.remocra.MainActivity;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.widget.InfoView;

/**
 * Created by jpt on 30/07/13.
 */
public class Accueil extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView mTournee;
    private TextView mHydrant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        return inflater.inflate(R.layout.accueil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Message d'information
        InfoView infoView = (InfoView)view.findViewById(R.id.textViewInfo);
        infoView.setHtmlContent(Html.fromHtml(getResources().getString(R.string.accueilInfo)));

        mTournee = ((TextView) view.findViewById(R.id.accueil_tournee));
        mTournee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).selectItemFromDrawer(MainActivity.LISTE_TOURNEE, false);
            }
        });
        mHydrant = ((TextView) view.findViewById(R.id.accueil_hydrant));
        mHydrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).selectItemFromDrawer(MainActivity.LISTE_PT_EAU, false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int urlLoader, Bundle bundle) {
        return new CursorLoader(getActivity(), RemocraProvider.CONTENT_SUMMARY_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            int nbTournee = cursor.getInt(0);
            if (nbTournee == 0) {
                mTournee.setText(getResources().getString(R.string.nombreTourneeEmpty));
            } else {
                mTournee.setText(getResources().getQuantityString(R.plurals.nombreTournee, nbTournee, nbTournee));
            }

            int nbHydrant = cursor.getInt(1);
            if (nbHydrant == 0) {
                mHydrant.setText(getResources().getString(R.string.nombreHydrantEmpty));
            } else {
                mHydrant.setText(getResources().getQuantityString(R.plurals.nombreHydrant, nbHydrant, nbHydrant));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
