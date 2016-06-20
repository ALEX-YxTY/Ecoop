package com.milai.ecoop.view;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;


import com.bigkoo.convenientbanner.holder.Holder;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016/1/18 0018.
 */
public class ImageHolderView implements Holder<String> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        Picasso.with(context)
                .load(data)
                .into(imageView);
    }
}
