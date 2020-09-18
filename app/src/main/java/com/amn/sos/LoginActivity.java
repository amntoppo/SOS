package com.amn.sos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = findViewById(R.id.phoneNumber);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneNumber.getText().toString().trim();
                if(number.isEmpty() || number.length() < 10 ) {
                    phoneNumber.setError("Enter valid mobile number");
                    phoneNumber.requestFocus();
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, AuthenticateActivity.class);
                    intent.putExtra("number", number);
                    startActivity(intent);
                }

            }
        });


    }
}
