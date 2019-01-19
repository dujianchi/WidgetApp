package cn.dujc.widget.tablayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.dujc.widget.listener.OnDuItemClickListener;

public class TabLayout<T> extends FrameLayout {

    private final List<T> mData = new ArrayList<>();
    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updatePosition(position);
        }
    };

    private ITabFactory<T> mTabFactory = new DefaultTabFactoryImpl<T>();
    private RecyclerView mRecyclerView;
    private TabAdapter<T> mRecyclerAdapter;
    private ViewPager mViewPager;

    private int mPosition = 0;

    public TabLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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

    private void init(Context context) {
        mRecyclerView = new RecyclerView(context);
        mRecyclerAdapter = new TabAdapter<T>(mData, mTabFactory);
        mRecyclerView.setLayoutManager(new LayoutManager(context));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        addView(mRecyclerView);

        mRecyclerAdapter.setOnDuItemClickListener(new OnDuItemClickListener() {
            @Override
            public void onDuItemClickListener(View itemView, int position) {
                updatePosition(position);
            }
        });
    }

    public ITabFactory<T> getTabFactory() {
        return mTabFactory;
    }

    public void setTabFactory(@NonNull ITabFactory<T> tabFactory) {
        mTabFactory = tabFactory;
        mRecyclerAdapter.setTabFactory(tabFactory);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * 更新选中状态
     */
    private void updateItemState(int position) {
        if (position >= 0 && position < mRecyclerAdapter.getItemCount()) {
            mRecyclerAdapter.updateCurrentPosition(position);
        }
    }

    /**
     * 更新选中位置，连带viewpager一起
     */
    private void updatePosition(int position) {
        if (mPosition != position) {
            mPosition = position;
            updateItemState(mPosition);

            if (mViewPager != null && mViewPager.getAdapter() != null && mViewPager.getCurrentItem() != mPosition && mPosition >= 0 && mPosition < mViewPager.getAdapter().getCount()) {
                mViewPager.setCurrentItem(mPosition);
            }
        }
    }

    public TabAdapter getRecyclerAdapter() {
        return mRecyclerAdapter;
    }

    public void setData(List<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        mRecyclerAdapter.notifyDataSetChanged();
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

    private static class LayoutManager extends LinearLayoutManager {

        LayoutManager(Context context) {
            super(context, HORIZONTAL, false);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            final int count = getItemCount();
            //第一个和最后一个都完整地呈现，说明数据可能无法填满屏幕。所以均分每一项的宽度
            if (findFirstCompletelyVisibleItemPosition() == 0 && findLastCompletelyVisibleItemPosition() == count - 1) {
                final int width = MeasureSpec.getSize(widthSpec) / count;
                for (int index = 0; index < count; index++) {
                    final View child = findViewByPosition(index);
                    if (child != null) {
                        final ViewGroup.LayoutParams params = child.getLayoutParams();
                        params.width = width;
                        child.setLayoutParams(params);
                    }
                }
            }
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }

}
