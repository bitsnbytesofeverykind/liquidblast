package com.boxfight.boxfight;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfight.boxfight.boxfight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinRoom extends Activity {
    TextView romeNameTv;
    TextView playTv;
    EditText myUsernameedt;
    EditText oppUsernameedt;
    Spinner spinner;
    private int timerVal;

    private FirebaseAuth mFirebaseAuth;
    private  FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseUser firebaseUser;

    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        // romeNameTv = (TextView)findViewById(R.id.activity_create_room_roomnametv);
        playTv = (TextView)findViewById(R.id.activity_create_room_playtv);
        //spinner = (Spinner)findViewById(R.id.activity_creat_room_timer);
        myUsernameedt = (EditText)findViewById(R.id.activity_create_room_myusername);
        oppUsernameedt = (EditText)findViewById(R.id.activity_create_room_oppousername);
        setListeners();
        //setFirebaseUtility();
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        Toast.makeText(getApplicationContext(),firebaseUser.getEmail(),Toast.LENGTH_LONG).show();

    }

    private void setFirebaseUtility() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        firebaseUser = mFirebaseAuth.getCurrentUser();
    }


    private void setListeners() {
        playTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String opoUsername = oppUsernameedt.getText().toString();
                mDatabaseReference.child("ListUsers").child(opoUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                      /*  if(dataSnapshot.exists()){
                            String id = dataSnapshot.getValue(String.class);
                            Log.v("Idddd",id);
                            mDatabaseReference.child("Users").child(id).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        String status = dataSnapshot.getValue(String.class);
                                        if(status.equals("1")){
                                            User user = new User();
                                            user.myUsername = myUsernameedt.getText().toString();
                                            user.oppoUserName = oppUsernameedt.getText().toString();
                                            user.email = firebaseUser.getEmail();
                                            user.preOppoUserName = user.oppoUserName;
                                            String userId = firebaseUser.getUid();

                                            if(!(user.myUsername.equals(user.oppoUserName))){

                                                mDatabaseReference.child("Users").child(userId).setValue(user);
                                                mDatabaseReference.child("ListUsers").child(user.myUsername).setValue(userId);

                                                Intent intent = new Intent(JoinRoom.this,Game.class);
                                                Bundle bundle = ActivityOptions.makeScaleUpAnimation(view,0,0,view.getWidth(),view.getHeight()).toBundle();
                                                Bundle b = new Bundle();
                                                b.putString("oppousername",user.oppoUserName);
                                                b.putString("isCreator","N");
                                                intent.putExtras(b);
                                                startActivity(intent,bundle);

                                            }

                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"User is not available",Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else{
                            //TODo : display appropriate output to user
                            Toast.makeText(getApplicationContext(),"User is not registered",Toast.LENGTH_LONG).show();
                        }*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        myUsernameedt = (EditText)findViewById(R.id.activity_create_room_myusername);
        oppUsernameedt = (EditText)findViewById(R.id.activity_create_room_oppousername);

        setFirebaseUtility();
        final String userId = firebaseUser.getUid();
        mDatabaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    isExist = true;
                    mDatabaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                User user = dataSnapshot.getValue(User.class);
                                  myUsernameedt.setText(user.myUsername);
                              //    oppUsernameedt.setText(user.preOppoUserName);
                                mDatabaseReference.child("Users").child(firebaseUser.getUid()).child("status").setValue("1");

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    isExist = false;
                    //TODO : get username on before create activity....
                    mDatabaseReference.child("ListUsers").child(firebaseUser.getDisplayName()).setValue(firebaseUser.getUid());
                    mDatabaseReference.child("Users").child(firebaseUser.getUid()).child("status").setValue("1");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
