package com.rtsoftware.order.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.rtsoftware.order.R;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.data.Table;
import com.rtsoftware.order.model.tranfer.TranferConfirmToMain;
import com.rtsoftware.order.model.tranfer.TranferMainToConfirm;
import com.rtsoftware.order.pesenter.POrder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmOrderFragment extends Fragment implements View.OnClickListener {
    private static final String PREFS_NAME = "Order";
    private static final String NEXT_ID_ORDER = "next_id_order";
    Context context;
    View rootView;
    ArrayList<Order.FoodInOrder> lstFoodInOrder;
    Table tableIsChoose;

    TextView tvTableName;
    RecyclerView rcvFoodOfOrder;
    LinearLayout btnAddFood;
    TextView tvTotalPrice;
    TextView btnDelete;
    TextView btnSuccess;
    ImageView btnBack;

    int totalPrice = 0;
    boolean isResult = false;


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
    public void getDataTranfer(TranferMainToConfirm tranferMainToConfirm) {
        if (tranferMainToConfirm != null) {
            Log.d("fgh", "getDataTranfer: ");
            Log.d("fgh", "getListFoodOrder: " + tranferMainToConfirm.getLstFoodInOrder().size());
            lstFoodInOrder = tranferMainToConfirm.getLstFoodInOrder();
            tableIsChoose = tranferMainToConfirm.getTableIsChoose();
            EventBus.getDefault().removeStickyEvent(tranferMainToConfirm);
//            if (!isResult) {
            tinhGia();
//            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_xac_nhan_order, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        tvTableName = rootView.findViewById(R.id.tvTableIdConfirm);
        rcvFoodOfOrder = rootView.findViewById(R.id.rcvFoodInOrder);
        btnAddFood = rootView.findViewById(R.id.btnAddFoodConfirm);
        tvTotalPrice = rootView.findViewById(R.id.tvtTotalPriceOrder);
        tvTotalPrice.setText("");
        btnDelete = rootView.findViewById(R.id.btnDeleteConfirm);
        btnSuccess = rootView.findViewById(R.id.btnSuccessConfirm);
        btnBack= rootView.findViewById(R.id.btnBackConfirmOrder);
        btnBack.setOnClickListener(this);
        btnSuccess.setOnClickListener(this);
        btnAddFood.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        POrder.IfGetListOrderResult ifGetListOrderResult = new POrder.IfGetListOrderResult() {
            @Override
            public void getListOrderSuccess(ArrayList<Order> orders) {
                if (!orders.isEmpty()) {
                    Order orderMax = orders.get(0);
                    for (Order order : orders) {
                        if (order.getTime() > orderMax.getTime()) {
                            orderMax = order;
                        }
                    }
                    String id = orderMax.getOrderId();
                    setLastId(id);
                } else
                    setLastId("");
            }

            @Override
            public void getListOrderFailr(String error) {
                setLastId("");
            }
        };
        POrder pOrder = new POrder();
        pOrder.getListOrder(ifGetListOrderResult);
        tinhGia();
    }

    private void tinhGia() {
        totalPrice=0;
        if (lstFoodInOrder != null) {
            tvTableName.setText("Bàn " + tableIsChoose.getTableId());
            for (Order.FoodInOrder food : lstFoodInOrder) {
                totalPrice += food.getQuantity() * food.getFoodFrice();
                Log.d("cba", totalPrice +"_"+ food.getQuantity()+"_"+ food.getFoodFrice());
            }
            showListFood();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBackConfirmOrder:
                EventBus.getDefault().post(new TranferConfirmToMain(false));
                break;
            case R.id.btnDeleteConfirm:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hủy order");
                builder.setMessage("Bạn có thực sự muốn hủy Order này?");
                builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post(new TranferConfirmToMain(true));
                    }
                });
                builder.show();
                break;
            case  R.id.btnSuccessConfirm:
                tableIsChoose.setStatus(1);
                String lastId = getLastId();
                if (lastId.equals("")) {
                    lastId = "OD_1";
                } else {
                    String[] c = lastId.split("_");
                    if (c.length == 2) {
                        int a = Integer.parseInt(c[1]);
                        a++;
                        lastId = c[0] + "_" + a;
                    }
                }
                setLastId(lastId);
                String lstFood = "";
                Map<String, Order.FoodInOrder> listOrder = new HashMap<>();
                for (int i = 0; i < lstFoodInOrder.size(); i++) {
                    listOrder.put(lstFoodInOrder.get(i).getFoodId(), lstFoodInOrder.get(i));
                    if (i == 0) {
                        lstFood += lstFoodInOrder.get(i).getFoodId();
                    } else
                        lstFood += "_>_" + lstFoodInOrder.get(i).getFoodId();
                }
                String uid = FirebaseAuth.getInstance().getUid();
                Order order = new Order(lastId, tableIsChoose.getTableId(), 1, System.currentTimeMillis(), lstFood, listOrder, totalPrice, uid);
                POrder.IfAddOrderResult ifAddOrderResult = new POrder.IfAddOrderResult() {
                    @Override
                    public void addOrderSuccess() {
                        EventBus.getDefault().post(new TranferConfirmToMain(true));
                    }

                    @Override
                    public void addOrderFailr(String error) {
                        Toast.makeText(context, "Đặt món gặp lỗi! Xin thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                };
                POrder pOrder = new POrder();
                pOrder.addOrUpdateOrder(ifAddOrderResult, order);

                FirebaseDatabase.getInstance().getReference("table").child(tableIsChoose.getTableId())
                        .setValue(tableIsChoose).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                break;
            case R.id.btnAddFoodConfirm:
                EventBus.getDefault().post(new TranferConfirmToMain(false));
                break;
        }
    }

    private String getLastId() {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(NEXT_ID_ORDER, "");
    }

    private void setLastId(String lastId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NEXT_ID_ORDER, lastId);
        editor.commit();
    }

    private void showListFood() {
        int orientation = DividerItemDecoration.VERTICAL;
        DividerItemDecoration decoration = new DividerItemDecoration(context, orientation);
        FoodAdapter foodAdapter = new FoodAdapter(context, lstFoodInOrder);
        rcvFoodOfOrder.setLayoutManager(new LinearLayoutManager(context));
        rcvFoodOfOrder.setAdapter(foodAdapter);
        rcvFoodOfOrder.addItemDecoration(decoration);
        tvTotalPrice.setText(String.valueOf(totalPrice));
    }

    private class FoodAdapter extends RecyclerView.Adapter<ConfirmOrderFragment.FoodHolder> {
        private List<Order.FoodInOrder> foodChooses;
        private LayoutInflater inflater;


        FoodAdapter(Context context, List<Order.FoodInOrder> foodChooses) {
            this.foodChooses = foodChooses;
            inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public ConfirmOrderFragment.FoodHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.item_food_confirm_order, viewGroup, false);
            return new ConfirmOrderFragment.FoodHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ConfirmOrderFragment.FoodHolder foodHolder, final int index) {
            foodHolder.tvFoodName.setText(foodChooses.get(index).getFoodName());
            foodHolder.tvQuantity.setText(foodChooses.get(index).getQuantity() + " " + foodChooses.get(index).getFoodUnit());
            foodHolder.tvFoodPrice.setText(String.valueOf(foodChooses.get(index).getFoodFrice()));
            int temp = foodChooses.get(index).getFoodFrice() * foodChooses.get(index).getQuantity();
            foodHolder.tvFoodTotalPrice.setText(String.valueOf(temp));
        }

        @Override
        public int getItemCount() {
            return foodChooses.size();
        }
    }

    private class FoodHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName;
        private TextView tvQuantity;
        private TextView tvFoodPrice;
        private TextView tvFoodTotalPrice;


        FoodHolder(@NonNull View itemView) {
            super(itemView);
            tvQuantity = itemView.findViewById(R.id.tvQuantityFoodConfirmOrder);
            tvFoodName = itemView.findViewById(R.id.tvFoodNameConfirmOrder);
            tvFoodPrice = itemView.findViewById(R.id.tvPriceFoodConfirmOrder);
            tvFoodTotalPrice = itemView.findViewById(R.id.tvTotalPriceFoodConfirmOrder);
        }
    }
}
