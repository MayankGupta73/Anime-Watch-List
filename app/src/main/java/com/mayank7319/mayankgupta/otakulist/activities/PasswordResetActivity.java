package com.mayank7319.mayankgupta.otakulist.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mayank7319.mayankgupta.otakulist.R;

public class PasswordResetActivity extends AppCompatActivity {

    AutoCompleteTextView mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mFirebaseAuth;
    Context ctx;

    public static final String TAG = "Anime Watch List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_password_reset);

        mFirebaseAuth = FirebaseAuth.getInstance();
        ctx = this;

        mEmailView = findViewById(R.id.email);
        Button mPasswordResetButton = findViewById(R.id.password_reset_button);

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);

        mPasswordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    void resetPassword(){
        mEmailView.setError(null);
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            sendPasswordResetEmail(email);
        }
    }

    void sendPasswordResetEmail(String email){
        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showProgress(false);
                            mEmailView.setText("");
                            Toast.makeText(ctx, "Password Reset Email sent. Please check your inbox.",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Password Reset Email sent");
                        }
                        else{
                            Toast.makeText(ctx, "Error sending email. Please try again later.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}
