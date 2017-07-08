package myprojects.com.ayurconnectassingment.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import myprojects.com.ayurconnectassingment.Adapter.ImageAdapter;
import myprojects.com.ayurconnectassingment.Database.GeneralDatabaseHelper;
import myprojects.com.ayurconnectassingment.R;

public class ShowBookmarksActivity extends AppCompatActivity {

    private static final String TAG = "ShowBookmarksActivity";

    private RecyclerView myList;
    private FloatingActionButton fab;
    private ViewPager viewPager;
    private GeneralDatabaseHelper db;
    private Cursor cursor;
    private TextView textMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bookmarks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        textMsg = (TextView) findViewById(R.id.tv_bookmark_activity_msg);

        db = new GeneralDatabaseHelper(this);
        cursor = db.getAllBookmarkedPair();

        if(cursor.getCount() > 0) {
            ImageAdapter adapter = new ImageAdapter(this, cursor);
            viewPager.setAdapter(adapter);
            textMsg.setVisibility(View.GONE);
        }else{

            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
    }

    /*
    * Get root view of the window
    */

    public void shareImage(){

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        Bitmap bitmap = getScreenShot(rootView);

        store(bitmap, "bookmark");
    }

    /*
    * Get Screen shot of the rootView in bitmap image format
    */

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /*
    * Store bitmap into storage
    */

    public void store(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        shareImage(file);
    }

    /*
    * Share image with available image sharing application
    */

    private void shareImage(File file){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
