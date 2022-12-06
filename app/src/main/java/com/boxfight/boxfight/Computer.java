package com.boxfight.boxfight;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SHIVAM VYAS
 */
public class Computer {
    int next = -1;
    int pid = 1;
    int opid = 2;
    int Depth = 0;
    boolean stopFlag = false;

    public boolean isMoveLeft(int[][] board,int p){
        for(int i=1;i<=9;i++){
            if(board[p][i] == 0)return true;
        }
        return false;
    }
    public long evalute(int[][] board,int parent,int child,int id){
        boolean[] checkPoints = getCheckPoints(board,id,parent);
        long score = 0;
        if(checkPoints[0]){score++;}
        if(checkPoints[1]){score+= 2;}
        if(checkPoints[2]){score++;}
        if(checkPoints[3]){score++;}
        if(checkPoints[4]){score+=2;}
        if(checkPoints[5]){score++;}
        if(checkPoints[6]){score+=2;}
        if(checkPoints[7]){score+=2;}
        if(id == 4)score =  -score;
       /*  if(score == 1){
             for(int i=0;i<8;i++)
                 System.out.println(checkPoints[i]);
         }*/
        setCheckPoints(board,id,checkPoints,parent);
        return score;
    }
    public long minmax(int[][] board,int parent,int child,boolean isMax,int ps,int ops,int depth){
   /* if(depth == 1)
        System.out.println(child+"      "+(ps+ops));
        */
        if(stopFlag){return  0;}
        if(depth == Depth)
        {  return  0;
           }

        if(!isMoveLeft(board,child)){
            return -3;
            /*if(isMax){
                return  ops;
            }
            else{
                return  ps;
            }*/
        }
  /*      int bestMax = -1000;
        int bestMin = 1000;
        for(int i=1;i<=9;i++){
            if(board[child][i] == 0){
                int[] temp = new int[10];
                for(int k=1;k<=9;k++)temp[k] = board[child][k];
                int id;
                if(isMax){
                    id = 3;
                    board[child][i] = pid;
                }
                else{
                    id =4;
                    board[child][i] = opid;

                }
                int score = evalute(board,child,i,id);
                if(score != 0){
                    //     System.out.println(depth+" "+isMax + "  "+score+"   "+child+"   "+i);
                }
                int tempval = minmax(board,child,i,!isMax,ps+score,ops,depth+1);
                bestMax = Math.max(bestMax,tempval);
                bestMin = Math.min(bestMin, tempval);
                // board[child][i] = 0;
                for(int k=1;k<=9;k++)board[child][k] = temp[k];

            }
        }
        //System.out.println("( "+bestMax+"    "+bestMin+" )");

        if(bestMin<=0 && bestMax>=0)return bestMax + bestMin;
        else if(bestMin<0 && bestMax<0)return bestMin;
        else if(bestMin>0 && bestMax>0)return bestMax;
        else return 0;
*/

        if(isMax){
         long best = -1000000000;
         for(int i =1;i<=9;i++){
             if(board[child][i] == 0){
                 int[] temp = new int[10];
                 for(int k=1;k<=9;k++)temp[k] = board[child][k];

                 long tempscore = 0;

                 board[child][i] = opid;
                 tempscore = evalute(board,child,i,4);
                 tempscore = -tempscore;

                 for(int k=1;k<=9;k++) board[child][k] = temp[k];

                 board[child][i] = pid;

                 long score = evalute(board,child,i,3);
                 score += tempscore;

                 best = Math.max(best,
                            score + minmax(board,child,i,false,ps,ops,depth+1));
                //board[child][i] = 0;
                 for(int k=1;k<=9;k++)board[child][k] = temp[k];

             }
         }

        return best;
        }
        else{
            long best = 1000000000;
         for(int i =1;i<=9;i++){
             if(board[child][i] == 0){
                 int[] temp = new int[10];
                 for(int k=1;k<=9;k++)temp[k] = board[child][k];

                 long tempscore = 0;

                 board[child][i] = pid;
                 tempscore = evalute(board,child,i,3);
                 tempscore = -tempscore;

                 for(int k=1;k<=9;k++) board[child][k] = temp[k];


                 board[child][i] = opid;

                 long score = evalute(board,child,i,4);
                 score += tempscore;

                 best = Math.min(best,
                            score + minmax(board,child,i,true,ps,ops,depth+1));
                //board[child][i] = 0;
                 for(int k=1;k<=9;k++)board[child][k] = temp[k];

                 // System.out.println("best ----"+best);
             }
         }

        return best;
        }
    }
    public int play(int[][] board,int child,int ps,int ops){
        System.out.println("Hey I'm there----------");
        long bestval = -1000000000;
        int move = -1;
        long five = -1000000000;
        for(int i=1;i<=9;i++){
            if(board[child][i] == 0){
                int[] temp = new int[10];
                for(int k=1;k<=9;k++)temp[k] = board[child][k];


                long tempscore = 0;

                board[child][i] = opid;
                tempscore = evalute(board,child,i,4);
                tempscore = -tempscore;

                for(int k=1;k<=9;k++) board[child][k] = temp[k];

                board[child][i] = pid;
                long score = evalute(board,child,i,3);
                score += tempscore;

                long moveval = score + minmax(board,child,i,false,ps,ops,0);

                //board[child][i] = 0;
                for(int k=1;k<=9;k++) board[child][k] = temp[k];
                if(i == 5)five = moveval;
                if(moveval>bestval){
                    move = i;
                    bestval = moveval;
                }
                     System.out.println(i+"  "+moveval);

            }
        }
      if(five == bestval)move = 5;

        board[child][move] = pid;
        System.out.println(""+move+"  --  "+bestval);
        next = move;
        long score = evalute(board,child,move,3);
       /* for(int i=1;i<=9;i++)
        {
            System.out.println("MAT"+" "+i);
            for(int j=1;j<=9;j++)
            {
                System.out.print(board[i][j]+"      ");
           /* if(board[i][j] == 1 ||board[i][j] == 3 )
            {System.out.print("x  ");}
            else if(board[i][j] == 2 ||board[i][j] == 4 ){System.out.print("o  ");}
            else{System.out.print("-");}*//*
            if(j==3 || j==6)System.out.println("");
        }
            System.out.println("\n");


       }
*/

        return move;
    }
   /* public static void main(String[] args) {
        // TODO code application logic here
        BoxFightComputer b = new BoxFightComputer();

        int[][] board = new int[10][10];

        for(int i=1;i<=9;i++)
            for(int j=1;j<=9;j++)
                board[i][j] = 0;
        Scanner input = new Scanner(System.in);

        int parent = input.nextInt();

        while(true){
            int child = input.nextInt();

            board[parent][child] = 2;
            b.play(board,child,0,0);
            parent = b.next;
        for(int i=1;i<=9;i++)
        {
            for(int j=1;j<=9;j++)
        {
            System.out.print(board[i][j]+"      ");
           /* if(board[i][j] == 1 ||board[i][j] == 3 )
            {System.out.print("x  ");}
            else if(board[i][j] == 2 ||board[i][j] == 4 ){System.out.print("o  ");}
            else{System.out.print("-");}
            if(j==3 || j==6)System.out.println("");
        }
            System.out.println("\n");


       }
        *//*


        }

    }
*/
    boolean[] getCheckPoints(int[][] Matrix,int doneid, int i){
        int playerid = doneid - 2;
        boolean[] checkPoints = new boolean[8];
        //Rows ...
        if (    (Matrix[i][1] == playerid || Matrix[i][2] == playerid || Matrix[i][3] == playerid)&&
                (Matrix[i][1] == playerid ||  Matrix[i][1] == doneid) &&
                (Matrix[i][2] == playerid ||  Matrix[i][2] == doneid) &&
                (Matrix[i][3] == playerid ||  Matrix[i][3] == doneid)
                )
        {
            checkPoints[0] = true;
        }
        if (    (Matrix[i][4] == playerid || Matrix[i][5] == playerid || Matrix[i][6] == playerid)&&
                (Matrix[i][4] == playerid ||  Matrix[i][4] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][6] == playerid ||  Matrix[i][6] == doneid)
                )
        {
            checkPoints[1] = true;
        }

