package com.example.kemal.seniorproject.Settings;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MyGlide {
    public static void image(Context context, String Url, ImageView view) {

        try {
            Glide.with(context).load(Url).into(view);
        } catch (Exception ex) {
        }
    }
}
