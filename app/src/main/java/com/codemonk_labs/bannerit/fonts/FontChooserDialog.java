package com.codemonk_labs.bannerit.fonts;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemonk_labs.bannerit.R;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FontChooserDialog extends DialogFragment {

    private static final String TAG = FontChooserDialog.class.getSimpleName();

    private Unbinder unbinder;
    private FontSelectionListener listener;
    private Random random;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FontSelectionListener) context;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_font_chooser, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view_fonts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new FontsListAdapter(listener));
        ((AppCompatTextView) contentView.findViewById(R.id.toolbar_title))
                .setText(getString(R.string.font_chooser_dialog_title));
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

    @OnClick({R.id.dialog_cancel, R.id.back_button})
    void dismissDialog() {
        dismiss();
    }

    @OnClick(R.id.dialog_random)
    void selectRandomFont() {
        if (random == null) {
            random = new Random();
        }
        if (listener != null) {
            listener.onFontSelected(random.nextInt(Fonts.list.length));
        }
        dismiss();
    }

    public void show(FragmentManager fragmentManager) {
        this.show(fragmentManager.beginTransaction(), TAG);
    }

}
