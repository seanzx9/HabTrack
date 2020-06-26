package com.example.habtrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {
    private int loginType;
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //get argument from login page
        Bundle b = getIntent().getExtras();
        loginType = -1;
        if(b != null)
            loginType = b.getInt("loginType");
        t = (TextView) findViewById(R.id.text);
        t.setText(Integer.toString(loginType));
    }
}