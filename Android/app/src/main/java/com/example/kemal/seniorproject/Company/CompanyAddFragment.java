package com.example.kemal.seniorproject.Company;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.kemal.seniorproject.Settings.GallerySettings;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyAddFragment extends Fragment {


    SessionManager sessionManager;
    private ProgressDialog progressDialog;


    private TextView ed_name, ed_since, ed_adress, ed_city, ed_ilce, ed_description;
    private Button btn_companyAdd;
    private FloatingActionButton select_photo;
    private ImageView companyPhoto;


    private String COMPANY_ADD_URL;
    private String myId;

    private static int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    private AccountFragment accountFragment;
    private String TAG = "CompanyAddFragment";

    private Spinner select_sector;
    public String sector = "";

    public CompanyAddFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_add, container, false);


        sessionManager = new SessionManager(getContext());

        SessionManager.init(getActivity());
        myId = SessionManager.myId;
        progressDialog = new ProgressDialog(getContext());


        accountFragment = new AccountFragment();

        ServerAdress.init(getActivity());
        COMPANY_ADD_URL = ServerAdress.host + "company_add.php";

        ed_name = view.findViewById(R.id.ed_company_name);
        ed_since = view.findViewById(R.id.ed_since);
        ed_adress = view.findViewById(R.id.ed_adres);
        ed_city = view.findViewById(R.id.city);
        ed_ilce = view.findViewById(R.id.ilce);
        ed_description = view.findViewById(R.id.ed_company_description);
        btn_companyAdd = view.findViewById(R.id.btn_companyAdd);
        select_photo = view.findViewById(R.id.select_company_photo);
        companyPhoto = view.findViewById(R.id.company_add_imageView);
        select_sector = view.findViewById(R.id.select_sector);

        btn_companyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = ed_name.getText().toString().trim();
                String since = ed_since.getText().toString().trim();
                String adress = ed_adress.getText().toString().trim();
                String city = ed_city.getText().toString().trim();
                String ilce = ed_ilce.getText().toString().trim();
                String description = ed_description.getText().toString().trim();
                String imageBitmap = getStringImage(bitmap);


                InsertCompany(name, since, adress, city, ilce, description, imageBitmap);
            }
        });

        select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        select_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

                ((TextView) parent.getChildAt(0)).setTextSize(13);
                if (parent.getItemIdAtPosition(pos) != 0)
                    sector = String.valueOf(parent.getItemAtPosition(pos));
                else
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }


    private void InsertCompany(final String name, final String since, final String adress, final String city, final String ilce, final String description, final String imageBitmap) {
        if (name.equals("")) {
            ed_name.setError("Şirket ismi boş geçilemez");
            return;

        }
        if (since.equals("")) {
            ed_since.setError("Tarih kısmı boş geçilemez");
            return;

        }
        if (adress.equals("")) {
            ed_adress.setError("Adres boş geçilemez");
            return;

        }
        if (sector.equals("")) {
            Toast.makeText(getContext(), "Sektör alanı boş geçilemez", Toast.LENGTH_SHORT).show();
            return;
        }
        if (city.equals("")) {
            ed_city.setError("Şehir boş geçilemez");
            return;
        }
        if (ilce.equals("")) {
            ed_ilce.setError("İlçe boş geçilemez");
            return;
        }
        if (description.equals("")) {
            ed_description.setError("Açıklama boş geçilemez");
            return;
        }
        progressDialog.setTitle("Lütfen bekleyin...");
        progressDialog.setMessage("Şirket oluşturuluyor...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);
        params.put("name", name);
        params.put("since", since);
        params.put("sector", sector);
        params.put("adress", adress);
        params.put("city", city);
        params.put("ilce", ilce);
        params.put("description", description);
        params.put("photo", imageBitmap);

        MyVolley volley = new MyVolley();
        volley.volley(getContext(), COMPANY_ADD_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    int id = jsonObject.getInt("id");
                    String companyName = jsonObject.getString("companyName");
                    String image_url = jsonObject.getString("image_url");

                    Boolean control = jsonObject.getBoolean("control");
                    if (control) {
                        sessionManager.addCompany(String.valueOf(id), companyName, image_url);
                        Toast.makeText(getContext(), "Şirket başarıyla oluşturuldu", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content, accountFragment)
                                .addToBackStack(null)
                                .commit();

                    } else {
                        Toast.makeText(getContext(), "Şirket başarıyla oluşturulurken bir hata meydana geldi", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });


    }

    private void selectPhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), this);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);

            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = GallerySettings.activityResult(requestCode, resultCode, data, getActivity(), getActivity().getCacheDir());


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                companyPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPhoto();
            }
        }
    }

    public String getStringImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byte[] imageByteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

            return encodedImage;
        } catch (Exception ex) {
            return "";
        }
    }


}
