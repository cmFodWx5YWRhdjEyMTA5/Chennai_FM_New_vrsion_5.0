package com.chennaifmradiosongs.onlinemadrasradiostation;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

class Typefile {

    static void overrideFont(Context context) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Ubuntu-R.ttf");
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("DEFAULT");
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
