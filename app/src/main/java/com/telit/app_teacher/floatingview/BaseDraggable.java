package com.telit.app_teacher.floatingview;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.app_teacher.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 拖拽抽象类
 */
public abstract class BaseDraggable implements View.OnTouchListener {

    private PopWindows mToast;
    public View mRootView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    //头布局
    public ImageView iv_guan_kong_head;
    //锁屏
    public TextView tv_suo_ping;
    //解屏
    public TextView tv_ji_ping;
    //开启屏幕广播
    public TextView tv_start_guang_bo;
    //关闭屏幕广播
    public TextView tv_stop_guang_bo;
    //选择的点击事件
    public LinearLayout ll__guan_kong_check;
    public LinearLayout ll_guan_kong_count;
    //画笔的点击事件
    public LinearLayout ll_guan_kong_pant;
    //板擦的点击事件
    public LinearLayout ll_guan_kong_ban_ca;
    //管控的点击事件
    public LinearLayout ll_guan_kong_base;

    /**
     * Toast 显示后回调这个类
     */
    public void start(PopWindows toast) {
        mToast = toast;
        mRootView = toast.getView();
        mWindowManager = toast.getWindowManager();
        mWindowParams = toast.getWindowParams();

        mRootView.setOnTouchListener(this);
        iv_guan_kong_head= mRootView.findViewById(R.id.iv_guan_kong_head);
        tv_suo_ping = mRootView.findViewById(R.id.tv_suo_ping);
        tv_ji_ping = mRootView.findViewById(R.id.tv_ji_ping);
        tv_start_guang_bo = mRootView.findViewById(R.id.tv_start_guang_bo);
        tv_stop_guang_bo = mRootView.findViewById(R.id.tv_stop_guang_bo);
        ll__guan_kong_check = mRootView.findViewById(R.id.ll__guan_kong_check);
        ll_guan_kong_count = mRootView.findViewById(R.id.ll_guan_kong_count);
        ll_guan_kong_pant = mRootView.findViewById(R.id.ll_guan_kong_pant);
        ll_guan_kong_ban_ca = mRootView.findViewById(R.id.ll_guan_kong_ban_ca);
        ll_guan_kong_base = mRootView.findViewById(R.id.ll_guan_kong_base);


    }

    protected PopWindows getXToast() {
        return mToast;
    }

    protected WindowManager getWindowManager() {
        return mWindowManager;
    }

    protected WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    protected View getRootView() {
        return mRootView;
    }

    /**
     * 获取状态栏的高度
     */
    protected int getStatusBarHeight() {
        Rect frame = new Rect();
        getRootView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 更新悬浮窗的位置
     *
     * @param x             x 坐标
     * @param y             y 坐标
     */
    protected void updateLocation(float x, float y) {
        updateLocation((int) x, (int) y);
    }

    /**
     * 更新 WindowManager 所在的位置
     */
    protected void updateLocation(int x, int y) {
        if (mWindowParams.x != x || mWindowParams.y != y) {
            mWindowParams.x = x;
            mWindowParams.y = y;
            // 一定要先设置重心位置为左上角
            mWindowParams.gravity = Gravity.TOP | Gravity.START;
            try {
                mWindowManager.updateViewLayout(mRootView, mWindowParams);
            } catch (IllegalArgumentException ignored) {
                // 当 WindowManager 已经消失时调用会发生崩溃
                // IllegalArgumentException: View not attached to window manager
            }
        }
    }

    /**
     * 判断用户是否移动了，判断标准以下：
     * 根据手指按下和抬起时的坐标进行判断，不能根据有没有 move 事件来判断
     * 因为在有些机型上面，就算用户没有手指没有移动也会产生 move 事件
     *
     * @param downX         手指按下时的 x 坐标
     * @param upX           手指抬起时的 x 坐标
     * @param downY         手指按下时的 y 坐标
     * @param upY           手指抬起时的 y 坐标
     */
    protected boolean isTouchMove(float downX, float upX, float downY, float upY) {
        return ((int) downX) != ((int) upX) || ((int) (downY)) != ((int) upY);
    }
}