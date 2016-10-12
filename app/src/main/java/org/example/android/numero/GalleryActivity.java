package org.example.android.numero;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;

public class GalleryActivity extends FragmentActivity {

    private String category_name;
    private int _id;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile=null;
    private RecyclerView mRecyclerView;
    private GalleryRecylcerAdapter1 galleryRecylcerAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        mRecyclerView=(RecyclerView) findViewById(R.id.galleryall_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));

        Bundle bundle=getIntent().getExtras();
        category_name=bundle.getString("category_name");
        _id=bundle.getInt("_id");
        File folder = new File(Environment.getExternalStorageDirectory() + "/numeros/" + category_name);
        if (folder.exists()) {
            listFile = folder.listFiles();
            if(listFile!=null){
                if(folder.isDirectory()) {
                    // Create a String array for FilePathStrings
                    FilePathStrings = new String[listFile.length];
                    // Create a String array for FileNameStrings
                    FileNameStrings = new String[listFile.length];
                    for (int i = 0; i < listFile.length; i++) {
                        // Get the path of the image file
                        FilePathStrings[i] = listFile[i].getAbsolutePath();
                        // Get the name image file
                        FileNameStrings[i] = listFile[i].getName();
                    }

                    galleryRecylcerAdapter1 = new GalleryRecylcerAdapter1(GalleryActivity.this,FilePathStrings,FileNameStrings,category_name);
                    mRecyclerView.setAdapter(galleryRecylcerAdapter1);
                }
                }
            else{
                Intent i = new Intent(GalleryActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }else{
            Intent i = new Intent(GalleryActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}
