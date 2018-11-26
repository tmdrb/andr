package com.example.lee.game;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ViewUtils;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.lee.game.Result;
import java.security.BasicPermission;
import java.security.PrivilegedAction;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;
class Dice extends View{
    int num;
    public Dice(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(40);
        canvas.drawText(String.valueOf(num)+"칸 이동하시오.",100,100,paint);
    }
}
class Direction extends View{
    Bitmap bitmap;

    public Direction(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.direct);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try{
        canvas.drawBitmap(bitmap,0,0,null);
    }
        catch (Exception e){}
    }
}
class EndContent extends View{
    String name,price;
    public EndContent(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(55);
        paint.setColor(Color.BLUE);
        canvas.drawText("도시이름 : "+ name,0,50,paint);
        canvas.drawText("판매 금액 : " + price,0,150,paint);
    }
}
class End extends ConstraintLayout{
    Button button;
    EndContent endContent;
    Direction direction;
    boolean ch;
    Character character;
    int n = 0;
    public End(final Context context, AttributeSet attrs) {
        super(context, attrs);
        ch = false;
        endContent = new EndContent(context,attrs);
        direction = new Direction(context,attrs);
        direction.setLayoutParams(new ViewGroup.LayoutParams(direction.bitmap.getWidth(),direction.bitmap.getHeight()));
        direction.setX(400);
        direction.setY(50);
        direction.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        n++;
                        if(n >= character.City.size())
                            n -= character.City.size();
                        endContent.name = character.City.get(n).name;
                        endContent.price=String.valueOf((int)(character.City.get(n).price)/1.5);
                        endContent.invalidate();

                }
                return true;
            }
        });
        button = new Button(context);
        button.setX(250);
        button.setY(350);
        button.setText("판매");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                character.money +=(int)(character.City.get(n).price)/1.5;
                character.City.get(n).hcount=0;
                character.City.get(n).mcount=0;
                character.City.get(n).bcount=0;
                character.City.get(n).who ="";
                character.City.remove(n);
                ch =true;
                setVisibility(INVISIBLE);}
                catch (Exception e){}

            }
        });
        addView(endContent);
        addView(direction);
        addView(button);

    }
    public void setCharacter(Character character){
        this.character = character;
        endContent.name = this.character.City.get(0).name;
        endContent.price= String.valueOf((int)(this.character.City.get(0).price/1.5));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(66);
        canvas.drawText("hihi",100,100,paint);
    }
}
class AllTax extends ConstraintLayout{
    Tax tax;
    boolean check;
    public AllTax(Context context, AttributeSet attrs) {
        super(context, attrs);
        check =false;
        tax = new Tax(context,attrs);
        addView(tax);
        Button button= new Button(context);
        button.setX(100);
        button.setY(300);
        button.setText("닫기");
        addView(button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                check =true;
            }
        });
    }

}
class Tax extends View{
    String t;
    public Tax(Context context,AttributeSet attrs) {
        super(context, attrs);
        t ="";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText(t + "만원의 20%가",100,50,paint);
        canvas.drawText("세금으로 지출됬습니다.",100,100,paint);
    }
}
class Card extends View{
   ArrayList<String> contents;
   String now;
   Random random;
    public Card(Context context,AttributeSet attrs) {
        super(context, attrs);
        contents = new ArrayList<>();
        random = new Random();
        init();
        now ="";

    }
    protected void init(){
        contents.add("뒤로 3칸 이동하시오");
        contents.add("앞으로 3칸 이동하시오");
        contents.add("30만원을 기부하시오");
        contents.add("시작으로 가시오");
        contents.add("도시를 반납하시오");

    }
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.BLUE);
        now = contents.get(random.nextInt(contents.size()));
        canvas.drawText(now,100,100,paint);
    }


}
class AllCard extends ConstraintLayout{
    Card card;
    boolean check;
    public AllCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        check =false;
        card = new Card(context,attrs);
        addView(card);
        Button button = new Button(context);
        button.setX(100);
        button.setY(300);

