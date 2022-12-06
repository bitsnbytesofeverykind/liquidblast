package com.boxfight.boxfight;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.boxfight.boxfight.boxfight.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To get full screen on start
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        final boolean first = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        if(first) {
            Intent intent = new Intent(SplashScreen.this, Introduction.class);
            startActivity(intent);
            finish();
        }
       else{
            Intent intent = new Intent(SplashScreen.this, Home.class);
            Bundle bundle = ActivityOptions.makeScaleUpAnimation(findViewById(R.id.activity_splash_screen_parent), 0, 0, findViewById(R.id.activity_splash_screen_parent).getWidth(), findViewById(R.id.activity_splash_screen_parent).getHeight()).toBundle();
            startActivity(intent);
            finish();
        }
            }
        },2000);

     }
}
