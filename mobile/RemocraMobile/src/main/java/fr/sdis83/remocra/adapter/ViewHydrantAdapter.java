package fr.sdis83.remocra.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.HydrantTable.TYPE_SAISIE;
import fr.sdis83.remocra.fragment.AbstractHydrant;
import fr.sdis83.remocra.fragment.Hydrant0;
import fr.sdis83.remocra.fragment.Hydrant1;
import fr.sdis83.remocra.fragment.Hydrant2;
import fr.sdis83.remocra.fragment.Hydrant3;
import fr.sdis83.remocra.fragment.Hydrant4;
/**
 * Created by jpt on 08/08/13.
 */
public class ViewHydrantAdapter extends FragmentPagerAdapter {

    private final Context context;
    private Cursor hydrant;

    private SparseArray<Object> mCurFragment = new SparseArray<Object>();
    public List<String> fragmentClassNames = new LinkedList<String>();
    private HydrantTable.TYPE_SAISIE typeSaisie = HydrantTable.TYPE_SAISIE.LECT;

    public ViewHydrantAdapter(FragmentManager supportFragmentManager, Context context) {
        super(supportFragmentManager);
        this.context = context;
        fragmentClassNames.add(Hydrant0.class.getName());
        fragmentClassNames.add(Hydrant1.class.getName());
        fragmentClassNames.add(Hydrant2.class.getName());
        fragmentClassNames.add(Hydrant3.class.getName());
        fragmentClassNames.add(Hydrant4.class.getName());
    }

    public void setHydrant(Cursor hydrant, int currentPosition) {
        this.hydrant = hydrant;
        findTypeSaisieForCursor();
        for (int i = 0; i < getCount(); ++i) {
            Object obj = mCurFragment.get(i);
            if (obj != null) {
                ((AbstractHydrant) obj).setCursor(typeSaisie, hydrant);
                if (i == currentPosition) {
                    ((AbstractHydrant) obj).loadDataFromCursor();
                }
            }
        }
    }

    private void findTypeSaisieForCursor() {
        GlobalRemocra global = GlobalRemocra.getInstance(context);
        String actualTypeSaisie = hydrant.getString(hydrant.getColumnIndex(HydrantTable.COLUMN_TYPE_SAISIE));
        Integer nbVisite = hydrant.getInt(hydrant.getColumnIndex(HydrantTable.COLUMN_NB_VISITE));
        if (TextUtils.isEmpty(actualTypeSaisie)) {
            typeSaisie = TYPE_SAISIE.LECT;
            if (hydrant.getLong(hydrant.getColumnIndex(HydrantTable.COLUMN_DATE_RECEP)) == 0) {
                if (global.getCanVisitRecption()) {
                    typeSaisie = TYPE_SAISIE.CREA;
                }
            } else if (nbVisite == 1) {
                if (global.getCanReception()) {
                    typeSaisie = TYPE_SAISIE.RECEP;
                }

            } else {
                if (global.getCanReconnaissance()) {
                    typeSaisie = TYPE_SAISIE.RECO;
                }
                if (global.getCanControl()) {
                    typeSaisie = TYPE_SAISIE.CTRL;
                }
            }
        } else {
            typeSaisie = TYPE_SAISIE.valueOf(actualTypeSaisie);
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment f = this.getItem(position);
        if (f instanceof AbstractHydrant) {
            AbstractHydrant ah = (AbstractHydrant)f;
            return ah.getTabTitle();
        }
        return "-";
    }

    @Override
    public int getCount() {
        return fragmentClassNames.size();
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        AbstractHydrant hydrantFragment = (AbstractHydrant)Fragment.instantiate(context, fragmentClassNames.get(position), args);
        hydrantFragment.setCursor(typeSaisie, this.hydrant);
        return hydrantFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mCurFragment.delete(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        mCurFragment.put(position, object);
        return object;
    }

    public Fragment getCurrentFragment(int pos) {
        return (Fragment) mCurFragment.get(pos);
    }

    public HydrantTable.TYPE_SAISIE getTypeSaisie() {
        return typeSaisie;
    }

    public void setTypeSaisie(TYPE_SAISIE typeSaisie) {
        this.typeSaisie = typeSaisie;
    }
}
