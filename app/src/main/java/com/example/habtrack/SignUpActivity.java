package com.example.habtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //animate sign in text out
        Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        final TextView signUpText = (TextView) findViewById(R.id.sign_up_text);

        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);
                signUpText.setText(R.string.sign_up);
                signUpText.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        signUpText.startAnimation(slideOut);

        //initialize back button
        TextView cancel = (TextView) findViewById(R.id.cancel_text);
        cancel.setOnClickListener(this);

        //initialize sign up button
        ConstraintLayout signUp = (ConstraintLayout) findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);

        //initialize input fields and handle Done buttons
        emailInput = (TextInputEditText) findViewById(R.id.email_input_edit);
        emailInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    emailInput.clearFocus();
                return false;
            }
        });
        passwordInput = (TextInputEditText) findViewById(R.id.password_input_edit);
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    passwordInput.clearFocus();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel_text) {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            slideOut.setDuration(150);
            final TextView signUpText = (TextView) findViewById(R.id.sign_up_text);

            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);
                    slideIn.setDuration(150);
                    signUpText.setText(R.string.login);

                    slideIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            finishAfterTransition();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    signUpText.startAnimation(slideIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            signUpText.startAnimation(slideOut);
        }
        else
            //sign in
            finish();
    }
}