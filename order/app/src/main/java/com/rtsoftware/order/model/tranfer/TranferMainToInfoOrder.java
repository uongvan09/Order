package com.rtsoftware.order.model.tranfer;

import com.rtsoftware.order.model.data.Order;

public class TranferMainToInfoOrder {
    Order order;

    public TranferMainToInfoOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
