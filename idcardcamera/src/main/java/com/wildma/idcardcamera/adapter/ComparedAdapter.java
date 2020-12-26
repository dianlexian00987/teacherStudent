package com.wildma.idcardcamera.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.camera.ComparedActivity;
import com.wildma.idcardcamera.camera.FullImageActivity;
import com.wildma.idcardcamera.cropper.RotateTransformation;
import com.wildma.idcardcamera.global.ImageUrlBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ComparedAdapter extends RecyclerView.Adapter {

    private Context context;
    private ViewHolder viewHolder;


    private OnItemClickListener listener;
    private LinearLayout ll_compared_item;

    private ImageView iv_dui_bi_max;

    private List<ImageUrlBean> imageUrlBeans;

    public ComparedAdapter(Context context, List<ImageUrlBean> imageUrlBeans) {

        this.imageUrlBeans = imageUrlBeans;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compared_adapter, parent, false);
        // 实例化viewholder
        viewHolder = new ViewHolder(v);
      // viewHolder.setIsRecyclable(false);
        return viewHolder;
    }



    //旋转
    private boolean isClick=false;

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
         // holder.itemView.setTag(position);
        if (position % 2 != 0) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ll_compared_item.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            ll_compared_item.setLayoutParams(lp);
        }
        //旋转的点击事件
       /* if (currentIndexUrl == position){
            //旋转 todo 旋转有问题
           rotationImage();

        }else {
           // iv_compared_item.animate().rotation(0);

        }
     */
        //正常绑定数据逻辑
        if (isClick) {
            viewHolder.iv_compared_item.animate().rotation(90);
        }

           // rotationImage();


        Glide.with(context)
                .load(imageUrlBeans.get(position).getGetUrl())
                .into(viewHolder.iv_compared_item);

        viewHolder.item_dui_bi_xuan_zhuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notifyItemChanged(position,"payload");
                isClick=true;
                if (listener!=null)listener.onItemClick(viewHolder.item_dui_bi_xuan_zhuan,position);

            }
        });
        //全屏的点击事件
        iv_dui_bi_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    //listener.onItemClickMax(iv_compared_item, position);
                    Intent intent=new  Intent(context, FullImageActivity.class);
                    intent.putExtra("position",position);
                    context.startActivity(intent);
                }
            }
        });


       viewHolder. iv_compared_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new  Intent(context, FullImageActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });


    }

    private void rotationImage() {
      /*  iv_compared_item.setPivotX(iv_compared_item.getWidth()/2);
        iv_compared_item.setPivotY(iv_compared_item.getHeight()/2);//支点在图片中心
        iv_compared_item.setRotation(90);*/

       // iv_compared_item.animate().rotation(90);



        // 第二个参数"rotation"表明要执行旋转
        // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
        ObjectAnimator anim = ObjectAnimator.ofFloat(viewHolder.iv_compared_item, "rotation", 0f, 90f);

        // 动画的持续时间，执行多久？
        anim.setDuration(500);

        // 回调监听
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                Log.d("zhangphil", value + "");
            }
        });

        // 正式开始启动执行动画
        anim.start();

    }

    @Override
    public int getItemCount() {
        return imageUrlBeans.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView item_dui_bi_xuan_zhuan;
        private ImageView iv_compared_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_compared_item = itemView.findViewById(R.id.ll_compared_item);
            iv_compared_item = itemView.findViewById(R.id.iv_compared_item);
            item_dui_bi_xuan_zhuan = itemView.findViewById(R.id.item_dui_bi_xuan_zhuan);
            iv_dui_bi_max = itemView.findViewById(R.id.iv_dui_bi_max);


        }
    }

    public interface OnItemClickListener {

        void onItemClick(ImageView view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }
}
