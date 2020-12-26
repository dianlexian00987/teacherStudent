package com.telit.app_teacher.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.encoder.input.gl.SpriteGestureController;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.view.OpenGlView;
import com.telit.app_teacher.Dialog.LuPingDialoge;
import com.telit.app_teacher.R;
import com.telit.app_teacher.customNetty.SimpleClientNetty;
import com.telit.app_teacher.eventbus.EventBus;
import com.telit.app_teacher.utils.Constant;
import com.telit.app_teacher.utils.MsgUtils;


import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.Camera1Base}
 * {@link com.pedro.rtplibrary.rtmp.RtmpCamera1}
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OpenGlRtmpActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback,
        View.OnTouchListener {

    private RtmpCamera1 rtmpCamera1;
    private Button button;
    private Button bRecord;


    private String currentDateAndTime = "";
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/rtmp-rtsp-stream-client-java");
    private OpenGlView openGlView;
    private SpriteGestureController spriteGestureController = new SpriteGestureController();
    private ImageView iv_zan_tai_finsh;
    private ImageView iv_open_ling;
    private ImageView iv_open_zan_tai;
    private ImageView iv_open_lu_ping;
    private TextView tv_open_lu_ping;
    private TextView tv_open_lu_ping_time;
    private Timer timer;
    //记录当前的time
    private int times = 0;
    private static final int TIMES_SEND = 0X100;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMES_SEND:
                    tv_open_lu_ping_time.setText(timeConversion(times));
                    break;
            }
        }
    };
    private TextView tv_open_zan_tai;

    /**
     * 将毫秒转时分秒
     *
     * @param time
     * @return
     */
    public String timeConversion(int time) {
        int hour = 0;
        int minutes = 0;
        int sencond = 0;
        int temp = time % 3600;
        if (time > 3600) {
            hour = time / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    minutes = temp / 60;
                    if (temp % 60 != 0) {
                        sencond = temp % 60;
                    }
                } else {
                    sencond = temp;
                }
            }
        } else {
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        return (hour < 10 ? ("0" + hour) : hour) + ":" + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_open_gl);
        openGlView = findViewById(R.id.surfaceView);
        button = findViewById(R.id.b_start_stop);
        button.setOnClickListener(this);
        bRecord = findViewById(R.id.b_record);
        bRecord.setOnClickListener(this);
        Button switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);

        rtmpCamera1 = new RtmpCamera1(openGlView, this);
        openGlView.getHolder().addCallback(this);
        openGlView.setOnTouchListener(this);

        //换回
        iv_zan_tai_finsh = (ImageView) findViewById(R.id.iv_zan_tai_finsh);
        //灯光
        iv_open_ling = (ImageView) findViewById(R.id.iv_open_ling);
        //开启暂台
        iv_open_zan_tai = (ImageView) findViewById(R.id.iv_open_zan_tai);
        tv_open_zan_tai = (TextView) findViewById(R.id.tv_open_zan_tai);
        //开始录屏
        iv_open_lu_ping = (ImageView) findViewById(R.id.iv_open_lu_ping);
        tv_open_lu_ping = (TextView) findViewById(R.id.tv_open_lu_ping);
        tv_open_lu_ping_time = (TextView) findViewById(R.id.tv_open_lu_ping_time);
        iv_zan_tai_finsh.setOnClickListener(this);
        iv_open_ling.setOnClickListener(this);
        iv_open_zan_tai.setOnClickListener(this);
        iv_open_lu_ping.setOnClickListener(this);

        //注册EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenGlRtmpActivity.this, "Connection success", Toast.LENGTH_SHORT).show();

                //推流成功发送消息
                SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_Screen_Cast, MsgUtils.teacherAddress(Constant.RtmpUrl));

            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenGlRtmpActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                        .show();
                rtmpCamera1.stopStream();
                button.setText(R.string.start_button);
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenGlRtmpActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenGlRtmpActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenGlRtmpActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isLuPing = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_zan_tai_finsh:
                //返回
                finish();
                break;
            case R.id.iv_open_ling:
                //打开手电筒
                boolean light = rtmpCamera1.switchFlashLight();
                iv_open_ling.setImageResource(light ? R.mipmap.pai_zhao_deng_open : R.mipmap.deng_close);
                break;
            case R.id.iv_open_lu_ping:
                //开始录屏视频保存到本地
                isLuPing = !isLuPing;
                iv_open_lu_ping.setImageResource(isLuPing ? R.mipmap.start_lu_ping : R.mipmap.end_lu_ping);
                tv_open_lu_ping.setText(isLuPing ? "录制中..." : "录制视频");
                if (isLuPing) {
                    tv_open_lu_ping_time.setVisibility(View.VISIBLE);
                    //显示录制的时间
                    showReadTime();
                } else {
                    tv_open_lu_ping_time.setVisibility(View.GONE);
                  times=0;
                }
                if (!rtmpCamera1.isRecording()) {
                    try {
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                        currentDateAndTime = sdf.format(new Date());
                        if (!rtmpCamera1.isStreaming()) {
                            if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                                rtmpCamera1.startRecord(
                                        folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                                bRecord.setText(R.string.stop_record);
                                Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error preparing stream, This device cant do it",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            rtmpCamera1.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                            bRecord.setText(R.string.stop_record);
                            Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        rtmpCamera1.stopRecord();
                        bRecord.setText(R.string.start_record);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    rtmpCamera1.stopRecord();
                    bRecord.setText(R.string.start_record);
                    Toast.makeText(this,
                            "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();

                    //结束推流把视频保存到本地和上传到服务端
                    saveVideoLuPing();
                    currentDateAndTime = "";
                    times = 0;
                    if (timer != null){
                      timer.cancel();
                      timer=null;
                    }
                }
                break;

            case R.id.iv_open_zan_tai:
                //开始录屏推流
                if (!rtmpCamera1.isStreaming()) {
                    rtmpCamera1.enableAutoFocus();
                    if (rtmpCamera1.isRecording()
                            || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                        button.setText(R.string.stop_button);
                        //开始推流
                        rtmpCamera1.startStream(Constant.RtmpUrl);
                        iv_open_zan_tai.setImageResource(R.mipmap.end_zhan_tai);
                        tv_open_zan_tai.setText("关闭展台");

                    } else {
                        Toast.makeText(this, "Error preparing stream, This device cant do it",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    button.setText(R.string.start_button);
                    rtmpCamera1.stopStream();
                    iv_open_zan_tai.setImageResource(R.mipmap.start_zhan_tai);
                    tv_open_zan_tai.setText("开启展台");
                    rtmpCamera1.disableAutoFocus();
                }
                break;
            case R.id.switch_camera:
                try {
                    rtmpCamera1.switchCamera();
                } catch (CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;


            default:
                break;
        }
    }

    /**
     * 保存录屏的视频
     */
    private void saveVideoLuPing() {
        LuPingDialoge luPingDialoge = new LuPingDialoge(this, folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
        luPingDialoge.show();
    }


    /**
     * 显示录制的时间
     */
    private void showReadTime() {
        if (timer == null) {

            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // System.out.println("系统正在运行……");
                times++;
                //发送到主线程
                handler.sendEmptyMessage(TIMES_SEND);

            }
        }, 1500, 1000);
        /*当启动定时器后，5s之后开始每隔2s执行一次定时器任务*/
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rtmpCamera1.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (rtmpCamera1.isRecording()) {
            rtmpCamera1.stopRecord();
            bRecord.setText(R.string.start_record);
            Toast.makeText(this,
                    "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            currentDateAndTime = "";
        }
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
            button.setText(getResources().getString(R.string.start_button));
        }
        rtmpCamera1.stopPreview();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (spriteGestureController.spriteTouched(view, motionEvent)) {
            spriteGestureController.moveSprite(view, motionEvent);
            spriteGestureController.scaleSprite(motionEvent);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {

            timer.cancel();
            timer = null;
        }

        //注册EventBus
        EventBus.getDefault().unregister(this);
    }
}