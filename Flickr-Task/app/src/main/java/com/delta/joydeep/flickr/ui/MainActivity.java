package com.delta.joydeep.flickr.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.delta.joydeep.flickr.R;
import com.delta.joydeep.flickr.adapter.PhotoAdapter;
import com.delta.joydeep.flickr.client.Flickr;
import com.delta.joydeep.flickr.client.entities.Photo;
import com.delta.joydeep.flickr.client.entities.PhotoModel;
import com.delta.joydeep.flickr.client.entities.Recommendation;
import com.delta.joydeep.flickr.client.services.FlickrService;
import com.delta.joydeep.flickr.misc.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PHOTOS_PER_PAGE = 20;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout tapToRetry;
    private PhotoAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progress_bar);
        tapToRetry = findViewById(R.id.tap_to_retry);

        initializeRecyclerView();
        configureRetry();
        loadData(true);
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PhotoAdapter(photoList, Glide.with(this), recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(new PhotoAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(false);
            }
        });
    }

    private void configureRetry() {
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapToRetry.setVisibility(View.GONE);
                loadData(true);
            }
        });
    }

    private void loadData(final boolean firstLoad) {
        if (firstLoad) {
            progressBar.setVisibility(View.VISIBLE);
            adapter.setCurrentPage(0);
        } else {
            photoList.add(null);
            adapter.notifyItemInserted(photoList.size() - 1);
        }

        Flickr flickr = new Flickr(Constants.FLICKR_API_KEY);
        Call<Recommendation> recommendations = flickr.tvService()
                .recommendations(FlickrService.interestingnessMethod, PHOTOS_PER_PAGE,
                        adapter.getCurrentPage() + 1, "json", 1);

        recommendations.enqueue(new Callback<Recommendation>() {
            @Override
            public void onResponse(Call<Recommendation> call, Response<Recommendation> response) {
                progressBar.setVisibility(View.GONE);
                if (firstLoad) {
                    photoList.clear();
                } else {
                    photoList.remove(photoList.size() - 1);
                    adapter.notifyItemRemoved(photoList.size());
                }
                tapToRetry.setVisibility(View.GONE);
                if (response.code() == 200) {
                    PhotoModel photoModel = response.body().photos;
                    int pageCount = photoModel.pages;
                    int currentPage = photoModel.page;
                    adapter.setServerPageCount(pageCount);
                    adapter.setCurrentPage(currentPage);
                    photoList.addAll(photoModel.photo);
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded(true);
                } else {
                    Log.e(TAG, "Failed");
                    tapToRetry.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Recommendation> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                if (firstLoad) {
                    tapToRetry.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    photoList.remove(photoList.size() - 1);
                    adapter.notifyItemRemoved(photoList.size());
                    Toast.makeText(MainActivity.this, R.string.error_fetching_data,
                            Toast.LENGTH_SHORT).show();
                    adapter.setLoaded(false);
                }
            }
        });
    }
}
