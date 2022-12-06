package com.boxfight.boxfight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
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

import com.airbnb.lottie.LottieAnimationView;
import com.boxfight.boxfight.boxfight.R;

import java.util.Random;

public class GameWithComputer extends Activity {
    ImageButton exitbtn;
    TextView player1Tv;
    TextView player2Tv;
    TextView player1indiTv;
    TextView player2indiTv;
    TextView[][] Board = new TextView[10][10];
    int activeIndicator = R.drawable.player_active_indicator;
    int deactiveIndicator = R.drawable.player_deactive_indicator;

    TableLayout[] tableLayout = new TableLayout[10];

    RelativeLayout parentLayout;
    private  int ExitFlag;
    Player player1;
    Player player2;
    Box box1;
    Box box2;
    AudioManager audioManager;

    MediaPlayer mediaPlayer;
    MediaPlayer tempsuccessMediaPlayer;
    MediaPlayer tempwrongboxmediaMediaPlayer;

    public int preparent1 = -1;
    public int prechild1 = -1;
    public int preparent2  = -1;
    public int prechild2 = -1;

    private int seekPos = 0;
    private  boolean SOUNDFLAG = false;
    ImageButton soundbtn;
    int noofMoves = 0;
    public int[][] cboard = new int[10][10];
    Computer computer = new Computer();
    CountDownTimer downTimer = new CountDownTimer(6*1000, 1000) {
        @Override
        public void onTick(long l) {
          //  Toast.makeText(getApplicationContext(),""+l/1000 ,Toast.LENGTH_SHORT).show();
            System.out.println("Hi******");
        }

        @Override
        public void onFinish() {
            computer.stopFlag  = true;
        }
    };


    public boolean Enable = true;
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

    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To get full screen on start
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        setContentView(R.layout.activity_game);


        animationView = (LottieAnimationView) findViewById(R.id.activity_game_animation);
        animationView.setAnimation("timer.json");
        animationView.loop(true);
        animationView.playAnimation();


        SharedPreferences sharedPreferences = getSharedPreferences("com.boxfight.shivam.boxfight",MODE_PRIVATE);
        SOUNDFLAG = sharedPreferences.getBoolean("SOUND",true);

        audioManager = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
        tempsuccessMediaPlayer  = MediaPlayer.create(GameWithComputer.this,R.raw.success);
        tempsuccessMediaPlayer.setVolume(1.0f,1.0f);

        tempwrongboxmediaMediaPlayer = MediaPlayer.create(GameWithComputer.this,R.raw.errorwrongbox);
        tempwrongboxmediaMediaPlayer.setVolume(0.5f,0.5f);

        makeReference();
        Enable = true;
        player1 = new Player();
        player2 = new Player();
        player1.Name = "Computer";
        player2.Name = "You";
        player1.score = 0;
        player2.score = 0;
        player1Tv.setText(player1.Name+" : 0");
        player2Tv.setText(player2.Name+" : 0");
        player1.sign = R.drawable.player1_sign;
        player2.sign = R.drawable.player2_sign;
        player1.id = 1;
        player2.id = 2;

        final MediaPlayer btnplay =  MediaPlayer.create(GameWithComputer.this, R.raw.touch);
        btnplay.setVolume(0.5f,0.5f);

        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                String str = "activity_game_btn"+i+""+j;

