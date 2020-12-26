package com.wildma.idcardcamera.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.adapter.ImageAdapter;
import com.wildma.idcardcamera.cropper.CropImageView;
import com.wildma.idcardcamera.cropper.CropListener;
import com.wildma.idcardcamera.cropper.ScreenShotView;
import com.wildma.idcardcamera.global.ImageUrlBean;
import com.wildma.idcardcamera.utils.CommonUtils;
import com.wildma.idcardcamera.utils.FileUtils;
import com.wildma.idcardcamera.utils.ImageUtils;
import com.wildma.idcardcamera.utils.PermissionUtils;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;


import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${拍照界面}
 */
public class CameraActivity extends Activity implements View.OnClickListener, ImageAdapter.OnItemClickListener {

    private CropImageView mCropImageView;
    private Bitmap mCropBitmap;
    private CameraPreview mCameraPreview;
    private View mLlCameraCropContainer;
    private ImageView mIvCameraCrop;
    private ImageView mIvCameraFlash;
    private View mLlCameraOption;
    private View mLlCameraResult;
    private TextView mViewCameraCropBottom;
    private FrameLayout mFlCameraOption;
    private View mViewCameraCropLeft;

    private int mType;//拍摄类型
    private boolean isToast = true;//是否弹吐司，为了保证for循环只弹一次
    private ImageView iv_camera_get_image;
    private RelativeLayout rl_root_curt;
    private TextView tv_tu_ku;
    private ImageView iv_camera_close_tu_pian;
    private LinearLayout ll_camera_left_layout;
    private int clickNum = 0;
    private ImageView iv_camera_close;
    private LinearLayout ll_check_images;
    private RecyclerView rv_camera_layout;
    private List<ImageUrlBean> imageUrlBeans;
    private String imagePath;
    private LinearLayout ll_picImage_result;
    private LinearLayout ii_duibi_jiangjie;
    private ImageView iv_camera_close_ok;

    private static final int REQUEST_LIST_CODE = 0;
    private static final int REQUEST_CAMERA_CODE = 1;
    private ImageAdapter imageAdapter;
    private ScreenShotView comm_screen_screenshot;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        /*动态请求需要的权限*/
        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(this, IDCardCamera.PERMISSION_CODE_FIRST,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (checkPermissionFirst) {
            init();
        }
    }

