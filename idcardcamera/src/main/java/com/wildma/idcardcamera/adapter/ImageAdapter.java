package com.wildma.idcardcamera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.global.ImageUrlBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter {
    private List<ImageUrlBean> images;

    private Context context;
    private ViewHolder viewHolder;


    private  ImageView check_item_img;
    private  ImageView tv_del;

    private OnItemClickListener listener;

    public ImageAdapter(List<ImageUrlBean> images, Context context) {

        this.images = images;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_check, parent, false);
        // 实例化viewholder
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Glide.with(context)
                .load(images.get(position).getGetUrl())
                .into(check_item_img);

        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onItemClick(tv_del,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            check_item_img = itemView.findViewById(R.id.check_item_img);
            tv_del = itemView.findViewById(R.id.tv_del);



        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        this.listener = listener;
    }
}
