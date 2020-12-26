package com.telit.app_teacher.net;

/**
 * @author qiujuer
 */

public class Common {
    /**
     * 一些不可变的永恒的参数
     * 通常用于一些配置
     */
    public interface Constance {
        // 基础的网络请求地址
       // String API_URL = "http://172.16.5.36:8080/api/";
     //   String API_URL = "http://172.16.5.11:8089/kindergarten/";
        //String API_URL = "http://172.16.5.11:8800/kindergarten/";
        //String API_URL = "http://172.16.5.11:8443/app/";
       // String API_URL = "http://172.16.4.136:8080";
        String API_URL = "http://resource.ahtelit.com";

        //上传微课的视频到服务端
         String UPWEILEVIDEO=API_URL+"/api/v3/disk/createOrUpdateFolder";
         //获取作业的接口
        String HOME_WORK_WEB="/api/v3/homework/toIndex?";
        //省平台资源
        String SHENG_PING_TAI="/wisdomclass/interface/resource/index";
        //学科网资源
        String XUEKEWANG="/xkw/oauth/authentication";
        //我的云盘
        String MYPAN="/api/v3/disk/index";


    }
}
