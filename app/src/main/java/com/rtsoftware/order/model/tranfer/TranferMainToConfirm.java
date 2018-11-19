package com.rtsoftware.order.model.tranfer;

import com.rtsoftware.order.model.data.Order;
import com.rtsoftware.order.model.data.Table;

import java.util.ArrayList;

public class TranferMainToConfirm {
    ArrayList<Order.FoodInOrder> lstFoodInOrder;
    Table tableIsChoose;

    public TranferMainToConfirm(ArrayList<Order.FoodInOrder> lstFoodInOrder, Table tableIsChoose) {
        this.lstFoodInOrder = lstFoodInOrder;
        this.tableIsChoose = tableIsChoose;
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
}
