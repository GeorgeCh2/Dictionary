package com.example.george.dictionary;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    //底部两个按钮
    private Button translation;
    private Button dictionary;

    //中间内容区域
    private ViewPager viewPager;

    //页面集合
    private List<Fragment> fragments;

    //两个Fragment
    private TranPage tranPage;
    private DicPage dicPage;

    //选项卡总数
    private static final int TAB_COUNT = 2;

    //当前显示的选项卡位置
    private int current_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
        //初始化控件
        initView();

        //初始化底部按钮事件
        initEvent();

    }

    private void initEvent() {
        //为按钮注册监听事件
        translation.setOnClickListener(this);
        dictionary.setOnClickListener(this);
    }

    private void initView() {
        //底部按钮
        this.translation = (Button) findViewById(R.id.translation);
        this.dictionary = (Button) findViewById(R.id.dictionary);

        //中间内容区域
        this.viewPager = (ViewPager) findViewById(R.id.content);

        fragments = new ArrayList<Fragment>();
        tranPage = new TranPage();
        dicPage = new DicPage();

        fragments.add(tranPage);
        fragments.add(dicPage);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.translation:
                translation.setBackgroundColor(0xffD9D9D9);
                dictionary.setBackgroundColor(0xFFFFFFFF);
                viewPager.setCurrentItem(0);
                break;

            case R.id.dictionary:
                dictionary.setBackgroundColor(0xffD9D9D9);
                translation.setBackgroundColor(0xFFFFFFFF);
                viewPager.setCurrentItem(1);
                break;

            default:
                break;
        }
    }
}
