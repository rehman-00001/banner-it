package com.codemonk_labs.bannerit.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codemonk_labs.bannerit.R;
import com.codemonk_labs.bannerit.model.Feedback;
import com.codemonk_labs.bannerit.network.NetworkHandler;
import com.codemonk_labs.bannerit.network.PostRequest;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.codemonk_labs.bannerit.Constants.API_FEEDBACK;
import static com.codemonk_labs.bannerit.Constants.SHARED_PREF;

import com.crashlytics.android.Crashlytics;

public class FeedbackActivity extends AppCompatActivity {

    public static final String TAG = FeedbackActivity.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.toolbar_title)
    AppCompatTextView toolbarTitle;

    @BindView(R.id.edit_text_name)
    TextInputEditText userNameET;

    @BindView(R.id.edit_text_email)
    TextInputEditText emailET;

    @BindView(R.id.edit_text_feedback)
    TextInputEditText feedbackET;

    @BindView(R.id.progressBar)
    FrameLayout progressBar;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        unbinder = ButterKnife.bind(this);
        preferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        toolbarTitle.setText(getString(R.string.submit_feedback));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.feedback_cancel, R.id.back_button})
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.feedback_submit)
    public void onSubmitFeedback() {
        if (!validateInputs()) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Feedback feedback = new Feedback();
        feedback.userName = String.valueOf(userNameET.getText());
        feedback.content = String.valueOf(feedbackET.getText());
        if (emailET != null && emailET.getText() != null) {
            feedback.email = emailET.getText().toString();
        }

        StringRequest stringRequest = new PostRequest(API_FEEDBACK,
                this::onFeedbackSubmitted,
                this::onFeedbackFailed,
                NetworkHandler.toJson(feedback, Feedback.class));
        NetworkHandler.instance(this).addToRequestQueue(stringRequest);
    }

    private boolean validateInputs() {
        if (userNameET == null || userNameET.getText() == null) {
            Crashlytics.log(Log.ERROR, TAG,
                    "userNameET or userNameET.getText() returned null");
            return false;
        }

        if (feedbackET == null || feedbackET.getText() == null) {
            Crashlytics.log(Log.ERROR, TAG,
                    "feedbackET or feedbackET.getText() returned null");
            return false;
        }

        if (userNameET.getText().toString().trim().isEmpty()) {
            userNameET.setError(getString(R.string.required_username));
        } else {
            userNameET.setError(null);
        }

        if (feedbackET.getText().toString().isEmpty() ||
                feedbackET.getText().toString().trim().length() < 10) {
            feedbackET.setError(getString(R.string.required_content));
            return false;
        }
        return true;
    }

    private void onFeedbackSubmitted(String response) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this,
                getString(R.string.feedback_received),
                Toast.LENGTH_LONG)
                .show();
        onBackPressed();
    }

    private void onFeedbackFailed(VolleyError error) {
        progressBar.setVisibility(View.INVISIBLE);
        String message = getString(R.string.default_network_error);
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            message = getString(R.string.connection_error);
        } else if (error instanceof AuthFailureError) {
            ///Error indicating that there was an Authentication Failure while performing the request
            message = getString(R.string.server_auth_failed);
        }
        Crashlytics.log(Log.ERROR, TAG, error.getMessage());
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}