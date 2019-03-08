package com.jackson_siro.beexpress.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.ui.notification.MySingleton;
import com.jackson_siro.beexpress.shared.PrefManager;
import com.jackson_siro.beexpress.R;

public class UserRegisterActivity extends AppCompatActivity {

    private CircleImageView user_image;
    private CoordinatorLayout coordinator_layout;
    private EditText txtName, txtEmail, txtPassword;
    private ProgressDialog progressBar;
    private Button btnRegister, choose_photo, go_profile;
    private PrefManager session;
    private CoordinatorLayout coordinatorLayout;
    private int id_userse;
    private String id_users;
    private Bitmap bitmap;
    private SharedPreferences preferences;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtName = findViewById(R.id.input_name);
        txtEmail = findViewById(R.id.input_email);
        txtPassword = findViewById(R.id.input_password);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        btnRegister = findViewById(R.id.button_register);
        coordinator_layout = findViewById(R.id.coordinator_layout);

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);

        session = new PrefManager(getApplicationContext());
        if (session.isLoggedIn()){
            startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtName.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    registerItemsUser(name, email, password);
                    showPopUp();
                }else{
                    Snackbar.make(coordinatorLayout, getString(R.string.validate_input), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showPopUp(){
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.card_upload_photo, null);
        user_image = view.findViewById(R.id.user_image);
        choose_photo = view.findViewById(R.id.choose_photo);
        go_profile = view.findViewById(R.id.go_profile);

        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

    }

    private void choosePhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri file = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                user_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            id_userse = preferences.getInt("login_user_id_img", 0);
            uploadPhoto(String.valueOf(id_userse), getBitmap(bitmap));
            Log.d("CheckUpload", String.valueOf(id_userse));
        }
    }

    private void uploadPhoto(final String id, final String image) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading_upload_image));
        progressDialog.show();

        final StringRequest request = new StringRequest(Request.Method.POST, BaseUrlConfig.BASE_URL + BaseUrlConfig.UPDATE_IMAGE_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equals("BeExpressKey")){

                        Snackbar.make(coordinator_layout, R.string.image_upload, Snackbar.LENGTH_SHORT).show();
                        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        preferences.edit().remove("login_user_id_img").apply();
                        preferences.edit().remove("login_profile_image").apply();

                        go_profile.setVisibility(View.VISIBLE);
                        go_profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(UserRegisterActivity.this, UserLoginActivity.class));
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.failed_upload_image, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.upload_image_error, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("User_Id", id);
                param.put("profile_image", image);
                return param;
            }
        };

        MySingleton.getmInstance(this).addToRequest(request);
    }

    private String getBitmap(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        byte[] img = outputStream.toByteArray();
        String encoding = Base64.encodeToString(img, Base64.DEFAULT);

        return encoding;
    }

    private void registerItemsUser(final String name, final String email, final String password) {
        progressBar.setMessage("Please wait. . .");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseUrlConfig.BASE_URL + BaseUrlConfig.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        JSONObject user = jsonObject.getJSONObject("register");

                        String user_id = user.getString("ids");
                        String profile_image = user.getString("profile_image");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putInt("login_user_id_img", Integer.parseInt(user_id));
                        editor.putString("login_profile_image", profile_image);
                        editor.apply();

                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.already_exists), Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> param = new HashMap<>();
                param.put("name", name);
                param.put("email", email);
                param.put("password", password);

                return param;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    public void doLogin(View view){
        startActivity(new Intent(UserRegisterActivity.this, UserLoginActivity.class));
        finish();
    }

    private void showDialog(){
        if (!progressBar.isShowing()){
            progressBar.show();
        }
    }

    private void hideDialog(){
        if (progressBar.isShowing()){
            progressBar.dismiss();
        }
    }
}
