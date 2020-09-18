package com.amn.sos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthenticateActivity extends AppCompatActivity {

    private String realOTP;
    private String number;
    private EditText editOTP;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_authenticate);

        mAuth = FirebaseAuth.getInstance();
        editOTP = findViewById(R.id.OTP);
        Intent intent = getIntent();
        String mobileNum = intent.getStringExtra("number");
        Log.e("number", mobileNum);
        sendVerificationCode(mobileNum);
        findViewById(R.id.OTPButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editOTP.getText().toString().trim();
                if(code.isEmpty() || code.length() < 6) {
                    editOTP.setError("Enter valid code");
                    //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    editOTP.requestFocus();
                    //return;
                }
                else {
                    verifyOTP(code);
                }
            }
        });


    }

    private void sendVerificationCode(String mobileNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobileNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code!= null) {
                editOTP.setText(code);
                Toast.makeText(getApplicationContext(), "verifying", Toast.LENGTH_SHORT);
                verifyOTP(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.e("verification", "code sent");
            realOTP = s;
        }
    };

    private void verifyOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(realOTP, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(AuthenticateActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (newUser) {
                                //new user intent
                                Toast.makeText(getApplicationContext(), "Logged in as new user", Toast.LENGTH_SHORT);
                            }
                            else {
                                //old user
                                Intent intent = new Intent(AuthenticateActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
}
