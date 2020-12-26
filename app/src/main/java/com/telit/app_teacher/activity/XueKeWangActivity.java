package com.telit.app_teacher.activity;


import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.telit.app_teacher.R;
import com.telit.app_teacher.net.Common;

import butterknife.BindView;

public class XueKeWangActivity extends Actvity {
    @BindView(R.id.ll_web)
    LinearLayout ll_web;

    @Override
    protected int getContentLayoutId() {
        return R.layout.home_web_layout;
    }

    @Override
    protected void initData() {
        super.initData();
        String url= Common.Constance.API_URL+ Common.Constance.XUEKEWANG+"?userName=tiyanjs1&schoolId=";
        AgentWeb mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) ll_web, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);
    }
}
