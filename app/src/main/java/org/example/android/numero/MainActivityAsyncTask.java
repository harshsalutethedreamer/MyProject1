package org.example.android.numero;

import android.content.Context;
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
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.IOException;

/**
 * Created by harshgupta on 11/10/16.
 */
public class MainActivityAsyncTask extends AsyncTask<Void,Void,Void> {

    String category_name;
    Context ncontext;
    Bitmap bitmap1;

    public MainActivityAsyncTask(Context context,String category_name) {
        this.category_name = category_name;
        ncontext=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((MainActivity) ncontext).successfulvideocreated();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("working","working");
        video(category_name);
        return null;
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
                paint.setTextSize(convertToPixels(ncontext.getApplicationContext(),6));

                Rect textRect = new Rect();
                paint.getTextBounds(text, 0, text.length(), textRect);
                //            Log.d("dee","reached here2");
                Canvas canvas = new Canvas(mutableBitmap);
                Log.d("dee","reached here3");
                //If the text is bigger than the canvas , reduce the font size
                if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
                    paint.setTextSize(convertToPixels(ncontext.getApplicationContext(), 4));        //Scaling needs to be used for different dpi's

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
            int bitmapResId = ncontext.getResources().getIdentifier("six", "drawable", ncontext.getPackageName());
            Bitmap bitmap = getBitmapFromResources(ncontext.getResources(), bitmapResId);
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
        Picture dst = Picture.create((int)src.getWidth(), (int)src.getHeight(), ColorSpace.RGB);
        fromBitmap(src, dst);
        Log.d("dee", dst.getWidth() + "");
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
}
