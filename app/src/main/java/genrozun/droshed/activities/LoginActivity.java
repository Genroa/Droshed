package genrozun.droshed.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import genrozun.droshed.R;
import genrozun.droshed.sync.SheetUpdateService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText user;
    private EditText password;
    private View mProgressView;
    private View mLoginFormView;

    //logins
    private SharedPreferences logins;


    private BroadcastReceiver receiver;

    public LoginActivity() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showProgress(false);
                String result = intent.getStringExtra("result");
                if (result.equals(SheetUpdateService.OPERATION_OK)) {
                    logins.edit().putString("droshed_user", user.getText().toString())
                            .putString("droshed_password", password.getText().toString())
                            .commit();

                    Intent priciplePageIntent = new Intent(getApplicationContext(), FileListActivity.class);
                    startActivity(priciplePageIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("droshed-auth"));
        // Set up the login form.
        logins = getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        user = (EditText) findViewById(R.id.user_name_input);
        password = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (!logins.getString("droshed_user", "").equals("")) {
            Log.i(LoginActivity.class.getName(), "enter condition "  + logins.getString("droshed_user", ""));

            user.setText(logins.getString("droshed_user", ""));
            password.setText(logins.getString("droshed_password", ""));
            //attemptLogin();
        }

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        user.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String userLogin = user.getText().toString();
        String passwordTxt = this.password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordTxt)) {
            Log.e(LoginActivity.class.getName(), passwordTxt);
            this.password.setError(getString(R.string.error_invalid_password));
            focusView = this.password;
            cancel = true;
        }

        // Check for a valid userLogin address.
        if (TextUtils.isEmpty(userLogin)) {
            user.setError(getString(R.string.error_field_required));
            focusView = user;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // Auth HTTP définie ici
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userLogin, passwordTxt.toCharArray());
                }
            });
            testConnexion();
        }
    }

    private void testConnexion() {
        SheetUpdateService.startCheckAuth(getApplicationContext());
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

