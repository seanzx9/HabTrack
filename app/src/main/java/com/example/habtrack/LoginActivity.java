package com.example.habtrack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailInput;
    private EditText passwordInput;
    private Button emailButton;
    private LinearLayout googleButton;
    private LinearLayout facebookButton;
    private Animation buttonPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_login);

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

        //initialize text views
        TextView signUp = (TextView) findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);
        TextView forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

        //initialize animations
        buttonPress = AnimationUtils.loadAnimation(this, R.anim.button_press);
        buttonPress.reset();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_login:
                emailButton.startAnimation(buttonPress);
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                EmailLoginTask login = new EmailLoginTask();
                login.execute(email, password);
                break;
            case R.id.google_login:
                googleButton.startAnimation(buttonPress);
                break;
            case R.id.facebook_login:
                facebookButton.startAnimation(buttonPress);
                break;
            case R.id.sign_up:
                Intent intent = new Intent(this, SignUpActivity.class);
                Bundle b = new Bundle();
                b.putInt("loginType", 0);
                intent.putExtras(b);
                startActivity(intent);
                break;
            case R.id.forgot:
                break;
        }
    }

    private void finishLogin(String token) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
        finish();
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

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
            token = "It worked";
            finishLogin(token);
        }
    }
}