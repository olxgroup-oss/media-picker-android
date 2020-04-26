package com.mediapicker.gallery.utils;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mediapicker.gallery.R;

public class SnackbarUtils {

    public static void show(View view, int text, int length) {
        Snackbar snackbar = make(view, text, length);
        if (snackbar != null) snackbar.show();
    }

    public static void show(View view, CharSequence text, int length) {
        Snackbar snackbar = make(view, text, length);
        if (snackbar != null) snackbar.show();
    }

    private static Snackbar make(View view, int text, int length) {
        if (view != null) {
            return make(view, view.getContext().getResources().getText(text), length);
        }
        return null;
    }

    public static Snackbar make(View view, CharSequence text, int length) {
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, text, length);

            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(view.getContext().getResources().getColor(R.color.snackbar_text));

            return snackbar;
        }
        return null;
    }
}
