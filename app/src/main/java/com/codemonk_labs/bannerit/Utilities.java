package com.codemonk_labs.bannerit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.Locale;

import static com.codemonk_labs.bannerit.Constants.COLOR_PICKER;
import static com.codemonk_labs.bannerit.MainActivity.REQ_CODE_SPEECH_INPUT;

class Utilities {

    static void hideSystemUI(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static void showColorPicker(FragmentManager fragmentManager, ColorPickerDialogListener listener) {
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder().create();
        dialog.setColorPickerDialogListener(listener);
        dialog.show(fragmentManager, COLOR_PICKER);
    }

    static void promptSpeechInput(Activity activity, View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                activity.getString(R.string.voice_to_text_hint));
        try {
            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity.getApplicationContext(),
                    R.string.voice_to_text_not_supported,
                    Toast.LENGTH_SHORT).show();
        }
    }

//    static void showFontPickerDialog(final Context context,
//                                     int selectedIndex, final FontSelectionListener listener) {
//        final FontsListAdapter arrayAdapter = new FontsListAdapter(context);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(context.getString(R.string.font_chooser_dialog_title));
//        builder.setNegativeButton(context.getString(android.R.string.cancel),
//                (dialog, which) -> dialog.dismiss());
//        builder.setSingleChoiceItems(arrayAdapter, selectedIndex,
//                (dialog, which) -> {
//                    listener.onFontSelected(which);
//                    dialog.dismiss();
//                });
//        builder.show();
//    }

}