        if (    (Matrix[i][7] == playerid || Matrix[i][8] == playerid || Matrix[i][9] == playerid)&&
                (Matrix[i][7] == playerid ||  Matrix[i][7] == doneid) &&
                (Matrix[i][8] == playerid ||  Matrix[i][8] == doneid) &&
                (Matrix[i][9] == playerid ||  Matrix[i][9] == doneid)
                )
        {
            checkPoints[2] = true;
        }

        // Colums ...
        if (    (Matrix[i][1] == playerid || Matrix[i][4] == playerid || Matrix[i][7] == playerid)&&
                (Matrix[i][1] == playerid ||  Matrix[i][1] == doneid) &&
                (Matrix[i][4] == playerid ||  Matrix[i][4] == doneid) &&
                (Matrix[i][7] == playerid ||  Matrix[i][7] == doneid)
                )
        {
            checkPoints[3] = true;
        }

        if (    (Matrix[i][2] == playerid || Matrix[i][5] == playerid || Matrix[i][8] == playerid)&&
                (Matrix[i][2] == playerid ||  Matrix[i][2] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][8] == playerid ||  Matrix[i][8] == doneid)
                )
        {
            checkPoints[4] = true;
        }

        if (    (Matrix[i][3] == playerid || Matrix[i][6] == playerid || Matrix[i][9] == playerid)&&
                (Matrix[i][3] == playerid ||  Matrix[i][3] == doneid) &&
                (Matrix[i][6] == playerid ||  Matrix[i][6] == doneid) &&
                (Matrix[i][9] == playerid ||  Matrix[i][9] == doneid)
                )
        {
            checkPoints[5] = true;
        }

        //Diagonals ...
        if (    (Matrix[i][1] == playerid || Matrix[i][5] == playerid || Matrix[i][9] == playerid)&&
                (Matrix[i][1] == playerid ||  Matrix[i][1] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][9] == playerid ||  Matrix[i][9] == doneid)
                )
        {
            checkPoints[6] = true;
        }
        if (    (Matrix[i][3] == playerid || Matrix[i][5] == playerid || Matrix[i][7] == playerid)&&
                (Matrix[i][3] == playerid ||  Matrix[i][3] == doneid) &&
                (Matrix[i][5] == playerid ||  Matrix[i][5] == doneid) &&
                (Matrix[i][7] == playerid ||  Matrix[i][7] == doneid)
                )
        {
            checkPoints[7] = true;
        }
        return checkPoints;
    }
    void setCheckPoints(int[][] Matrix,int doneid,boolean[] checkPoints,int i){
        int playerid = doneid - 2;

        if(checkPoints[0]){Matrix[i][1] = doneid;Matrix[i][2] = doneid;Matrix[i][3] = doneid;}
        if(checkPoints[1]){Matrix[i][4] = doneid;Matrix[i][5] = doneid;Matrix[i][6] = doneid;}
        if(checkPoints[2]){Matrix[i][7] = doneid;Matrix[i][8] = doneid;Matrix[i][9] = doneid;}
        if(checkPoints[3]){Matrix[i][1] = doneid;Matrix[i][4] = doneid;Matrix[i][7] = doneid;}
        if(checkPoints[4]){Matrix[i][2] = doneid;Matrix[i][5] = doneid;Matrix[i][8] = doneid;}
        if(checkPoints[5]){Matrix[i][3] = doneid;Matrix[i][6] = doneid;Matrix[i][9] = doneid;}
        if(checkPoints[6]){Matrix[i][1] = doneid;Matrix[i][5] = doneid;Matrix[i][9] = doneid;}
        if(checkPoints[7]){Matrix[i][3] = doneid;Matrix[i][5] = doneid;Matrix[i][7] = doneid;}

    }


}
