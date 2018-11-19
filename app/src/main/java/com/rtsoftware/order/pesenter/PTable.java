package com.rtsoftware.order.pesenter;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtsoftware.order.model.data.Table;

import java.util.ArrayList;

public class PTable {
    IfGetTableResult ifGetTableResult;

    public PTable() {
    }


    public void getListTable(final IfGetTableResult ifGetTableResult) {
        FirebaseDatabase.getInstance().getReference("table")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Table> tables = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Table table = snapshot.getValue(Table.class);
                            if (table != null) {
                                tables.add(table);
                            }
                        }
                        ifGetTableResult.getTableSuccess(tables);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ifGetTableResult.getTableFailt("NOT CHANGE");
                    }
                });
    }

    public interface IfGetTableResult {
        void getTableSuccess(ArrayList<Table> tables);

        void getTableFailt(String error);

    }
}
