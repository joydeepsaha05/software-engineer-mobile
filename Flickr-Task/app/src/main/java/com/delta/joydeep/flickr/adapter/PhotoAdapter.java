package com.delta.joydeep.flickr.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.delta.joydeep.flickr.App;
import com.delta.joydeep.flickr.R;
import com.delta.joydeep.flickr.client.entities.Photo;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhotoAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private List<Photo> items;
    private RequestManager requestManager;
    private OnLoadMoreListener onLoadMoreListener;
    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private int serverPageCount, currentPage;
    private boolean loading;
    private long lastLoadTime;

    public PhotoAdapter(List<Photo> items, RequestManager requestManager, RecyclerView recyclerView) {
        this.items = items;
        this.requestManager = requestManager;

        serverPageCount = 1;
        currentPage = 0;
        lastLoadTime = 0;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)
                            && currentPage < serverPageCount) {
                        // Reached the end
                        if (TimeUnit.SECONDS.convert(System.currentTimeMillis() - lastLoadTime,
                                TimeUnit.MILLISECONDS) < 7) {
                            return;
                        }
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_main, parent, false);
            vh = new PhotoViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_progress_bar, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhotoViewHolder) {
            final PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            Photo photo = items.get(position);

            photoViewHolder.backView.setVisibility(View.GONE);
            photoViewHolder.frontView.setVisibility(View.VISIBLE);
            photoViewHolder.isBackVisible = false;

            String imageURL = "https://farm" + photo.farm + ".staticflickr.com/" + photo.server + "/"
                    + photo.id + "_" + photo.secret + "_n.jpg";
            requestManager.load(imageURL)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            int originalWidth, originalHeight;
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();
                            /*Log.d("PhotoAdapter",
                                    items.get(photoViewHolder.getAdapterPosition()).title +
                                            " " + width + "x" + height);*/
                            if (width >= height) {
                                originalWidth = 320;
                                originalHeight = (int) (originalWidth * ((float) height / width));
                            } else {
                                originalHeight = 320;
                                originalWidth = (int) (originalHeight * ((float) width / height));
                            }
                            String size = originalWidth + "x" + originalHeight;
                            photoViewHolder.tvSize.setText(size);
                            return false;
                        }
                    })
                    .into(photoViewHolder.imgViewIcon);
            photoViewHolder.tvTitle.setText(photo.title);


            photoViewHolder.tvOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.flickr.com/people/"
                            + PhotoAdapter.this.items.get(photoViewHolder.getAdapterPosition()).owner
                            + "/");
                    CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                    intentBuilder.setToolbarColor(ContextCompat.getColor(App.getAppContext(),
                            R.color.colorPrimary));
                    intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(App.getAppContext(),
                            R.color.colorPrimaryDark));
                    intentBuilder.setExitAnimations(App.getAppContext(), android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);

                    CustomTabsIntent customTabsIntent = intentBuilder.build();
                    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    customTabsIntent.launchUrl(App.getAppContext(), uri);
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded(boolean isSuccessful) {
        loading = false;
        if (!isSuccessful) {
            lastLoadTime = System.currentTimeMillis();
        }
    }

    public void setServerPageCount(int pageCount) {
        serverPageCount = pageCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        FrameLayout backView, frontView;
        TextView tvTitle, tvOwner, tvSize;
        ImageView imgViewIcon;
        boolean isBackVisible = false;

        PhotoViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvTitle = itemLayoutView.findViewById(R.id.title);
            tvOwner = itemLayoutView.findViewById(R.id.owner);
            tvSize = itemLayoutView.findViewById(R.id.size);
            imgViewIcon = itemLayoutView.findViewById(R.id.image);
            backView = itemLayoutView.findViewById(R.id.back_view);
            frontView = itemLayoutView.findViewById(R.id.front_view);

            final AnimatorSet leftIn = (AnimatorSet) AnimatorInflater
                    .loadAnimator(App.getAppContext(), R.animator.card_flip_left_in);
            final AnimatorSet rightOut = (AnimatorSet) AnimatorInflater
                    .loadAnimator(App.getAppContext(), R.animator.card_flip_right_out);
            final AnimatorSet leftOut = (AnimatorSet) AnimatorInflater
                    .loadAnimator(App.getAppContext(), R.animator.card_flip_left_out);
            final AnimatorSet rightIn = (AnimatorSet) AnimatorInflater
                    .loadAnimator(App.getAppContext(), R.animator.card_flip_right_in);

            rightOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    backView.setVisibility(View.VISIBLE);
                    frontView.setVisibility(View.GONE);
                    rightIn.start();
                }
            });
            rightIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isBackVisible = true;
                }
            });
            leftOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    backView.setVisibility(View.GONE);
                    frontView.setVisibility(View.VISIBLE);
                    leftIn.start();
                }
            });
            leftIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isBackVisible = false;
                }
            });

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBackVisible) {
                        leftOut.setTarget(backView);
                        leftIn.setTarget(frontView);
                        leftOut.start();
                    } else {
                        rightIn.setTarget(backView);
                        rightOut.setTarget(frontView);
                        rightOut.start();
                    }
                }
            });
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar);
        }
    }
}
