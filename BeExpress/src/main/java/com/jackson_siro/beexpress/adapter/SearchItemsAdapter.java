package com.jackson_siro.beexpress.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.modal.ItemsSearch;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.R;

public class SearchItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<ItemsSearch> itemsSearches;
    public OnItemClickListener onItemClickListener;
    private boolean loading;
    public interface OnItemClickListener{
        void onItemClick(View view, ItemsSearch itemsSearch, int i);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public SearchItemsAdapter(Activity activity, List<ItemsSearch> itemsSearches) {
        this.activity = activity;
        this.itemsSearches = itemsSearches;
    }

    public class SViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgsubCategory;
        public TextView textsubCategory;
        public MaterialRippleLayout lyt_parent;

        public SViewHolder(View itemView) {
            super(itemView);

            imgsubCategory = (ImageView) itemView.findViewById(R.id.imgsubCategory);
            textsubCategory = (TextView) itemView.findViewById(R.id.textsubCategory);
            lyt_parent = (MaterialRippleLayout) itemView.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sub_category, parent, false);
        return new SViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemsSearch itemsSearch = itemsSearches.get(position);
        SViewHolder sViewHolder = (SViewHolder) holder;
        Picasso.with(activity).load(BaseUrlConfig.BASE_URL+itemsSearch.image).into(sViewHolder.imgsubCategory);
        sViewHolder.textsubCategory.setText(itemsSearch.title);
        sViewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(view, itemsSearch, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsSearches.size();
    }


    public void resetListData(){
        this.itemsSearches = new ArrayList<>();
        notifyDataSetChanged();
    }
}

