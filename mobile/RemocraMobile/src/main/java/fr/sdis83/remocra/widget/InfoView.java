package fr.sdis83.remocra.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by cva on 18/11/13.
 */
public class InfoView extends TextView {

    public InfoView(Context context) {
        super(context);
        setListeners();
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setListeners();
    }

    public InfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setListeners();
    }

    protected void setListeners() {
        // Masquage
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setVisibility(View.GONE);
                return true;
            }
        });
    }

    public void setHtmlContent(CharSequence html) {
        setText(html);
        // Cas d'une webview (ne pas étendre TextView mais WebView) :
        // loadData(html, "text/html; charset=UTF-8", "UTF-8");
    }
}
