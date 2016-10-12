package org.example.android.numero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public class SecondActivity extends FragmentActivity {

    SharedPreferences pref;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Intent i = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 100);

    }

}
