package org.app.mydukan.server;

/**
 * Created by Codespeak on 28-07-2016.
 */
public interface ApiResult {

    void onSuccess(Object data);

    void onFailure(String response);

}
