package com.delta.joydeep.flickr.realm;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmSingleton {

    private static final int REALM_SCHEMA_VERSION = 1;
    private static RealmSingleton mInstance = null;
    private Realm realm;

    private RealmSingleton() {
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            Log.d("RealmSingleton", "Realm Schema Version: " + REALM_SCHEMA_VERSION);
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .schemaVersion(REALM_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
            Log.d("RealmSingleton", String.valueOf(realm.getSchema().getAll()));
        }
    }

    public static RealmSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new RealmSingleton();
        }
        return mInstance;
    }

    public Realm getRealm() {
        return realm;
    }

}