package com.delta.joydeep.flickr.client;

import com.delta.joydeep.flickr.App;
import com.delta.joydeep.flickr.client.services.FlickrService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Helper class for easy usage of the Flickr API using retrofit.
 */
public class Flickr {

    //https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=7092e1051bade47a6f9dff132a4cea74&per_page=10&format=json&nojsoncallback=1

    public static final String API_HOST = "api.flickr.com";
    public static final String API_TYPE = "services";
    public static final String API_URL = "https://" + API_HOST + "/" + API_TYPE + "/";

    /**
     * API key query parameter name.
     */
    public static final String PARAM_API_KEY = "api_key";

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    private String apiKey;

    /**
     * Create a new manager instance.
     *
     * @param apiKey Your Flickr API key.
     */
    public Flickr(String apiKey) {
        this.apiKey = apiKey;
    }

    public void apiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String apiKey() {
        return apiKey;
    }

    /**
     * Creates a {@link Retrofit.Builder} that sets the base URL, adds a Gson converter and sets {@link #okHttpClient()}
     * as its client.
     *
     * @see #okHttpClient()
     */
    protected Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(FlickrHelper.getGsonBuilder().create()))
                .client(okHttpClient());
    }

    /**
     * Returns the default OkHttp client instance. It is strongly recommended to override this and use your app
     * instance.
     *
     * @see #setOkHttpClientDefaults(OkHttpClient.Builder)
     */
    protected synchronized OkHttpClient okHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            setOkHttpClientDefaults(builder);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * Adds an interceptor to add the api key query parameter and to log requests.
     */
    protected void setOkHttpClientDefaults(OkHttpClient.Builder builder) {
        builder.addInterceptor(new FlickrInterceptor(this));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (App.ENABLE_LOGGING) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        builder.addInterceptor(logging);
    }

    /**
     * Return the current {@link Retrofit} instance. If none exists (first call, auth changed), builds a new one.
     * When building, sets the base url and a custom client with an {@link Interceptor} which supplies authentication
     * data.
     */
    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = retrofitBuilder().build();
        }
        return retrofit;
    }

    public FlickrService tvService() {
        return getRetrofit().create(FlickrService.class);
    }

}
