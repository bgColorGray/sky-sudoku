package com.example.suduku.window.map;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class ReboundScrollView extends ScrollView {

    private boolean mEnableTopRebound = true;
    private boolean mEnableBottomRebound = true;
    private OnReboundEndListener mOnReboundEndListener;
    private View mContentView;
    private Rect mRect = new Rect();

    public ReboundScrollView(Context context) {
        super(context);
    }

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReboundScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** after inflating view, we can get the width and height of view */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mContentView == null) return;
        // to remember the location of mContentView
        mRect.set(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());
    }

    public ReboundScrollView setOnReboundEndListener(OnReboundEndListener onReboundEndListener){
        this.mOnReboundEndListener = onReboundEndListener;
        return this;

    }

    public ReboundScrollView setEnableTopRebound(boolean enableTopRebound){
        this.mEnableTopRebound = enableTopRebound;
        return this;
    }

    public ReboundScrollView setEnableBottomRebound(boolean mEnableBottomRebound){
        this.mEnableBottomRebound = mEnableBottomRebound;
        return this;
    }

    private int lastY;
    private boolean rebound = false;
    private int reboundDirection = 0; //<0 ?????????????????? >0 ?????????????????? 0???????????????

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mContentView == null){
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isScrollToTop() && !isScrollToBottom()){
                    lastY = (int) ev.getY();
                    break;
                }
                //????????????????????????
                int deltaY = (int) (ev.getY() - lastY);
                //deltaY > 0 ?????? deltaY < 0 ??????


                //disable top or bottom rebound
                if ((!mEnableTopRebound && deltaY > 0) || (!mEnableBottomRebound && deltaY < 0)){
                    break;
                }

                int offset = (int) (deltaY * 0.48);
                mContentView.layout(mRect.left, mRect.top + offset, mRect.right, mRect.bottom + offset);
                rebound = true;
                break;

            case MotionEvent.ACTION_UP:
                if (!rebound) break;
                reboundDirection = mContentView.getTop() - mRect.top;
                TranslateAnimation animation = new TranslateAnimation(0, 0, mContentView.getTop(), mRect.top);
                animation.setDuration(300);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mOnReboundEndListener != null){
                            if (reboundDirection > 0){
                                mOnReboundEndListener.onReboundTopComplete();
                            }
                            if (reboundDirection < 0){
                                mOnReboundEndListener.onReboundBottomComplete();
                            }
                            reboundDirection = 0;
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mContentView.startAnimation(animation);
                mContentView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
                rebound = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setFillViewport(boolean fillViewport) {
        super.setFillViewport(true); //???????????????ScrollView ?????????XML?????????????????????fillViewport??????
    }

    /**
     * ????????????ScrollView??????????????????
     */
    private boolean isScrollToTop(){
        return getScrollY() == 0;
    }

    /**
     * ????????????ScrollView?????????????????????
     */
    private boolean isScrollToBottom(){
        return mContentView.getHeight() <= getHeight() + getScrollY();
    }

    /**
     * listener for top and bottom rebound
     * do your implement in the following methods
     */
    public interface OnReboundEndListener{

        void onReboundTopComplete();

        void onReboundBottomComplete();
    }
}