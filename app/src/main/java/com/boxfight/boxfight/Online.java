package com.boxfight.boxfight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfight.boxfight.boxfight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/***************************************************
 * TODO : ASSUMPTION                                 *
 * INTERNET IS ALWAYS ON                     *
 * USER IS SIGNED IN                         *
 * USERNAME IS EXIST                         *
 * USER WILL NOT EXIT IN BETWEEN             *
 * *                                                 *                                                  *
 * /
 **************************************************/
//TODO : WHEN INTERNET IS OFF THEN SHOW SNACKBAR AND DISABLE ALL THINGS
//TODO : USER CAN'T SEND REQUEST HIMSELF;
public class Online extends AppCompatActivity {
    //XML REFERENCE...
    Button invitebtn;
    EditText invitedt;
    TextView welcomeTv;
    ImageButton settingbtn;

    private boolean signInStatus = false;
    private final String INVITITION = "Invitition";
    private final String MYINVITITION = "MyInvitition";
    private String UserId;
    private String myUserName;
    private ListView listView;
    private DetailsAdapter adapter;
    private List<InviteDetails> detailsList = new ArrayList<>();

    //Firebase Variables...
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;//TODO : UNUSED BECAUSE OF ASSUMPTIONS

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    ValueEventListener TimeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                long value = (Long) dataSnapshot.getValue();
                CURRTIME = value;
                System.out.println("mytime" + value);

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    ValueEventListener myUserNameValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                try {
                    myUserName = dataSnapshot.getValue(String.class);
                    welcomeTv.setText("Welcome " + myUserName);
                    SharedPreferences sharedPreferences = getSharedPreferences("com.boxfight.shivam.boxfight", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("UNAME",myUserName);
                    editor.commit();
                } catch (DatabaseException e) {
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    ValueEventListener myGarbageInvititionValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.exists() && snapshot != null) {
                            InviteBox inviteBox = snapshot.getValue(InviteBox.class);
                            String key = snapshot.getKey();
                            if ((inviteBox.response != null) && (inviteBox.response).equals("N")) {
                                System.out.print("I'm here-child");
                                Log.v("zzz","childgarbage");

                                    mDatabaseReference.child(MYINVITITION).child(UserId).child(key).setValue(null);
                                    mDatabaseReference.child(INVITITION).child(key).child(UserId).setValue(null);
                            }

                        }

                    }
                } catch (DatabaseException e) {
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    ChildEventListener myInvititionChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.exists()) {
                try {
                    InviteBox inviteBox = dataSnapshot.getValue(InviteBox.class);
                    String response = inviteBox.response;
                    String result = dataSnapshot.getKey();
                    if (response != null && response.equals("Y")) {
                        mDatabaseReference.child("Users").child(mFirebaseUser.getUid()).child("Status").setValue("F");
                        Intent intent = new Intent(Online.this, Game.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("OPPOUSERID", result);
                        bundle.putString("MYUSERID", mFirebaseUser.getUid());
                        bundle.putString("CREATOR", "Y");
                        intent.putExtras(bundle);
                        Log.v("zzz","res-y");

                        mDatabaseReference.child(INVITITION).child(result).child(UserId).setValue(null);
                        mDatabaseReference.child(MYINVITITION).child(UserId).child(result).setValue(null);
                        //TODO : remove invitition
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();
                    } else {
                    if(response!= null && response.equals("N")){

                        Log.v("zzz","res-N");

                        mDatabaseReference.child(INVITITION).child(result).child(UserId).setValue(null);
                       mDatabaseReference.child(MYINVITITION).child(UserId).child(result).setValue(null);

                    }
                        }
                } catch (DatabaseException e) {

                }


            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    View.OnClickListener inviteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Get Opponent's Username
            String oppoUserName = invitedt.getText().toString();

            if (!oppoUserName.equals(myUserName)) {
                mDatabaseReference.child("Time").setValue(ServerValue.TIMESTAMP);
                sendInvitition(oppoUserName);
            } else {
                Toast.makeText(getApplicationContext(), "You can't invite yourself", Toast.LENGTH_LONG).show();
            }

        }
    };
    FirebaseUser mFirebaseUser;
    Snackbar snackbar;
    private String OFFLINE_MSG = "You are Offline";
    public static  boolean REFRESH = false;
    public static boolean OFFLINE = false;
    public String isExist;
    private boolean INITIALIZE = false;
    private String root = "boxfight-auth";
    private long CURRTIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Full Screen...
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Firebase Initialization Part

        //TODO : MAKE STATUS TRUE
        // mDatabaseReference.child("Users").child(mFirebaseUser.getUid()).child("Status").setValue("T");

        //XML INITIALIZATION PART
        setContentView(R.layout.activity_online);
        invitebtn = (Button) findViewById(R.id.activity_online_invitebtn);
        listView = (ListView) findViewById(R.id.activity_online_recyclerview);
        invitedt = (EditText) findViewById(R.id.activity_online_inviteedt);
        welcomeTv = (TextView) findViewById(R.id.activity_online_welcome);
        settingbtn = (ImageButton)findViewById(R.id.activity_online_setting);
        adapter = new DetailsAdapter(this, detailsList);
        listView.setAdapter(adapter);

        invitedt.setText("");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("Time").addValueEventListener(TimeValueEventListener);
        mDatabaseReference.child("Time").setValue(ServerValue.TIMESTAMP);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            signInStatus = true;
            Log.v("Onlineabcd", "Hi");
            UserId = mFirebaseUser.getUid();

            mDatabaseReference.child("Users").child(UserId).child("Status").setValue("T");
            mDatabaseReference.child("Users").child(UserId).child("myUsername").addListenerForSingleValueEvent(myUserNameValueEventListener);
            mDatabaseReference.child(MYINVITITION).child(UserId).addListenerForSingleValueEvent(myGarbageInvititionValueEventListener);
            mDatabaseReference.child(MYINVITITION).child(UserId).addChildEventListener(myInvititionChildEventListener);
            invitebtn.setOnClickListener(inviteOnClickListener);

            settingbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if( myUserName != null && UserId != null && !OFFLINE ){
                    Intent intent = new Intent(Online.this,Username.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("UID",UserId);
                    bundle.putString("PN",myUserName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }


                }
            });



            readData();



        }



    }

    private void sendInvitition(String user) {

        mDatabaseReference.child("ListUsers").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        invitedt.setText("");
                        String result = dataSnapshot.getValue(String.class);
                        isExist = result;


                        if (isExist != null && !isExist.equals("N")) {
                            Log.v("OnlineClick", "clicked2");

                            // TODO : SEND invitation TO OPPONENT AND ADD DATE AND TIME TO INVITITION BOX
                            InviteBox inviteBox = new InviteBox();
                            inviteBox.time = CURRTIME;
                            mDatabaseReference.child(INVITITION).child(result).child(UserId).setValue(inviteBox);
                            mDatabaseReference.child(MYINVITITION).child(UserId).child(result).setValue(inviteBox);
                            isExist = null;
                        } else {
                        }

                    } catch (DatabaseException e) {

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username does not exist", Toast.LENGTH_LONG).show();
                    isExist = "N";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isExist = "N";
            }
        });

    }

    private void readData() {


        detailsList.clear();
        adapter.notifyDataSetChanged();

        /*TODO : READ LIST FROM INVITITITON BOX AND IF IT IS NOT EMPTY THEN
                 GET DETAILS OBJECT  AND INVITTITION DATE AND TIME THEN CALCULATE REMAINNING AND ASSIGN TO DETAILS COUNTER
                 ADD DETAILS OBJECT IN LIST
                 NOTIFY DATA CHANGED

        */
        //TODO :: OPTIMIZE VALUE EVENT LISTENER

        mDatabaseReference.child(INVITITION).child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                REFRESH = true;
                detailsList.clear();
                adapter.notifyDataSetChanged();

                if (dataSnapshot.exists()) {
                    try {
                        mDatabaseReference.child("Time").setValue(ServerValue.TIMESTAMP);
                        new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }
                            @Override
                            public void onFinish() {
                                detailsList.clear();
                                adapter.notifyDataSetChanged();
                                REFRESH = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    final String key = snapshot.getKey();
                                    InviteBox inviteBox = snapshot.getValue(InviteBox.class);

                                    final long time = (inviteBox.time + 60000) - CURRTIME;

                                    Log.v("anc", time + "");
                                    if (time > 3000) {

                                        mDatabaseReference.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    User user = dataSnapshot.getValue(User.class);

                                                    InviteDetails details = new InviteDetails();
                                                    details.setMyUserId(UserId);
                                                    details.setOppoUserId(key);
                                                    details.setCounter(time);
                                                    details.setOppoUserName(user.myUsername);
                                                    details.photoUrl = user.PhotoUrl;
                                                    detailsList.add(details);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                          Log.v("zzz","whta");
                                        mDatabaseReference.child(INVITITION).child(UserId).child(key).setValue(null);
                                        mDatabaseReference.child(MYINVITITION).child(key).child(UserId).setValue(null);
                                    }
                                }

                            }
                        }.start();


                        adapter.notifyDataSetChanged();


                    } catch (DatabaseException e) {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TODO : ADD CHILD-ADDED LISTENER AND ON TRIGGER CHILD SHOULD BE ADD TO LIST
        //TODO : ADD CHILD-REMOVED LISTENER AND ON TRIGGER CHILD SHOULD BE REMOVE FROM LIST

    }




    private void showSnack(String msg) {
        snackbar = Snackbar.make(findViewById(R.id.activity_online_parent), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            OFFLINE = false;
            if (snackbar != null)
                snackbar.dismiss();

            invitebtn.setEnabled(true);
        } else {
            OFFLINE = true;
            invitebtn.setEnabled(false);
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
        Intent intent = new Intent(Online.this, Home.class);
        startActivity(intent);
        finish();

    }
}
