package com.example.photobook;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


public class Drawing_Activity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    FrameLayout frame; //그림판 뷰
    DrawingPaper drawingPaper;
    LinearLayout linearLayout; //이미지뷰들을 담는 레이아웃
    LinearLayout palette;//백그라운드 색상변경할 팔레트
    LinearLayout sizecontrol; //사이즈버튼 누르면 나오는 사이즈 조절하는 레이아웃
    LinearLayout pencolors; //브러쉬버튼 누르면 나오는 펜 지정하는 레이아웃
    Dialog dialog; //스티커버튼 누르면 나오는 다이얼로그
    float X_val,Y_val; // 이미지뷰 이동할때 좌표
    ImageView size_v; //사이즈 조절할 대상이 될 이미지뷰
    int x=100, y=100; //이미지뷰 크기를 조절하기 위한 가로세로 변수
    Button done; //완료버튼
    static Bitmap bitmap; //만든이미지를 저장할 변수
    int check = 0; // 그림판이 있을경우, 없을경우를 구분하기 위한 변수

    NotificationManager manager; //알림기능 관련
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";


    static String colorst ="#000000"; //pen 초기값 및 이 변수 이용하여 값 바꿈
    static ImageView[] sticker = new ImageView[4]; //스티커가 들어갈 4개의 뷰(1개는 핸드폰이 작아서 잘림ㅜㅜ)
    static ImageButton[] stickers = new ImageButton[15]; // 스티커
    static ImageButton[] size_stickers = new ImageButton[4]; //사이즈 조절버튼 누르면 있는 스티커가 보이는 이미지버튼뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        //기능이 감춰져 있기때문에 나오는 알림메세지
        Toast.makeText(getApplicationContext(), "Click the 3-line button", Toast.LENGTH_LONG).show();

        Button sti = findViewById(R.id.sticker);
        sti.setOnClickListener(this);

        //스티커 이미지뷰
        sticker[0] = findViewById(R.id.sticker1);
        sticker[1] = findViewById(R.id.sticker2);
        sticker[2] = findViewById(R.id.sticker3);
        sticker[3] = findViewById(R.id.sticker4);

        //이미지뷰 터치리스너
        sticker[0].setOnTouchListener((View.OnTouchListener) this);
        sticker[1].setOnTouchListener((View.OnTouchListener) this);
        sticker[2].setOnTouchListener((View.OnTouchListener) this);
        sticker[3].setOnTouchListener((View.OnTouchListener) this);

        //스티커
        stickers[0] = findViewById(R.id.and);
        stickers[1] = findViewById(R.id.arrow);
        stickers[2] = findViewById(R.id.bar);
        stickers[3] = findViewById(R.id.barcode);
        stickers[14] = findViewById(R.id.clip);
        stickers[4] = findViewById(R.id.textcircle);
        stickers[5] = findViewById(R.id.oval);
        stickers[6] = findViewById(R.id.roundsquare);
        stickers[7] = findViewById(R.id.twinkleline);
        stickers[8] = findViewById(R.id.twinkle);
        stickers[9] = findViewById(R.id.heart);
        stickers[10] = findViewById(R.id.eyes);
        stickers[11] = findViewById(R.id.tw);
        stickers[12] = findViewById(R.id.flower);
        stickers[13] = findViewById(R.id.circle);

        //사이즈조절 할 스티커 배열
        size_stickers[0] = findViewById(R.id.size_sticker1);
        size_stickers[1] = findViewById(R.id.size_sticker2);
        size_stickers[2] = findViewById(R.id.size_sticker3);
        size_stickers[3] = findViewById(R.id.size_sticker4);

        linearLayout = findViewById(R.id.linear);



        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button line3 = findViewById(R.id.line3);
                line3.setVisibility(v.GONE);
                //done버튼 누르면 삼줄버튼 없어지게 하고 캡처 진행
                if(check == 0){ //그림판 안 쓸경우
                    linearLayout.setDrawingCacheEnabled(true);
                    bitmap = linearLayout.getDrawingCache();}
                else{ //그림판 쓸경우
                    frame.setDrawingCacheEnabled(true);
                    bitmap = frame.getDrawingCache();
                }
                //드로잉했던 이미지를 갤러리에 저장
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "capture", "");

                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                showNoti(); //알림
                Intent intent = new Intent(Drawing_Activity.this, SubActivity.class);
                startActivity(intent);
            }
        });


    }

    public void showNoti(){ //알림기능
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );

            builder = new NotificationCompat.Builder(this,CHANNEL_ID);

            //하위 버전일 경우
        }else{
            builder = new NotificationCompat.Builder(this);
        }

        //알림창 제목
        builder.setContentTitle("내 폰안에 폴꾸");

        //알림창 메시지
        builder.setContentText("갤러리에 사진 저장 완료!");

        //알림창 아이콘
        builder.setSmallIcon(R.drawable.phone);

        //클릭시 갤러리로 이동
        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        //알림창 실행
        manager.notify(1,notification);
    }


    public void choose(View v){ //사이즈 조절버튼에서 이미지 조절할 스티커 선택
        switch (v.getId()){
            case R.id.size_sticker1:
                size_v=findViewById(R.id.sticker1);
                break;
            case R.id.size_sticker2:
                size_v=findViewById(R.id.sticker2);
                break;
            case R.id.size_sticker3:
                size_v=findViewById(R.id.sticker3);
                break;
            case R.id.size_sticker4:
                size_v=findViewById(R.id.sticker4);
                break;
        }
    }


    public void controlsize(View v){ //선택된 뷰를 줄이고 늘린다
        switch (v.getId()) {
            case R.id.smaller:
                x-=20;
                y-=20;
                size_v.getLayoutParams().height=x;
                size_v.getLayoutParams().width=y;
                size_v.requestLayout();
                break;
            case R.id.bigger:
                x+=20;
                y+=20;
                size_v.getLayoutParams().height=x;
                size_v.getLayoutParams().width=y;
                size_v.requestLayout();
                break;

        }

    }

    public void size(View v){ //사이즈 조절버튼 누르면 레이아웃 나옴
        sizecontrol =findViewById(R.id.control);
        if (sizecontrol.getVisibility() == View.GONE){
            sizecontrol.setVisibility(View.VISIBLE);}
        else{
            sizecontrol.setVisibility(View.GONE);
        }

    }


    public boolean onTouch(View v, MotionEvent event){ //스티커 이미지뷰로 움직이게하는 이벤트
        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            X_val = event.getX();
            Y_val = event.getY();

            Log.i("Tag1", "Action Down rX " + event.getRawX() + "," + event.getRawY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            v.setX(event.getRawX() - X_val);
            v.setY(event.getRawY() - (Y_val + v.getHeight()));

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (v.getX() > width && v.getY() > height) {
                v.setX(width);
                v.setY(height);
            } else if (v.getX() < 0 && v.getY() > height) {
                v.setX(0);
                v.setY(height);
            } else if (v.getX() > width && v.getY() < 0) {
                v.setX(width);
                v.setY(0);
            } else if (v.getX() < 0 && v.getY() < 0) {
                v.setX(0);
                v.setY(0);
            } else if (v.getX() < 0 || v.getX() > width) {
                if (v.getX() < 0) {
                    v.setX(0);
                    v.setY(event.getRawY() - Y_val - v.getHeight());
                } else {
                    v.setX(width);
                    v.setY(event.getRawY() - Y_val - v.getHeight());
                }
            } else if (v.getY() < 0 || v.getY() > height) {
                if (v.getY() < 0) {
                    v.setX(event.getRawX() - X_val);
                    v.setY(0);
                } else {
                    v.setX(event.getRawX() - X_val);
                    v.setY(height);}
            }
        }
        return true;
    }

    public void onClick(View v) { // 스티커 버튼 누르면 다이얼로그 나오게
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_sticker);
        dialog.show();
    }

    public void sticker(View view){ //스티커 클릭시 이미지뷰에 적용
        for(int i =0; i<sticker.length; i++) {
            if (sticker[i].getVisibility() == View.GONE) {
                sticker[i].setVisibility(View.VISIBLE);
                switch (view.getId()){
                    case R.id.and:
                        sticker[i].setImageResource(R.drawable.and_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.and_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.arrow:
                        sticker[i].setImageResource(R.drawable.arrows_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.arrows_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.bar:
                        sticker[i].setImageResource(R.drawable.bar_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.bar_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.barcode:
                        sticker[i].setImageResource(R.drawable.barcode_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.barcode_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.clip:
                        sticker[i].setImageResource(R.drawable.clip_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.clip_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.textcircle:
                        sticker[i].setImageResource(R.drawable.text_circle_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.text_circle_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.oval:
                        sticker[i].setImageResource(R.drawable.oval_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.oval_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.roundsquare:
                        sticker[i].setImageResource(R.drawable.round_square_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.round_square_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.twinkleline:
                        sticker[i].setImageResource(R.drawable.twinkle1_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.twinkle1_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.twinkle:
                        sticker[i].setImageResource(R.drawable.twinkle2_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.twinkle2_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.heart:
                        sticker[i].setImageResource(R.drawable.heart_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.heart_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.eyes:
                        sticker[i].setImageResource(R.drawable.eyes_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.eyes_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.tw:
                        sticker[i].setImageResource(R.drawable.tw_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.tw_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.flower:
                        sticker[i].setImageResource(R.drawable.flower_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.flower_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                    case R.id.circle:
                        sticker[i].setImageResource(R.drawable.circle_sticker);
                        for (int j =0; j<size_stickers.length; j++){
                            if(size_stickers[j].getVisibility() == View.INVISIBLE){
                                size_stickers[j].setVisibility(View.VISIBLE);
                                size_stickers[j].setImageResource(R.drawable.circle_sticker);
                                break;
                            }
                            else{continue;}
                        }
                        size_v = sticker[i];
                        dialog.dismiss();
                        break;
                }
                break;
            } else {
                continue;
            }
        }
    }


    public void list(View view){ // 삼줄버튼 누르면 리스트 나오게
        LinearLayout li =findViewById(R.id.buttons);
        if (li.getVisibility() == View.GONE){
            li.setVisibility(View.VISIBLE);}
        else{
            li.setVisibility(View.GONE);
        }
    }

    public void Brush (View view) { //브러쉬버튼 누르면 그림판 나오고, 원래 이미지뷰들 안보이게 설정
        Button btn =findViewById(R.id.line3);
        btn.setVisibility(View.GONE);
        linearLayout.setDrawingCacheEnabled(true);
        bitmap = linearLayout.getDrawingCache();
        Drawable drawable = new BitmapDrawable(bitmap);
        if(colorst.equals("#000000")){
            linearLayout.setBackgroundColor(Color.parseColor("#6986E2"));
        }else{linearLayout.setBackgroundColor(Color.BLACK);}

        ImageView a = findViewById(R.id.backimg);
        ImageView b = findViewById(R.id.sticker1);
        ImageView c = findViewById(R.id.sticker2);
        ImageView d = findViewById(R.id.sticker3);
        ImageView e = findViewById(R.id.sticker4);
        a.setVisibility(View.GONE);
        b.setVisibility(View.GONE);
        c.setVisibility(View.GONE);
        d.setVisibility(View.GONE);
        e.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        frame = findViewById(R.id.frame_container);
        drawingPaper = new DrawingPaper(this);
        frame.addView(drawingPaper);
        frame.setBackground(drawable);
        frame.setVisibility(View.VISIBLE);
        check = 1;

        pencolors = findViewById(R.id.pencolors);
        pencolors.setVisibility(View.VISIBLE);
    }

    public void setPencolors (View view){ //pen 색상 정함

        if(view.getId()==R.id.penred){
            colorst = "#fa3636";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.penorange){
            colorst = "#ff9829";
            drawingPaper.setPaintColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.penyellow){
            colorst = "#FFE780";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.pengreen){
            colorst = "#7AA57A";
            drawingPaper.setPaintColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.penblue){
            colorst = "#2d9bfe";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.penpink){
            colorst = "#FFA4D2";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.penpurple){
            colorst = "#D27DFF";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.penwhite){
            colorst = "#FFFFFF";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.penblack){
            colorst = "#000000";
            drawingPaper.setPaintColor(Color.parseColor(colorst));
        }


    }

    public void color(View view){ //버튼 누르면 컬러팔레트 나오게
        linearLayout = findViewById(R.id.linear);
        palette = findViewById(R.id.palette);
        if(palette.getVisibility() == View.GONE){
            palette.setVisibility(View.VISIBLE);}
        else {palette.setVisibility(View.GONE);
    }}

    public void colorpicker(View view){ //백그라운드 배경 설정
        if(view.getId()==R.id.Red){
            colorst = "#F75454";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.Orange){
            colorst = "#F4A856";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.Yellow){
            colorst = "#FFE780";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));
        }
        else if(view.getId()==R.id.Green){
            colorst = "#7AA57A";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.Blue){
            colorst = "#68B6FC";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.Pink){
            colorst = "#FFA4D2";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.Purple){
            colorst = "#D27DFF";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.White){
            colorst = "#FFFFFF";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
        else if(view.getId()==R.id.Black){
            colorst = "#000000";
            linearLayout.setBackgroundColor(Color.parseColor(colorst));

        }
    }

    public void Gallery(View view){ //갤러리로 전환
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    //uri로 갤러리 이미지얻어서 이미지뷰에 설정
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView img = findViewById(R.id.backimg);
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    img.setImageURI(uri);}
                break;}
    }

    @Override  //오류때문에 넣은 코드
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
