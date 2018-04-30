package com.delta.joydeep.flickr.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delta.joydeep.flickr.R;
import com.delta.joydeep.flickr.adapter.SearchAdapter;
import com.delta.joydeep.flickr.client.entities.Photo;
import com.delta.joydeep.flickr.realm.RealmPhoto;
import com.delta.joydeep.flickr.realm.RealmSingleton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by joydeep.
 */
public class BookmarkActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private TextView noPhotoTV;
    private List<Photo> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.recyclerView);
        noPhotoTV = findViewById(R.id.textview);

        initializeRecyclerView();
        loadData(true);
    }

    private void initializeRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SearchAdapter(photoList, Glide.with(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadData(final boolean firstLoad) {
        Realm realm = RealmSingleton.getInstance().getRealm();
        RealmResults<RealmPhoto> realmPhotos = realm.where(RealmPhoto.class)
                .findAll();
        for (RealmPhoto realmPhoto : realmPhotos) {
            photoList.add(realmPhoto.getPhoto());
        }
        if (photoList.isEmpty()) {
            noPhotoTV.setVisibility(View.VISIBLE);
        } else {
            noPhotoTV.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }
}
