package com.jackson_siro.beexpress.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.ui.notification.MySingleton;

import com.jackson_siro.beexpress.R;

public class CommentUser extends AppCompatActivity{

    private static final String EXT_OBJ_ID = "key.EXTRA_OBJ_ID";
    private static final String EXT_NOTIFICATION_ID = "key.NOTIFICATION.ID";

    public static void navigateParent(Activity activity, Integer id, Boolean b){
        Intent intent = navigateBase(activity, id, b);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }

    public static Intent navigateBase(Context context, Integer id, Boolean b){
        Intent intent = new Intent(context,CommentUser.class);
        intent.putExtra(EXT_OBJ_ID, id);
        intent.putExtra(EXT_NOTIFICATION_ID, id);
        return intent;
    }

    private EditText input_user_comment;
    private ImageView user_comment_send;
    private ProgressDialog dialog;
    private LinearLayout linear_layout;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int id;
    private String get_comment, get_user_photo;
    private int items_id;
    private CircleImageView user_comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initInterface();
    }

    private void initInterface() {
        items_id = getIntent().getIntExtra(EXT_OBJ_ID, 0);
        input_user_comment = (EditText) findViewById(R.id.input_user_comment);
        user_comment_send = (ImageView) findViewById(R.id.user_comment_send);
        linear_layout = (LinearLayout) findViewById(R.id.linear_layout);
        user_comment = (CircleImageView) findViewById(R.id.user_comment);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        get_user_photo = preferences.getString("login_profile_image", "");
        Picasso.with(getApplicationContext()).load(BaseUrlConfig.BASE_URL+BaseUrlConfig.BASE_URL_IMAGE+get_user_photo).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(user_comment);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.adding_comment));
        get_comment = input_user_comment.getText().toString().trim();

        user_comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                id = preferences.getInt("login_user_id", 0);
                get_comment = input_user_comment.getText().toString().trim();
                items_id = getIntent().getIntExtra(EXT_OBJ_ID, 0);

                if (get_comment.isEmpty()){
                    Toast.makeText(getApplicationContext(), "please", Toast.LENGTH_LONG).show();
                }else {
                    sendComment(String.valueOf(id), String.valueOf(items_id), get_comment);
                    Log.d("UserId", String.valueOf(id));
                    Log.d("ItemsId", String.valueOf(items_id));
                    Log.d("CommentUser", get_comment);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CommentShow.navigateParentFromCommentUser(CommentUser.this, items_id);
                        }
                    }, 1500);
                }
            }
        });
    }

    private void sendComment(final String app_user, final String items_id, final String comment) {
        showDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StringRequest request = new StringRequest(Request.Method.POST, BaseUrlConfig.BASE_URL + BaseUrlConfig.USER_COMMENT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("com.jackson_siro.beexpress_comment")){

                                Snackbar.make(linear_layout, R.string.success_adding_comment, Snackbar.LENGTH_SHORT).show();
                                input_user_comment.getText().clear();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String> param = new HashMap<>();
                        param.put("User_Id", app_user);
                        param.put("itemid", items_id);
                        param.put("comment", comment);
                        return param;
                    }
                };

                MySingleton.getmInstance(getApplication()).addToRequest(request);
            }
        }, 1000);
    }

    private void showDialog(){
        if (!dialog.isShowing()){
            dialog.show();
        }
    }

    private void hideDialog(){
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommentShow.navigateParentFromCommentUser(CommentUser.this, items_id);
    }
}
