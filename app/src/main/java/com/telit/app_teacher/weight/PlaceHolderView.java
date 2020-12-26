package com.telit.app_teacher.weight;


import androidx.annotation.StringRes;

/**
 * Created by on tiantianqin.
 * Email:qin15156589949@gmail.com
 * Date:2019/8/2
 * Time:14:43
 */

public interface PlaceHolderView {
    /**
     * 空布局
     */
    void triggerEmpty();
    /**
     * 网络请求错误的布局
     */
    void triggerNetError();
    /**
     * 加载错误显示错误的信息
     */
    void triggerError(@StringRes int strRes);

    /**
     * 加载中的布局
     */
    void triggerLoading();

    /**
     * 加载成功的布局
     * 隐藏当前的占位布局
     */
    void triggerOk();
    /**
     * 该方法如果传入的isOk为True则为成功状态，
     * 此时隐藏布局，反之显示空数据布局
     *
     * @param isOk 是否加载成功数据
     */
    void triggerOkOrEmpty(boolean isOk);

}
