package com.codemonk_labs.bannerit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.codemonk_labs.bannerit.about.AboutAppDialog;
import com.codemonk_labs.bannerit.components.OnSwipeTouchListener;
import com.codemonk_labs.bannerit.fonts.FontChooserDialog;
import com.codemonk_labs.bannerit.fonts.FontSelectionListener;
import com.codemonk_labs.bannerit.fonts.Fonts;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.codemonk_labs.bannerit.Constants.AUTOSCROLL;
import static com.codemonk_labs.bannerit.Constants.BG_COLOR;
import static com.codemonk_labs.bannerit.Constants.DEFAULT_FONT_INDEX;
import static com.codemonk_labs.bannerit.Constants.FULLSCREEN;
import static com.codemonk_labs.bannerit.Constants.SCALE_FACTOR;
import static com.codemonk_labs.bannerit.Constants.TEXT_COLOR;
import static com.codemonk_labs.bannerit.Constants.TEXT_SIZE;
import static com.codemonk_labs.bannerit.Constants.TEXT_STYLE;

public class MainActivity extends AppCompatActivity implements FontSelectionListener {

    @BindView(R.id.main_layout) RelativeLayout mainLayout;
    @BindView(R.id.display_layout) FrameLayout displayLayout;
    @BindView(R.id.text_view_display) AppCompatTextView displayTextView;
    @BindView(R.id.layout_editor) CardView editorLayout;
    @BindView(R.id.edit_text_editor) EditText editorEditText;
    @BindView(R.id.options_layout) ConstraintLayout optionsLayout;
    @BindView(R.id.button_clear) AppCompatImageView clearButton;
    @BindView(R.id.button_full_screen) AppCompatImageView fullscreenButton;
    @BindView(R.id.button_bg_color) AppCompatImageView bgColorButton;
    @BindView(R.id.button_text_color) AppCompatImageView textColorButton;
    @BindView(R.id.button_mic) AppCompatImageView micButton;
    @BindView(R.id.button_text_style) AppCompatImageView fontChooserButton;
    @BindView(R.id.button_about) AppCompatImageView aboutButton;
    @BindView(R.id.auto_scroll) Switch autoScrollSwitch;


    private Unbinder unbinder;
    private ScaleGestureDetector SGD;
    private OnSwipeTouchListener swipeListener;
    private float scaleFactor = 1.0f;
    private float initialSize;
    private boolean fullscreenEnabled;
    private int textColor;
    private int bgColor;
    private int fontIndex = DEFAULT_FONT_INDEX;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    private Handler handler = new Handler();
    private FontChooserDialog fontChooserDialog;
    private AboutAppDialog aboutAppDialog;

    private Runnable fullScreen = () -> {
        if (fullscreenEnabled) {
            Utilities.hideSystemUI(this);
        }
    };

