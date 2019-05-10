package com.codemonk_labs.bannerit.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codemonk_labs.bannerit.R;


/**
 * Created by ABDUL on 11/3/2017.
 */

public class DetailView extends RelativeLayout {

    private final AttributeSet attributes;
    private final Context context;

    private TextView labelText1;
    private TextView labelText2;
    private ImageView dvIcon;

    public DetailView(Context context) {
        this(context, null);
    }

    public DetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attributes = attrs;
        init();
    }

    private void init() {
        Integer resourceId = 0;
        String text1 = null, text2 = null;

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.DetailView);
        int count = typedArray.getIndexCount();
        try {
            for (int i = 0; i < count; ++i) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.DetailView_labelText1) {
                    text1 = typedArray.getString(attr);
                } else if (attr == R.styleable.DetailView_labelText2) {
                    text2 = typedArray.getString(attr);
                } else if (attr == R.styleable.DetailView_dvIcon) {
                    resourceId = typedArray.getResourceId(attr, 0);
                }
            }
        } finally {
            typedArray.recycle();
        }

        if (text2 == null) {
            LayoutInflater.from(getContext())
                    .inflate(R.layout.detail_view_content_single_line, this);
            dvIcon = findViewById(R.id.img_dv_icon);
            dvIcon.setImageResource(resourceId);
            labelText1 = findViewById(R.id.tv_dv_label_1);
            labelText1.setText(text1);
            return;
        }

        LayoutInflater.from(getContext()).inflate(R.layout.detail_view_content, this);
        dvIcon = findViewById(R.id.img_dv_icon);
        dvIcon.setImageResource(resourceId);
        labelText1 = findViewById(R.id.tv_dv_label_1);
        labelText1.setText(text1);
        labelText2 = findViewById(R.id.tv_dv_label_2);
        labelText2.setText(text2);
    }

    public void setLabelText1(String text) {
        this.labelText1.setText(text);
    }

    public void setLabelText2(String text) {
        this.labelText2.setText(text);
    }

    public void setDvIcon(int resourceId) {
        this.dvIcon.setImageResource(resourceId);
    }
}
