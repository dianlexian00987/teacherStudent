package com.wildma.idcardcamera.camera;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.adapter.ComparedAdapter;
import com.wildma.idcardcamera.cropper.PaletteView;
import com.wildma.idcardcamera.global.ImageUrlBean;
import com.wildma.idcardcamera.utils.SharedPreferenceUtil;

import org.litepal.LitePal;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ComparedActivity extends AppCompatActivity implements View.OnClickListener, ComparedAdapter.OnItemClickListener {
    private  LinearLayout ll_bottom_check;
    private RecyclerView rv_campare_dui_bi;
    private RecyclerView rv_campare_dui_bi1;
    private LinearLayout ll_dui_pi_zu;
    private PopupWindow morePop;
    private ImageView iv_dui_pi_zu;
    private TextView tv_dui_pi_zu;
    private boolean isPizu=false;
    private FrameLayout fl_hua_plane;
    private ImageView iv_dui_bi_back;
    private ComparedAdapter adapter;
    private List<ImageUrlBean> imageUrlBeans;
    private LinearLayout ll_dui_check;
    private ImageView iv_dui_check;
    private TextView tv_dui_check;
    private boolean isCheck=false;
    private boolean isBanCa=false;
    private LinearLayout ll_dui_cha_ban;
    private ImageView iv_dui_cha_ban;
    private TextView tv_dui_cha_ban;
    private PaletteView new_color_board_view;
    private FrameLayout fl_hua_plane_red;
    private FrameLayout fl_hua_plane_bule;
    private FrameLayout fl_hua_plane_yellow;
    private TextView tv_red;
    private TextView tv_bule;
    private TextView tv_yellow;
    private int parth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.campare_layout);

        initView();
        initData();


    }

    private void initData() {
        //从数据库中获取图片
        imageUrlBeans = LitePal.findAll(ImageUrlBean.class);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        rv_campare_dui_bi1.setLayoutManager(gridLayoutManager);
        adapter = new ComparedAdapter(this,imageUrlBeans);
        rv_campare_dui_bi1.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }


    private void initView() {
         ll_bottom_check=(LinearLayout) findViewById(R.id.ll_bottom_check);
        rv_campare_dui_bi1 = (RecyclerView) findViewById(R.id.rv_campare_dui_bi);
        ll_bottom_check.setBackgroundColor(getResources().getColor(R.color.color_000000));
        ll_bottom_check.getBackground().setAlpha(100);
        //批注的点击事件
        ll_dui_pi_zu = (LinearLayout) findViewById(R.id.ll_dui_pi_zu);
        iv_dui_pi_zu = (ImageView) findViewById(R.id.iv_dui_pi_zu);
        tv_dui_pi_zu = (TextView) findViewById(R.id.tv_dui_pi_zu);
        iv_dui_bi_back = (ImageView) findViewById(R.id.iv_dui_bi_back);
        iv_dui_bi_back.setOnClickListener(this);
        ll_dui_pi_zu.setOnClickListener(this);
        //选择的事件
        ll_dui_check = (LinearLayout) findViewById(R.id.ll_dui_check);
        iv_dui_check = (ImageView) findViewById(R.id.iv_dui_check);
        tv_dui_check = (TextView) findViewById(R.id.tv_dui_check);
        ll_dui_check.setOnClickListener(this);
        //黑板檫
        ll_dui_cha_ban = (LinearLayout) findViewById(R.id.ll_dui_cha_ban);
        iv_dui_cha_ban = (ImageView) findViewById(R.id.iv_dui_cha_ban);
        tv_dui_cha_ban = (TextView) findViewById(R.id.tv_dui_cha_ban);
        ll_dui_cha_ban.setOnClickListener(this);

        //批注的布局
        new_color_board_view = (PaletteView) findViewById(R.id.new_color_board_view);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_dui_pi_zu) {
            //批注的点击事件
            isPizu=!isPizu;
            if (isPizu){
                poppizuPan(v);
                iv_dui_pi_zu.setImageResource(R.mipmap.check_pi_zu);
                tv_dui_pi_zu.setTextColor(getResources().getColor(R.color.color_pi_zu));
                //批注的界面
                new_color_board_view.setVisibility(View.VISIBLE);

                //这个时候已经选择了
                isCheck=true;
            }else {
                isCheck=false;
            }
        }else if (id ==  R.id.iv_dui_bi_back){
            finish();
        }else if (id ==  R.id.ll_dui_check){
           //选着的点击事件
            isCheck=!isCheck;
            if (isCheck){

                iv_dui_check.setImageResource(R.mipmap.check_xuanzhe);
                tv_dui_check.setTextColor(getResources().getColor(R.color.color_pi_zu));
                //批注的界面
                new_color_board_view.setVisibility(View.VISIBLE);
            }else {
                iv_dui_check.setImageResource(R.mipmap.pizu_check);
                tv_dui_check.setTextColor(getResources().getColor(R.color.color_afff));


                //批注的界面
                iv_dui_pi_zu.setImageResource(R.mipmap.duibi_pizu);
                tv_dui_pi_zu.setTextColor(getResources().getColor(R.color.color_afff));

                //板擦的界面
                iv_dui_cha_ban.setImageResource(R.mipmap.jiang_jie_ban_ca);
                tv_dui_cha_ban.setTextColor(getResources().getColor(R.color.color_afff));
                //正常模式
                new_color_board_view.setMode(PaletteView.Mode.DRAW);

                //批注的界面
                new_color_board_view.setVisibility(View.GONE);
                //初始化模式
                isCheck=false;
                isBanCa=false;
                isPizu=false;
            }
        }else if (id ==  R.id.ll_dui_cha_ban){
            //黑板檫的点击事件
            isBanCa=!isBanCa;
            if (isBanCa){

                iv_dui_cha_ban.setImageResource(R.mipmap.check_ban_cha);
                tv_dui_cha_ban.setTextColor(getResources().getColor(R.color.color_pi_zu));
                new_color_board_view.setMode(PaletteView.Mode.ERASER);
            }else {
                iv_dui_cha_ban.setImageResource(R.mipmap.jiang_jie_ban_ca);
                tv_dui_cha_ban.setTextColor(getResources().getColor(R.color.color_afff));
                //正常模式
                new_color_board_view.setMode(PaletteView.Mode.DRAW);
            }
        }else if (id == R.id.fl_hua_plane_red){
            //选着红色画笔
            tv_red.setVisibility(View.VISIBLE);
            tv_bule.setVisibility(View.GONE);
            tv_yellow.setVisibility(View.GONE);
            new_color_board_view.setPenColor(R.color.preview_mock);
            SharedPreferenceUtil.getInstance(this).setInt("parth",R.color.preview_mock);

        }else if (id == R.id.fl_hua_plane_bule){
            //选着陆色画笔
            tv_red.setVisibility(View.GONE);
            tv_bule.setVisibility(View.VISIBLE);
            tv_yellow.setVisibility(View.GONE);
            new_color_board_view.setPenColor(R.color.preview_mock_bule);
            SharedPreferenceUtil.getInstance(this).setInt("parth",R.color.preview_mock_bule);


        }else if (id == R.id.fl_hua_plane_yellow){
            //选着黄色画笔
            tv_red.setVisibility(View.GONE);
            tv_bule.setVisibility(View.GONE);
            tv_yellow.setVisibility(View.VISIBLE);
            new_color_board_view.setPenColor(R.color.preview_mock_yellow);
            SharedPreferenceUtil.getInstance(this).setInt("parth",R.color.preview_mock_yellow);


        }

    }

    private void poppizuPan(View v) {
        if (morePop != null) {
            morePop.dismiss();
        }
        View moreView = LayoutInflater.from(this).inflate(R.layout.pop_white_board_pi_zu_vertical_layout, null);
        fl_hua_plane_red = (FrameLayout) moreView.findViewById(R.id.fl_hua_plane_red);
        fl_hua_plane_bule = (FrameLayout) moreView.findViewById(R.id.fl_hua_plane_bule);
        fl_hua_plane_yellow = (FrameLayout) moreView.findViewById(R.id.fl_hua_plane_yellow);
        //里面的小方块
        tv_red = (TextView) moreView.findViewById(R.id.tv_red);
        tv_bule = (TextView) moreView.findViewById(R.id.tv_bule);
        tv_yellow = (TextView) moreView.findViewById(R.id.tv_yellow);

        //打开画板
        fl_hua_plane_red.setOnClickListener(this);
        fl_hua_plane_bule.setOnClickListener(this);
        fl_hua_plane_yellow.setOnClickListener(this);
        int moreWidth = getResources().getDimensionPixelSize(R.dimen.y388);
        int moreHeight = getResources().getDimensionPixelSize(R.dimen.x88);
        morePop = new PopupWindow(moreView, moreWidth, moreHeight);
        morePop.setBackgroundDrawable(new ColorDrawable());
        morePop.setOutsideTouchable(true);


        //这里粗劣计算头布局的高度一半
        int offsetHeight = getResources().getDimensionPixelSize(R.dimen.y388)/2;
        morePop.showAsDropDown(v, -offsetHeight-20, -15);
        //默认设置画笔字体的大小和颜色

        parth = SharedPreferenceUtil.getInstance(this).getInt("parth", R.color.preview_mock);
        new_color_board_view.setPenColor(parth);

        if (parth == R.color.preview_mock){
            tv_red.setVisibility(View.VISIBLE);
            tv_bule.setVisibility(View.GONE);
            tv_yellow.setVisibility(View.GONE);
        }else if (parth == R.color.preview_mock_bule){
            tv_red.setVisibility(View.GONE);
            tv_bule.setVisibility(View.VISIBLE);
            tv_yellow.setVisibility(View.GONE);
        }else if (parth == R.color.preview_mock_yellow){
            tv_red.setVisibility(View.GONE);
            tv_bule.setVisibility(View.GONE);
            tv_yellow.setVisibility(View.VISIBLE);
        }
    }
    private void rotationImage(int darre,ImageView mIvCameraCrop) {
        mIvCameraCrop.setPivotX(mIvCameraCrop.getWidth() / 2);
        mIvCameraCrop.setPivotY(mIvCameraCrop.getHeight() / 2);//支点在图片中心
        mIvCameraCrop.setRotation(darre);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(ImageView view, int position) {
        adapter.notifyItemChanged(position);
    }
}
