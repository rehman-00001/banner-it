package com.codemonk_labs.bannerit.about;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.codemonk_labs.bannerit.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.crashlytics.android.Crashlytics;

public class AboutAppDialog extends DialogFragment {

    private static final String TAG = AboutAppDialog.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_about, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        ((AppCompatTextView)contentView.findViewById(R.id.toolbar_title)).setText(getString(R.string.about));
        return contentView;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        if (getActivity() == null) {
            throw new NullPointerException("Main activity is null!");
        }

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void show(FragmentManager fragmentManager) {
        this.show(fragmentManager, TAG);
    }


    @OnClick(R.id.rate_the_app)
    void rateAppOnPlayStore() {
        Log.d(TAG, "Rate App on Play store");
        if (getContext() == null) {
            Crashlytics.log(Log.ERROR, TAG, "getContext() returned null");
            return;
        }

        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getContext().getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            Crashlytics.log(Log.INFO, TAG, e.getMessage());
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }
    }

    @OnClick(R.id.back_button)
    void onBackButtonPressed() {
        dismiss();
    }

    @OnClick(R.id.share_app)
    void shareApp() {
        String shareAppText = getString(R.string.share_this_app_text);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareAppText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @OnClick(R.id.submit_feedback)
    void submitFeedback() {
        Intent feedbackIntent = new Intent(getContext(), FeedbackActivity.class);
        startActivity(feedbackIntent);
    }
}
