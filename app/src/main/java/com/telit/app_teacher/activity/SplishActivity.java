package com.telit.app_teacher.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.telit.app_teacher.MainActivity;
import com.telit.app_teacher.Myapp;
import com.telit.app_teacher.R;
import com.telit.app_teacher.adapter.MainAdapter;
import com.telit.app_teacher.bean.ServerIpInfo;
import com.telit.app_teacher.customNetty.SimpleClientListener;
import com.telit.app_teacher.customNetty.SimpleClientNetty;
import com.telit.app_teacher.eventbus.EventBus;
import com.telit.app_teacher.eventbus.Subscriber;
import com.telit.app_teacher.eventbus.ThreadMode;

import com.telit.app_teacher.floatingview.OnClickListener;
import com.telit.app_teacher.floatingview.OnTouchListener;
import com.telit.app_teacher.floatingview.PopWindows;
import com.telit.app_teacher.floatingview.SpringDraggable;
import com.telit.app_teacher.fragmeng.HomeOneFragment;
import com.telit.app_teacher.fragmeng.HomeTwoFragment;
import com.telit.app_teacher.server.DisplayService;
import com.telit.app_teacher.server.MultServer;
import com.telit.app_teacher.utils.Constant;
import com.telit.app_teacher.utils.MsgUtils;
import com.telit.app_teacher.utils.SharedPreferenceUtil;
import com.wildma.idcardcamera.cropper.PaletteView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

public class SplishActivity extends Actvity implements SimpleClientListener, SpringDraggable.onItemClickListener, View.OnClickListener {
    public static final String TAG = "SplishActivity";
    @BindView(R.id.ll_check_class)
    LinearLayout ll_check_class;
    @BindView(R.id.tv_check_class)
    TextView tv_check_class;
    @BindView(R.id.rt_start_lesson)
    Button rt_start_lesson;
    @BindView(R.id.vp_home_list)
    ViewPager vp_home_list;

    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    @BindView(R.id.new_color_board_view)
    PaletteView new_color_board_view;

    AttachPopupView attachPopupView;
    private List<Fragment> fragments = new ArrayList<>();
    private int checkPosition = 0;

    private boolean isClasslesson = false;

    private final int REQUEST_CODE_STREAM = 179; //random num
    private final int REQUEST_CODE_RECORD = 180; //random num
    private NotificationManager notificationManager;
    private HomeOneFragment homeOneFragment;
    private PopWindows popWindows;
    private ImageView iv_guan_long_suo;
    private TextView tv_guan_long_suo;
    private LinearLayout ll_guan_long_suo;
    private LinearLayout ll_guan_long_tou;
    private LinearLayout ll_guan_long_close;
    private LinearLayout ll_guan_long_chuan;
    private LinearLayout ll_guan_long_join;
    private ImageView iv_guan_long_chuan;
    private TextView tv_guan_long_chuan;
    private ImageView iv_guan_long_tou;
    private TextView tv_guan_long_tou;
    private ImageView iv_guan_long_close;
    private TextView tv_guan_long_close;
    private ImageView iv_guan_long_join;
    private TextView tv_guan_long_join;

    @Override
    protected int getContentLayoutId() {
        return R.layout.splish_activity_layout;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册EventBus
        EventBus.getDefault().register(this);

        //初始化服务
        DisplayService.Companion.init(this);

    }

