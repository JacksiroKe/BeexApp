package com.jackson_siro.beexpress.adapter;

import android.app.Activity;
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

public class AllItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Items> itemsList;
    public OnItemClickListener onItemClickListener;
    public OnLoadMoreListener onLoadMoreListener;
    public boolean loading;
    private int VIEW_PROGRES = 0;
    private int VIEW_ITEM = 1;

    public interface OnItemClickListener{
        void onItemClick(View view, Items items);
    }

    public AllItemsAdapter(Activity activity, List<Items> itemsList){
        this.activity = activity;
        this.itemsList = itemsList;
    }

    public class SourceView extends RecyclerView.ViewHolder{

        public MaterialRippleLayout lyt_parent;
        public ImageView imgsubCategory;
        public TextView title;
        public TextView shortTitle;
        public TextView date_items;

        public SourceView(View itemView) {
            super(itemView);
            lyt_parent = itemView.findViewById(R.id.material_ripple);
            imgsubCategory = itemView.findViewById(R.id.imgBig);
            title = itemView.findViewById(R.id.title);
            shortTitle = itemView.findViewById(R.id.shortTitle);
            date_items = itemView.findViewById(R.id.date_items);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{

        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_all_items, parent, false);
            viewHolder = new SourceView(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_data, parent, false);
            viewHolder = new SourceView(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SourceView){
            final Items items = itemsList.get(position);
            SourceView sourceView = (SourceView) holder;
            Picasso.with(activity).load(BaseUrlConfig.BASE_URL+items.image).into(sourceView.imgsubCategory);
            sourceView.title.setText(items.title);
            sourceView.shortTitle.setText(items.code);
            sourceView.date_items.setText(items.created);
            sourceView.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClick(view, items);
                    }
                }
            });
        }else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return this.itemsList.get(position) != null ? VIEW_ITEM : VIEW_PROGRES;
    }

    public void resetData(){
        this.itemsList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void insertData(List<Items> itemsList){
//        setLoaded();
        int itemStart = getItemCount();
        int itemCount = itemsList.size();
        this.itemsList.addAll(itemsList);
        notifyItemRangeInserted(itemStart, itemCount);
    }

    public void setLoaded(){
        loading = false;
        for (int k = 0; k<getItemCount(); k++){
            if (itemsList.get(k) == null){
                itemsList.remove(k);
                notifyItemRemoved(k);
            }
        }
    }

    public void setLoading(){
        if (getItemCount() != 0){
            this.itemsList.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemsView(RecyclerView recyclerView){
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastItems = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastItems == getItemCount() - 1 && onLoadMoreListener != null){
                        if (onLoadMoreListener != null){
                            int get = getItemCount() / BaseUrlConfig.Request_Load_More;
                            onLoadMoreListener.onLoadMore(get);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener{
        void onLoadMore(int page);
    }
}
