package com.telit.app_teacher.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.view.WindowManager;


import androidx.annotation.LayoutRes;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @discription: 基础dialog
 * @author: zcq
 * @e_mail: 2473117666@qq.com
 * @date: 2017/10/31
 * @version:
 * @修改人：
 * @修改记录:
 */
public abstract class BaseDialog extends Dialog {

    private Unbinder unbinder;

    public Context context;
    public DialogInterface dialogInterface;
    public String dialogTitle;
    public String dialogMessage;
    public String okMessage;
    public String cancelMessage;
    public boolean canceledOnKeyBack = true;

    public BaseDialog(Context context) {
        super(context);
        this.context = context;
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState, @LayoutRes int layout) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setContentView(layout);

        unbinder = ButterKnife.bind(this);

        this.initBoots();
        this.initViews();
        this.initData();
        this.initEvents();
    }

    @Override
    public void onDetachedFromWindow() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDetachedFromWindow();
    }

    public abstract void initBoots();

    public abstract void initViews();

    public abstract void initData();

    public abstract void initEvents();

    public BaseDialog setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    public BaseDialog setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
        return this;
    }

    public BaseDialog setDialogOkMessage(String okMessage) {
        this.okMessage = okMessage;
        return this;
    }

    public BaseDialog setDialogCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
        return this;
    }

    public BaseDialog setDialogAllInfo(String dialogTitle, String dialogMessage, String okMessage, String cancelMessage) {
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.okMessage = okMessage;
        this.cancelMessage = cancelMessage;
        return this;
    }

    public BaseDialog setDialogCallBack(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
        return this;
    }

    public BaseDialog setCanceledOnKeyBack(boolean canceledOnKeyBack) {
        this.canceledOnKeyBack = canceledOnKeyBack;
        return this;
    }

    public Resources getRes() {
        return this.context.getResources();
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return this.canceledOnKeyBack && keyCode == 4?true:super.onKeyDown(keyCode, event);
//    }



    /**
     * [防止快速点击]
     *
     * @return
     */
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    public boolean fastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}

