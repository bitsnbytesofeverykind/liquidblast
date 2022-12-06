package com.boxfight.boxfight;

/**
 * Created by SHIVAM VYAS on 14-Oct-17.
 */

public class Box {

    int nextBoxNumber = 1;
    boolean turn = false;
    int playerid;
    int playerScore = 0;
    int opponentScore;
    int doneid;
    boolean gameOver = false;
    int[][] Matrix = new int[10][10];
    Box(int pid,int nbox){
        playerid = pid;
        doneid = playerid + 2;
        nextBoxNumber = nbox;
    }
    void reset(){
        for(int i=1;i<=9;i++)
            for(int j=1;j<=9;j++)
                Matrix[i][j] = 0;

        nextBoxNumber = 1;
        turn = false;
        playerScore = 0;
        gameOver = false;
    }
    void setMove(int parent,int child,int id,int score){
        Matrix[parent][child] = id;
        opponentScore = score;
        nextBoxNumber = child;
    }

    boolean makeMove(int parent,int child){
        if(isGameOver(parent)){
            gameOver = true;
            return true;
        }
        if(!isValidMove(parent,child))return false;

        Matrix[parent][child] = playerid;
        updatePlayerScore(parent,child);
        nextBoxNumber = child;
        if(isGameOver(child)){
            gameOver = true;
        }
        return true;
    }
    void updatePlayerScore(int i,int j){
        boolean[] checkPoints = getCheckPoints(i);
        setCheckPoints(checkPoints,i);
        turn = false;

    }

    boolean[] getCheckPoints(int i){
        boolean[] checkPoints = new boolean[8];
        //Rows ...
        if (    (Matrix[i][1] == playerid || Matrix[i][2] == playerid || Matrix[i][3] == playerid)&&
                (Matrix[i][1] == playerid ||  Matrix[i][1] == doneid) &&
                (Matrix[i][2] == playerid ||  Matrix[i][2] == doneid) &&
                (Matrix[i][3] == playerid ||  Matrix[i][3] == doneid)
                )
        {
            checkPoints[0] = true;
            playerScore++;
        }
        if (    (Matrix[i][4] == playerid || Matrix[i][5] == playerid || Matrix[i][6] == playerid)&&
                (Matrix[i][4] == playerid ||  Matrix[i][4] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][6] == playerid ||  Matrix[i][6] == doneid)
                )
        {
            checkPoints[1] = true;
            playerScore++;
        }

        if (    (Matrix[i][7] == playerid || Matrix[i][8] == playerid || Matrix[i][9] == playerid)&&
                (Matrix[i][7] == playerid ||  Matrix[i][7] == doneid) &&
                (Matrix[i][8] == playerid ||  Matrix[i][8] == doneid) &&
                (Matrix[i][9] == playerid ||  Matrix[i][9] == doneid)
                )
        {
            checkPoints[2] = true;
            playerScore++;
        }

        // Colums ...
        if (    (Matrix[i][1] == playerid || Matrix[i][4] == playerid || Matrix[i][7] == playerid)&&
                (Matrix[i][1] == playerid ||  Matrix[i][1] == doneid) &&
                (Matrix[i][4] == playerid ||  Matrix[i][4] == doneid) &&
                (Matrix[i][7] == playerid ||  Matrix[i][7] == doneid)
                )
        {
            checkPoints[3] = true;
            playerScore++;
        }

        if (    (Matrix[i][2] == playerid || Matrix[i][5] == playerid || Matrix[i][8] == playerid)&&
                (Matrix[i][2] == playerid ||  Matrix[i][2] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][8] == playerid ||  Matrix[i][8] == doneid)
                )
        {
            checkPoints[4] = true;
            playerScore++;
        }

        if (    (Matrix[i][3] == playerid || Matrix[i][6] == playerid || Matrix[i][9] == playerid)&&
                (Matrix[i][3] == playerid ||  Matrix[i][3] == doneid) &&
                (Matrix[i][6] == playerid ||  Matrix[i][6] == doneid) &&
                (Matrix[i][9] == playerid ||  Matrix[i][9] == doneid)
                )
        {
            checkPoints[5] = true;
            playerScore++;
        }

        //Diagonals ...
        if (    (Matrix[i][1] == playerid || Matrix[i][5] == playerid || Matrix[i][9] == playerid)&&
                (Matrix[i][1] == playerid ||  Matrix[i][1] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][9] == playerid ||  Matrix[i][9] == doneid)
                )
        {
            checkPoints[6] = true;
            playerScore++;
        }
        if (    (Matrix[i][3] == playerid || Matrix[i][5] == playerid || Matrix[i][7] == playerid)&&
                (Matrix[i][3] == playerid ||  Matrix[i][3] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][7] == playerid ||  Matrix[i][7] == doneid)
                )
        {
            checkPoints[7] = true;
            playerScore++;
        }
        return checkPoints;
    }
    void setCheckPoints(boolean[] checkPoints,int i){
        if(checkPoints[0]){Matrix[i][1] = doneid;Matrix[i][2] = doneid;Matrix[i][3] = doneid;}
        if(checkPoints[1]){Matrix[i][4] = doneid;Matrix[i][5] = doneid;Matrix[i][6] = doneid;}
        if(checkPoints[2]){Matrix[i][7] = doneid;Matrix[i][8] = doneid;Matrix[i][9] = doneid;}
        if(checkPoints[3]){Matrix[i][1] = doneid;Matrix[i][4] = doneid;Matrix[i][7] = doneid;}
        if(checkPoints[4]){Matrix[i][2] = doneid;Matrix[i][5] = doneid;Matrix[i][8] = doneid;}
        if(checkPoints[5]){Matrix[i][3] = doneid;Matrix[i][6] = doneid;Matrix[i][9] = doneid;}
        if(checkPoints[6]){Matrix[i][1] = doneid;Matrix[i][5] = doneid;Matrix[i][9] = doneid;}
        if(checkPoints[7]){Matrix[i][3] = doneid;Matrix[i][5] = doneid;Matrix[i][7] = doneid;}

    }
    boolean isValidBox(int parent) {
        if(parent == nextBoxNumber)return true;
        return false;
    }



    boolean isValidMove(int parent,int child){

        if(!turn) return  false;
        if(!isValidBox(parent)) return false;

        if(Matrix[parent][child] != 0)return false;


        return true;
    }

    boolean isGameOver(int i){
        boolean flag = true;
        for(int j=1;j<=9;j++){
            if(Matrix[i][j] == 0)flag = false;
        }
        return flag;
    }
}
