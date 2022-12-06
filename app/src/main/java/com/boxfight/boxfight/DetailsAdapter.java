package com.boxfight.boxfight;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boxfight.boxfight.boxfight.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public  class  DetailsAdapter extends ArrayAdapter<InviteDetails>{

    private List<InviteDetails> detailsList;
    private  Context context;
    private String oppoStatus;
   // DatabaseReference reference;

    public DetailsAdapter(Context contextt, List<InviteDetails> resource) {
        super(contextt, R.layout.list_item,resource);
        this.context = contextt;
        detailsList = resource;
   //     reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return detailsList.size();
    }

    @Override
    public InviteDetails getItem(int i) {
        return detailsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item, viewGroup, false);

        TextView username = (TextView)rowView.findViewById(R.id.list_item_oppoUsername);
        final ImageView imageView = (ImageView)rowView.findViewById(R.id.list_item_image);
        final TextView timer = (TextView)rowView.findViewById(R.id.list_item_timer);

        final InviteDetails details = detailsList.get(i);

        username.setText(details.getOppoUserName());

        Glide.with(context).load(details.photoUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        new CountDownTimer(details.getCounter(),1000){

            @Override
            public void onTick(long l) {
                timer.setText(""+l/1000);
                details.setCounter(l);
            if(Online.REFRESH){
              Log.v("zzz","T-off");
               cancel();

            }
            }

            @Override
            public void onFinish() {
            timer.setText("TimeOut!");
            details.setCounter(0);
                //TODO : SET VISIBILITY GONE
                rowView.setVisibility(View.GONE);
                //TODO : GET DETAILS OPPOUSERID
                //TODO : GET DETAILS MYUSERID
                //TODO : GOTO INVITITION BOX AND SET STATUS NO

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Invitition").child(details.getMyUserId()).child(details.getOppoUserId()).child("response").setValue("N");
                reference.child("MyInvitition").child(details.getOppoUserId()).child(details.getMyUserId()).child("response").setValue("N");
                Log.v("zzz","Timer");
            }
        }.start();

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          if(!Online.OFFLINE)
          {
              //TODO : GET OPPO STATUS
              final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
              reference.child("Users").child(details.getOppoUserId()).child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                      if(dataSnapshot.exists()){
                          String result= dataSnapshot.getValue(String.class);
                          Log.v("DetailsAdapterloggg","Hi"+result);
                          oppoStatus = result;
                          String status = oppoStatus;
                          if(status!= null && !status.equals("N") && status.equals("T") ){
                              //TODO : MAKE STATUS FALSE
                              reference.child("Users").child(details.getMyUserId()).child("Status").setValue("F");
                              //TODO : GET DETAILS OPPOUSERNAME
                              //TODO : GET DETAILS MYUSERNAME
                              //TODO : GOTO INVITITION BOX AND SET STATUS YES
                              reference.child("Invitition").child(details.getMyUserId()).child(details.getOppoUserId()).child("response").setValue("Y");
                              reference.child("MyInvitition").child(details.getOppoUserId()).child(details.getMyUserId()).child("response").setValue("Y");
                              reference.child(details.getOppoUserId() + details.getMyUserId()).setValue(null);
                              //TODO : GOTO GAME AND SEND OPPOUSERNAME ,CREATOR(N) TO GAME
                              Intent intent = new Intent(getContext(),Game.class);
                              Bundle bundle = new Bundle();
                              bundle.putString("OPPOUSERID",details.getOppoUserId());
                              bundle.putString("MYUSERID",details.getMyUserId());
                              bundle.putString("CREATOR","N");
                              intent.putExtras(bundle);
                              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              detailsList.clear();
                              context.startActivity(intent);
                              ((Online)context).finish();
                          }

                      }
                      else{
                          oppoStatus = "N";
                      }
                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {
                      oppoStatus = "N";

                  }
              });
          }
            }
        });

        return rowView;
    }
/*
    private String getStatus(String oppoUserId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //reference.child("Users").child("Status").setValue("F");

        reference.child("Users").child(oppoUserId).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                   String result= dataSnapshot.getValue(String.class);
                   Log.v("DetailsAdapterloggg","Hi"+result);
                    oppoStatus = result;
                }
                else{
                    oppoStatus = "N";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                oppoStatus = "N";

            }
        });

        Log.v("DetailsAdapterloggg","Hii"+oppoStatus);

        return  oppoStatus;
    }*/
}
/*
public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder>{
    private List<InviteDetails> detailsList;

    public DetailsAdapter(List<InviteDetails> detailsList){
        this.detailsList = detailsList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
            final InviteDetails details = detailsList.get(position);
            holder.oppouserNametv.setText(details.getUserName());


        new CountDownTimer(details.getCounter(), 1000) {
            @Override
            public void onTick(long l) {

                holder.timertv.setText(""+l/1000);
                Log.v("lisss",""+l/1000);
                details.setCounter(l);
            }

            @Override
            public void onFinish() {
                holder.timertv.setText("Time out!");
                details.setCounter(0);
            }
        }.start();


    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView oppouserNametv,timertv;
        public long counter = -1;
        public MyViewHolder(View view) {
            super(view);
            oppouserNametv = (TextView)view.findViewById(R.id.list_item_oppoUsername);
            timertv = (TextView)view.findViewById(R.id.list_item_timer);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("lisssss",oppouserNametv.getText().toString());
                }
            });


        }
    }


}
*/

//   String status = getStatus(details.getOppoUserId());
               /* if(status!= null && !status.equals("N") && status.equals("T") ){
                    //TODO : MAKE STATUS FALSE
                    reference.child("Users").child(details.getMyUserId()).child("Status").setValue("F");
                    //TODO : GET DETAILS OPPOUSERNAME
                    //TODO : GET DETAILS MYUSERNAME
                    //TODO : GOTO INVITITION BOX AND SET STATUS YES
                    reference.child("Invitition").child(details.getMyUserId()).child(details.getOppoUserId()).child("response").setValue("Y");
                    //TODO : GOTO GAME AND SEND OPPOUSERNAME ,CREATOR(N) TO GAME
                    Intent intent = new Intent(getContext(),Game.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("OPPOUSERID",details.getOppoUserId());
                    bundle.putString("MYUSERID",details.getMyUserId());
                    bundle.putString("CREATOR","N");
                    intent.putExtras(bundle);
                    context.startActivity(intent);


                }
                else{
                    Log.v("DetailsAdapterabcd","status is F");
                }*/
