package fr.sdis83.remocra.fragment.components;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import fr.sdis83.remocra.util.DbUtils;

/**
 * Pour activer le clic sur la dialog il faut setter le
 * FragmentManager de la frame principale
 *
 * Created by vde on 07/02/2017.
 */
public class EditDate extends EditText {

    private DatePickerFragment datePickerFragment;
    private FragmentManager fragmentManager;

    public EditDate(Context context) {
        super(context);
        init();
    }

    public EditDate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditDate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private  void init() {
        setFocusable(false);
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTextView(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentManager == null) throw new AssertionError("FramentManager undefined");

                datePickerFragment.show(fragmentManager, "datePicker");
            }
        });
    }

    public void setNow() throws ParseException {
        Calendar c = Calendar.getInstance();
        this.setText(String.valueOf(DbUtils.DATE_FORMAT_EDIT.format(c.getTime())));

    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