        button.setText("닫기");
        addView(button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                setVisibility(GONE);
            }
        });
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
class Little {
    Bitmap bitmap,r;
    float x,y;
   public Little(Bitmap bitmap,float x,float y){
       this.bitmap =bitmap;
       this.x=x;
       this.y=y;
       Matrix matrix= new Matrix();
       matrix.preScale(0.3f,0.3f);
       r = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
   }
    public void Draw(Canvas canvas){
       Matrix matrix= new Matrix();
       matrix.setTranslate(x+15,y+35);
       try{
       canvas.drawBitmap(r,matrix,null);}
       catch (Exception e){}
    }
}
class Status extends View{
    Character character;
    int order;

    public Status(Context context, AttributeSet attrs,int order) {
        super(context, attrs);
        this.order =order;
    }
    public void setCharacters(Character character){
        this.character = character;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if(order ==0 ){
            Paint paint =new Paint();
            paint.setTextSize(50);
            canvas.drawText(character.name, 1500, 800, paint);
            canvas.drawText(String.valueOf(character.money), 1500, 700, paint);
            }
            else if(order ==1 ){
                Paint paint =new Paint();
                paint.setTextSize(50);
                canvas.drawText(character.name, 1500, 300, paint);
                canvas.drawText(String.valueOf(character.money), 1500, 200, paint);
               }
            else if(order ==2 ){
                Paint paint =new Paint();
                paint.setTextSize(50);
                canvas.drawText(character.name, 100, 800, paint);
                }
            else if(order ==3 ){
                Paint paint =new Paint();
                paint.setTextSize(50);
                canvas.drawText(character.name, 100, 300, paint);
            }
        }
        catch (Exception e){}
    }
}
class PlayerOrder {
    int order;
    PlayerOrder(){
        order = 0;
    }
}
class InsideResult extends View{
    Land land;
    InsideResult(Context context,AttributeSet attr){
        super(context,attr);
    }

