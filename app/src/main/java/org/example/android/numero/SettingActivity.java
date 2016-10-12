package org.example.android.numero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button save = (Button) findViewById(R.id.save);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText nickname = (EditText) findViewById(R.id.nickname);

        final SharedPreferences pref=getSharedPreferences("BasicUserDetail", Context.MODE_PRIVATE);
        String usernamex= pref.getString("username", "");
        username.setText(usernamex);

        String nicknamex= pref.getString("nickname", "");
        nickname.setText(nicknamex);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reply="";
                if(username.getText().toString()!=null){
                    SharedPreferences.Editor edit=pref.edit();
                    edit.putString("username", username.getText().toString());
                    edit.commit();
                    reply="Username:"+username.getText().toString();
                }
                if(nickname.getText().toString()!=null){
                    SharedPreferences.Editor edit=pref.edit();
                    edit.putString("nickname", nickname.getText().toString());
                    edit.commit();
                    if(reply != null || reply!="") {
                        reply =reply + " & Nickname:" + nickname.getText().toString();
                    }else{
                        reply ="Nickname:" + nickname.getText().toString();
                    }
                }
                Toast.makeText(SettingActivity.this,reply+" added successfully",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
