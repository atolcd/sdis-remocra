package fr.sdis83.remocra.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.adapter.TourneeAdapter;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.TourneeTable;

/**
 * Activité qui affiche la liste des tournées.
 */
public class ListTournee extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface ListTourneeListener {
        void openTournee(Long idTournee, boolean anim);
    }

    // Identifies a particular Loader being used in this component
    private static final int URL_LOADER = 0;
    private static final String PARAM_FILTER = "ListTournee.PARAM_FILTER";
    private int itemMenuFilter = R.id.action_filter_all;

    private ListTourneeListener mListener = null;
    private TourneeAdapter adapter;

    public static ListTournee newInstance() {
        return new ListTournee();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tournees, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(itemMenuFilter).setChecked(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PARAM_FILTER, itemMenuFilter);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            itemMenuFilter = savedInstanceState.getInt(PARAM_FILTER);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_FILTER, this.itemMenuFilter);
        getLoaderManager().initLoader(URL_LOADER, bundle, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.action_filter_all:
            case R.id.action_filter_finish:
            case R.id.action_filter_in_progress:
            case R.id.action_filter_start:
                itemMenuFilter = item.getItemId();
                item.setChecked(!item.isChecked());
                bundle.putInt(PARAM_FILTER, item.getItemId());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        getLoaderManager().restartLoader(URL_LOADER, bundle, this);
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check that the container activity has implemented the callback interface
        try {
            mListener = (ListTourneeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ListTourneeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tournee_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TourneeAdapter(getActivity().getBaseContext());
        setListAdapter(adapter);
        ((TextView) view.findViewById(android.R.id.empty)).setText(getString(R.string.text_loading));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.openTournee(id, true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case URL_LOADER:
                String select = null;
                Integer paramFilter = bundle.getInt(PARAM_FILTER);
                switch (paramFilter) {
                    case R.id.action_filter_all:
                        break;
                    case R.id.action_filter_finish:
                        select = TourneeTable.FIELD_TOTAL_HYDRANT + ">=100";
                        break;
                    case R.id.action_filter_in_progress:
                        select = TourneeTable.FIELD_TOTAL_HYDRANT + "> 0 AND " + TourneeTable.FIELD_TOTAL_HYDRANT + "< 100 ";
                        break;
                    case R.id.action_filter_start:
                        select = TourneeTable.FIELD_TOTAL_HYDRANT + "<=0";
                        break;
                    default:
                        break;
                }

                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        RemocraProvider.CONTENT_TOURNEE_URI,        // Table to query
                        adapter.getProjection(),     // Projection to return
                        select,         // No selection clause
                        null,           // No selection arguments
                        null            // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((TextView) getView().findViewById(android.R.id.empty)).setText(getString(R.string.syncNbTourneeEmpty));
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);
    }
}
