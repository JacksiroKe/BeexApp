package com.jackson_siro.beexpress.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.jackson_siro.beexpress.adapter.CommentShowAdapter;
import com.jackson_siro.beexpress.modal.Callback.CallbackShowComment;
import com.jackson_siro.beexpress.modal.Comment;
import com.jackson_siro.beexpress.retrofitconfig.API;
import com.jackson_siro.beexpress.retrofitconfig.CallJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.jackson_siro.beexpress.R;

public class CommentShow extends AppCompatActivity {

    private static final String EXT_OBJ_ID = "key.EXTRA_OBJ_ID";

    public static void navigateParentFromCommentUser(Activity activity, Integer id){
        Intent intent = navigateBase(activity, id);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }

    public static Intent navigateBase(Context context, Integer id){
        Intent intent = new Intent(context,CommentShow.class);
        intent.putExtra(EXT_OBJ_ID, id);
        return intent;
    }

    public static void navigateParent(Activity activity, Integer id, Boolean b){
        Intent intent = navigateBase(activity, id, b);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.animation_right_to_left, R.anim.animation_blank);
    }

    public static Intent navigateBase(Context context, Integer id, Boolean b){
        Intent intent = new Intent(context,CommentShow.class);
        intent.putExtra(EXT_OBJ_ID, id);
        return intent;
    }

    private int Id_Show;
    private CommentShowAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private Call<CallbackShowComment> commentCall;
    private CircleImageView add_comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        initInterface();
        initData();
    }

    private void initInterface(){
        Id_Show = getIntent().getIntExtra(EXT_OBJ_ID, 0);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        add_comment = (CircleImageView) findViewById(R.id.add_comment);
        layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CommentShowAdapter(getApplicationContext(), new ArrayList<Comment>());
        recyclerView.setAdapter(adapter);

        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentUser.navigateParent(CommentShow.this, Id_Show, false);
                Log.d("UserIdCheck", String.valueOf(Id_Show));
            }
        });
    }

    private void showComment(List<Comment> comments){
        adapter.insertComment(comments);
    }

    private void initData(){
        API api = CallJson.callJson();
        commentCall = api.getShowComment(Id_Show);
        commentCall.enqueue(new Callback<CallbackShowComment>() {
            @Override
            public void onResponse(Call<CallbackShowComment> call, Response<CallbackShowComment> response) {
                CallbackShowComment csc = response.body();
                if (csc != null){
                    showComment(csc.data);
                }
            }

            @Override
            public void onFailure(Call<CallbackShowComment> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailItems.navigateParentFromCommentShow(CommentShow.this, Id_Show);
    }
}
