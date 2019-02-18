package cn.dujc.widget.tablayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class TabLayout<T> extends HorizontalScrollView {

    private final List<T> mData = new ArrayList<>();
    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updatePosition(position, false);
        }
    };
    private final OnLayoutChangeListener mLayoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            Log.d("-------", "TabLayout onLayoutChange");
            refreshChildWidth();
        }
    };

    private final TabFactory<T> mTabFactory = new TabFactory<T>();
    private final LinearLayout mInnerLayout;
    private ViewPager mViewPager;
    private ITabWidthCalculator mTabWidthCalculator = new ITabWidthCalculator.FixedImpl();

    public TabLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInnerLayout = new LinearLayout(context);

        setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);

        addView(mInnerLayout
                , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.MATCH_PARENT));

        removeOnLayoutChangeListener(mLayoutChangeListener);
        addOnLayoutChangeListener(mLayoutChangeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
            mViewPager.addOnPageChangeListener(mPageChangeListener);
        }
    }

    @NonNull
    public TabFactory<T> getTabFactory() {
        return mTabFactory;
    }

    public void setInstaller(ITab<T> installer) {
        if (installer != null) mTabFactory.setInstaller(installer);
    }

    public ITabWidthCalculator getTabWidthCalculator() {
        return mTabWidthCalculator;
    }

    public void setTabWidthCalculator(ITabWidthCalculator tabWidthCalculator) {
        if (tabWidthCalculator != null) mTabWidthCalculator = tabWidthCalculator;
    }

    /**
     * 更新选中状态
     */
    private void updateItemState(int position) {
        if (position >= 0 && position < mData.size()) {
            mTabFactory.update(position, mTabFactory.getCurrent() == position);
        }
    }

    /**
     * 更新选中位置，连带viewpager一起
     */
    private void updatePosition(int position, boolean viewPagerToo) {
        updateItemState(position);

        if (viewPagerToo && mViewPager != null
                && mViewPager.getAdapter() != null
                && mViewPager.getCurrentItem() != position
                && position >= 0
                && position < mViewPager.getAdapter().getCount()) {
            mViewPager.setCurrentItem(position);
        }
    }

    public void setData(List<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        refreshChildView();
        updatePosition(0, true);
    }

    public final void refreshChildView() {
        final int height = getMeasuredHeight();
        mInnerLayout.removeAllViews();
        for (int index = 0, size = mData.size(); index < size; index++) {
            ITab<T> tab = mTabFactory.get(index);
            View view = tab.getView(mInnerLayout);
            mTabFactory.setTabClick(view, index);
            tab.onTabUpdate(index, mData.get(index));
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , height == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : height));
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            mInnerLayout.addView(view);
        }
    }

    public final void refreshChildWidth() {
        final int width = getWidth();
        int original = mInnerLayout.getMeasuredWidth();
        final int size = mData.size();
        if (original < width) {
            for (int index = 0; index < size; index++) {
                View child = mInnerLayout.getChildAt(index);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = mTabWidthCalculator
                        .calc(original, width, child.getMeasuredWidth(), size);
                child.setLayoutParams(params);
            }
        }
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(mPageChangeListener);
            viewPager.addOnPageChangeListener(mPageChangeListener);
        }
    }

    public void setDataAndViewPage(List<T> data, ViewPager viewPager) {
        setData(data);
        setViewPager(viewPager);
    }

}
