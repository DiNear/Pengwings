package com.gady.pengwings;

import java.lang.*;
import android.app.Service;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.widget.TextView;


/**
 * Created by Andy on 2015-02-21.
 */
public class ChatHeadService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;
    private ImageView removeChatHead;
    private WindowManager.LayoutParams chatHeadParams;
    private WindowManager.LayoutParams removeChatHeadParams;
    private WindowManager.LayoutParams menuParams;
    private Point size;
    private View menuView;
    private LayoutInflater inflater;
    private TextView message;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        // Add the view for the menu
        inflater = LayoutInflater.from(this);
        menuView = inflater.inflate(R.layout.layout_menu, null);
        menuView.setVisibility(View.GONE);

        menuParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        menuParams.gravity = Gravity.TOP | Gravity.LEFT;
        menuParams.x = 0;
        menuParams.y = 200;

        windowManager.addView(menuView, menuParams);

        // Add the view for the thing that removes the chat head
        removeChatHead = new ImageView(this);
        removeChatHead.setImageResource(R.drawable.igloo);
        removeChatHead.setAdjustViewBounds(true);
        removeChatHead.setMaxWidth(150);
        removeChatHead.setMaxHeight(150);
        removeChatHead.setVisibility(View.GONE);

        removeChatHeadParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        removeChatHeadParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        removeChatHeadParams.x = 0;
        removeChatHeadParams.y = 75;

        windowManager.addView(removeChatHead, removeChatHeadParams);

        // Add the view for the chat head
        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.private1);
        chatHead.setAdjustViewBounds(true);
        chatHead.setMaxWidth(150);
        chatHead.setMaxHeight(150);

        chatHeadParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        chatHeadParams.gravity = Gravity.TOP | Gravity.LEFT;
        chatHeadParams.x = 0;
        chatHeadParams.y = 100;

        windowManager.addView(chatHead, chatHeadParams);

        // Add the touch listener for the service
        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float savedX;
            private float savedY;
            private boolean menuOpen = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!menuOpen) {
                            removeChatHead.setVisibility(View.VISIBLE);
                        }

                        removeChatHead.setVisibility(View.VISIBLE);
                        initialX = chatHeadParams.x;
                        initialY = chatHeadParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        return true;
                    case MotionEvent.ACTION_UP:
                        int[] loc = new int[2];
                        removeChatHead.getLocationOnScreen(loc);

                        // If the chat head is close to the 'igloo' then remove the chat head
                        if(((chatHeadParams.y <= loc[1]) && (chatHeadParams.y >= loc[1] - chatHead.getHeight())) &&
                                ((chatHeadParams.x + chatHead.getWidth()/2 >= loc[0]) && (chatHeadParams.x + chatHead.getWidth()/2 <= removeChatHead.getWidth() + loc[0]))) {
                            chatHead.setVisibility(View.GONE);
                            stopSelf();
                        } else {    // Otherwise
                            // If the menu was open, restore it
                            if(menuOpen) {
                                chatHeadParams.x = 0;
                                chatHeadParams.y = 0;

                                menuView.setVisibility(View.VISIBLE);
                                windowManager.updateViewLayout(chatHead, chatHeadParams);
                            } else {    // Otherwise snap the chat head to the appropriate side of the screen
                                if(chatHeadParams.x <= size.x/2) {
                                    chatHeadParams.x = 0;
                                } else {
                                    chatHeadParams.x = size.x;
                                }

                                chatHeadParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                                windowManager.updateViewLayout(chatHead, chatHeadParams);
                            }
                        }
                        removeChatHead.setVisibility(View.GONE);

                        // If the user taps on the chat head then toggle the menu visibility
                        if(Math.abs(event.getRawX() - initialTouchX) <= 5 && Math.abs(event.getRawY() - initialTouchY) <= 5){
                            if(menuOpen){
                                // If the menu was open, close it
                                menuOpen = false;
                                menuView.setVisibility(View.GONE);

                                chatHeadParams.x = (int)savedX;
                                chatHeadParams.y = (int)savedY;
                                windowManager.updateViewLayout(chatHead, chatHeadParams);
                            } else {
                                // Otherwise open it
                                menuOpen = true;
                                menuView.setVisibility(View.VISIBLE);

                                savedX = initialX;
                                savedY = initialY;

                                chatHeadParams.x = 0;
                                chatHeadParams.y = 0;
                                windowManager.updateViewLayout(chatHead, chatHeadParams);
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if(menuOpen) {
                            menuView.setVisibility(View.GONE);
                        }

                        chatHeadParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        chatHeadParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, chatHeadParams);
                        return true;
                }
                return false;
            }
        });
    }

    public void renderMessage(final String msg) {
        message = new TextView(this);
        message.setText(msg);
        message.setMaxWidth((int)(size.x - chatHead.getX()));
        if(chatHeadParams.x == 0) {
            message.setX(chatHead.getWidth());
        } else {
            message.setX(size.x - message.getWidth() - chatHead.getWidth());
        }
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