    public void setPrice(Land land){
        this.land = land;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        try{
            Paint paint=new Paint();
            paint.setTextSize(36);
            canvas.drawText("모텔구입비용 : "+ String.valueOf(land.motelprice),200,200,paint);
            canvas.drawText("호텔구입비용 : "+String.valueOf(land.hotelprice),200,250,paint);
            canvas.drawText("빌딩구입비용 : "+String.valueOf(land.buildingprice),200,300,paint);
            canvas.drawText("주인 : "+String.valueOf(land.who) ,200,350,paint);
        }
        catch (Exception e){}
    }
}
class Result extends ConstraintLayout {
    Button button,button1,button2,button3,button4;
    Land land;
    Board board;
    Character character;
    DiceButton diceButton;
    InsideResult insideResult;
    PlayerOrder playorder;
    public Result(Context context, AttributeSet attr)  {
        super(context,attr);
        insideResult = new InsideResult(context,attr);
        button = new Button(context);
        button1 = new Button(context);
        button2 = new Button(context);
        button3 = new Button(context);
        button4 = new Button(context);
        button.setText("모텔구입");
        button.setX(0);
        button.setY(0);
        button1.setX(200);
        button1.setY(0);
        button2.setX(400);
        button2.setY(0);
        button3.setX(600);
        button3.setY(0);
        button1.setText("호텔구입");
        button2.setText("빌딩구입");
        button3.setText("턴넘기기");
        addView(button);
        addView(button1);
        addView(button2);
        addView(button3);
        addView(insideResult);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(character.Mbuy(land)){

                Log.e("tt","zzz");
                setVisibility(GONE);
                    board.dice.setVisibility(INVISIBLE);
                playorder.order++;}
                else{}


            }
        });
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(character.Hbuy(land)){
                setVisibility(GONE);

                playorder.order++;}
                else{}

            }
        });
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(character.Bbuy(land)){
                setVisibility(GONE);

                playorder.order++;}
                else {}


            }
        });
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                playorder.order++;
            }
        });
    }

    public void setPlayer(PlayerOrder playorder){
        this.playorder = playorder;
    }
    public void setDicebutton(Board board){
        this.board = board;
    }
    public void setCharacter(Character character){
        this.character=character;
    }

    public void SetLand(Land land){
        this.land = land;
        insideResult.setPrice(land);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
class Data{
    static boolean dice;
    static int rand1,rand2,order;
    Data(){
        dice = false;
        rand1 =0;
        rand2 =0;
    }
}
class Land {
    int x,y;
    String name;
    String who;
    int hotelprice,motelprice,buildingprice;
    int price;
    boolean olympic;
    int mcount,hcount,bcount;
    Land(int x,int y,String name,int motelprice, int hotelprice, int buildingprice){
        this.x = x;
        this.y = y;
        this.name =name;
        this.hotelprice =hotelprice;
        this.motelprice=motelprice;
        this.buildingprice=buildingprice;
        this.who= "";
        mcount =0;
        hcount=0;
        bcount=0;
        price =0;
        olympic =false;
    }

    public void onDraw(Canvas canvas){
        try{

        }
        catch (Exception e){}
    }
}

class Character {
    Bitmap seu;
    String name;
    ArrayList<Land> City;
    Matrix matrix;
    int money;
    Board board;
    float x,y;
    Character(Bitmap bitmap,float x,float y,String name){
        this.seu = bitmap;
        this.x = x;
        this.y =y;
        this.name = name;
        this.money = 500;
        City = new ArrayList<>();
    }

    public void setBoard(Board board){
        this.board=board;
    }
    public boolean Mbuy(Land land){

        if(land.who == "" ){
            Log.e("tt","st");
            if(this.money >= land.motelprice){
                this.money -= land.motelprice;
                land.price = (int)(land.motelprice*1.5);
                land.who = this.name;
                City.add(land);
                Log.e("tt",String.valueOf(City.get(City.size()-1)));
                land.mcount +=1;
                board.background.invalidate();
                return  true;}
            else{
                return false;
            }
        }
        else if(land.who==this.name ){
            if(this.money > land.motelprice){
                this.money -= land.motelprice;
                land.price += (int)(land.motelprice*1.5);
                land.mcount +=1;
                board.background.invalidate();
                return  true;}
            else{
                return false;
            }
        }
        return false;
    }

    public boolean Hbuy(Land land){
        if(land.who == "" ){
            Log.e("tt","st");
            if(this.money >= land.hotelprice){
                this.money -= land.hotelprice;
                land.price = (int)(land.hotelprice*1.5);
                land.who = this.name;
                City.add(land);
                land.hcount +=1;
                board.background.invalidate();
                return  true;}
            else{
                return false;
            }
        }
        else if(land.who==this.name ){
            if(this.money > land.hotelprice){
                this.money -= land.hotelprice;
                land.price += (int)(land.hotelprice*1.5);
                land.hcount +=1;
                board.background.invalidate();
                return  true;}
            else{
                return false;
            }
        }
      return false;
    }
    public boolean Bbuy(Land land){
        if(land.who == "" ){
            Log.e("tt","st");
            if(this.money >= land.buildingprice){
                this.money -= land.buildingprice;
                land.price = (int)(land.buildingprice*1.5);
                land.who = this.name;
                City.add(land);
                land.bcount +=1;
                board.background.invalidate();
                return  true;}
            else{
                return false;
            }
        }
        else if(land.who==this.name ){
            if(this.money > land.buildingprice){
                this.money -= land.buildingprice;
                land.price += (int)(land.buildingprice*1.5);
                land.bcount +=1;
                board.background.invalidate();
                return  true;}
            else{
                return false;
            }
        }
       return false;
    }
    public void onDraw(Canvas canvas){
        try{
            matrix = new Matrix();
            matrix.setRotate(90);
            matrix.setTranslate(x,y);
            canvas.drawBitmap(seu,matrix,null);
        }
        catch (Exception e){}
    }

}

class DiceButton extends View{
    Bitmap dicebutton,re;
    int x,y;
    Paint paint;
    Matrix matrix;
    public DiceButton(Context context,AttributeSet attr) {
        super(context,attr);
        matrix = new Matrix();
        matrix.preScale(0.65f,0.65f);
        dicebutton = BitmapFactory.decodeResource(getResources(),R.drawable.button);
        try{
        re = Bitmap.createBitmap(dicebutton,0,0,dicebutton.getWidth(),dicebutton.getHeight(),matrix,true);

        }
        catch (Exception e){}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            matrix = new Matrix();
            paint = new Paint();
            paint.setTextSize(50);
            paint.setColor(Color.RED);

            matrix.setTranslate(this.x, this.y);
            canvas.drawBitmap(re, matrix, null);
        }
        catch (Exception e){}
    }
}
class Background extends View{
    Bitmap background,cha;
    ArrayList<Character> CharacterList;
    ArrayList<Land> lands;
    Board board;
    Result a;
    int num;


