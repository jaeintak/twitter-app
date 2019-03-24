package com.example.user.twitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        // If using in a fragment

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            getProfileAndProceed(accessToken);
        } else {
            FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getProfileAndProceed(loginResult.getAccessToken() );
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("////////////////", error.toString());
                }
            };

            // Callback registration
            loginButton.registerCallback(callbackManager, facebookCallback);
        }





    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void getProfileAndProceed(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = (String) response.getJSONObject().get("name");
                            String id = (String) response.getJSONObject().get("id");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            intent.putExtra("user_name", name);
                            intent.putExtra("user_id", id);
                            startActivity(intent);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name");
        parameters.putString("locale", "ko_KR");
        request.setParameters(parameters);
        request.executeAsync();

    }
}
