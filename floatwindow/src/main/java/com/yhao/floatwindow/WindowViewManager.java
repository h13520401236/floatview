package com.yhao.floatwindow;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by heng on 2019-05-28
 */
public class WindowViewManager implements ViewStateListener, PermissionListener {
    private String TAG = "WindowViewManager";
    private static WindowViewManager mWindowViewManager;
    public static boolean isShow = false;

    private Context appContext;

    private ViewGroup mParent;

    private ScaleCircleImageView myImageView;

    private boolean isfrist = true;


    public static WindowViewManager getInstance(ViewGroup parent, Context application) {
        if (mWindowViewManager == null) {
            synchronized (WindowViewManager.class) {
                if (mWindowViewManager == null) {
                    mWindowViewManager = new WindowViewManager(parent, application);
                }
            }
        }
        return mWindowViewManager;
    }


    private WindowViewManager(ViewGroup parent, Context application) {
        this.appContext = application;
        this.mParent = parent;
        myImageView = new ScaleCircleImageView(appContext);

        Log.i(TAG, "WindowViewManager: " + mParent.getHeight());
        Log.i(TAG, "WindowViewManager: " + mParent.getWidth());


    }

    public static WindowViewManager windowViewManager() {
        return mWindowViewManager;
    }

    public void remove() {
        FloatWindow.destroy("old");
        FloatWindow.destroy("cancel2");
        FloatWindow.destroy("cancel");
        isShow = false;
    }


    public void updateUiManager(int cancleLayoutResId, int btnResId) {
        remove();
        updateShowWindow(cancleLayoutResId, btnResId);

    }


    public IFloatWindow getIFloatWindow(String tag) {
        return FloatWindow.get(tag);
    }


    private void updateShowWindow(int cancleLayoutResId, int btnResId) {

        if (PermissionUtil.hasPermission(appContext)) {
            IFloatWindow old = FloatWindow.get("old");
            if (old == null) {
                IFloatWindow cancel2 = FloatWindow.get("cancel2");
                if (cancel2 == null) {
                    FloatWindow
                            .with(appContext)
                            .setTag("cancel2")
                            .setView(cancleLayoutResId)
                            .setCancelParam2(320)
                            .setMoveType(MoveType.inactive, 0, 0)
                            .setDesktopShow(false)
                            .build();
                }
                IFloatWindow cancel = FloatWindow.get("cancel");
                if (cancel == null) {
                    FloatWindow
                            .with(appContext)
                            .setTag("cancel")
                            .setView(cancleLayoutResId)
                            .setCancelParam2(300)
                            .setMoveType(MoveType.inactive, 0, 0)
                            .setDesktopShow(false)
                            .build();
                }


                ImageView imageView = new ImageView(appContext);
                imageView.setBackgroundResource(btnResId);
                FloatWindow
                        .with(appContext)
                        .setTag("old")
                        .setView(imageView)
                        .setMoveType(MoveType.slide, 0, 0)
                        .setWidth(55)
//                        .setFilter(false, WebViewActivity.class)
                        .setHeight(55)
                        .setX(Screen.width, 0.8f)  //设置控件初始位置
                        .setY(mParent.getHeight() <= 0 ? 1854 : mParent.getHeight() / 3)
                        .setParentHeight(mParent.getHeight() <= 0 ? 1854 : mParent.getHeight())
                        .setMoveStyle(300, new AccelerateInterpolator())
                        .setViewStateListener(this)
                        .setPermissionListener(this)
                        .setDesktopShow(false)
                        .build();
                old = FloatWindow.get("old");
                startAnimation(old);
            } else {
                startAnimation(old);
            }
            isShow = true;
        } else {
            //没有浮窗权限
            startEmptyAnimation();
        }

    }

