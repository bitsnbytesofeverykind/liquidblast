package com.boxfight.boxfight;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.boxfight.boxfight.boxfight.R;

public class LANWIFI extends AppCompatActivity {

    private Button hostbtn;
    private Button joinbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To get full screen on start
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_lanwifi);

        hostbtn = (Button)findViewById(R.id.activity_lanwifi_hostbtn);
        joinbtn = (Button)findViewById(R.id.activity_lanwifi_joinbtn);

        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        String ipAddress = Formatter.formatIpAddress(ip);
        System.out.println("IP :"+ipAddress);

        hostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LANWIFI.this,LanGame.class);
                Bundle bundle = new Bundle();
                bundle.putString("Server","Y");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        });

        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LANWIFI.this,LanGame.class);
                Bundle bundle = new Bundle();
                bundle.putString("Server","N");
                bundle.putString("IP","192.168.43.1");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }
}
