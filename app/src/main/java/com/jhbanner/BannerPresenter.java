package com.jhbanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class BannerPresenter {

    private Context mContext;

    public BannerPresenter(Context context) {
        this.mContext = context;
    }

    public View getView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_banner, null);
        return view;
    }
}
