package com.boxfight.boxfight;

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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class LanGame extends AppCompatActivity {

    ImageButton exitbtn;
    TextView player1Tv;
    TextView player2Tv;
    TextView player1indiTv;
    TextView player2indiTv;
    ImageButton soundbtn;
    WaitForOverServer waitForOverServer;
    WaitForOverClient waitForOverClient;
    ServerTask serverTask;
    ClientTask clientTask;
    static boolean GameOverFlag = false;
    static boolean startFlag = false;
    TableLayout[] tableLayout = new TableLayout[10];

    static int ExitFlag = 0;
    TextView[][] Board = new TextView[10][10];



    int activeIndicator = R.drawable.player_active_indicator;
    int deactiveIndicator = R.drawable.player_deactive_indicator;
    RelativeLayout parentLayout;

    public ServerThread serverThread;
    public Thread tserver;
    Client client;


    AudioManager audioManager;
    MediaPlayer mediaPlayer;
    MediaPlayer tempsuccessMediaPlayer;
    MediaPlayer tempwrongboxmediaMediaPlayer;

    private int seekPos = 0;
    private  boolean SOUNDFLAG = false;

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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        /*set layout*/
        setContentView(R.layout.activity_game);


            /*Make Reference*/
            makeReference();
            player1Tv.setText("Player 1");
            player2Tv.setText("Player 2");

        SharedPreferences sharedPreferences = getSharedPreferences("com.boxfight.shivam.boxfight",MODE_PRIVATE);
        SOUNDFLAG = sharedPreferences.getBoolean("SOUND",true);

        audioManager = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);

        tempsuccessMediaPlayer  = MediaPlayer.create(LanGame.this,R.raw.success);
        tempsuccessMediaPlayer.setVolume(1.0f,1.0f);

        tempwrongboxmediaMediaPlayer = MediaPlayer.create(LanGame.this,R.raw.errorwrongbox);
        tempwrongboxmediaMediaPlayer.setVolume(0.5f,0.5f);

        final MediaPlayer btnplay =  MediaPlayer.create(LanGame.this, R.raw.touch);
        btnplay.setVolume(0.5f,0.5f);

        Bundle bundle = getIntent().getExtras();
        final String Server = bundle.getString("Server");

        if(Server.equals("Y")){
            //serverThread = new ServerThread();
            //tserver = new Thread(serverThread);
            //tserver.start();

            serverTask = new ServerTask();
            serverTask.execute();
             waitForOverServer = new WaitForOverServer(serverTask);
               waitForOverServer.execute();
            /*Give references to Boxes(81)*/
            for(int i=1;i<=9;i++){
                for(int j=1;j<=9;j++){
                    String str = "activity_game_btn"+i+""+j;

                    int btnid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
                    Board[i][j] = (TextView) findViewById(btnid);
                    final int parent = i;
                    final int child  = j;

                /*Set Default Background*/
                 //   Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
                    //Board[i][j].setAlpha((float)0.3);

                    /*set box listeners*/
                    Board[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        if(SOUNDFLAG)
                            btnplay.start();
                       if(LanGame.startFlag)
                            serverTask.makeGameMove(parent, child);
                        }
                    });
                }

            }

        }
        else{
            String ip = bundle.getString("IP");
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                //client = new Client(inetAddress);
                //Thread thread = new Thread(client);
                //thread.start();
                clientTask = new ClientTask(inetAddress);
                clientTask.execute();
                waitForOverClient = new WaitForOverClient(clientTask);
                waitForOverClient.execute();
                /*Give references to Boxes(81)*/
                for(int i=1;i<=9;i++){
                    for(int j=1;j<=9;j++){
                        String str = "activity_game_btn"+i+""+j;

                        int btnid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
                        Board[i][j] = (TextView) findViewById(btnid);
                        final int parent = i;
                        final int child  = j;

                /*Set Default Background*/
                    //    Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
                      //  Board[i][j].setAlpha((float)0.3);
                /*set box listeners*/
                        Board[i][j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            if(SOUNDFLAG)
                                btnplay.start();
                             if(LanGame.startFlag)
                                clientTask.makeGameMove(parent, child);
                            }
                        });
                    }
                }

            //    System.out.println("Connect :"+client+" "+inetAddress);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

      }
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
                    if(Server.equals("Y")){
                    if(LanGame.startFlag)
                    {serverTask.makeGameOver();}
                        else{
                        releaseMediaPlayer();
                        Intent intent = new Intent(LanGame.this,LANWIFI.class);
                        startActivity(intent);
                        finish();

                    }
                        /*try {
                            serverTask.gameOverFlag = true;
                            if(serverTask.socket!= null && !serverTask.socket.isClosed())
                            {    serverTask.socket.close();}

                            if(serverTask.serverSocket!= null && !serverTask.serverSocket.isClosed())
                           {    serverTask.serverSocket.close();}

                            if(serverTask.dis!= null )
                            {    serverTask.dis.close();}
                            if(serverTask.dos!= null )
                            {    serverTask.dos.close();}

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        serverTask.cancel(true);*/
                    }
                    else{
                    if(LanGame.startFlag)
                    {    clientTask.makeGameOver();}
                    else {
                        releaseMediaPlayer();
                        Intent intent = new Intent(LanGame.this,LANWIFI.class);
                        startActivity(intent);
                        finish();

                    }
                        /*try {
                            clientTask.gameOverFlag = true;
                            if(clientTask.socket!= null && !clientTask.socket.isClosed())
                            {    clientTask.socket.close();}


                            if(clientTask.dis!= null )
                            {    clientTask.dis.close();}
                            if(clientTask.dos!= null )
                            {    clientTask.dos.close();}

                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        clientTask.cancel(true);*/
                    }
                }

            }

        });

    }

    class WaitForOverServer extends AsyncTask<Void,Void,Void>{
        ServerTask task;
        public WaitForOverServer(ServerTask serverTask) {
            task = serverTask;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!LanGame.GameOverFlag);
            task.cancel(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long x = 0;
            if(LanGame.startFlag){x = 3000;}
            new  Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    releaseMediaPlayer();
                    Intent intent = new Intent(LanGame.this,LANWIFI.class);
                    startActivity(intent);
                    finish();

                }
            },x);

        }
    }


    class WaitForOverClient extends AsyncTask<Void,Void,Void>{
        ClientTask task;
        public WaitForOverClient(ClientTask serverTask) {
            task = serverTask;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println(ExitFlag);
            while (!LanGame.GameOverFlag);
            task.cancel(true);

            System.out.println("Heloooooooooooooooooo******");
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long x = 0;
            if(LanGame.startFlag){x = 3000;}
            new  Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    releaseMediaPlayer();
                    Intent intent = new Intent(LanGame.this,LANWIFI.class);
                    startActivity(intent);
                    finish();

                }
            },x);

        }
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


    class ServerTask extends AsyncTask<Void,Void,Void>{
        ServerSocket serverSocket;

        boolean startflag = false;
        public String MYUSERNAME = getSharedPreferences("com.boxfight.shivam.boxfight", MODE_PRIVATE).getString("UNAME","Player 1");
        public String OPPOUSERNAME = "Player 2";
        ObjectInputStream dis;
        ObjectOutputStream dos;
        boolean firstflag = false;
        boolean gameOverFlag = false;
        //DataInputStream dis;
        //DataOutputStream dos;
        Socket socket = null;


        Player player;
        Player oppoPlayer;
        public int preparent1 = -1;
        public int prechild1 = -1;
        public int preparent2  = -1;
        public int prechild2 = -1;

        Box box;


        private void makeGameMove(int p, int c) {
            box.turn = player.turn;
            if(player.turn ){
                final int parent = p;
                final int child = c;
                if(box.makeMove(parent,child)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Board[parent][child].setBackgroundResource(player.sign);

                        }
                    });
                    int tempplayerscore = player.score;
                    player.score = box.playerScore;
                    if(tempplayerscore < player.score){
                        if(SOUNDFLAG){
                            tempsuccessMediaPlayer.start();
                        }
                    }
                    player.turn = false;
                    oppoPlayer.turn = true;
                    sendMove(parent,child,player.score,box.gameOver);


                    final String score = player.score + "";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player1Tv.setText(MYUSERNAME+": "+score);

                        }
                    });
                    cancelHighlight(parent,child);
                    makeHighlight(child);

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
        public void makeGameOver(){
            System.out.println("Closing this connection : " + socket);
            try {
               if(socket != null) {
                   socket.close();
               }serverSocket.close();
                System.out.println("Connection closed");
                if(dis!=null){dis.close();}
                if(dos!=null){dos.close();}

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(LanGame.startFlag){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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
                        if(player.score > oppoPlayer.score)
                        { resulttv.setText("You Won");}
                        else if(player.score== oppoPlayer.score){
                            resulttv.setText("Tie");
                        }
                        else{
                            resulttv.setText("You Lost");
                        }
                        relativeLayout.addView(resulttv);
                        relativeLayout.setAlpha((float) 1.0);

                        addContentView(relativeLayout,rlp);

                    }
                });
            }

            LanGame.GameOverFlag = true;
            gameOverFlag = true;

        }
        public void sendMove(int parent, int child, int score, boolean gameOver){
            LanMove move = new LanMove(parent,child,score,gameOver);
            try {
                System.out.println(MYUSERNAME+"Hi");
                dos.writeObject(move);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void makeHighlight(final int i){
            int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];
            }
            final int[] arr = temp_arr.clone();
            if(player.turn){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player1indiTv.setBackgroundResource(activeIndicator);
                        player2indiTv.setBackgroundResource(deactiveIndicator);
                        tableLayout[i].setBackgroundResource(R.drawable.player1_highlight);

                    }
                });

            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player2indiTv.setBackgroundResource(activeIndicator);
                        player1indiTv.setBackgroundResource(deactiveIndicator);
                        tableLayout[i].setBackgroundResource(R.drawable.player2_highlight);

                    }
                });
            }
        /*if(player.turn)HIGHLIGHTCOLOR = player.highlight;
        else HIGHLIGHTCOLOR = oppoPlayer.highlight;
        */
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableLayout[i].setBackgroundResource(R.drawable.highlight);

                    /*for(int j=1;j<=9;j++){
                        //if(arr[j] == 0){
                          //  Board[i][j].setBackgroundResource(R.drawable.selected_repeat);
                            Board[i][j].setAlpha((float)1.0);
                        //}
                        /*else if(arr[j] == 1 || arr[j] == 3){
                            Board[i][j].setBackgroundResource(R.drawable.player1_sign);

                        }
                        else if(arr[j] == 2 || arr[j] == 4){
                            Board[i][j].setBackgroundResource(R.drawable.player2_sign);

                        }
                        else{

                        }*//*

                    }*//*

                }
            });*/

        }

        private void cancelHighlight(final int parent,final int child) {
            /*int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];

            }
            final int[] arr = temp_arr.clone();
            */runOnUiThread(new Runnable() {
                @Override
                public void run() {

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
                        Board[preparent2][prechild2].setBackgroundResource(R.drawable.previousplayer2sign);
                    else
                        Board[preparent2][prechild2].setBackgroundResource(R.drawable.previousplayer1sign);

                    /*for(int j=1;j<=9;j++){
                       // if(arr[j] == 0){
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
            });

        }


        public void sendMessage(String msg){
            try {

                System.out.println("sent "+msg);
                if(dos != null) {
                    if(!firstflag){
                        /* Make Toss*/
                        Random random = new Random();
                        final int toss = random.nextInt(2);
                        int x = random.nextInt(9) + 1;
                        String tossres;
                        if(toss == 0){
                            tossres =  "You Won the Toss";
                            player.turn = true;
                            oppoPlayer.turn = false;
                        }
                        else{
                            player.turn = false;
                            oppoPlayer.turn = true;
                            tossres = "You Lost the Toss";
                        }
                        final String tossres_final = tossres;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),tossres_final,Toast.LENGTH_LONG).show();
                                player1Tv.setText(MYUSERNAME+": 0");
                                player2Tv.setText(OPPOUSERNAME+": 0");
                            }
                        });
                        box = new Box(player.id,x);
                        box.turn = player.turn;
                        makeHighlight(x);

                        Begin begin = new Begin(toss,x,MYUSERNAME);
                        dos.writeObject(begin);

                    }
                    else{


                    }
                    //   Begin user = new User("Server :",msg);
                    //    dos.writeObject(user);
                }
                /*if(msg!=null && msg.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    dis.close();
                    dos.close();
                    flag = true;

                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                serverSocket = new ServerSocket(5056);
                socket = serverSocket.accept();
                LanGame.startFlag = true;
                player = new Player();
                oppoPlayer = new Player();
                player.id = 1;
                oppoPlayer.id = 2;
                player.score = 0;
                oppoPlayer.score = 0;

                player.sign = R.drawable.player1_sign;
                player.highlight = R.drawable.player1_highlight;
                oppoPlayer.sign = R.drawable.player2_sign;
                oppoPlayer.highlight = R.drawable.player2_highlight;

                System.out.println("A new client is connected : " + socket);
                dos =  new ObjectOutputStream(socket.getOutputStream());
                dis = new ObjectInputStream(socket.getInputStream());
                sendMessage("Hi");

                Begin begin = (Begin) dis.readObject();
                OPPOUSERNAME = begin.name;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player2Tv.setText(OPPOUSERNAME+": 0");
                    }
                });

/*                dis = new DataInputStream(s.getInputStream());
                dos =  new DataOutputStream(s.getOutputStream());
*/
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            while (true){
                System.out.println("Hiiiiiiiiiiiiiiiiiiiiiiiiiiii");

                if(gameOverFlag){
                    System.out.println("+++++++++++++++++");
                    break;
                }
                else{
                    if(dis != null) {
                        try {
                            LanMove move = (LanMove) dis.readObject();
                            boolean isGameOver = move.gameOver;
                            final int parent = move.parent;
                            final int child = move.child;
                            player.turn = true;
                            oppoPlayer.turn = false;
                            int tempoppoplayerscore = oppoPlayer.score;
                            oppoPlayer.score = move.score;
                            if(tempoppoplayerscore < oppoPlayer.score){
                                if(SOUNDFLAG){
                                    tempsuccessMediaPlayer.start();
                                }
                            }
                            box.setMove(parent, child, oppoPlayer.id, oppoPlayer.score);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Board[parent][child].setBackgroundResource(oppoPlayer.sign);
                                    player2Tv.setText(OPPOUSERNAME + ": " + oppoPlayer.score);

                                }
                            });
                            cancelHighlight(parent,child);
                            makeHighlight(child);

                            if(isGameOver){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();

                                    }
                                });

                                makeGameOver();
                                break;
                            }
                        } catch (IOException e) {
                            System.out.println(e);
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            System.out.println(e);

                            e.printStackTrace();
                        }
                    }

                }

            }


            return null;
        }
    }
    class ClientTask extends AsyncTask<Void,Void,Void>{
        Socket socket;
        InetAddress address;
        ObjectInputStream dis;
        ObjectOutputStream dos;
        public String MYUSERNAME = getSharedPreferences("com.boxfight.shivam.boxfight", MODE_PRIVATE).getString("UNAME","Player 2");
        public String OPPOUSERNAME = "Player 1";
        boolean gameOverFlag = false;
        Player player;
        Player oppoPlayer;
        Box box;
        public int preparent1 = -1;
        public int prechild1 = -1;
        public int preparent2  = -1;
        public int prechild2 = -1;

        //DataInputStream dis;
        //DataOutputStream dos;
        boolean startflag = false;
        ClientTask(InetAddress address){
            this.address = address;

        }

        private void makeGameMove(int p, int c) {
            box.turn = player.turn;
            if(player.turn ){
                final int parent = p;
                final int child = c;
                if(box.makeMove(parent,child)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Board[parent][child].setBackgroundResource(player.sign);

                        }
                    });
                    int tempplayerscore = player.score;
                    player.score = box.playerScore;
                    if(tempplayerscore < player.score){
                        if(SOUNDFLAG){
                            tempsuccessMediaPlayer.start();
                        }
                    }
                    player.turn = false;
                    oppoPlayer.turn = true;

                    sendMove(parent,child,player.score,box.gameOver);


                    final String score = player.score + "";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player2Tv.setText(MYUSERNAME+": "+score);

                        }
                    });
                    cancelHighlight(parent,child);
                    makeHighlight(child);

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
        public void sendMove(int parent, int child, int score, boolean gameOver){
            LanMove move = new LanMove(parent,child,score,gameOver);
            try {
                dos.writeObject(move);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void makeHighlight(final int i){
            int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];
            }
            final int[] arr = temp_arr.clone();
            if(player.turn){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player2indiTv.setBackgroundResource(activeIndicator);
                        player1indiTv.setBackgroundResource(deactiveIndicator);
                        tableLayout[i].setBackgroundResource(R.drawable.player2_highlight);


                    }
                });

            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player1indiTv.setBackgroundResource(activeIndicator);
                        player2indiTv.setBackgroundResource(deactiveIndicator);
                        tableLayout[i].setBackgroundResource(R.drawable.player1_highlight);


                    }
                });
            }
        /*if(player.turn)HIGHLIGHTCOLOR = player.highlight;
        else HIGHLIGHTCOLOR = oppoPlayer.highlight;
        */
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableLayout[i].setBackgroundResource(R.drawable.highlight);

                   /* for(int j=1;j<=9;j++){
                        //if(arr[j] == 0){
                          //  Board[i][j].setBackgroundResource(R.drawable.selected_repeat);
                            Board[i][j].setAlpha((float)1.0);
                        //}
                        /*else if(arr[j] == 1 || arr[j] == 3){
                            Board[i][j].setBackgroundResource(R.drawable.player1_sign);

                        }
                        else if(arr[j] == 2 || arr[j] == 4){
                            Board[i][j].setBackgroundResource(R.drawable.player2_sign);

                        }
                        else{

                        }*//*

                    }*/
