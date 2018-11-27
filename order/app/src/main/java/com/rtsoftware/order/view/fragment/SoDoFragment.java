package com.rtsoftware.order.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtsoftware.order.R;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.data.Table;
import com.rtsoftware.order.pesenter.POrder;
import com.rtsoftware.order.pesenter.PTable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SoDoFragment extends Fragment implements View.OnClickListener {
    Context context;
    View rootView;

    LinearLayout btnListBanTrong;
    LinearLayout btnListBanPhucVu;
    GridView grdListBan;


    ArrayList<Table> lstTable;
    ArrayList<Table> listShow;
    TableAdapter tableAdapter;

    boolean isChooseTable = false;
    int isTypeShow = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_map_table, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getIsChooseTable(boolean t) {
        isChooseTable = t;
        EventBus.getDefault().removeStickyEvent(t);
    }

    private void init() {
        btnListBanTrong = rootView.findViewById(R.id.btnListBanTrong);
        btnListBanPhucVu = rootView.findViewById(R.id.btnListBanPhucVu);
        grdListBan = rootView.findViewById(R.id.grdListTable);
        btnListBanTrong.setOnClickListener(this);
        btnListBanPhucVu.setOnClickListener(this);
        lstTable = new ArrayList<>();
        listShow = new ArrayList<>();
        grdListBan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final POrder pOrder = new POrder();
                if (listShow.get(i).getStatus() == 1) {
                    POrder.IfGetOrderTableResult ifGetOrderTableResult = new POrder.IfGetOrderTableResult() {
                        @Override
                        public void getOrderTableSucess(final Order order) {
                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_thanh_toan);
                            dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                            TextView tableName = dialog.findViewById(R.id.tvTableIdDialogTT);
                            tableName.setText("Bàn " + order.getTableId());
                            TextView tvOrderId = dialog.findViewById(R.id.tvOrderIdDialogTT);
                            tvOrderId.setText(order.getOrderId());
                            TextView tvStatus = dialog.findViewById(R.id.tvStatusDialogTT);
                            int a = order.getStatus();
                            if (a == 2) {
                                tvStatus.setText("Đang xử lý");
                            }
                            if (a == 3) {
                                tvStatus.setText("Đang phục vụ");
                            }
                            TextView tvTotalPrice = dialog.findViewById(R.id.tvTotalPriceDialogTT);
                            tvTotalPrice.setText(String.valueOf(order.getTotalPrice()));
                            TextView btnThanhToan = dialog.findViewById(R.id.btnTTDialogTT);
                            btnThanhToan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final DatabaseReference ta = FirebaseDatabase.getInstance().getReference("table").child(order.getTableId());
                                    ta.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Table table = dataSnapshot.getValue(Table.class);
                                            table.setStatus(2);
                                            ta.setValue(table);
                                            dialog.dismiss();
                                            getListTable();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    POrder.IfAddOrderResult ifAddOrderResult = new POrder.IfAddOrderResult() {
                                        @Override
                                        public void addOrderSuccess() {

                                        }

                                        @Override
                                        public void addOrderFailr(String error) {

                                        }
                                    };
                                    order.setStatus(4);
                                    pOrder.addOrUpdateOrder(ifAddOrderResult, order);
                                }
                            });
                            TextView btnExit = dialog.findViewById(R.id.btnExitDialogTT);
                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }

                        @Override
                        public void getOrderTableFailt(String error) {

                        }
                    };

                    pOrder.getOrderTable(ifGetOrderTableResult, listShow.get(i).getTableId());
                } else {
                    EventBus.getDefault().post(listShow.get(i));
                }
            }
        });
        getListTable();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnListBanTrong) {
            if (isTypeShow == 1) {
                listShow.clear();
                listShow.addAll(lstTable);
                tableAdapter.notifyDataSetChanged();
                btnListBanTrong.setBackgroundResource(R.color.colorButtonDefault);
                btnListBanPhucVu.setBackgroundResource(R.color.colorButtonDefault);
                isTypeShow = 0;
            } else {
                listShow.clear();
                for (Table table : lstTable) {
                    if (table.getStatus() == 2) {
                        listShow.add(table);
                    }
                }
                tableAdapter.notifyDataSetChanged();
                btnListBanTrong.setBackgroundResource(R.color.colorButtonChoose);
                btnListBanPhucVu.setBackgroundResource(R.color.colorButtonDefault);
                isTypeShow = 1;
            }
        }
        if (view.getId() == R.id.btnListBanPhucVu) {
            if (isTypeShow == 2) {
                listShow.clear();
                listShow.addAll(lstTable);
                tableAdapter.notifyDataSetChanged();
                btnListBanTrong.setBackgroundResource(R.color.colorButtonDefault);
                btnListBanPhucVu.setBackgroundResource(R.color.colorButtonDefault);
                isTypeShow = 0;
            } else {
                listShow.clear();
                for (Table table : lstTable) {
                    if (table.getStatus() == 1) {
                        listShow.add(table);
                    }
                }
                tableAdapter.notifyDataSetChanged();
                btnListBanTrong.setBackgroundResource(R.color.colorButtonDefault);
                btnListBanPhucVu.setBackgroundResource(R.color.colorButtonChoose);
                isTypeShow = 2;
            }
        }
    }

    private void getListTable() {
        PTable.IfGetTableResult ifGetTableResult = new PTable.IfGetTableResult() {
            @Override
            public void getTableSuccess(ArrayList<Table> tables) {
                lstTable = tables;
                listShow.clear();
                listShow.addAll(lstTable);
                showListTable();
            }

            @Override
            public void getTableFailt(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        };
        PTable pTable = new PTable();
        pTable.getListTable(ifGetTableResult);
    }

    private void showListTable() {
        tableAdapter = new TableAdapter(context, listShow);
        grdListBan.setAdapter(tableAdapter);
    }

    private class ViewHolder {
        private ImageView imgBgTable;
        private TextView tvNumberTable;

        public ViewHolder(View view) {
            this.imgBgTable = view.findViewById(R.id.imgBgTable);
            this.tvNumberTable = view.findViewById(R.id.tvNumberTable);
        }
    }

    private class TableAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Table> tables;

        public TableAdapter(Context context, List<Table> tables) {
            this.tables = tables;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return tables.size();
        }

        @Override
        public Object getItem(int i) {
            return tables.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int index, View view, ViewGroup viewGroup) {
            SoDoFragment.ViewHolder viewHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.item_table, viewGroup, false);
                viewHolder = new SoDoFragment.ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (SoDoFragment.ViewHolder) view.getTag();
            }
            if (tables.get(index).getStatus() == 2) {
                viewHolder.imgBgTable.setBackgroundResource(R.drawable.icon_table_null);
            } else {
                viewHolder.imgBgTable.setBackgroundResource(R.drawable.icon_table_full);

            }
            viewHolder.tvNumberTable.setText(tables.get(index).getTableId());
            return view;
        }
    }
}
