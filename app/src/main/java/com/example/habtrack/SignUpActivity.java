package com.example.habtrack;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialize back button
        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);
        TextView backText = (TextView) findViewById(R.id.back_text);
        backText.setOnClickListener(this);

        //initialize sign up button
        ImageButton signUp = (ImageButton) findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);
        TextView signUpText = (TextView) findViewById(R.id.sign_up_text);
        signUpText.setOnClickListener(this);

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
        if (id == R.id.back || id == R.id.back_text)
            finishAfterTransition();
        else
            finish();
    }
}