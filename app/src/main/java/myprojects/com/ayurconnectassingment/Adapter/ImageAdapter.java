package myprojects.com.ayurconnectassingment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import myprojects.com.ayurconnectassingment.Database.GeneralDatabaseHelper;
import myprojects.com.ayurconnectassingment.R;

/**
 * Created by mayur on 7/7/17.
 */

public class ImageAdapter extends PagerAdapter {

    private Context context;
    private Cursor cursor;
    private LayoutInflater layoutInflater;

    public ImageAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position) {

        View view = this.layoutInflater.inflate(R.layout.bookmark_list_item, container, false);

        ImageView imgShirt = (ImageView) view.findViewById(R.id.iv_bookmark_shirt);
        ImageView imgPant = (ImageView) view.findViewById(R.id.iv_bookmark_pant);

        cursor.moveToPosition(position);

        Picasso.with(context).load(cursor.getString(0)).resize(300,300).into(imgShirt);
        Picasso.with(context).load(cursor.getString(1)).resize(300,300).into(imgPant);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
         container.removeView((View) object);
    }
}
