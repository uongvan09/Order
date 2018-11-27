package com.rtsoftware.order.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtsoftware.order.R;
import com.rtsoftware.order.model.Untill;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.tranfer.TranferTraMonToMain;
import com.rtsoftware.order.pesenter.POrder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TraMonFragment extends Fragment {
    Context context;
    View rootView;
    RecyclerView rcvTraMon;
    ArrayList<Order> lstOrder;
    ArrayList<Order> lstOrderShows;
    ArrayList<Order> lstOrderLastSearch;
    OrderAdapter orderAdapter;
    EditText edtInputSearch;
    ImageView btnClearnAll;

    private int lastTextLength = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = container.getContext();
        rootView = inflater.inflate(R.layout.framment_danh_sach_order, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        lstOrder = new ArrayList<>();
        lstOrderShows = new ArrayList<>();
        lstOrderLastSearch = new ArrayList<>();
        final Untill untill = new Untill();

        edtInputSearch = rootView.findViewById(R.id.edtSearchListOrder);
        edtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    btnClearnAll.setVisibility(View.VISIBLE);
                } else {
                    btnClearnAll.setVisibility(View.GONE);
                }
                ArrayList<Order> list = new ArrayList<>();
                boolean flagDelete = false;
                boolean isVietnames = false;

                if (editable.length() >= lastTextLength) {
                    if (lstOrderShows.isEmpty()) {
                        lstOrderShows.addAll(lstOrder);
                    }
                } else {
                    flagDelete = true;
                    lstOrderShows.clear();
                    if (lstOrderLastSearch.isEmpty()) {
                        lstOrderShows.addAll(lstOrder);
                    } else {
                        lstOrderShows.addAll(lstOrderLastSearch);
                    }
                }


                //check tieengs vietj
                if (!editable.toString().isEmpty()) {
                    String temp = untill.convertStringUTF8(editable.toString());
                    if (!temp.equals(editable.toString().toUpperCase())) {
                        isVietnames = true;
                    }
                }
                if (isVietnames) {
                    for (Order order : lstOrderShows) {
                        if (order.getTableId().toUpperCase().contains(editable.toString().toUpperCase()) ||
                                order.getOrderId().toUpperCase().contains(editable.toString().toUpperCase())) {
                            list.add(order);
                        }
                    }
                    for (Order order : lstOrderShows) {
                        String tableId = order.getTableId().toUpperCase();
                        String orderId = order.getOrderId().toUpperCase();
                        if (untill.convertStringUTF8(tableId)
                                .contains(untill.convertStringUTF8(editable.toString()).toUpperCase())
                                || untill.convertStringUTF8(orderId)
                                .contains(untill.convertStringUTF8(editable.toString()).toUpperCase())) {
                            boolean a = true;
                            for (Order order1 : list) {
                                if (order1.equals(order)) {
                                    a = false;
                                    break;
                                }
                            }
                            if (a) {
                                list.add(order);
                            }
                        }
                    }
                } else {
                    for (Order order : lstOrderShows) {
                        String tableId = order.getTableId().toUpperCase();
                        String orderId = order.getOrderId().toUpperCase();
                        if (untill.convertStringUTF8(tableId)
                                .contains(untill.convertStringUTF8(editable.toString()).toUpperCase())
                                || untill.convertStringUTF8(orderId)
                                .contains(untill.convertStringUTF8(editable.toString()).toUpperCase())) {
                            list.add(order);
                        }
                    }
                }
                if (!flagDelete) {
                    lstOrderLastSearch.clear();
                    lstOrderLastSearch.addAll(lstOrderShows);
                } else {
                    lstOrderLastSearch.clear();
                }
                lstOrderShows.clear();
                lstOrderShows.addAll(list);
                rcvTraMon.scrollToPosition(0);
                if (orderAdapter != null) {
                    orderAdapter.notifyDataSetChanged();
                }
                lastTextLength = editable.length();
            }
        });


        btnClearnAll = rootView.findViewById(R.id.btnClearnAllListOrder);
        btnClearnAll.setVisibility(View.GONE);
        btnClearnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtInputSearch.setText("");
                btnClearnAll.setVisibility(View.GONE);
                lstOrderShows.clear();
                lstOrderShows.addAll(lstOrder);
                if ((orderAdapter != null)) {
                    orderAdapter.notifyDataSetChanged();
                }
            }
        });
        rcvTraMon = rootView.findViewById(R.id.rcvTraMon);
        rcvTraMon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                com.rtsoftware.order.view.Untill.hideKeyboard(getActivity());
                return false;
            }
        });
        POrder.IfGetListOrderResult ifGetListOrderResult = new POrder.IfGetListOrderResult() {
            @Override
            public void getListOrderSuccess(ArrayList<Order> orders) {
                lstOrder = orders;
                showListOrder();
            }

            @Override
            public void getListOrderFailr(String error) {

            }
        };
        POrder pOrder = new POrder();
        pOrder.getListOrder(ifGetListOrderResult);
    }

    private void showListOrder() {
        sortListOrder();
        lstOrderShows.clear();
        lstOrderShows.addAll(lstOrder);
        orderAdapter = new OrderAdapter(context, lstOrderShows);
        rcvTraMon.setLayoutManager(new LinearLayoutManager(context));
        rcvTraMon.setAdapter(orderAdapter);
        int orientation = DividerItemDecoration.VERTICAL;
        DividerItemDecoration decoration = new DividerItemDecoration(context, orientation);
        rcvTraMon.addItemDecoration(decoration);
    }

    public void sortListOrder() {
        boolean a = true;
        while (a) {
            boolean b = true;
            for (int i = 1; i < lstOrder.size(); i++) {
                if (lstOrder.get(i).getTime() < lstOrder.get(i - 1).getTime()) {
                    long temp = lstOrder.get(i - 1).getTime();
                    lstOrder.get(i - 1).setTime(lstOrder.get(i).getTime());
                    lstOrder.get(i).setTime(temp);
                    b = false;
                }
            }
            a = !b;
        }
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderHolder> {
        private List<Order> orders;
        private LayoutInflater inflater;


        OrderAdapter(Context context, List<Order> orders) {
            this.orders = orders;
            inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public OrderHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.item_order, viewGroup, false);
            return new OrderHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final OrderHolder orderHolder, final int index) {
            orderHolder.tvOrderId.setText(orders.get(index).getOrderId());
            orderHolder.tvTableId.setText("Bàn " + orders.get(index).getTableId());
            Map<String, Order.FoodInOrder> foods = orders.get(index).getListOrder();
            String s = orders.get(index).getLstOrderId();
            String[] listOrder = null;
            int end = 0;
            int total = 0;
            if (s.contains("_>_")) {
                listOrder = s.split("_>_");
            }
            if (listOrder != null) {
                for (String s1 : listOrder) {
                    Order.FoodInOrder foodInOrder = foods.get(s1);
                    if (foodInOrder.getStatus() == 3) {
                        end++;
                    }
                }
                total = listOrder.length;
            } else {
                total = 1;
                if (foods.get(s).getStatus() == 3) {
                    end = 1;
                }
            }
            String count = end + "/ " + total;
            orderHolder.tvCountFood.setText(count);
            orderHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo send main
                    EventBus.getDefault().post(new TranferTraMonToMain(orders.get(index)));
                }
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

    }

    private class OrderHolder extends RecyclerView.ViewHolder {
        private TextView tvTableId;
        private TextView tvOrderId;
        private TextView tvCountFood;


        OrderHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderIdViewOrder);
            tvTableId = itemView.findViewById(R.id.tableNameItemOrder);
            tvCountFood = itemView.findViewById(R.id.tvCountViewOrder);
        }
    }
}
