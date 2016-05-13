package fr.sdis83.remocra.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.database.AnomalieTable;

/**
 * Created by jpt on 12/09/13.
 */
public class AnomalieAdapter extends CursorAdapter {

    /**
     * State of ListView item that has never been determined.
     */
    private static final int STATE_UNKNOWN = 0;

    /**
     * State of a ListView item that is sectioned. A sectioned item must
     * display the separator.
     */
    private static final int STATE_SECTIONED_CELL = 1;

    /**
     * State of a ListView item that is not sectioned and therefore does not
     * display the separator.
     */
    private static final int STATE_REGULAR_CELL = 2;

    private final int colLibelle;
    private final int colCritere;
    private int[] mCellStates;
    private ListView listView;

    private static class AnomalieViewHolder {
        public TextView separator;
        public CheckedTextView anomalieView;
        public String critere;
    }

    public AnomalieAdapter(Context context) {
        this(context, null);
    }

    public AnomalieAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
        colLibelle = cursor != null ? cursor.getColumnIndex(AnomalieTable.COLUMN_LIBELLE) : -1;
        colCritere = cursor != null ? cursor.getColumnIndex(AnomalieTable.COLUMN_CRITERE) : -1;
        initFromCursor(cursor);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        initFromCursor(cursor);
        super.changeCursor(cursor);
    }

    private void initFromCursor(Cursor cursor) {
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
    }

    public void setListView(ListView lstAnomalie) {
        this.listView = lstAnomalie;
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CheckedTextView checkedtext = (CheckedTextView) view.findViewById(android.R.id.text1);
                checkedtext.toggle();
                listView.setItemChecked(position, checkedtext.isChecked());
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.anomalie_list_item, viewGroup, false);
        // The following code allows us to keep a reference on the child views of the item. It prevents us from calling findViewById at each getView/bindView and boosts the rendering code.
        AnomalieViewHolder holder = new AnomalieViewHolder();
        holder.separator = (TextView) v.findViewById(R.id.separator);
        holder.anomalieView = (CheckedTextView) v.findViewById(android.R.id.text1);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final AnomalieViewHolder holder = (AnomalieViewHolder) view.getTag();

        boolean needSeparator = false;

        final int position = cursor.getPosition();
        holder.critere = cursor.getString(colCritere);

        if (position > (mCellStates.length - 1)) {
            return;
        }

        switch (mCellStates[position]) {
            case STATE_SECTIONED_CELL:
                needSeparator = true;
                break;

            case STATE_REGULAR_CELL:
                needSeparator = false;
                break;

            case STATE_UNKNOWN:
            default:
                // A separator is needed if it's the first itemview of the ListView or if the group of the current cell is different from the previous itemview.
                if (position == 0) {
                    needSeparator = true;
                } else {
                    cursor.moveToPosition(position - 1);
                    String mLastCritere = cursor.getString(colCritere);
                    if (mLastCritere != null && !mLastCritere.equals(holder.critere)) {
                        needSeparator = true;
                    }
                    cursor.moveToPosition(position);
                }
                // Cache the result
                mCellStates[position] = needSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if (needSeparator) {
            holder.separator.setText(holder.critere);
            holder.separator.setVisibility(View.VISIBLE);
            holder.separator.setOnClickListener(null);
            holder.separator.setOnLongClickListener(null);
        } else {
            holder.separator.setVisibility(View.GONE);
        }
        holder.anomalieView.setText(cursor.getString(colLibelle));
        holder.anomalieView.setChecked(this.listView.isItemChecked(position));
    }
}
