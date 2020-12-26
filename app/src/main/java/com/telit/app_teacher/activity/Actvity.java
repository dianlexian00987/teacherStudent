package com.telit.app_teacher.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.telit.app_teacher.R;
import com.telit.app_teacher.floatingview.SpringDraggable;
import com.telit.app_teacher.floatingview.PopWindows;

import com.telit.app_teacher.weight.PlaceHolderView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;


public abstract class Actvity extends AppCompatActivity {
    protected PlaceHolderView mPlaceHolderView;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.RECORD_AUDIO",
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.SYSTEM_ALERT_WINDOW"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在界面未初始化之前调用的初始化窗口
        initWidows();
        //班牌初始化不同的布局类型
        initBcakageType();
        //获取权限
        requestPerm();
        if (initArgs(getIntent().getExtras())) {
            // 得到界面Id并设置到Activity界面中
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int layId = getContentLayoutId();
            setContentView(layId);

            initBefore();

            initWidget();

            initView();
            initData();
        } else {
            finish();
        }
    }


    protected  void initView(){};

    /**
     * 初始化数据
     */
    protected void initData() {

    }
    //初始化控件
    private void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化班牌样式的类型
     * @return
     */
    protected  int initBcakageType(){
        return 0;
    };

    //在控件初始化前调用
    protected void initBefore() {
        //初始化刷卡
        //初始化开关机

    }
    /**
     * 初始化窗口
     */
    protected void initWidows() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    protected  abstract int getContentLayoutId();





    /**
     * 设置占位布局
     *
     * @param placeHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }
    private final static int PERMISSIONS_OK = 10001;
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    init();
                } else {
                    // showWaringDialog();
                }
                break;
            default:
                break;
        }
    }
    private void requestPerm() {
        if (Build.VERSION.SDK_INT>22) {
            if (!checkPermissionAllGranted(PERMISSIONS_STORAGE)) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS_STORAGE, PERMISSIONS_OK);
            }else{
                init();
            }
        }else{
            init();
        }
    }

    private void init() {

    }
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    
}