/*
                }
            });*/

        }
        private void cancelHighlight(final int parent,final int child) {
            /*int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];

            }
            final int[] arr = temp_arr.clone();
            */runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                        Board[preparent2][prechild2].setBackgroundResource(R.drawable.previousplayer1sign);
                    else
                        Board[preparent2][prechild2].setBackgroundResource(R.drawable.previousplayer2sign);

                    /*for(int j=1;j<=9;j++){
                        //if(arr[j] == 0){
                          //  Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
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
            });

        }
        public void makeGameOver(){

            System.out.println("Closing this connection : " + socket);
            try {

               if(socket!=null){ socket.close();}
                System.out.println("Connection closed");
                if(dis!=null){dis.close();}
                if(dos!=null){dos.close();}

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(LanGame.startFlag){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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
                        if(player.score > oppoPlayer.score)
                        { resulttv.setText("You Won");}
                        else if(player.score== oppoPlayer.score){
                            resulttv.setText("Tie");
                        }
                        else{
                            resulttv.setText("You Lost");
                        }
                        relativeLayout.addView(resulttv);
                        relativeLayout.setAlpha((float) 1.0);

                        addContentView(relativeLayout,rlp);
                    }
                });

            }

            LanGame.GameOverFlag = true;
            gameOverFlag = true;



        }

        public  void sendmessage(String msg){
            try {
                System.out.println("sent "+msg);
                if(dos != null) {
                    //    User user = new User("Client :",msg);
                    //  dos.writeObject(user);
                }

                if(msg.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    dis.close();
                    dos.close();
                   // flag = true;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("Hello Client");
            try {
                socket = new Socket(address,5056);
                dos =  new ObjectOutputStream(socket.getOutputStream());
                dis = new ObjectInputStream(socket.getInputStream());
                LanGame.startFlag = true;
                player = new Player();
                oppoPlayer = new Player();
                player.id = 2;
                oppoPlayer.id = 1;
                player.score = 0;
                oppoPlayer.score = 0;

                player.sign = R.drawable.player2_sign;
                player.highlight = R.drawable.player2_highlight;
                oppoPlayer.sign = R.drawable.player1_sign;
                oppoPlayer.highlight = R.drawable.player1_highlight;
//TODO:uguyfygukh
                try {
                    final Begin begin = (Begin) dis.readObject();
                    String tossres;
                    if(begin.toss == 1){
                        tossres =  "You Won the Toss";
                        player.turn = true;
                        oppoPlayer.turn = false;
                    }
                    else{
                        player.turn = false;
                        oppoPlayer.turn = true;
                        tossres = "You Lost the Toss";
                    }
                    OPPOUSERNAME = begin.name;
                    final String tossres_final = tossres;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),tossres_final,Toast.LENGTH_LONG).show();
                            player1Tv.setText(OPPOUSERNAME+": 0");
                            player2Tv.setText(MYUSERNAME+": 0");

                        }
                    });

                    box = new Box(player.id,begin.box);
                    box.turn = player.turn;
                    makeHighlight(begin.box);

                    Begin begin1 = new Begin(1,1,MYUSERNAME);
                    dos.writeObject(begin1);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


/*                dis = new DataInputStream(socket.getInputStream());
                dos =  new DataOutputStream(socket.getOutputStream());
*/

            } catch (IOException e) {
                e.printStackTrace();
            }


            while (true){
                if(gameOverFlag){

                    break;
                }

                if(dis != null) {
                    try {
                        System.out.println("Client:Hi");
                        LanMove move = (LanMove) dis.readObject();
                        boolean isGameOver = move.gameOver;
                        final int parent = move.parent;
                        final int child = move.child;
                        player.turn = true;
                        oppoPlayer.turn = false;
                        int tempoppoplayerscore = oppoPlayer.score;
                        oppoPlayer.score = move.score;
                        if(tempoppoplayerscore < oppoPlayer.score){
                            if(SOUNDFLAG){
                                tempsuccessMediaPlayer.start();
                            }
                        }
                        System.out.println("Client:Hello");

                        box.setMove(parent, child, oppoPlayer.id, oppoPlayer.score);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Board[parent][child].setBackgroundResource(oppoPlayer.sign);
                                player1Tv.setText(OPPOUSERNAME + ": " + oppoPlayer.score);

                            }
                        });
                        cancelHighlight(parent,child);
                        makeHighlight(child);

                        if(isGameOver){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();

                                }
                            });
                            makeGameOver();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }


            }


            return null;
        }
    }

    class  ServerThread implements Runnable{
        ServerSocket serverSocket;
        boolean flag = false;
        public String MYUSERNAME = "Server";
        public String OPPOUSERNAME = "Client";
        ObjectInputStream dis;
        ObjectOutputStream dos;
        boolean firstflag = false;
        boolean gameOverFlag = false;
        //DataInputStream dis;
        //DataOutputStream dos;
        Socket socket = null;


        Player player;
        Player oppoPlayer;
        Box box;


        private void makeGameMove(int p, int c) {
            box.turn = player.turn;
            if(player.turn ){
                final int parent = p;
                final int child = c;
                if(box.makeMove(parent,child)){
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Board[parent][child].setBackgroundResource(player.sign);

                         }
                     });
                        player.score = box.playerScore;
                        player.turn = false;
                        oppoPlayer.turn = true;
                        sendMove(parent,child,player.score,box.gameOver);


                        final String score = player.score + "";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player1Tv.setText(MYUSERNAME+": "+score);

                        }
                    });
                        cancelHighlight(parent);
                        makeHighlight(child);

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
        public void makeGameOver(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

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
                    resulttv.setTextColor(getResources().getColor(R.color.colorBlack));
                    if(player.score > oppoPlayer.score)
                    { resulttv.setText("You Won");}
                    else if(player.score== oppoPlayer.score){
                        resulttv.setText("Tie");
                    }
                    else{
                        resulttv.setText("You Lost");
                    }
                    relativeLayout.addView(resulttv);
                    relativeLayout.setAlpha((float) 1.0);

                    addContentView(relativeLayout,rlp);
                new  Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        releaseMediaPlayer();
                        Intent intent = new Intent(LanGame.this,LANWIFI.class);
                        startActivity(intent);
                        finish();

                    }
                },3000);
                }
            });

            gameOverFlag = true;



        }
        public void sendMove(int parent, int child, int score, boolean gameOver){
            LanMove move = new LanMove(parent,child,score,gameOver);
            try {
                System.out.println(MYUSERNAME+"Hi");
                dos.writeObject(move);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void makeHighlight(final int i){
            int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];
            }
            final int[] arr = temp_arr.clone();
            if(player.turn){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player1indiTv.setBackgroundResource(activeIndicator);
                        player2indiTv.setBackgroundResource(deactiveIndicator);

                    }
                });

            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player2indiTv.setBackgroundResource(activeIndicator);
                        player1indiTv.setBackgroundResource(deactiveIndicator);

                    }
                });
            }
        /*if(player.turn)HIGHLIGHTCOLOR = player.highlight;
        else HIGHLIGHTCOLOR = oppoPlayer.highlight;
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tableLayout[i].setBackgroundResource(R.drawable.highlight);

               /* for(int j=1;j<=9;j++){
                    //if(arr[j] == 0){
                      //  Board[i][j].setBackgroundResource(R.drawable.selected_repeat);
                        Board[i][j].setAlpha((float)1.0);
                    //}
                    /*else if(arr[j] == 1 || arr[j] == 3){
                        Board[i][j].setBackgroundResource(R.drawable.player1_sign);

                    }
                    else if(arr[j] == 2 || arr[j] == 4){
                        Board[i][j].setBackgroundResource(R.drawable.player2_sign);

                    }
                    else{

                    }*//*

                }*/

            }
        });

        }
        private void cancelHighlight(final int i) {
            int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];

            }
            final int[] arr = temp_arr.clone();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableLayout[i].setBackgroundResource(R.drawable.tablebackground);

                    /*
                    for(int j=1;j<=9;j++){
                       // if(arr[j] == 0){
                         //   Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
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
            });

        }


        public void sendMessage(String msg){
            try {

                System.out.println("sent "+msg);
                if(dos != null) {
                    if(!firstflag){
                        /* Make Toss*/
                        Random random = new Random();
                        final int toss = random.nextInt(2);
                        int x = random.nextInt(9) + 1;
                        String tossres;
                        if(toss == 0){
                            tossres =  "You Won the Toss";
                            player.turn = true;
                            oppoPlayer.turn = false;
                        }
                        else{
                            player.turn = false;
                            oppoPlayer.turn = true;
                            tossres = "You Lost the Toss";
                        }
                        final String tossres_final = tossres;
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),tossres_final,Toast.LENGTH_LONG).show();
                                    player1Tv.setText(MYUSERNAME+": 0");
                                    player2Tv.setText(OPPOUSERNAME+": 0");
                                }
                            });
                        box = new Box(player.id,x);
                        box.turn = player.turn;
                        makeHighlight(x);
                        Begin begin = new Begin(toss,x,MYUSERNAME);
                        dos.writeObject(begin);

                    }
                    else{


                    }
                 //   Begin user = new User("Server :",msg);
                //    dos.writeObject(user);
                }
                /*if(msg!=null && msg.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    dis.close();
                    dos.close();
                    flag = true;

                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                serverSocket = new ServerSocket(5056);
                socket = serverSocket.accept();
                player = new Player();
                oppoPlayer = new Player();
                player.id = 1;
                oppoPlayer.id = 2;
                player.score = 0;
                oppoPlayer.score = 0;

                player.sign = R.drawable.player1_sign;
                player.highlight = R.drawable.player1_highlight;
                oppoPlayer.sign = R.drawable.player2_sign;
                oppoPlayer.highlight = R.drawable.player2_highlight;

                System.out.println("A new client is connected : " + socket);
                dos =  new ObjectOutputStream(socket.getOutputStream());
                dis = new ObjectInputStream(socket.getInputStream());
                sendMessage("Hi");

/*                dis = new DataInputStream(s.getInputStream());
                dos =  new DataOutputStream(s.getOutputStream());
*/
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true){
                System.out.println("Hiiiiiiiiiiiiiiiiiiiiiiiiiiii");

                if(gameOverFlag){
                    System.out.println("+++++++++++++++++");
                      break;
                }
              else{
                    if(dis != null) {
                        try {
                            LanMove move = (LanMove) dis.readObject();
                            boolean isGameOver = move.gameOver;
                            final int parent = move.parent;
                            final int child = move.child;
                            player.turn = true;
                            oppoPlayer.turn = false;
                            oppoPlayer.score = move.score;
                            box.setMove(parent, child, oppoPlayer.id, oppoPlayer.score);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Board[parent][child].setBackgroundResource(oppoPlayer.sign);
                                    player2Tv.setText(OPPOUSERNAME + ": " + oppoPlayer.score);

                                }
                            });
                            cancelHighlight(parent);
                            makeHighlight(child);

                            if(isGameOver){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();

                                    }
                                });

                                makeGameOver();
                                break;
                         }
                        } catch (IOException e) {
                            System.out.println(e);
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            System.out.println(e);

                            e.printStackTrace();
                        }
                    }

                }

            }
        }
    }

    class Client implements Runnable{
        Socket socket;
        InetAddress address;
        ObjectInputStream dis;
        ObjectOutputStream dos;
        public String MYUSERNAME = "Client";
        public String OPPOUSERNAME = "Server";
        boolean gameOverFlag = false;
        Player player;
        Player oppoPlayer;
        Box box;

        //DataInputStream dis;
        //DataOutputStream dos;
        boolean flag = false;
        Client(InetAddress address){
            this.address = address;

        }

        private void makeGameMove(int p, int c) {
            box.turn = player.turn;
            if(player.turn ){
                final int parent = p;
                final int child = c;
                if(box.makeMove(parent,child)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Board[parent][child].setBackgroundResource(player.sign);

                        }
                    });

                    player.score = box.playerScore;

                    player.turn = false;
                    oppoPlayer.turn = true;

                    sendMove(parent,child,player.score,box.gameOver);


                    final String score = player.score + "";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player2Tv.setText(MYUSERNAME+": "+score);

                        }
                    });
                    cancelHighlight(parent);
                    makeHighlight(child);

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
        public void sendMove(int parent, int child, int score, boolean gameOver){
            LanMove move = new LanMove(parent,child,score,gameOver);
            try {
                dos.writeObject(move);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void makeHighlight(final int i){
            int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];
            }
            final int[] arr = temp_arr.clone();
            if(player.turn){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player2indiTv.setBackgroundResource(activeIndicator);
                        player1indiTv.setBackgroundResource(deactiveIndicator);

                    }
                });

            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player1indiTv.setBackgroundResource(activeIndicator);
                        player2indiTv.setBackgroundResource(deactiveIndicator);
                    }
                });
            }
        /*if(player.turn)HIGHLIGHTCOLOR = player.highlight;
        else HIGHLIGHTCOLOR = oppoPlayer.highlight;
        */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableLayout[i].setBackgroundResource(R.drawable.highlight);

                    /*
                    for(int j=1;j<=9;j++){
                        //if(arr[j] == 0){
                          //  Board[i][j].setBackgroundResource(R.drawable.selected_repeat);
                            Board[i][j].setAlpha((float)1.0);
                        //}
                        /*else if(arr[j] == 1 || arr[j] == 3){
                            Board[i][j].setBackgroundResource(R.drawable.player1_sign);

                        }
                        else if(arr[j] == 2 || arr[j] == 4){
                            Board[i][j].setBackgroundResource(R.drawable.player2_sign);

                        }
                        else{

                        }*//*

                    }*/

                }
            });

        }
        private void cancelHighlight(final int i) {
            int[] temp_arr = new int[10];
            for(int j=1;j<=9;j++){
                temp_arr[j] = box.Matrix[i][j];

            }
            final int[] arr = temp_arr.clone();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableLayout[i].setBackgroundResource(R.drawable.tablebackground);

                    /*
                    for(int j=1;j<=9;j++){
                        //if(arr[j] == 0){
                          //  Board[i][j].setBackgroundResource(R.drawable.unselected_repeat);
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
            });

        }
        public void makeGameOver(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

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
                    resulttv.setTextColor(getResources().getColor(R.color.colorBlack));
                    if(player.score > oppoPlayer.score)
                    { resulttv.setText("You Won");}
                    else if(player.score== oppoPlayer.score){
                        resulttv.setText("Tie");
                    }
                    else{
                        resulttv.setText("You Lost");
                    }
                    relativeLayout.addView(resulttv);
                    relativeLayout.setAlpha((float) 1.0);

                    addContentView(relativeLayout,rlp);
                    gameOverFlag = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            releaseMediaPlayer();
                            Intent intent = new Intent(LanGame.this,LANWIFI.class);
                            startActivity(intent);
                            finish();
                        }
                    },3000);
                }
            });




        }

        public  void sendmessage(String msg){
            try {
                System.out.println("sent "+msg);
                if(dos != null) {
                //    User user = new User("Client :",msg);
                  //  dos.writeObject(user);
                }

                if(msg.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    dis.close();
                    dos.close();
                    flag = true;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            System.out.println("Hello Client");
            try {
                socket = new Socket(address,5056);
                dos =  new ObjectOutputStream(socket.getOutputStream());
                dis = new ObjectInputStream(socket.getInputStream());
                player = new Player();
                oppoPlayer = new Player();
                player.id = 2;
                oppoPlayer.id = 1;
                player.score = 0;
                oppoPlayer.score = 0;

                player.sign = R.drawable.player2_sign;
                player.highlight = R.drawable.player2_highlight;
                oppoPlayer.sign = R.drawable.player1_sign;
                oppoPlayer.highlight = R.drawable.player1_highlight;
//TODO:uguyfygukh
                try {
                    final Begin begin = (Begin) dis.readObject();
                    String tossres;
                    if(begin.toss == 1){
                        tossres =  "You Won the Toss";
                        player.turn = true;
                        oppoPlayer.turn = false;
                    }
                    else{
                        player.turn = false;
                        oppoPlayer.turn = true;
                        tossres = "You Lost the Toss";
                    }
                    final String tossres_final = tossres;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),tossres_final,Toast.LENGTH_LONG).show();
                            player1Tv.setText(OPPOUSERNAME+": 0");
                            player2Tv.setText(MYUSERNAME+": 0");

                        }
                    });

                    box = new Box(player.id,begin.box);
                    box.turn = player.turn;
                    makeHighlight(begin.box);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


/*                dis = new DataInputStream(socket.getInputStream());
                dos =  new DataOutputStream(socket.getOutputStream());
*/

            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true){
                if(gameOverFlag){

                    break;
                }

                if(dis != null) {
                    try {
                        System.out.println("Client:Hi");
                        LanMove move = (LanMove) dis.readObject();
                        boolean isGameOver = move.gameOver;
                        final int parent = move.parent;
                        final int child = move.child;
                        player.turn = true;
                        oppoPlayer.turn = false;
                        oppoPlayer.score = move.score;

                        System.out.println("Client:Hello");

                        box.setMove(parent, child, oppoPlayer.id, oppoPlayer.score);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Board[parent][child].setBackgroundResource(oppoPlayer.sign);
                                player1Tv.setText(OPPOUSERNAME + ": " + oppoPlayer.score);

                            }
                        });
                        cancelHighlight(parent);
                        makeHighlight(child);

                        if(isGameOver){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();

                                }
                            });
                        makeGameOver();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }


            }

        }
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
                mediaPlayer = MediaPlayer.create(LanGame.this, R.raw.game_music);
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
        Toast.makeText(getApplicationContext(),"You can't go back",Toast.LENGTH_SHORT).show();
    }
}
