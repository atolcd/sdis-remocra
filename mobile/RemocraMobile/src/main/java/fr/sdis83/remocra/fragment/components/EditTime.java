package fr.sdis83.remocra.fragment.components;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import fr.sdis83.remocra.util.DbUtils;

/**
 * Pour activer le clic sur la dialog il faut setter le
 * FragmentManager de la frame principale
 *
 * Created by yaz on 20/09/2019.
 */
public class EditTime extends EditText {

    private TimePickerFragment timePickerFragment;
    private FragmentManager fragmentManager;

    public EditTime(Context context) {
        super(context);
        init();
    }

    public EditTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTime(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private  void init() {
        setFocusable(false);
        timePickerFragment = new TimePickerFragment();
        timePickerFragment.setTextView(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentManager == null) throw new AssertionError("FramentManager undefined");

                timePickerFragment.show(fragmentManager, "timePicker");
            }
        });
    }

    public void setNow() throws ParseException {
        Calendar c = Calendar.getInstance();

        this.setText(String.valueOf(DbUtils.TIME_FORMAT_EDIT.format(c.getTime())));

    }

    public void setTime(Long t) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        this.setText(DbUtils.TIME_FORMAT_EDIT.format(cal.getTime()));

    }


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
