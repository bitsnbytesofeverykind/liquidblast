package com.boxfight.boxfight;

import java.io.Serializable;

/**
 * Created by SHIVAM VYAS on 03-Nov-17.
 */
public class Begin implements Serializable {
    public int toss;
    public int box;
    public String name;
    Begin(int t,int b,String n){
        toss = t;
        box = b;
        name = n;
    }
}
