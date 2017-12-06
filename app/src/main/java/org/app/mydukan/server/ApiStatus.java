package org.app.mydukan.server;

/**
 * Created by Codespeak on 28-07-2016.
 */
public interface ApiStatus {
    void onSuccess(String response);

    void onFailure(Exception error, String response);
}

