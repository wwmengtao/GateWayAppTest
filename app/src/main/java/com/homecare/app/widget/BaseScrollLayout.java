package com.homecare.app.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.*;
import android.view.*;
import android.widget.Scroller;
import com.homecare.app.OnViewChangeListener;

public class BaseScrollLayout extends ViewGroup {

    private VelocityTracker mVelocityTracker;            // 用于判断甩动手势
    private static final int SNAP_VELOCITY = 400;
    private Scroller mScroller;                        // 滑动控制
    private int mCurScreen;
    private float mLastMotionX;
    private float mLastMotionY;

    private boolean isPass = false;
    //   private int mTouchSlop;

    //    private static final int TOUCH_STATE_REST = 0;
    //    private static final int TOUCH_STATE_SCROLLING = 1;
    //    private int mTouchState = TOUCH_STATE_REST;

    private OnViewChangeListener mOnViewChangeListener;

    public BaseScrollLayout(Context context) {
        super(context);
        init(context);
    }

    public BaseScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mCurScreen = 0;
        //   mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller = new Scroller(context);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childLeft = 0;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE) {
                    final int childWidth = childView.getMeasuredWidth();
                    childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                    childLeft += childWidth;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
//        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurScreen * width, 0);
    }

    public void snapToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0, 300);

            mCurScreen = whichScreen;
            invalidate();       // Redraw the layout
            if (mOnViewChangeListener != null) {
                mOnViewChangeListener.OnViewChange(mCurScreen);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(event);
                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotionX - x);
                if (IsCanMove(deltaX)) {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                    }
                    mLastMotionX = x;
                    scrollBy(deltaX, 0);
                }

                break;
            case MotionEvent.ACTION_UP:
                int velocityX = 0;
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    velocityX = (int) mVelocityTracker.getXVelocity();
                }
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                    // Fling enough to move left
                    snapToScreen(mCurScreen - 1);
                }
                else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
                    // Fling enough to move right
                    snapToScreen(mCurScreen + 1);
                }
                else {
                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                //      mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPass) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPass) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                double tanNum = Math.atan(Math.abs(event.getY() - mLastMotionY) / Math.abs(event.getX() - mLastMotionX));
                double retote = tanNum / 3.14 * 180;
                isPass = retote < 45;
                onInterceptTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean IsCanMove(int deltaX) {
        if (getScrollX() <= 0 && deltaX < 0) {
            return false;
        }
        if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
            return false;
        }
        return true;
    }

    public void SetOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }

}