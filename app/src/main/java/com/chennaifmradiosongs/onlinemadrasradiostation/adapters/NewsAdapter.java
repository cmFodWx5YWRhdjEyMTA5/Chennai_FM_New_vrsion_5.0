package com.chennaifmradiosongs.onlinemadrasradiostation.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.RSSItem;

import java.util.List;

public class NewsAdapter extends PagerAdapter {
    Context context;
    List<RSSItem> rssItemList;

    private ImageLoader imgloader = ImageLoader.getInstance();

    public NewsAdapter(Context contexts, List<RSSItem> rssItemList) {
        this.context = contexts;
        this.rssItemList = rssItemList;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.layout_news_frame, collection, false);
        collection.addView(layout);
        ViewHolder holder = null;
        holder = new ViewHolder();
        holder.date = layout.findViewById(R.id.news_update);
        holder.news_desc = layout.findViewById(R.id.news_description);
        holder.news_title = layout.findViewById(R.id.news_title);
        holder.user_image = layout.findViewById(R.id.user_images);


        holder.news_title.setText(rssItemList.get(position).title);
        holder.news_desc.setText(rssItemList.get(position).description);
        holder.date.setText(rssItemList.get(position).pubdate);

        Picasso.with(context)
                .load(rssItemList.get(position).images)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.user_image);
        return layout;
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return rssItemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    /*private view holder class*/
    private class ViewHolder {
        RoundedImageView user_image;
        TextView news_title, news_desc, date;
    }
}