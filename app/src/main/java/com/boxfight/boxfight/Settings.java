package com.boxfight.boxfight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.boxfight.boxfight.boxfight.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends Activity {
    private EditText usernameedt;
    private Button submitbtn;
    private TextView errorTv;
    Snackbar snackbar;
    private String OFFLINE_MSG = "You are Offline";

    private static final String ERROEMSGOFEXIST = "*Username is already taken.";
    private static  final String ERROROFUSERNAME = "*Username should only contain A-Z , a-z , 0-9 , _ characters of length between 2-10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To get full screen on start
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_settings);


        Bundle b = getIntent().getExtras();
        final String userID = b.getString("UID");
        final String email = b.getString("EMAIL");
        final String photourl = b.getString("PHOTOURL");
        final String displayName = b.getString("DISPLAYNAME");

        usernameedt = (EditText) findViewById(R.id.activity_settings_username);
        submitbtn = (Button) findViewById(R.id.activity_settings_submit);
        errorTv = (TextView) findViewById(R.id.activity_settings_error);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                final String name = usernameedt.getText().toString();
                if (nameIsValid(name)) {
                    reference.child("ListUsers").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                errorTv.setVisibility(View.VISIBLE);
                                errorTv.setText(ERROEMSGOFEXIST);
                            } else {
                                errorTv.setVisibility(View.INVISIBLE);
                                User user = new User();
                                user.myUsername = name;
                                user.email = email;
                                user.DisplayName = displayName;
                                user.PhotoUrl = photourl;
                                reference.child("Users").child(userID).setValue(user);
                                reference.child("ListUsers").child(name).setValue(userID);
                                Intent intent = new Intent(Settings.this, Online.class);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText(ERROROFUSERNAME);
                }
            }
        });

    }

    private boolean nameIsValid(String name) {
        if (name.length() < 2 || name.length() > 10) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char x = name.charAt(i);

            if (!((x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z') || (x == '_') || (x >= '0' && x <= '9'))) {
                return false;
            }
        }
        return true;
    }

    private void showSnack(String msg) {
        snackbar = Snackbar.make(findViewById(R.id.activity_settings_parent), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            if (snackbar != null)
                snackbar.dismiss();

            submitbtn.setEnabled(true);
        } else {
            submitbtn.setEnabled(false);
            showSnack(OFFLINE_MSG);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(new ConnectivityReceiver.ConnectivityReceiverListener() {
            @Override
            public void onNetworkConnectionChanged(boolean isConnected) {
                checkConnection();

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Settings.this, Home.class);
        startActivity(intent);
        finish();
    }
}
