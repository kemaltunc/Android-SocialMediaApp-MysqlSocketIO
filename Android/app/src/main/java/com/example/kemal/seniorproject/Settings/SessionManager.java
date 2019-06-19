package com.example.kemal.seniorproject.Settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.kemal.seniorproject.LoginActivity;
import com.example.kemal.seniorproject.MainActivity;


import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;

    public static String myId, email, name, surname, userImage, companyId, companyname, companyImage;

    public static SessionManager sessionManager = null;

    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;


    private static final String LOGIN = "IS_LOGIN";
    public static final String ID = "ID";
    public static final String EMAIL = "EMAIL";
    public static final String NAME = "NAME";
    public static final String SURNAME = "SURNAME";
    public static final String USER_IMAGE_URL = "USER_IMAGE_URL";
    public static final String COMPANYID = "COMPANYID";
    public static final String COMPANYNAME = "COMPANYNAME";
    public static final String COMPANY_IMAGE_URL = "COMPANY_IMAGE_URL";

    public static final String FILTERS = "FILTERS";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN", PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String email, String name, String surname, String userImage, String companyId, String companyName, String companyImage) {
        editor.putBoolean(LOGIN, true);
        editor.putString(ID, id);
        editor.putString(EMAIL, email);
        editor.putString(NAME, name);
        editor.putString(SURNAME, surname);
        editor.putString(USER_IMAGE_URL, userImage);
        editor.putString(COMPANYID, companyId);
        editor.putString(COMPANYNAME, companyName);
        editor.putString(COMPANY_IMAGE_URL, companyImage);
        editor.apply();
    }

    public void addFilter(String sectors) {
        editor.putString(FILTERS, sectors);
        editor.apply();
    }

    public String filters(Context context) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();

        return user.get(SessionManager.FILTERS);
    }

    public void editNameSurname(String name, String surname) {
        editor.putString(NAME, name);
        editor.putString(SURNAME, surname);
        editor.apply();
    }

    public void addCompany(String companyId, String companyName, String image_url) {
        editor.putString(COMPANYID, companyId);
        editor.putString(COMPANYNAME, companyName);
        editor.putString(COMPANY_IMAGE_URL, image_url);
        editor.apply();
    }


    public static void init(Context context) {

        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        myId = user.get(sessionManager.ID);
        email = user.get(sessionManager.EMAIL);
        name = user.get(sessionManager.NAME);
        surname = user.get(sessionManager.SURNAME);
        userImage = user.get(sessionManager.USER_IMAGE_URL);
        companyId = user.get(sessionManager.COMPANYID);
        companyname = user.get(sessionManager.COMPANYNAME);
        companyImage = user.get(sessionManager.COMPANY_IMAGE_URL);


    }

    public Boolean checkLogin() {
        if (sharedPreferences.getBoolean(LOGIN, false)) {
            return true;
        }
        return false;
    }


    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(SURNAME, sharedPreferences.getString(SURNAME, null));
        user.put(USER_IMAGE_URL, sharedPreferences.getString(USER_IMAGE_URL, null));
        user.put(COMPANYID, sharedPreferences.getString(COMPANYID, null));
        user.put(COMPANYNAME, sharedPreferences.getString(COMPANYNAME, null));
        user.put(COMPANY_IMAGE_URL, sharedPreferences.getString(COMPANY_IMAGE_URL, null));
        user.put(FILTERS, sharedPreferences.getString(FILTERS, null));

        return user;
    }

    public void logout() {
        myId = "";
        email = "";
        name = "";
        surname = "";
        userImage = "";
        companyId = "";
        companyname = "";
        companyImage = "";
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }


}
