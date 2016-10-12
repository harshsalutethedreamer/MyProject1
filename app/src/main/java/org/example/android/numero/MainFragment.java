package org.example.android.numero;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<InformationActivity>> {

    private RecyclerView mRecyclerView;
    private NumeroRecyclerAdapter numeroRecyclerAdapter;
    private ContentResolver mContentResolver;
    private List<InformationActivity> mnumeros;
    private static int LOADER_ID=1;
    private static int LOADER_IDA = 2;
    SharedPreferences pref;
    private String username,matchText="",nickname;
    private EditText mSearchEditText;
    private ImageView nimageview_delete,nimageview_editcategory;
    private String mdelete_name;
    private int mdelete_id=-1;
    private Boolean refreshview=false;;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView= (ViewGroup) inflater.inflate(R.layout.activity_main2,container,false);

        mSearchEditText = (EditText) rootView.findViewById(R.id.search);
        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    matchText=mSearchEditText.getText().toString();
                    getActivity().getSupportLoaderManager().initLoader(LOADER_ID++, null,MainFragment.this);
                    return true;
                }
                return false;
            }
        });

        mRecyclerView=(RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        pref = getActivity().getSharedPreferences("BasicUserDetail", getActivity().MODE_PRIVATE);
        nickname= pref.getString("nickname", "");
        username= pref.getString("username", "");
        if(nickname != null && nickname != ""){
            TextView tusername = (TextView)rootView.findViewById(R.id.nickname);
            tusername.setText(nickname);
        }


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ImageView addcategory = (ImageView) rootView.findViewById(R.id.addcategory);
        addcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),AddActivity.class);
                startActivity(i);
            }
        });

        ImageView setting = (ImageView) rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),SettingActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID++, null,this);
        return rootView;
    }

    @Override
    public Loader<List<InformationActivity>> onCreateLoader(int id, Bundle args) {
        mContentResolver=getActivity().getContentResolver();
        Log.d("valuex",String.valueOf(id));
        if(id==1){
            return new NumeroListLoader(getActivity(),NumeroContract.URI_TABLE, mContentResolver);
        }else{
            return new NumerosSearchListLoader(getActivity(),NumeroContract.URI_TABLE,mContentResolver,matchText);
        }

    }

    @Override
    public void onLoadFinished(Loader<List<InformationActivity>> loader, List<InformationActivity> numeros) {
        mnumeros = numeros;
        Log.d("mumeros",String.valueOf(numeros.size()));
        if(numeros.size()>0) {
            numeroRecyclerAdapter = new NumeroRecyclerAdapter((MainActivity) getActivity(),getActivity(), mnumeros,getActivity().getSupportFragmentManager(),mRecyclerView);
            mRecyclerView.setAdapter(numeroRecyclerAdapter);

//            numeroRecyclerAdapter.setOnDeleteClickListener(new NumeroRecyclerViewHolder.Ondeletelistener() {
//                @Override
//                public void onDeletelistener(int _id, String name) {
//                    nimageview_delete.setClickable(true);
//                    nimageview_editcategory.setClickable(true);
//                    mdelete_name=name;
//                    mdelete_id=_id;
//                    Log.d("longclick",mdelete_name);
//                }
//            });
        }

        Log.d("finding",String.valueOf(mnumeros.size()));
    }

    @Override
    public void onLoaderReset(Loader<List<InformationActivity>> loader) {

    }
}
