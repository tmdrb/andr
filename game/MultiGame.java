package com.example.lee.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;
class Client extends Thread{
    Socket socket;
    Rec rec;
    @Override
    public void run() {

        try {
            socket = new Socket("192.168.0.7",9000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class Rec extends Thread{
    Socket socket;
    BufferedReader bf;
    String getcon;
    public Rec(Socket socket) throws IOException {
        this.socket =socket;
        bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        getcon =null;
    }
    public String GetString(){

        return getcon;
    }

    @Override
    public void run() {
        while (true){
            try {
                if(bf.readLine() != null){
                    getcon = bf.readLine();
                    Log.e("tt",getcon);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
public class MultiGame extends AppCompatActivity {
    Random random;
    AttributeSet attributeSet;
    Inflater inflater;
    ArrayList<Character> characterList;
    Board board;
    AllTax allTax;
    ArrayList<Status> status;
    DiceButton diceButton;
    Background background;
    PlayerOrder playerOrder;
    ArrayList<Bitmap> bitmaps;
    Result result;
    AllCard allCard;
    Handler handler;

    Client client;
    private Socket socket;
    PrintWriter out;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private String ip = "192.168.0.7"; // IP
    private int port = 9000; // PORT번호
    BufferedReader bf;
    ArrayList<Land> Lands;
    int rand1,rand2,num1,ch,order,before,d,f;
    boolean dice ;
    ConstraintLayout constraintLayout;
    ConstraintLayout.LayoutParams layoutParams;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        client = new Client();
        client.start();
        while(true) {
            if(client.socket!=null)
                break;
            Log.e("tt", String.valueOf(client.socket));
        }
        Log.e("tt", String.valueOf(client.socket));
        socket = client.socket;
        constraintLayout = (ConstraintLayout)findViewById(R.id.board);
        playerOrder = new PlayerOrder();
        status = new ArrayList<>();
        Lands = new ArrayList<>();
        random = new Random();
        board = new Board(getApplicationContext(),null);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what ==0){
                    diceButton.setVisibility(View.GONE);
                    result.setVisibility(View.VISIBLE);}
                else if(msg.what ==1){
                    diceButton.setVisibility(View.GONE);
                    allCard.setVisibility(View.VISIBLE);
                }
                else if(msg.what ==2){
                    diceButton.setVisibility(View.VISIBLE);
                    allCard.setVisibility(View.GONE);
                }
                else if(msg.what ==3){
                    diceButton.setVisibility(View.GONE);
                    allTax.setVisibility(View.VISIBLE);
                }
                else if(msg.what ==4){
                    diceButton.setVisibility(View.VISIBLE);
                    allTax.setVisibility(View.GONE);
                }

            }
        };
        diceButton = board.diceButton;
        background = board.background;
        result = board.result;
        allTax = board.allTax;
        allCard = board.allCard;
        init();
        result.setCharacter(characterList.get(0));
        constraintLayout.addView(board);
        diceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:

                        rand1 =random.nextInt(6)+1;
                        rand2 =random.nextInt(6)+1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                PrintWriter pw = null;
                                try {
                                    pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
                                    pw.println(String.valueOf(rand2+rand1));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                        if(before == 8) {
                            if(rand1 != rand2 || d<2){
                                d++;
                                Log.e("tt",String.valueOf(d));
                                dice =true;
                            }
                            if(rand2 == rand1 ||d>2){
                                Log.e("tt","d");
                                before += rand1+rand2;
                                characterList.get(playerOrder.order % 2).x = Lands.get(before).x;
                                characterList.get(playerOrder.order % 2).y = Lands.get(before).y;
                                d=0;
                                background.invalidate();

                                dice = true;
                            }
                        }
                        else {
                            before += rand1+rand2;
                            if(before > 31){
                                characterList.get(playerOrder.order%2).money+=30;
                                before -= 32;
                            }
                            characterList.get(playerOrder.order % 2).x = Lands.get(before).x;
                            characterList.get(playerOrder.order % 2).y = Lands.get(before).y;
                            background.invalidate();
                            dice = true;
                        }
                        break;
                }

                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    try {
                        bf = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                        String a = bf.readLine();
                        int b = Integer.parseInt(a);
                        if(a!=null){

                            for(Character c: characterList){

                                if(c.name == "컴퓨터"){
                                    c.x = Lands.get(b).x;
                                    c.y = Lands.get(b).y;
                                    c.Mbuy(Lands.get(b));
                                    background.invalidate();
                                    break;

                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
     new Thread(new Runnable() {
            @Override

            public void run() {

                while(true){

                    for(Character c: characterList){
                        if(c.money < 0){

                        }
                    }
                    for(Status s:status){
                        s.invalidate();
                    }

                    board.invalidate();
                    if(playerOrder.order%2==0 && dice == true){
                        if(before ==0 || before ==32){
                            before += rand1+rand2;
                            characterList.get(playerOrder.order % 2).x = Lands.get(before).x;
                            characterList.get(playerOrder.order % 2).y = Lands.get(before).y;
                            background.invalidate();
                            dice = false;
                            playerOrder.order++;

                        }
                        else if(before == 2){

                            if(random.nextInt(2) == 1){
                                Log.e("tt","Wkr");
                                characterList.get(playerOrder.order%2).money += 50;
                            }
                            background.invalidate();
                            playerOrder.order++;
                            dice = false;

                        }
                        else if(before == 8){
                            dice = false;
                            playerOrder.order++;
                        }
                        else if(before == 12 || before ==20 || before==28){

                            if(allCard.check ==false){

                                Message cardmessage = handler.obtainMessage();
                                cardmessage.what =1;
                                handler.sendMessage(cardmessage);

                            }
                            else if(allCard.check){
                                Message message = handler.obtainMessage();
                                message.what =2;
                                handler.sendMessage(message);
                                dice =false;
                                playerOrder.order++;
                                allCard.check =false;

                            }
                        }
                        else if (before == 16){
                            background.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            if (event.getRawX() < 890) {
                                                for (Land l : Lands) {
                                                    if (l.name == "하와이") {
                                                        characterList.get(playerOrder.order % 2).x = l.x;
                                                        characterList.get(playerOrder.order % 2).y = l.y;
                                                        playerOrder.order++;
                                                        before = Lands.indexOf(l);
                                                        dice = false;
                                                        background.setOnTouchListener(null);
                                                        break;
                                                    }
                                                }


                                            }
                                    }
                                    return false;
                                }
                            });
                        }
                        else if(before == 24) {
                            if(d != 1){
                                Log.e("tt",String.valueOf(d));
                                d++;
                                playerOrder.order++;}
                            else{
                                Log.e("tt", String.valueOf(before));
                                background.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                if (event.getRawX() < 890) {
                                                    for (Land l : Lands) {
                                                        if (l.name == "하와이") {
                                                            characterList.get(playerOrder.order % 2).x = l.x;
                                                            characterList.get(playerOrder.order % 2).y = l.y;
                                                            background.invalidate();
                                                            before = Lands.indexOf(l);
                                                            d =0;
                                                            background.setOnTouchListener(null);
                                                            break;
                                                        }
                                                    }


                                                }
                                        }
                                        return false;
                                    }
                                });
                            }
                        }
                        else if(before == 30){
                            if(allTax.check ==false){
                                Message message =handler.obtainMessage();
                                message.what=3;
                                handler.sendMessage(message);
                                allTax.tax.invalidate();
                            }
                            else{
                                characterList.get(playerOrder.order%2).money -=  characterList.get(playerOrder.order%2).money*0.2;
                                Message message =handler.obtainMessage();
                                message.what=4;
                                handler.sendMessage(message);
                                dice=false;
                                allTax.check=false;
                                playerOrder.order++;
                            }
                        }

                        else{
                            if(Lands.get(before).who !=characterList.get(playerOrder.order%2).name && Lands.get(before).who != "" ){
                                characterList.get(playerOrder.order%2).money -= Lands.get(before).price;
                                for(Character i : characterList){
                                    if(Lands.get(before).who == i.name){
                                        i.money += Lands.get(before).price;
                                        Log.e("tt","돈나간다"+String.valueOf(Lands.get(before).price));
                                        break;
                                    }
                                }

                            }
                            result.character =characterList.get(playerOrder.order%2);
                            result.SetLand(Lands.get(before));
                            result.setPlayer(playerOrder);
                            Message message = handler.obtainMessage();
                            message.what=0;
                            handler.sendMessage(message);
                            dice = false;
                        }
                    }


                        }


                    }
                }).start();

    }
    protected void init(){
        Landinit();
        order =0;
        dice = false;
        ch =0;
        d=0;
        f=0;
        num1=0;
        before =0;
        bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.seu));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.dd));
        characterList = new ArrayList<Character>();
        characterList.add(new Character(bitmaps.get(0),Lands.get(0).x,Lands.get(0).y,"버섯"));
        characterList.add(new Character(bitmaps.get(1),Lands.get(0).x,Lands.get(0).y,"컴퓨터"));
        result.setVisibility(View.GONE);
        result.setCharacter(null);
        result.setDicebutton(board);
        allTax.setVisibility(View.GONE);
        allCard.setVisibility(View.GONE);
        background.SetCharacter(characterList);
        background.SetLand(Lands);
        for(Character c: characterList){
            c.setBoard(board);
        }
        for(Character i : characterList){
            status.add(new Status(getApplicationContext(),null,order));
            status.get(order).setCharacters(i);
            status.get(order).setY(100);
            status.get(order).setX(100);
            board.addView(status.get(order));
            order++;
        }
    }
    protected void Landinit(){
        Lands.add(new Land(860,800,"시작",0,0,0));
        Lands.add(new Land(760,730,"방콕",10,20,30));
        Lands.add(new Land(700,680,"보너스게임",0,0,0));
        Lands.add(new Land(620,640,"베이징",10,20,30));
        Lands.add(new Land(540,600,"독도",10,20,30));
        Lands.add(new Land(460,560,"타이페이",10,20,30));
        Lands.add(new Land(380,520,"두바이",10,20,30));
        Lands.add(new Land(300,480,"카이로",10,20,30));
        Lands.add(new Land(200,420,"무인도",0,0,0));
        Lands.add(new Land(300,360,"발리",30,50,70));
        Lands.add(new Land(380,320,"도쿄",30,50,70));
        Lands.add(new Land(460,280,"시드니",30,50,70));
        Lands.add(new Land(540,230,"카드",0,0,0));
        Lands.add(new Land(620,180,"퀘벡",30,50,70));
        Lands.add(new Land(700,140,"하와이",30,50,70));
        Lands.add(new Land(760,100,"상파울로",30,50,70));
        Lands.add(new Land(860,50,"올림픽",0,0,0));
        Lands.add(new Land(960,100,"프라하",50,70,100));
        Lands.add(new Land(1020,140,"푸켓",50,70,100));
        Lands.add(new Land(1100,180,"베를린",50,70,100));
        Lands.add(new Land(1190,220,"카드",0,0,0));
        Lands.add(new Land(1280,270,"모스크바",50,70,100));
        Lands.add(new Land(1340,320,"제네바",50,70,100));
        Lands.add(new Land(1420,370,"로마",50,70,100));
        Lands.add(new Land(1520,420,"세계여행",0,0,0));
        Lands.add(new Land(1420,470,"타히티",100,200,300));
        Lands.add(new Land(1340,510,"런던",100,200,300));
        Lands.add(new Land(1260,550,"파리",200,300,400));
        Lands.add(new Land(1180,590,"카드",0,0,0));
        Lands.add(new Land(1100,640,"뉴욕",100,300,500));
        Lands.add(new Land(1020,690,"국세청",0,0,0));
        Lands.add(new Land(950,730,"서울",100,500,700));
    }


}

