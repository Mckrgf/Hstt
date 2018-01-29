package com.example.yb.hstt.Adpater;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.DisplayUtil;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 图片浏览ViewPageAdapter
 * Created by Jelly on 2016/3/10.
 */
public class ViewPageAdapter extends PagerAdapter {
    private static final String TAG = "ViewPageAdapter";
    private Context context;
    private List<String> images;
    private SparseArray<View> cacheView;
    private ViewGroup containerTemp;
    private int targetW,targetH;

    public ViewPageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        cacheView = new SparseArray(images.size());
        targetW=DisplayUtil.Width(context);
        targetH= SystemAppUtils.getScreenHeight(context);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        if (containerTemp == null) containerTemp = container;

        View view = cacheView.get(position);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.vp_item_image, container, false);
            view.setTag(position);
            final ImageView image = (ImageView) view.findViewById(R.id.image);

            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(image);
            String path = images.get(position);
//            Log.i(TAG, "instantiateItem: " + path);
            Picasso.with(context).load(images.get(position))
//                    .resize(targetW,targetH)
//                    .centerCrop()
                    .into(image);

            photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    Activity activity = (Activity) context;
                    activity.finish();
                }
            });
            cacheView.put(position, view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}