    public Background(Context context,AttributeSet attrs) {
        super(context, attrs);
        background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        CharacterList = new ArrayList<Character>();

    }
    public void SetCharacter(ArrayList<Character> character){
        this.CharacterList = character;
    }
    public void SetLand(ArrayList<Land> lands){
        this.lands = lands;
    }
    public void SetBoard(Board board){
        this.board = board;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        matrix.preScale((float)getMeasuredWidth()/(float)background.getWidth(),(float)getMeasuredHeight()/(float)background.getHeight());
        try{
            canvas.drawBitmap(background,matrix,null);
            }
        catch (Exception e){}

        for(Character i : CharacterList) {
            for(Land l:i.City) {
                new Little(i.seu, l.x, l.y).Draw(canvas);
                }
            }
            for(Character i : CharacterList) {
                i.onDraw(canvas);
            }

    }
}
public class Board extends ConstraintLayout {
    DiceButton diceButton;
    Background background;
    End end;
    AllCard allCard;
    Dice dice;
    InsideResult insideResult;
    Result result;
    AllTax allTax;
    PlayerOrder playerOrder;
    Bitmap cha,cha1;
    ArrayList<Status> status;
    ArrayList<Character> CharacterList;
    Character character,character1;
    int rand1,rand2,num,order,ch,num1;
    public Board(Context context){
        super(context);
    }
    public Board(final Context context, AttributeSet attr) {
        super(context, attr);
        playerOrder = new PlayerOrder();
        CharacterList = new ArrayList<>();
        result = new Result(context,attr);
        result.setX(600);
        result.setY(200);
        dice = new Dice(context,attr);
        dice.setX(700);
        dice.setY(600);
        dice.setVisibility(INVISIBLE);
        dice.setLayoutParams(new ViewGroup.LayoutParams(500,500));
        allTax = new AllTax(context,attr);
        allTax.setLayoutParams(new ViewGroup.LayoutParams(500,500));
        allTax.setX(650);
        allTax.setY(300);
        allCard = new AllCard(context,attr);
        allCard.setX(650);
        allCard.setY(300);
        allCard.setLayoutParams(new ViewGroup.LayoutParams(500,500));
        end = new End(context,attr);
        end.setX(650);
        end.setY(300);
        end.setLayoutParams(new ViewGroup.LayoutParams(700,700));
        end.setVisibility(INVISIBLE);
        background = new Background(context,attr);
        background.SetBoard(this);
        diceButton = new DiceButton(context,attr);
        diceButton.setLayoutParams(new ViewGroup.LayoutParams(250,200));
        diceButton.setX(770);
        diceButton.setY(540);
        Log.e("tt",String.valueOf(diceButton.getWidth())+String.valueOf(diceButton.getHeight()));

        for(Character c:CharacterList){
            c.setBoard(this);
        }
        addView(background);
        addView(diceButton);
        addView(dice);
        addView(result);
        addView(allCard);
        addView(allTax);


        addView(end);
        num =0;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }



    @Override
    public void onDraw(Canvas canvas) {
        try {

        }
        catch (Exception e){}
    }

}


