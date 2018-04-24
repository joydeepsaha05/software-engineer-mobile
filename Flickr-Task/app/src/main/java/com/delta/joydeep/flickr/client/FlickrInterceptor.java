package com.delta.joydeep.flickr.client;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * {@link Interceptor} to add the API key query parameter.
 */
public class FlickrInterceptor implements Interceptor {

    private Flickr flickr;

    public FlickrInterceptor(Flickr flickr) {
        this.flickr = flickr;
    }

    /**
     * If the host matches {@link Flickr#API_HOST} adds a query parameter with the API key.
     */
    public static Response handleIntercept(Chain chain, String apiKey) throws IOException {
        Request request = chain.request();
        if (!Flickr.API_HOST.equals(request.url().host())) {
            // do not intercept requests for other hosts
            // this allows the interceptor to be used on a shared okhttp client
            return chain.proceed(request);
        }

        // add (or replace) the API key query parameter
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        urlBuilder.setEncodedQueryParameter(Flickr.PARAM_API_KEY, apiKey);

        Request.Builder builder = request.newBuilder();
        builder.url(urlBuilder.build());

        return chain.proceed(builder.build());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return handleIntercept(chain, flickr.apiKey());
    }

}
