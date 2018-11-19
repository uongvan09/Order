package com.rtsoftware.order.model.tranfer;

import com.rtsoftware.order.model.data.Order;

public class TranferTraMonToMain {
    Order order;

    public TranferTraMonToMain(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
