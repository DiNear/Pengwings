package com.gady.pengwings;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.zip.Inflater;

/**
 * Created by Andy on 2015-02-21.
 */
public class ChatHeadService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;
    private View menuView;
    private LayoutInflater inflater;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams menuParams;

    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.ic_launcher);

        inflater = LayoutInflater.from(this);
        menuView = inflater.inflate(R.layout.layout_menu, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        menuParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        menuParams.gravity = Gravity.TOP | Gravity.LEFT;
        menuParams.x = 0;
        menuParams.y = 200;

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        windowManager.addView(chatHead, params);

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float savedX;
            private float savedY;
            private boolean menuOpen = false;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();


                        return true;
                    case MotionEvent.ACTION_UP:
                        if(event.getRawX() == initialTouchX && event.getRawY() == initialTouchY){
                            if(menuOpen){
                                menuOpen = false;
                                windowManager.removeView(menuView);
                                params.x = (int)savedX;
                                params.y = (int)savedY;
                                windowManager.updateViewLayout(chatHead, params);
                            }
                            else{
                                menuOpen = true;
                                windowManager.addView(menuView, menuParams);

                                savedX = initialX;
                                savedY = initialY;

                                params.x = 0;
                                params.y = 0;
                                windowManager.updateViewLayout(chatHead, params);
                                chatHead.bringToFront();
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        if(!menuOpen) {
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(chatHead, params);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

}
