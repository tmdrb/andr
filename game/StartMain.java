package com.example.lee.game;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartMain extends AppCompatActivity {
    EditText editText;
    Button single,multi;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_main);
        single = (Button)findViewById(R.id.single);
        multi = (Button)findViewById(R.id.multi);
        editText =(EditText)findViewById(R.id.editText);
        intent = new Intent(this,GameMain.class);

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()==0){

                }
                else{
                   intent.putExtra("nickname",String.valueOf(editText.getText()));
                    startActivity(intent);
                }
            }
        });
    }
}

