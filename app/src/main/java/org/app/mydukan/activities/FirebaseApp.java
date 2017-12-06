package org.app.mydukan.activities;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vaibhavkumar on 09/10/17.
 */

public class FirebaseApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