    @Override
    protected void initBefore() {
        super.initBefore();
        //通讯服务接口
        SimpleClientNetty.getInstance().setSimpleClientListener(this);

        //获取到班级信息
        startService(new Intent(this, MultServer.class));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SpringDraggable springDraggable = new SpringDraggable();
        springDraggable.setOnItemClickListener(this);
        XXPermissions.with(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> granted, boolean all) {

                        if (popWindows == null) {
                            popWindows = new PopWindows(getApplication())
                                    .setView(R.layout.popwindoeview)
                                    .setGravity(Gravity.LEFT | Gravity.TOP)
                                    .setYOffset(100)
                                    .setDraggable(springDraggable)
                                    .show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> denied, boolean never) {

                    }
                });
        //默认保存锁屏的状态
        SharedPreferenceUtil.getInstance(this).putBoolean("is_close_open",true);


    }

    @Override
    protected void initView() {
        super.initBefore();
        //初始化fragment
        homeOneFragment = new HomeOneFragment();
        HomeTwoFragment homeTwoFragment = new HomeTwoFragment();
        fragments.add(homeOneFragment);
        fragments.add(homeTwoFragment);

        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), fragments);
        vp_home_list.setAdapter(mainAdapter);
    }
    @OnClick(R.id.ll_check_class)
    void checkClass() {
        if (className != null && className.length > 0) {
            attachPopupView = new XPopup.Builder(this)
                    .hasShadowBg(false)
//                            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                        .isDarkTheme(true)
//                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter) //NoAnimation表示禁用动画
//                        .isCenterHorizontal(true) //是否与目标水平居中对齐
//                        .offsetY(-60)
//                        .offsetX(80)
//                        .popupPosition(PopupPosition.Top) //手动指定弹窗的位置
                    .atView(ll_check_class)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                    .asAttachList(className,
                            new int[]{},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    tv_check_class.setText(text);

                                    //去连接服务器
                                    if (classInfos.size()== 0)return;
                                    String ip = classInfos.get(position).getDeviceIp();
                                    int port = Integer.valueOf(classInfos.get(position).getDevicePort());
                                    Log.i(TAG, "handleMessage: " + ip + port);
                                    SharedPreferenceUtil.getInstance(Myapp.getInstance()).putString("socketIp", ip);
                                    SharedPreferenceUtil.getInstance(Myapp.getInstance()).putInt("port", port);
                                    //建立连接
                                    SimpleClientNetty.getInstance().reConnect();
                                }
                            }, 0, 0);
            ;
