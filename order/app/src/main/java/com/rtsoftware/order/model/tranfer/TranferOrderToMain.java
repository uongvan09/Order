package com.rtsoftware.order.model.tranfer;

import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.data.Table;

import java.util.ArrayList;

public class TranferOrderToMain {
    ArrayList<Order.FoodInOrder> lstFoodInOrder;
    Table tableIsChoose;
    boolean typeTranfer;

    public TranferOrderToMain(ArrayList<Order.FoodInOrder> lstFoodInOrder, Table tableIsChoose, boolean typeTranfer) {
        this.lstFoodInOrder = lstFoodInOrder;
        this.tableIsChoose = tableIsChoose;
        this.typeTranfer = typeTranfer;
    }

    public ArrayList<Order.FoodInOrder> getLstFoodInOrder() {
        return lstFoodInOrder;
    }

    public void setLstFoodInOrder(ArrayList<Order.FoodInOrder> lstFoodInOrder) {
        this.lstFoodInOrder = lstFoodInOrder;
    }

    public Table getTableIsChoose() {
        return tableIsChoose;
    }

    public void setTableIsChoose(Table tableIsChoose) {
        this.tableIsChoose = tableIsChoose;
    }

    public boolean isTypeTranfer() {
        return typeTranfer;
    }

    public void setTypeTranfer(boolean typeTranfer) {
        this.typeTranfer = typeTranfer;
    }
}
