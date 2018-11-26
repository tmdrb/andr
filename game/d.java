package com.example.lee.game;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class d extends AppCompatActivity {
    private String html = "";
    Button button;
    JSONObject jsonObject;
    Handler handler;
    private Socket socket;
    PrintWriter out;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    TextView textView;
    private String ip = "192.168.0.7"; // IP
    private int port = 9000; // PORT번호

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d);
        jsonObject = new JSONObject();
        try {
            jsonObject.put("name","이승규");
            jsonObject.put("buy","true");
            jsonObject.put("te","3");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        textView = new TextView(getApplicationContext());
        button = new Button(getApplicationContext());
        addContentView(button,new ViewGroup.LayoutParams(300,100));
        addContentView(textView,new ViewGroup.LayoutParams(600,500));
        textView.setX(1000);
        textView.setText("시작");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket(ip,port);
                    networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
                    networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(networkWriter,true);
                    String a ="hi";
                    out.println(jsonObject);
                    while(true){
                    Message message = handler.obtainMessage();
                    message.obj = networkReader.readLine();
                    handler.sendMessage(message);
                    }
                }
                catch (Exception e){}
            }
        }).start();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                textView.setText((String)msg.obj);

            }
        };



        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        out.println("dd");
                    }
                }).start();

            }
        });
    }



}
class send extends Thread{
   Socket socket;
   BufferedWriter bf;
   PrintWriter out;
    public send(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            bf = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
            out = new PrintWriter(bf,true);
            String a ="hid";
            out.println(a);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}