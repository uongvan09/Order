package com.rtsoftware.order.pesenter;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtsoftware.order.model.data.User;

public class PUsers {

    public  PUsers(){}

    public void  getUser(String userId, final IfGetUserRespond ifGetUserRespond){
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user= dataSnapshot.getValue(User.class);
                        if (user!=null){
                            ifGetUserRespond.onSuccess(user);
                        }else
                            ifGetUserRespond.onFailt();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ifGetUserRespond.onFailt();
                    }
                });
    }

    public  interface IfGetUserRespond{
        void onSuccess(User user);
        void  onFailt();
    }
}
