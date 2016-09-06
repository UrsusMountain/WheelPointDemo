package com.ursus.wheelpointdemo;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.wheelpointview)
    WheelPointView wheelpointview;
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            View view = getLayoutInflater().inflate(R.layout.layout_demo, null);
            viewList.add(view);
        }
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }
        });
        wheelpointview.bindViewPager(viewPager);
    }

    @OnClick(R.id.btn_change)
    public void onClick() {
        switch (wheelpointview.getMode()) {
            case WheelPointView.SCROLL_MODE_NORMAL:
                wheelpointview.setMode(WheelPointView.SCROLL_MODE_VISCOSITY);
                break;
            case WheelPointView.SCROLL_MODE_VISCOSITY:
                wheelpointview.setMode(WheelPointView.SCROLL_MODE_NORMAL);
                break;
        }
    }
}
