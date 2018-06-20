package com.jhbanner;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ViewPager mBannerPager;
    private BannerPagerAdapter mBannerPagerAdapter;
    private TabLayout mBannerTabLayout;

    private List<View> mBannerList = new ArrayList<>();
    private int mCurrentBanner = 1;
    private int mBannerCount;
    private boolean mIsUserTouch = false;
    private long mUserTouchTime = 0L;

    private Handler mHandler = new Handler();
    private final long DELAY_DURATION = 2000L;
    private final int PAGER_SCROLL_DURATION = 800;

    private Runnable mBannerSlideShowTask = new Runnable() {
        @Override
        public void run() {
            if (mIsUserTouch) {
                return;
            }
            mCurrentBanner = mCurrentBanner % (mBannerCount + 1) + 1;
            if (mCurrentBanner == 1) {
                mBannerPager.setCurrentItem(mCurrentBanner, false);
                mHandler.post(this);
            } else {
                mBannerPager.setCurrentItem(mCurrentBanner, true);
                mHandler.postDelayed(this, DELAY_DURATION);
            }
        }
    };

    private Runnable mBannerUserTouchCheckTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - mUserTouchTime;
            long compare = diff - DELAY_DURATION;
            if (compare < 1000 && compare >= 0) {
                mIsUserTouch = false;
                mHandler.post(mBannerSlideShowTask);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = MainActivity.this;
        setContentView(R.layout.activity_main);

        initView();
        startSlideShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSlideShow();
    }

    private void startSlideShow() {
        mHandler.postDelayed(mBannerSlideShowTask, DELAY_DURATION);
    }

    private void stopSlideShow() {
        mHandler.removeCallbacks(mBannerSlideShowTask);
    }

    private void initPagerScrollDuration() {
        try {
            Field scrollField = mBannerPager.getClass().getDeclaredField("mScroller");
            scrollField.setAccessible(true);
            BannerScroller bannerScroller = new BannerScroller(mContext);
            bannerScroller.setScrollDuration(PAGER_SCROLL_DURATION);
            scrollField.set(mBannerPager, bannerScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initBannerView() {
        int[] bannerColor = new int[]{
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_purple,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light};

        for (int i = 0; i < 5; i++) {
            View view = new BannerPresenter(mContext).getView();
            TextView bannerTV = view.findViewById(R.id.tv_banner);
            bannerTV.setText(mContext.getString(R.string.banner_index, String.valueOf(i)));
            bannerTV.setBackgroundColor(ContextCompat.getColor(mContext, bannerColor[i]));
            mBannerList.add(view);
        }
        mBannerCount = mBannerList.size();
    }

    private void initView() {
        mBannerPager = findViewById(R.id.pager_banner);
        mBannerPagerAdapter = new BannerPagerAdapter();
        mBannerPager.setAdapter(mBannerPagerAdapter);

        initPagerScrollDuration();
        initBannerView();
        mBannerPagerAdapter.refresh(mBannerList);

        mBannerPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mHandler.removeCallbacks(mBannerUserTouchCheckTask);
                mIsUserTouch = true;

                mUserTouchTime = System.currentTimeMillis();
                mHandler.postDelayed(mBannerUserTouchCheckTask, DELAY_DURATION);
                return false;
            }
        });

        mBannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentBanner = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (mCurrentBanner == mBannerCount + 1) {
                            mBannerPager.setCurrentItem(1, false);
                        } else if (mCurrentBanner == 0) {
                            mBannerPager.setCurrentItem(mBannerCount, false);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (mCurrentBanner == mBannerCount + 1) {
                            mBannerPager.setCurrentItem(1, false);
                        } else if (mCurrentBanner == 0) {
                            mBannerPager.setCurrentItem(mBannerCount, false);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });

        mBannerTabLayout = findViewById(R.id.banner_tabLayout);
        mBannerTabLayout.setupWithViewPager(mBannerPager);

        for (int i = 0; i < mBannerCount + 2; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_banner_tab, null);
            TabLayout.Tab tab = mBannerTabLayout.getTabAt(i);
            if (i == 0 || i == mBannerCount + 1) {
                tab.setCustomView(null);
            } else {
                tab.setCustomView(view);
            }
        }

        mBannerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (mCurrentBanner == 0) {
                    view = mBannerTabLayout.getTabAt(mBannerCount).getCustomView();
                } else if (mCurrentBanner == mBannerCount + 1) {
                    view = mBannerTabLayout.getTabAt(1).getCustomView();
                }

                if (view == null) {
                    return;
                }
                RadioButton bannerTabRB = view.findViewById(R.id.radio_tab);
                bannerTabRB.setChecked(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view == null) {
                    return;
                }

                RadioButton bannerTabRB = view.findViewById(R.id.radio_tab);
                bannerTabRB.setChecked(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBannerPager.setCurrentItem(1);


    }
}
