package com.boxfight.boxfight;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.boxfight.boxfight.boxfight.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "HomeActivity"; // for printing log
    private static final int RC_SIGN_IN = 1;//to confirm sign in
    private static final String OFFLINE_MSG = "You are Offline";
    /* UI references... */
    private Button quickPlaybtn;
    private Button signbtn;
    private ImageButton helpbtn;
    private ImageButton exitbtn;
    private Button onlinebtn;
    private ImageButton soundbtn;
    private Button lanbtn;
    private Button computerbtn;
    private Snackbar snackbar;
    private Interpolator interpolator;  //This is used for animation
    //TODO: know role of ProgressDialog
    private ProgressDialog mProgressDialog;//Don't know

    //Firebase and Google
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    //Flags
    private boolean signInStatus = false;//Sign in Flag
    private String isUserExist = "U";//U means Unknown
    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (firebaseUser != null) {
                signbtn.setText("Sign Out");
                signInStatus = true;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                final String userId = firebaseUser.getUid();
                reference.child("Users").child(userId).child("myUsername").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            isUserExist = "Y";
                        } else {
                            isUserExist = "N";
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                signbtn.setText("Sign In");
                signInStatus = false;
            }

        }
    };
    private boolean SOUNDFLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To get full screen on start
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }


        setContentView(R.layout.activity_home);

        initialize();
        makeReference();
        setAnimationUtil();
        setListenersUtil();

    }

    private void initialize() {

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    private void setListenersUtil() {
        //Check previous sound state
        SharedPreferences sharedPreferences = getSharedPreferences("com.boxfight.shivam.boxfight", MODE_PRIVATE);
        SOUNDFLAG = sharedPreferences.getBoolean("SOUND", true);
        if (SOUNDFLAG) {
            soundbtn.setImageResource(R.drawable.ic_volume_up_black_24dp);
        } else {
            soundbtn.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }


        soundbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.boxfight.shivam.boxfight", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (SOUNDFLAG) {
                    SOUNDFLAG = false;
                    soundbtn.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    Toast.makeText(getApplicationContext(), "Sound Disabled", Toast.LENGTH_SHORT).show();
                } else {
                    SOUNDFLAG = true;
                    soundbtn.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    Toast.makeText(getApplicationContext(), "Sound Enabled", Toast.LENGTH_SHORT).show();

                }
                editor.putBoolean("SOUND", SOUNDFLAG);
                editor.commit();
            }
        });


        quickPlaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, QuickPlay.class);
                Bundle bundle = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();
                startActivity(intent, bundle);
                finish();
            }
        });


        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    signbtn.setEnabled(false);

                    if (signInStatus) {
                        signOut();
                    } else {
                        signIn();
                    }
                    disableButton(3, signbtn);
                } else {
                    showSnack(OFFLINE_MSG);
                }


            }
        });


        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        onlinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    if (signInStatus) {
                        onlinebtn.setEnabled(false);

                        if (isUserExist.equals("Y")) {
                            Intent intent = new Intent(Home.this, Online.class);
                            startActivity(intent);
                            finish();
                        } else if (isUserExist.equals("U")) {

                        } else {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            Intent intent = new Intent(Home.this, Settings.class);
                            Bundle b = new Bundle();
                            b.putString("UID", firebaseUser.getUid());
                            b.putString("EMAIL", firebaseUser.getEmail());
                            b.putString("DISPLAYNAME", firebaseUser.getDisplayName());
                            b.putString("PHOTOURL", String.valueOf(firebaseUser.getPhotoUrl()));
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(getApplication(), "Please sign in", Toast.LENGTH_LONG).show();
                    }

                    disableButton(3, onlinebtn);
                } else {
                    showSnack(OFFLINE_MSG);
                }

            }
        });


        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCEAGTQhFsShIuxjp0TJ4BDg")));
            }
        });

        lanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,LANWIFI.class);
                startActivity(intent);
            }
        });

        computerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, GameWithComputer.class);
                Bundle bundle = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();
                startActivity(intent, bundle);
                finish();

            }
        });

    }

    private void disableButton(long x, final Button tv) {
        new CountDownTimer(x * 1000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v("xyzz", l / 1000 + "");
            }

            @Override
            public void onFinish() {
                tv.setEnabled(true);
            }
        }.start();

    }

    private void showSnack(String msg) {
        snackbar = Snackbar.make(findViewById(R.id.activity_home_parent), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void signOut() {

        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                });
    }


    private void setAnimationUtil() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        String path = "android.view.animation.LinearInterpolator,";

        try {
            interpolator = (Interpolator) Class.forName(path).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setcreatAnimation(computerbtn,200,1200,displayMetrics);
        setcreatAnimation(quickPlaybtn, 500, 1200, displayMetrics);
        setcreatAnimation(onlinebtn, 800, 1200, displayMetrics);
        setcreatAnimation(lanbtn, 950, 1200, displayMetrics);
        setcreatAnimation(signbtn, 1100, 1200, displayMetrics);
        setcreatAnimation(helpbtn, 1300, 1200, displayMetrics);
        setcreatAnimation(exitbtn, 1500, 1200, displayMetrics);
        setcreatAnimation(soundbtn, 1700, 1200, displayMetrics);
    }

    private void makeReference() {
        quickPlaybtn = (Button) findViewById(R.id.activity_home_quick_play);
        signbtn = (Button) findViewById(R.id.activity_home_sign);
        helpbtn = (ImageButton) findViewById(R.id.activity_home_help);
        exitbtn = (ImageButton) findViewById(R.id.activity_home_exit);
        onlinebtn = (Button) findViewById(R.id.activity_home_online);
        soundbtn = (ImageButton) findViewById(R.id.activity_home_sound);
        lanbtn = (Button)findViewById(R.id.activity_home_lan);
        computerbtn = (Button)findViewById(R.id.activity_home_computer);

    }


    private void setcreatAnimation(Button ref, int startTime, int duration, DisplayMetrics displayMetrics) {
        ref.setTranslationY(displayMetrics.heightPixels);

        ref.animate().setInterpolator(interpolator)
                .setDuration(duration)
                .setStartDelay(startTime)
                .translationYBy(-displayMetrics.heightPixels)
                .start();
    }

    private void setcreatAnimation(ImageButton ref, int startTime, int duration, DisplayMetrics displayMetrics) {
        ref.setTranslationY(displayMetrics.heightPixels);

        ref.animate().setInterpolator(interpolator)
                .setDuration(duration)
                .setStartDelay(startTime)
                .translationYBy(-displayMetrics.heightPixels)
                .start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mFirebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                //FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                //Toast.makeText(getApplicationContext(),firebaseUser.getEmail(),Toast.LENGTH_LONG).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(Home.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        } else {

        }
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    private void showProgressDialog() {
        if (!this.isFinishing()) {

            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("loading...");
                mProgressDialog.setIndeterminate(true);
            }

            mProgressDialog.show();

        }

    }

    private void hideProgressDialog() {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
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

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        hideProgressDialog();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    private void checkSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            if (snackbar != null)
                snackbar.dismiss();
            Log.v(TAG, "Connected..");
            //Toast.makeText(getApplicationContext(),"Connected...",Toast.LENGTH_LONG).show();
            checkSignIn();
        } else {
            Log.v(TAG, "ccDisconnected..");
            showSnack(OFFLINE_MSG);
        }

    }


}
