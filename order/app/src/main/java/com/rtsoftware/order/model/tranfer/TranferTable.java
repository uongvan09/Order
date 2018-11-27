package com.rtsoftware.order.model.tranfer;

import com.rtsoftware.order.model.data.Table;

public class TranferTable {
    Table table;

    public TranferTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
