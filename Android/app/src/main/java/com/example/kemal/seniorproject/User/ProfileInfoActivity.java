package com.example.kemal.seniorproject.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kemal.seniorproject.AccountFragment;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton close;


    private String USER_INFO_PROFILE;
    private String USER_EDIT_PROFILE;
    private ServerAdress serverAdress;

    private String myId, id;
    private SessionManager sessionManager;

    private TextView profile_name, profile_surname, profile_birth, profile_phone, profile_email, profile_website, profile_education, profile_unvan;

    private AccountFragment accountFragment;

    private String TAG = "ProfileInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);


        accountFragment = new AccountFragment();

        profile_name = findViewById(R.id.profile_name);
        profile_surname = findViewById(R.id.profile_surname);
        profile_birth = findViewById(R.id.profile_birth);
        profile_phone = findViewById(R.id.profile_phone);
        profile_email = findViewById(R.id.profile_email);
        profile_website = findViewById(R.id.profile_website);
        profile_education = findViewById(R.id.profile_education);
        profile_unvan = findViewById(R.id.profile_unvan);


        profile_name.setOnClickListener(this);
        profile_surname.setOnClickListener(this);
        profile_birth.setOnClickListener(this);
        profile_phone.setOnClickListener(this);
        profile_email.setOnClickListener(this);
        profile_website.setOnClickListener(this);
        profile_education.setOnClickListener(this);
        profile_unvan.setOnClickListener(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        close = findViewById(R.id.profile_info_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
                finish();

            }
        });

        sessionManager = new SessionManager(getApplicationContext());


        ServerAdress.init(ProfileInfoActivity.this);
        USER_INFO_PROFILE = ServerAdress.host + "user_info.php";
        USER_EDIT_PROFILE = ServerAdress.host + "edit_user_profile.php";

        SessionManager.init(ProfileInfoActivity.this);
        myId = SessionManager.myId;
        id = getIntent().getStringExtra("id");

        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("type", "typetwo");

        MyVolley volley = new MyVolley();

        volley.volley(ProfileInfoActivity.this, USER_INFO_PROFILE, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });


        control();

    }

    private void control() {

        if (!myId.equals(id)) {
            profile_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_surname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_birth.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_website.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_education.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            profile_unvan.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

    }

    private void loadData(JSONObject jsonObject) {

        try {

            Boolean success = jsonObject.getBoolean("success");
            if (success) {
                if (jsonObject.getString("name").equals("null"))
                    profile_name.setText("");
                else
                    profile_name.setText(jsonObject.getString("name").trim());
                if (jsonObject.getString("surname").equals("null"))
                    profile_surname.setText("");
                else
                    profile_surname.setText(jsonObject.getString("surname").trim());
                if (jsonObject.getString("birthday").equals("null"))
                    profile_birth.setText("");
                else
                    profile_birth.setText(jsonObject.getString("birthday").trim());
                if (jsonObject.getString("email_adress").equals("null"))
                    profile_email.setText("");
                else
                    profile_email.setText(jsonObject.getString("email_adress").trim());
                if (jsonObject.getString("phone").equals("null"))
                    profile_phone.setText("");
                else
                    profile_phone.setText(jsonObject.getString("phone").trim());
                if (jsonObject.getString("website_adress").equals("null"))
                    profile_website.setText("");
                else
                    profile_website.setText(jsonObject.getString("website_adress").trim());
                if (jsonObject.getString("education").equals("null"))
                    profile_education.setText("");
                else
                    profile_education.setText(jsonObject.getString("education").trim());
                if (jsonObject.getString("unvan").equals("null"))
                    profile_unvan.setText("");
                else
                    profile_unvan.setText(jsonObject.getString("unvan").trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ProfileInfoActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void openDialog(final int max, final String hint, final TextView profile_textview, final String column) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileInfoActivity.this);
        builder.setTitle("");
        builder.setMessage("");
        builder.setCancelable(false);

        View open_dialog = View.inflate(ProfileInfoActivity.this, R.layout.open_dialog, null);
        final EditText mEdit_Text = open_dialog.findViewById(R.id.edittext_dialog);
        final TextView mTextView = open_dialog.findViewById(R.id.textview_dialog);

        mEdit_Text.setText(profile_textview.getText().toString());
        mTextView.setText(profile_textview.length() + "/" + max);

        mEdit_Text.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(max)
        });
        mEdit_Text.setSelection(mEdit_Text.length());
        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTextView.setText(String.valueOf(charSequence.length()) + "/" + String.valueOf(max));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mEdit_Text.setTextSize(14);
        mEdit_Text.setHint(hint);
        mEdit_Text.addTextChangedListener(mTextEditorWatcher);
        builder.setView(open_dialog);


        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = mEdit_Text.getText().toString();
                profile_textview.setText(value);

                edit(column, value);


            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }

    private void edit(final String column, final String value) {
        Map<String, String> params = new HashMap<>();
        params.put("id", myId);
        params.put("column", column);
        params.put("value", value);

        MyVolley volley = new MyVolley();

        volley.volley(ProfileInfoActivity.this, USER_EDIT_PROFILE, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean control = jsonObject.getBoolean("control");
                    if (control) {
                        Toast.makeText(ProfileInfoActivity.this, "Güncelleme başarıyla gerçekleşti.", Toast.LENGTH_SHORT).show();
                        if (column.equals("name") || column.equals("surname")) {
                            HashMap<String, String> user = sessionManager.getUserDetail();
                            sessionManager.editNameSurname(profile_name.getText().toString(), profile_surname.getText().toString());
                        }
                    } else
                        Toast.makeText(ProfileInfoActivity.this, "Güncelleme sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {

        if (myId.equals(id)) {

            switch (view.getId()) {
                case R.id.profile_name:
                    openDialog(30, "Adınızı girin", profile_name, "name");
                    break;
                case R.id.profile_surname:
                    openDialog(30, "Soyadınızı girin", profile_surname, "surname");
                    break;
                case R.id.profile_phone:
                    openDialog(30, "Telefon numarınızı girin", profile_phone, "phone");
                    break;
                case R.id.profile_email:
                    openDialog(50, "Email adresinizi girin", profile_email, "email_adress");
                    break;
                case R.id.profile_website:
                    openDialog(100, "Website adresinizi girin", profile_website, "website_adress");
                    break;
                case R.id.profile_education:
                    openDialog(100, "Eğitim bilgilerinizi girin", profile_education, "education");
                    break;
                case R.id.profile_unvan:
                    openDialog(100, "Unvanızı girin", profile_unvan, "unvan");
                    break;

            }
        }
    }
}
