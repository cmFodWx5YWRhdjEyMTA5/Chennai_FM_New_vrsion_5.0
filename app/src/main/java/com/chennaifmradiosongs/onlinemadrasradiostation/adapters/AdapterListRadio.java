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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterListRadio extends ArrayAdapter<ItemListRadio> {

    public ArrayList<ItemListRadio> arraylist;
    private ImageLoader imgloader = ImageLoader.getInstance();
    private int row;
    private Activity activity;
    private List<ItemListRadio> arrayItemListRadio;

    public AdapterListRadio(Activity act, int resource, List<ItemListRadio> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.arrayItemListRadio = arrayList;

        arraylist = new ArrayList<ItemListRadio>();
        arraylist.addAll(arrayItemListRadio);

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
            holder.view = view.findViewById(R.id.card_view_image);
            imgloader.displayImage(arrayItemListRadio.get(position).getRadioImageUrl(), holder.view);
            holder.textView.setText(arrayItemListRadio.get(position).getRadioId()+"  "+arrayItemListRadio.get(position).getRadioName());
        }
        return view;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        arrayItemListRadio.clear();
        if (charText.length() == 0) {
            arrayItemListRadio.addAll(arraylist);

        } else {
            for (ItemListRadio postDetail : arraylist) {
                if (postDetail.getRadioName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayItemListRadio.add(postDetail);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        ImageView view;
        TextView textView;
    }
}
