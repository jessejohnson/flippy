package com.jojo.flippy.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import util.AlertDialogManager;
import util.InternetConnectionDetector;


public class WelcomeActivity extends Activity {

    static String TWITTER_CONSUMER_KEY = "PxDcIiGD7lNsWu4FolCUkYUCJ";
    static String TWITTER_CONSUMER_SECRET ="R0gR1YC2sHJ1xr6Sz7Fr9IUAPRblRLPtaZdfuO4aHBJxVqN6Wh";

    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

    //TODO change to actual flippy URL, or github pages URL
    static final String TWITTER_CALLBACK_URL = "oauth://com.jojo.flippy.app";

    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;
    private User user;

    private static SharedPreferences sharedPreferences;
    private InternetConnectionDetector internetConnectionDetector;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //check Internet connection state
        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());


        //initialize signin buttons
        Button SigninWithTwitter = (Button) findViewById(R.id.buttonSigninWithTwitter);
        Button SigninWithEmail = (Button) findViewById(R.id.buttonSigninWithEmail);

        //set shared preferences
        sharedPreferences = WelcomeActivity.this.getSharedPreferences("MyPref", 0);



        SigninWithTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!internetConnectionDetector.isConnectingToInternet()){
                    alert.showAlertDialog(WelcomeActivity.this,
                            getString(R.string.internet_connection_error_dialog_title),
                            getString(R.string.internet_connection_error_dialog_message),
                            false);
                    //stop execution after dialog
                    return;
                }

                //check if twitter keys are set
                if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
                    alert.showAlertDialog(WelcomeActivity.this,
                            getString(R.string.twitter_auth_error_title),
                            getString(R.string.twitter_auth_error_message),
                            false);
                    //stop execution after dialog
                    return;
                }
                signinWithTwitter();
            }
        });

        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {

                // oAuth verifier
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                new OAuthAccessTokenTask().execute(verifier);
            }
        }
    }

    private void signinWithTwitter() {
        if(!isTwitterLoggedInAlready()){
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            twitter4j.conf.Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        WelcomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Already Logged into twitter", Toast.LENGTH_LONG).show();
                    }
                }
            });
            thread.start();
        } else {
            Toast.makeText(WelcomeActivity.this,
                    "Already logged in",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isTwitterLoggedInAlready() {
        return sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    private class OAuthAccessTokenTask extends AsyncTask<String, Void, Exception>
    {
        @Override
        protected Exception doInBackground(String... params) {
            Exception toReturn = null;
            String verifier = null;

            Uri uri = getIntent().getData();
            if(uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)){
                verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
            }


            try {
                //accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                user = twitter.showUser(accessToken.getUserId());

            }
            catch(TwitterException e) {
                Log.e(WelcomeActivity.class.getName(), "TwitterError: " + e.getErrorMessage());
                toReturn = e;
            }
            catch(Exception e) {
                Log.e(WelcomeActivity.class.getName(), "Error: " + e.getMessage());
                toReturn = e;
            }

            return toReturn;
        }

        @Override
        protected void onPostExecute(Exception exception) {
            onRequestTokenRetrieved(exception);
        }
    }

    private void onRequestTokenRetrieved(Exception result) {

        if (result != null) {
            Toast.makeText(
                    this,
                    result.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }

        else {
            try {
                // Shared Preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                editor.commit();

                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());


                // Getting user details from twitter
                String username = user.getName();
                String profileImageUrl= user.getBiggerProfileImageURL();
                String handle = user.getScreenName();
                int followers = user.getFollowersCount();


                Toast.makeText(WelcomeActivity.this,
                        "Logged in " + username + " " + handle + " "+ followers+ " ",
                        Toast.LENGTH_LONG).show();

            }
            catch (Exception ex) {
                // Check log for login errors
                Log.e("Twitter Login Error", "> " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    protected void onResume() {
        super.onResume();
    }
}
