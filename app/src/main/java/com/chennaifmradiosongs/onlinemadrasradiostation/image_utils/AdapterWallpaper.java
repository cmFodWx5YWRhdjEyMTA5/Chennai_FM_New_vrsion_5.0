package com.chennaifmradiosongs.onlinemadrasradiostation.image_utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;
import com.chennaifmradiosongs.onlinemadrasradiostation.AppInstallNativeAdViewHolder;
import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;

import java.util.ArrayList;
import java.util.List;


public class AdapterWallpaper extends RecyclerView.Adapter {

    private static final int AD_VIEW_TYPE = 2;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final String[] users = new String[]{"Aadhini", "Aathi", "Amaravati", "Ankavi", "Anpumozhi", "Arulvezhi", "Aadhi",
            "Anbarasan", "Anbushelvan", "Anpalahan", "Boopalan", "Bharani", "Chentamilan", "Geerthi", "Chelliyan", "Eelavarasi",
            "Elakkieya", "Ellilmozhi", "Ellilazhahi", "Elilarasan", "Gagan", "Gagnesh", "Jai Darsh", "Kaamik", "Kanishk", "Kaushik",
            "Lenisha", "Maarish", "Nagila", "Nancy", "Nanda", "Omesh", "Onish", "Saanjali", "Sadhya", "Sabarish", "Sabita", "Saarik",
            "Saarth", "Saathvi", "Sabarish", "Taalish", "Taamas", "Taanish", "Tamilan", "Tanishk", "Tamil Selvi", "Ujwani", "Urvisha", "Vaahila"};
    private final List data;
    private ArrayList<String> bg_array;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;


    public AdapterWallpaper(Context context, List<Object> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.data = arrayList;
        this.context = context;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.vid_cell_admob,
                        parent, false);
                return new AppInstallNativeAdViewHolder(unifiedNativeLayoutView);
            case VIEW_PROG:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_layout_progressbar,
                        parent, false);
                return new ProgressViewHolder(v);
            default:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_layout_image_wall,
                        parent, false);
                final MyViewHolder myViewHolder = new MyViewHolder(itemView);
                return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) data.get(position);
                populateNativeAdView(nativeAd, ((AppInstallNativeAdViewHolder) holder).getAdView());
                break;

            default:
                if (holder instanceof MyViewHolder) {

                    ((MyViewHolder) holder).textView_cat.setText(((ItemWallpaper) data.get(position)).getCName());
                    ((MyViewHolder) holder).textView_cat.setVisibility(View.GONE);

                    if (((ItemWallpaper) data.get(position)).getTotalViews() != null) {
                        ((MyViewHolder) holder).view_count.setText("" + ((ItemWallpaper) data.get(position)).getTotalViews() + " Views");
                    }

                    if (((ItemWallpaper) data.get(position)).getTags() != null) {
                        ((MyViewHolder) holder).iv_hash_tags.setVisibility(View.GONE);
                        ((MyViewHolder) holder).iv_hash_tags.setText("" + ((ItemWallpaper) data.get(position)).getTags());
                    } else {
                        ((MyViewHolder) holder).iv_hash_tags.setVisibility(View.GONE);
                    }

                    if (((ItemWallpaper) data.get(position)).getTotalRate() != null) {
                        if (Integer.parseInt(((ItemWallpaper) data.get(position)).getTotalRate()) > 1) {
                            ((MyViewHolder) holder).rating_details.setText("" +
                                    Integer.parseInt(((ItemWallpaper) data.get(position)).getTotalRate()));
                        } else if (Integer.parseInt(((ItemWallpaper) data.get(position)).getTotalRate()) == 1) {
                            ((MyViewHolder) holder).rating_details.setText("" +
                                    Integer.parseInt(((ItemWallpaper) data.get(position)).getTotalRate()));
                        } else if (Integer.parseInt(((ItemWallpaper) data.get(position)).getTotalRate()) == 0) {
                            ((MyViewHolder) holder).rating_details.setText("0");
                        }
                    }


                    Picasso.with(context)
                            .load(((ItemWallpaper) data.get(position)).getImage())
                            .placeholder(R.drawable.placeholder)
                            .into(((MyViewHolder) holder).imageView);

                    ((MyViewHolder) holder).share_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new ShareImages(context, "share").execute(((ItemWallpaper) data.get(holder.getAdapterPosition())).getImage());
                        }
                    });
                    ((MyViewHolder) holder).download_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new ShareImages(context, "save").execute(((ItemWallpaper) data.get(holder.getAdapterPosition())).getImage());
                            Toast.makeText(context, "Image Download Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ((MyViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            recyclerViewClickListener.onClick(holder.getAdapterPosition());
                        }
                    });
                    ((MyViewHolder) holder).Whatsapps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new ShareImages(context, "whatsapp").execute(((ItemWallpaper) data.get(holder.getAdapterPosition())).getImage());
                        }
                    });
                } else {
                    if (getItemCount() == 1) {
                        ProgressViewHolder.progressBar.setVisibility(View.GONE);
                    }
                }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return (position >= 0 && position == data.size());
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = data.get(position);
        if (recyclerViewItem instanceof ItemWallpaper) {
            return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
        }
        return AD_VIEW_TYPE;
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        if (Config.ENABLE_ADS) {
            // Some assets are guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            NativeAd.Image icon = nativeAd.getIcon();

            if (icon == null) {
                adView.getIconView().setVisibility(View.INVISIBLE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // Assign native ad object to the native view.
            adView.setNativeAd(nativeAd);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView share_layout, download_layout, Whatsapps;
        private RelativeLayout rootlayout;
        private ImageView imageView;
        //        private ImageView imageView_fav;
        private TextView textView_cat, rating_details, iv_hash_tags, view_count;
        private View vieww;

        private MyViewHolder(View view) {
            super(view);
            rootlayout = view.findViewById(R.id.rootlayout);

            imageView = view.findViewById(R.id.iv_wall);
            rating_details = view.findViewById(R.id.rating_details);
            iv_hash_tags = view.findViewById(R.id.iv_hash_tags);
            view_count = view.findViewById(R.id.tv_wall_details_views);
            share_layout = view.findViewById(R.id.share);
            download_layout = view.findViewById(R.id.download);
            Whatsapps = view.findViewById(R.id.whatsapp);

            textView_cat = view.findViewById(R.id.tv_wall_cat);
            vieww = view.findViewById(R.id.view_wall);
        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onClick(getAdapterPosition());
        }
    }
}