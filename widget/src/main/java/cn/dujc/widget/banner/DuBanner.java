package cn.dujc.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.dujc.widget.R;

/**
 * 基于recyclerView的banner
 * Created by jc199 on 2017/6/13.
 */
public class DuBanner extends FrameLayout {

    private static final int OFFSET_SCALE = 10000;//因为将RecyclerView设置了int.max为数据的长度，所以需要一个默认的偏移量倍数
    private static final int TIME_DEFAULT = 3500;//默认滚动时间
    private PagerSnapHelper mSnapHelper;
    private LinearLayoutManager mLayoutManager;

    public interface ImageLoader {
        void loadImage(View view, ImageView imageView, String url);
    }

    public interface OnBannerClickListener {
        void onBannerClicked(View view, int position);
    }

    private RecyclerView mRecyclerView;
    private BannerAdapter mBannerAdapter;
    private OnBannerItemClick mOnBannerItemClick;
    private DuBannerIndicator mIndicator;
    private int mCurrent, mActual;//当前position和实际position
    private int mTimeInterval = TIME_DEFAULT;
    private int mIndicatorMarginLayout = 10;
    private float mHeightScale = 0f;//9/16
    private boolean mAutoScroll = true;

    private static Handler sHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRecyclerView != null) {
                if (isShown()) {
                    mRecyclerView.smoothScrollToPosition(++mActual);
                }
                onStop();
                onStart();
            }
        }
    };

    public DuBanner(@NonNull Context context) {
        this(context, null, 0);
    }

    public DuBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DuBanner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightScale != 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            float height = width * mHeightScale;
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        int indicatorMarginBetween = 0, indicatorEdge = 10;
        Drawable drawableDefault = null, drawableSelected = null;
        int colorDefault = 0xff2222, colorSelected = 0xcccccc;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DuBanner);
            mTimeInterval = array.getInt(R.styleable.DuBanner_widget_time_interval, mTimeInterval);
            drawableDefault = array.getDrawable(R.styleable.DuBanner_widget_drawable_default);
            drawableSelected = array.getDrawable(R.styleable.DuBanner_widget_drawable_selected);
            if (drawableDefault == null && drawableSelected == null) {
                colorDefault = array.getColor(R.styleable.DuBanner_widget_color_default, colorDefault);
                colorSelected = array.getColor(R.styleable.DuBanner_widget_color_selected, colorSelected);
            }
            mAutoScroll = array.getBoolean(R.styleable.DuBanner_widget_auto_scroll, mAutoScroll);
            indicatorMarginBetween = array.getDimensionPixelOffset(R.styleable.DuBanner_widget_indicator_margin_between, indicatorMarginBetween);
            mIndicatorMarginLayout = array.getDimensionPixelOffset(R.styleable.DuBanner_widget_indicator_margin_layout, mIndicatorMarginLayout);
            indicatorEdge = array.getDimensionPixelOffset(R.styleable.DuBanner_widget_indicator_edge, indicatorEdge);
            mHeightScale = array.getFloat(R.styleable.DuBanner_widget_height_scale, mHeightScale);
            array.recycle();
        }
        if (indicatorMarginBetween == 0) indicatorMarginBetween = indicatorEdge;

        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setFocusableInTouchMode(false);
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mIndicator = new DefaultIndicator(context, drawableDefault, drawableSelected, colorDefault, colorSelected, indicatorMarginBetween, mIndicatorMarginLayout, indicatorEdge);
        LayoutParams indicatorParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        indicatorParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        indicatorParams.bottomMargin = mIndicatorMarginLayout;
        addView(mIndicator.getView(), indicatorParams);

        mBannerAdapter = new BannerAdapter();

        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mBannerAdapter);

        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = mSnapHelper.findSnapView(mLayoutManager);
                    int position = mRecyclerView.getChildAdapterPosition(snapView);
                    if (position != RecyclerView.NO_POSITION) {
                        mActual = position;
                    }
                    if (mActual <= 10 || mActual > Integer.MAX_VALUE - 10) {
                        mActual = calcOffset() + mActual;
                    }
                    mRecyclerView.scrollToPosition(mActual);
                    mCurrent = mBannerAdapter.getRealPosition(mActual);

                    refreshIndicator();
                    onStart();
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    onStop();
                }
                //Log.d("-----------", "------------ actual = " + mActual + ", current = " + mCurrent);
            }
        });

        onStart();
    }

    private LayoutParams getIndicatorParams() {
        LayoutParams indicatorParams = (LayoutParams) mIndicator.getView().getLayoutParams();
        if (indicatorParams == null) {
            indicatorParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            indicatorParams.bottomMargin = mIndicatorMarginLayout;
        }
        return indicatorParams;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        //当指示器不在本控件内时，则设置本控件的显示和隐藏的同时，指示器也同时设置
        if (mIndicator != null && mIndicator.getView().getParent() != this) {
            mIndicator.getView().setVisibility(visibility);
        }
    }

    /**
     * 替换指示器布局；用于显示一个在banner外部的指示器
     *
     * @param indicator DuBannerIndicator
     */
    public void replaceIndicatorLayout(DuBannerIndicator indicator) {
        if (mIndicator != null && mIndicator.getView().getParent() == this) {
            removeView(mIndicator.getView());
        }
        mIndicator = indicator;
        refreshIndicator();
    }

    /**
     * 设置指示器布局对应的位置
     *
     * @param gravity
     */
    public void setIndicatorLayoutGravity(int gravity) {
        if (mIndicator != null) {
            LayoutParams indicatorParams = getIndicatorParams();
            indicatorParams.gravity = gravity;
            mIndicator.getView().setLayoutParams(indicatorParams);
        }
    }

    /**
     * 设置指示器布局对外的间距
     *
     * @param margin
     */
    public void setIndicatorLayoutMargin(int margin) {
        setIndicatorLayoutMargin(margin, margin, margin, margin);
    }

    /**
     * 设置指示器布局对外的间距
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setIndicatorLayoutMargin(int left, int top, int right, int bottom) {
        if (mIndicator != null) {
            LayoutParams indicatorParams = getIndicatorParams();
            if (left >= 0) indicatorParams.leftMargin = left;
            if (top >= 0) indicatorParams.topMargin = top;
            if (right >= 0) indicatorParams.rightMargin = right;
            if (bottom >= 0) indicatorParams.bottomMargin = bottom;
            mIndicator.getView().setLayoutParams(indicatorParams);
        }
    }

    public void setImageLoader(ImageLoader imageLoader) {
        if (mBannerAdapter != null) {
            mBannerAdapter.mImageLoader = imageLoader;
        }
    }

    public void setData(List list) {
        mBannerAdapter.mList.clear();
        if (list != null && list.size() > 0) {
            mBannerAdapter.mList.addAll(list);
            onStart();
        } else {
            onStop();
        }
        mActual = calcOffset();
        mCurrent = mBannerAdapter.getRealPosition(mActual);
        mBannerAdapter.notifyDataSetChanged();
        refreshIndicator();
        mRecyclerView.scrollToPosition(mActual);
    }

    public void setOnBannerClickListener(OnBannerClickListener bannerClickListener) {
        if (mOnBannerItemClick != null) {
            mRecyclerView.removeOnItemTouchListener(mOnBannerItemClick);
        }
        mOnBannerItemClick = new OnBannerItemClick(mRecyclerView, mBannerAdapter, bannerClickListener);
        mRecyclerView.addOnItemTouchListener(mOnBannerItemClick);
    }

    /**
     * 开始滚动，不考虑其他因素，一旦调用此方法，无论如何都会在handler里面开始计算时间，同时mAutoScroll会设置成true
     */
    public void start() {
        mAutoScroll = true;
        sHandler.postDelayed(mRunnable, mTimeInterval);
    }

    /**
     * 停止滚动，不考虑其他因素，一旦调用此方法，将直接移除handler内的callback
     */
    public void stop() {
        sHandler.removeCallbacks(mRunnable);
    }

    /**
     * 开始滚动，此方法可以与生命周期配合使用，调用此方法会先判断是否开启了自动滚动，或者是否正在滚动，并且可滚动的数量是否大于1才会执行滚动
     */
    public void onStart() {
        if (mAutoScroll && mBannerAdapter.mList.size() > 1) {
            start();
        }
    }

    /**
     * 停止滚动，此方法可以与生命周期配合使用，调用此方法会先判断是否在滚动中（mAutoScroll等同于滚动状态）才会执行
     */
    public void onStop() {
        if (mAutoScroll) {
            stop();
        }
    }

    private int calcOffset() {
        return mBannerAdapter.mList.size() * OFFSET_SCALE;
    }

    private void refreshIndicator() {
        if (mIndicator != null) {
            final int size = mBannerAdapter.mList.size();
            mIndicator.updateIndex(mCurrent, size);
        }
    }

    private static class BannerHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public BannerHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView;
        }
    }

    private static class BannerAdapter extends RecyclerView.Adapter<BannerHolder> {

        final List mList = new ArrayList();
        private ImageLoader mImageLoader = new DuBannerDefaultLoader();

        @Override
        public BannerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ImageView imageView = new ImageView(parent.getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return new BannerHolder(imageView);
        }

        @Override
        public void onBindViewHolder(BannerHolder holder, int position) {
            if (mImageLoader != null) {
                mImageLoader.loadImage(holder.itemView, holder.mImageView, "https://imagestest.shangwenwan.com/mall/3425cf58-6dfc-4e07-8a2d-e4902ec5c52a?imageMogr2/size-limit/136.2k!");
            }
        }

        private int getRealPosition(int position) {
            int realItemCount = getRealItemCount();
            return realItemCount == 0 ? position : position % realItemCount;
        }

        private int getRealItemCount() {
            return mList.size();
        }

        @Override
        public int getItemCount() {
            final int realItemCount = getRealItemCount();
            return realItemCount > 1 ? Integer.MAX_VALUE : realItemCount;
        }
    }

    private static class OnBannerItemClick extends RecyclerView.SimpleOnItemTouchListener {
        private GestureDetectorCompat mDetectorCompat;

        public OnBannerItemClick(final RecyclerView recyclerView, final BannerAdapter adapter, final OnBannerClickListener clickListener) {
            mDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (clickListener != null) {
                        View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (itemView != null) {
                            clickListener.onBannerClicked(itemView, adapter.getRealPosition(recyclerView.getChildAdapterPosition(itemView)));
                            return true;
                        }
                    }
                    return false;
                }
            });
            mDetectorCompat.setIsLongpressEnabled(false);
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return mDetectorCompat.onTouchEvent(e);
        }
    }


}
