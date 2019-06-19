package com.example.kemal.seniorproject.User;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.GallerySettings;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.ShowPhotoActivity;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView profile_status, following_count, post_count;
    private ImageView profile_image;
    private ImageButton profile_popup_menu;
    private FloatingActionButton add_post;

    private String myId, image_url, name, surname, companyId;


    private static int PICK_IMAGE_REQUEST = 1;
    private String USER_INFO_PROFILE;
    private String USER_EDIT_PROFILE;


    private Bitmap bitmap;

    ProfileWarningFragment profileWarningFragment;
    ProfilePostFragment profilePostFragment;

    private String TAG = "ProfileFragment";
    private MyVolley volley;

    private TabLayout tabLayout;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tabLayout = view.findViewById(R.id.profile_tab_menu);

        initTab();


        profileWarningFragment = new ProfileWarningFragment();
        profilePostFragment = new ProfilePostFragment();

        ServerAdress.init(getActivity());
        USER_INFO_PROFILE = ServerAdress.host + "user_info.php";
        USER_EDIT_PROFILE = ServerAdress.host + "edit_user_profile.php";

        SessionManager.init(getContext());
        myId = SessionManager.myId;
        companyId = SessionManager.companyId;

        profile_image = view.findViewById(R.id.profile_image_fragment_profile);
        profile_popup_menu = view.findViewById(R.id.profile_popup_menu);
        profile_status = view.findViewById(R.id.profile_status);
        following_count = view.findViewById(R.id.following_count);
        post_count = view.findViewById(R.id.post_count);
        add_post = view.findViewById(R.id.float_add_post);


        profile_image.setOnClickListener(this);
        profile_popup_menu.setOnClickListener(this);
        add_post.setOnClickListener(this);

        if (companyId.equals("")) {
            add_post.setVisibility(View.INVISIBLE);
            setFragment(profileWarningFragment);

        } else {
            add_post.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putString("id", myId);
            profilePostFragment = new ProfilePostFragment();
            profilePostFragment.setArguments(bundle);
            setFragment(profilePostFragment);
        }

        Bundle bundle = new Bundle();
        bundle.putString("id", myId);
        profilePostFragment = new ProfilePostFragment();
        profilePostFragment.setArguments(bundle);
        setFragment(profilePostFragment);

        volley = new MyVolley();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        tabLayout.getTabAt(0).select();
        Map<String, String> params = new HashMap<>();
        String myId = SessionManager.myId;
        params.put("id", myId);
        params.put("type", "typeone");

        volley.volley(getContext(), USER_INFO_PROFILE, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        volley.cancel(TAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.float_add_post:
                Intent postIntent = new Intent(getContext(), PostAddActivity.class);
                startActivity(postIntent);
                break;
            case R.id.profile_image_fragment_profile:
                Intent photoIntent = new Intent(getContext(), ShowPhotoActivity.class);
                photoIntent.putExtra("image_url", image_url);
                photoIntent.putExtra("name", name);
                photoIntent.putExtra("surname", surname);
                startActivity(photoIntent);
                break;

            case R.id.profile_popup_menu:
                showPopupMenu();
                break;


        }
    }


    private void showPopupMenu() {
        new BottomSheet.Builder(getActivity()).sheet(R.menu.profile_popup_menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case R.id.change_picture_popup:
                        changePhoto();
                        break;

                    case R.id.change_status_popup:
                        changeStatus();
                        break;
                }
            }
        }).show();
    }

    private void changePhoto() {
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

    private void changeStatus() {
        View openDialog = View.inflate(getContext(), R.layout.open_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage("");
        builder.setCancelable(false);

        final EditText editText = openDialog.findViewById(R.id.edittext_dialog);
        final TextView textView = openDialog.findViewById(R.id.textview_dialog);

        editText.setText(profile_status.getText().toString());
        textView.setText(profile_status.length() + "/" + 100);

        editText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(100)
        });

        editText.setSelection(editText.length());

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView.setText(String.valueOf(charSequence.length()) + "/" + String.valueOf(100));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editText.setTextSize(14);
        editText.setHint("Yeni durumunuzu giriniz");
        editText.addTextChangedListener(textWatcher);
        builder.setView(openDialog);


        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String newStatus = editText.getText().toString().trim();
                editPhotoOrStatus(newStatus, "status");
                profile_status.setText(newStatus);

            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadData(JSONObject jsonObject) {

        try {

            Boolean success = jsonObject.getBoolean("success");


            if (success) {
                image_url = ServerAdress.profile_images + jsonObject.getString("image_url");

                Glide.with(getContext()).load(image_url).into(profile_image);
                String status = jsonObject.getString("status");
                profile_status.setText(status);
                name = jsonObject.getString("name");
                surname = jsonObject.getString("surname");

                int flwCount = jsonObject.getInt("followingCount");
                following_count.setText(String.valueOf(flwCount));

                int pstCount = jsonObject.getInt("postCount");
                post_count.setText(String.valueOf(pstCount));


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                changePhoto();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {

            Uri uri = GallerySettings.activityResult(requestCode, resultCode, data, getActivity(), getActivity().getCacheDir());

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                String imageBitmap = getStringImage(bitmap);


                editPhotoOrStatus(imageBitmap, "image_url");


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void editPhotoOrStatus(final String value, final String column) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_EDIT_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean control = jsonObject.getBoolean("control");

                    if (control) {
                        Toast.makeText(getContext(), "Güncelleştirme başarıyla gerçekleşti.", Toast.LENGTH_SHORT).show();
                        if (column.equals("image_url")) {
                            Picasso.get().load(image_url).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_image);
                        }
                    } else
                        Toast.makeText(getContext(), "Güncelleştirme sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    Toast.makeText(getContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String myId = SessionManager.myId;
                params.put("id", myId);
                params.put("column", column);
                params.put("value", value);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

    private void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void initTab() {

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_bookmarks));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_info));

        int black = ContextCompat.getColor(getContext(), R.color.black);
        int grey = ContextCompat.getColor(getContext(), R.color.grey);

        tabLayout.getTabAt(0).getIcon().setColorFilter(black, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Bundle bundle;
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                switch (tab.getPosition()) {
                    case 0:
                        bundle = new Bundle();
                        bundle.putString("id", myId);
                        profilePostFragment = new ProfilePostFragment();
                        profilePostFragment.setArguments(bundle);
                        setFragment(profilePostFragment);
                        break;
                    case 1:
                        FavPostFragment favPostFragment = new FavPostFragment();
                        setFragment(favPostFragment);
                        break;
                    case 2:
                        Intent intent = new Intent(getContext(), ProfileInfoActivity.class);
                        intent.putExtra("id", myId);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.grey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
