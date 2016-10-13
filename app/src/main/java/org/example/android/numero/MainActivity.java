package org.example.android.numero;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
        private ViewPager mViewPager;
        private PagerAdapter mPager;
        Bitmap bitmap1;
        private AsyncTask task;
    private String username,nickname;
    SharedPreferences pref;
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity=MainActivity.this;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        actionBar.setDisplayShowTitleEnabled(false);

        LinearLayout startlinearLayout=(LinearLayout)findViewById(R.id.linearstart);
        startlinearLayout.setOnClickListener(this);

        pref = getSharedPreferences("BasicUserDetail", MODE_PRIVATE);
        nickname= pref.getString("nickname", "");
        username= pref.getString("username", "");
        if(nickname != null && nickname != ""){
            TextView tusername = (TextView)findViewById(R.id.nickname);
            tusername.setText(nickname.toUpperCase());
        }

        mViewPager=(ViewPager)findViewById(R.id.pager);
        mPager=new SliderAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPager);
        mViewPager.setCurrentItem(1);

        final ImageView imageView=(ImageView)findViewById(R.id.action_settings);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    public class SliderAdapter extends FragmentStatePagerAdapter{

        public SliderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("working","check");
            if(position==0){
                return new FavouriteActivity();
            }else{
                return new MainFragment();
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    void setAdapter(int position) {
        Log.d("mainsetadapter",String.valueOf(position));
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            int countryCode = data.getExtras().getInt(DialogChoiceActivity.RESULT_POSITION);
            int _id = data.getExtras().getInt(DialogChoiceActivity.RESULT_ID);
            String name = data.getExtras().getString(NumeroContract.NumeroColumns.NUMERO_CATEGORY);
            if(countryCode==0){
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                i.putExtra("_id",_id);
                startActivity(i);
            }else if(countryCode==1){
                NumeroDialog dialog = new NumeroDialog();
                Bundle args = new Bundle();
                args.putString(NumeroDialog.DIALOG_TYPE, NumeroDialog.DELETE_RECORD);
                args.putInt(NumeroContract.NumeroColumns.NUMERO_ID,_id);
                args.putString(NumeroContract.NumeroColumns.NUMERO_CATEGORY,name.toUpperCase());
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(),"delete-record");
            }else if(countryCode==2){
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                    String category_name = name.toLowerCase();
                    File folder = new File(Environment.getExternalStorageDirectory() + "/numeros/" + category_name);
                    if (folder.exists()) {
                        Intent i = new Intent(MainActivity.this,GalleryActivity.class);
                        i.putExtra("category_name",category_name);
                        i.putExtra("_id",_id);
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, "No Image Exist", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "External storage is not mounted READ/WRITE", Toast.LENGTH_LONG).show();
                }
            }else if(countryCode==3){
                String category_name=name.toLowerCase();
                Log.d("mustcal",category_name);
                File folder = new File(Environment.getExternalStorageDirectory() + "/numeros/" + category_name);
                if (folder.exists()) {
                    video(category_name);
                  task=new MainActivityAsyncTask(MainActivity.this,category_name).execute();
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/numero" + category_name + ".mp4"));
//                    intent.setDataAndType(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/numero" + category_name + ".mp4"), "video/mp4");
//                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "No Image Exist for creating video", Toast.LENGTH_SHORT).show();
                }
            }else if(countryCode==4){
                String category_name=name.toLowerCase();
                File folder = new File(Environment.getExternalStorageDirectory() + "/numeros/" + category_name);
                if (folder.exists()) {
                    this.video(category_name);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    Uri uri = Uri.fromFile(GetSDPathToFile("",category_name + ".mp4"));
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setDataAndType(uri, "video/*");
                    intent.setType("video/mp4");
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "No Image Exist for creating and sharing video", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void video(String category_name) {
        try {
            File file = this.GetSDPathToFile("",category_name+".mp4");
            NewSequenceEncoder encoder = new NewSequenceEncoder(file);
            File folder = new File(Environment.getExternalStorageDirectory() + "/numeros/" + category_name);
            File[] listFile = folder.listFiles();
            String text="NUMERO";
            // only 5 frames in total
            Log.d("problem1",String.valueOf(listFile.length));
            for (int i = 1; i <= listFile.length; i++) {
                String FilePathStrings=listFile[i-1].getAbsolutePath();
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmapa = BitmapFactory.decodeFile(FilePathStrings,bmOptions);
                Bitmap mutableBitmap = bitmapa.copy(Bitmap.Config.ARGB_8888, true);
                Typeface tf = Typeface.create("google lato", Typeface.BOLD);

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLUE);
                paint.setTypeface(tf);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(convertToPixels(getApplicationContext(),6));

                Rect textRect = new Rect();
                paint.getTextBounds(text, 0, text.length(), textRect);
                //            Log.d("dee","reached here2");
                Canvas canvas = new Canvas(mutableBitmap);
                Log.d("dee","reached here3");
                //If the text is bigger than the canvas , reduce the font size
                if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
                    paint.setTextSize(convertToPixels(getApplicationContext(), 4));        //Scaling needs to be used for different dpi's

                //Calculate the positions
                int xPos = (canvas.getWidth()) - 55;     //-2 is for regulating the x position offset

                //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
                int yPos = (int) ((canvas.getHeight()) - 10/*((paint.descent() + paint.ascent()) / 2)*/);

                canvas.drawText("NUMERO", xPos, yPos, paint);
                // int bitmapResId = this.getB.getIdentifier("image" + (i),"drawable", this.getPackageName());
                //Log.d("dee", this.getResources().getIdentifier("image" + (i),  "drawable", this.getPackageName()) + "  " +" is the no");
                Log.d("dee","image"+i);
                //    Log.d("dee",this.getBitmapFromResources(getApplicationContext().getResources(),bitmapResId).toString());
                //  Bitmap bitmap = getBitmapFromResources(this.getResources(), bitmapResId);
                //Log.d("dee",R.drawable.class.getResource().l   +"  fields in drawable");
                //BitmapDrawable bitmap1 = this.writeTextOnDrawable(bitmapResId,"NUMERO",getApplicationContext());
                Picture pic = this.fromBitmap(mutableBitmap);
                //  Toast.makeText(getApplicationContext(),"Image +" + i,Toast.LENGTH_SHORT).show();
                encoder.encodeNativeFrame(pic);
                Log.d("dee","reached here4");
            }
            int bitmapResId = this.getResources().getIdentifier("six", "drawable", this.getPackageName());
            Bitmap bitmap = getBitmapFromResources(this.getResources(), bitmapResId);
            Log.d("positionhello","helloend");
            Picture pic = this.fromBitmap((bitmap));
            encoder.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromResources(Resources resources, int bitmapResId) {
        return BitmapFactory.decodeResource(resources, bitmapResId);
    }

    // get full SD path

    //Change the path in the below function harsh bhaiya
    protected File GetSDPathToFile(String filePatho, String fileName) {
        File extBaseDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d("dee",  Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString());
        if (filePatho == null || filePatho.length() == 0 || filePatho.charAt(0) != '/')
            filePatho = "/" + filePatho;

        createDirIfNotExists(filePatho);
        File file = new File(extBaseDir.getAbsoluteFile() + filePatho);

        return new File(file.getAbsolutePath() + "/" + fileName);// file;
    }

    //cahnge in this one too
    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    // convert from Bitmap to Picture (jcodec native structure)
    public Picture fromBitmap(Bitmap src) {
        Log.d("frombitmap", src.getWidth() + "");
        Log.d("frombitmap", src.getHeight() + "");
        Picture dst = Picture.create((int)src.getWidth(), (int)src.getHeight(), ColorSpace.RGB);
        fromBitmap(src, dst);
        return dst;
    }

    public void fromBitmap(Bitmap src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];

        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for (int i = 0, srcOff = 0, dstOff = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++, srcOff++, dstOff += 3) {
                int rgb = packed[srcOff];
                dstData[dstOff]     = (rgb >> 16) & 0xff;
                dstData[dstOff + 1] = (rgb >> 8) & 0xff;
                dstData[dstOff + 2] = rgb & 0xff;
            }
        }
    }

    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    @Override
    public void onClick(View view) {
            if(view.getId()==R.id.linearstart){
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
    }

    public void successfulvideocreated(){
        Toast.makeText(activity,"Video save successfully in gallery",Toast.LENGTH_LONG);
    }
}
