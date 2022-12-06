package com.boxfight.boxfight;

import java.io.Serializable;

/**
 * Created by SHIVAM VYAS on 03-Nov-17.
 */
public class LanMove implements Serializable{
    public int parent;
    public int child;
    public int score;
    public boolean gameOver;
    LanMove(int p,int c,int s,boolean g){
        parent = p;
        child = c;
        score = s;
        gameOver = g;
    }
}
