package com.delta.joydeep.flickr.realm;

import com.delta.joydeep.flickr.client.entities.Photo;

import io.realm.RealmObject;

public class RealmPhoto extends RealmObject {

    public String id;
    public String owner;
    public String secret;
    public long server;
    public long farm;
    public String title;

    public void setDetails(Photo photo) {
        id = photo.id;
        owner = photo.owner;
        secret = photo.secret;
        server = photo.server;
        farm = photo.farm;
        title = photo.title;
    }

    public Photo getPhoto() {
        Photo photo = new Photo();
        photo.id = id;
        photo.owner = owner;
        photo.secret = secret;
        photo.server = server;
        photo.farm = farm;
        photo.title = title;
        return photo;
    }
}
