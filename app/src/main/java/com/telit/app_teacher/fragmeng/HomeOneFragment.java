package com.telit.app_teacher.fragmeng;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hjq.toast.ToastUtils;
import com.telit.app_teacher.R;
import com.telit.app_teacher.activity.Fragment;
import com.telit.app_teacher.activity.OpenGlRtmpActivity;
import com.telit.app_teacher.adapter.HomeOneAdapter;
import com.telit.app_teacher.bean.ServerIpInfo;
import com.telit.app_teacher.customNetty.SimpleClientListener;
import com.telit.app_teacher.customNetty.SimpleClientNetty;
import com.telit.app_teacher.eventbus.EventBus;
import com.telit.app_teacher.eventbus.Subscriber;
import com.telit.app_teacher.eventbus.ThreadMode;
import com.telit.app_teacher.server.MultServer;
import com.telit.app_teacher.utils.Constant;
import com.telit.app_teacher.utils.MsgUtils;
import com.wildma.idcardcamera.camera.IDCardCamera;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class HomeOneFragment extends Fragment implements HomeOneAdapter.OnItemClickListener  {
    public static final String TAG="HomeOneFragment";

    private boolean isLine=false;
    @BindView(R.id.rv_home_one)
    RecyclerView rv_home_one;
    private List<Integer> images=new ArrayList<>();
    private List<String> names=new ArrayList<>();
    private int [] ints={
            R.mipmap.dianzibaiban,
            R.mipmap.paizhaijiangjie,
            R.mipmap.zhantai,
            R.mipmap.pingjia,
            R.mipmap.weike,
            R.mipmap.pingjia,
    };
    private String isOnLion="offLine";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.home_one_fragment;
    }


    @Override
    protected void initData() {
        super.initData();
        for (int indeex : ints){
            images.add(indeex);
        }
        names.add("电子白板");
        names.add("拍照讲解");
        names.add("实物展台");
        names.add("讲评");
        names.add("微课");
        names.add("评价");
        // 设置布局管理器
        rv_home_one.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // 设置adapter
        HomeOneAdapter homeOneAdapter=new HomeOneAdapter(images,names,getContext());
        homeOneAdapter.setOnItemClickListener(this);
        rv_home_one.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rv_home_one.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(15, 15, 15, 15);
            }
        });
        rv_home_one.setAdapter(homeOneAdapter);
        // rv_home_one.setAdapter();
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position){
            case 0:
                break;
            case 1:
                //点击了拍照讲解
                if (isOnLion.equals("onLine")){
                    IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);

                }else {
                    ToastUtils.show("请先加入班级");
                }


                break;
            case 2:
                //点击了实物展台
                Log.i(TAG, "onItemClick: ");
                startActivity(new Intent(getContext(), OpenGlRtmpActivity.class));
                if (isOnLion.equals("onLine")){

                }else {
                    ToastUtils.show("请先加入班级");
                }
                break;
            case 3:
                break;
        }
    }

    /**
     * 判断设备是不是在线
     * @param online
     */
    private String serviceOnline="offLine";
    @Subscriber(tag = Constant.CLASS_IS_ON_LINE, mode = ThreadMode.MAIN)
    public void isServerLine(String online) {
        Log.i("qin", "lingChuangList: " + online);
        serviceOnline=online;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注册EventBus
        EventBus.getDefault().unregister(this);
        //退出班级,服务端会主动关闭连接
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_Stop_Screen_Cast, MsgUtils.stopTeacherAddress());
    }

    public void setOnLine(String isOnLion) {

        this.isOnLion = isOnLion;
    }
}