    /**
     * 处理请求权限的响应
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 请求权限结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissions = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) { //用户选择了"不再询问"
                    if (isToast) {
                        Toast.makeText(this, "请手动打开该应用需要的权限", Toast.LENGTH_SHORT).show();
                        isToast = false;
                    }
                }
            }
        }
        isToast = true;
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "允许所有权限");
            init();
        } else {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "有权限不允许");
            finish();
        }
    }

    private void init() {
        mType = getIntent().getIntExtra(IDCardCamera.TAKE_TYPE, 0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        initView();
        initListener();
        //从数据库中获取图片
        imageUrlBeans = LitePal.findAll(ImageUrlBean.class);
        initData();


    }

    private void initData() {
        if (imageUrlBeans != null&& imageUrlBeans.size()>0) {

            ll_check_images.setVisibility(View.VISIBLE);
            rv_camera_layout.setVisibility(View.VISIBLE);
            ii_duibi_jiangjie.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_camera_layout.setLayoutManager(linearLayoutManager);
            imageAdapter = new ImageAdapter(imageUrlBeans, this);
            // HomeOneAdapter homeOneAdapter=new HomeOneAdapter(images,names,getContext());
            imageAdapter.setOnItemClickListener(this);
            rv_camera_layout.setOverScrollMode(View.OVER_SCROLL_NEVER);

            rv_camera_layout.setAdapter(imageAdapter);
        }

    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mLlCameraCropContainer = findViewById(R.id.ll_camera_crop_container);
        mIvCameraCrop = (ImageView) findViewById(R.id.iv_camera_crop);
        mIvCameraFlash = (ImageView) findViewById(R.id.iv_camera_flash);
        mLlCameraOption = findViewById(R.id.ll_camera_option);
        mLlCameraResult = findViewById(R.id.ll_camera_result);

        mFlCameraOption = (FrameLayout) findViewById(R.id.fl_camera_option);

        iv_camera_get_image = findViewById(R.id.iv_camera_get_image);
        rl_root_curt = findViewById(R.id.rl_root_curt);
        tv_tu_ku = findViewById(R.id.tv_tu_ku);
        ll_camera_left_layout = findViewById(R.id.ll_camera_left_layout);
        iv_camera_close = findViewById(R.id.iv_camera_close);
        ll_check_images = findViewById(R.id.ll_check_images);
        rv_camera_layout = findViewById(R.id.rv_camera_layout);
        ll_picImage_result = findViewById(R.id.ll_picImage_result);
        ii_duibi_jiangjie = findViewById(R.id.ii_duibi_jiangjie);
        iv_camera_close_ok = findViewById(R.id.iv_camera_close_ok);
        //设置透明度
        ll_check_images.getBackground().setAlpha(100);
        //拍照要裁剪的图
        comm_screen_screenshot = findViewById(R.id.comm_screen_screenshot);




        /*增加0.5秒过渡界面，解决个别手机首次申请权限导致预览界面启动慢的问题*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCameraPreview.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 500);


        iv_camera_get_image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View v) {

                ISNav.getInstance().init(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, String path, ImageView imageView) {
                        Glide.with(context).load(path).into(imageView);
                    }
                });
                //选着图片
                ISListConfig config = new ISListConfig.Builder()
                        .multiSelect(true)
                        // 是否记住上次选中记录
                        .rememberSelected(false)
                        // 使用沉浸式状态栏
                        .statusBarColor(Color.parseColor("#414141")).build();

                ISNav.getInstance().toListActivity(CameraActivity.this, config, REQUEST_LIST_CODE);


            }
        });
    }

    private void initListener() {
        mCameraPreview.setOnClickListener(this);
        mIvCameraFlash.setOnClickListener(this);
        //关闭
        findViewById(R.id.iv_camera_close).setOnClickListener(this);
        findViewById(R.id.iv_camera_take).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_ok).setOnClickListener(this);
        //重拍的点击事件
        findViewById(R.id.iv_camera_result_cancel).setOnClickListener(this);
        //截图的点击事件
        findViewById(R.id.iv_camera_result_jietu).setOnClickListener(this);
        //旋转的点击事件
        findViewById(R.id.iv_camera_result_xuanzhuang).setOnClickListener(this);
        findViewById(R.id.iv_camera_close_ok).setOnClickListener(this);
        findViewById(R.id.ll_picImage_result).setOnClickListener(this);
        //对比讲解的事件
        findViewById(R.id.tv_dui_bi_jiangjie).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_preview) {
            mCameraPreview.focus();
        } else if (id == R.id.iv_camera_close_ok) {
            finish();
        } else if (id == R.id.iv_camera_close) {
            finish();
        } else if (id == R.id.iv_camera_take) {
            //拍照的点击事件
            if (!CommonUtils.isFastClick()) {

                takePhoto();
            }
        } else if (id == R.id.iv_camera_flash) {
            if (CameraUtils.hasFlash(this)) {
                boolean isFlashOn = mCameraPreview.switchFlashLight();
                mIvCameraFlash.setImageResource(isFlashOn ? R.mipmap.camera_flash_on : R.mipmap.camera_flash_off);
            } else {
                Toast.makeText(this, R.string.no_flash, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_camera_result_ok) {
            //拍照完成
            confirm();


        } else if (id == R.id.iv_camera_result_cancel) {
            //重拍的点击事件
            finish();
            Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
            startActivity(intent);

            //setTakePhotoLayout();
        } else if (id == R.id.iv_camera_result_jietu) {
            //截图的点击事件
            cropImage(imagePath);
        } else if (id == R.id.iv_camera_result_xuanzhuang) {
            //旋转的点击事件
            clickNum++;
            rotationImage(90 * (clickNum % 4));
        }else if (id == R.id.ll_picImage_result){
            //全部删除的点击事件
            imageUrlBeans.clear();
            LitePal.deleteAll(ImageUrlBean.class);
            ll_check_images.setVisibility(View.GONE);
        }else if (id == R.id.tv_dui_bi_jiangjie){
            //对比讲解的点击事件
            Intent intent=new Intent(CameraActivity.this,ComparedActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void rotationImage(int darre) {
        mIvCameraCrop.setPivotX(mIvCameraCrop.getWidth() / 2);
        mIvCameraCrop.setPivotY(mIvCameraCrop.getHeight() / 2);//支点在图片中心
        mIvCameraCrop.setRotation(darre);
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        mCameraPreview.setEnabled(false);
        CameraUtils.getCamera().setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] bytes, Camera camera) {
                final Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                camera.stopPreview();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int w = size.width;
                        final int h = size.height;
                        Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, w, h);

                        /*保存图片到sdcard并返回图片路径*/
                        imagePath = new StringBuffer().append(FileUtils.getImageCacheDir(CameraActivity.this))
                                .append(File.separator)
                                .append(System.currentTimeMillis()).append(".jpg").toString();
                        ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG);
                        //显示图片
                        showPicimage(bitmap);
                    }
                }).start();
            }
        });
    }

    //显示图片
    private void showPicimage(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(CameraActivity.this)
                        //.asGif() // 只能加载gif文件
                        .asBitmap() // 将gif作为静态图加载
                        .load(bitmap) //加载图片地址，可以是本地，网络，资源
                        .into(mIvCameraCrop);

                //设置要显示的布局
                setCropLayout();
                //开始截图

            }


        });
    }

    /**
     * 裁剪图片
     */
    public static final int  CROP          = 0x13;//裁剪标记
    @SuppressLint("SourceLockedOrientationActivity")
    private void cropImage(String imagePath) {

   /*     PictureSelector.create(CameraActivity.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .freeStyleCropEnabled(true);// 裁剪框是否可拖拽


        startCrop(imagePath);*/
      /*  Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(imagePath));
                startActivityForResult(  crop(this, photoURI,
                        0, 0, 0, 0), CROP);*/

      ;

    }

    /**
     * 设置裁剪布局
     */
    @SuppressLint("ResourceAsColor")
    private void setCropLayout() {
        mLlCameraResult.setVisibility(View.VISIBLE);
        iv_camera_close_ok.setVisibility(View.VISIBLE);
        //右边图库的文字
        tv_tu_ku.setVisibility(View.GONE);
        //拍照的相机
         mCameraPreview.setVisibility(View.GONE);
        rl_root_curt.setBackgroundColor(getResources().getColor(R.color.color_000000));
        mIvCameraFlash.setVisibility(View.GONE);
        iv_camera_close.setVisibility(View.GONE);
        ll_picImage_result.setVisibility(View.GONE);
        ii_duibi_jiangjie.setVisibility(View.GONE);
        rv_camera_layout.setVisibility(View.GONE);

        iv_camera_get_image.setVisibility(View.GONE);
        mLlCameraOption.setVisibility(View.GONE);
        ll_check_images.setVisibility(View.GONE);

        //剪切的原始图片
        mIvCameraCrop.setVisibility(View.VISIBLE);
        //剪切的图片
        comm_screen_screenshot.setVisibility(View.VISIBLE);
    }

    /**
     * 点击确认，返回图片路径
     */
    private void confirm() {
        //拍照好图片的返回
        //把图片保存到数据库
        ImageUrlBean imageUrlBean = new ImageUrlBean();
        imageUrlBean.setGetUrl(imagePath);
        imageUrlBean.save();
        finish();

        Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraPreview != null) {
            mCameraPreview.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraPreview != null) {
            mCameraPreview.onStop();
        }
    }
    @Override
    public void onItemClick(View view, int position) {
        imageUrlBeans = LitePal.findAll(ImageUrlBean.class);
        imageUrlBeans.get(position).delete();
        Log.i("", "onItemClick: "+position);
        imageUrlBeans.remove(position);
       //int pos= LitePal.delete(ImageUrlBean.class,position);

        if (imageUrlBeans.size()==0){
            ll_check_images.setVisibility(View.GONE);
        }else {
            initData();
        }


    }
    public static final String INTENT_RESULT = "result";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== REQUEST_LIST_CODE && resultCode == RESULT_OK){
            ArrayList<String> imagesUrls = data.getStringArrayListExtra(INTENT_RESULT);
            for (String image:imagesUrls) {
                ImageUrlBean imageUrlBean = new ImageUrlBean();
                imageUrlBean.setGetUrl(image);
                imageUrlBean.save();
            }

            init();
        }

    }
}