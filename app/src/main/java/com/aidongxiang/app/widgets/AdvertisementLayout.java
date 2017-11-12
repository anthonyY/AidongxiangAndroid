package com.aidongxiang.app.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.aidongxiang.app.R;
import com.aidongxiang.app.adapter.BannerAdapter;
import com.aidongxiang.app.model.Ad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AdvertisementLayout extends LinearLayout {

    private ViewPager viewPager;
    private LinearLayout group;
    private ImageView[] images;
    //	private int currentItem = 0;
    private Context context;
    private ScheduledExecutorService scheduledExecutorService;
    private int TIME_UNIT;
    int pageIndex = 1;
    private List<Ad> list;
    private float RATIO = 2f / 4;
    private int dp6;
    private int dp9;

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initView(View view) {
        dp6 = dip2px(view.getContext(), 6);
        dp9 = dip2px(view.getContext(), 9);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        group = (LinearLayout) view.findViewById(R.id.viewGroup);
    }

    public AdvertisementLayout(Context context) {
        super(context);
        this.context = context;

    }

    public AdvertisementLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.ad);
        RATIO = params.getFloat(R.styleable.ad_ratio, 0.5f);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View adview = mInflater.inflate(R.layout.layout_ad, null);
        initView(adview);
        setCircleGravity(Gravity.CENTER);
        addView(adview);
        params.recycle();
    }


    public void setCircleGravity(int gravity) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) group.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        group.setLayoutParams(layoutParams);
        group.setGravity(gravity);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        //高度和宽度一样
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize * RATIO), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("NewApi")
    public AdvertisementLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    //实例化，动态创建图片下方的小点
    private void initListNavigation(int num) {
//		int num = im.length;
        images = new ImageView[num];
        for (int i = 0; i < num; i++) {
            images[i] = new ImageView(context);

            LayoutParams params = new LayoutParams(10, 10);
            if (i == 0) {
                params.width = dp9;
                params.height = dp9;
            } else {
                params.width = dp6 ;
                params.height = dp6;
            }
            params.leftMargin = 6;
            params.rightMargin = 6;
            images[i].setLayoutParams(params);
            //暂时不要原点
            images[i].setBackgroundColor(Color.TRANSPARENT);

            group.addView(images[i]);
        }
    }

    //滑动监听
    private class PagerListener implements OnPageChangeListener {
        //滑动中
        @Override
        public void onPageScrollStateChanged(int arg0) {
            //0--->空闲，1--->是滑行中，2--->加载完毕
            switch (arg0) {
                case 0:
                case 1:
                    //当当前的页是第一张的时候就切成显示最后1张。
                    if (pageIndex == 0) {
                        pageIndex = list.size();
                        viewPager.setCurrentItem(pageIndex, false);// 取消动画
                        //当当前的页是最后一张的时候就切成显示的第一张。
                    } else if (pageIndex == list.size() + 1) {
                        pageIndex = 1;
                        viewPager.setCurrentItem(pageIndex, false);// 取消动画
                    } else {
                        viewPager.setCurrentItem(pageIndex);// 动画
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new ViewPagerScrollTask(), TIME_UNIT, TIME_UNIT, TimeUnit.SECONDS);
            pageIndex = position;
            //当当前的页是第一张的时候就切成显示最后1张。
            if (position == 0) {
                for (int i = 0; i < images.length; i++) {
                    LayoutParams params = (LayoutParams) images[i].getLayoutParams();
                    if (i == list.size() - 1) {
                        params.width = dp9;
                        params.height = dp9;
                    } else {
                        params.width = dp6;
                        params.height = dp6;
                    }
                    images[i].setLayoutParams(params);
                }
                //当当前的页是最后一张的时候就切成显示的第一张。
            } else if (position == list.size() + 1) {
                for (int i = 0; i < images.length; i++) {
                    LayoutParams params = (LayoutParams) images[i].getLayoutParams();
                    if (i == 0) {
                        params.width = dp9;
                        params.height = dp9;
                    } else {
                        params.width = dp6 ;
                        params.height = dp6;
                    }
                    images[i].setLayoutParams(params);
                }
            } else {
                for (int i = 0; i < images.length; i++) {
                    LayoutParams params = (LayoutParams) images[i].getLayoutParams();
                    if (i == position - 1) {
                        params.width = dp9;
                        params.height = dp9;
                    } else {
                        params.width = dp6 ;
                        params.height = dp6;
                    }
                    images[i].setLayoutParams(params);
                }
            }

        }
    }


    private class ViewPagerScrollTask implements Runnable {

        public void run() {
            synchronized (viewPager) {
//				currentItem = (currentItem + 1) % images.length;
                pageIndex++;
                if (pageIndex >= list.size() + 1) pageIndex = 1;
                handler.obtainMessage().sendToTarget(); // 通
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(pageIndex);//
//			setCurrentItem() ;
        }

        ;
    };

    /**
     * 广告调用接口
     *
     * @param num       集合的size
     * @param time_unit 自动滑动的时间（秒）
     * @param is_open   是否自动滑动
     * @param list      图片集合
     * @param ratio     这个是广告的宽高的比例(传-1就是默认0.5比例)
     */
    public void startAD(int num, int time_unit, boolean is_open, ArrayList<Ad> list, float ratio) {
        if (ratio != -1) {
            RATIO = ratio;
        }
        this.list = list;
        group.removeAllViews();
        if (is_open) {
            if (time_unit <= 0) {
                TIME_UNIT = 1;
            } else {
                TIME_UNIT = time_unit;
            }
        }
        initListNavigation(num);

        if (num <= 0) {
            findViewById(R.id.linear_ad).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linear_ad).setVisibility(View.VISIBLE);
//            viewPager.setBackgroundResource(R.color.gray_line);
            BannerAdapter bannerAdapter = new BannerAdapter(context, getList(list));
            if(onItemClickListener != null){
                bannerAdapter.setOnItemClickListener(onItemClickListener);
            }
            viewPager.setAdapter(bannerAdapter);
            viewPager.addOnPageChangeListener(new PagerListener());
            viewPager.setCurrentItem(pageIndex);
            if (is_open) {
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdown();
                }
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutorService.scheduleAtFixedRate(new ViewPagerScrollTask(), TIME_UNIT, TIME_UNIT, TimeUnit.SECONDS);

            } else {
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdown();
                }
            }
        }
    }

    /**
     * 广告调用接口
     *
     * @param num       集合的size
     * @param time_unit 自动滑动的时间（秒）
     * @param is_open   是否自动滑动
     * @param list      图片集合
     */
    public void startAD(int num, int time_unit, boolean is_open, ArrayList<Ad> list) {
        startAD(num, time_unit, is_open, list, RATIO);
    }

    //最前面加一张最后一张，最后面再加一张第一张。
    private ArrayList<Ad> getList(ArrayList<Ad> list) {
        ArrayList<Ad> ll = new ArrayList<>();
        if (list.size() > 1) {
            group.setVisibility(View.VISIBLE);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    ll.add(list.get(list.size() - 1));
                }
                ll.add(list.get(i));
                if (i == list.size() - 1) {
                    ll.add(list.get(0));
                }
            }
        } else {
            ll.addAll(list);
            group.setVisibility(View.GONE);
        }
        return ll;
    }

    private BannerAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(BannerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
