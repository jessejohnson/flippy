package com.jojo.flippy.app;

import android.annotation.SuppressLint;
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
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
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
    private Button signInWithEmail;
    private String regUserEmail, regUserAuthToken, regUserID, regFirstName, regLastName, regAvatar, regGender, regAvatarURL;
    private String regDateOfBirth = "";
    private String fbId, first_name, last_name, userEmail, profilePic, profilePicSmall, gender = "", avatar = "", avatar_thumb = "", date_of_birth = "";
    private SuperToast superToast;
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
        superToast = new SuperToast(mContext);

        signInWithEmail = (Button) view.findViewById(R.id.buttonSigninWithEmail);
        signInWithEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithEmail();
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
                                first_name = user.getFirstName();
                                last_name = user.getLastName();
                                fbId = user.getId();
                                userEmail = (String) user.asMap().get("email");
                                profilePic = "http://graph.facebook.com/" + fbId + "/picture?type=large";
                                profilePicSmall = "http://graph.facebook.com/" + fbId + "/picture?type=small";
                                gender = user.asMap().get("gender").toString();
                                //setting the user parameters
                                JsonObject json = new JsonObject();
                                json.addProperty("email", userEmail);
                                json.addProperty("first_name", first_name);
                                json.addProperty("last_name", last_name);
                                json.addProperty("password", fbId);
                                //json.addProperty("avatar",profilePic);
                                //json.addProperty("avatar_thumb",profilePicSmall);
                                // json.addProperty("date_of_birth",date_of_birth);
                                json.addProperty("gender", gender);

                                Ion.with(mContext)
                                        .load(Flippy.users + "signup/")
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
                                                        JsonObject json = new JsonObject();
                                                        json.addProperty("email", userEmail);
                                                        json.addProperty("password", fbId);
                                                        Ion.with(mContext)
                                                                .load(Flippy.users + "login/")
                                                                .setJsonObjectBody(json)
                                                                .asJsonObject()
                                                                .setCallback(new FutureCallback<JsonObject>() {
                                                                    @Override
                                                                    public void onCompleted(Exception e, JsonObject result) {

                                                                        if (e != null) {
                                                                            superToast.setAnimations(SuperToast.Animations.FLYIN);
                                                                            superToast.setDuration(SuperToast.Duration.LONG);
                                                                            superToast.setBackground(SuperToast.Background.PURPLE);
                                                                            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
                                                                            superToast.setText(getResources().getString(R.string.internet_connection_error_dialog_title));
                                                                            superToast.show();
                                                                            return;
                                                                        } else {

                                                                            if (result.has("detail")) {
                                                                                superToast.setAnimations(SuperToast.Animations.FLYIN);
                                                                                superToast.setDuration(SuperToast.Duration.LONG);
                                                                                superToast.setBackground(SuperToast.Background.RED);
                                                                                superToast.setTextSize(SuperToast.TextSize.MEDIUM);
                                                                                superToast.setText(result.get("detail").getAsString());
                                                                                superToast.show();
                                                                                return;
                                                                            }
                                                                            regUserAuthToken = result.get("auth_token").getAsString();
                                                                            regUserID = result.get("id").getAsString();
                                                                            regFirstName = result.get("first_name").getAsString();
                                                                            regUserEmail = result.get("email").getAsString();
                                                                            regLastName = result.get("last_name").getAsString();
                                                                            if (!result.get("avatar").isJsonNull()) {
                                                                                avatar = result.get("avatar").getAsString();
                                                                            }
                                                                            if (!result.get("avatar_thumb").isJsonNull()) {
                                                                                avatar_thumb = result.get("avatar_thumb").getAsString();
                                                                            }

                                                                            if (!result.get("gender").isJsonNull()) {
                                                                                gender = result.get("gender").getAsString();
                                                                            }
                                                                            if (!result.get("date_of_birth").isJsonNull()) {
                                                                                date_of_birth = result.get("date_of_birth").getAsString();
                                                                            }

                                                                            createUser(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName, avatar, avatar_thumb, gender, date_of_birth);
                                                                            Intent intent = new Intent(getActivity(), SelectCommunityActivity.class);
                                                                            intent.putExtra("regUserEmail", regUserEmail);
                                                                            intent.putExtra("regUserAuthToken", regUserAuthToken);
                                                                            intent.putExtra("regUserID", regUserID);
                                                                            startActivity(intent);
                                                                        }
                                                                    }

                                                                });
                                                    }
                                                    regUserAuthToken = result.get("auth_token").getAsString();
                                                    regUserID = result.get("id").getAsString();
                                                    regFirstName = result.get("first_name").getAsString();
                                                    regLastName = result.get("last_name").getAsString();
                                                    regUserEmail = result.get("email").getAsString();
                                                    regGender = gender;
                                                    createUser(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName, profilePic, profilePicSmall, regGender, regDateOfBirth);

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

    private void createUser(String userID, String userToken, String userEmail, String userFirstName, String userLastName, String profilePic, String profilePicSmall, String gender, String dateOfBirth) {
        try {
            Dao<User, Integer> userDao = ((Flippy) getActivity().getApplication()).userDao;
            List<User> userList = userDao.queryForAll();
            if (!userList.isEmpty()) {
                userDao.delete(userList);
            }
            User user = new User(userID, userToken, userEmail, userFirstName, userLastName, profilePic, profilePicSmall, gender, dateOfBirth);
            userDao.create(user);

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
        }
    }

    private void loginWithEmail() {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
    }

}