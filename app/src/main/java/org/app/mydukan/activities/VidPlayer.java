package org.app.mydukan.activities;

import android.net.Uri;
import android.support.annotation.NonNull;

import android.os.Bundle;

import retrofit2.http.Url;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;

import org.app.mydukan.R;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.youtube.Config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class VidPlayer extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    GoogleApiClient mGoogleApiClient;
    // YouTube player view
    private YouTubePlayerView youTubeView;
    String path = "";
    String remoteUrl;
    ImageButton closeButton;
    AdView mAdView, cALdview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(AppContants.REMOTECONFIG_VID) ) {
                remoteUrl =  bundle.getString(AppContants.REMOTECONFIG_VID);
                if(remoteUrl==null){
                    try {
                        path = new URL(data.toString()).getPath();
                        path = path.substring(1);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Log.d("Path", path);
                }else{
                   path=remoteUrl;
                   // path="D8Ert5yjMV4";
                }
            }
        }
 /*
        //initialization of adview in this activity//
        //initialize ads for the app
        MobileAds.initialize(this,"ca-app-pub-1640690939729824/3548733794");
        // MobileAds.initialize(VidPlayer.this ,"ca-app-pub-1640690939729824/3548733794");
        mAdView = (AdView) findViewById(R.mCatId.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


      try {
            path = new URL(data.toString()).getPath();
            path = path.substring(1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("Path", path);
        */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .build();
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Log.d("Deep", "Status True");
                                    // Extract deep link from Intent

                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    path=deepLink;
                                    Log.d("Deep Link", deepLink);
                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.

                                    // ...
                                } else {
                                    Log.d(TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.videoplayer);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        closeButton =(ImageButton) findViewById(R.id.closeButton);
         closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //startActivity(new Intent(VidPlayer.this, MainActivity.class));
                //finish();
            }
        });

        // Initializing v/home/rk/Desktop/Photos.zipideo player with developer key
        youTubeView.initialize(Config.DEVELOPER_KEY, this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/2806052595");
        cALdview = (AdView) findViewById(R.id.adView_youtube_video);
        AdRequest adRequest = new AdRequest.Builder().build();
        cALdview.loadAd(adRequest);

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "Error Playing Video", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
        Answers.getInstance().logCustom(new CustomEvent("video page ")
                .putCustomAttribute("Video_Player","video Id:"+path ));
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        // Hiding player controls
        player.setPlayerStyle(PlayerStyle.DEFAULT);

        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            if(path==""){
                player.cueVideo(Config.YOUTUBE_VIDEO_CODE);
            }else{
                player.cueVideo(path);
            }

        }
    }
    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
        super.onBackPressed();
        return;
    }
    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
