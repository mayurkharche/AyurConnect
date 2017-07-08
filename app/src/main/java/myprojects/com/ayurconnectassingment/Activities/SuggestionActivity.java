package myprojects.com.ayurconnectassingment.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.Random;

import myprojects.com.ayurconnectassingment.Animation.BounceInterpolator;
import myprojects.com.ayurconnectassingment.Database.GeneralDatabaseHelper;
import myprojects.com.ayurconnectassingment.ImagePicker.PickerBuilder;
import myprojects.com.ayurconnectassingment.R;
import myprojects.com.ayurconnectassingment.Session.SessionManager;

public class SuggestionActivity extends AppCompatActivity {

    private static final String TAG = "SuggestionActivity";

    private GeneralDatabaseHelper db;
    private Button btnBookmark;
    private Button btnDislike;
    private Button btnBookmarkActive;
    private Button btnDislikeActive;
    private Button btnGotoBookmark;
    private ImageView ranShirt;
    private ImageView ranPant;
    private ImageView refresh;
    private long availableShirts=0;
    private long availablePants=0;
    private String shirtUri,pantUri;
    private SessionManager session;
    private CardView cardView;
    private TextView msg;
    private LoginManager loginManager;
    private Boolean isBookmarked = false;
    private com.github.clans.fab.FloatingActionButton btnShirt;
    private com.github.clans.fab.FloatingActionButton btnPant;
    private CharSequence options[] = new CharSequence[] {"Camera", "Gallery"};
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggession);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize();

        updateData();

        suggestPair();

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        btnShirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on options[which]

                        switch(which){
                            case 0 : addShirtThroughCamera();
                                break;
                            case 1:addShirtThroughGallery();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        btnPant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on options[which]

                        switch(which){
                            case 0 :addPantThroughCamera();
                                break;
                            case 1:addPantThroughGallery();
                                break;
                        }
                    }
                });

                builder.show();
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnDislike.setAnimation(myAnim);
                onDislikeClicked();

            }
        });

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnBookmark.setVisibility(View.GONE);
                btnBookmarkActive.setVisibility(View.VISIBLE);

                isBookmarked = true;

                btnBookmark.setAnimation(myAnim);
            }
        });

        btnBookmarkActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isBookmarked) {
                    btnBookmark.setVisibility(View.VISIBLE);
                    btnBookmarkActive.setVisibility(View.GONE);

                    isBookmarked = false;

                    btnBookmark.setAnimation(myAnim);
                }
            }
        });

        btnGotoBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SuggestionActivity.this, ShowBookmarksActivity.class);
                startActivity(i);

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(availableShirts > 0 && availablePants > 0){
                    cardView.setVisibility(View.VISIBLE);

                    suggestPair();
                }

            }
        });
    }

    /*
    * Initialize all the variables
    */

    public void initialize(){

        builder = new AlertDialog.Builder(SuggestionActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Select your option:");

        db = new GeneralDatabaseHelper(this);
        session = new SessionManager(getApplicationContext());

        btnShirt = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.btn_add_shirt);
        btnPant = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.btn_add_pant);

        btnBookmark = (Button) findViewById(R.id.btn_bookmark);
        btnBookmarkActive = (Button) findViewById(R.id.btn_bookmark_active);
        btnGotoBookmark = (Button) findViewById(R.id.btn_goto_bookmark);
        btnDislike = (Button) findViewById(R.id.btn_dislike);
        btnDislikeActive = (Button) findViewById(R.id.btn_dislike_active);
        refresh = (ImageView) findViewById(R.id.iv_refresh);

        ranShirt = (ImageView)findViewById(R.id.iv_shirt);
        ranPant = (ImageView)findViewById(R.id.iv_pant);

        cardView = (CardView) findViewById(R.id.card_view);
        msg = (TextView) findViewById(R.id.tv_main_screen_msg);
    }

    /*
    * Update availability of available shirts and pants
    */
    public void updateData(){

        availableShirts = db.getShirtCollectionCount();
        availablePants = db.getPantCollectionCount();
    }

    /*
    * Suggest a random pair from available shirts and pants
    * Check for available shirts and pants
    * If there are none then show the message
    * Check whether pair is already bookmarked or not
    */

    public void suggestPair(){

        if(availableShirts != 0 && availablePants != 0) {

            long shirtNumber = getRandomNumber(availableShirts);
            long pantNumber = getRandomNumber(availablePants);

            shirtUri = db.getShirtFromCollection(shirtNumber);
            pantUri = db.getPantFromCollection(pantNumber);

            if(db.isBookmarked(shirtUri,pantUri)){
                btnBookmark.setVisibility(View.GONE);
                btnBookmarkActive.setVisibility(View.VISIBLE);
            }else{
                btnBookmark.setVisibility(View.VISIBLE);
                btnBookmarkActive.setVisibility(View.GONE);
            }

            Picasso.with(this).load(shirtUri).resize(300,300).into(ranShirt);
            Picasso.with(this).load(pantUri).resize(300,300).into(ranPant);

        }else{
            //Toast.makeText(SuggestionActivity.this, "Please add atleast one pair of Shirt & Pant", Toast.LENGTH_SHORT).show();
            cardView.setVisibility(View.GONE);
        }
    }

    /*
    * Returns a random number from 0 to range-1
    */
    public long getRandomNumber(long range){

        return (new Random()).nextInt((int) range)+1;
    }

    /*
    * Suggest another random pair
    * If shirts and pants are available
    */

    public void onDislikeClicked(){

        if(availableShirts !=0 && availablePants !=0)
            suggestPair();
    }

    /*
    * Add pair to bookmark table
    * */
    public void onBookmarkClicked(){

        if(shirtUri != null && pantUri != null)
            db.addToBookmark(shirtUri,pantUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.menu_logout:
                session.logoutUser();

                try {
                    loginManager.getInstance().logOut();
                }catch(Exception e){
                    Log.e(TAG,"Something wrong when logging out from facebook");
                }

                try {
                    LoginActivity.signOut();
                }catch(Exception e){
                    Log.e(TAG,"Something wrong when logging out from Google");
                }

                finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isBookmarked){
            onBookmarkClicked();
        }
    }

    /*
    * Activate camera to add shirt
    * */
    public void addShirtThroughCamera(){

        new PickerBuilder(SuggestionActivity.this, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {

                        db.addToShirtCollection(imageUri.toString());
                        updateData();

                    }
                }).start();
    }

    /*
    * Activate camera to add pant
    * */
    public void addPantThroughCamera(){

        new PickerBuilder(SuggestionActivity.this, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {

                        db.addToPantCollection(imageUri.toString());
                        updateData();

                    }
                }).start();
    }

    /*
    * Open gallery to add shirt
    * */
    public void addShirtThroughGallery(){

        new PickerBuilder(SuggestionActivity.this, PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {

                        db.addToShirtCollection(imageUri.toString());
                        updateData();

                    }
                }).start();
    }

    /*
    * Open gallery to add pant
    * */
    public void addPantThroughGallery(){

        new PickerBuilder(SuggestionActivity.this, PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {

                        db.addToPantCollection(imageUri.toString());
                        updateData();

                    }
                }).start();
    }
}
