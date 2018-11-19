package com.rtsoftware.order.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rtsoftware.order.R;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.tranfer.TranferOrderToMain;
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
    OrderAdapter orderAdapter;

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
        rcvTraMon = rootView.findViewById(R.id.rcvTraMon);
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
        orderAdapter = new OrderAdapter(context, lstOrder);
        rcvTraMon.setLayoutManager(new LinearLayoutManager(context));
        rcvTraMon.setAdapter(orderAdapter);
        int orientation = DividerItemDecoration.VERTICAL;
        DividerItemDecoration decoration = new DividerItemDecoration(context, orientation);
        rcvTraMon.addItemDecoration(decoration);
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