    private void startEmptyAnimation() {
        //创建当前视图的bitmap
        View view = mParent;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth() <= 0 ? 1080 : view.getWidth(), view.getHeight() <= 0 ? 1854 : view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        int mWidth = Util.dp2px(appContext, 55);
        int xOffset = Util.getScreenWidth(appContext) - mWidth;
        int yOffset = mParent.getHeight() <= 0 ? 1854 : mParent.getHeight() / 3;
        myImageView
                .createAnmiationParam()
                .setFromLeftX(0)
                .setToLeftX(xOffset)
                .setFromRightX(view.getWidth() <= 0 ? 1080 : mParent.getWidth())
                .setToRightX(xOffset + mWidth)
                .setFromTopY(0)
                .setToTopY(yOffset)
                .setFromBottomY(view.getHeight() <= 0 ? 1854 : mParent.getHeight())
                .setFromRadius(0)
                .setToRadius(mWidth / 2)
                .setToBottomY(yOffset + mWidth);
        myImageView.startAnimation(bitmap, mWidth);
        myImageView.setScaleCircleListener(new ScaleCircleImageView.ScaleCircleListener() {
            @Override
            public void onAnimationEnd() {
//                MyApplication.getMyApplication().setBackNoPermission(true);
            }

        });


        if (iPermissionListener != null) {
            iPermissionListener.requestPermission();
        }

    }


    private int width;
    private int height;

    private void startAnimation(IFloatWindow old) {

        //创建当前视图的bitmap
        View view = mParent;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth() <= 0 ? 1080 : view.getWidth(), view.getHeight() <= 0 ? 1854 : view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        myImageView
                .createAnmiationParam()
                .setFromLeftX(0)
                .setToLeftX(old.getmB().xOffset)
                .setFromRightX(mParent.getWidth() <= 0 ? 1080 : mParent.getWidth())
                .setToRightX(old.getmB().xOffset + old.getmB().mWidth)
                .setFromTopY(0)
                .setToTopY(old.getmB().yOffset)
                .setFromBottomY(mParent.getHeight() <= 0 ? 1854 : mParent.getHeight())
                .setFromRadius(0)
                .setToRadius(old.getmB().mWidth / 2)
                .setToBottomY(old.getmB().yOffset + old.getmB().mWidth);
        myImageView.startAnimation(bitmap, old.getmB().mWidth);
        myImageView.setScaleCircleListener(new ScaleCircleImageView.ScaleCircleListener() {
            @Override
            public void onAnimationEnd() {
                onShow();

            }

        });
    }

    @Override
    public void onPositionUpdate(int x, int y) {

    }

    @Override
    public void onShow() {
        Log.i(TAG, "onShow: ");


    }

    @Override
    public void onHide() {
        Log.i(TAG, "onHide: ");
    }

    @Override
    public void onDismiss() {

        Log.i(TAG, "onDismiss: ");


    }

    @Override
    public void onMoveAnimStart() {
        Log.i(TAG, "onMoveAnimStart: ");
    }

    @Override
    public void onMoveAnimEnd() {
        Log.i(TAG, "onMoveAnimEnd: ");
    }

    @Override
    public void onBackToDesktop() {

        Log.i(TAG, "onBackToDesktop: ");

    }

    @Override
    public void onCancelHide() {
        Log.i(TAG, "onCancelHide: ");
        if (iViewStateListener != null) {
            iViewStateListener.onCancleMute();
        }

        isShow = false;
    }


    @Override
    public void onSuccess() {
        Log.i(TAG, "onSuccess: ");
    }

    @Override
    public void onFail() {
        Log.i(TAG, "onFail: ");
    }

    private IViewStateListener iViewStateListener;

    //监听btn状态
    public void setiViewStateListener(IViewStateListener iViewStateListener) {
        this.iViewStateListener = iViewStateListener;
    }

    //监听权限
    private IPermissionListener iPermissionListener;

    public void setiPermissionListener(IPermissionListener iPermissionListener) {
        this.iPermissionListener = iPermissionListener;
    }

    public void removeWindowManager() {
        if (mWindowViewManager != null) {
            remove();
        }
    }
}
