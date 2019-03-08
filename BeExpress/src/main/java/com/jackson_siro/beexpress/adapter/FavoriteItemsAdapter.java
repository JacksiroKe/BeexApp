package com.jackson_siro.beexpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.ui.OfflineItems;
import com.jackson_siro.beexpress.R;

public class FavoriteItemsAdapter extends BaseAdapter {

    private Context context;
    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int i);
    }

    public FavoriteItemsAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return OfflineItems.itemid.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null){

            view = LayoutInflater.from(context).inflate(R.layout.card_all_items, null);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imgHome = (ImageView) view.findViewById(R.id.imgBig);
        viewHolder.textHome = (TextView) view.findViewById(R.id.title);
        viewHolder.description = (TextView) view.findViewById(R.id.shortTitle);
        viewHolder.dateitems = (TextView) view.findViewById(R.id.date_items);

        Picasso.with(context).load(BaseUrlConfig.BASE_URL+ OfflineItems.image.get(i)).into(viewHolder.imgHome);
        viewHolder.textHome.setText("" + OfflineItems.title.get(i));
        viewHolder.description.setText("" + OfflineItems.code.get(i));
        viewHolder.dateitems.setText("" + OfflineItems.created.get(i));
        viewHolder.lyt_parent = (MaterialRippleLayout) view.findViewById(R.id.material_ripple);
        viewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(view, i);
                }
            }
        });

        return view;
    }

    static class ViewHolder{
        ImageView imgHome;
        TextView textHome;
        TextView description;
        TextView dateitems;
        MaterialRippleLayout lyt_parent;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
