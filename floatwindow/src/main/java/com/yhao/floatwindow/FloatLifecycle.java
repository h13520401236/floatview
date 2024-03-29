package com.yhao.floatwindow;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;


class FloatLifecycle extends BroadcastReceiver implements Application.ActivityLifecycleCallbacks {

    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final long delay = 300;
    private Handler mHandler;
    private Class[] activities;
    private boolean showFlag;
    private int startCount;
    private int resumeCount;
    private boolean appBackground;
    private LifecycleListener mLifecycleListener;
    private static ResumedListener sResumedListener;
    private static int num = 0;


    FloatLifecycle(Context applicationContext, boolean showFlag, Class[] activities, LifecycleListener lifecycleListener) {
        this.showFlag = showFlag;
        this.activities = activities;
        startCount = ActivityStack.getInstance().getSize() - 1;
        resumeCount = ActivityStack.getInstance().getSize() - 1;
//        if (startCount < 0) {
//            startCount = 0;
//        }
//        if (resumeCount < 0) {
//            resumeCount = 0;
//        }
        LogUtil.e("初始化第一次 resumeCount="+resumeCount+"   "+"  startCount="+startCount);
        num++;
        mLifecycleListener = lifecycleListener;
        mHandler = new Handler();
        ((Application) applicationContext).registerActivityLifecycleCallbacks(this);
        applicationContext.registerReceiver(this, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public static void setResumedListener(ResumedListener resumedListener) {
        sResumedListener = resumedListener;
    }

    private boolean needShow(Activity activity) {
        if (activities == null) {
            return true;
        }
        for (Class a : activities) {
            if (a.isInstance(activity)) {
                return showFlag;
            }
        }
        return !showFlag;
    }


    @Override
    public void onActivityResumed(Activity activity) {
        if (sResumedListener != null) {
            num--;
            if (num == 0) {
                sResumedListener.onResumed();
                sResumedListener = null;
            }
        }
        resumeCount++;
        LogUtil.e("onActivityResumed resumeCount="+resumeCount+"   "+activity);
        if (needShow(activity)) {
//            mLifecycleListener.onShow();
        } else {
//            mLifecycleListener.onHide();
        }
        if (appBackground) {
            appBackground = false;
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        resumeCount--;
        LogUtil.e("onActivityPaused resumeCount="+resumeCount+"   "+activity);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (resumeCount == 0) {
                    appBackground = true;
//                    mLifecycleListener.onBackToDesktop();
                }
            }
        }, delay);

    }

    @Override
    public void onActivityStarted(Activity activity) {
        startCount++;
        LogUtil.e("onActivityStarted startCount="+startCount+"   "+activity);
    }


    @Override
    public void onActivityStopped(Activity activity) {
        startCount--;
        LogUtil.e("onActivityStopped startCount="+startCount+"   "+activity);
        if (startCount == 0) {
//            mLifecycleListener.onBackToDesktop();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
//                mLifecycleListener.onBackToDesktop();
            }
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }


    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


}
