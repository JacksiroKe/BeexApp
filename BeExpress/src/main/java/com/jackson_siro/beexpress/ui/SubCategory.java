package com.jackson_siro.beexpress.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.adapter.SubCategoryAdapter;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsByCategory;
import com.jackson_siro.beexpress.modal.Category;
import com.jackson_siro.beexpress.modal.Items;
import com.jackson_siro.beexpress.retrofitconfig.API;
import com.jackson_siro.beexpress.retrofitconfig.CallJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.jackson_siro.beexpress.R;

public class SubCategory extends AppCompatActivity {

    private static final String EXTRA_GET_OBJ = "key.EXTRA_GET_OBJ";

    public static void passingIntent(Activity activity, Category category){
        Intent intent = new Intent(activity, SubCategory.class);
        intent.putExtra(EXTRA_GET_OBJ, category);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }

    private Toolbar toolbar;
    private ActionBar actionBar;
    private Category category;
    private AppBarLayout appBarLayout;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackItemsByCategory> callbackItemsByCategoryCall;
    private RecyclerView recyclerView;
    private SubCategoryAdapter adapter;
    private int post_total = 0;
    private int failed_page = 0;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        view = findViewById(android.R.id.content);
        category = (Category) getIntent().getSerializableExtra(EXTRA_GET_OBJ);
        component();
        toolbarSet();
        requestData();
    }

    private void toolbarSet() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    private void component() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new SubCategoryAdapter(this, recyclerView, new ArrayList<Items>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SubCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Items items, int position) {
                DetailItems.navigateParent(SubCategory.this, items.itemid, false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackItemsByCategoryCall != null && callbackItemsByCategoryCall.isExecuted())
                    callbackItemsByCategoryCall.cancel();
                adapter.resetListData();
                requestData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items_search_no_action, menu);
        menu.findItem(R.id.go_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_variable = item.getItemId();
        if (item_variable == android.R.id.home){
            onBackPressed();
        }else if (item_variable == R.id.go_search){
            startActivity(new Intent(SubCategory.this, SearchItems.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_blank, R.anim.animation_left_to_right);
    }

    private void displaydata(List<Items> categories) {
        adapter.insertData(categories);
        if (categories.size() == 0);
    }

    private void requestData() {
        API api = CallJson.callJson();
        callbackItemsByCategoryCall = api.getItemsCategory(category.categoryid);
        callbackItemsByCategoryCall.enqueue(new Callback<CallbackItemsByCategory>() {
            @Override
            public void onResponse(Call<CallbackItemsByCategory> call, Response<CallbackItemsByCategory> response) {
                CallbackItemsByCategory cnbc = response.body();
                if (cnbc != null){
                    displaydata(cnbc.data);
                }else{
                    //null showing
                }
            }

            @Override
            public void onFailure(Call<CallbackItemsByCategory> call, Throwable t) {
                if (!call.isCanceled());
                //null showing
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callbackItemsByCategoryCall != null && callbackItemsByCategoryCall.isExecuted()) {
            callbackItemsByCategoryCall.cancel();
        }
    }

}