    private View.OnSystemUiVisibilityChangeListener windowChangeListener = (visibility) -> {
        // Note that system bars will only be "visible" if none of the
        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            // The system bars are visible.
            Log.d(TAG, "Visibility: " + (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN));
            if (fullscreenEnabled) {
                handler.postDelayed(fullScreen, Constants.BACK_TO_FULLSCREEN_DELAY);
            }
        }
    };

    private ColorPickerDialogListener onBGColorSelected = new ColorPickerDialogListener() {
        @Override
        public void onColorSelected(int dialogId, int color) {
            bgColor = color;
            displayLayout.setBackgroundColor(color);
        }

        @Override
        public void onDialogDismissed(int dialogId) {}
    };

    private ColorPickerDialogListener onTextColorSelected = new ColorPickerDialogListener() {
        @Override
        public void onColorSelected(int dialogId, int color) {
            textColor = color;
            displayTextView.setTextColor(color);
        }

        @Override
        public void onDialogDismissed(int dialogId) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        fontChooserDialog = new FontChooserDialog();
        textColor = getResources().getColor(R.color.defaultTextColor);
        bgColor = getResources().getColor(R.color.defaultBgColor);
        initialSize = displayTextView.getTextSize();
        displayTextView.setText(R.string.sample_text);
        displayTextView.setSelected(true);
        displayTextView.setTextColor(textColor);
        editorEditText.addTextChangedListener(new EditTextWatcher());
        SGD = new ScaleGestureDetector(this, new ScaleListener());
        swipeListener = new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                onSwipeUpAction();
            }

            public void onSwipeBottom() {
                onSwipeDownAction();
            }
        };
        autoScrollSwitch.setOnClickListener(this::handleAutoScroll);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(windowChangeListener);
        if (savedInstanceState != null) {
            restoreInstance(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideEditor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideEditor();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            restoreInstance(savedInstanceState);
        }
    }

    private void restoreInstance(Bundle savedInstanceState) {
        scaleFactor = savedInstanceState.getFloat(SCALE_FACTOR, 1);
        displayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, savedInstanceState.getFloat(TEXT_SIZE));
        bgColor = savedInstanceState.getInt(BG_COLOR, getResources().getColor(R.color.defaultBgColor));
        textColor = savedInstanceState.getInt(TEXT_COLOR, getResources().getColor(R.color.defaultTextColor));
        fontIndex = savedInstanceState.getInt(TEXT_STYLE);
        fullscreenEnabled = savedInstanceState.getBoolean(FULLSCREEN);
        boolean isAutoScrollEnabled = savedInstanceState.getBoolean(AUTOSCROLL);
        displayLayout.setBackgroundColor(bgColor);
        displayTextView.setTextColor(textColor);
        Log.d(TAG, "onRestore: color: "+ textColor);
        displayTextView.setTypeface(ResourcesCompat.getFont(this, Fonts.of(fontIndex)));
        if (fullscreenEnabled) {
            fullscreenButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_buttons_enabled));
            fullscreenButton.setImageResource(R.drawable.ic_full_screen_enabled);
            Utilities.hideSystemUI(this);
        } else {
            fullscreenButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_with_border));
            fullscreenButton.setImageResource(R.drawable.ic_full_screen);
            Utilities.showSystemUI(this);
        }
        autoScrollSwitch.setChecked(isAutoScrollEnabled);
        handleAutoScroll(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putFloat(SCALE_FACTOR, scaleFactor);
            outState.putFloat(TEXT_SIZE, displayTextView.getTextSize());
            outState.putInt(TEXT_COLOR, textColor);
            Log.d(TAG, "onSave: color: "+ textColor);
            outState.putInt(TEXT_STYLE, fontIndex);
            outState.putInt(BG_COLOR, bgColor);
            outState.putBoolean(FULLSCREEN, fullscreenEnabled);
            outState.putBoolean(AUTOSCROLL, autoScrollSwitch.isChecked());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeListener.onTouch(mainLayout, event);
        return SGD.onTouchEvent(event);
    }

    private void showEditor() {
        editorLayout.setVisibility(View.VISIBLE);
    }

    private void hideEditor() {
        editorEditText.clearFocus();
        editorLayout.setVisibility(View.INVISIBLE);
        Utilities.hideKeyboard(this);
    }

    private void handleAutoScroll(View view) {
        if (autoScrollSwitch.isChecked()) {
            enableAutoScroll();
        } else {
            disableAutoScroll();
        }
    }

    private void enableAutoScroll() {
        displayTextView.setSingleLine(true);
        displayTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        displayTextView.setMarqueeRepeatLimit(-1);
        displayTextView.setFreezesText(true);
        displayTextView.setFocusable(true);
        displayTextView.setFocusableInTouchMode(true);
        displayTextView.setSelected(true);
    }

    private void disableAutoScroll() {
        displayTextView.setSingleLine(false);
        displayTextView.setEllipsize(TextUtils.TruncateAt.END);
        displayTextView.setMarqueeRepeatLimit(0);
        displayTextView.setFreezesText(false);
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, scaleFactor);
            displayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialSize * scaleFactor);
            Log.d(TAG, "onScale() - scaleFactor: " + scaleFactor + " initialSize: " + initialSize);
            return true;
        }
    }

    private class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            displayTextView.setText(s.toString());
            clearButton.setVisibility(s.toString().isEmpty() ? View.INVISIBLE : View.VISIBLE);
            resizeTextView();
        }
    }

    private void resizeTextView() {
        if (displayTextView.getHeight() >= displayLayout.getHeight()) {
            displayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, displayTextView.getTextSize() - 30);
        }
    }

    private void onSwipeUpAction() {
        ObjectAnimator editorPanelAnimation = ObjectAnimator.ofFloat(editorLayout, "translationY", -50f);
        editorPanelAnimation.setDuration(100);
        editorPanelAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showEditor();
            }
        });
        if (editorLayout.getVisibility() == View.INVISIBLE) {
            editorPanelAnimation.start();
        } else {
            optionsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void onSwipeDownAction() {
        ObjectAnimator editorPanelAnimation = ObjectAnimator.ofFloat(editorLayout, "translationY", 100f);
        editorPanelAnimation.setDuration(100);
        editorPanelAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideEditor();
                optionsLayout.setVisibility(View.GONE);
            }
        });
        editorPanelAnimation.start();
    }

    private void showOrHideSystemUI () {
        if (fullscreenEnabled) {
            fullscreenButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_buttons_enabled));
            fullscreenButton.setImageResource(R.drawable.ic_full_screen_enabled);
            Utilities.hideSystemUI(this);
        } else {
            fullscreenButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_with_border));
            fullscreenButton.setImageResource(R.drawable.ic_full_screen);
            Utilities.showSystemUI(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                editorEditText.setText(result.get(0));
            }
        }
    }

    @Override
    public void onFontSelected(int selectedFontIndex) {
        fontIndex = selectedFontIndex;
        displayTextView.setTypeface(ResourcesCompat.getFont(this, Fonts.of(fontIndex)));
        if (fontChooserDialog != null) {
            fontChooserDialog.dismiss();
        }
    }

    @OnClick(R.id.button_mic)
    public void onMicButtonClicked(View view) {
        Utilities.promptSpeechInput(this, view);
    }

    @OnClick(R.id.button_text_style)
    public void onFontChooserButtonClicked(View view) {
        if (!fontChooserDialog.isVisible()) {
            fontChooserDialog.show(getSupportFragmentManager());
        }
    }

    @OnClick(R.id.button_text_color)
    public void textColorPicker() {
        Utilities.showColorPicker(getSupportFragmentManager(), onTextColorSelected);
    }

    @OnClick(R.id.button_bg_color)
    public void bgColorPicker() {
        Utilities.showColorPicker(getSupportFragmentManager(), onBGColorSelected);
    }

    @OnClick(R.id.button_clear)
    public void clearText() {
        editorEditText.setText("");
        displayTextView.setText("");
    }

    @OnClick(R.id.button_full_screen)
    public void onFullScreenClicked() {
        fullscreenEnabled = !fullscreenEnabled;
        showOrHideSystemUI();
    }

    @OnClick(R.id.button_about)
    public void onAboutButtonClicked() {
        if (aboutAppDialog == null) {
            aboutAppDialog = new AboutAppDialog();
        }
        if (!aboutAppDialog.isVisible()) {
            aboutAppDialog.show(getSupportFragmentManager());
        }
    }

}
