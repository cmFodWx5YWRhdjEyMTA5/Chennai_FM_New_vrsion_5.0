package com.chennaifmradiosongs.onlinemadrasradiostation.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;

import java.util.List;

public class AdapterGridRadio extends ArrayAdapter<ItemListRadio> {

    private ImageLoader imgloader = ImageLoader.getInstance();
    private int row;
    private Activity activity;
    private List<ItemListRadio> arrayItemListRadio;


    public AdapterGridRadio(Activity act, int resource, List<ItemListRadio> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.arrayItemListRadio = arrayList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view = inflater.inflate(row, null);
                holder = new ViewHolder();
                view.setTag(holder);
            }
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((arrayItemListRadio == null) || ((position + 1) > arrayItemListRadio.size()))
            //noinspection ConstantConditions
            return view;

        ItemListRadio itemListRadio = arrayItemListRadio.get(position);

        if (holder != null) {
            holder.textView = view.findViewById(R.id.card_view_image_title);
            holder.cat = view.findViewById(R.id.card_view_image_cat);
            holder.view = view.findViewById(R.id.card_view_image);
            imgloader.displayImage(arrayItemListRadio.get(position).getRadioImageUrl(), holder.view);
            holder.textView.setText(arrayItemListRadio.get(position).getRadioName());
            holder.cat.setText(arrayItemListRadio.get(position).getRadioCateogory());
        }

        //noinspection ConstantConditions
        return view;
    }

    private class ViewHolder {
        TextView textView, cat;
        ImageView view;
    }
}
