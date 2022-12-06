package com.boxfight.boxfight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfight.boxfight.boxfight.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

/*TODO : ASSUMPTION
    Both Players are online

 */

public class Game extends AppCompatActivity {
    /*UI Variables...*/
    ImageButton exitbtn;
    TextView player1Tv;
    TextView player2Tv;
    TextView player1indiTv;
    TextView player2indiTv;
    ImageButton soundbtn;

    private int seekPos = 0;
    TextView[][] Board = new TextView[10][10];
    TableLayout[] tableLayout = new TableLayout[10];

    Snackbar snackbar;

    int HIGHLIGHTCOLOR = R.color.colorYellow_200;
    int BLANKCOLOR = R.drawable.temp;
    int activeIndicator = R.drawable.player_active_indicator;
    int deactiveIndicator = R.drawable.player_deactive_indicator;
    RelativeLayout parentLayout;

    /*Firebase Variables...*/
            //FirebaseAuth mFirebaseAuth;
            //FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    DatabaseReference GameReference;
    DatabaseReference rootReference;

    /*Audio Variables*/
    AudioManager audioManager;
    MediaPlayer mediaPlayer;
    MediaPlayer tempsuccessMediaPlayer;
    MediaPlayer tempwrongboxmediaMediaPlayer;
    public int preparent1 = -1;
    public int prechild1 = -1;
    public int preparent2  = -1;
    public int prechild2 = -1;


    /*Logical Variables...*/
    private boolean CREATOR = false;
    private String MYUSERID;
    private String OPPOUSERID;
    private String MYUSERNAME;
    private String OPPOUSERNAME;
    private  String ROOMNAME;
        //private  boolean TOSSRESULT;
    private int ExitFlag = 0;
    private  boolean SOUNDFLAG = false;
    private  String OFFLINE_MSG = "You are Offline";
    public static  boolean OFFLINE = false;
    public boolean Enable = true;
    Player player;
    Player oppoPlayer;
    Box box;
    //CountDownTimer highlightCountDownTimer;

