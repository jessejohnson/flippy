package com.jojo.flippy.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
    static final String TWITTER_CALLBACK_URL = "oauth://com.jojo.flippy.app";

    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    private static Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences sharedPreferences;
    private InternetConnectionDetector internetConnectionDetector;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //check Internet connection state
        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
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

        //initialize signin buttons
        Button SigninWithTwitter = (Button) findViewById(R.id.buttonSigninWithTwitter);
        Button SigninWithEmail = (Button) findViewById(R.id.buttonSigninWithEmail);

        //set shared preferences
        sharedPreferences = WelcomeActivity.this.getSharedPreferences("MyPref", 0);



        SigninWithTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginTask().execute();
            }
        });

        //after returning from the twitter page
        if(!isTwitterLoggedInAlready()){
            Uri uri = getIntent().getData();
            if(uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)){
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try{
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    editor.commit();

                    //now granted access to personal information via Twitter
                    long userID = accessToken.getUserId();
                    User user = twitter.showUser(userID);

                    //test authentication success by printing username
                    Toast.makeText(WelcomeActivity.this,
                            "You are " + user.getName(), Toast.LENGTH_LONG).show();
                } catch (TwitterException e){
                    e.printStackTrace();
                }
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

            try{
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
            } catch (TwitterException t){
                t.printStackTrace();
            }
        } else {
            Toast.makeText(WelcomeActivity.this,
                    "Already logged in",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isTwitterLoggedInAlready() {
        return sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            signinWithTwitter();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            WelcomeActivity.this
                    .startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(requestToken.getAuthenticationURL())));
        }
    }
}
