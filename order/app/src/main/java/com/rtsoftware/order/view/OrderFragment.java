package com.rtsoftware.order.view;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rtsoftware.order.R;
import com.rtsoftware.order.model.Untill;
import com.rtsoftware.order.model.data.Foods;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.data.Table;
import com.rtsoftware.order.model.tranfer.TranferOrderToMain;
import com.rtsoftware.order.model.tranfer.TranferTable;
import com.rtsoftware.order.pesenter.PFood;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderFragment extends Fragment {
    Context context;
    View rootView;
    static Table tableIsChoos;

    EditText edtInputSearch;
    ImageView btnClearnAll;
    Spinner spnFoodClass;
    RecyclerView rcvFoodShow;
    RecyclerView rcvFoodChoose;
    TextView btnBack;
    TextView btnnext;

    static ArrayList<Foods> lstFood;
    static ArrayList<Foods> lstFoodShow;
    static ArrayList<Order.FoodInOrder> lstFoodChoose;

    static
    ArrayList<Foods> lstFoodLastSearch;
    FoodChooseAdapter foodChooseAdapter;
    FoodShowAdapter foodShowAdapter;
    LinearLayoutManager lnmFoodChoose;


    private int lastTextLength = 0;
    Untill untill;

    String[] listFoodClass = new String[]{"-Chọn danh mục-", "Ăn vặt", "Điểm tâm", "Món chính", "Nước uống", "Tráng miệng"};

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
    public void getTableClick(TranferTable table) {
        if (table != null) {
            tableIsChoos = table.getTable();
            EventBus.getDefault().removeStickyEvent(table);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = container.getContext();
        Log.d("ghkf", "onCreateView: ");
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        untill = new Untill();
        lstFood = new ArrayList<>();
        lstFoodChoose = new ArrayList<>();
        lstFoodShow = new ArrayList<>();
        lstFoodLastSearch = new ArrayList<>();
        edtInputSearch = rootView.findViewById(R.id.edtInoutSearch);
        btnClearnAll = rootView.findViewById(R.id.btnClearnAll);
        btnClearnAll.setVisibility(View.GONE);
        spnFoodClass = rootView.findViewById(R.id.spnFoodClass);
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, listFoodClass);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFoodClass.setAdapter(adapter);
        spnFoodClass.setSelection(0);
        spnFoodClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                if (index != 0) {
                    lstFoodShow.clear();
                    for (Foods food : lstFood) {
                        if (food.getFoodClassify().equals(listFoodClass[index])) {
                            lstFoodShow.add(food);
                        }
                    }
                    if (foodShowAdapter != null) {
                        foodShowAdapter.notifyDataSetChanged();
                    }
                    edtInputSearch.setText("");
                } else {
                    lstFoodShow.clear();
                    lstFoodShow.addAll(lstFood);
                    if (foodShowAdapter != null) {
                        foodShowAdapter.notifyDataSetChanged();
                    }
                    edtInputSearch.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnClearnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnFoodClass.setSelection(0);
                edtInputSearch.setText("");
                lstFoodShow.clear();
                lstFoodShow.addAll(lstFood);
                btnClearnAll.setVisibility(View.GONE);
            }
        });

        edtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btnClearnAll.setVisibility(View.VISIBLE);
                } else {
                    btnClearnAll.setVisibility(View.GONE);
                }
                ArrayList<Foods> list = new ArrayList<>();
                boolean flagDelete = false;
                boolean isVietnames = false;

                if (charSequence.length() >= lastTextLength) {
                    if (lstFoodShow.isEmpty()) {
                        lstFoodShow.addAll(lstFood);
                    }
                } else {
                    flagDelete = true;
                    lstFoodShow.clear();
                    if (lstFoodLastSearch.isEmpty()) {
                        lstFoodShow.addAll(lstFood);
                    } else {
                        lstFoodShow.addAll(lstFoodLastSearch);
                    }
                }


                //check tieengs vietj
                if (!charSequence.toString().isEmpty()) {
                    String temp = untill.convertStringUTF8(charSequence.toString());
                    if (!temp.equals(charSequence.toString().toUpperCase())) {
                        isVietnames = true;
                    }
                }
                if (isVietnames) {
                    for (Foods food : lstFoodShow) {
                        if (food.getFoodName().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                            list.add(food);
                        }
                    }
                    for (Foods food : lstFoodShow) {
                        if (untill.convertStringUTF8(food.getFoodName()).toUpperCase()
                                .contains(untill.convertStringUTF8(charSequence.toString()).toUpperCase())) {
                            boolean a = true;
                            for (Foods food1 : list) {
                                if (food1.equals(food)) {
                                    a = false;
                                    break;
                                }
                            }
                            if (a) {
                                list.add(food);
                            }
                        }
                    }
                } else {
                    for (Foods food : lstFoodShow) {
                        if (untill.convertStringUTF8(food.getFoodName()).toUpperCase()
                                .contains(untill.convertStringUTF8(charSequence.toString()).toUpperCase())) {
                            list.add(food);
                        }
                    }
                }
                if (!flagDelete) {
                    lstFoodLastSearch.clear();
                    lstFoodLastSearch.addAll(lstFoodShow);
                } else {
                    lstFoodLastSearch.clear();
                }
                lstFoodShow.clear();
                lstFoodShow.addAll(list);
                rcvFoodShow.scrollToPosition(0);
                if (foodShowAdapter != null) {
                    foodShowAdapter.notifyDataSetChanged();
                }
                lastTextLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        rcvFoodShow = rootView.findViewById(R.id.rcvFoodShowOrder);
        rcvFoodChoose = rootView.findViewById(R.id.rcvFoodChooseOrder);
        btnBack = rootView.findViewById(R.id.btnBackOrder);
        btnnext = rootView.findViewById(R.id.btnNextOrder);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new TranferOrderToMain(lstFoodChoose, tableIsChoos, false));
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("fgh", "onClick: ");
                if (lstFoodChoose.isEmpty()) {
                    Toast.makeText(context, "Bạn hãy chọn món ăn để tiếp tục!", Toast.LENGTH_SHORT).show();
                } else
                    EventBus.getDefault().post(new TranferOrderToMain(lstFoodChoose, tableIsChoos, true));
            }
        });

        getListFood();
    }

    public void sortListFood(List<Foods> itemApps) {
        Collections.sort(itemApps, new Comparator<Foods>() {
            @Override
            public int compare(Foods o1, Foods o2) {
                return o1.getFoodName().compareTo(o2.getFoodName());
            }
        });
    }

    private void getListFood() {
        PFood.IfGetFoodResult ifGetFoodResult = new PFood.IfGetFoodResult() {
            @Override
            public void getListFoodSuccess(ArrayList<Foods> listFood) {
                sortListFood(listFood);
                lstFood = listFood;
                lstFoodShow.clear();
                lstFoodShow.addAll(listFood);
                lstFoodChoose.clear();
                showList();
            }

            @Override
            public void getListFoodFailt(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        };
        PFood pFood = new PFood();
        pFood.getListFood(ifGetFoodResult);
    }

    private void showList() {
        lnmFoodChoose = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rcvFoodChoose.setLayoutManager(lnmFoodChoose);
        rcvFoodShow.setLayoutManager(new LinearLayoutManager(context));
        foodShowAdapter = new FoodShowAdapter(context, lstFoodShow);
        foodChooseAdapter = new FoodChooseAdapter(context, lstFoodChoose);
        int orientation = DividerItemDecoration.VERTICAL;
        DividerItemDecoration decoration = new DividerItemDecoration(context, orientation);
        int holizontal = DividerItemDecoration.HORIZONTAL;
        DividerItemDecoration decorationChoose = new DividerItemDecoration(context, holizontal);
        rcvFoodShow.setAdapter(foodShowAdapter);
        rcvFoodChoose.setAdapter(foodChooseAdapter);
        rcvFoodShow.addItemDecoration(decoration);
        rcvFoodChoose.addItemDecoration(decorationChoose);
    }

    private class FoodShowAdapter extends RecyclerView.Adapter<FoodShowHolder> {
        private List<Foods> foods;
        private LayoutInflater inflater;


        FoodShowAdapter(Context context, List<Foods> foods) {
            this.foods = foods;
            inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public FoodShowHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.item_food_in_order, viewGroup, false);
            return new FoodShowHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final FoodShowHolder foodShowHolder, final int index) {
            if (index % 2 == 0) {
                foodShowHolder.bgFood.setBackgroundResource(R.color.colorFoodGray);
            } else {
                foodShowHolder.bgFood.setBackgroundResource(R.color.colorFoodDefaut);
            }
            String a = foodShowHolder.tvFoodCount.getText().toString();
            boolean fg = true;
            for (Order.FoodInOrder choose : lstFoodChoose) {
                if (choose.getFoodId().equals(foods.get(index).getFoodId())) {
                    foodShowHolder.tvFoodCount.setText(String.valueOf(choose.getQuantity()));
                    fg = false;
                    break;
                }
            }
            if (fg) {
                foodShowHolder.tvFoodCount.setText("0");
            }
            foodShowHolder.tvFoodName.setText(foods.get(index).getFoodName());
            foodShowHolder.tvFoodPrice.setText(String.valueOf(foods.get(index).getFoodPrice()));
            foodShowHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int c = Integer.parseInt(foodShowHolder.tvFoodCount.getText().toString());
                    c++;
                    foodShowHolder.tvFoodCount.setText(String.valueOf(c));
                    boolean a = true;
                    for (int i = 0; i < lstFoodChoose.size(); i++) {
                        if (lstFoodChoose.get(i).getFoodId().equals(foods.get(index).getFoodId())) {
                            a = false;
                            lstFoodChoose.get(i).setQuantity(c);
                            if (foodChooseAdapter != null & !lstFoodChoose.isEmpty()) {
                                foodChooseAdapter.notifyDataSetChanged();
                                lnmFoodChoose.scrollToPosition(i);
                            }
                            break;
                        }
                    }
                    if (a) {
                        lstFoodChoose.add(new Order.FoodInOrder(foods.get(index).getFoodId(), foods.get(index).getFoodName(),
                                foods.get(index).getFoodPrice(), foods.get(index).getFoodUnit(), c, 1));
                        if (foodChooseAdapter != null && !lstFoodChoose.isEmpty()) {
                            foodChooseAdapter.notifyDataSetChanged();
                            lnmFoodChoose.scrollToPosition(lstFoodChoose.size() - 1);
                        }
                    }
                }
            });
            foodShowHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int c = Integer.parseInt(foodShowHolder.tvFoodCount.getText().toString());
                    if (c > 0) {
                        c--;
                        foodShowHolder.tvFoodCount.setText(String.valueOf(c));
                        for (int i = 0; i < lstFoodChoose.size(); i++) {
                            if (lstFoodChoose.get(i).getFoodId().equals(foods.get(index).getFoodId())) {
                                if (c != 0) {
                                    lstFoodChoose.get(i).setQuantity(c);
                                    if (foodChooseAdapter != null && !lstFoodChoose.isEmpty()) {
                                        foodChooseAdapter.notifyDataSetChanged();
                                        lnmFoodChoose.scrollToPosition(i);
                                    }
                                } else {
                                    if (foodChooseAdapter != null && !lstFoodChoose.isEmpty()) {
                                        foodChooseAdapter.notifyDataSetChanged();
                                        lnmFoodChoose.scrollToPosition(i);
                                    }
                                    lstFoodChoose.remove(lstFoodChoose.get(i));
                                }
                                break;
                            }
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return foods.size();
        }
    }

    private class FoodShowHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName;
        private ImageView imgFood;
        private TextView tvFoodPrice;
        private TextView tvFoodCount;
        private ImageView btnMinus;
        private ImageView btnPlus;
        LinearLayout bgFood;


        FoodShowHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodNameOrder);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPriceOrder);
            tvFoodCount = itemView.findViewById(R.id.tvCountFoodOrder);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            bgFood = itemView.findViewById(R.id.bgFood);