    /*Listeners...*/
    private ValueEventListener HomeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String x = dataSnapshot.getValue(String.class);
                if(x!= null && x.equals("T")){
                    rootReference.setValue(null);
                    Toast.makeText(getApplicationContext(),"Player left the game",Toast.LENGTH_LONG).show();
                    releaseMediaPlayer();
                    Intent intent = new Intent(Game.this,Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    rootReference.child("H").removeEventListener(HomeValueEventListener);
                    startActivity(intent);
                    finish();
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener MYUSERNAMEValueEventListener =  new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists())
            {       MYUSERNAME = dataSnapshot.getValue(String.class);

                //myUsernameTv.setText(MYUSERNAME);
                player.Name = MYUSERNAME;
                if(CREATOR){
                    player1Tv.setText(MYUSERNAME+": 0");
                }
                else{
                    player2Tv.setText(MYUSERNAME+": 0");

                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private ValueEventListener OPPOUSERNAMEValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                OPPOUSERNAME = dataSnapshot.getValue(String.class);
                // oppoUsernameTv.setText(OPPOUSERNAME);
                oppoPlayer.Name = OPPOUSERNAME;
                if(CREATOR){
                    player2Tv.setText(OPPOUSERNAME+": 0");
                }
                else{
                    player1Tv.setText(OPPOUSERNAME+": 0");

                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                seekPos = mediaPlayer.getCurrentPosition();

                mediaPlayer.pause();
                mediaPlayer.seekTo(seekPos);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mediaPlayer.start();
                mediaPlayer.seekTo(seekPos);

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                seekPos = mediaPlayer.getCurrentPosition();

                releaseMediaPlayer();
                moveTaskToBack(true);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Make Screen Full Size*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        /*set layout*/
        setContentView(R.layout.activity_game);

        /*Initiate audioManager */
        audioManager = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);

        tempsuccessMediaPlayer  = MediaPlayer.create(Game.this,R.raw.success);
        tempsuccessMediaPlayer.setVolume(1.0f,1.0f);

        tempwrongboxmediaMediaPlayer = MediaPlayer.create(Game.this,R.raw.errorwrongbox);
        tempwrongboxmediaMediaPlayer.setVolume(0.5f,0.5f);

        /*Get Sounds values(ON/OFF)*/
        SharedPreferences sharedPreferences = getSharedPreferences("com.boxfight.shivam.boxfight",MODE_PRIVATE);
        SOUNDFLAG = sharedPreferences.getBoolean("SOUND",true);
        Enable = true;

        /*Make Reference*/
        makeReference();

       /* Get data from Online Activity*/
        Bundle bundle = getIntent().getExtras();

        String isCreator = bundle.getString("CREATOR");/* "Y" if player Invite anther one else "N" */
        if(isCreator.equals("Y")){
            CREATOR = true;
        }

        MYUSERID = bundle.getString("MYUSERID");
        OPPOUSERID = bundle.getString("OPPOUSERID");


        player = new Player();
        oppoPlayer = new Player();


        /*Initiate Firebase Database*/
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();


        /* Get Username */
        mDatabaseReference.child("Users").child(MYUSERID).child("myUsername").addListenerForSingleValueEvent(MYUSERNAMEValueEventListener);
        /*Get Oppousername */
        mDatabaseReference.child("Users").child(OPPOUSERID).child("myUsername").addListenerForSingleValueEvent(OPPOUSERNAMEValueEventListener);

        final MediaPlayer btnplay =  MediaPlayer.create(Game.this, R.raw.touch);
        btnplay.setVolume(0.5f,0.5f);

        /*Give references to Boxes(81)*/
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                String str = "activity_game_btn"+i+""+j;

                int btnid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
                Board[i][j] = (TextView) findViewById(btnid);
                final int parent = i;
                final int child  = j;

                /*Set Default Background*/
                //Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
                //Board[i][j].setAlpha((float)0.3);
                /*set box listeners*/
                Board[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     if(SOUNDFLAG)
                        btnplay.start();
                     if(Enable)
                        makeGameMove(parent, child);
                    }
                });
            }
        }




        if(CREATOR){
            ROOMNAME = MYUSERID + OPPOUSERID ;
            rootReference = mDatabaseReference.child(ROOMNAME);
            GameReference = rootReference.child("MOVE");
            rootReference.child("RA").setValue(null);

            long time = System.currentTimeMillis();
            rootReference.child("GameCreatedTime").setValue(time);

         /* Make Toss*/
            Random random = new Random();
            int toss = random.nextInt(2);
            if(toss == 0){
                rootReference.child("toss").setValue("W");
                player.turn = false;
            }
            else{
                rootReference.child("toss").setValue("L");
                player.turn = true;
            }

            /*Select random box Initially*/
            int x = random.nextInt(9) + 1;
            rootReference.child("NB").setValue(x+"");
            int nextbox = x;

            /*Assign Player's Propertise*/
            player.id = 1;
            oppoPlayer.id = 2;
            player.sign = R.drawable.player1_sign;
            player.highlight = R.drawable.previousplayer1sign;
            oppoPlayer.sign = R.drawable.player2_sign;
            oppoPlayer.highlight = R.drawable.previousplayer2sign;

            rootReference.child("P1N").setValue(MYUSERNAME);
            rootReference.child("P2N").setValue(OPPOUSERNAME);


            int nb = nextbox;
            box = new Box(player.id,nb);
            box.turn = player.turn;
            makeHighlight(nb);


        }
        else
        {
            ROOMNAME = OPPOUSERID + MYUSERID;
            rootReference = mDatabaseReference.child(ROOMNAME);
            GameReference = rootReference.child("MOVE");
            rootReference.child("RR").setValue(null);
            oppoPlayer.id = 1;
            player.id = 2;
            player.sign = R.drawable.player2_sign;
            player.highlight = R.drawable.previousplayer2sign;
            oppoPlayer.sign = R.drawable.player1_sign;
            oppoPlayer.highlight = R.drawable.previousplayer1sign;
            player.turn = false;

        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                rootReference.child("toss").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                        try{
                            String result = dataSnapshot.getValue(String.class);
                            if(result.equals("W")){
                                player.turn = true;
                                Toast.makeText(getApplicationContext(),"You won the Toss",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"You loss the Toss",Toast.LENGTH_LONG).show();
                                player.turn = false;
                            }

                            rootReference.child("NB").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                    try{
                                        int nb = Integer.parseInt(dataSnapshot.getValue(String.class));
                                        box = new Box(player.id,nb);
                                        box.turn = player.turn;
                                        makeHighlight(nb);

                                    }
                                    catch (DatabaseException e){

                                    }
                             }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        catch (DatabaseException e){

                        }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }.start();


        }


        player.score = 0;
        oppoPlayer.score = 0;

        makeListeners();


        GameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() ) {
                    try{
                        Move mv = dataSnapshot.getValue(Move.class);
                    if (mv != null) {
                        boolean isExit = mv.EX;
                        if (!isExit) {
                            boolean isGameOver = mv.GO;
                            int parent = mv.PARENT;
                            int child = mv.CHILD;
                            int pre = oppoPlayer.score;
                            if (CREATOR) {
                                player.turn = mv.P1T;
                                box.turn = mv.P1T;
                                player.score = mv.P1S;
                                oppoPlayer.score = mv.P2S;
                            } else {
                                player.turn = mv.P2T;
                                box.turn = mv.P2T;      //TODO ;issues
                                player.score = mv.P2S;
                                oppoPlayer.score = mv.P1S;

                            }
                            if (player.turn) {
                                successtrigger(pre,oppoPlayer.score);
                                box.setMove(parent, child, oppoPlayer.id, oppoPlayer.score);
                                Board[parent][child].setBackgroundResource(oppoPlayer.sign);

                                if (CREATOR) {

                                    player2Tv.setText(OPPOUSERNAME + ": " + oppoPlayer.score);
                                } else {
                                    player1Tv.setText(OPPOUSERNAME + ": " + oppoPlayer.score);
                                }
                            }
                            cancelHighlight(parent,child);
                            makeHighlight(child);


                            if (isGameOver) {
                                makeGameOver();
                            }
                        }  else {
                            if (CREATOR) {
                                player.score = mv.P1S;
                                oppoPlayer.score = mv.P2S;
                            } else {
                                player.score = mv.P2S;
                                oppoPlayer.score = mv.P1S;
                            }
                            makeGameOver();
                        }

                    }

                }catch (NullPointerException e){
                    //TODO :Exceptions occur many times
                        //    Toast.makeText(getApplicationContext(),"Connection problem",Toast.LENGTH_LONG).show();
                    }
                    catch (DatabaseException e){

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void makeListeners() {
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ExitFlag += 1;
            if(ExitFlag == 1){
                Toast.makeText(getApplicationContext(),"Press again to Exit",Toast.LENGTH_LONG).show();
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        ExitFlag = 0;
                    }
                }.start() ;

            }
            if(ExitFlag == 2){
                boolean b = true;
                GameReference.child("EX").setValue(b);

            }


            }

        });

    }

    private void makeGameMove(int parent, int child) {
        if(player.turn && !OFFLINE ){
            if(box.makeMove(parent,child)){
                if(CREATOR){
                    Board[parent][child].setBackgroundResource(player.sign);
                    int pre = player.score;
                    player.score = box.playerScore;
                    successtrigger(pre,player.score);
                    player.turn = false;
                    sendMove(parent,child,player.score,box.gameOver);


                    String score = player.score + "";

                    player1Tv.setText(MYUSERNAME+": "+score);

                    cancelHighlight(parent,child);
                    makeHighlight(child);
                }
                else{
                    Board[parent][child].setBackgroundResource(player.sign);
                    int pre = player.score;
                    player.score = box.playerScore;
                    successtrigger(pre,player.score);
                    player.turn = false;
                    sendMove(parent,child,player.score,box.gameOver);


                    String score = player.score + "";

                    player2Tv.setText(MYUSERNAME+": "+score);
                    cancelHighlight(parent,child);
                    makeHighlight(child);
                }

                if(box.gameOver){
                    Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();
                    makeGameOver();
                }
            }
            else{
                errortrigger();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 80 milliseconds
                v.vibrate(80);
            }
        }
        else{
            errortrigger();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 80 milliseconds
            v.vibrate(80);
        }

    }

    private void makeGameOver() {
        Enable = false;
        rootReference.child("H").addValueEventListener(HomeValueEventListener);

        parentLayout.setAlpha((float) 0.3);
        RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);


        final TextView resulttv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams resultlp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150);
        resultlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        resultlp.setMargins(15,15,15,30);

        resulttv.setLayoutParams(resultlp);
        resulttv.setTextSize(25);
        resulttv.setGravity(View.TEXT_ALIGNMENT_CENTER);
        resulttv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        resulttv.setTextColor(getResources().getColor(R.color.colorWhite));
        resulttv.setBackgroundResource(R.drawable.gameoverbackground);
        resulttv.setTypeface(null, Typeface.BOLD);
        resulttv.setGravity(Gravity.CENTER);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player.score > oppoPlayer.score)
                { resulttv.setText("You Won");}
                else if(player.score== oppoPlayer.score){
                    resulttv.setText("Tie");
                }
                else{
                    resulttv.setText("You Lost");
                }
            }
        },1500);

        relativeLayout.addView(resulttv);





        TextView hometv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams homelp = new RelativeLayout.LayoutParams(
                150,
                150);
        homelp.addRule(RelativeLayout.CENTER_VERTICAL);
        homelp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        homelp.setMargins(15,15,15,15);
        hometv.setLayoutParams(homelp);
        hometv.setBackgroundResource(R.drawable.ic_home_black_48dp);
        //hometv.setId(R.id.hometvid);
        hometv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        if(!OFFLINE) {
            rootReference.child("H").removeEventListener(HomeValueEventListener);
            rootReference.child("H").setValue("T");
            releaseMediaPlayer();
            Intent intent = new Intent(Game.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        }
        });
        relativeLayout.addView(hometv);




        final TextView rematchtv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams rematchlp = new RelativeLayout.LayoutParams(
                150,
                150);
        rematchlp.addRule(RelativeLayout.CENTER_VERTICAL);
        rematchlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // rematchlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //rematchlp.addRule(RelativeLayout.LEFT_OF,R.id.hometvid);
        rematchlp.setMargins(30,15,15,15);
        rematchtv.setLayoutParams(rematchlp);
        rematchtv.setBackgroundResource(R.drawable.ic_autorenew_black_48dp);
        //TODO : ENABLE REMATCH...
       if(!CREATOR) {
            rematchtv.setAlpha((float)0.4);
            rootReference.child("RR").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String x = dataSnapshot.getValue(String.class);
                        if(x != null &&  x.equals("T")){
                            final CountDownTimer countDownTimer =     new CountDownTimer(30*6*1000,1000){

                                @Override
                                public void onTick(long l) {
                                    l = l/1000;
                                    if(l%2 == 0){
                                        rematchtv.setAlpha((float)0.4);
                                    }
                                    else {
                                        rematchtv.setAlpha((float)1.0);
                                    }

                                }

                                @Override
                                public void onFinish() {

                                }
                            };
                            rematchtv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                            if(!OFFLINE) {
                                GameReference.setValue(null);
                                rootReference.child("toss").setValue(null);
                                rootReference.child("NB").setValue(null);
                                releaseMediaPlayer();
                                //TODO : GOTO GAME AND SEND OPPOUSERNAME ,CREATOR(N) TO GAME
                                releaseMediaPlayer();
                                Intent intent = new Intent(Game.this, Game.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("OPPOUSERID", OPPOUSERID);
                                bundle.putString("MYUSERID", MYUSERID);
                                bundle.putString("CREATOR", "N");
                                intent.putExtras(bundle);
                                rootReference.child("RA").setValue("T");
                                countDownTimer.cancel();
                                startActivity(intent);
                                finish();
                            }
                            }
                            });

                            countDownTimer.start();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

       }
       else {
           rematchtv.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(!OFFLINE){
                       showSnack("Request Sent");
                   new CountDownTimer(2000, 2000) {
                       @Override
                       public void onTick(long l) {

                       }

                       @Override
                       public void onFinish() {
                        snackbar.dismiss();
                       }
                   }.start();
                   rootReference.child("RR").setValue("T");
                   rootReference.child("RA").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                         if(dataSnapshot.exists()){
                             String x = dataSnapshot.getValue(String.class);
                             if(x!= null && x.equals("T")){
                                 releaseMediaPlayer();
                                 Intent intent = new Intent(Game.this,Game.class);
                                 Bundle bundle = new Bundle();
                                 bundle.putString("OPPOUSERID",OPPOUSERID);
                                 bundle.putString("MYUSERID",MYUSERID);
                                 bundle.putString("CREATOR","Y");
                                 intent.putExtras(bundle);
                                 startActivity(intent);
                                 finish();
                             }
                             /*else{
                                 Toast.makeText(getApplicationContext(),"Request Rejected",Toast.LENGTH_LONG).show();
                                 Intent intent = new Intent(Game.this,Home.class);
                                 startActivity(intent);
                                 finish();
                             }*/

                         }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
               }
               }
           });
       }

        relativeLayout.addView(rematchtv);






        /*TextView exittv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams exitlp = new RelativeLayout.LayoutParams(
                150,
                150);
        exitlp.addRule(RelativeLayout.CENTER_VERTICAL);
        exitlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //exitlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //exitlp.addRule(RelativeLayout.RIGHT_OF,R.id.hometvid);
        exitlp.setMargins(15,15,30,15);
        exittv.setLayoutParams(exitlp);
        exittv.setBackgroundResource(R.drawable.exit);
        exittv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             rootReference.child("H").setValue("T");
                //System.exit(0);
                finish();
                /*Intent intent = new Intent(getApplicationContext(), Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);*/
           /* }
        });
        relativeLayout.addView(exittv);
        */

        relativeLayout.setAlpha((float) 1.0);

        addContentView(relativeLayout,rlp);

    }

    private void sendMove(int parent, int child, int playerScore, boolean gameOver) {
        Move mv = new Move();
        mv.PARENT = parent;
        mv.CHILD = child;
     if(CREATOR) {
         mv.P1S = playerScore;
         mv.P1T = false;
         mv.P2T = true;

     }
     else   {
         mv.P2S = playerScore;
         mv.P2T = false;
         mv.P1T = true;

     }
        mv.GO = gameOver;

        GameReference.setValue(mv);
    }


    private void makeReference() {

        exitbtn = (ImageButton) findViewById(R.id.activity_game_exit);
        parentLayout = (RelativeLayout)findViewById(R.id.activity_game_rel);
        player1Tv = (TextView)findViewById(R.id.activity_game_playername);
        player2Tv = (TextView)findViewById(R.id.activity_game_opponentplayername);
        player1indiTv = (TextView)findViewById(R.id.activity_game_player1_indicator);
        player2indiTv = (TextView)findViewById(R.id.activity_game_player2_indicator);
        soundbtn = (ImageButton)findViewById(R.id.activity_game_sound);
        for(int i=1;i<=9;i++){
            String str = "activity_game_boxno"+i;

            int layoutid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());

            tableLayout[i] = (TableLayout)findViewById(layoutid);

        }

    }

