package org.example.android.numero;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FavouriteActivity extends Fragment{

    private static int NUM_PAGES = 0;
    private int[] _id;
    private VerticalViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Cursor mCursor;
    private ContentResolver mContentResolver;
    SharedPreferences pref;
    private String username,nickname;
    private int in=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_screen_slide, container, false);

        pref = getActivity().getSharedPreferences("BasicUserDetail", getActivity().MODE_PRIVATE);
        nickname= pref.getString("nickname", "");
        if(nickname != null && nickname != ""){
            TextView tusername = (TextView)rootView.findViewById(R.id.nickname);
            tusername.setText(nickname);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Instantiate a ViewPager and a PagerAdapter.
        if(in>0){
            mPagerAdapter.notifyDataSetChanged();
            mPager.invalidate();
        }else{
            in=in+1;
            mPager = (VerticalViewPager) view.findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
            mPager.setAdapter(mPagerAdapter);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("positionva",String.valueOf(_id[position]));
            return new ScreenSlideFavourityFragment().newInstance(NUM_PAGES,_id,position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            mContentResolver = getActivity().getContentResolver();
            String[] projection = {BaseColumns._ID,
                    NumeroContract.NumeroColumns.NUMERO_SPECIAL};
            String selection = NumeroContract.NumeroColumns.NUMERO_SPECIAL + " == 1";
            String order = NumeroContract.NumeroColumns.NUMERO_SPECIAL+" DESC,"+ NumeroContract.NumeroColumns.NUMERO_UPDATEDDATE+" DESC";
            mCursor = mContentResolver.query(NumeroContract.URI_TABLE, projection,null, null,order);
            NUM_PAGES = mCursor.getCount();
            if(NUM_PAGES !=0){
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
                    mCursor.close();

                }
            }
            return NUM_PAGES;
        }
    }

}
