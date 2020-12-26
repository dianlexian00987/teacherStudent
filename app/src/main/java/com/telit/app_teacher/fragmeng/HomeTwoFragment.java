package com.telit.app_teacher.fragmeng;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.telit.app_teacher.R;
import com.telit.app_teacher.activity.Fragment;
import com.telit.app_teacher.activity.HomeworkWebActivity;
import com.telit.app_teacher.activity.ShengPingTaiActivity;
import com.telit.app_teacher.activity.XueKeWangActivity;
import com.telit.app_teacher.adapter.HomeOneAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class HomeTwoFragment extends Fragment implements HomeOneAdapter.OnItemClickListener {
    @BindView(R.id.rv_home_two)
    RecyclerView rv_home_two;

    private List<Integer> images=new ArrayList<>();
    private List<String> names=new ArrayList<>();
    private int [] ints={
            R.mipmap.dianzibaiban,
            R.mipmap.paizhaijiangjie,
            R.mipmap.zhantai,
            R.mipmap.pingjia,
            R.mipmap.weike
    };
    private String isOnLion="offLine";
    @Override
    protected int getContentLayoutId() {
        return R.layout.home_two_fragment;
    }



    @Override
    protected void initData() {
        super.initData();
        for (int indeex : ints){
            images.add(indeex);
        }
        names.add("作业");
        names.add("省平台资源");
        names.add("学科网资源");
        names.add("云盘");
        names.add("书架");
        // 设置布局管理器
        rv_home_two.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // 设置adapter
        HomeOneAdapter homeOneAdapter=new HomeOneAdapter(images,names,getContext());
        homeOneAdapter.setOnItemClickListener(this);
        rv_home_two.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rv_home_two.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(15, 15, 15, 15);
            }
        });
        rv_home_two.setAdapter(homeOneAdapter);
        // rv_home_one.setAdapter();
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position){
            case 0:
                //作业

                Intent intent=new Intent(getContext(), HomeworkWebActivity.class);
                startActivity(intent);


                break;
            case 1:
                //省平台资源
                Intent intent1=new Intent(getContext(), ShengPingTaiActivity.class);
                startActivity(intent1);
                break;
            case 2:
                //学科网资源
                Intent intent2=new Intent(getContext(), XueKeWangActivity.class);
                startActivity(intent2);
                break;
            case 3:
                //云盘
                Intent intent3=new Intent(getContext(), XueKeWangActivity.class);
                startActivity(intent3);
                break;
            case 4:
                break;
        }
    }
}
