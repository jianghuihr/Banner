package com.jhbanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BannerPagerAdapter extends PagerAdapter {

    private List<View> bannerList;

    public BannerPagerAdapter() {
        bannerList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = bannerList.get(position);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = bannerList.get(position);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            container.removeView(view);
        }
    }

    public void refresh(List<View> bannerList) {
        this.bannerList.clear();
        this.bannerList.addAll(bannerList);
        notifyDataSetChanged();
    }
}
