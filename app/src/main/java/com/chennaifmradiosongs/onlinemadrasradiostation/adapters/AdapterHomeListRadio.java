package com.chennaifmradiosongs.onlinemadrasradiostation.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;

import java.util.List;

public class AdapterHomeListRadio extends RecyclerView.Adapter {

    ItemListRadio itemListRadio;
    private int row;
    private Activity activity;
    private List<ItemListRadio> arrayItemListRadio;
    private ImageLoader imgloader = ImageLoader.getInstance();
    private RecyclerViewClickListener recyclerViewClickListener;


    public AdapterHomeListRadio(Activity act, List<ItemListRadio> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.activity = act;
        this.arrayItemListRadio = arrayList;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_list_item,
                parent, false);
        final ViewHolder myViewHolder = new ViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            default:
                ((ViewHolder) holder).title.setText(arrayItemListRadio.get(position).getRadioName());
                try {
                    imgloader.displayImage(arrayItemListRadio.get(position).getRadioImageUrl(), ((ViewHolder) holder).view1);
                    Log.e("imageurl", arrayItemListRadio.get(position).getRadioImageUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((ViewHolder) holder).view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerViewClickListener.onClick(holder.getAdapterPosition());
                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        return arrayItemListRadio.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView view1;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_view_image_title);
            view1 = itemView.findViewById(R.id.card_view_image);
        }
    }
}
