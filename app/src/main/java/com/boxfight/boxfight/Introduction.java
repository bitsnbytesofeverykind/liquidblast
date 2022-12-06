package com.boxfight.boxfight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfight.boxfight.boxfight.R;

public class Introduction extends Activity {

    TextView introTv;
    TextView[][] Board = new TextView[10][10];
    private int blue = R.drawable.player2_sign;
    private int pink = R.drawable.player1_sign;
    float hval = (float) 1.0;
    float cval = (float) 0.3;
    int[][] arr = new int[10][3];
    int[][] box = new int[10][10];
    int cnt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To get full screen on start
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_game_dashboard);
        ImageButton imageButton = (ImageButton) findViewById(R.id.activity_game_exit);
        introTv = (TextView) findViewById(R.id.activity_game_introTv);
        linearLayout.setVisibility(View.GONE);
        //imageButton.setVisibility(View.GONE);
        introTv.setVisibility(View.VISIBLE);
        imageButton.setImageResource(R.drawable.ic_close_black_24dp);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Introduction.this,Home.class);
                startActivity(intent);
                finish();

            }
        });
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                String str = "activity_game_btn" + i + "" + j;

                int btnid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
                Board[i][j] = (TextView) findViewById(btnid);
                final int parent = i;
                final int child = j;
                Board[i][j].setAlpha(cval);
                Board[i][j].setEnabled(false);
            }
        }
        Board[1][2].setBackgroundResource(blue);
        Board[1][3].setBackgroundResource(pink);
        Board[1][7].setBackgroundResource(blue);

        Board[2][1].setBackgroundResource(pink);
        Board[2][6].setBackgroundResource(pink);

        Board[3][2].setBackgroundResource(blue);
        Board[3][4].setBackgroundResource(pink);
        Board[3][7].setBackgroundResource(blue);
        Board[3][8].setBackgroundResource(blue);

        Board[4][1].setBackgroundResource(blue);
        Board[4][5].setBackgroundResource(blue);
        Board[4][7].setBackgroundResource(blue);

        Board[5][1].setBackgroundResource(blue);
        Board[5][4].setBackgroundResource(pink);
        Board[5][5].setBackgroundResource(pink);
        Board[5][7].setBackgroundResource(blue);

        Board[6][5].setBackgroundResource(blue);

        Board[7][1].setBackgroundResource(pink);
        Board[7][3].setBackgroundResource(pink);
        Board[7][4].setBackgroundResource(pink);
        Board[7][5].setBackgroundResource(pink);
        Board[7][9].setBackgroundResource(pink);

        Board[8][9].setBackgroundResource(pink);

        Board[9][3].setBackgroundResource(blue);
        Board[9][7].setBackgroundResource(blue);


        box[1][2] = 2;
        box[1][3] = 1;
        box[1][7] = 2;

        box[2][1] = 1;
        box[2][6] = 1;

        box[3][2] = 2;
        box[3][4] = 1;
        box[3][7] = 2;
        box[3][8] = 2;

        box[4][1] = 2;
        box[4][5] = 2;
        box[4][7] = 2;

        box[5][1] = 2;
        box[5][4] = 1;
        box[5][5] = 1;
        box[5][7] = 2;

        box[6][5] = 2;

        box[7][1] = 1;
        box[7][3] = 1;
        box[7][4] = 1;
        box[7][5] = 1;
        box[7][9] = 1;

        box[8][9] = 1;

        box[9][3] = 2;
        box[9][7] = 2;


        arr[1][0] = 6;
        arr[1][1] = 7;
        arr[1][2] = 1;

        arr[2][0] = 6;
        arr[2][1] = 3;
        arr[2][2] = 7;

        arr[3][0] = 9;
        arr[3][1] = 5;
        arr[3][2] = 3;

        arr[4][0] = 6;
        arr[4][1] = 8;
        arr[4][2] = 5;

        arr[5][0] = 3;
        arr[5][1] = 5;
        arr[5][2] = 8;

        arr[6][0] = 3;
        arr[6][1] = 3;
        arr[6][2] = 5;

        arr[7][0] = 1;
        arr[7][1] = 4;
        arr[7][2] = 3;

        arr[8][0] = 3;
        arr[8][1] = 6;
        arr[8][2] = 4;

        arr[9][0] = 2;
        arr[9][1] = 3;
        arr[9][2] = 6;

           step_0();

    }

    private void step_0() {
    introTv.setText("We are in the middle of the Game");
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            introTv.setText("It's your turn now");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    introTv.setText("You can choose any of the Highlighted box.\n*But choose middle right from highlighted box.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            step_1(1);
                        }
                    },5000);

                }
            },2000);
        }
    },3000);


    }

    private void step_1(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();

        introTv.setText("Your opponent I mean my move will be dependent on your previous move.");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                introTv.setText("You choose middle right(6th box covered by black border) which is highlighted. \n You see?");

                final int j = arr[1][0];
                final int k = arr[1][1];

                Board[i][j].setEnabled(true);
                highlight(i);
                Board[i][j].setBackgroundResource(R.drawable.highlight_move);

                Board[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                introTv.setText("you selected small Box number 6 hence I can play my move in Big Box number 6");
                        box[i][j] = 1;
                        Board[i][j].setEnabled(false);
                        Board[i][j].setBackgroundResource(pink);
                        cancelHighlight(i);
                        Board[i][j].setAlpha(hval);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                highlight(j);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 0)
                                        Board[j][x].setBackgroundResource(R.drawable.highlight);
                                }
                                introTv.setText("As you can see Big Box number 6 is Highlighted");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[i][j].setAlpha(cval);
                                        for (int x = 1; x <= 9; x++) {
                                            if (box[j][x] == 1) {
                                                Board[j][x].setBackgroundResource(pink);
                                            } else if (box[j][x] == 2) {
                                                Board[j][x].setBackgroundResource(blue);

                                            } else {
                                                Board[j][x].setBackgroundResource(R.drawable.temp);

                                            }

                                        }
                                        Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();
                                        introTv.setText("Now I'm going to select box 7 (inside 6th Big box ).");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Board[j][k].setBackgroundResource(blue);
                                                box[j][k] = 2;
                                                cancelHighlight(j);
                                                Board[j][k].setAlpha(hval);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        highlight(k);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 0)
                                                                Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                        }
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Board[j][k].setAlpha(cval);
                                                                for (int x = 1; x <= 9; x++) {
                                                                    if (box[k][x] == 1) {
                                                                        Board[k][x].setBackgroundResource(pink);
                                                                    } else if (box[k][x] == 2) {
                                                                        Board[k][x].setBackgroundResource(blue);

                                                                    } else {
                                                                        Board[k][x].setBackgroundResource(R.drawable.temp);

                                                                    }

                                                                }
                                                                step_2(k);

                                                            }
                                                        }, 2000);
                                                    }
                                                }, 1000);
                                            }
                                        }, 8000);

                                    }
                                }, 8000);
                            }
                        }, 8000);


                    }
                });



            }
        },8000);

    }

    private void step_2(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();
        introTv.setText("Now you know basic moves\n Now  click on given  6th box(inside Big box 7)");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final int j = arr[2][0];
                final int k = arr[2][1];

                Board[i][j].setEnabled(true);
                highlight(i);
                Board[i][j].setBackgroundResource(R.drawable.highlight_move);

                Board[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        box[i][j] = 1;
                        Board[i][j].setEnabled(false);
                        introTv.setText("Now you made two points by making three in a row and three in a column.you can also make point by making three in diagonal too.(like tic tac toe)");
                        Board[i][j].setBackgroundResource(pink);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[7][4].setBackgroundResource(R.drawable.pairhighlight);
                                Board[7][5].setBackgroundResource(R.drawable.pairhighlight);
                                Board[7][6].setBackgroundResource(R.drawable.pairhighlight);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[7][4].setBackgroundResource(pink);
                                        Board[7][5].setBackgroundResource(pink);

                                        Board[7][3].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[7][9].setBackgroundResource(R.drawable.pairhighlight);


                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Board[7][3].setBackgroundResource(pink);
                                                Board[7][9].setBackgroundResource(pink);

                                                Board[i][j].setBackgroundResource(pink);
                                                cancelHighlight(i);
                                                Board[i][j].setAlpha(hval);
                                                introTv.setText("Now play Game like this for some moves for practice.");
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        highlight(j);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[j][x] == 0)
                                                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                                                        }

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Board[i][j].setAlpha(cval);
                                                                for (int x = 1; x <= 9; x++) {
                                                                    if (box[j][x] == 1) {
                                                                        Board[j][x].setBackgroundResource(pink);
                                                                    } else if (box[j][x] == 2) {
                                                                        Board[j][x].setBackgroundResource(blue);

                                                                    } else {
                                                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                                                    }

                                                                }
                                                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Board[6][3].setBackgroundResource(R.drawable.pairhighlight);
                                                                        Board[6][5].setBackgroundResource(R.drawable.pairhighlight);
                                                                        Board[6][7].setBackgroundResource(R.drawable.pairhighlight);

                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Board[6][5].setBackgroundResource(blue);
                                                                                Board[6][7].setBackgroundResource(blue);

                                                                                Board[j][k].setBackgroundResource(blue);
                                                                                box[j][k] = 2;
                                                                                cancelHighlight(j);
                                                                                Board[j][k].setAlpha(hval);

                                                                                new Handler().postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        highlight(k);
                                                                                        for (int x = 1; x <= 9; x++) {
                                                                                            if (box[k][x] == 0)
                                                                                                Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                                                        }
                                                                                        new Handler().postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Board[j][k].setAlpha(cval);
                                                                                                for (int x = 1; x <= 9; x++) {
                                                                                                    if (box[k][x] == 1) {
                                                                                                        Board[k][x].setBackgroundResource(pink);
                                                                                                    } else if (box[k][x] == 2) {
                                                                                                        Board[k][x].setBackgroundResource(blue);

                                                                                                    } else {
                                                                                                        Board[k][x].setBackgroundResource(R.drawable.temp);

                                                                                                    }

                                                                                                }
                                                                                                step_3(k);

                                                                                            }
                                                                                        }, 2000);
                                                                                    }
                                                                                }, 1000);
                                                                            }
                                                                        }, 2000);

                                                                    }
                                                                },1000);


                                                            }
                                                        }, 2000);
                                                    }
                                                }, 5000);



                                            }
                                        },1000);

                                    }
                                },1000);

                            }
                        },15000);



                    }
                });



            }
        },5000);

    }

    private void step_3(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();
        final int j = arr[3][0];
        final int k = arr[3][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);
                Board[i][j].setBackgroundResource(pink);
                cancelHighlight(i);
                Board[i][j].setAlpha(hval);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        highlight(j);
                        for (int x = 1; x <= 9; x++) {
                            if (box[j][x] == 0)
                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[i][j].setAlpha(cval);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 1) {
                                        Board[j][x].setBackgroundResource(pink);
                                    } else if (box[j][x] == 2) {
                                        Board[j][x].setBackgroundResource(blue);

                                    } else {
                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                    }

                                }
                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[9][3].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[9][5].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[9][7].setBackgroundResource(R.drawable.pairhighlight);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Board[9][3].setBackgroundResource(blue);
                                                Board[9][7].setBackgroundResource(blue);

                                                Board[j][k].setBackgroundResource(blue);
                                                box[j][k] = 2;
                                                cancelHighlight(j);
                                                Board[j][k].setAlpha(hval);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        highlight(k);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 0)
                                                                Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                        }
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Board[j][k].setAlpha(cval);
                                                                for (int x = 1; x <= 9; x++) {
                                                                    if (box[k][x] == 1) {
                                                                        Board[k][x].setBackgroundResource(pink);
                                                                    } else if (box[k][x] == 2) {
                                                                        Board[k][x].setBackgroundResource(blue);

                                                                    } else {
                                                                        Board[k][x].setBackgroundResource(R.drawable.temp);

                                                                    }

                                                                }
                                                                step_4(k);

                                                            }
                                                        }, 2000);
                                                    }
                                                }, 1000);
                                            }
                                        }, 2000);

                                    }
                                },1000);
                            }
                        }, 2000);
                    }
                }, 1000);


            }
        });



    }

    private void step_4(final int i) {

        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();

        final int j = arr[4][0];
        final int k = arr[4][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);

                Board[5][4].setBackgroundResource(R.drawable.pairhighlight);
                Board[5][5].setBackgroundResource(R.drawable.pairhighlight);
                Board[5][6].setBackgroundResource(R.drawable.pairhighlight);

               
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[5][4].setBackgroundResource(pink);
                                Board[5][5].setBackgroundResource(pink);

                                Board[i][j].setBackgroundResource(pink);
                                cancelHighlight(i);
                                Board[i][j].setAlpha(hval);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        highlight(j);
                                        for (int x = 1; x <= 9; x++) {
                                            if (box[j][x] == 0)
                                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                                        }

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Board[i][j].setAlpha(cval);
                                                for (int x = 1; x <= 9; x++) {
                                                    if (box[j][x] == 1) {
                                                        Board[j][x].setBackgroundResource(pink);
                                                    } else if (box[j][x] == 2) {
                                                        Board[j][x].setBackgroundResource(blue);

                                                    } else {
                                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                                    }

                                                }
                                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();
                                                                Board[j][k].setBackgroundResource(blue);
                                                                box[j][k] = 2;
                                                                cancelHighlight(j);
                                                                Board[j][k].setAlpha(hval);

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        highlight(k);
                                                                        for (int x = 1; x <= 9; x++) {
                                                                            if (box[k][x] == 0)
                                                                                Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                                        }
                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Board[j][k].setAlpha(cval);
                                                                                for (int x = 1; x <= 9; x++) {
                                                                                    if (box[k][x] == 1) {
                                                                                        Board[k][x].setBackgroundResource(pink);
                                                                                    } else if (box[k][x] == 2) {
                                                                                        Board[k][x].setBackgroundResource(blue);

                                                                                    } else {
                                                                                        Board[k][x].setBackgroundResource(R.drawable.temp);

                                                                                    }

                                                                                }
                                                                                step_5(k);

                                                                            }
                                                                        }, 2000);
                                                                    }
                                                                }, 1000);
                                               

                                            }
                                        }, 2000);
                                    }
                                }, 1000);




                    }
                },1000);


            }
        });





    }

    private void step_5(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();
        final int j = arr[5][0];
        final int k = arr[5][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);
                Board[i][j].setBackgroundResource(pink);
                cancelHighlight(i);
                Board[i][j].setAlpha(hval);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        highlight(j);
                        for (int x = 1; x <= 9; x++) {
                            if (box[j][x] == 0)
                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[i][j].setAlpha(cval);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 1) {
                                        Board[j][x].setBackgroundResource(pink);
                                    } else if (box[j][x] == 2) {
                                        Board[j][x].setBackgroundResource(blue);

                                    } else {
                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                    }

                                }
                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[3][2].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[3][5].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[3][8].setBackgroundResource(R.drawable.pairhighlight);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Board[3][2].setBackgroundResource(blue);
                                                Board[3][8].setBackgroundResource(blue);

                                                Board[j][k].setBackgroundResource(blue);
                                                box[j][k] = 2;
                                                cancelHighlight(j);
                                                Board[j][k].setAlpha(hval);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        highlight(k);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 0)
                                                                Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                        }
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Board[j][k].setAlpha(cval);
                                                                for (int x = 1; x <= 9; x++) {
                                                                    if (box[k][x] == 1) {
                                                                        Board[k][x].setBackgroundResource(pink);
                                                                    } else if (box[k][x] == 2) {
                                                                        Board[k][x].setBackgroundResource(blue);

                                                                    } else {
                                                                        Board[k][x].setBackgroundResource(R.drawable.temp);

                                                                    }

                                                                }
                                                                step_6(k);

                                                            }
                                                        }, 2000);
                                                    }
                                                }, 1000);
                                            }
                                        }, 2000);

                                    }
                                },1000);
                            }
                        }, 2000);
                    }
                }, 1000);


            }
        });





    }

    private void step_6(final int i) {

        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();
        final int j = arr[6][0];
        final int k = arr[6][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);
                Board[i][j].setBackgroundResource(pink);
                cancelHighlight(i);
                Board[i][j].setAlpha(hval);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        highlight(j);
                        for (int x = 1; x <= 9; x++) {
                            if (box[j][x] == 0)
                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[i][j].setAlpha(cval);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 1) {
                                        Board[j][x].setBackgroundResource(pink);
                                    } else if (box[j][x] == 2) {
                                        Board[j][x].setBackgroundResource(blue);

                                    } else {
                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                    }

                                }
                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[3][3].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[3][5].setBackgroundResource(R.drawable.pairhighlight);
                                        Board[3][7].setBackgroundResource(R.drawable.pairhighlight);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Board[3][5].setBackgroundResource(blue);
                                                Board[3][7].setBackgroundResource(blue);

                                                Board[j][k].setBackgroundResource(blue);
                                                box[j][k] = 2;
                                                cancelHighlight(j);
                                                Board[j][k].setAlpha(hval);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        highlight(k);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 0)
                                                                Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                        }
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Board[j][k].setAlpha(cval);
                                                                for (int x = 1; x <= 9; x++) {
                                                                    if (box[k][x] == 1) {
                                                                        Board[k][x].setBackgroundResource(pink);
                                                                    } else if (box[k][x] == 2) {
                                                                        Board[k][x].setBackgroundResource(blue);

                                                                    } else {
                                                                        Board[k][x].setBackgroundResource(R.drawable.temp);

                                                                    }

                                                                }
                                                                step_7(k);

                                                            }
                                                        }, 2000);
                                                    }
                                                }, 1000);
                                            }
                                        }, 2000);

                                    }
                                },1000);
                            }
                        }, 2000);
                    }
                }, 1000);


            }
        });


    }

    private void step_7(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();

        final int j = arr[7][0];
        final int k = arr[7][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);
                Board[i][j].setBackgroundResource(pink);
                cancelHighlight(i);
                Board[i][j].setAlpha(hval);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        highlight(j);
                        for (int x = 1; x <= 9; x++) {
                            if (box[j][x] == 0)
                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[i][j].setAlpha(cval);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 1) {
                                        Board[j][x].setBackgroundResource(pink);
                                    } else if (box[j][x] == 2) {
                                        Board[j][x].setBackgroundResource(blue);

                                    } else {
                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                    }

                                }
                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[j][k].setBackgroundResource(blue);
                                        box[j][k] = 2;
                                        cancelHighlight(j);
                                        Board[j][k].setAlpha(hval);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                highlight(k);
                                                for (int x = 1; x <= 9; x++) {
                                                    if (box[k][x] == 0)
                                                        Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Board[j][k].setAlpha(cval);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 1) {
                                                                Board[k][x].setBackgroundResource(pink);
                                                            } else if (box[k][x] == 2) {
                                                                Board[k][x].setBackgroundResource(blue);

                                                            } else {
                                                                Board[k][x].setBackgroundResource(R.drawable.temp);

                                                            }

                                                        }
                                                        step_8(k);

                                                    }
                                                }, 2000);
                                            }
                                        }, 1000);
                                    }
                                }, 2000);

                            }
                        }, 2000);
                    }
                }, 1000);


            }
        });



    }

    private void step_8(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();

        final int j = arr[8][0];
        final int k = arr[8][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);
                Board[i][j].setBackgroundResource(pink);
                cancelHighlight(i);
                Board[i][j].setAlpha(hval);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        highlight(j);
                        for (int x = 1; x <= 9; x++) {
                            if (box[j][x] == 0)
                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[i][j].setAlpha(cval);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 1) {
                                        Board[j][x].setBackgroundResource(pink);
                                    } else if (box[j][x] == 2) {
                                        Board[j][x].setBackgroundResource(blue);

                                    } else {
                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                    }

                                }
                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[j][k].setBackgroundResource(blue);
                                        box[j][k] = 2;
                                        cancelHighlight(j);
                                        Board[j][k].setAlpha(hval);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                highlight(k);
                                                for (int x = 1; x <= 9; x++) {
                                                    if (box[k][x] == 0)
                                                        Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Board[j][k].setAlpha(cval);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 1) {
                                                                Board[k][x].setBackgroundResource(pink);
                                                            } else if (box[k][x] == 2) {
                                                                Board[k][x].setBackgroundResource(blue);

                                                            } else {
                                                                Board[k][x].setBackgroundResource(R.drawable.temp);

                                                            }

                                                        }
                                                        step_9(k);

                                                    }
                                                }, 2000);
                                            }
                                        }, 1000);
                                    }
                                }, 2000);

                            }
                        }, 2000);
                    }
                }, 1000);


            }
        });


    }

    private void step_9(final int i) {
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();

        final int j = arr[9][0];
        final int k = arr[9][1];

        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setBackgroundResource(R.drawable.highlight_move);

        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                box[i][j] = 1;
                Board[i][j].setEnabled(false);
                Board[i][j].setBackgroundResource(pink);
                cancelHighlight(i);
                Board[i][j].setAlpha(hval);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        highlight(j);
                        for (int x = 1; x <= 9; x++) {
                            if (box[j][x] == 0)
                                Board[j][x].setBackgroundResource(R.drawable.highlight);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Board[i][j].setAlpha(cval);
                                for (int x = 1; x <= 9; x++) {
                                    if (box[j][x] == 1) {
                                        Board[j][x].setBackgroundResource(pink);
                                    } else if (box[j][x] == 2) {
                                        Board[j][x].setBackgroundResource(blue);

                                    } else {
                                        Board[j][x].setBackgroundResource(R.drawable.temp);

                                    }

                                }
                                Toast.makeText(getApplicationContext(), "My Turn", Toast.LENGTH_SHORT).show();
                                introTv.setText("Now I will select box 3 (inside Big box 2)\n But Big box 3 is full you will not able to make move.");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Board[j][k].setBackgroundResource(blue);
                                        box[j][k] = 2;
                                        cancelHighlight(j);
                                        Board[j][k].setAlpha(hval);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                highlight(k);
                                                for (int x = 1; x <= 9; x++) {
                                                    if (box[k][x] == 0)
                                                        Board[k][x].setBackgroundResource(R.drawable.highlight);
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Board[j][k].setAlpha(cval);
                                                        for (int x = 1; x <= 9; x++) {
                                                            if (box[k][x] == 1) {
                                                                Board[k][x].setBackgroundResource(pink);
                                                            } else if (box[k][x] == 2) {
                                                                Board[k][x].setBackgroundResource(blue);

                                                            } else {
                                                                Board[k][x].setBackgroundResource(R.drawable.temp);

                                                            }

                                                        }
                                                  //TODO :GAME OVER
                                                    introTv.setText("Hence Game Over");
                                                    }
                                                }, 2000);
                                            }
                                        }, 1000);
                                    }
                                }, 10000);

                            }
                        }, 2000);
                    }
                }, 1000);


            }
        });


    }

    private void cancelHighlight(int p) {
        for (int i = 1; i <= 9; i++) {
            Board[p][i].setAlpha(cval);
        }

    }

    private void highlight(int p) {
        for (int i = 1; i <= 9; i++) {
            Board[p][i].setAlpha(hval);
        }

    }
}


    /*private void step_1(final int i) {
    if(cnt>9){introTv.setText("Game Over");return;}
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();
        final int j = arr[cnt][0];
        final int k = arr[cnt][1];
    //    introTv.setText("You can select any highlighted boxes");
        //    introTv.setText("select box"+);
            Log.v("Move: ","("+i+" "+j+" )"+" "+k);

            Board[i][j].setAlpha(hval);
            Board[i][j].setEnabled(true);
            highlight(i);
            Board[i][j].setBackgroundResource(R.drawable.highlight);

            Board[i][j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO : IF PAIR THEN GREEN.

                    Board[i][j].setBackgroundResource(pink);
                    Board[i][j].setAlpha(cval);
                    Board[i][j].setEnabled(false);
                    cancelHighlight(i);

                    (new  Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Board[i][j].setAlpha(hval);
                            Board[i][j].setBackgroundResource(R.drawable.highlight);
                            highlight(j);

                            (new  Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Board[i][j].setAlpha(cval);
                                    Board[i][j].setBackgroundResource(pink);


                                    Toast.makeText(getApplicationContext(),"My Turn",Toast.LENGTH_SHORT).show();

                                    //            introTv.setText("Press "+j+" button in highlighted box");

                                    Board[j][k].setAlpha(hval);
                                    // highlight(j);


                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Board[j][k].setBackgroundResource(blue);
                                            Board[j][k].setAlpha(cval);
                                            cancelHighlight(j);
                                            (new  Handler()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Board[j][k].setAlpha(hval);
                                                    Board[j][k].setBackgroundResource(R.drawable.highlight);
                                                    highlight(k);
                                                    (new  Handler()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Board[j][k].setAlpha(cval);
                                                            Board[j][k].setBackgroundResource(blue);

                                                            cnt++;
                                                            step_1(k);

                                                        }
                                                    }, 2000);

                                                }
                                            }, 2000);


                                        }
                                    },3000);


                                }
                            }, 2000);

                        }
                    }, 2000);



                }
            });


    }
*/

