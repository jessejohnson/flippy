package com.jojo.flippy.app;

/**
 * Created by bright on 6/9/14.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.AlertDialogManager;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@SuppressLint("NewApi")
public class FacebookSigninFragment extends Fragment {
    private static final String TAG = "FacebookLogin";
    Context mContext;
    LoginButton mLoginBtn;
    private Button signInWithEmail;
    private String regUserEmail, regUserAuthToken, regUserID, regFirstName, regLastName, regAvatar,regGender,regAvatarURL,regDateOfBirth;

    //session status callback variable
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    //manages ui flow
    private UiLifecycleHelper uiHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);

        mContext = getActivity();

        signInWithEmail = (Button) view.findViewById(R.id.buttonSigninWithEmail);
        signInWithEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mLoginBtn = (LoginButton) view.findViewById(R.id.authButton);
        mLoginBtn.setFragment(this);
        mLoginBtn.setReadPermissions(Arrays.asList("public_profile", "email"));


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            mLoginBtn.setText("Logging you in...");
            makeMeRequest(session);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            Log.i(TAG, "Facebook Exception " + exception);
        }
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, final Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                String fullName = user.getName();
                                String username = user.getUsername();
                                String first_name = user.getFirstName();
                                String last_name = user.getLastName();
                                String fbId = user.getId();
                                String userEmail = (String) user.asMap().get("email");
                                final String profilePic = "http://graph.facebook.com/" + fbId + "/picture?type=large";
                                final String profilePicSmall = "http://graph.facebook.com/" + fbId + "/picture?type=small";
                                final String gender = user.asMap().get("gender").toString();

                                //Try to register the user, but if the user already exist, then sign him in instead
                                Log.e("user facebook info",first_name+ " " + gender);
                                //setting the user parameters
                                JsonObject json = new JsonObject();
                                json.addProperty("email", userEmail);
                                json.addProperty("first_name", first_name);
                                json.addProperty("last_name", last_name);
                                json.addProperty("password", fbId);
                                //json.addProperty("avatar",profilePic);
                                //json.addProperty("avatar_thumb",profilePicSmall);
                               // json.addProperty("date_of_birth",date_of_birth);
                                json.addProperty("gender",gender);

                                Ion.with(mContext)
                                        .load(Flippy.regURL)
                                        .setJsonObjectBody(json)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {

                                                if (e != null) {
                                                    ToastMessages.showToastLong(mContext, getResources().getString(R.string.internet_connection_error_dialog_title));
                                                    Log.e("Error", e.toString());
                                                } else {
                                                    if (result.has("detail")) {
                                                        Intent intent = new Intent(getActivity(), SelectCommunityActivity.class);
                                                        startActivity(intent);
                                                        return;
                                                    }
                                                    regUserAuthToken = result.get("auth_token").getAsString();
                                                    regUserID = result.get("id").getAsString();
                                                    regFirstName = result.get("first_name").getAsString();
                                                    regLastName = result.get("last_name").getAsString();
                                                    regUserEmail =result.get("email").getAsString();
                                                    regGender = gender;
                                                    //regAvatarURL = result.get("avatar").getAsString();
                                                   // regDateOfBirth = result.get("date_of_birth").getAsString();
                                                    //Save the information in the database
                                                    try {
                                                        Dao<User, Integer> userDao = ((Flippy) getActivity().getApplication()).userDao;
                                                        User user = new User(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName,profilePic,profilePicSmall,regGender,"");
                                                        userDao.create(user);

                                                    } catch (java.sql.SQLException sqlE) {
                                                        sqlE.printStackTrace();
                                                    }
                                                    //the end of the persistence
                                                    Intent intent = new Intent(getActivity(), SelectCommunityActivity.class);
                                                    intent.putExtra("regUserEmail", regUserEmail);
                                                    intent.putExtra("regUserAuthToken", regUserAuthToken);
                                                    intent.putExtra("regUserID", regUserID);
                                                    intent.setClass(getActivity(), SelectCommunityActivity.class);
                                                    startActivity(intent);
                                                    return;
                                                }
                                            }

                                        });


                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, unable to log user in by facebook
                            AlertDialogManager facebookSignInError = new AlertDialogManager();
                            facebookSignInError.showAlertDialog(getActivity(), getString(R.string.facebook_error_tittle), getString(R.string.facebook_error_message), true);
                        }
                    }
                }
        );

        request.executeAsync();
    }

}