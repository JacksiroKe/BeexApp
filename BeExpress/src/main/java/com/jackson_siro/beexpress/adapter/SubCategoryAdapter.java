package com.jackson_siro.beexpress.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.modal.Items;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.R;
import com.jackson_siro.beexpress.shared.SharedStore;

public class SubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<Items> itemsList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnLoadMoreListener onLoadMoreListener;
    private SharedStore sharedStore;
    public int ITEM = 1;
    public int PROGRES = 0;
    private boolean loading;
    public interface OnItemClickListener{
        void onItemClick(View view, Items items, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public SubCategoryAdapter(Context context, RecyclerView view, List<Items> itemsList1) {
        this.context = context;
        this.itemsList = itemsList1;
        sharedStore = new SharedStore(context);
        viewDetector(view);
    }

    public class VHolder extends RecyclerView.ViewHolder{

        public ImageView imgsubCategory;
        public TextView textsubCategory;
        public TextView shortTitlesubCategory;
        public TextView dateItems;
        public MaterialRippleLayout lyt_parent;

        public VHolder(View itemView) {
            super(itemView);
            imgsubCategory = (ImageView) itemView.findViewById(R.id.imgBig);
            textsubCategory = (TextView) itemView.findViewById(R.id.title);
            dateItems = (TextView) itemView.findViewById(R.id.date_items);
            shortTitlesubCategory = (TextView) itemView.findViewById(R.id.shortTitle);
            lyt_parent = (MaterialRippleLayout) itemView.findViewById(R.id.material_ripple);
        }
    }

    public static class ProgressLoadingData extends RecyclerView.ViewHolder{

        public ProgressBar progressBar;

        public ProgressLoadingData(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_all_items, parent, false);
            vh = new VHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_data, parent, false);
            vh = new VHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof  VHolder){
            final Items items = itemsList.get(position);
            VHolder vHolder = (VHolder) holder;
            Picasso.with(context).load(BaseUrlConfig.BASE_URL+items.image).into(vHolder.imgsubCategory);
            vHolder.textsubCategory.setText(items.title);
            vHolder.shortTitlesubCategory.setText(items.code);
            vHolder.dateItems.setText(items.created);
            vHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClick(view, items, position);
                    }
                }
            });
        }else{
            ((ProgressLoadingData) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.itemsList.get(position) != null ? ITEM : PROGRES;
    }

    public void insertData(List<Items> itemInsert){
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = itemInsert.size();
        this.itemsList.addAll(itemInsert);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int k = 0; k < getItemCount(); k++){
            if (itemsList.get(k) == null){
                itemsList.remove(k);
                notifyItemRemoved(k);
            }
        }
    }

    public void setLoadingItems(){
        if (getItemCount() != 0){
            this.itemsList.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData(){
        this.itemsList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void viewDetector(RecyclerView view){
        if (view.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager layoutManager = (LinearLayoutManager) view.getLayoutManager();
            view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int ltPost = layoutManager.findFirstVisibleItemPosition();
                    if (!loading && ltPost == getItemCount() - 1 && onLoadMoreListener != null){
                        if (onLoadMoreListener != null){
                            int cur_pg = getItemCount() / BaseUrlConfig.Request_Load_More;
                            onLoadMoreListener.onLoadMore(cur_pg);
                        }
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener{
        void onLoadMore(int cur_page);
    }
}
