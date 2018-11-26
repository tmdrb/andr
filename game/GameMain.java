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
public class GameMain extends AppCompatActivity {
    Random random;
    Intent intent;
    ArrayList<Character> characterList;
    Board board;
    AllTax allTax;
    Dice di;
    End end;
    ArrayList<Status> status;
    DiceButton diceButton;
    Background background;
    PlayerOrder playerOrder;
    ArrayList<Bitmap> bitmaps;
    Result result;
    AllCard allCard;
    Handler handler;
    ArrayList<Land> Lands;
    int rand1,rand2,num1,ch,ch1,order,before,d,f,mych,otherch,totalplayer,worldtravel,worldtravel1;
    boolean dice,dicheck ;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        intent = getIntent();
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
                result.setVisibility(View.VISIBLE);}
                else if(msg.what ==1){
                    allCard.setVisibility(View.VISIBLE);

                }
                else if(msg.what ==2){
                    diceButton.setVisibility(View.VISIBLE);

                }
                else if(msg.what ==3){
                    diceButton.setVisibility(View.GONE);
                }
                else if(msg.what ==4){
                    diceButton.setVisibility(View.VISIBLE);
                    allTax.setVisibility(View.GONE);
                    allCard.setVisibility(View.GONE);
                    end.setVisibility(View.INVISIBLE);
                }
                else if(msg.what ==5){
                    allCard.setVisibility(View.VISIBLE);

                }
                else if(msg.what ==6){
                    allTax.setVisibility(View.VISIBLE);
                }
                else if(msg.what ==7){
                    di.setVisibility(View.VISIBLE);
                }
                else if(msg.what ==8){
                    di.setVisibility(View.INVISIBLE);

                }
                else if(msg.what ==9)
                {
                    end.setVisibility(View.VISIBLE);
                }

            }
        };
        diceButton = board.diceButton;
        background = board.background;
        result = board.result;
        allTax = board.allTax;
        allCard = board.allCard;
        di = board.dice;
        end = board.end;
        init();

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
                        if(before ==8){
                            if(rand1 == rand2 || ch1 == 2){
                                before+=rand1+rand2;
                                dice = true;
                                dicheck = true;
                                Message dic = handler.obtainMessage();
                                dic.what=3;
                                handler.sendMessage(dic);
                                ch1 =0;
                            }
                            else{
                                dice = true;
                                dicheck = true;
                                Message dic = handler.obtainMessage();
                                dic.what=3;
                                handler.sendMessage(dic);
                                ch1 ++;
                            }
                        }
                        else {
                        before+=rand1+rand2;
                        if(before >31){
                            characterList.get(playerOrder.order % 2).money +=30;
                            before -=32;
                        }

                        dice = true;
                        dicheck = true;
                        Message dic = handler.obtainMessage();
                        dic.what=3;
                        handler.sendMessage(dic);
                        break;
                        }
                }

                return true;
            }
        });

        new Thread(new Runnable() {
            @Override

            public void run() {
                while(true){

                    for(Status s:status){
                        s.invalidate();
                    }
                    board.invalidate();
                    background.invalidate();

                    if(playerOrder.order%totalplayer== mych && dice ==true){

                        result.setCharacter(characterList.get(playerOrder.order%totalplayer));
                        if(dicheck){
                        di.num =rand1+rand2;
                        di.invalidate();
                        Message message1 = handler.obtainMessage();
                        message1.what=7;
                        handler.sendMessage(message1);

                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message m3 = handler.obtainMessage();
                        m3.what=8;
                        handler.sendMessage(m3);
                        dicheck =false;
                        }

                        characterList.get(playerOrder.order % 2).x = Lands.get(before).x;
                        characterList.get(playerOrder.order % 2).y = Lands.get(before).y;
                        background.invalidate();
                        if(before ==0){
                            dice = false;
                            dicheck = false;
                           playerOrder.order++;

                            }
                        else if(before == 2){

                            if(random.nextInt(2) == 1){
                                Log.e("tt","홀짝게임");
                                characterList.get(playerOrder.order%totalplayer).money += 50;
                            }
                            background.invalidate();
                            playerOrder.order++;
                            dicheck = false;
                            dice = false;
                        }
                        else if(before == 8){

                            dice=false;
                            dicheck = false;
                            playerOrder.order++;
                        }
                        else if(before == 12 || before ==20 || before==28){

                            if(allCard.check ==false){
                                dicheck = false;
                            Message cardmessage = handler.obtainMessage();
                            cardmessage.what =1;
                            handler.sendMessage(cardmessage);

                            }
                            if(allCard.check){

                                if(allCard.card.now == "뒤로 3칸 이동하시오"){
                                    before -=3;
                                    characterList.get(playerOrder.order%totalplayer).x = Lands.get(before).x;
                                    characterList.get(playerOrder.order%totalplayer).y = Lands.get(before).y;
                                    background.invalidate();
                                }
                                else if(allCard.card.now == "앞으로 3칸 이동하시오"){
                                    before +=3;
                                    characterList.get(playerOrder.order%totalplayer).x = Lands.get(before).x;
                                    characterList.get(playerOrder.order%totalplayer).y = Lands.get(before).y;
                                    background.invalidate();
                                }
                                else if(allCard.card.now == "30만원을 기부하시오"){
                                    characterList.get(playerOrder.order%totalplayer).money -=30;
                                    playerOrder.order++;
                                    dice=false;
                                }
                                else if(allCard.card.now == "시작으로 가시오"){
                                    before =0;
                                    characterList.get(playerOrder.order%totalplayer).x = Lands.get(before).x;
                                    characterList.get(playerOrder.order%totalplayer).y = Lands.get(before).y;
                                    background.invalidate();

                                }
                                else if(allCard.card.now == "도시를 반납하시오"){
                                        end.character = characterList.get(playerOrder.order%totalplayer);
                                        Message message = handler.obtainMessage();
                                        message.what =9;
                                        handler.sendMessage(message);
                                        if(end.ch== true){
                                        playerOrder.order++;
                                        dice =false;
                                        }
                                    }
                                }


                        }
                        else if (before == 16){

                            background.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:

                                            Log.e("tt",String.valueOf(event.getRawX())+"  : "+String.valueOf(event.getRawY()));
                                               if(event.getRawX() >798 && event.getRawX() <999 && event.getRawY() >875 && event.getRawY() <1025){
                                                   Log.e("tt","0");
                                               }
                                                else if(event.getRawX() >725 && event.getRawX() <892 && event.getRawY() >833 && event.getRawY() <926){
                                                   dice =false;
                                                   Lands.get(1).olympic =true;
                                                   Lands.get(1).hotelprice *=2;
                                                   Lands.get(1).motelprice *=2;
                                                   Lands.get(1).buildingprice *=2;

                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >650 && event.getRawX() <820 && event.getRawY() >790 && event.getRawY() <890){
                                                   Log.e("tt","2");
                                               }
                                               else if(event.getRawX() >570 && event.getRawX() <740 && event.getRawY() >745 && event.getRawY() <845){
                                                   dice =false;
                                                   Lands.get(3).olympic =true;
                                                   Lands.get(3).hotelprice *=2;
                                                   Lands.get(3).motelprice *=2;
                                                   Lands.get(3).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                                   Log.e("tt","3");
                                               }
                                               else if(event.getRawX() >494 && event.getRawX() <662 && event.getRawY() >697 && event.getRawY() <802){
                                                   dice =false;
                                                   Log.e("tt","4");
                                                   Lands.get(4).olympic =true;
                                                   Lands.get(4).hotelprice *=2;
                                                   Lands.get(4).motelprice *=2;
                                                   Lands.get(4).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >419 && event.getRawX() <578 && event.getRawY() >654 && event.getRawY() <762){
                                                   dice =false;
                                                   Log.e("tt","5");
                                                   Lands.get(5).olympic =true;
                                                   Lands.get(5).hotelprice *=2;
                                                   Lands.get(5).motelprice *=2;
                                                   Lands.get(5).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >341 && event.getRawX() <503 && event.getRawY() >615 && event.getRawY() <715){
                                                   dice =false;
                                                   Log.e("tt","6");
                                                   Lands.get(6).olympic =true;
                                                   Lands.get(6).hotelprice *=2;
                                                   Lands.get(6).motelprice *=2;
                                                   Lands.get(6).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >260 && event.getRawX() <430 && event.getRawY() >570 && event.getRawY() <670){
                                                   dice =false;
                                                   Log.e("tt","7");
                                                   Lands.get(7).olympic =true;
                                                   Lands.get(7).hotelprice *=2;
                                                   Lands.get(7).motelprice *=2;
                                                   Lands.get(7).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >100 && event.getRawX() <360 && event.getRawY() >495 && event.getRawY() <640){
                                                   Log.e("tt","8");
                                               }
                                               else if(event.getRawX() >260 && event.getRawX() <440 && event.getRawY() >465 && event.getRawY() <565){
                                                   Log.e("tt","9");
                                                   dice =false;
                                                   Lands.get(9).olympic =true;
                                                   Lands.get(9).hotelprice *=2;
                                                   Lands.get(9).motelprice *=2;
                                                   Lands.get(9).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >340 && event.getRawX() <510 && event.getRawY() >420 && event.getRawY() <520){
                                                   Log.e("tt","10");
                                                   dice =false;
                                                   Lands.get(10).olympic =true;
                                                   Lands.get(10).hotelprice *=2;
                                                   Lands.get(10).motelprice *=2;
                                                   Lands.get(10).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >415 && event.getRawX() <585 && event.getRawY() >375 && event.getRawY() <475){
                                                   Log.e("tt","11");
                                                   dice =false;
                                                   Lands.get(11).olympic =true;
                                                   Lands.get(11).hotelprice *=2;
                                                   Lands.get(11).motelprice *=2;
                                                   Lands.get(11).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >490 && event.getRawX() <660 && event.getRawY() >335 && event.getRawY() <435){
                                                   Log.e("tt","12");
                                               }
                                               else if(event.getRawX() >565 && event.getRawX() <735 && event.getRawY() >290 && event.getRawY() <390){
                                                   Log.e("tt","13");
                                                   dice =false;
                                                   Lands.get(13).olympic =true;
                                                   Lands.get(13).hotelprice *=2;
                                                   Lands.get(13).motelprice *=2;
                                                   Lands.get(13).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >645 && event.getRawX() <815 && event.getRawY() >245 && event.getRawY() <345){
                                                   dice =false;
                                                   Log.e("tt","14");
                                                   Lands.get(14).olympic =true;
                                                   Lands.get(14).hotelprice *=2;
                                                   Lands.get(14).motelprice *=2;
                                                   Lands.get(14).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >720 && event.getRawX() <890 && event.getRawY() >205 && event.getRawY() <305){
                                                   dice =false;
                                                   Log.e("tt","15");
                                                   Lands.get(15).olympic =true;
                                                   Lands.get(15).hotelprice *=2;
                                                   Lands.get(15).motelprice *=2;
                                                   Lands.get(15).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >803 && event.getRawX() <992 && event.getRawY() >80 && event.getRawY() <226){
                                                   Log.e("tt","16");
                                               }
                                               else if(event.getRawX() >905 && event.getRawX() <1053 && event.getRawY() >172 && event.getRawY() <270){
                                                   dice =false;
                                                   Log.e("tt","17");
                                                   Lands.get(17).olympic =true;
                                                   Lands.get(17).hotelprice *=2;
                                                   Lands.get(17).motelprice *=2;
                                                   Lands.get(17).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >974 && event.getRawX() <1133 && event.getRawY() >219 && event.getRawY() <322){
                                                   dice =false;
                                                   Log.e("tt","18");
                                                   Lands.get(18).olympic =true;
                                                   Lands.get(18).hotelprice *=2;
                                                   Lands.get(18).motelprice *=2;
                                                   Lands.get(18).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1053 && event.getRawX() <1208 && event.getRawY() >261 && event.getRawY() <357){
                                                   dice =false;
                                                   Log.e("tt","19");
                                                   Lands.get(19).olympic =true;
                                                   Lands.get(19).hotelprice *=2;
                                                   Lands.get(19).motelprice *=2;
                                                   Lands.get(19).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1130 && event.getRawX() <1285 && event.getRawY() >310 && event.getRawY() <401){
                                                   Log.e("tt","20");
                                               }
                                               else if(event.getRawX() >1210 && event.getRawX() <1358 && event.getRawY() >359 && event.getRawY() <446){
                                                   dice =false;
                                                   Log.e("tt","21");
                                                   Lands.get(21).olympic =true;
                                                   Lands.get(21).hotelprice *=2;
                                                   Lands.get(21).motelprice *=2;
                                                   Lands.get(21).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1287 && event.getRawX() <1437 && event.getRawY() >401 && event.getRawY() <490){
                                                   dice =false;
                                                   Log.e("tt","22");
                                                   Lands.get(22).olympic =true;
                                                   Lands.get(22).hotelprice *=2;
                                                   Lands.get(22).motelprice *=2;
                                                   Lands.get(22).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1362 && event.getRawX() <1507 && event.getRawY() >444 && event.getRawY() <530){
                                                   dice =false;
                                                   Log.e("tt","23");
                                                   Lands.get(23).olympic =true;
                                                   Lands.get(23).hotelprice *=2;
                                                   Lands.get(23).motelprice *=2;
                                                   Lands.get(23).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1450 && event.getRawX() <1683 && event.getRawY() > 474&& event.getRawY() <600){
                                                   Log.e("tt","24");
                                               }
                                               else if(event.getRawX() >1365 && event.getRawX() <1521 && event.getRawY() >544 && event.getRawY() <643){
                                                   dice =false;
                                                   Log.e("tt","25");
                                                   Lands.get(25).olympic =true;
                                                   Lands.get(25).hotelprice *=2;
                                                   Lands.get(25).motelprice *=2;
                                                   Lands.get(25).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1280 && event.getRawX() <1447 && event.getRawY() >594 && event.getRawY() <685){
                                                   dice =false;
                                                   Log.e("tt","26");
                                                   Lands.get(26).olympic =true;
                                                   Lands.get(26).hotelprice *=2;
                                                   Lands.get(26).motelprice *=2;
                                                   Lands.get(26).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1205 && event.getRawX() <1369 && event.getRawY() >636 && event.getRawY() <722){
                                                   dice =false;
                                                   Log.e("tt","27");
                                                   Lands.get(27).olympic =true;
                                                   Lands.get(27).hotelprice *=2;
                                                   Lands.get(27).motelprice *=2;
                                                   Lands.get(27).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >1130 && event.getRawX() <1290 && event.getRawY() >676 && event.getRawY() <767){
                                                   Log.e("tt","28");
                                               }
                                               else if(event.getRawX() >1050 && event.getRawX() <1215 && event.getRawY() >722 && event.getRawY() <814){
                                                   dice =false;
                                                   Log.e("tt","29");
                                                   Lands.get(29).olympic =true;
                                                   Lands.get(29).hotelprice *=2;
                                                   Lands.get(29).motelprice *=2;
                                                   Lands.get(29).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                               else if(event.getRawX() >981 && event.getRawX() <1145 && event.getRawY() >762 && event.getRawY() <849){
                                                   Log.e("tt","30");
                                               }
                                               else if(event.getRawX() >913 && event.getRawX() <1065 && event.getRawY() >809 && event.getRawY() <909){
                                                   dice =false;
                                                   Log.e("tt","31");
                                                   Lands.get(31).olympic =true;
                                                   Lands.get(31).hotelprice *=2;
                                                   Lands.get(31).motelprice *=2;
                                                   Lands.get(31).buildingprice *=2;
                                                   background.setOnTouchListener(null);
                                                   playerOrder.order++;
                                               }
                                    }

                                    return false;
                                }
                            });

                        }
                        else if (before == 24){
                            if(worldtravel ==0 ){
                                playerOrder.order++;
                                dice =false;
                                worldtravel++;
                                background.setOnTouchListener(null);
                            }
                            else{
                                Message dic = handler.obtainMessage();
                                dic.what=3;
                                handler.sendMessage(dic);
                            dicheck =false;
                            background.setOnTouchListener(new View.OnTouchListener() {

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            if(event.getRawX() >798 && event.getRawX() <999 && event.getRawY() >875 && event.getRawY() <1025){
                                                dice=false;
                                                Log.e("tt","0");
                                                before =0;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                                playerOrder.order++;
                                            }
                                            else if(event.getRawX() >725 && event.getRawX() <892 && event.getRawY() >833 && event.getRawY() <926){
                                                Log.e("tt","1");
                                                before =1;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;

                                            }
                                            else if(event.getRawX() >650 && event.getRawX() <820 && event.getRawY() >790 && event.getRawY() <890){
                                                Log.e("tt","2");
                                                before =2;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >570 && event.getRawX() <740 && event.getRawY() >745 && event.getRawY() <845){
                                                Log.e("tt","3");
                                                before =3;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >494 && event.getRawX() <662 && event.getRawY() >697 && event.getRawY() <802){
                                                Log.e("tt","4");
                                                before =4;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;

                                            }
                                            else if(event.getRawX() >419 && event.getRawX() <578 && event.getRawY() >654 && event.getRawY() <762){
                                                Log.e("tt","5");
                                                before =5;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >341 && event.getRawX() <503 && event.getRawY() >615 && event.getRawY() <715){
                                                Log.e("tt","6");
                                                before =6;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >260 && event.getRawX() <430 && event.getRawY() >570 && event.getRawY() <670){
                                                Log.e("tt","7");
                                                before =7;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >100 && event.getRawX() <360 && event.getRawY() >495 && event.getRawY() <640){
                                                Log.e("tt","8");
                                                dice=false;
                                                before =8;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                playerOrder.order++;
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >260 && event.getRawX() <440 && event.getRawY() >465 && event.getRawY() <565){
                                                Log.e("tt","9");
                                                before =9;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >340 && event.getRawX() <510 && event.getRawY() >420 && event.getRawY() <520){
                                                Log.e("tt","10");
                                                before =10;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >415 && event.getRawX() <585 && event.getRawY() >375 && event.getRawY() <475){
                                                Log.e("tt","11");
                                                before =11;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >490 && event.getRawX() <660 && event.getRawY() >335 && event.getRawY() <435){
                                                Log.e("tt","12");
                                                before =12;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >565 && event.getRawX() <735 && event.getRawY() >290 && event.getRawY() <390){
                                                Log.e("tt","13");
                                                before =13;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >645 && event.getRawX() <815 && event.getRawY() >245 && event.getRawY() <345){
                                                Log.e("tt","14");
                                                before =14;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >720 && event.getRawX() <890 && event.getRawY() >205 && event.getRawY() <305){
                                                Log.e("tt","15");
                                                before =15;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >803 && event.getRawX() <992 && event.getRawY() >80 && event.getRawY() <226){
                                                Log.e("tt","16");
                                                before =16;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >905 && event.getRawX() <1053 && event.getRawY() >172 && event.getRawY() <270){
                                                Log.e("tt","17");
                                                before =17;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >974 && event.getRawX() <1133 && event.getRawY() >219 && event.getRawY() <322){
                                                Log.e("tt","18");
                                                before =18;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1053 && event.getRawX() <1208 && event.getRawY() >261 && event.getRawY() <357){
                                                Log.e("tt","19");
                                                before =19;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1130 && event.getRawX() <1285 && event.getRawY() >310 && event.getRawY() <401){
                                                Log.e("tt","20");
                                                before =20;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1210 && event.getRawX() <1358 && event.getRawY() >359 && event.getRawY() <446){
                                                Log.e("tt","21");
                                                before =21;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1287 && event.getRawX() <1437 && event.getRawY() >401 && event.getRawY() <490){
                                                Log.e("tt","22");
                                                before =22;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1362 && event.getRawX() <1507 && event.getRawY() >444 && event.getRawY() <530){
                                                Log.e("tt","23");
                                                before =23;
                                                characterList.get(playerOrder.order%totalplayer).money +=30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }

                                            else if(event.getRawX() >1365 && event.getRawX() <1521 && event.getRawY() >544 && event.getRawY() <643){
                                                Log.e("tt","25");
                                                before =25;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1280 && event.getRawX() <1447 && event.getRawY() >594 && event.getRawY() <685){
                                                Log.e("tt","26");
                                                before =26;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1205 && event.getRawX() <1369 && event.getRawY() >636 && event.getRawY() <722){
                                                Log.e("tt","27");
                                                before =27;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1130 && event.getRawX() <1290 && event.getRawY() >676 && event.getRawY() <767){
                                                Log.e("tt","28");
                                                before =28;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >1050 && event.getRawX() <1215 && event.getRawY() >722 && event.getRawY() <814){
                                                Log.e("tt","29");
                                                before =29;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >981 && event.getRawX() <1145 && event.getRawY() >762 && event.getRawY() <849){
                                                Log.e("tt","30");
                                                before =30;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
                                            }
                                            else if(event.getRawX() >913 && event.getRawX() <1065 && event.getRawY() >809 && event.getRawY() <909){
                                                Log.e("tt","31");
                                                before =31;
                                                characterList.get(playerOrder.order%totalplayer).x=Lands.get(before).x;
                                                characterList.get(playerOrder.order%totalplayer).y=Lands.get(before).y;
                                                background.setOnTouchListener(null);
                                                worldtravel =0;
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
                                message.what=6;
                                handler.sendMessage(message);
                                allTax.tax.t=String.valueOf(characterList.get(playerOrder.order%totalplayer).money);
                                allTax.tax.invalidate();
                            }
                            else{
                            characterList.get(playerOrder.order%totalplayer).money -=  characterList.get(playerOrder.order%totalplayer).money*0.2;
                            dice=false;
                            allTax.check=false;
                                dicheck = false;
                            playerOrder.order++;
                            }
                        }

                        else{

                            if(Lands.get(before).who !=characterList.get(playerOrder.order%totalplayer).name && Lands.get(before).who != "" ){
                                if(characterList.get(playerOrder.order%totalplayer).money-Lands.get(before).price>=0){
                                    characterList.get(playerOrder.order%totalplayer).money-=Lands.get(before).price;
                                    for(Character i : characterList){
                                        if(Lands.get(before).who == i.name){
                                            i.money += Lands.get(before).price;
                                            Log.e("tt","돈나간다"+String.valueOf(Lands.get(before).price));
                                            playerOrder.order++;
                                            dice = false;
                                            dicheck = false;
                                            break;
                                        }
                                    }
                                }
                                else{
                                    if(characterList.get(playerOrder.order%totalplayer).City == null){}
                                    else{
                                    end.character = characterList.get(playerOrder.order%totalplayer);
                                    Message message = handler.obtainMessage();
                                    message.what=9;
                                    handler.sendMessage(message);}
                                }
                                }
                            else{
                            result.character =characterList.get(playerOrder.order%totalplayer);
                            result.SetLand(Lands.get(before));
                            result.setPlayer(playerOrder);
                            Message message = handler.obtainMessage();
                            message.what=0;
                            handler.sendMessage(message);
                            dice = false;
                            dicheck = false;
                            }
                        }
                    }
                    //**********************컴퓨터 게임로직**************************************

               if(playerOrder.order%totalplayer==otherch){
                        if(worldtravel1 ==0){
                   rand1 = random.nextInt(6)+1;
                   rand2 = random.nextInt(6)+1;
                   di.num =rand1+rand2;
                   di.invalidate();
                   Message message1 = handler.obtainMessage();
                   message1.what=7;
                   handler.sendMessage(message1);
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   Message m3 = handler.obtainMessage();
                   m3.what=8;
                   handler.sendMessage(m3);}
                        if(num1 != 8){
                            if(worldtravel1 ==0){
                            num1 += rand1 + rand2;
                            Log.e("tt",String.valueOf(num1));
                            if(num1 > 31){
                                characterList.get(playerOrder.order%totalplayer).money+=30;
                                num1 -= 32;
                            }
                            characterList.get(playerOrder.order%totalplayer).x = Lands.get(num1).x;
                            characterList.get(playerOrder.order%totalplayer).y = Lands.get(num1).y;
                            background.invalidate();}
                            if(num1 == 2){

                                if(random.nextInt(2) == 1){
                                    Log.e("tt","홀짝게임");
                                    characterList.get(playerOrder.order%totalplayer).money += 50;
                                }
                                background.invalidate();
                                playerOrder.order++;
                                worldtravel1 = 0;
                                Message message = handler.obtainMessage();
                                message.what = 2;
                                handler.sendMessage(message);

                            }
                            else if(num1 == 8){
                                playerOrder.order++;
                                worldtravel1 = 0;
                                Message message = handler.obtainMessage();
                                message.what = 2;
                                handler.sendMessage(message);
                            }
                            else if(num1 ==12 || num1 ==20|| num1==28){

                            }
                            else if(num1 == 16){

                                int rand = random.nextInt(characterList.get(playerOrder.order%totalplayer).City.size());
                                Lands.get(Lands.indexOf(characterList.get(playerOrder.order%totalplayer).City.get(rand))).olympic = true;
                                Lands.get(Lands.indexOf(characterList.get(playerOrder.order%totalplayer).City.get(rand))).motelprice *=2;
                                Lands.get(Lands.indexOf(characterList.get(playerOrder.order%totalplayer).City.get(rand))).hotelprice *=2;
                                Lands.get(Lands.indexOf(characterList.get(playerOrder.order%totalplayer).City.get(rand))).buildingprice *=2;
                                playerOrder.order++;
                                worldtravel1 = 0;
                            }
                            else if(num1 == 24){
                                if(worldtravel1 == 0){
                                    worldtravel1 ++;
                                    playerOrder.order++;
                                }
                                else{

                                    int ran = random.nextInt(32);
                                    if(Lands.get(ran).who == "" || Lands.get(ran).who == characterList.get(playerOrder.order%totalplayer).name){
                                        num1 = ran;
                                        characterList.get(playerOrder.order%totalplayer).x = Lands.get(num1).x;
                                        characterList.get(playerOrder.order%totalplayer).y = Lands.get(num1).y;
                                        background.invalidate();
                                    }



                                }

                            }
                            else if(num1 == 30){
                                if(allTax.check ==false){
                                    Message message =handler.obtainMessage();
                                    message.what=6;
                                    handler.sendMessage(message);
                                    allTax.tax.t=String.valueOf(characterList.get(playerOrder.order%totalplayer).money);
                                    allTax.tax.invalidate();
                                }
                                else{
                                    characterList.get(playerOrder.order%totalplayer).money -= (int)(characterList.get(playerOrder.order%totalplayer).money*0.2);
                                    allTax.check=false;
                                    playerOrder.order++;
                                    worldtravel1 = 0;
                                }

                            }
                            else{
                                if(Lands.get(num1).who !=characterList.get(playerOrder.order%totalplayer).name && Lands.get(num1).who != "" ) {
                                    if (characterList.get(playerOrder.order % totalplayer).money < (int)(Lands.get(num1).price /1.5)) {
                                        end.setCharacter(characterList.get(playerOrder.order % totalplayer));
                                        if (characterList.get(playerOrder.order % totalplayer).City == null) {
                                        } else {
                                            characterList.get(playerOrder.order % totalplayer).money += (int) (characterList.get(playerOrder.order % totalplayer).City.get(0).price / 1.5);
                                            characterList.get(playerOrder.order % totalplayer).City.get(0).bcount = 0;
                                            characterList.get(playerOrder.order % totalplayer).City.get(0).mcount = 0;
                                            characterList.get(playerOrder.order % totalplayer).City.get(0).hcount = 0;
                                            characterList.get(playerOrder.order % totalplayer).City.get(0).who = "";
                                            characterList.get(playerOrder.order % totalplayer).City.remove(0);
                                        }

                                    } else {
                                        characterList.get(playerOrder.order % totalplayer).money -= Lands.get(num1).price;
                                        for (Character i : characterList) {
                                            if (Lands.get(num1).who == i.name) {
                                                i.money += Lands.get(num1).price;
                                                Log.e("tt", "돈나간다" + String.valueOf(Lands.get(num1).price));
                                                playerOrder.order++;
                                                worldtravel1 = 0;
                                                Message message = handler.obtainMessage();
                                                message.what = 2;
                                                handler.sendMessage(message);
                                                break;
                                            }
                                        }

                                    }
                                }
                                else{
                                    if(characterList.get(playerOrder.order % totalplayer).money  >= Lands.get(num1).buildingprice){
                                        characterList.get(playerOrder.order % totalplayer).Bbuy(Lands.get(num1));
                                    }
                                    else if(characterList.get(playerOrder.order % totalplayer).money  >= Lands.get(num1).hotelprice){
                                        characterList.get(playerOrder.order % totalplayer).Hbuy(Lands.get(num1));
                                    }
                                    else if(characterList.get(playerOrder.order % totalplayer).money  >= Lands.get(num1).motelprice){
                                        characterList.get(playerOrder.order % totalplayer).Mbuy(Lands.get(num1));
                                    }
                                    playerOrder.order++;
                                    worldtravel1 = 0;
                                    Message message = handler.obtainMessage();
                                    message.what = 2;
                                    handler.sendMessage(message);
                                }
                            }

                        }
                       else if(num1 == 8 ){
                            Log.e("tt",String.valueOf(rand1)+","+String.valueOf(rand2));
                            if(rand1 == rand2 || ch == 2){
                                num1 += rand1 +rand2;
                                Log.e("tt","칼출");
                                characterList.get(playerOrder.order%totalplayer).x = Lands.get(num1).x;
                                characterList.get(playerOrder.order%totalplayer).y = Lands.get(num1).y;
                                background.invalidate();
                                ch =0;
                                if(num1 == 2){

                                    if(random.nextInt(2) == 1){
                                        Log.e("tt","홀짝게임");
                                        characterList.get(playerOrder.order%totalplayer).money += 50;
                                    }
                                    background.invalidate();
                                    playerOrder.order++;
                                    Message message = handler.obtainMessage();
                                    message.what = 2;
                                    handler.sendMessage(message);

                                }
                                else if(num1 == 8){
                                    playerOrder.order++;
                                    Message message = handler.obtainMessage();
                                    message.what = 2;
                                    handler.sendMessage(message);
                                }
                                else{
                                    if(Lands.get(num1).who !=characterList.get(playerOrder.order%totalplayer).name && Lands.get(num1).who != "" ) {
                                        if (characterList.get(playerOrder.order % totalplayer).money < (int)(Lands.get(num1).price /1.5)) {
                                            end.setCharacter(characterList.get(playerOrder.order % totalplayer));
                                            if (characterList.get(playerOrder.order % totalplayer).City == null) {
                                            } else {
                                                characterList.get(playerOrder.order % totalplayer).money += (int) (characterList.get(playerOrder.order % totalplayer).City.get(0).price / 1.5);
                                                characterList.get(playerOrder.order % totalplayer).City.get(0).bcount = 0;
                                                characterList.get(playerOrder.order % totalplayer).City.get(0).mcount = 0;
                                                characterList.get(playerOrder.order % totalplayer).City.get(0).hcount = 0;
                                                characterList.get(playerOrder.order % totalplayer).City.get(0).who = "";
                                                characterList.get(playerOrder.order % totalplayer).City.remove(0);
                                            }

                                        } else {
                                            characterList.get(playerOrder.order % totalplayer).money -= Lands.get(num1).price;
                                            for (Character i : characterList) {
                                                if (Lands.get(num1).who == i.name) {
                                                    i.money += Lands.get(num1).price;
                                                    Log.e("tt", "돈나간다" + String.valueOf(Lands.get(num1).price));
                                                    playerOrder.order++;
                                                    Message message = handler.obtainMessage();
                                                    message.what = 2;
                                                    handler.sendMessage(message);
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                    else{
                                        if(characterList.get(playerOrder.order % totalplayer).money  >= Lands.get(num1).buildingprice){
                                            characterList.get(playerOrder.order % totalplayer).Bbuy(Lands.get(num1));
                                        }
                                        else if(characterList.get(playerOrder.order % totalplayer).money  >= Lands.get(num1).hotelprice){
                                            characterList.get(playerOrder.order % totalplayer).Hbuy(Lands.get(num1));
                                        }
                                        else if(characterList.get(playerOrder.order % totalplayer).money  >= Lands.get(num1).motelprice){
                                            characterList.get(playerOrder.order % totalplayer).Mbuy(Lands.get(num1));
                                        }
                                        playerOrder.order++;
                                        Message message = handler.obtainMessage();
                                        message.what = 2;
                                        handler.sendMessage(message);
                                    }
                                }

                            }
                            else{
                                ch++;
                                Log.e("tt",String.valueOf(ch));
                                playerOrder.order++;
                                Message message = handler.obtainMessage();
                                message.what = 2;
                                handler.sendMessage(message);
                            }

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
        dicheck = false;
        ch =0;
        d=0;
        f=0;
        num1=0;
        before =0;
        worldtravel =0;
        worldtravel1 = 0;
        totalplayer=2;
        mych=0;
        otherch=1;
        bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.seu));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.dd));
        characterList = new ArrayList<Character>();
        for(int i=0;i<totalplayer;i++)
            characterList.add(null);
        characterList.set(mych,new Character(bitmaps.get(0),Lands.get(0).x,Lands.get(0).y,intent.getStringExtra("nickname")));
        characterList.set(otherch,new Character(bitmaps.get(1),Lands.get(0).x,Lands.get(0).y,"컴퓨터"));
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

