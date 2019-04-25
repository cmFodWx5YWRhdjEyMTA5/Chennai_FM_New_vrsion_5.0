package com.chennaifmradiosongs.onlinemadrasradiostation.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;

/**
 * Created by chris on 17/03/15.
 * For Calligraphy.
 */
public class TextField extends TextView {

    public TextField(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }

}