package com.telit.app_teacher.activity;

import com.telit.app_teacher.R;
import com.wildma.idcardcamera.cropper.PaletteView;

public class PaleTeViewActivity extends Actvity {

    private PaletteView new_color_board_view;

    @Override
    protected int getContentLayoutId() {
        return R.layout.palete_activity_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        new_color_board_view = (PaletteView) findViewById(R.id.new_color_board_view);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
