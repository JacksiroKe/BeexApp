package com.jackson_siro.beexpress.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.jackson_siro.beexpress.adapter.FavoriteItemsAdapter;
import com.jackson_siro.beexpress.db.DatabaseHelpers;
import com.jackson_siro.beexpress.R;

public class OfflineItems extends AppCompatActivity{

    TextView not_yet;
    ListView listviewFavorites;
    ProgressBar progressFavorites;
    DatabaseHelpers databaseHelpers;
    FavoriteItemsAdapter favoriteItemsAdapter;
    AlertDialog.Builder builder;
    ArrayList<ArrayList<Object>> getDatas = new ArrayList<>();
    public static final ArrayList<Integer> itemid = new ArrayList<>();
    public static final ArrayList<Object> title = new ArrayList<>();
    public static final ArrayList<Object> code = new ArrayList<>();
    public static final ArrayList<Object> image = new ArrayList<>();
    public static final ArrayList<Object> content = new ArrayList<>();
    public static final ArrayList<Object> created = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_items);
        not_yet = (TextView) findViewById(R.id.not_yet);
        listviewFavorites = (ListView) findViewById(R.id.listviewFavorites);
        progressFavorites = (ProgressBar) findViewById(R.id.progress_loading);
        databaseHelpers = new DatabaseHelpers(this);
        favoriteItemsAdapter = new FavoriteItemsAdapter(this);

        new getDataFromServer().execute();

        favoriteItemsAdapter.setOnItemClickListener(new FavoriteItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(getApplicationContext(), DetailItemsFavorite.class);
                intent.putExtra("menu_id", itemid.get(i));
                intent.putExtra("menu_title", (String) title.get(i));
                intent.putExtra("short_title", (String) code.get(i));
                intent.putExtra("menu_image", (String) image.get(i));
                intent.putExtra("description", (String) content.get(i));
                startActivity(intent);
                overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
            }
        });

        toolbarSet();
    }

    private void toolbarSet() {
//        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setTitle("");
    }

//    private void showPopUpDelete(final int id) {
//
//        builder = new AlertDialog.Builder(this);
//        builder.setTitle("Hapus atau Lihat");
//        builder.setMessage("Klik hapus jika ingin menghapus, klik lihat jika ingin melihat");
//        builder.setPositiveButton(R.string.deleted, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                databaseHelpers.deleteData(id);
//                listviewFavorites.invalidateViews();
//                new getDataFromServer().execute();
//            }
//        });
//        builder.setNegativeButton(R.string.view, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.create();
//        builder.show();
//    }

    public void clearData(){
        itemid.clear();
        title.clear();
        code.clear();
        image.clear();
        content.clear();
        created.clear();
    }

    private class getDataFromServer extends AsyncTask<Void, Void, Void> {

        getDataFromServer(){
            if (!progressFavorites.isShown()){
                progressFavorites.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromSql();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (itemid.size() > 0){
                listviewFavorites.setAdapter(favoriteItemsAdapter);
            }else {
                not_yet.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getDataFromSql() {

        clearData();
        getDatas = databaseHelpers.getAllData();
        for (int o = 0; o<getDatas.size(); o++){
            ArrayList<Object> rows = getDatas.get(o);
            itemid.add(Integer.parseInt(rows.get(0).toString()));
            title.add(rows.get(1).toString());
            code.add(rows.get(2).toString());
            image.add(rows.get(3).toString());
            content.add(rows.get(4).toString());
            created.add(rows.get(5).toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_variable = item.getItemId();
        if (item_variable == android.R.id.home){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        new getDataFromServer().execute();
        favoriteItemsAdapter.notifyDataSetChanged();
    }

    private void showPopUp() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_all));
        builder.setMessage(getString(R.string.message_delete_all));
        builder.setPositiveButton(R.string.deleted, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHelpers.deleteAllData();
                listviewFavorites.invalidateViews();
                new getDataFromServer().execute();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OfflineItems.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_blank);
    }
}
