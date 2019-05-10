package com.codemonk_labs.bannerit.fonts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codemonk_labs.bannerit.R;

public class FontsListAdapter extends RecyclerView.Adapter<FontsListAdapter.FontViewHolder> {

    private FontSelectionListener listener;

    FontsListAdapter(FontSelectionListener listener) {
        super();
        this.listener = listener;
    }

    @NonNull
    @Override
    public FontsListAdapter.FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new FontViewHolder(parent.getContext(), view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FontsListAdapter.FontViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return Fonts.list.length;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_item_font;
    }

    static class FontViewHolder extends RecyclerView.ViewHolder {
        Context context;
        AppCompatTextView checkedTextView;
        FontSelectionListener listener;

        FontViewHolder(Context context, View view, FontSelectionListener listener) {
            super(view);
            this.context = context;
            this.listener = listener;
            checkedTextView = view.findViewById(R.id.list_item_font);
        }

        void bindData(int index) {
            Fonts.FontDetail font = Fonts.list[index];
            checkedTextView.setText(font.name);
            checkedTextView.setTypeface(ResourcesCompat.getFont(context, font.resourceId));
            checkedTextView.setOnClickListener((v) -> listener.onFontSelected(index));
        }
    }
}
