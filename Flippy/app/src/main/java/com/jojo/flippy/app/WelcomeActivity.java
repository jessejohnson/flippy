package com.jojo.flippy.app;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.jojo.flippy.util.AlertDialogManager;
import com.jojo.flippy.util.InternetConnectionDetector;
import com.google.android.gms.common.ConnectionResult;


public class WelcomeActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    static String TWITTER_CONSUMER_KEY = "PxDcIiGD7lNsWu4FolCUkYUCJ";
    static String TWITTER_CONSUMER_SECRET = "R0gR1YC2sHJ1xr6Sz7Fr9IUAPRblRLPtaZdfuO4aHBJxVqN6Wh";

    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    static final String TWITTER_CALLBACK_URL = "oauth://com.jojo.flippy.app";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;
    private User user;

    private static SharedPreferences sharedPreferencesTwitter;
    private InternetConnectionDetector internetConnectionDetector;
    AlertDialogManager alert = new AlertDialogManager();

    //g-plus section login
    private SignInButton gplus;
    private static final int RC_SIGN_IN = 0;
    // Log cat tag
    private static final String TAG = "WelcomeActivity";
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        //check Internet connection state
        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        //set shared preferences
        sharedPreferencesTwitter = WelcomeActivity.this.getSharedPreferences("MyPref", 0);


        //initialize sign in buttons
        Button SigninWithTwitter = (Button) findViewById(R.id.buttonSigninWithTwitter);
        Button SigninWithEmail = (Button) findViewById(R.id.buttonSigninWithEmail);
        gplus = (SignInButton) findViewById(R.id.btn_sign_in);


        //The g-plus login option
        gplus.setSize(SignInButton.SIZE_WIDE);
        gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGplus();
            }
        });


        //The onclick listener for the email login
        SigninWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(emailIntent);

            }
        });

        //The onclick listener for the twitter button
        SigninWithTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!internetConnectionDetector.isConnectingToInternet()) {
                    alert.showAlertDialog(WelcomeActivity.this,
                            getString(R.string.internet_connection_error_dialog_title),
                            getString(R.string.internet_connection_error_dialog_message),
                            false);
                    //stop execution after dialog
                    return;
                }

                //check if twitter keys are set
                if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
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

    //for the g-plus login section
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any sign in errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        getProfileInformation();

    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    private void signinWithTwitter() {
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            twitter4j.conf.Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable() {
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
        return sharedPreferencesTwitter.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    private class OAuthAccessTokenTask extends AsyncTask<String, Void, Exception> {
        @Override
        protected Exception doInBackground(String... params) {
            Exception toReturn = null;
            String verifier = null;

            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
            }


            try {
                //accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                user = twitter.showUser(accessToken.getUserId());

            } catch (TwitterException e) {
                Log.e(WelcomeActivity.class.getName(), "TwitterError: " + e.getErrorMessage());
                toReturn = e;
            } catch (Exception e) {
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
        } else {
            try {
                // Shared Preferences
                SharedPreferences.Editor editor = sharedPreferencesTwitter.edit();
                editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                editor.commit();

                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());


                // Getting user details from twitter
                String username = user.getName();
                String profileImageUrl = user.getBiggerProfileImageURL();
                String handle = user.getScreenName();
                int followers = user.getFollowersCount();


                Toast.makeText(WelcomeActivity.this,
                        "Logged in " + username + " " + handle + " " + followers + " " + profileImageUrl + "",
                        Toast.LENGTH_LONG).show();

            } catch (Exception ex) {
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
