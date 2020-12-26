package com.telit.app_teacher.floatingview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telit.app_teacher.Myapp;
import com.telit.app_teacher.R;
import com.telit.app_teacher.eventbus.EventBus;
import com.telit.app_teacher.utils.Constant;

import butterknife.OnTouch;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 拖拽后回弹处理实现类
 */
public class SpringDraggable extends BaseDraggable {

    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    /** 回弹的方向 */
    private int mOrientation;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;
    private onItemClickListener listener;


    public SpringDraggable() {
        this(LinearLayout.HORIZONTAL);
    }

    public SpringDraggable(int orientation) {
        mOrientation = orientation;
        switch (mOrientation) {
            case LinearLayout.HORIZONTAL:
            case LinearLayout.VERTICAL:
                break;
            default:
                throw new IllegalArgumentException("You cannot pass in directions other than horizontal or vertical");
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float rawMoveX;
        float rawMoveY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();

                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();


                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                rawMoveX = event.getRawX();
                rawMoveY = event.getRawY() - getStatusBarHeight();

                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                // 更新移动的位置
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY);
                break;
            case MotionEvent.ACTION_UP:
                // 记录移动的位置（相对屏幕的坐标）
                if (Math.abs(xDownInScreen - xInScreen) <= ViewConfiguration.get(Myapp.getInstance()).getScaledTouchSlop()
                        && Math.abs(yDownInScreen - yInScreen) <= ViewConfiguration.get(Myapp.getInstance()).getScaledTouchSlop()) {
//                    FloatWindowManager.getInstance().dismissWindow();

                    //点击处理
                    onclick(ll__guan_kong_check,iv_guan_kong_head,tv_suo_ping,tv_ji_ping,
                            tv_start_guang_bo,tv_stop_guang_bo,ll_guan_kong_pant,ll_guan_kong_ban_ca
                    ,ll_guan_kong_base);
                }else {
                    rawMoveX = event.getRawX();
                    rawMoveY = event.getRawY() - getStatusBarHeight();

                    // 自动回弹吸附
                    switch (mOrientation) {
                        case LinearLayout.HORIZONTAL:
                            final float rawFinalX;
                            // 获取当前屏幕的宽度
                            int screenWidth = getScreenWidth();
                            if (rawMoveX < screenWidth / 2f) {
                                // 回弹到屏幕左边
                                rawFinalX = 0f;
                            } else {
                                // 回弹到屏幕右边
                                rawFinalX = screenWidth;
                            }
                            // 从移动的点回弹到边界上
                            startHorizontalAnimation(rawMoveX - mViewDownX, rawFinalX  - mViewDownX, rawMoveY - mViewDownY);
                            break;
                        case LinearLayout.VERTICAL:
                            final float rawFinalY;
                            // 获取当前屏幕的高度
                            int screenHeight = getScreenHeight();
                            if (rawMoveY < screenHeight / 2f) {
                                // 回弹到屏幕顶部
                                rawFinalY = 0f;
                            } else {
                                // 回弹到屏幕底部
                                rawFinalY = screenHeight;
                            }
                            // 从移动的点回弹到边界上
                            startVerticalAnimation(rawMoveX - mViewDownX, rawMoveY - mViewDownY, rawFinalY);



                            break;
                        default:
                            break;
                    }
                }
               return isTouchMove(mViewDownX, event.getX(), mViewDownY, event.getY());
                //return isclick;
            default:
                break;
        }
        return true;
    }


    /**
     * 执行点击事件
     *
     */
    private boolean isShowd=true;
    private void onclick(LinearLayout ll__guan_kong_check, ImageView iv_guan_kong_head, TextView tv_suo_ping,
                         TextView tv_ji_ping, TextView tv_start_guang_bo,
                         TextView tv_stop_guang_bo, LinearLayout ll_guan_kong_pant,
                         LinearLayout ll_guan_kong_ban_ca,
                         LinearLayout ll_guan_kong_base   ){
        if (inRangeOfView(iv_guan_kong_head)){
            setOperateShowOrHide();
         //   Toast.makeText(Myapp.getInstance(),"点击了头",Toast.LENGTH_LONG).show();
            //隐藏那个布局
            isShowd=!isShowd;
            if (isShowd){
                ll_guan_kong_count.setVisibility(View.VISIBLE);
            }else {
                ll_guan_kong_count.setVisibility(View.GONE);

            }

        }else if (inRangeOfView(tv_suo_ping)){
           // Toast.makeText(Myapp.getInstance(),"锁屏",Toast.LENGTH_LONG).show();
            if (listener!=null){
                listener.onclick(1);
            }
        }else if (inRangeOfView(tv_ji_ping)){
            //Toast.makeText(Myapp.getInstance(),"解屏",Toast.LENGTH_LONG).show();
            if (listener!=null){
                listener.onclick(2);
            }
        }else if (inRangeOfView(tv_start_guang_bo)){
            //Toast.makeText(Myapp.getInstance(),"开启广播",Toast.LENGTH_LONG).show();
            if (listener!=null){
                listener.onclick(3);
            }
        }else if (inRangeOfView(tv_stop_guang_bo)){
          //  Toast.makeText(Myapp.getInstance(),"关闭广播",Toast.LENGTH_LONG).show();
            if (listener!=null){
                listener.onclick(4);
            }
        }else if (inRangeOfView(ll__guan_kong_check)){
            //选择的点击事件
            ll__guan_kong_check.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.wrong_color_guan_kong));
            ll_guan_kong_pant.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.word_white));
            ll_guan_kong_ban_ca.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.word_white));
            if (listener!=null){
                listener.onclick(8);
            }

        }else if (inRangeOfView(ll_guan_kong_pant)){
            ll_guan_kong_pant.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.wrong_color_guan_kong));
            ll__guan_kong_check.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.word_white));
            ll_guan_kong_ban_ca.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.word_white));
            //画笔的点击事件
            if (listener!=null){
                listener.onclick(5);
            }
        }else if (inRangeOfView(ll_guan_kong_ban_ca)){
            //板擦的点击事件
            ll_guan_kong_ban_ca.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.wrong_color_guan_kong));
            ll__guan_kong_check.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.word_white));
            ll_guan_kong_pant.setBackgroundColor(Myapp.getInstance().getResources().getColor(R.color.word_white));
            if (listener!=null){
                listener.onclick(6);
            }
        }else if (inRangeOfView(ll_guan_kong_base)){
            //管控的点击事件
            if (listener!=null){
                listener.onViewClick(7,ll_guan_kong_base);
            }
        }
    }


    private boolean inRangeOfView(View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        Log.i("OkGo","X:"+x+" Y:"+y);


        if(xInScreen < x || xInScreen > (x + view.getWidth()) || yInScreen < y || yInScreen > (y + view.getHeight())){
            return false;
        }
        return true;
    }
    private boolean hided;//是否隐藏
    /**
     * 显示或隐藏操作
     */
    private void setOperateShowOrHide(){
        if (hided){
            hided = false;
/*
            tv1.setVisibility(VISIBLE);
            tv2.setVisibility(VISIBLE);*/
        }else {
            hided = true;

           /* tv1.setVisibility(GONE);
            tv2.setVisibility(GONE);*/
        }
    }



    /**
     *  获取屏幕的宽度
     */
    private int getScreenWidth() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     *  获取屏幕的高度
     */
    private int getScreenHeight() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 执行水平回弹动画
     *
     * @param startX        X 轴起点坐标
     * @param endX          X 轴终点坐标
     * @param y             Y 轴坐标
     */
    private void startHorizontalAnimation(float startX, float endX, final float y) {
        ValueAnimator animator = ValueAnimator.ofFloat(startX, endX);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateLocation((float) animation.getAnimatedValue(), y);
            }
        });
        animator.start();
    }

    /**
     * 执行垂直回弹动画
     *
     * @param x             X 轴坐标
     * @param startY        Y 轴起点坐标
     * @param endY          Y 轴终点坐标
     */
    private void startVerticalAnimation(final float x, float startY, final float endY) {
        ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateLocation(x, (float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    public interface onItemClickListener{
        void onclick(int position);
        void onViewClick(int position,LinearLayout layout);
    }
   public void setOnItemClickListener(onItemClickListener listener){

        this.listener = listener;
    }
}