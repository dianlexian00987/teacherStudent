package com.wildma.idcardcamera.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.adapter.PreviewImageAdapter;
import com.wildma.idcardcamera.global.ImageUrlBean;

import org.litepal.LitePal;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class FullImageActivity extends Activity implements View.OnClickListener {

    private RecyclerView rv_full_image;
    private List<ImageUrlBean> imageUrlBeans;
    private LinearLayoutManager linearLayoutManager;
    private TextView tv_full_title;
    private LinearLayout ll_full_finish;
    private PreviewImageAdapter previewImageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_layout);

        initView();
        initData();
    }

    private void initData() {
        //从数据库中获取图片
        imageUrlBeans = LitePal.findAll(ImageUrlBean.class);
        initViewPager();
        int position = getIntent().getIntExtra("position", 0);
        rv_full_image.scrollToPosition(position);
        tv_full_title.setText((position + 1) + "/" + imageUrlBeans.size());
    }

    private void initViewPager() {
        rv_full_image.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        linearLayoutManager = (LinearLayoutManager) rv_full_image.getLayoutManager();
        previewImageAdapter = new PreviewImageAdapter(this, imageUrlBeans);
        rv_full_image.setAdapter(previewImageAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_full_image);


        rv_full_image.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = linearLayoutManager.findLastVisibleItemPosition();

                    tv_full_title.setText((position + 1) + "/" + imageUrlBeans.size());

                    previewImageAdapter.onChangePage();

                }
            }
        });
    }

    private void initView() {
        rv_full_image = (RecyclerView) findViewById(R.id.rv_full_image);
        tv_full_title = (TextView) findViewById(R.id.tv_full_title);
        ll_full_finish = (LinearLayout) findViewById(R.id.ll_full_finish);
        ll_full_finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       int id= v.getId();
        if (id== R.id.ll_full_finish) {
            finish();
        }
    }

}
