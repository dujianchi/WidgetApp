package cn.dujc.widget.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DefaultIndicator extends LinearLayout implements DuBannerIndicator {

    private final SparseArray<CircleView> mIndicators = new SparseArray<>();
    private Drawable mDrawableDefault, mDrawableSelected;
    private int mColorDefault, mColorSelected;
    private int mIndicatorMarginBetween = 0, mIndicatorMarginLayout = 10, mIndicatorEdge = 10;

    public DefaultIndicator(Context context
            , Drawable drawableDefault
            , Drawable drawableSelected
            , int colorDefault
            , int colorSelected
            , int indicatorMarginBetween
            , int indicatorMarginLayout
            , int indicatorEdge) {
        super(context);
        mDrawableDefault = drawableDefault;
        mDrawableSelected = drawableSelected;
        mColorDefault = colorDefault;
        mColorSelected = colorSelected;
        mIndicatorMarginBetween = indicatorMarginBetween;
        mIndicatorMarginLayout = indicatorMarginLayout;
        mIndicatorEdge = indicatorEdge;
    }

    @Override
    protected void onDetachedFromWindow() {
        mIndicators.clear();
        super.onDetachedFromWindow();
    }

    @NonNull
    @Override
    public ViewGroup getView() {
        return this;
    }

    @Override
    public void updateIndex(int current, int count) {
        if (count <= 1) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);

            LayoutParams params = new LayoutParams(mIndicatorEdge, mIndicatorEdge);
            params.leftMargin = params.rightMargin = mIndicatorMarginBetween / 2;
            removeAllViews();
            for (int index = 0; index < count; index++) {
                CircleView indicator = mIndicators.get(index);
                if (indicator == null) {
                    indicator = new CircleView(getContext(), mIndicatorEdge);
                    mIndicators.put(index, indicator);
                }
                final boolean isSelected = index == current;
                if (mDrawableSelected == null && mDrawableDefault == null) {
                    indicator.setColor(isSelected ? mColorSelected : mColorDefault);
                } else {
                    ViewCompat.setBackground(indicator, isSelected ? mDrawableSelected : mDrawableDefault);
                }
                addView(indicator, params);
            }
        }
    }

    private static class CircleView extends View {

        final int mEdge;
        final Paint mPaint;
        int mColor = 0x00000000;

        public CircleView(Context context, int edge) {
            super(context);
            mEdge = edge;
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }

        public void setColor(int color) {
            mColor = color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = mEdge / 2;
            mPaint.setColor(mColor);
            canvas.drawCircle(radius, radius, radius, mPaint);
            super.onDraw(canvas);
        }
    }
}
