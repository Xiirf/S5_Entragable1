package com.example.entrega1;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0x52;
    private FirebaseAuth mAuth;
    private Button signInButtonGoogle, signInButtonMail, loginButtonSignUp;
    private ProgressBar progressBar;
    private AutoCompleteTextView loginEmail, loginPass;
    private TextInputLayout loginEmailParent, loginPassParent;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.login_progress);
        progressBar.setVisibility(View.GONE);
        loginEmail = findViewById(R.id.login_email_et);
        loginPass = findViewById(R.id.login_pass_et);
        loginEmailParent = findViewById(R.id.login_email);
        loginPassParent = findViewById(R.id.login_email);
        signInButtonGoogle = findViewById(R.id.login_button_google);
        signInButtonMail = findViewById(R.id.login_button_mail);
        loginButtonSignUp = findViewById(R.id.login_button_register);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        signInButtonGoogle.setOnClickListener(l -> attemptLoginGoogle(googleSignInOptions));

        signInButtonMail.setOnClickListener(l -> attemptLoginEmail());

        loginButtonSignUp.setOnClickListener(l -> redirectSignUpActivity());
    }

    private void redirectSignUpActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(SignupActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);
    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient googleSignIn = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Toast.makeText(LoginActivity.this, "test", Toast.LENGTH_LONG).show();
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                if (mAuth == null) {
                    mAuth = FirebaseAuth.getInstance();
                }
                if (mAuth != null) {
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            checkUserDatabaseLogin(user);
                        } else {
                            showErrorDialogMail();
                        }
                    });
                } else {
                    showGooglePlayServicesError();
                }
            } catch (ApiException e) {
                showErrorDialogMail();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void attemptLoginEmail() {
        hideLoginButton(true);
        loginEmailParent.setError(null);
        loginPassParent.setError(null);

        if (loginEmail.getText().length() == 0) {
            loginEmailParent.setErrorEnabled(true);
            hideLoginButton(false);
            loginEmailParent.setError(getString(R.string.login_email_error_1));
        } else if (loginPass.getText().length() == 0) {
            loginPassParent.setErrorEnabled(true);
            hideLoginButton(false);
            loginPassParent.setError(getString(R.string.login_mail_error_2));
        } else {
            signInEmail();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signInEmail() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString()).addOnCompleteListener(this, task -> {
               if (!task.isSuccessful() || task.getResult().getUser() == null) {
                   showErrorDialogMail();
               } else if (!task.getResult().getUser().isEmailVerified()) {
                   showErrorEmailVerified(task.getResult().getUser());
               } else {
                   FirebaseUser user = task.getResult().getUser();
                   checkUserDatabaseLogin(user);
               }
            });
        } else {
            showGooglePlayServicesError();
        }
    }

    private void showGooglePlayServicesError() {
        Snackbar.make(loginButtonSignUp, R.string.login_google_play_service_error, Snackbar.LENGTH_SHORT).setAction(R.string.login_download_gps, view -> {
           try {
               startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gps_download_url))));
           } catch (Exception ex) {
               startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_download_url))));
           }
        });
    }

    private void checkUserDatabaseLogin(FirebaseUser user) {
        //Dummy
        //TODO: complete
        Toast.makeText(this, String.format(getString(R.string.login_completed), user.getEmail()), Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showErrorEmailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_verified_mail_error)
                .setPositiveButton(R.string.login_verified_mail_error_ok, ((dialog, which) -> {
                    user.sendEmailVerification().addOnCompleteListener( task1 -> {
                        if (task1.isSuccessful()) {
                            Snackbar.make(loginEmail, R.string.login_verified_email_error_sent, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(loginEmail, R.string.login_verified_email_error_no_sent, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }))
                .setNegativeButton(R.string.login_verified_mail_error_cancel, (dialog, which)-> {
                }).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideLoginButton(boolean hide) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(1000);
        transitionSet.addTransition(layoutFade);

        if (hide) {
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            progressBar.setVisibility(View.VISIBLE);
            signInButtonMail.setVisibility(View.GONE);
            signInButtonGoogle.setVisibility(View.GONE);
            loginButtonSignUp.setVisibility(View.GONE);
            loginEmailParent.setEnabled(false);
            loginPassParent.setEnabled(false);
        } else {
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            progressBar.setVisibility(View.GONE);
            signInButtonMail.setVisibility(View.VISIBLE);
            signInButtonGoogle.setVisibility(View.VISIBLE);
            loginButtonSignUp.setVisibility(View.VISIBLE);
            loginEmailParent.setEnabled(true);
            loginPassParent.setEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(signInButtonMail, getString(R.string.login_email_access_error), Snackbar.LENGTH_SHORT).show();
    }
}
