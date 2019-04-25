package com.chennaifmradiosongs.onlinemadrasradiostation.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.CommonRSSItem;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CommonNewsAdapter extends PagerAdapter {
    Context context;
    List<CommonRSSItem> rssItemList;
    int pos;

    public CommonNewsAdapter(Context contexts, List<CommonRSSItem> rssItemList, int i) {
        this.context = contexts;
        this.rssItemList = rssItemList;
        this.pos = i;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.layout_custom_news_frame, collection, false);
        collection.addView(layout);
        ViewHolder holder = null;
        holder = new ViewHolder();
        holder.date = layout.findViewById(R.id.news_update);
        holder.news_desc = layout.findViewById(R.id.news_description);
        holder.news_title = layout.findViewById(R.id.news_title);
        holder.roundedImageView = layout.findViewById(R.id.user_images);


        if (pos == 1) {
            holder.roundedImageView.setImageResource(R.drawable.tamilnews);
        } else if (pos == 2) {
            holder.roundedImageView.setImageResource(R.drawable.worldnews);
        }
        holder.news_title.setText(rssItemList.get(position).title);
        holder.date.setText(rssItemList.get(position).pubdate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.news_desc.setText(Html.fromHtml(rssItemList.get(position).description, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.news_desc.setText(Html.fromHtml(rssItemList.get(position).description));
        }

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
        RoundedImageView roundedImageView;
        TextView news_title, news_desc, date;
    }
}