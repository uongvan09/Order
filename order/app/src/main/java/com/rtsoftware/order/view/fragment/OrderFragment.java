package com.rtsoftware.order.view.fragment;

import android.content.Context;
import android.graphics.Rect;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.rtsoftware.order.model.data.Food;
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
import java.util.Objects;

public class OrderFragment extends Fragment implements View.OnClickListener {
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
    LinearLayout layoutOrder;
    LinearLayout layoutHideKeyboard;

    static ArrayList<Food> lstFood;
    static ArrayList<Food> lstFoodShow;
    static ArrayList<Order.FoodInOrder> lstFoodChoose;

    static
    ArrayList<Food> lstFoodLastSearch;
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
        layoutOrder = rootView.findViewById(R.id.lnlOrder);
        layoutHideKeyboard= rootView.findViewById(R.id.lnlHideKeyboard);
        rcvFoodShow = rootView.findViewById(R.id.rcvFoodShowOrder);
        rcvFoodChoose = rootView.findViewById(R.id.rcvFoodChooseOrder);
        btnBack = rootView.findViewById(R.id.btnBackOrder);
        btnnext = rootView.findViewById(R.id.btnNextOrder);
        spnFoodClass = rootView.findViewById(R.id.spnFoodClass);

        btnClearnAll.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, listFoodClass);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFoodClass.setAdapter(adapter);
        spnFoodClass.setSelection(0);
        spnFoodClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                if (index != 0) {
                    lstFoodShow.clear();
                    for (Food food : lstFood) {
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
        edtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btnClearnAll.setVisibility(View.VISIBLE);
                } else {
                    btnClearnAll.setVisibility(View.GONE);
                }
                ArrayList<Food> list = new ArrayList<>();
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
                    for (Food food : lstFoodShow) {
                        if (food.getFoodName().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                            list.add(food);
                        }
                    }
                    for (Food food : lstFoodShow) {
                        if (untill.convertStringUTF8(food.getFoodName()).toUpperCase()
                                .contains(untill.convertStringUTF8(charSequence.toString()).toUpperCase())) {
                            boolean a = true;
                            for (Food food1 : list) {
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
                    for (Food food : lstFoodShow) {
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
        btnClearnAll.setOnClickListener(this);
        layoutOrder.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        rcvFoodShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                com.rtsoftware.order.view.Untill.hideKeyboard(Objects.requireNonNull(getActivity()));
                layoutHideKeyboard.setVisibility(View.VISIBLE);
                return false;
            }
        });
        edtInputSearch.setOnClickListener(this);

        layoutOrder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                layoutOrder.getWindowVisibleDisplayFrame(r);
                int screenHeight = layoutOrder.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    layoutHideKeyboard.setVisibility(View.GONE);
                }
                else {
                    // keyboard is closed
                    layoutHideKeyboard.setVisibility(View.VISIBLE);

                }
            }
        });
        getListFood();
        rcvFoodChoose.setVisibility(View.GONE);
    }

    public void sortListFood(List<Food> itemApps) {
        Collections.sort(itemApps, new Comparator<Food>() {
            @Override
            public int compare(Food o1, Food o2) {
                return o1.getFoodName().compareTo(o2.getFoodName());
            }
        });
    }

    private void getListFood() {
        PFood.IfGetFoodResult ifGetFoodResult = new PFood.IfGetFoodResult() {
            @Override
            public void getListFoodSuccess(ArrayList<Food> listFood) {
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
        if (lstFoodChoose.isEmpty()){
            rcvFoodChoose.setVisibility(View.GONE);
        }else {
            rcvFoodChoose.setVisibility(View.VISIBLE);
        }
        DividerItemDecoration decorationChoose = new DividerItemDecoration(context, holizontal);
        rcvFoodShow.setAdapter(foodShowAdapter);
        rcvFoodChoose.setAdapter(foodChooseAdapter);
        rcvFoodShow.addItemDecoration(decoration);
        rcvFoodChoose.addItemDecoration(decorationChoose);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClearnAll:
                spnFoodClass.setSelection(0);
                edtInputSearch.setText("");
                lstFoodShow.clear();
                lstFoodShow.addAll(lstFood);
                btnClearnAll.setVisibility(View.GONE);
                layoutHideKeyboard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNextOrder:
                if (lstFoodChoose.isEmpty()) {
                    Toast.makeText(context, "Bạn hãy chọn món ăn để tiếp tục!", Toast.LENGTH_SHORT).show();
                } else
                    EventBus.getDefault().post(new TranferOrderToMain(lstFoodChoose, tableIsChoos, true));
                break;
            case R.id.btnBackOrder:
                EventBus.getDefault().post(new TranferOrderToMain(lstFoodChoose, tableIsChoos, false));
                break;
            case R.id.edtInoutSearch:
                layoutHideKeyboard.setVisibility(View.GONE);
                break;
            default:
                com.rtsoftware.order.view.Untill.hideKeyboard(Objects.requireNonNull(getActivity()));
                layoutHideKeyboard.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class FoodShowAdapter extends RecyclerView.Adapter<FoodShowHolder> {
        private List<Food> foods;
        private LayoutInflater inflater;


        FoodShowAdapter(Context context, List<Food> foods) {
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
                            if (lstFoodChoose.isEmpty()){
                                rcvFoodChoose.setVisibility(View.GONE);
                            }else {
                                rcvFoodChoose.setVisibility(View.VISIBLE);
                            }
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
                        if (lstFoodChoose.isEmpty()){
                            rcvFoodChoose.setVisibility(View.GONE);
                        }else {
                            rcvFoodChoose.setVisibility(View.VISIBLE);
                        }
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
                                    if (lstFoodChoose.isEmpty()){
                                        rcvFoodChoose.setVisibility(View.GONE);
                                    }else {
                                        rcvFoodChoose.setVisibility(View.VISIBLE);
                                    }
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
                                    if (lstFoodChoose.isEmpty()){
                                        rcvFoodChoose.setVisibility(View.GONE);
                                    }else {
                                        rcvFoodChoose.setVisibility(View.VISIBLE);
                                    }
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
