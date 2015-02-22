package com.gady.pengwings;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by Andy on 2015-02-21.
 */
public class ChatHeadService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;
    private ImageView removeChatHead;
    private WindowManager.LayoutParams chatHeadParams;
    private WindowManager.LayoutParams removeChatHeadParams;
    private Point size;

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

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.ic_launcher);

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

        removeChatHead = new ImageView(this);
        removeChatHead.setImageResource(R.drawable.ic_launcher);
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

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        removeChatHead.setVisibility(View.VISIBLE);
                        initialX = chatHeadParams.x;
                        initialY = chatHeadParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int[] loc = new int[2];
                        removeChatHead.getLocationOnScreen(loc);
                        if((chatHeadParams.y <= loc[1]) && (chatHeadParams.y >= loc[1] - chatHead.getHeight())) {
                            if((chatHeadParams.x + chatHead.getWidth()/2 >= loc[0]) && (chatHeadParams.x + chatHead.getWidth()/2 <= removeChatHead.getWidth() + loc[0])) {
                                chatHead.setVisibility(View.GONE);
                                stopSelf();
                            }
                        } else {
                            if(chatHeadParams.x <= size.x/2) {
                                chatHeadParams.x = 0;
                            } else {
                                chatHeadParams.x = size.x;
                            }
                            chatHeadParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(chatHead, chatHeadParams);
                        }
                        removeChatHead.setVisibility(View.GONE);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        chatHeadParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        chatHeadParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, chatHeadParams);
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