//            btnPlus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int c = Integer.parseInt(tvFoodCount.getText().toString());
//                    c++;
//                    tvFoodCount.setText(String.valueOf(c));
//                    boolean a = true;
//                    for (int i = 0; i < lstFoodChoose.size(); i++) {
//                        if (lstFoodChoose.get(i).getFoods().getFoodId().equals(lstFoodShow.get(getAdapterPosition()).getFoodId())) {
//                            a = false;
//                            lstFoodChoose.get(i).setCount(c);
//                            if (foodChooseAdapter != null & !lstFoodChoose.isEmpty()) {
//                                foodChooseAdapter.notifyDataSetChanged();
//                                lnmFoodChoose.scrollToPosition(i);
//                            }
//                            break;
//                        }
//                    }
//                    if (a) {
//                        lstFoodChoose.add(new FoodChoose(lstFoodShow.get(getAdapterPosition()), c));
//                        if (foodChooseAdapter != null && !lstFoodChoose.isEmpty()) {
//                            foodChooseAdapter.notifyDataSetChanged();
//                            lnmFoodChoose.scrollToPosition(lstFoodChoose.size() - 1);
//                        }
//                    }
//                }
//            });
//
//            btnMinus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int c = Integer.parseInt(tvFoodCount.getText().toString());
//                    if (c > 0) {
//                        c--;
//                        tvFoodCount.setText(String.valueOf(c));
//                        for (int i = 0; i < lstFoodChoose.size(); i++) {
//                            if (lstFoodChoose.get(i).getFoods().getFoodId().equals(lstFoodShow.get(getAdapterPosition()).getFoodId())) {
//                                if (c != 0) {
//                                    lstFoodChoose.get(i).setCount(c);
//                                    if (foodChooseAdapter != null && !lstFoodChoose.isEmpty()) {
//                                        foodChooseAdapter.notifyDataSetChanged();
//                                        lnmFoodChoose.scrollToPosition(i);
//                                    }
//                                } else {
//                                    if (foodChooseAdapter != null && !lstFoodChoose.isEmpty()) {
//                                        foodChooseAdapter.notifyDataSetChanged();
//                                        lnmFoodChoose.scrollToPosition(i);
//                                    }
//                                    lstFoodChoose.remove(lstFoodChoose.get(i));
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//            });
        }
    }

    private class FoodChooseAdapter extends RecyclerView.Adapter<FoodChooseHolder> {
        private List<Order.FoodInOrder> foodChooses;
        private LayoutInflater inflater;


        FoodChooseAdapter(Context context, List<Order.FoodInOrder> foodChooses) {
            this.foodChooses = foodChooses;
            inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public FoodChooseHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.item_food_is_choose, viewGroup, false);
            return new FoodChooseHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final FoodChooseHolder foodChooseHolder, final int index) {
//            foodChooseHolder
            foodChooseHolder.tvFoodName.setText(foodChooses.get(index).getFoodName());
            foodChooseHolder.tvFoodCount.setText(String.valueOf(foodChooses.get(index).getQuantity()));
        }

        @Override
        public int getItemCount() {
            return foodChooses.size();
        }
    }

    private class FoodChooseHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName;
        private ImageView imgFood;
        private TextView tvFoodCount;


        FoodChooseHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFoodChoose);
            tvFoodName = itemView.findViewById(R.id.tvFoodChooseName);
            tvFoodCount = itemView.findViewById(R.id.tvFoodChooseCount);
        }
    }
}
