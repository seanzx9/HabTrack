package com.example.habtrack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getName();
    private final static int G_SIGN_IN = 1;
    private EditText emailInput;
    private EditText passwordInput;
    private Button emailButton;
    private LinearLayout googleButton;
    private LinearLayout facebookButton;
    private LoginButton facebookLoginButton;
    private Animation buttonPress;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //initialize welcome text
        TextView appName = (TextView) findViewById(R.id.app_name);
        String textColor = "#" + Integer
                .toHexString(ContextCompat.getColor(this, R.color.text) & 0x00ffffff);
        String accentColor = "#" + Integer
                .toHexString(ContextCompat.getColor(this, R.color.purple) & 0x00ffffff);
        appName.setText(HtmlCompat.fromHtml(
                getColoredSpanned("Hab", accentColor) +
                       getColoredSpanned("Track", textColor),
                       HtmlCompat.FROM_HTML_MODE_LEGACY));

        //initialize input fields for email/password and handle Done button
        emailInput = (EditText) findViewById(R.id.email_input);
        emailInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        emailInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    emailInput.clearFocus();
                return false;
            }
        });
        passwordInput = (EditText) findViewById(R.id.password_input);
        passwordInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    passwordInput.clearFocus();
                return false;
            }
        });

        //initialize sign in buttons
        emailButton = (Button) findViewById(R.id.email_login);
        emailButton.setOnClickListener(this);
        googleButton = (LinearLayout) findViewById(R.id.google_login);
        googleButton.setOnClickListener(this);
        facebookButton = (LinearLayout) findViewById(R.id.facebook_login);
        facebookButton.setOnClickListener(this);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_hidden);

        //initialize text views
        TextView signUp = (TextView) findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);
        TextView forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

        //initialize animations
        buttonPress = AnimationUtils.loadAnimation(this, R.anim.button_press);
        buttonPress.reset();

        //initialize google login
        initializeOfficialLogin();
    }

    /**
     * Changes color of text in UI.
     *
     * @param text text to change
     * @param color color to change to
     * @return colored text
     */
    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    /**
     * Handles click events for buttons on page.
     *
     * @param view current view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_login:
                emailButton.startAnimation(buttonPress);
                emailLogin();
                break;
            case R.id.google_login:
                googleButton.startAnimation(buttonPress);
                googleLogin();
                break;
            case R.id.facebook_login:
                facebookButton.startAnimation(buttonPress);
                facebookLoginButton.performClick();
                break;
            case R.id.sign_up:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.forgot:
                break;
        }
    }

    /**
     * Checks if user already signed in on start of activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Ends login activity if user is already signed in.
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
            finishLogin();
        }
    }

    /**
     * Initialize official authentication providers.
     */
    private void initializeOfficialLogin() {
        //initialize google login
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //initialize Facebook login
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    /**
     * Log in using email and password.
     */
    private void emailLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        EmailLoginTask login = new EmailLoginTask();
        login.execute(email, password);
    }

    /**
     * Log in using Google account.
     */
    private void googleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);
    }

    /**
     * Authenticate the user with Google.
     *
     * @param idToken user's id token
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            //handle merge here
                        }
                    }
                });
    }

    /**
     * Authenticate the user with Facebook.
     *
     * @param token user's id token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            //handle merge here
                        }
                    }
                });
    }

    /**
     * When the authentication is complete.
     *
     * @param requestCode activity request code
     * @param resultCode activity result code
     * @param data data from intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == G_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Logs in user from backend function using okhttp.
     */
    private class EmailLoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
//            try {
//                String url = "https://us-central1-habtrack-333.cloudfunctions.net/api/logintest";
//                RequestBody formBody = new FormBody.Builder()
//                        .add("email", params[0])
//                        .add("password", params[1])
//                        .build();
//
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(formBody)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                return response.body().string();
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//                return "";
//            }
            return "";
        }

        @Override
        protected void onPostExecute(String token) {
            finishLogin();
        }
    }

    /**
     * Finishes LoginActivity and moves to MainActivity.
     */
    private void finishLogin() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}