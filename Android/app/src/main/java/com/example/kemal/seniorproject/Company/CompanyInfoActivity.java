package com.example.kemal.seniorproject.Company;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemal.seniorproject.LoginActivity;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyInfoActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private String COMPANY_INFO_URL, id;

    private TextView txtname, txtsector, txtsince, txtdescription, txtadress;
    private ImageButton close;

    private String TAG = "CompanyInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        txtname = findViewById(R.id.company_info_name);
        txtsector = findViewById(R.id.company_info_sector);
        txtsince = findViewById(R.id.company_info_since);
        txtdescription = findViewById(R.id.company_info_description);
        txtadress = findViewById(R.id.company_info_adress);
        close = findViewById(R.id.company_info_close);


        ServerAdress.init(CompanyInfoActivity.this);

        COMPANY_INFO_URL =ServerAdress.host + "company_info.php";
        id = getIntent().getStringExtra("id");


        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("type", "typetwo");


        MyVolley volley = new MyVolley();

        volley.volley(getApplicationContext(), COMPANY_INFO_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

    }

    private void loadData(JSONObject jsonObject) {
        try {
            Boolean control = jsonObject.getBoolean("success");
            if (control) {
                String name = jsonObject.getString("name");
                String sector = jsonObject.getString("sector");
                String since = jsonObject.getString("since");
                String description = jsonObject.getString("description");
                final String adress = jsonObject.getString("adress");

                txtname.setText(name);
                txtsector.setText(sector);
                txtsince.setText(since);
                txtdescription.setText(description);
                txtadress.setText(adress);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        onMyMapReady(googleMap, adress);
                    }
                });

            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(CompanyInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }


    private void onMyMapReady(GoogleMap googleMap, String location) {

        mMap = googleMap;
        float zoomLevel = 16.0f;

        if (location != null && !location.equals("")) {
            List<Address> adressList = null;
            Geocoder geocoder = new Geocoder(CompanyInfoActivity.this);
            try {
                adressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = adressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Burası " + location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));


        }


    }


}
