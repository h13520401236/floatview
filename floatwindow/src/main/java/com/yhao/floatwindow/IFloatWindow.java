package com.yhao.floatwindow;

import android.view.View;


public abstract class IFloatWindow {
    public abstract void show();

    public abstract void showCancel(boolean isBig);

    public abstract void showCancelBig();


    public abstract void hideCancel(boolean isBig);

    public abstract void hide();

    public abstract boolean isShowing();

    public abstract int getX();

    public abstract int getY();

    public abstract void updateX(int x);

    public abstract void updateX(@Screen.screenType int screenType,float ratio);

    public abstract void updateY(int y);

    public abstract void updateY(@Screen.screenType int screenType,float ratio);

    public abstract View getView();

    public abstract void dismiss();

    abstract int[] getOffset();

    public abstract FloatWindow.B getmB();

    public abstract  boolean isShow();

}
