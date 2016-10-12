package org.example.android.numero;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class AllVerticalActivity extends FragmentActivity{

    private static int NUM_PAGES = 0;
    private int[] _id;
    private VerticalViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Cursor mCursor;
    private ContentResolver mContentResolver;
    SharedPreferences pref;
    private String username,nickname;
    private ImageView iv;
    private int initialposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        Bundle bundle=getIntent().getExtras();
        initialposition=bundle.getInt("position");
        Log.d("initial",String.valueOf(initialposition));

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (VerticalViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(initialposition);

        pref = getSharedPreferences("BasicUserDetail", MODE_PRIVATE);
        nickname= pref.getString("nickname", "");

        if(nickname != null && nickname != ""){
            TextView tusername = (TextView)findViewById(R.id.nickname);
            tusername.setText(nickname);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlideTotalFragment().newInstance(NUM_PAGES,_id,position);
        }

        @Override
        public int getCount() {
            mContentResolver = AllVerticalActivity.this.getContentResolver();
            String[] projection = {BaseColumns._ID,
                    NumeroContract.NumeroColumns.NUMERO_SPECIAL};
            mCursor = mContentResolver.query(NumeroContract.URI_TABLE, projection,null, null, null);
            NUM_PAGES = mCursor.getCount();
            if (mCursor != null) {
                    int i =0;
                    _id =new int[mCursor.getCount()];
                    if (mCursor.moveToFirst()) {
                        do {
                            int id = mCursor.getInt(
                                    mCursor.getColumnIndex(NumeroContract.NumeroColumns.NUMERO_ID));
                            _id[i]=id;
                            i=i+1;
                        } while (mCursor.moveToNext());
                    }

                }

            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
