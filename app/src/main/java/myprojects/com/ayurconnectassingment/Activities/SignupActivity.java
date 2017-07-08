package myprojects.com.ayurconnectassingment.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import myprojects.com.ayurconnectassingment.Database.DatabaseHelper;
import myprojects.com.ayurconnectassingment.R;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private Button btnSignup;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private TextView gotoLogin;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignup = (Button) findViewById(R.id.btn_signup);
        nameText = (EditText) findViewById(R.id.et_signup_name);
        emailText = (EditText) findViewById(R.id.et_signup_email);
        passwordText = (EditText) findViewById(R.id.et_signup_pass);
        gotoLogin = (TextView) findViewById(R.id.tv_login);

        db = new DatabaseHelper(this);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(db.addContentToMainDatabase(email.getText().toString(),password.getText().toString())){
                    Toast.makeText(SignupActivity.this, "Your Signup is successful, Please Login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }*/

                signup();
            }
        });
    }

    /*
    * Start signup process
    * Check for validation of the inputs
    * If all inputs are valid then proceed further
    * Else redirect to onSignupFailed
    */

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnSignup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(db.addContentToMainDatabase(email,password)){
            onSignupSuccess();
        }else{
            onSignupFailed();
        }

        progressDialog.dismiss();
    }

    /*
    * On successful signup
    * Redirect to Login Activity
    * finish this activity
    */


    public void onSignupSuccess() {
        btnSignup.setEnabled(true);

        Toast.makeText(SignupActivity.this, "Your Signup is successful, Please Login", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(i);

        finish();
    }

    /*
    * On signup failed
    * Do nothing
    */

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        btnSignup.setEnabled(true);
    }

    /*
    * Check for valid inputs
    * If all inputs are valid return true
    * Else set error
    */

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("Password field must contain atleast 4 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
