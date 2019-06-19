package com.example.kemal.seniorproject.Settings;

import android.app.Activity;

import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.RegisterActivity;

public class ServerAdress {

    public static String root;
    public static String host;
    public static String profile_images;
    public static String post_images;
    public static String company_images;
    public static String message_images;
    public static String image_root;


    public static void init(Activity activity) {

        host = "https://kemaltuunc.000webhostapp.com/";

        image_root = host + "uploads/images/";

        profile_images = image_root + "profile_images/";
        post_images = image_root + "post_images/";
        company_images = image_root + "company_images/";
        message_images = image_root + "message_images/";

    }

}
