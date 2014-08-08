package com.jojo.flippy.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.AlertDialogManager;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Arrays;
import java.util.List;


@SuppressLint("NewApi")
public class FacebookSignInFragment extends Fragment {
    private static final String TAG = "FacebookLogin";
    Context mContext;
    LoginButton mLoginBtn;
    private Button signInWithEmail, buttonLoginWithEmail;
    private String regUserEmail, regUserAuthToken, regUserID, regFirstName, regLastName, regGender;
    private String fbId, first_name, last_name, userEmail, profilePic, profilePicSmall, gender = "", avatar = "", date_of_birth = "", community = "";
    private Dao<User, Integer> userDao;
    private Dao<Channels, Integer> channelDao;


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
        userDao = ((Flippy) getActivity().getApplication()).userDao;
        buttonLoginWithEmail = (Button) view.findViewById(R.id.buttonLoginWithEmail);
        buttonLoginWithEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });
        signInWithEmail = (Button) view.findViewById(R.id.buttonSigninWithEmail);
        signInWithEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
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
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, final Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                first_name = user.getFirstName();
                                last_name = user.getLastName();
                                fbId = user.getId();
                                userEmail = (String) user.asMap().get("email");
                                profilePic = "http://graph.facebook.com/" + fbId + "/picture?type=large";
                                profilePicSmall = "http://graph.facebook.com/" + fbId + "/picture?type=small";
                                gender = user.asMap().get("gender").toString();
                                avatar = profilePic;
                                //setting the user parameters
                                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                                progressDialog.setMessage("Just a minute ...");
                                progressDialog.show();
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("first_name", first_name);
                                jsonObject.addProperty("last_name", last_name);
                                jsonObject.addProperty("email", userEmail);
                                jsonObject.addProperty("profile_pic_url", profilePic);
                                jsonObject.addProperty("gender", gender.toString().substring(0, 1).toUpperCase());
                                Ion.with(mContext)
                                        .load(Flippy.USERS_URL + "social-signup/")
                                        .setHeader("Authorization", "Token "+Flippy.DEFAULT_TOKEN)
                                        .setJsonObjectBody(jsonObject)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                progressDialog.dismiss();
                                                try {
                                                    if (result != null) {
                                                        if (result.has("detail")) {
                                                            ToastMessages.showToastShort(mContext, "sorry try again later");
                                                            return;
                                                        } else {
                                                            regUserAuthToken = result.get("auth_token").getAsString();
                                                            regUserID = result.get("id").getAsString();
                                                            regFirstName = result.get("first_name").getAsString();
                                                            regUserEmail = result.get("email").getAsString();
                                                            regLastName = result.get("last_name").getAsString();
                                                            if (!result.get("community").isJsonNull()) {
                                                                community = result.get("community").getAsString();
                                                            }
                                                            if (!result.get("date_of_birth").isJsonNull()) {
                                                                date_of_birth = result.get("date_of_birth").getAsString();
                                                            }
                                                            if (!result.get("gender").isJsonNull()) {
                                                                regGender = result.get("gender").getAsString();
                                                            }
                                                            if (!result.get("avatar").isJsonNull()) {
                                                                avatar = result.get("avatar").getAsString();
                                                            }
                                                            User user = new User(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName, avatar, profilePicSmall, regGender, date_of_birth);
                                                            try {
                                                                List<User> userList = userDao.queryForAll();
                                                                if (!userList.isEmpty()) {
                                                                    userDao.delete(userList);
                                                                }
                                                                userDao.create(user);
                                                                userDao.refresh(user);

                                                            } catch (java.sql.SQLException sqlE) {
                                                                sqlE.printStackTrace();
                                                            }

                                                            if (community == null || community.equalsIgnoreCase("")) {
                                                                Intent intent = new Intent(mContext, SelectCommunityActivity.class);
                                                                intent.putExtra("regUserEmail", regUserEmail);
                                                                intent.putExtra("regUserAuthToken", regUserAuthToken);
                                                                intent.putExtra("regUserID", regUserID);
                                                                startActivity(intent);

                                                            } else {
                                                                try {
                                                                    List<User> userList = userDao.queryForAll();
                                                                    if (!userList.isEmpty()) {
                                                                        User sameUser = userList.get(0);
                                                                        sameUser.community_id = community;
                                                                        userDao.update(sameUser);
                                                                        userDao.refresh(sameUser);
                                                                    }
                                                                } catch (java.sql.SQLException sqlE) {
                                                                    sqlE.printStackTrace();
                                                                }
                                                                saveUserChannels(regUserID);
                                                            }
                                                        }
                                                    }
                                                    if (e != null) {
                                                        ToastMessages.showToastShort(mContext, "sorry try again later");
                                                        return;
                                                    }

                                                } catch (Exception exception) {
                                                    Log.e("Facebook login", exception.toString());

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

    private void saveUserChannels(String userId) {
        String url = Flippy.USERS_URL + userId + "/subscriptions/";
        if (userId == null || userId == "") {
            ToastMessages.showToastShort(mContext, "Unfortunately something went wrong, try again later");
            return;
        }
        Ion.with(mContext)
                .load(url)
                .setHeader("Authorization", "Token "+regUserAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                JsonArray channelArray = result.getAsJsonArray("results");
                                for (int i = 0; i < channelArray.size(); i++) {
                                    JsonObject item = channelArray.get(i).getAsJsonObject();
                                    String channel_id = item.get("id").getAsString();
                                    try {
                                        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(mContext,
                                                DatabaseHelper.class);
                                        channelDao = databaseHelper.getChannelDao();
                                        Channels channels = new Channels(channel_id);
                                        channelDao.createOrUpdate(channels);
                                    } catch (java.sql.SQLException sqlE) {
                                        sqlE.printStackTrace();
                                        Log.e("Sign in activity", "Error getting all user channels");
                                    }
                                }
                                Intent intent = new Intent(mContext, CommunityCenterActivity.class);
                                intent.putExtra("regUserEmail", regUserEmail);
                                intent.putExtra("regUserAuthToken", regUserAuthToken);
                                intent.putExtra("regUserID", regUserID);
                                startActivity(intent);
                            } else if (e != null) {
                                ToastMessages.showToastShort(mContext, "Internet connection error occurred");
                            } else {
                                Log.e("Fragment Facebook", "something else went wrong");
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e("Fragment Facebook", "Error loading channels " + exception.toString());
                        }
                    }
                });

    }
}