package com.rtsoftware.order;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.rtsoftware.order.model.data.Foods;
import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.data.Table;
import com.rtsoftware.order.model.data.Users;
import com.rtsoftware.order.model.tranfer.TranferConfirmToMain;
import com.rtsoftware.order.model.tranfer.TranferInfoToMain;
import com.rtsoftware.order.model.tranfer.TranferMainToConfirm;
import com.rtsoftware.order.model.tranfer.TranferMainToInfoOrder;
import com.rtsoftware.order.model.tranfer.TranferOrderToMain;
import com.rtsoftware.order.model.tranfer.TranferTable;
import com.rtsoftware.order.model.tranfer.TranferTraMonToMain;
import com.rtsoftware.order.view.InfoOrderFragment;
import com.rtsoftware.order.view.OrderFragment;
import com.rtsoftware.order.view.SoDoFragment;
import com.rtsoftware.order.view.TraMonFragment;
import com.rtsoftware.order.view.ConfirmOrderFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG_SO_DO_FRAGMENT = "so_do_fragment";
    private static final String TAG_ORDER_FRAGMENT = "order_fragment";
    private static final String TAG_XN_ORDER_FRAGMENT = "xn_order_fragment";
    private static final String TAG_TRA_MON_FRAGMENT = "tra_mon_fragment";
    private static final String TAG_INFO_ORDER_FRAGMENT = "info_order_fragment";
    LinearLayout btnSoDo;
    LinearLayout btnOrder;
    LinearLayout btnTraMon;
    LinearLayout btnKhac;
    FrameLayout fragmentMain;


    SoDoFragment soDoFragment;
    OrderFragment orderFragment;
    ConfirmOrderFragment confirmOrderFragment;
    TraMonFragment traMonFragment;
    InfoOrderFragment infoOrderFragment;


    ArrayList<Order.FoodInOrder> lstFoodInOrder;
    boolean isChooseTable = false;

    boolean isChangeTable = false;
    boolean isUpdateOrder = false;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenSoDo(Table tableChoose) {
        if (tableChoose != null) {
            if (!isChooseTable) {
                showFragmentOrder(true);
                Log.d("ghd", "listenSoDo: ");
                EventBus.getDefault().postSticky(new TranferTable(tableChoose));
            } else {
                EventBus.getDefault().postSticky(new TranferMainToConfirm(lstFoodInOrder, tableChoose));
                showXacNhanOrder(true);
                isChooseTable = false;
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenTraMon(TranferTraMonToMain tranferTraMonToMain) {
        if (tranferTraMonToMain != null) {
            Log.d("ght", "listenTraMon: ");
            EventBus.getDefault().postSticky(new TranferMainToInfoOrder(tranferTraMonToMain.getOrder()));
            showFragmentInFoOrder(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenInfo(TranferInfoToMain tranferInfoToMain) {
        if (tranferInfoToMain != null) {
            showFragmentSoDo(true, true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getListFoodOrder(TranferOrderToMain tranferOrderToMain) {
        if (tranferOrderToMain != null) {
            if (!tranferOrderToMain.isTypeTranfer()) {
                showFragmentSoDo(false, true);
            } else {
                if (tranferOrderToMain.getTableIsChoose() != null) {
                    Log.d("fgh", "getListFoodOrder: " + tranferOrderToMain.getLstFoodInOrder().size());

                    EventBus.getDefault().postSticky(
                            new TranferMainToConfirm(tranferOrderToMain.getLstFoodInOrder(),
                                    tranferOrderToMain.getTableIsChoose()));
                    showXacNhanOrder(false);
                } else {
                    lstFoodInOrder = tranferOrderToMain.getLstFoodInOrder();
                    isChooseTable = true;
                    EventBus.getDefault().postSticky(true);
                    showFragmentSoDo(false, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn chưa chọn bàn ăn. \nHãy tiến hành chọn bàn");
                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenConfirm(TranferConfirmToMain tranferConfirmToMain) {
        if (tranferConfirmToMain.isSuccess()) {
            showFragmentSoDo(true, true);
        } else {
            showFragmentOrder(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        login();
        lstFoodInOrder = new ArrayList<>();
        btnSoDo = findViewById(R.id.btnSoDoMain);
        btnOrder = findViewById(R.id.btnOrderMain);
        btnTraMon = findViewById(R.id.btnTraMonMain);
        btnKhac = findViewById(R.id.btnKhacMain);
        fragmentMain = findViewById(R.id.fragmentMain);
        btnSoDo.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnTraMon.setOnClickListener(this);
        btnKhac.setOnClickListener(this);
        btnSoDo.setBackgroundResource(R.color.colorButtonChoose);
        btnOrder.setBackgroundResource(R.color.colorButtonDefault);
        btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
        btnKhac.setBackgroundResource(R.color.colorButtonDefault);
        showFragmentSoDo(true, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSoDoMain:
                btnSoDo.setBackgroundResource(R.color.colorButtonChoose);
                btnOrder.setBackgroundResource(R.color.colorButtonDefault);
                btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
                btnKhac.setBackgroundResource(R.color.colorButtonDefault);
                if (isChangeTable) {
                    showFragmentSoDo(true, true);
                } else
                    showFragmentSoDo(false, true);
                break;
            case R.id.btnOrderMain:
                btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
                btnOrder.setBackgroundResource(R.color.colorButtonChoose);
                btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
                btnKhac.setBackgroundResource(R.color.colorButtonDefault);
                showFragmentOrder(true);
                break;
            case R.id.btnTraMonMain:
                btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
                btnOrder.setBackgroundResource(R.color.colorButtonDefault);
                btnTraMon.setBackgroundResource(R.color.colorButtonChoose);
                btnKhac.setBackgroundResource(R.color.colorButtonDefault);
                showFragmentTraMon(true);
                break;
            case R.id.btnKhacMain:
                btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
                btnOrder.setBackgroundResource(R.color.colorButtonDefault);
                btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
                btnKhac.setBackgroundResource(R.color.colorButtonChoose);
                break;
            default:
                break;
        }
    }

    private void login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("thanhnd@order.com", "88888888");
    }

    private void createAccount() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword("dungbt@order.com", "88888888")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = FirebaseAuth.getInstance().getUid();
                            if (uid != null) {
                                Users users1 = new Users(uid, "dungbt", "88888888",
                                        "dungbt@order.com", "Bùi Thị Dung", "1997/12/01", Users.CASHIER);
                                FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(users1)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Them Thanh Cong", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(MainActivity.this, "Them That bai", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            } else {
                                Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(MainActivity.this, "create failt", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createTable() {
        ArrayList<Table> tables = new ArrayList<>();
        tables.add(new Table("T01", 2, 4));
        tables.add(new Table("T02", 2, 4));
        tables.add(new Table("T03", 2, 4));
        tables.add(new Table("T04", 2, 4));
        tables.add(new Table("T05", 2, 2));
        tables.add(new Table("T06", 2, 2));
        tables.add(new Table("T07", 2, 2));
        tables.add(new Table("T08", 2, 5));
        tables.add(new Table("T09", 2, 5));
        tables.add(new Table("T10", 2, 6));
        tables.add(new Table("T11", 2, 6));
        tables.add(new Table("T12", 2, 7));
        for (Table table : tables) {
            FirebaseDatabase.getInstance().getReference("table").child(table.getTableId()).setValue(table).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }

    private void createFood() {
        ArrayList<Foods> foods = new ArrayList<>();
        foods.add(new Foods("F001", "Phở", "",
                "Đặc sản Hà Nội", 25000, Foods.CLASSIFY_MON_CHINH, Foods.UNIT_BAT));
        foods.add(new Foods("F002", "Tào Phớ", "",
                "Đặc sản Sài Gòn", 5000, Foods.CLASSIFY_AN_VAT, Foods.UNIT_BAT));
        foods.add(new Foods("F003", "Nem Cuốn", "",
                "Đặc sản Hà Nội", 10000, Foods.CLASSIFY_MON_CHINH, Foods.UNIT_CAI));
        foods.add(new Foods("F004", "Bánh Mỳ", "",
                "Đặc sản Hà Nội", 15000, Foods.CLASSIFY_AN_VAT, Foods.UNIT_CAI));
        foods.add(new Foods("F005", "Ớt xào xả ớt", "",
                "Đặc sản Hà Nội", 50000, Foods.CLASSIFY_MON_CHINH, Foods.UNIT_XUAT));
        foods.add(new Foods("F006", "Cơm rang dưa bò", "",
                "Đặc sản Hà Nội", 25000, Foods.CLASSIFY_MON_CHINH, Foods.UNIT_XUAT));
        foods.add(new Foods("F007", "Bún Cá", "",
                "Đặc sản Hà Nội", 25000, Foods.CLASSIFY_MON_CHINH, Foods.UNIT_XUAT));
        foods.add(new Foods("F008", "Kẹo Mút", "",
                "Đặc sản Hà Nội", 2500, Foods.CLASSIFY_AN_VAT, Foods.UNIT_CAI));
        foods.add(new Foods("F009", "Cocacola", "",
                "Đặc sản Hà Nội", 10000, Foods.CLASSIFY_NUOC_UONG, Foods.UNIT_CHAI));
        foods.add(new Foods("F010", "Nuti food", "",
                "Đặc sản Hà Nội", 8000, Foods.CLASSIFY_NUOC_UONG, Foods.UNIT_CHAI));
        foods.add(new Foods("F011", "Bia Hà Nội", "",
                "Đặc sản Hà Nội", 25000, Foods.CLASSIFY_NUOC_UONG, Foods.UNIT_CHAI));
        foods.add(new Foods("F012", "Rượu Whisky", "",
                "Đặc sản Hà Nội", 250000, Foods.CLASSIFY_NUOC_UONG, Foods.UNIT_CHAI));
        foods.add(new Foods("F013", "Bánh kem", "",
                "Đặc sản Hà Nội", 50000, Foods.CLASSIFY_TRANG_MIENG, Foods.UNIT_CAI));
        foods.add(new Foods("F014", "Kem ly", "",
                "Đặc sản Hà Nội", 15000, Foods.CLASSIFY_TRANG_MIENG, Foods.UNIT_COC));
        foods.add(new Foods("F015", "Salat rau quả", "",
                "Đặc sản Hà Nội", 25000, Foods.CLASSIFY_DIEM_TAM, Foods.UNIT_XUAT));
        foods.add(new Foods("F016", "Súp gà", "",
                "Đặc sản Hà Nội", 16000, Foods.CLASSIFY_DIEM_TAM, Foods.UNIT_BAT));
        foods.add(new Foods("F017", "Xoài Cát", "",
                "Đặc sản Hà Nội", 65000, Foods.CLASSIFY_TRANG_MIENG, Foods.UNIT_XUAT));
        foods.add(new Foods("F018", "Xúc xích", "",
                "Đặc sản Hà Nội", 2500, Foods.CLASSIFY_DIEM_TAM, Foods.UNIT_CAI));
        foods.add(new Foods("F019", "Bánh da", "",
                "Đặc sản Hà Nội", 5000, Foods.CLASSIFY_DIEM_TAM, Foods.UNIT_CAI));

        for (Foods foods1 : foods) {
            FirebaseDatabase.getInstance().getReference("food").child(foods1.getFoodId()).
                    setValue(foods1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }

    private void showFragmentSoDo(boolean isNew, boolean changeColor) {
        if (changeColor) {
            btnSoDo.setBackgroundResource(R.color.colorButtonChoose);
            btnOrder.setBackgroundResource(R.color.colorButtonDefault);
            btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
            btnKhac.setBackgroundResource(R.color.colorButtonDefault);
        }
        if (isNew) {
            Fragment temp = getSupportFragmentManager().findFragmentByTag(TAG_SO_DO_FRAGMENT);
            if (temp != null) {
                getSupportFragmentManager().beginTransaction().detach(temp).commit();
            }
            soDoFragment = new SoDoFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, soDoFragment, TAG_SO_DO_FRAGMENT)
                    .show(soDoFragment).commit();
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        } else {
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(soDoFragment).commit();
            } else {
                soDoFragment = new SoDoFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, soDoFragment, TAG_SO_DO_FRAGMENT)
                        .show(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        }
    }

    private void showFragmentOrder(boolean isNew) {
        btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
        btnOrder.setBackgroundResource(R.color.colorButtonChoose);
        btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
        btnKhac.setBackgroundResource(R.color.colorButtonDefault);
        if (isNew) {
            Fragment temp = getSupportFragmentManager().findFragmentByTag(TAG_ORDER_FRAGMENT);
            if (temp != null) {
                getSupportFragmentManager().beginTransaction().detach(temp).commit();
            }
            orderFragment = new OrderFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, orderFragment, TAG_ORDER_FRAGMENT)
                    .show(orderFragment).commit();
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        } else {
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(orderFragment).commit();
            } else {
                orderFragment = new OrderFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, orderFragment, TAG_ORDER_FRAGMENT)
                        .show(orderFragment).commit();
            }
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        }
    }

    private void showXacNhanOrder(boolean isNew) {
        btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
        btnOrder.setBackgroundResource(R.color.colorButtonChoose);
        btnTraMon.setBackgroundResource(R.color.colorButtonDefault);
        btnKhac.setBackgroundResource(R.color.colorButtonDefault);
        if (isNew) {
            Fragment temp = getSupportFragmentManager().findFragmentByTag(TAG_XN_ORDER_FRAGMENT);
            if (temp != null) {
                getSupportFragmentManager().beginTransaction().detach(temp).commit();
            }
            confirmOrderFragment = new ConfirmOrderFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, confirmOrderFragment, TAG_XN_ORDER_FRAGMENT)
                    .show(confirmOrderFragment).commit();
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        } else {
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(confirmOrderFragment).commit();
            } else {
                confirmOrderFragment = new ConfirmOrderFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, confirmOrderFragment, TAG_XN_ORDER_FRAGMENT)
                        .show(confirmOrderFragment).commit();
            }
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        }
    }

    private void showFragmentTraMon(boolean isNew) {
        btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
        btnOrder.setBackgroundResource(R.color.colorButtonDefault);
        btnTraMon.setBackgroundResource(R.color.colorButtonChoose);
        btnKhac.setBackgroundResource(R.color.colorButtonDefault);
        if (isNew) {
            Fragment temp = getSupportFragmentManager().findFragmentByTag(TAG_TRA_MON_FRAGMENT);
            if (temp != null) {
                getSupportFragmentManager().beginTransaction().detach(temp).commit();
            }
            traMonFragment = new TraMonFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, traMonFragment, TAG_TRA_MON_FRAGMENT)
                    .show(traMonFragment).commit();
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        } else {
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(traMonFragment).commit();
            } else {
                traMonFragment = new TraMonFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, traMonFragment, TAG_TRA_MON_FRAGMENT)
                        .show(traMonFragment).commit();
            }
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(infoOrderFragment).commit();
            }
        }
    }

    private void showFragmentInFoOrder(boolean isNew) {
        btnSoDo.setBackgroundResource(R.color.colorButtonDefault);
        btnOrder.setBackgroundResource(R.color.colorButtonDefault);
        btnTraMon.setBackgroundResource(R.color.colorButtonChoose);
        btnKhac.setBackgroundResource(R.color.colorButtonDefault);
        if (isNew) {
            Fragment temp = getSupportFragmentManager().findFragmentByTag(TAG_INFO_ORDER_FRAGMENT);
            if (temp != null) {
                getSupportFragmentManager().beginTransaction().detach(temp).commit();
            }
            infoOrderFragment = new InfoOrderFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, infoOrderFragment, TAG_INFO_ORDER_FRAGMENT)
                    .show(infoOrderFragment).commit();
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
        } else {
            if (infoOrderFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(infoOrderFragment).commit();
            } else {
                infoOrderFragment = new InfoOrderFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, infoOrderFragment, TAG_INFO_ORDER_FRAGMENT)
                        .show(infoOrderFragment).commit();
            }
            if (soDoFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(soDoFragment).commit();
            }
            if (orderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(orderFragment).commit();
            }
            if (confirmOrderFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(confirmOrderFragment).commit();
            }
            if (traMonFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(traMonFragment).commit();
            }
        }
    }
}
