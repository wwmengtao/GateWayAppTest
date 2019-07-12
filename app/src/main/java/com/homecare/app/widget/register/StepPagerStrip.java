package com.homecare.app.widget.register;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import com.homecare.app.R;

public class StepPagerStrip extends View {
    private static final int[] ATTRS = new int[]{
            android.R.attr.gravity
    };
    private int mPageCount;

    private int mGravity = Gravity.START | Gravity.TOP;
    private float mTabWidth;
    private float mTabHeight;
    private float mTabSpacing;

    private RectF mTempRectF = new RectF();

    public StepPagerStrip(Context context) {
        this(context, null, 0);
    }

    public StepPagerStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepPagerStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mGravity = a.getInteger(0, mGravity);
        a.recycle();

        final Resources res = getResources();
        mTabWidth = res.getDimensionPixelSize(R.dimen.step_pager_tab_width);
        mTabHeight = res.getDimensionPixelSize(R.dimen.step_pager_tab_height);
        mTabSpacing = res.getDimensionPixelSize(R.dimen.step_pager_tab_spacing);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPageCount == 0) {
            return;
        }

        float totalWidth = mPageCount * (mTabWidth + mTabSpacing) - mTabSpacing;
        float totalLeft;
        boolean fillHorizontal = false;

        switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                totalLeft = (getWidth() - totalWidth) / 2;
                break;
            case Gravity.END:
                totalLeft = getWidth() - getPaddingRight() - totalWidth;
                break;
            case Gravity.FILL_HORIZONTAL:
                totalLeft = getPaddingLeft();
                fillHorizontal = true;
                break;
            default:
                totalLeft = getPaddingLeft();
        }

        switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.CENTER_VERTICAL:
                mTempRectF.top = (int) (getHeight() - mTabHeight) / 2;
                break;
            case Gravity.BOTTOM:
                mTempRectF.top = getHeight() - getPaddingBottom() - mTabHeight;
                break;
            default:
                mTempRectF.top = getPaddingTop();
        }

        mTempRectF.bottom = mTempRectF.top + mTabHeight;

        float tabWidth = mTabWidth;
        if (fillHorizontal) {
            tabWidth = (getWidth() - getPaddingRight() - getPaddingLeft()
                    - (mPageCount - 1) * mTabSpacing) / mPageCount;
        }

        for (int i = 0; i < mPageCount; i++) {
            mTempRectF.left = totalLeft + (i * (tabWidth + mTabSpacing));
            mTempRectF.right = mTempRectF.left + tabWidth;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                View.resolveSize(
                        (int) (mPageCount * (mTabWidth + mTabSpacing) - mTabSpacing)
                                + getPaddingLeft() + getPaddingRight(),
                        widthMeasureSpec),
                View.resolveSize(
                        (int) mTabHeight
                                + getPaddingTop() + getPaddingBottom(),
                        heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scrollCurrentPageIntoView();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setCurrentPage(int currentPage) {
        invalidate();
        scrollCurrentPageIntoView();

        // TODO: Set content description appropriately
    }

    private void scrollCurrentPageIntoView() {
        // TODO: only works with left gravity for now
//
//        float widthToActive = getPaddingLeft() + (mCurrentPage + 1) * (mTabWidth + mTabSpacing)
//                - mTabSpacing;
//        int viewWidth = getWidth();
//
//        int startScrollX = getScrollX();
//        int destScrollX = (widthToActive > viewWidth) ? (int) (widthToActive - viewWidth) : 0;
//
//        if (mScroller == null) {
//            mScroller = new Scroller(getContext());
//        }
//
//        mScroller.abortAnimation();
//        mScroller.startScroll(startScrollX, 0, destScrollX - startScrollX, 0);
//        postInvalidate();
    }

    public void setPageCount(int count) {
        mPageCount = count;
        invalidate();

        // TODO: Set content description appropriately
    }

}
