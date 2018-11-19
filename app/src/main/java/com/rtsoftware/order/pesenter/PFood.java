package com.rtsoftware.order.pesenter;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtsoftware.order.model.data.Foods;

import java.util.ArrayList;

public class PFood {

    public PFood() {
    }

    public void getListFood(final IfGetFoodResult ifGetFoodResult) {
        FirebaseDatabase.getInstance().getReference("food")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Foods> foods = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Foods food = snapshot.getValue(Foods.class);
                            foods.add(food);
                        }
                        ifGetFoodResult.getListFoodSuccess(foods);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ifGetFoodResult.getListFoodFailt("NOT CHANGE");
                    }
                });
    }

    public interface IfGetFoodResult {
        void getListFoodSuccess(ArrayList<Foods> listFood);

        void getListFoodFailt(String error);
    }
}
