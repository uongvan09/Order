package com.rtsoftware.order.pesenter;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtsoftware.order.model.data.Order;

import java.util.ArrayList;

public class POrder {

    public POrder() {

    }

    public void addOrUpdateOrder(final IfAddOrderResult ifAddOrderResult, Order order) {
        FirebaseDatabase.getInstance().getReference("order").child(order.getOrderId())
                .setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ifAddOrderResult.addOrderSuccess();
                } else {
                    ifAddOrderResult.addOrderFailr("ERROR");
                }
            }
        });
    }

    public void getListOrder(final IfGetListOrderResult ifGetListOrderResult) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                FirebaseDatabase.getInstance().getReference("order")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Order> orders = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Order order = snapshot.getValue(Order.class);
                                    if (order != null) {
                                        if (order.getStatus() == 1 || order.getStatus() == 2)
                                            orders.add(order);
                                    }
                                }
                                ifGetListOrderResult.getListOrderSuccess(orders);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ifGetListOrderResult.getListOrderFailr("NOT_CHANGE");
                            }
                        });
            }
        };
        thread.start();
    }

    public void updateFoodOrder(String orderId, Order.FoodInOrder foodInOrder, final IfUpdateFoodOrder ifUpdateFoodOrder) {
        FirebaseDatabase.getInstance().getReference("order").child(orderId).child("listOrder").child(foodInOrder.getFoodId())
                .setValue(foodInOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ifUpdateFoodOrder.isSuccess();
                } else
                    ifUpdateFoodOrder.isFailt();
            }
        });
    }


    public void getOrderTable(final IfGetOrderTableResult ifGetOrderTableResult, final String tableId) {
        FirebaseDatabase.getInstance().getReference("order")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Order order = snapshot.getValue(Order.class);
                            if (order != null)
                                if (order.getStatus() == 3 && order.getTableId().equals(tableId)) {
                                    ifGetOrderTableResult.getOrderTableSucess(order);
                                    break;
                                }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ifGetOrderTableResult.getOrderTableFailt("NOT_CHANGE");
                    }
                });
    }

    public interface IfGetListOrderResult {
        void getListOrderSuccess(ArrayList<Order> orders);

        void getListOrderFailr(String error);
    }

    public interface IfGetOrderTableResult {
        void getOrderTableSucess(Order order);

        void getOrderTableFailt(String error);
    }

    public interface IfAddOrderResult {
        void addOrderSuccess();

        void addOrderFailr(String error);
    }

    public interface IfUpdateFoodOrder {
        void isSuccess();

        void isFailt();
    }
}
