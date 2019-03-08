package com.jackson_siro.beexpress.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jackson_siro.beexpress.db.DatabaseHelpers;
import com.jackson_siro.beexpress.modal.Callback.CallbackCountComment;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsDetail;
import com.jackson_siro.beexpress.modal.Comment;
import com.jackson_siro.beexpress.modal.Items;
import com.jackson_siro.beexpress.retrofitconfig.API;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.retrofitconfig.CallJson;
import com.jackson_siro.beexpress.setting.CheckNetwork;
import com.jackson_siro.beexpress.shared.PrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.jackson_siro.beexpress.R;

public class DetailItems extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private AdRequest interstitialAdRequest;
    private AdView adView;

    private static final String EXT_OBJ_ID = "key.EXTRA_OBJ_ID";
    private static final String EXT_NOTIFICATION_ID = "key.NOTIFICATION.ID";

    public static void navigateParentFromCommentShow(Activity activity, Integer id){
        Intent intent = navigateBase(activity, id);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }

    public static Intent navigateBase(Context context, Integer id){
        Intent intent = new Intent(context, DetailItems.class);
        intent.putExtra(EXT_OBJ_ID, id);
        intent.putExtra(EXT_NOTIFICATION_ID, id);
        return intent;
    }

    public static void navigateParent(Activity activity, Integer id, Boolean b){
        Intent intent = navigateBase(activity, id, b);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }

    public static Intent navigateBase(Context context, Integer id, Boolean b){
        Intent intent = new Intent(context, DetailItems.class);
        intent.putExtra(EXT_OBJ_ID, id);
        intent.putExtra(EXT_NOTIFICATION_ID, id);
        return intent;
    }

    private long menu_id;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Boolean form_notification;
    private Call<CallbackItemsDetail> itemsCall = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private WebView webView;
    private ImageView image, img_cover;
    private TextView title;
    private Items items;
    private MenuItem wishlist, favourites;
    private DatabaseHelpers databaseHelpers;
    private PrefManager session;
    private FrameLayout user_comment;
    private TextView cart_badge;
    private Call<CallbackCountComment> countCommentCall;
    private Comment comment;
    private Button user_comment_load;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_items);
        menu_id = getIntent().getIntExtra(EXT_OBJ_ID, 0);
        webView = (WebView) findViewById(R.id.webView);
        image = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        img_cover = (ImageView) findViewById(R.id.img_cover);
        user_comment_load = (Button) findViewById(R.id.user_comment);
        session = new PrefManager(getApplicationContext());

        loadInterstitialAd();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME_ADS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int counter = getResources().getInteger(R.integer.mAdCounter);
        counter = sharedPreferences.getInt(getString(R.string.PREFERENCES_COUNTER), 0);
        if (counter == getResources().getInteger(R.integer.adsCounterRequestDetailItems)){
            interstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
            counter = 0;
        }else{
            counter++;
        }
        editor.putInt(getString(R.string.PREFERENCES_NAME_ADS), counter);
        editor.commit();

        MobileAds.initialize(this, getString(R.string.banner_ads));
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //form_notification = getIntent().getBooleanExtra(EXT_NOTIFICATION_ID, false);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layot);
        databaseHelpers = new DatabaseHelpers(this);
        swipeData();
        toolbarSet();
        requestAction();

        if (session.isLoggedIn()){
            user_comment_load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentShow.navigateParentFromCommentUser(DetailItems.this, items.itemid);
                    Log.d("BUTTON_LOAD", String.valueOf(items.itemid));
                }
            });
        }
    }

    public void loadInterstitialAd(){
        interstitialAdRequest = new AdRequest.Builder().build();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ads));
        interstitialAd.loadAd(interstitialAdRequest);
    }

    public void showInterstitial(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }
    }

    public void shareItem(String url, final String text) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_TEXT, text);
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    private Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void swipeData() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
            }
        });
    }

    private void toolbarSet() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    private void requestAction() {
        onFailedView(false, "");
        swipeRefreshProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestActionDetail();
            }
        }, 1000);
    }

    private void onFailedView(boolean show, String s){
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        View lyt_content = (View) findViewById(R.id.lyt_content);

        ((TextView) findViewById(R.id.failed_text)).setText(s);
        if (show){
            lyt_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        }else{
            lyt_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
    }

    private void onFailedRequest(){
        swipeRefreshProgress(false);
        if (CheckNetwork.isConnectCheck(this)){
            onFailedView(true, getString(R.string.no_data));
        }else{
            onFailedView(true, getString(R.string.not_yet_items));
        }
    }

    private void swipeRefreshProgress(final boolean show){
        if (!show){
            swipeRefreshLayout.setRefreshing(show);
            return;
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(show);
            }
        });
    }

    private void requestActionDetail() {
        API api = CallJson.callJson();
        itemsCall = api.getAllItemsDetail(menu_id);
        itemsCall.enqueue(new Callback<CallbackItemsDetail>() {
            @Override
            public void onResponse(Call<CallbackItemsDetail> call, Response<CallbackItemsDetail> response) {
                CallbackItemsDetail cl = response.body();
                if (cl != null){
                    items = cl.data;
                    displayData();
                    swipeRefreshProgress(false);
                    img_cover.setVisibility(View.GONE);
                }else{
                    onFailedRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackItemsDetail> call, Throwable t) {
                if (!call.isCanceled()) onFailedRequest();
            }
        });


    }

    private void displayData() {
        title.setText(items.title);
        String html = "<style>img{display: inline; height: auto; max-width: 100%;}</style>";
        webView.loadDataWithBaseURL(null, html+items.content, "text/html", "UTF-8", null);
        Picasso.with(getApplicationContext()).load(BaseUrlConfig.BASE_URL+items.image).into(image);
    }

    @Override
    protected void onPause() {
        if (adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null){
            adView.resume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.detail_items, menu);
        favourites = menu.findItem(R.id.action_wish);
        wishlist = menu.findItem(R.id.user_comment);


        View view = (View) MenuItemCompat.getActionView(wishlist);
        cart_badge = (TextView) view.findViewById(R.id.cart_badge);
        user_comment = (FrameLayout) view.findViewById(R.id.user_comment);
        getCountComment();

        if (databaseHelpers.isDataExist(menu_id)){
            favourites.setIcon(R.drawable.ic_favorite_black_48dp);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(wishlist);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_variable = item.getItemId();
        if (item_variable == android.R.id.home){
            onBackPressed();
        }else if (item_variable == R.id.action_wish){
            try{
                if (databaseHelpers.isDataExist(items.itemid)){
                    databaseHelpers.deleteData(items.itemid);
                    favourites.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }else{
                    databaseHelpers.addData(items.itemid, items.title, items.code, items.category, items.image, items.content, items.created);
                    favourites.setIcon(R.drawable.ic_favorite_black_48dp);
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), getString(R.string.please_wait_loading_data), Toast.LENGTH_LONG).show();
            }
        }else if (item_variable == R.id.share){
            final String uri = BaseUrlConfig.BASE_URL+items.image;
            shareItem(uri, getString(R.string.text_share) + " "
                    +getString(R.string.google_play_store)+getPackageName());
        }else if (item_variable == R.id.user_comment){

            if (session.isLoggedIn()){
                CommentShow.navigateParentFromCommentUser(DetailItems.this, items.itemid);
                Log.d("OnOptionLoad", String.valueOf(items.itemid));
            }else{
                Toast.makeText(getApplicationContext(), R.string.you_must_login, Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCountComment(){
        API api = CallJson.callJson();
        countCommentCall = api.getCountComment(menu_id);
        countCommentCall.enqueue(new Callback<CallbackCountComment>() {
            @Override
            public void onResponse(Call<CallbackCountComment> call, Response<CallbackCountComment> response) {
                CallbackCountComment ccc = response.body();
                if (ccc != null){
                    comment = ccc.comment;
                    if (comment.id == 0){
                        cart_badge.setVisibility(View.GONE);
                        user_comment_load.setVisibility(View.GONE);
                    }else{
                        cart_badge.setText(comment.id + "");
                        user_comment_load.setText(comment.id + " " + "Comment");
                        user_comment_load.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackCountComment> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_blank, R.anim.animation_left_to_right);
    }

    @Override
    protected void onDestroy() {
        if (adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }
}