//                }
            attachPopupView.show();
        } else {
            ToastUtils.show("请先加入班级");
        }


    }

    @OnClick(R.id.rt_start_lesson)
    void startLesson() {
        //开始授课
        //必须选中了班级才能开始推流
        if (classInfos.size() > 0) {
            isClasslesson = !isClasslesson;
            rt_start_lesson.setText(isClasslesson ? "结束授课" : "开始授课");
            if (isClasslesson) {

                if (!DisplayService.Companion.isStreaming()) {

                    startActivityForResult(DisplayService.Companion.sendIntent(), REQUEST_CODE_STREAM);
                    ToastUtils.show("开始上课");
                }
            } else {
                if (DisplayService.Companion.isStreaming()) {

                    stopService(new Intent(SplishActivity.this, DisplayService.class));
                   // ToastUtils.show("结束上课");

                    //发送结束上课个server
                    //推流成功发送消息
                    SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_Stop_Screen_Cast,
                            MsgUtils.sendServerInfo(MsgUtils.HEAD_Stop_Screen_Cast,""));
                   // tv_check_class.setText("请选着班级");
       //             classInfos.clear();

                }
            }
        } else {
            ToastUtils.show("请先加入授课班级");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && (requestCode == REQUEST_CODE_STREAM
                || requestCode == REQUEST_CODE_RECORD && resultCode == Activity.RESULT_OK)) {
            initNotification();
            DisplayService.Companion.setData(resultCode, data);
            Intent intent = new Intent(this, DisplayService.class);
            intent.putExtra("endpoint", Constant.RtmpUrl);
            startService(intent);
        } else {
            Toast.makeText(this, "No permissions available", Toast.LENGTH_SHORT).show();
            //button.setText(R.string.start_button);
        }
    }


    private CopyOnWriteArrayList<ServerIpInfo> classInfos = new CopyOnWriteArrayList<>();
    private String[] className;

    @Subscriber(tag = Constant.CLASS_INFO_LIST, mode = ThreadMode.MAIN)
    public void lingChuangList(CopyOnWriteArrayList<ServerIpInfo> appInfoList) {
        Log.i("qin", "lingChuangList: " + appInfoList);
        if (appInfoList!=null){
            classInfos.clear();
            classInfos.addAll(appInfoList);
            className = new String[classInfos.size()];
            if (appInfoList != null) {
                for (int i = 0; i < appInfoList.size(); i++) {
                    className[i] = appInfoList.get(i).getClassName();
                }
            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //注册EventBus
        EventBus.getDefault().unregister(this);

        //tudo 退出班级
        if (isOnLion.equals("onLine")){
            //退出班级
            SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_OUT_OF_CLASS,
                    MsgUtils.sendServerInfo(MsgUtils.HEAD_OUT_OF_CLASS,""));
        }
        if (popWindows!=null){
            popWindows.cancel();
            popWindows=null;
        }
        SharedPreferenceUtil.getInstance(SplishActivity.this).putBoolean("startTeacher",false);
        SharedPreferenceUtil.getInstance(this).putBoolean("is_close_open",true);
    }

    @Override
    public void onLine() {
        //把地址告诉教师端
        sendJoinClass();
        isOnLion = "onLine";

        homeOneFragment.setOnLine(isOnLion);
    }

    private String isOnLion = "offLine";

    @Override
    public void offLine() {
        isOnLion = "offLine";
        homeOneFragment.setOnLine(isOnLion);
    }
    @Override
    public void receiveData(String msgInfo) {
        //如果没有收到内容
        Log.i(TAG, "receiveDataqin001: "+msgInfo);
        if (TextUtils.isEmpty(msgInfo)) return;
        String[] splitString = msgInfo.trim().split(MsgUtils.SEPARATOR);
        String head = splitString[0];

        //如果没有发送头指令就结束
        if (TextUtils.isEmpty(head) || head.equals(MsgUtils.HEAD_HEART) || splitString.length<=1) return;
        String seqId = splitString[1];

        if (head.equals(MsgUtils.HEAD_StopPadScreenCast)){
            //接收到教师端关闭的通知，关闭推流

            rt_start_lesson.setText("开始授课");
            stopService(new Intent(SplishActivity.this, DisplayService.class));
            isClasslesson=false;
           // ToastUtils.show("结束上课");
           // tv_check_class.setText("请选着班级");
           // classInfos.clear();
        }
        if (head.equals(MsgUtils.HEAD_ScreenIp)){
          Constant.RtmpUrl="rtsp://"+splitString[2]+":554/tiantianqin";
            //Constant.RtmpUrl="rtmp://"+splitString[2]+"/live/tiantainqin";
            Log.i(TAG, "receiveData: "+ Constant.RtmpUrl);
        }
        //客户端消息收到的反馈,服务端的心跳包不要回执,服务端回执也不需要回执
        if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
            SimpleClientNetty.getInstance().sendMsgToServer
                    (MsgUtils.HEAD_ACKNOWLEDGE, MsgUtils.createAcknowledge(seqId));
        }
    }
    private void sendJoinClass() {
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_JOINCLASS,
                MsgUtils.joinClass(SimpleClientNetty.getInstance().isReconnected()));
    }

    /**
     * This notification is to solve MediaProjection problem that only render surface if something
     * changed.
     * It could produce problem in some server like in Youtube that need send video and audio all time
     * to work.
     */
    private void initNotification() {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this).setSmallIcon(R.drawable.notification_anim)
                        .setContentTitle("Streaming")
                        .setContentText("Display mode stream")
                        .setTicker("Stream in progress");
        notificationBuilder.setAutoCancel(true);
        if (notificationManager != null)
            notificationManager.notify(12345, notificationBuilder.build());
    }

    private void stopNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(12345);
        }
    }

    //管控 item 的点击事件
    private boolean isClose=true;
    @Override
    public void onclick(int position) {
        switch (position) {
            case 3:
                //开启广播的点击事件
                ToastUtils.show("开启广播");

                ToastUtils.show("开启屏幕广播");
                if (isOnLion.equals("onLine")){
                    //发送开启屏幕广播的消息给教师端
                    SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_BROADCAST,
                            MsgUtils.sendServerInfo(MsgUtils.HEAD_BROADCAST,Constant.RtmpUrl));
                }else {
                    ToastUtils.show("请先加入班级");
                }


                break;
            case 4:
                //关闭广播点击事件
                if (isOnLion.equals("onLine")){
                    //发送关闭广播的消息给教师端
                    SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_STOP_BROADCAST,
                            MsgUtils.sendServerInfo(MsgUtils.HEAD_STOP_BROADCAST,""));
                    ToastUtils.show("关闭屏幕广播");
                }else {
                    ToastUtils.show("请先加入班级");
                }
                break;
            case 5:
                //画笔的点击事件
                /*Intent intent=new Intent(this,PaleTeViewActivity.class);
                startActivity(intent);*/
                new_color_board_view.setVisibility(View.VISIBLE);
                new_color_board_view.setMode(PaletteView.Mode.DRAW);
                new_color_board_view.init();
                new_color_board_view.refresh();
               // new_color_board_view.reDraw();
                break;
            case 6:
                //板擦的点击事件
                new_color_board_view.setMode(PaletteView.Mode.ERASER);
                break;
            case 8:
                //选折的点击
                new_color_board_view.clear();
                new_color_board_view.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onViewClick(int position, LinearLayout layout) {
        if (position == 7){
            //管控的点击事件
            if (isOnLion.equals("onLine")){
                popMorePan(fl_content);
            }else {
                ToastUtils.show("请先加入班级");
            }

        }
    }

    private PopupWindow morePop;

    /**
     * 弹出更多内容选择弹框
     */
    @SuppressLint("ResourceType")
    private void popMorePan(View v) {
        if (morePop != null) {
            morePop.dismiss();
        }
        View moreView = LayoutInflater.from(this).inflate(R.layout.pop_guan_kong_layout, null);
        int moreWidth = getResources().getDimensionPixelSize(R.dimen.x530);
        int moreHeight = getResources().getDimensionPixelSize(R.dimen.y106);
        morePop = new PopupWindow(moreView, moreWidth, moreHeight);

        morePop.setBackgroundDrawable(new ColorDrawable());
        morePop.setOutsideTouchable(true);

       //锁屏的点击事件
        ll_guan_long_suo = moreView.findViewById(R.id.ll_guan_long_suo);
        iv_guan_long_suo = moreView.findViewById(R.id.iv_guan_long_suo);
        //锁屏解屏的图标
        tv_guan_long_suo = moreView.findViewById(R.id.tv_guan_long_suo);
        ll_guan_long_suo.setOnClickListener(this);
        //教师传屏
        ll_guan_long_chuan = moreView.findViewById(R.id.ll_guan_long_chuan);
        iv_guan_long_chuan = moreView.findViewById(R.id.iv_guan_long_chuan);
        tv_guan_long_chuan = moreView.findViewById(R.id.tv_guan_long_chuan);
        ll_guan_long_chuan.setOnClickListener(this);
        //学生投屏的点击事件
        ll_guan_long_tou = moreView.findViewById(R.id.ll_guan_long_tou);
        iv_guan_long_tou = moreView.findViewById(R.id.iv_guan_long_tou);
        tv_guan_long_tou = moreView.findViewById(R.id.tv_guan_long_tou);
        ll_guan_long_tou.setOnClickListener(this);
        //一键关机的点击事件
        ll_guan_long_close = moreView.findViewById(R.id.ll_guan_long_close);
        iv_guan_long_close = moreView.findViewById(R.id.iv_guan_long_close);
        tv_guan_long_close = moreView.findViewById(R.id.tv_guan_long_close);
        ll_guan_long_close.setOnClickListener(this);
        //一键加入
        ll_guan_long_join = moreView.findViewById(R.id.ll_guan_long_join);
        iv_guan_long_join = moreView.findViewById(R.id.iv_guan_long_join);
        tv_guan_long_join = moreView.findViewById(R.id.tv_guan_long_join);
        ll_guan_long_join.setOnClickListener(this);



        //这里粗劣计算头布局的高度一半
        int offsetHeight = getResources().getDimensionPixelSize(R.dimen.x655);
        morePop.showAsDropDown(v, 140, offsetHeight);
        boolean is_close_open = SharedPreferenceUtil.getInstance(this).getBoolean("is_close_open");


        //回显上次点击的状态
        int guang_kong_status = SharedPreferenceUtil.getInstance(this).getInt("guang_kong_status");
        switch (guang_kong_status){
          case  R.id.ll_guan_long_suo:
              //锁屏
              if (is_close_open){
                  iv_guan_long_suo.setImageResource(R.mipmap.wei_xuan_witter_suo);
                  tv_guan_long_suo.setText("已锁屏");

              }else {
                  iv_guan_long_suo.setImageResource(R.mipmap.dian_ji_jie_ping);
                  tv_guan_long_suo.setText("已解屏");
              }
              tv_guan_long_suo.setTextColor(getResources().getColor(R.color.white));

              ll_guan_long_suo.setBackground(getResources().getDrawable(R.drawable.ic_pop_guan_kong_item_one));
            break;
            case  R.id.ll_guan_long_chuan:
                //教师传屏
                ll_guan_long_chuan.setBackground(getResources().getDrawable(R.drawable.ic_pop_guan_kong_item_one));
                iv_guan_long_chuan.setImageResource(R.mipmap.jiao_shi_chuan_select);
                tv_guan_long_chuan.setTextColor(getResources().getColor(R.color.white));
                boolean startTeacher = SharedPreferenceUtil.getInstance(this).getBoolean("startTeacher");
                if (startTeacher){
                    tv_guan_long_chuan.setText("取消传屏");
                }else {
                    tv_guan_long_chuan.setText("教师传屏");

                }
                break;
            case  R.id.ll_guan_long_tou:
                //学生投屏
                ll_guan_long_tou.setBackground(getResources().getDrawable(R.drawable.ic_pop_guan_kong_item_one));
                iv_guan_long_tou.setImageResource(R.mipmap.xueng_sheng_select);
                tv_guan_long_tou.setTextColor(getResources().getColor(R.color.white));
                break;

            case  R.id.ll_guan_long_join:
                //一键加入
                ll_guan_long_join.setBackground(getResources().getDrawable(R.drawable.ic_pop_guan_kong_item_one));

                iv_guan_long_join.setImageResource(R.mipmap.yi_jian_add_select);
                tv_guan_long_join.setTextColor(getResources().getColor(R.color.white));
                break;
            case  R.id.ll_guan_long_close:
                //关机
                ll_guan_long_close.setBackground(getResources().getDrawable(R.drawable.ic_pop_guan_kong_item_one));
                iv_guan_long_close.setImageResource(R.mipmap.yi_jian_close_clect);
                tv_guan_long_close.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.ll_guan_long_suo:
                //锁屏的点击事件
                isClose=!isClose;

                //发送解屏的消息给教师端
                if (!isClose){
                    SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_UnlockScreen,
                            MsgUtils.sendServerInfo(MsgUtils.HEAD_UnlockScreen,""));
                    SharedPreferenceUtil.getInstance(this).putBoolean("is_close_open",false);

                }else {
                    //发送锁屏的消息给教师端
                    SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_LockScreen,
                            MsgUtils.sendServerInfo(MsgUtils.HEAD_LockScreen,""));
                    SharedPreferenceUtil.getInstance(this).putBoolean("is_close_open",true);

                }
               morePop.dismiss();

                //保留点击管控的状态
               SharedPreferenceUtil.getInstance(this).putInt("guang_kong_status",R.id.ll_guan_long_suo);

                break;
            case R.id.ll_guan_long_chuan:
                //教师传屏  是把教师的pc 画面推给学生端
                //必须选中了班级才能开始推流
                if (classInfos.size() > 0) {
                    isClasslesson = !isClasslesson;
                    if (isClasslesson) {

                        if (!DisplayService.Companion.isStreaming()) {

                            startActivityForResult(DisplayService.Companion.sendIntent(), REQUEST_CODE_STREAM);
                            ToastUtils.show("开始上课");
                            SharedPreferenceUtil.getInstance(SplishActivity.this).putBoolean("startTeacher",true);
                        }
                    } else {
                        if (DisplayService.Companion.isStreaming()) {

                            stopService(new Intent(SplishActivity.this, DisplayService.class));
                            // ToastUtils.show("结束上课");

                            //发送结束上课个server
                            //推流成功发送消息
                            SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_Stop_Screen_Cast,
                                    MsgUtils.sendServerInfo(MsgUtils.HEAD_Stop_Screen_Cast,""));

                            SharedPreferenceUtil.getInstance(SplishActivity.this).putBoolean("startTeacher",false);

                        }
                    }

                    //保留点击管控的状态
                    SharedPreferenceUtil.getInstance(this).putInt("guang_kong_status",R.id.ll_guan_long_chuan);
                    morePop.dismiss();
                } else {
                    ToastUtils.show("请先加入授课班级");
                }
                break;
            case R.id.ll_guan_long_tou:
                //   //学生投屏的点击事件

                //保留点击管控的状态
                SharedPreferenceUtil.getInstance(this).putInt("guang_kong_status",R.id.ll_guan_long_tou);
                morePop.dismiss();
                break;
            case R.id.ll_guan_long_close:
                //一键关机的点击事件
                SharedPreferenceUtil.getInstance(this).putInt("guang_kong_status",R.id.ll_guan_long_close);
                morePop.dismiss();
                break;
            case R.id.ll_guan_long_join:
                //一键加入
                //保留点击管控的状态
                SharedPreferenceUtil.getInstance(this).putInt("guang_kong_status",R.id.ll_guan_long_join);
                morePop.dismiss();
                break;
        }
    }
}
