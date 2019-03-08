package com.jackson_siro.beexpress.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import java.util.List;

import com.jackson_siro.beexpress.modal.ItemsSlider;
import com.jackson_siro.beexpress.retrofitconfig.BaseUrlConfig;
import com.jackson_siro.beexpress.R;

public class ItemsSliderAdapter extends PagerAdapter{

    private Activity activity;
    private List<ItemsSlider> itemsList;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View view, ItemsSlider items);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public ItemsSliderAdapter(Activity activity, List<ItemsSlider> itemsList) {
        this.activity = activity;
        this.itemsList = itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    public ItemsSlider getItem(int position){
        return itemsList.get(position);
    }

    public void setItem(List<ItemsSlider> items){
        this.itemsList = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ItemsSlider items = itemsList.get(position);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_item_image, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.image);
        MaterialRippleLayout mtr = (MaterialRippleLayout) view.findViewById(R.id.lyt_parent);
        Glide.with(activity).load(BaseUrlConfig.BASE_URL+items.image).into(img);
        mtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(view, items);
                }
            }
        });
        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
