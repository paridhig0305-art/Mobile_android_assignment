package com.example.gallery;
import com.example.gallery.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<File> imageList;

    public ImageAdapter(Context context, ArrayList<File> imageList) {
        this.context   = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() { return imageList.size(); }

    @Override
    public Object getItem(int position) { return imageList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load image as thumbnail
        Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(position).getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        return imageView;
    }
}
