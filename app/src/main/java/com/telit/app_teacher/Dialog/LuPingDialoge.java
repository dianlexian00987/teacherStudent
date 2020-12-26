package com.telit.app_teacher.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.telit.app_teacher.R;
import com.telit.app_teacher.net.Common;

import java.io.File;

public class LuPingDialoge extends BaseDialog implements View.OnClickListener {

    private TextView tv_save_server;
    private String path;

    public LuPingDialoge(Context context, String path) {
        super(context);
        this.path = path;
    }

    public LuPingDialoge(Context context, int theme) {
        super(context, R.style.Dialog_Style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.dialog_lu_ping_model);

    }

    @Override
    public void initBoots() {

    }

    @Override
    public void initViews() {
        tv_save_server = (TextView) findViewById(R.id.tv_save_server);
        tv_save_server.setOnClickListener(this);

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvents() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save_server:
                //把视频保存到服务端
                saveLessonServer();
                dismiss();
                break;
        }
    }

    public static final String TAG = "OpenGlRtmpActivity";

    //视频录制好后保存到server
    public void saveLessonServer() {
        Log.i("qin", "lingChuangList: ");
        File file = new File(path);
        OkGo.<String>post(Common.Constance.UPWEILEVIDEO)
                .tag(this)
                .isMultipart(true)
                .params("type", "0")
                .params("delFlag", "0")
                .params("userId", "66666819626")
                .params("name", file.getName())
                .params("diskId", "udppoiuytrewq")
                .params("dealFlag", "create")
                .params("fileType", "1")
                .params("file",file)
                .params(" parentId",0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i(TAG, "onSuccess: "+response.body().toString());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.i(TAG, "onError: "+response.body());
                    }
                });


    }
}
