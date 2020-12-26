package com.telit.app_teacher.utils;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * author: qzx
 * Date: 2019/4/28 11:40
 */
public class MsgUtils {
    public static final String SEPARATOR = " ";//分隔符

    public static final String HEAD_HEART = "HeartBeat";//心跳

    public static final String HEAD_ACKNOWLEDGE = "TPadAcknowledgement";//消息反馈

    public static final String HEAD_JOINCLASS = "TPadRegister";//连接服务成功后加入班级


    public static final String HEAD_OUT_OF_CLASS = "OutOfClass";//退出登录时引用



    public static final String HEAD_Screen_Cast="ScreenCast"; //教师端pad推流实物展台
    public static final String HEAD_Stop_Screen_Cast="StopScreenCast"; //发送教师端pad结束推流实物展台
    public static final String HEAD_StopPadScreenCast="StopPadScreenCast"; //接收教师端pad结束推流实物展台

    public static final String HEAD_LockScreen="LockScreen"; //教师端pad锁屏
    public static final String HEAD_UnlockScreen="UnlockScreen"; //教师端pad解屏


    public static final String HEAD_STOP_BROADCAST = "StopScreenbroadcast";//停止屏幕广播

    public static final String HEAD_BROADCAST = "Screenbroadcast";//开始屏幕广播
    public static final String HEAD_ScreenIp = "ScreenIp";//获取本地推流的ip 地址




    /**
     * @describe 消息反馈
     * @author luxun
     * create at 2017/5/5 0005 17:21
     * <p>
     * 注意：这个uuid是来自服务端带过来的,服务端去重,这个不要
     */
    public static String createAcknowledge(String fromServerUUID) {
        return HEAD_ACKNOWLEDGE + SEPARATOR + fromServerUUID + "\r\n";
    }

    /**
     * @describe 心跳信息
     * @author luxun
     * create at 2017/3/21 16:51
     */
    public static String heartMsg() {
//        QZXTools.logE("heartMsg=" + UserUtils.getStudentId(), null);
        return HEAD_HEART + SEPARATOR + MsgUtils.uuid() + "\r\n";
    }

    /**
     * @describe 退出登录命令
     */
    public static String outOfClass() {
        return HEAD_OUT_OF_CLASS + SEPARATOR + uuid() +
                SEPARATOR+ "\r\n";
    }

    /**
     * 加入班级
     *
     * @param isReconnected 是否重连
     */
    public static String joinClass(boolean isReconnected) {


        return HEAD_JOINCLASS + SEPARATOR + uuid() + SEPARATOR +"66666864384" + "\r\n";

    }


    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }



    /**
     * 新版的讨论内容发送给服务端
     * 示例：Discuss SequenceNumber GroupId StudentName Content/r/n
     */
    public static String createNewDiscuss() {
        StringBuffer stringBuffer = new StringBuffer();

        return "";
    }

    /*
     * 教师端推送地址
     *
     * */

    public static String teacherAddress(String url) {
        return HEAD_Screen_Cast + SEPARATOR + uuid() +
                SEPARATOR + url + "\r\n";
    }
    /*
    *
    * 教师端推送地址结束
    * */
    public static String stopTeacherAddress(){
        return HEAD_Stop_Screen_Cast + SEPARATOR + uuid() +
                SEPARATOR +  "\r\n";
    }

    /**
     * 添加发送给教师端的信息
     * @param headInfo
     * @return
     */
    public static String sendServerInfo(String headInfo,String info){
      return headInfo+  SEPARATOR + uuid() +
              SEPARATOR + info+ "\r\n";
    }


}
