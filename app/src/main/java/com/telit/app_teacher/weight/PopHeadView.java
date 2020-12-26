package com.telit.app_teacher.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class PopHeadView extends ImageView {

    private boolean isclick;
    private long startTime = 0;
    private long endTime = 0;

    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    public PopHeadView(Context context) {
        super(context);
    }

    public PopHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PopHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float rawMoveX;
        float rawMoveY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();

                isclick = false;//当按下的时候设置isclick为false，具体原因看后边的讲解
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                if ((endTime - startTime) > 0.1 * 1000L) {
                    isclick = true;
                    return isclick;
                } else {
                    isclick = false;
                }

                // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
               return isclick;
            default:
                break;
        }
        return true;
    }
}
