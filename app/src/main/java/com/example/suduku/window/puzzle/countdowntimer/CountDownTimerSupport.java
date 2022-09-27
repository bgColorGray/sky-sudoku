package com.example.suduku.window.puzzle.countdowntimer;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;


public class CountDownTimerSupport implements ITimerSupport {
    private Timer mTimer;

    private Handler mHandler;

    static private CountDownTimerSupport instance;

    /**
     * 倒计时时间
     */
    private long mMillisInFuture;

    /**
     * 间隔时间
     */
    private long mCountDownInterval;
    /**
     * 倒计时剩余时间
     */
    private long mMillisUntilFinished;

    private OnCountDownTimerListener mOnCountDownTimerListener;

    private TimerState mTimerState = TimerState.FINISH;

    @Deprecated
    public CountDownTimerSupport() {
        this.mHandler = new Handler();
    }

    public CountDownTimerSupport(long millisInFuture, long countDownInterval) {
        this.setMillisInFuture(millisInFuture);
        this.setCountDownInterval(countDownInterval);
        this.mHandler = new Handler();
    }


    static CountDownTimerSupport getInstance() {    //单例模式
        if (instance == null) {
            synchronized (CountDownTimerSupport.class) {
                if (instance == null) {
                    instance = new CountDownTimerSupport();
                }
            }
        }
        return instance;
    }

    @Override
    public void start() {   //开始计时
        //防止重复启动 重新启动要先reset再start
        if (mTimer == null && mTimerState != TimerState.START) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(createTimerTask(), 0, mCountDownInterval);
            mTimerState = TimerState.START;
        }
    }

    @Override
    public void pause() {   //暂停计时
        if (mTimer != null && mTimerState == TimerState.START) {
            cancelTimer();
            mTimerState = TimerState.PAUSE;
        }
    }

    @Override
    public void resume() {  //继续计时
        if (mTimerState == TimerState.PAUSE) {
            start();
        }
    }

    @Override
    public void stop() {
        stopTimer(true);
    }   //停止计时

    @Override
    public void reset() {   //重置计时
        if (mTimer != null) {
            cancelTimer();
        }
        mMillisUntilFinished = mMillisInFuture;
        mTimerState = TimerState.FINISH;
    }

    private void stopTimer(final boolean cancel) {  //停止计时
        if (mTimer != null) {
            cancelTimer();
            mMillisUntilFinished = mMillisInFuture;
            mTimerState = TimerState.FINISH;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnCountDownTimerListener != null) {
                        if (cancel) {
                            mOnCountDownTimerListener.onCancel();
                        } else {
                            //判断activity是否在前台
                            mOnCountDownTimerListener.onFinish();
                        }
                    }
                }
            });
        }
    }

    private void cancelTimer() {    //取消计时
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    public boolean isStart() {
        return mTimerState == TimerState.START;
    }

    public boolean isFinish() {
        return mTimerState == TimerState.FINISH;
    }

    /**
     * @param millisInFuture
     * @deprecated 使用构造方法
     */
    @Deprecated
    public void setMillisInFuture(long millisInFuture) {
        this.mMillisInFuture = millisInFuture;
        this.mMillisUntilFinished = mMillisInFuture;
    }

    /**
     * @param countDownInterval
     * @deprecated 使用构造方法
     */
    @Deprecated
    public void setCountDownInterval(long countDownInterval) {
        this.mCountDownInterval = countDownInterval;
    }

    public void setOnCountDownTimerListener(OnCountDownTimerListener listener) {
        this.mOnCountDownTimerListener = listener;
    }

    public long getMillisUntilFinished() {
        return mMillisUntilFinished;
    }

    public TimerState getTimerState() {
        return mTimerState;
    }

    protected TimerTask createTimerTask() {//创建计时任务
        return new TimerTask() {
            private long startTime = -1;

            @Override
            public void run() {
                if (startTime < 0) {
                    //第一次回调 记录开始时间

                    startTime = scheduledExecutionTime() - (mMillisInFuture - mMillisUntilFinished);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnCountDownTimerListener != null) {
                                mOnCountDownTimerListener.onTick(mMillisUntilFinished);
                            }
                        }
                    });
                } else {
                    //剩余时间
                    mMillisUntilFinished = mMillisInFuture - (scheduledExecutionTime() - startTime);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnCountDownTimerListener != null) {
                                mOnCountDownTimerListener.onTick(mMillisUntilFinished);
                            }
                        }
                    });
                    if (mMillisUntilFinished <= 0) {
                        //如果没有剩余时间 就停止
                        stopTimer(false);
                    }
                }
            }
        };
    }

}