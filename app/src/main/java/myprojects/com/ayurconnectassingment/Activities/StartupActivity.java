package myprojects.com.ayurconnectassingment.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import myprojects.com.ayurconnectassingment.R;
import myprojects.com.ayurconnectassingment.Session.SessionManager;

public class StartupActivity extends AppCompatActivity {

    private static final String TAG = "StartupActivity";

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());

        // delay the process by 2sec

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(session.isLoggedIn()){

                    Intent gotoSuggestionActivity = new Intent(StartupActivity.this,SuggestionActivity.class);
                    startActivity(gotoSuggestionActivity);
                    finish();

                }else{

                    Intent gotoLoginActivity = new Intent(StartupActivity.this,LoginActivity.class);
                    startActivity(gotoLoginActivity);
                    finish();

                }
            }
        }, 2000);
    }
}
