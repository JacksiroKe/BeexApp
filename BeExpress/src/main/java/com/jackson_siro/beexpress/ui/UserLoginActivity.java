package com.jackson_siro.beexpress.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.ui.notification.MySingleton;
import com.jackson_siro.beexpress.setting.ToolsUtils;
import com.jackson_siro.beexpress.shared.PrefManager;
import com.jackson_siro.beexpress.R;

public class UserLoginActivity extends AppCompatActivity{

    private View view;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button buttonLogin;
    private TextView register;
    private Intent intent;
    private ProgressDialog progressDialog;
    private PrefManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        register = (TextView) findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        session = new PrefManager(getApplicationContext());

        if (session.isLoggedIn()){
            startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
            finish();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()){
                    loginAlready(email, password);
                }else{
                    Toast.makeText(getApplicationContext(), "Please input email or password", Toast.LENGTH_LONG).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, UserRegisterActivity.class));
            }
        });
    }

    public void loginAlready(final String email, final String password){
        showDialog();

        StringRequest request = new StringRequest(Request.Method.POST, BaseUrlConfig.BASE_URL + BaseUrlConfig.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error){
                        session.checkSetLogin(true);
                        JSONObject user = jsonObject.getJSONObject("login");
                        String user_id = user.getString("ids");
                        String uid = user.getString("uid");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        String profile_image = user.getString("profile_image");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putInt("login_user_id", Integer.parseInt(user_id));
                        editor.putString("login_unique_id", uid);
                        editor.putString("login_name", name);
                        editor.putString("login_email", email);
                        editor.putString("login_created_at", created_at);
                        editor.putString("login_profile_image", profile_image);
                        editor.apply();

                        try{
                            ToolsUtils.mainActivity.initBindMenu();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(), "Cannot Login", Toast.LENGTH_LONG).show();
                        Log.d("test", response);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String, String> param = new HashMap<>();
                param.put("email", email);
                param.put("password", password);

                return param;
            }

        };

        MySingleton.getmInstance(this).addToRequest(request);
    }

    private void showDialog(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void hideDialog(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }
}
