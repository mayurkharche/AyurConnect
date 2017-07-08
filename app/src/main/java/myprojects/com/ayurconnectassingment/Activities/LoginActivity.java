package myprojects.com.ayurconnectassingment.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import myprojects.com.ayurconnectassingment.Database.DatabaseHelper;
import myprojects.com.ayurconnectassingment.Database.GeneralDatabaseHelper;
import myprojects.com.ayurconnectassingment.R;
import myprojects.com.ayurconnectassingment.Session.SessionManager;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LoginActivity";
    private static final int GOOGLE_SIGN_IN = 9823;

    private Button btnLogin;
    private LoginButton loginButton;
    private EditText emailText;
    private EditText passwordText;
    private TextView gotoSignup;
    private CallbackManager callbackManager;
    private static GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;

    private DatabaseHelper db;

    // Session Manager Class
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        emailText = (EditText) findViewById(R.id.et_login_email);
        passwordText = (EditText) findViewById(R.id.et_login_pass);
        gotoSignup = (TextView) findViewById(R.id.tv_signup);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        db = new DatabaseHelper(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){

            @Override
            public void onSuccess(LoginResult loginResult) {

                String facebookId = loginResult.getAccessToken().getUserId();

                String dbid = socialSignup(facebookId);

                if(dbid != null){

                    GeneralDatabaseHelper.setDatabaseName(dbid);

                    session.createLoginSession(facebookId, dbid);

                    Intent i = new Intent(LoginActivity.this, SuggestionActivity.class);
                    startActivity(i);
                    finish();

                }
            }

            @Override
            public void onCancel() {

                Toast.makeText(LoginActivity.this, "Login attempt canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(LoginActivity.this, "Login attempt failed", Toast.LENGTH_SHORT).show();

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        gotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();
            }
        });
    }

    /*
    * Start login process
    * Check for validation of the inputs
    * If all inputs are valid then proceed further
    * Else redirect to onLoginFailed
    */

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        //Authentication

        if(db.checkCredentials(email,password)){
            onLoginSuccess();
        }else{
            onLoginFailed();
        }

        progressDialog.dismiss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /*
    * On successful login
    * Get the database id for the corresponding user
    * Start the session
    * Redirect to Suggestion Activity
    * finish this activity
    */

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);

        String emailId = emailText.getText().toString();

        String dbid = db.getDbid(emailId);

        if(dbid != null) {

            GeneralDatabaseHelper.setDatabaseName(dbid);

            session.createLoginSession(emailId, dbid);

            Intent i = new Intent(LoginActivity.this, SuggestionActivity.class);
            startActivity(i);

        }

        finish();
    }

    /*
    * On login failed
    * Do nothing
    */

    public void onLoginFailed() {

        Toast.makeText(getBaseContext(), "Login failed!!!", Toast.LENGTH_LONG).show();

        btnLogin.setEnabled(true);
    }

    /*
    * Check for valid inputs
    * If all inputs are valid return true
    * Else set error
    */

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("Password field should not be empty");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    /*
    * Store facebook user id to the main database
    * return the corresponding database id
    */

    public String socialSignup(String userId){

        db.addContentToMainDatabase(userId,null);

        String dbid = db.getDbid(userId);

        return dbid;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }


    public static void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String email = acct.getEmail();

            String dbid = socialSignup(email);

            if(dbid != null){

                GeneralDatabaseHelper.setDatabaseName(dbid);

                session.createLoginSession(email, dbid);

                Intent i = new Intent(LoginActivity.this, SuggestionActivity.class);
                startActivity(i);
                finish();

            }

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Login Failed!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