/*

    private void step_2() {
        Board[7][6].setAlpha(hval);
        Board[7][6].setEnabled(true);

        Board[1][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Board[1][6].setBackgroundResource(pink);
                Board[1][6].setAlpha(cval);
                Board[1][6].setEnabled(false);
                Board[6][7].setAlpha(hval);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Board[6][7].setBackgroundResource(blue);
                        Board[6][7].setAlpha(cval);
                    }
                },2000);
            }
        });

    }




}
/*
*
* package com.boxfight.shivam.boxfight;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Introduction extends Activity  {

    TextView introTv;
    TextView[][] Board = new TextView[10][10];
    private  int blue = R.drawable.player2_sign;
    private int pink = R.drawable.player1_sign;
    float hval = (float) 1.0;
    float cval = (float) 0.3;
    int[][] arr = new int[10][3];
    int cnt = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_game);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.activity_game_dashboard);
        ImageButton imageButton = (ImageButton)findViewById(R.id.activity_game_exit);
        introTv = (TextView)findViewById(R.id.activity_game_introTv);
        linearLayout.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        introTv.setVisibility(View.VISIBLE);

        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                String str = "activity_game_btn"+i+""+j;

                int btnid = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
                Board[i][j] = (TextView) findViewById(btnid);
                final int parent = i;
                final int child  = j;
                Board[i][j].setAlpha(cval);
                Board[i][j].setEnabled(false);
            }
        }
        Board[1][2].setBackgroundResource(blue);
        Board[1][3].setBackgroundResource(pink);
        Board[1][7].setBackgroundResource(blue);

        Board[2][1].setBackgroundResource(pink);
        Board[2][6].setBackgroundResource(pink);

        Board[3][2].setBackgroundResource(blue);
        Board[3][4].setBackgroundResource(pink);
        Board[3][7].setBackgroundResource(blue);
        Board[3][8].setBackgroundResource(blue);

        Board[4][1].setBackgroundResource(blue);
        Board[4][5].setBackgroundResource(blue);
        Board[4][7].setBackgroundResource(blue);

        Board[5][1].setBackgroundResource(blue);
        Board[5][4].setBackgroundResource(pink);
        Board[5][5].setBackgroundResource(pink);
        Board[5][7].setBackgroundResource(blue);

        Board[6][5].setBackgroundResource(blue);

        Board[7][1].setBackgroundResource(pink);
        Board[7][3].setBackgroundResource(pink);
        Board[7][4].setBackgroundResource(pink);
        Board[7][5].setBackgroundResource(pink);
        Board[7][9].setBackgroundResource(pink);

        Board[8][9].setBackgroundResource(pink);

        Board[9][3].setBackgroundResource(blue);
        Board[9][7].setBackgroundResource(blue);
        arr[1][0] = 6;
        arr[1][1] = 7;
        arr[1][2] = 1;

        arr[2][0] = 6;
        arr[2][1] = 3;
        arr[2][2] = 7;

        arr[3][0] = 9;
        arr[3][1] = 5;
        arr[3][2] = 3;

        arr[4][0] = 6;
        arr[4][1] = 8;
        arr[4][2] = 5;

        arr[5][0] = 3;
        arr[5][1] = 5;
        arr[5][2] = 8;

        arr[6][0] = 3;
        arr[6][1] = 3;
        arr[6][2] = 5;

        arr[7][0] = 1;
        arr[7][1] = 4;
        arr[7][2] = 3;

        arr[8][0] = 3;
        arr[8][1] = 6;
        arr[8][2] = 4;

        arr[9][0] = 2;
        arr[9][1] = 3;
        arr[9][2] = 6;


        step_1(1);






    }

    private void step_1(final int i) {
    if(cnt>9){introTv.setText("Game Over");return;}
        Toast.makeText(getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT).show();
        final int j = arr[cnt][0];
        final int k = arr[cnt][1];
        introTv.setText("You can select any highlighted boxes");
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {

        }
    },2000);
        introTv.setText("Press button number "+j+" in highlighted box");
        Log.v("Move: ","("+i+" "+j+" )"+" "+k);

        Board[i][j].setAlpha(hval);
        Board[i][j].setEnabled(true);
        highlight(i);
        Board[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"My Turn",Toast.LENGTH_SHORT).show();

                introTv.setText("Press "+j+" button in highlighted box");
                Board[i][j].setBackgroundResource(pink);
                Board[i][j].setAlpha(cval);
                Board[i][j].setEnabled(false);
                cancelHighlight(i);

                Board[j][k].setAlpha(hval);
                highlight(j);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Board[j][k].setBackgroundResource(blue);
                        Board[j][k].setAlpha(cval);
                        cancelHighlight(j);

                        cnt++;
                        step_1(k);
         }
                },3000);
            }
        });

    }

    private void cancelHighlight(int p) {
        for(int i =1;i<=9;i++){
            Board[p][i].setAlpha(cval);
        }

    }

    private void highlight(int p) {
        for(int i =1;i<=9;i++){
            Board[p][i].setAlpha(hval);
        }

    }



    private void step_2() {
        Board[7][6].setAlpha(hval);
        Board[7][6].setEnabled(true);

        Board[1][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Board[1][6].setBackgroundResource(pink);
                Board[1][6].setAlpha(cval);
                Board[1][6].setEnabled(false);
                Board[6][7].setAlpha(hval);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Board[6][7].setBackgroundResource(blue);
                        Board[6][7].setAlpha(cval);
                    }
                },2000);
            }
        });

    }




}

*
*
*
*
*
*
*
* */