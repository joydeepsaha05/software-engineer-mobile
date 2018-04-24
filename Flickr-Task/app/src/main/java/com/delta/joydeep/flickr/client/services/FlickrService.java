package com.delta.joydeep.flickr.client.services;

import com.delta.joydeep.flickr.client.entities.Recommendation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrService {

    String interestingnessMethod = "flickr.interestingness.getList";

    /**
     * Returns the list of interesting photos for the most recent day or a user-specified date.
     */
    @GET("rest/")
    Call<Recommendation> recommendations(
            @Query("method") String method,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("format") String format,
            @Query("nojsoncallback") int callback
    );
}
