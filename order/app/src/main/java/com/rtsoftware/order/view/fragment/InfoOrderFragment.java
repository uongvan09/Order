package com.rtsoftware.order.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtsoftware.order.R;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.tranfer.TranferInfoToMain;
import com.rtsoftware.order.model.tranfer.TranferMainToInfoOrder;
import com.rtsoftware.order.pesenter.POrder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoOrderFragment extends Fragment {
    Context context;
    View rootView;
    static ArrayList<Order.FoodInOrder> lstFood;
    FoodTranferAdapter foodTranferAdapter;


    TextView tvOrderId;
    TextView btnSuccess;
    ImageView btnBack;
    RecyclerView rcvListFood;
    POrder.IfUpdateFoodOrder ifUpdateFoodOrder;
    POrder pOrder;

    static Order order;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getInfoOrder(TranferMainToInfoOrder tranferMainToInfoOrder) {
        if (tranferMainToInfoOrder != null) {
            Log.d("ght", "getInfoOrder: ");
            lstFood = new ArrayList<>();
            order = tranferMainToInfoOrder.getOrder();
            Map<String, Order.FoodInOrder> food = order.getListOrder();
            String s = order.getLstOrderId();
            if (s.contains("_>_")) {
                String[] temp = s.split("_>_");
                if (temp.length > 1) {
                    for (String ss : temp) {
                        lstFood.add(food.get(ss));
                    }
                }
            } else {
                lstFood.add(food.get(s));
            }
            EventBus.getDefault().removeStickyEvent(tranferMainToInfoOrder);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_info_order_in_tra_mon, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        Log.d("ght", "init: ");
        tvOrderId = rootView.findViewById(R.id.tvOrderIfInfo);
        tvOrderId.setText(order.getOrderId());
        btnSuccess = rootView.findViewById(R.id.btnHoanThanhInfo);
        rcvListFood = rootView.findViewById(R.id.rcvFoodInfo);
        btnSuccess.setVisibility(View.GONE);
        btnBack = rootView.findViewById(R.id.btnBackInfoOrder);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new TranferInfoToMain("BYE"));
            }
        });
        if (lstFood == null) {
            lstFood = new ArrayList<>();
        }
        pOrder = new POrder();
        ifUpdateFoodOrder = new POrder.IfUpdateFoodOrder() {
            @Override
            public void isSuccess() {
                foodTranferAdapter.notifyDataSetChanged();
            }

            @Override
            public void isFailt() {

            }
        };
        showListFood();

        btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                POrder.IfAddOrderResult ifAddOrderResult = new POrder.IfAddOrderResult() {
                    @Override
                    public void addOrderSuccess() {
                        EventBus.getDefault().post(new TranferInfoToMain("HELLO"));
                    }

                    @Override
                    public void addOrderFailr(String error) {

                    }
                };
                order.setStatus(3);
                Map<String, Order.FoodInOrder> food = new HashMap<>();
                for (Order.FoodInOrder food1 : lstFood) {
                    food.put(food1.getFoodId(), food1);
                }
                order.setListOrder(food);
                pOrder.addOrUpdateOrder(ifAddOrderResult, order);
            }
        });
    }

    private void showListFood() {
        rcvListFood.setLayoutManager(new LinearLayoutManager(context));
        foodTranferAdapter = new FoodTranferAdapter(context, lstFood);
        rcvListFood.setAdapter(foodTranferAdapter);
        int orientation = DividerItemDecoration.VERTICAL;
        DividerItemDecoration decoration = new DividerItemDecoration(context, orientation);
        rcvListFood.addItemDecoration(decoration);
    }

    private class FoodTranferAdapter extends RecyclerView.Adapter<InfoOrderFragment.FoodTranferHolder> {
        private List<Order.FoodInOrder> foodOrder;
        private LayoutInflater inflater;


        FoodTranferAdapter(Context context, List<Order.FoodInOrder> foodOrder) {
            this.foodOrder = foodOrder;
            inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public InfoOrderFragment.FoodTranferHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.item_food_in_info_order, viewGroup, false);
            return new InfoOrderFragment.FoodTranferHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final InfoOrderFragment.FoodTranferHolder foodTranferHolder, final int index) {
            foodTranferHolder.tvFoodName.setText(foodOrder.get(index).getFoodName());
            foodTranferHolder.tvUnitFood.setText("__ " + foodOrder.get(index).getQuantity() + " " + foodOrder.get(index).getFoodUnit());
            if (foodOrder.get(index).getStatus() == 1) {
                foodTranferHolder.btnTranfer.setText(R.string.doi);
                foodTranferHolder.btnTranfer.setBackgroundResource(R.color.colorYellow);
                foodTranferHolder.louFillter.setVisibility(View.GONE);
            }
            if (foodOrder.get(index).getStatus() == 2) {
                foodTranferHolder.btnTranfer.setText(R.string.tra);
                foodTranferHolder.btnTranfer.setBackgroundResource(R.color.colorButtonChoose);
                foodTranferHolder.louFillter.setVisibility(View.GONE);
            }
            if (foodOrder.get(index).getStatus() == 3) {
                foodTranferHolder.btnTranfer.setVisibility(View.INVISIBLE);
                foodTranferHolder.louFillter.setVisibility(View.VISIBLE);
            }
            boolean a = true;
            for (Order.FoodInOrder food : lstFood) {
                if (food.getStatus() == 1) {
                    a = false;
                    break;
                }
            }
            if (a) {
                btnSuccess.setVisibility(View.VISIBLE);
            } else {
                btnSuccess.setVisibility(View.GONE);
            }
            foodTranferHolder.btnTranfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (foodOrder.get(index).getStatus() == 2) {
                        foodOrder.get(index).setStatus(3);
                        pOrder.updateFoodOrder(order.getOrderId(), foodOrder.get(index), ifUpdateFoodOrder);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return foodOrder.size();
        }

    }

    private class FoodTranferHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName;
        private TextView tvUnitFood;
        private TextView btnTranfer;
        private ImageView louFillter;


        FoodTranferHolder(@NonNull View itemView) {
            super(itemView);
            tvUnitFood = itemView.findViewById(R.id.tvUnitFood);
            tvFoodName = itemView.findViewById(R.id.tvNameFoodInInfo);
            btnTranfer = itemView.findViewById(R.id.btnTranferInfo);
            louFillter = itemView.findViewById(R.id.loufillter);
        }
    }
}