private void makeHighlight(int i) {
        int[] arr = new int[10];
        for(int j=1;j<=9;j++){
            arr[j] = box.Matrix[i][j];

        }
        if(player.turn){
           if(CREATOR){
            player1indiTv.setBackgroundResource(activeIndicator);
            player2indiTv.setBackgroundResource(deactiveIndicator);
            tableLayout[i].setBackgroundResource(R.drawable.player1_highlight);

           }
            else {
               player2indiTv.setBackgroundResource(activeIndicator);
               player1indiTv.setBackgroundResource(deactiveIndicator);
               tableLayout[i].setBackgroundResource(R.drawable.player2_highlight);

           }
        }
        else{
            if(CREATOR){
                player2indiTv.setBackgroundResource(activeIndicator);
                player1indiTv.setBackgroundResource(deactiveIndicator);
                tableLayout[i].setBackgroundResource(R.drawable.player2_highlight);

            }
            else {
                player1indiTv.setBackgroundResource(activeIndicator);
                player2indiTv.setBackgroundResource(deactiveIndicator);
                tableLayout[i].setBackgroundResource(R.drawable.player1_highlight);
            }

        }
    //tableLayout[i].setBackgroundResource(R.drawable.highlight);

        /*if(player.turn)HIGHLIGHTCOLOR = player.highlight;
        else HIGHLIGHTCOLOR = oppoPlayer.highlight;
        */
   /* for(int j=1;j<=9;j++){
       // if(arr[j] == 0){
           // Board[i][j].setBackgroundResource(R.drawable.selected_repeat);
            Board[i][j].setAlpha((float)1.0);
        //}
        /*else if(arr[j] == 1 || arr[j] == 3){
            Board[i][j].setBackgroundResource(R.drawable.player1_sign);

        }
        else if(arr[j] == 2 || arr[j] == 4){
            Board[i][j].setBackgroundResource(R.drawable.player2_sign);

        }
        else{

        }*/
/*
    }*/

}
    private void cancelHighlight(int parent,int child) {
        /*int[] arr = new int[10];
        for(int j=1;j<=9;j++){
            arr[j] = box1.Matrix[i][j];

        }*/
        tableLayout[parent].setBackgroundResource(R.drawable.tablebackground);

        preparent1 = preparent2;
        prechild1 =  prechild2;
        if(preparent1 != -1)
        {
            if(player.turn)
                Board[preparent1][prechild1].setBackgroundResource(player.sign);
            else
                Board[preparent1][prechild1].setBackgroundResource(oppoPlayer.sign);

        }

        preparent2 = parent;
        prechild2 = child;
        if(player.turn)
            Board[preparent2][prechild2].setBackgroundResource(oppoPlayer.highlight);
        else
            Board[preparent2][prechild2].setBackgroundResource(player.highlight);


       /* for(int j=1;j<=9;j++){
            //if(arr[j] == 0){
                // Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
                Board[i][j].setAlpha((float)0.3);

            //}
            /*else if(arr[j] == 1 || arr[j] == 3){
                Board[i][j].setBackgroundResource(R.drawable.unselected_p1sign);

            }
            else if(arr[j] == 2 || arr[j] == 4){
                Board[i][j].setBackgroundResource(R.drawable.unselected_p2sign);

            }
            else{

            }*//*
        }*/
    }



    private void releaseMediaPlayer(){
        if(mediaPlayer != null){

            mediaPlayer.release();

            mediaPlayer = null;

            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
    /*
    private  void startHighlightCounter(final TextView tv1){
      if(highlightCountDownTimer != null)
        highlightCountDownTimer.cancel();

        highlightCountDownTimer = new CountDownTimer(60*60*1000,1000) {
            @Override
            public void onTick(long l) {
                if(l%2 == 0){
                    tv1.setTextSize((float) 15.0);
                }
                else{
                    tv1.setTextSize((float) 5.0);
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }
*/
    @Override
    public void onBackPressed() {

        Toast.makeText(getApplicationContext(),"You can't go back",Toast.LENGTH_SHORT).show();

    }


   private void successtrigger(int pre,int now){
       if(pre<now){
           if(SOUNDFLAG){
               tempsuccessMediaPlayer.start();
           }
       }
   }
    private void errortrigger(){
        if(SOUNDFLAG)
            tempwrongboxmediaMediaPlayer.start();

    }

    private void showSnack(String msg){
        snackbar = Snackbar.make(findViewById(R.id.activity_game_rel),msg,Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if(isConnected){
            OFFLINE = false;
            if(snackbar != null)
                snackbar.dismiss();

        }
        else{
            OFFLINE = true;
            showSnack(OFFLINE_MSG);
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
if(SOUNDFLAG){
    int res = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
    if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
        mediaPlayer = MediaPlayer.create(Game.this,R.raw.game_music);
        mediaPlayer.setVolume(0.5f,0.5f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        mediaPlayer.seekTo(seekPos);
    }

}

        checkConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null && SOUNDFLAG) {
            seekPos = mediaPlayer.getCurrentPosition();
            System.out.println("Hello0000 " + seekPos);
        }
        releaseMediaPlayer();
    }
}



/*     rel.setAlpha((float) 0.1);

                RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.FILL_PARENT);
                TextView textView1 = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                textView1.setLayoutParams(lp);
                textView1.setTextSize(25);
                textView1.setText("Hello");
                textView1.setTextColor(getResources().getColor(R.color.colorAccent));
                relativeLayout.addView(textView1);
                relativeLayout.setAlpha((float) 1.0);
                addContentView(relativeLayout,rlp);
*/
