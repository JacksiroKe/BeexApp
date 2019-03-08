package com.jackson_siro.beexpress.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.adapter.ItemsSliderAdapter;
import com.jackson_siro.beexpress.modal.Callback.CallbackItems;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsSlider;
import com.jackson_siro.beexpress.modal.ItemsSlider;
import com.jackson_siro.beexpress.retrofitconfig.API;
import com.jackson_siro.beexpress.retrofitconfig.CallJson;
import com.jackson_siro.beexpress.ui.DetailItems;
import com.jackson_siro.beexpress.R;
import com.jackson_siro.beexpress.setting.ToolsUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsSliderFragment extends Fragment{

    private View root_view;
    private View root_recycler;
    private Call<CallbackItems> callbackItemsCall;
    private ViewPager viewPager;
    private LinearLayout layout_dots;
    private ImageButton btn_next, btn_prev;
    private Handler handler = new Handler();
    private Runnable runnable = null;
    private ItemsSliderAdapter itemsSliderAdapter;
    private TextView title, date_items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_slider, null);
        component();
        requestData();
        return root_view;
    }

    private void component() {
        root_recycler = (CardView) root_view.findViewById(R.id.lyt_cart);
        viewPager = (ViewPager) root_view.findViewById(R.id.pager);
        layout_dots = (LinearLayout) root_view.findViewById(R.id.layout_dots);
        btn_next = (ImageButton) root_view.findViewById(R.id.bt_next);
        btn_prev = (ImageButton) root_view.findViewById(R.id.bt_previous);
        title = (TextView) root_view.findViewById(R.id.featured_items_title);
        date_items = (TextView) root_view.findViewById(R.id.date_items);
        itemsSliderAdapter = new ItemsSliderAdapter(getActivity(), new ArrayList<ItemsSlider>());

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev();
            }
        });
    }

    private void displayFromServer(List<ItemsSlider> itemsSliders){
        itemsSliderAdapter.setItem(itemsSliders);
        viewPager.setAdapter(itemsSliderAdapter);
        final ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = ToolsUtils.getFeaturedItemsImageHeight(getActivity());
        viewPager.setLayoutParams(layoutParams);

        viewPager.setCurrentItem(0);
        title.setText(itemsSliderAdapter.getItem(0).title);
        date_items.setText(itemsSliderAdapter.getItem(0).created);
        dotsAdd(layout_dots, itemsSliderAdapter.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ItemsSlider change_items = itemsSliderAdapter.getItem(position);
                title.setText(change_items.title);
                date_items.setText(change_items.created);
                dotsAdd(layout_dots, itemsSliderAdapter.getCount(), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        autoSlider(itemsSliderAdapter.getCount());
        itemsSliderAdapter.setOnItemClickListener(new ItemsSliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ItemsSlider items) {
                DetailItems.navigateParent(getActivity(), items.itemid, false);
                Log.d("You click", items.itemid.toString());
            }
        });
        root_recycler.setVisibility(View.VISIBLE);

    }

    private void requestData() {
        API api = CallJson.callJson();
        api.getItemsSlider().enqueue(new Callback<CallbackItemsSlider>() {
            @Override
            public void onResponse(Call<CallbackItemsSlider> call, Response<CallbackItemsSlider> response) {
                CallbackItemsSlider callbackItems = response.body();
                if (callbackItems != null){
                    displayFromServer(callbackItems.data);
                }
            }

            @Override
            public void onFailure(Call<CallbackItemsSlider> call, Throwable t) {

            }
        });
    }

    public void autoSlider(final int slider){
        runnable = new Runnable() {
            @Override
            public void run() {
                int position = viewPager.getCurrentItem();
                position = position + 1;
                if (position >= slider) position = 0;
                viewPager.setCurrentItem(position);
                handler.postDelayed(runnable, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void next(){
        int position = viewPager.getCurrentItem();
        position = position + 1;
        if (position >= itemsSliderAdapter.getCount()) position = 0;
        viewPager.setCurrentItem(position);
    }

    private void prev(){
        int position = viewPager.getCurrentItem();
        position = position - 1;
        if (position < 0) position = itemsSliderAdapter.getCount();
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onDestroy() {
        if (runnable != null) handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void dotsAdd(LinearLayout layout_dots, int size, int current){
        ImageView[] dots_img = new ImageView[size];
        layout_dots.removeAllViews();
        for (int a = 0; a < dots_img.length; a++){
            dots_img[a] = new ImageView(getActivity());
            int w_h_dots = 16;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(w_h_dots, w_h_dots));
            layoutParams.setMargins(9,9,9,9);
            dots_img[a].setLayoutParams(layoutParams);
            dots_img[a].setImageResource(R.drawable.dots_shape);
            dots_img[a].setColorFilter(ContextCompat.getColor(getActivity(), R.color.black_color));
            layout_dots.addView(dots_img[a]);
        }
        if (dots_img.length > 1){
            dots_img[current].setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        }
    }
}
