package com.wildma.idcardcamera.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.global.ImageUrlBean;


import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ProjectName：ImageSelector-master
 * PackageName：com.donkingliang.imageselector.adapter
 * Author：wenjie
 * Date：2018-06-14 18:31
 * Description：
 */
public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.ImageHolder> {

    private Context mContext;
    private List<ImageUrlBean> mImgList;
    private int currentPosition=0;

    public void onChangePage() {
        currentPosition=0;
    }


    public interface OnItemClcikLitener {
        void OnItemClcik(PreviewImageAdapter previewImageAdapter, View iteView, int position);
    }

    public OnItemClcikLitener onItemClcikLitener;

    public void setOnItemClcikLitener(OnItemClcikLitener onItemClcikLitener) {
        this.onItemClcikLitener = onItemClcikLitener;
    }

    public PreviewImageAdapter(Context mContext, List<ImageUrlBean> mImgList) {
        this.mContext = mContext;
        this.mImgList = mImgList;
    }



    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ImageHolder imageHolder = new ImageHolder(LayoutInflater.from(mContext).inflate(R.layout.preview_item, parent, false));
        imageHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (onItemClcikLitener != null){
                        onItemClcikLitener.OnItemClcik(PreviewImageAdapter.this , imageHolder.itemView , imageHolder.getLayoutPosition());
                    }
            }
        });


        return imageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageHolder holder, final int position) {

        holder.imageView.setTag(position);
        String path = mImgList.get(position).getGetUrl();

        Glide.with(mContext)
                .load(path)
                .into(holder.imageView);
        //todo 图片的旋转有问题
        holder.item_full_xuan_zhuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition++;
             int pos= (int) holder.imageView.getTag();
                if (position == pos){
                    holder.imageView.animate().rotation(currentPosition%2==0?0:90);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView item_full_xuan_zhuan;

        ImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_itemimg);
            item_full_xuan_zhuan = itemView.findViewById(R.id.item_full_xuan_zhuan);
        }
    }


}