                int btnid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
                Board[i][j] = (TextView) findViewById(btnid);
                final int parent = i;
                final int child  = j;
                //Board[i][j].setAlpha((float)0.3);
                //Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
                Board[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                  if(player2.turn) {
                      if (SOUNDFLAG)
                          btnplay.start();
                      if (Enable)
                          makeGameMove(parent, child);
                  }
                  else{
                      errortrigger();
                      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                      // Vibrate for 500 milliseconds
                      v.vibrate(80);

                  }
                    }
                });
            }
        }
        /*soundbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SOUNDFLAG){
                    SOUNDFLAG = false;
                    soundbtn.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    releaseMediaPlayer();
                }
                else{
                    SOUNDFLAG = true;
                    soundbtn.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    int res = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                    if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                        mediaPlayer = MediaPlayer.create(QuickPlay.this,R.raw.game_music);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                }
            }
        });*/

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
                    releaseMediaPlayer();
                    Intent intent = new Intent(GameWithComputer.this,Home.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

        player1.turn = false;
        player2.turn = true;
        Random random = new Random();

        int nb = random.nextInt(9)+1;

        box1 = new Box(player1.id,nb);
        box2 = new Box(player2.id,nb);
        box1.turn = player1.turn;
        box2.turn = player2.turn;

        makeHighlight(nb);

    }

    private void makeGameMove(int parent, final int child) {
        if(player2.turn){
            if(box2.makeMove(parent,child)){
                Board[parent][child].setBackgroundResource(player2.sign);
                int tempplayer2score = player2.score;
                player2.score = box2.playerScore;
                if(tempplayer2score<player2.score){
                    if(SOUNDFLAG){
                        tempsuccessMediaPlayer.start();
                    }
                }

                player2.turn = false;
                box2.turn = false;
                player2Tv.setText(player2.Name+" : "+player2.score);

                player1.turn = true;
                box1.turn = true;
                box1.setMove(parent,child,player2.id,player2.score);

                cancelHighlight(parent,child);
                makeHighlight(child);

                if(box2.gameOver){
                    makeGameOver();
                }
                else{
                    animationView.setVisibility(View.VISIBLE);
                    // cboard[parent][child] = 2;
                    for(int i=1;i<=9;i++){
                        for(int j=1;j<=9;j++){
                            if(box2.Matrix[i][j] == 1 || box2.Matrix[i][j] == 3){
                                cboard[i][j] = box2.Matrix[i][j];
                            }
                            else if(box1.Matrix[i][j] == 2 || box1.Matrix[i][j]==4){
                                cboard[i][j] = box1.Matrix[i][j];
                            }
                            else{
                                cboard[i][j] = 0;
                            }
                        }
                    }

                    if(noofMoves<=10)computer.Depth = 1;
                    if(noofMoves<=20)computer.Depth = 3;
                    else if(noofMoves<=30)computer.Depth = 4;
                    else computer.Depth = 5;
                    //int move = 0;
                    int comchild = child;
                    computer.stopFlag = false;
                    downTimer.start();

                    ComputerThread computerThread = new ComputerThread(comchild,cboard);
                    computerThread.run();

                        // ComputerTask computerTask = new ComputerTask(comchild);


                   // computerTask.execute(cboard);

                    //move = computerTask.doInBackground(cboard);
                    //int move = computer.play(cboard,child,0,0);
                    noofMoves++;
                }

            }
            else{
                errortrigger();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(80);
            }
        }

    }

    private void makeGameOver() {
        Enable = false;
        parentLayout.setAlpha((float) 0.3);
        RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);



        TextView resulttv = new TextView(getApplicationContext());
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
        if(player1.score > player2.score)
        { resulttv.setText(player1.Name+" Won");}
        else if(player1.score== player2.score){
            resulttv.setText("Tie");
        }
        else{
            resulttv.setText(player2.Name+" Won");
        }
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
        hometv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseMediaPlayer();
                Intent intent = new Intent(GameWithComputer.this,Home.class);
                startActivity(intent);
                finish();
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
        rematchtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               releaseMediaPlayer();
                Intent intent = new Intent(GameWithComputer.this,GameWithComputer.class);
                startActivity(intent);
                finish();
            }
        });
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

                finish();
                //      System.exit(0);
            }
        });
        relativeLayout.addView(exittv);
        */
        relativeLayout.setAlpha((float) 1.0);

        addContentView(relativeLayout,rlp);


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
            arr[j] = box1.Matrix[i][j];

        }
        if(player1.turn){
            player1indiTv.setBackgroundResource(activeIndicator);
            player2indiTv.setBackgroundResource(deactiveIndicator);
            tableLayout[i].setBackgroundResource(R.drawable.player1_highlight);

        }
        else{
            player2indiTv.setBackgroundResource(activeIndicator);
            player1indiTv.setBackgroundResource(deactiveIndicator);
            tableLayout[i].setBackgroundResource(R.drawable.player2_highlight);

        }
        //tableLayout[i].setBackgroundResource(R.drawable.highlight);

        /*if(player.turn)HIGHLIGHTCOLOR = player.highlight;
        else HIGHLIGHTCOLOR = oppoPlayer.highlight;
        *//*
        for(int j=1;j<=9;j++){
            //  if(arr[j] == 0){
            //     Board[i][j].setBackgroundResource(R.drawable.selected_repeat);
            Board[i][j].setAlpha((float)1.0);
            //}
            /*else if(arr[j] == 1 || arr[j] == 3){
                Board[i][j].setBackgroundResource(R.drawable.player1_sign);

            }
            else if(arr[j] == 2 || arr[j] == 4){
                Board[i][j].setBackgroundResource(R.drawable.player2_sign);

            }
            else{

            }
*//*
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
            if(player1.turn)
                Board[preparent1][prechild1].setBackgroundResource(player1.sign);
            else
                Board[preparent1][prechild1].setBackgroundResource(player2.sign);

        }

        preparent2 = parent;
        prechild2 = child;
        if(player1.turn)
            Board[preparent2][prechild2].setBackgroundResource(R.drawable.previousplayer2sign);
        else
            Board[preparent2][prechild2].setBackgroundResource(R.drawable.previousplayer1sign);


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

    private void errortrigger(){
        if(SOUNDFLAG)
            tempwrongboxmediaMediaPlayer.start();

    }

    private void releaseMediaPlayer(){
        if(mediaPlayer != null){

            mediaPlayer.release();

            mediaPlayer = null;

            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(SOUNDFLAG) {
            int res = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer = MediaPlayer.create(GameWithComputer.this, R.raw.game_music);
                mediaPlayer.setVolume(0.5f,0.5f);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                mediaPlayer.seekTo(seekPos);
            }
        }
        System.out.println("Hello "+seekPos);
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

    @Override
    public void onBackPressed() {

    }

    class ComputerTask extends AsyncTask<int[][],Void,Integer>{
        int child;
        public ComputerTask(int child) {
            this.child = child;
        }

        @Override
        protected Integer doInBackground(int[][]... ints) {

            int move =  computer.play(cboard,child,0,0);
            return  move;
        }

        @Override
        protected void onPostExecute(final Integer integer) {
            super.onPostExecute(integer);

            final Integer iinteger = integer;
          new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  downTimer.cancel();

                  int parent = child;
                  child = iinteger;

                  int finalParent = parent;
                  int finalChild = child;
                  if(box1.makeMove(finalParent, finalChild)){
                      player1.turn = false;
                      box1.turn = false;

                      Board[finalParent][finalChild].setBackgroundResource(player1.sign);
                      int tempplayer1score = player1.score;
                      player1.score = box1.playerScore;
                      if(tempplayer1score < player1.score){
                          if(SOUNDFLAG){
                              tempsuccessMediaPlayer.start();
                          }
                      }
                      player1Tv.setText(player1.Name+" : "+player1.score);


                      player2.turn = true;
                      box2.turn = true;
                      box2.setMove(finalParent, finalChild,player1.id,player1.score);

                      animationView.setVisibility(View.INVISIBLE);
                      cancelHighlight(finalParent,finalChild);
                      makeHighlight(finalChild);

                      if(box1.gameOver){
                          makeGameOver();
                      }
                  }
                  else{
                      errortrigger();
                      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                      // Vibrate for 500 milliseconds
                      v.vibrate(80);
                  }

              }
          },2000);

        }
    }


    public class ComputerThread implements Runnable {
        int child;
        int[][] cboard;
        ComputerThread(int c,int[][] cb){
            child = c;
            cboard = cb;
        }
        @Override
        public void run() {
            final int move =  computer.play(cboard,child,0,0);
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          downTimer.cancel();

                                          int parent = child;
                                          child = move;

                                          int finalParent = parent;
                                          int finalChild = child;
                                          if(box1.makeMove(finalParent, finalChild)){
                                              player1.turn = false;
                                              box1.turn = false;

                                              Board[finalParent][finalChild].setBackgroundResource(player1.sign);
                                              int tempplayer1score = player1.score;
                                              player1.score = box1.playerScore;
                                              if(tempplayer1score < player1.score){
                                                  if(SOUNDFLAG){
                                                      tempsuccessMediaPlayer.start();
                                                  }
                                              }
                                              player1Tv.setText(player1.Name+" : "+player1.score);


                                              player2.turn = true;
                                              box2.turn = true;
                                              box2.setMove(finalParent, finalChild,player1.id,player1.score);

                                              animationView.setVisibility(View.INVISIBLE);
                                              cancelHighlight(finalParent,finalChild);
                                              makeHighlight(finalChild);

                                              if(box1.gameOver){
                                                  makeGameOver();
                                              }
                                          }
                                          else{
                                              errortrigger();
                                              Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                              // Vibrate for 500 milliseconds
                                              v.vibrate(80);
                                          }

                                      }
                                  },2000);



                              }
                          }
    );

        }
    }


}
