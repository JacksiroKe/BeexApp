package com.jackson_siro.beexpress.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.adapter.AllItemsAdapter;
import com.jackson_siro.beexpress.modal.Callback.CallbackItems;
import com.jackson_siro.beexpress.modal.Items;
import com.jackson_siro.beexpress.retrofitconfig.API;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.retrofitconfig.CallJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.jackson_siro.beexpress.R;

public class AllItems extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private AdRequest interstitialAdRequest;
    private AdView adView;

    private Call<CallbackItems> callbackItemsCall;
    private RecyclerView recyclerView;
    private AllItemsAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int pagesa = 1;
    private boolean isLoading;
    private int pastVisibleItem, visibleIemCount, totalItemCount, previous_total = 0;
    private int view_thresould = 7;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        loadInterstitialAd();
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME_ADS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int counter = getResources().getInteger(R.integer.mAdCounter);
        counter = preferences.getInt(getString(R.string.PREFERENCES_COUNTER), 0);
        if (counter == getResources().getInteger(R.integer.adsCounterRequestAllItemsActivity)){
            interstitialAd.setAdListener(new AdListener(){
                public void onAdLoaded(){
                    showInterstitial();
                }
            });
            counter = 0;
        }else{
            counter++;
        }
        editor.putInt(getString(R.string.PREFERENCES_NAME_ADS), counter);
        editor.commit();
        adapterSet();
        requestAction(1);
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

    private void adapterSet() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AllItemsAdapter(this, new ArrayList<Items>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AllItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Items items) {
                DetailItems.navigateParent(AllItems.this, items.itemid, false);
            }
        });

//        adapter.setOnLoadMoreListener(new AllItemsAdapter.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(int page) {
//                int next_page = page++;
//                requestAction(next_page);
////                if (items_total > adapter.getItemCount() && page != 0){
////
////                }else{
////                    adapter.setLoaded();
////                }
//            }
//        });
    }

    private void displayApiResult(final List<Items> items) {
        adapter.insertData(items);
        swipeProgress(false);
        if (items.size() == 0);
    }

    private void requestAction(final int page){
        if (page == 1){
            swipeProgress(true);
        }else{
            adapter.setLoading();
        }new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData(page);
            }
        }, 1000);
    }

    private void requestData(final int page) {
        API api = CallJson.callJson();
        callbackItemsCall = api.getAllItems(BaseUrlConfig.Request_Load_More, page);
        callbackItemsCall.enqueue(new Callback<CallbackItems>() {
            @Override
            public void onResponse(Call<CallbackItems> call, Response<CallbackItems> response) {
                CallbackItems callbackItems = response.body();
                if (callbackItems != null){
                    displayApiResult(callbackItems.data);
                }
            }

            @Override
            public void onFailure(Call<CallbackItems> call, Throwable t) {
                if (!call.isCanceled());
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleIemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                pastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (dy>0){
                    if (isLoading){
                        if (totalItemCount>previous_total){
                            isLoading = false;
                            previous_total = totalItemCount;
                        }
                    }
                    if (!isLoading && (totalItemCount-visibleIemCount)<=pastVisibleItem+view_thresould){

                        ++pagesa;
                        pagination(pagesa);
                        Toast.makeText(getApplicationContext(), getString(R.string.load_more_items), Toast.LENGTH_LONG).show();
                        isLoading = true;
                    }
                    swipeProgress(false);
                }

            }
        });
    }

    private void pagination(int page){
        swipeProgress(false);
        API api = CallJson.callJson();
        callbackItemsCall = api.getAllItems(BaseUrlConfig.Request_Load_More, page);
        callbackItemsCall.enqueue(new Callback<CallbackItems>() {
            @Override
            public void onResponse(Call<CallbackItems> call, Response<CallbackItems> response) {
                CallbackItems callbackItems = response.body();
                if (callbackItems != null){
                    List<Items> items = response.body().data;
                    adapter.insertData(items);
                }
                swipeProgress(true);
            }

            @Override
            public void onFailure(Call<CallbackItems> call, Throwable t) {
                if (!call.isCanceled());
            }
        });
    }

    @Override
    public void onPause() {
        if (adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null){
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null){
            adView.destroy();
        }
        super.onDestroy();
        if (callbackItemsCall != null && callbackItemsCall.isExecuted()){
            callbackItemsCall.cancel();
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AllItems.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }
}
